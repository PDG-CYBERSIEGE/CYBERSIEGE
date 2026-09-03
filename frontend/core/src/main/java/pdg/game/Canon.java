package pdg.game;

import com.badlogic.gdx.Game;import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import pdg.game.Entity.Robot;
import pdg.game.network.websocket.GameMessages;
import pdg.game.ui.RobotChoiceButton;

/**
 * Gère la visée (drag), la prévisualisation de trajectoire et le lancement des robots d'une équipe.
 *
 * <p>Différences majeures avec l'ancienne classe Catapult (tuto Angry Birds) : - Pas de
 * SpriteBatch/camera/Box2DDebugRenderer propres : ArenaScreen en a déjà, partagés pour toute la
 * scène. Canon dessine dans le batch déjà ouvert par ArenaScreen (voir draw(batch)). - Pas de
 * world.step() ici : déjà fait une fois par frame dans ArenaScreen.update(), sinon la simulation
 * avancerait trop vite dès qu'il y a 2 canons (1 par joueur) sur le même World. - Travaille
 * uniquement en mètres (comme le reste d'ArenaScreen), plus de mélange pixels/mètres. - La
 * trajectoire prévisualisée réutilise la vraie gravité du World (world.getGravity()) au lieu d'une
 * constante dupliquée : elle ne peut donc plus se désynchroniser du tir réel. - Plus de système
 * d'index/indexC façon "file d'oiseaux" : la file de robots disponibles, c'est directement
 * team.robots (ArrayList<Robot>), parcourue dans l'ordre.
 *
 * <p>API supposée sur Robot (à ajouter si elle n'existe pas déjà) : boolean isReady() -> le
 * cooldown de ce robot est-il écoulé ? void startCooldown() -> relance le cooldown de ce robot
 * après un tir (reduceCooldown() existe déjà et est appelé depuis ArenaScreen.teamUpdate)
 */
public class Canon {

  private static final float MAX_DRAG_DISTANCE = 40f; // px écran
  private static final float POWER_MULTIPLIER = 4f;

  private static final int TRAJECTORY_POINT_COUNT = 8;
  private static final float TRAJECTORY_TIME_STEP = 0.1f;

  // Taille d'affichage du sprite du canon, en mètres (world units).
  private static final float CANON_WIDTH = 1.5f;
  private static final float CANON_HEIGHT = 1.5f;

  // Rayon de la zone cliquable autour du canon, en mètres.
  private static final float TOUCH_RADIUS = 1.5f;

  // Animation de tir : bande de 8 frames côte à côte dans un seul fichier.
  private static final int FIRE_FRAME_COUNT = 8;
  private static final float FIRE_FRAME_DURATION =
      0.05f; // 8 frames * 0.05s = 0.4s d'animation totale
  private static final float SPRITE_BASE_ANGLE_OFFSET =
      90f; // le sprite pointe vers le haut à 0° de rotation

  private final Main game;

  private final World world;
  private final Stage itemStage; // uniquement pour unproject() les touch input
  private final Vector2
      canonPosition; // en pixels, comme le reste des positions "brutes" d'ArenaScreen
  private final Texture trajectoryDotTexture;

  private final TextureRegion idleFrame; // première frame, affichée hors tir
  private final Animation<TextureRegion> fireAnimation;
  private float fireStateTime = 0f;
  private boolean isFiring = false;

  private final Vector2 dragStart = new Vector2();
  private boolean isDragging = false;
  private boolean isAiming = false;
  private float power = 0f;
  private float angle = 0f;

  private Robot loadedRobot;
  private RobotChoiceButton btn;

  private InputProcessor cachedInputProcessor;

  public Canon(Main game, World world, Stage itemStage, Vector2 canonPosition, Texture fireSpriteSheet) {

    this.game = game;
    this.world = world;
    this.itemStage = itemStage;
    this.canonPosition = canonPosition;
    this.trajectoryDotTexture = createWhiteDotTexture(16);

    // Découpe la bande horizontale en FIRE_FRAME_COUNT frames égales.
    int frameWidth = fireSpriteSheet.getWidth() / FIRE_FRAME_COUNT;
    int frameHeight = fireSpriteSheet.getHeight();
    TextureRegion[][] tmp = TextureRegion.split(fireSpriteSheet, frameWidth, frameHeight);
    TextureRegion[] frames = tmp[0]; // une seule ligne

    this.idleFrame = frames[0];
    this.fireAnimation = new Animation<>(FIRE_FRAME_DURATION, frames);
    this.fireAnimation.setPlayMode(Animation.PlayMode.NORMAL); // joue une fois, pas de boucle
  }

  /** Positionne le prochain robot de la file sur le canon, prêt à être tiré. */
  public void loadNextRobot(RobotChoiceButton btn) {
    if (this.btn != null) {
      this.btn.unload();
    }

    loadedRobot = btn.robot;
    this.btn = btn;
    this.btn.load();
  }

  public boolean hasRobotLoaded() {
    return loadedRobot != null;
  }

  public InputProcessor getInputProcessor() {
    if (cachedInputProcessor != null) return cachedInputProcessor;

    cachedInputProcessor =
        new InputAdapter() {
          @Override
          public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (loadedRobot == null || !loadedRobot.isReady()) return false;

            // Convertit les pixels écran bruts en unités itemStage (mètres).
            Vector2 worldPos = itemStage.getViewport().unproject(new Vector2(screenX, screenY));

            if (worldPos.dst(canonPosition) > TOUCH_RADIUS) return false;

            dragStart.set(worldPos);
            isDragging = true;
            isAiming = true;
            return true;
          }

          @Override
          public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (!isDragging) return false;

            Vector2 dragEnd = itemStage.getViewport().unproject(new Vector2(screenX, screenY));
            float distance = dragStart.dst(dragEnd);
            if (distance > MAX_DRAG_DISTANCE) {
              dragEnd = dragStart.cpy().lerp(dragEnd, MAX_DRAG_DISTANCE / distance);
            }

            power = dragStart.dst(dragEnd) * POWER_MULTIPLIER;
            angle = dragEnd.sub(dragStart).angleDeg() - 180;
            return true;
          }

          @Override
          public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (!isDragging) return false;
            onTouchUp();
            return true;
          }
        };

    return cachedInputProcessor;
  }

  private void onTouchUp(){
    isDragging = false;
    isAiming = false;
    game.getMainMenuScreen().getGameWebSocket().send(GameMessages.fire(power, angle, loadedRobot.getId()));
    fire();
  }

  private void fire() {
    if (loadedRobot == null) return;

    loadedRobot.getBody().setTransform(canonPosition.x, canonPosition.y, 0);

    Vector2 launchVelocity = new Vector2(power, 0f).setAngleDeg(angle);

    loadedRobot.getBody().setLinearVelocity(launchVelocity);
    loadedRobot.startCooldown();

    // déclenche l'animation de tir
    isFiring = true;
    fireStateTime = 0f;

    if (btn != null){
      btn.unload();
      btn = null;
    }
    loadedRobot = null;
  }

  /** Fait avancer l'animation de tir. world.step() reste géré par ArenaScreen. */
  public void update(float delta) {
    if (isFiring) {
      fireStateTime += delta;
      if (fireAnimation.isAnimationFinished(fireStateTime)) {
        isFiring = false;
      }
    }
  }

  /** A appeler ENTRE batch.begin() et batch.end() dans ArenaScreen.render(). */
  public void draw(SpriteBatch batch) {
    TextureRegion currentFrame =
        isFiring ? fireAnimation.getKeyFrame(fireStateTime, false) : idleFrame;

    batch.draw(
        currentFrame,
        canonPosition.x - CANON_WIDTH / 2f,
        canonPosition.y - CANON_HEIGHT / 2f,
        CANON_WIDTH / 2f,
        CANON_HEIGHT / 2f,
        CANON_WIDTH,
        CANON_HEIGHT,
        1f,
        1f,
        isAiming ? angle - SPRITE_BASE_ANGLE_OFFSET : 0f);

    if (!isAiming) return;

    Vector2 v = new Vector2(power, 0f).setAngleDeg(angle);
    float gravity = world.getGravity().y;

    float t = 0f;
    for (int i = 0; i < TRAJECTORY_POINT_COUNT; i++) {
      float px = canonPosition.x + v.x * t;
      float py = canonPosition.y + v.y * t + 0.5f * gravity * t * t;
      if (py < 0) break;

      batch.draw(trajectoryDotTexture, px - 0.05f, py - 0.05f, 0.1f, 0.1f);
      t += TRAJECTORY_TIME_STEP;
    }
  }

  /**
   * trajectoryDotTexture est géré par l'Asset manager d'ArenaScreen (chargé/libéré une seule fois
   * là-bas), donc rien à disposer ici.
   */
  public void dispose() {}

  private Texture createWhiteDotTexture(int diameterPx) {
    Pixmap pixmap = new Pixmap(diameterPx, diameterPx, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fillCircle(diameterPx / 2, diameterPx / 2, diameterPx / 2);
    Texture texture = new Texture(pixmap);
    pixmap.dispose();
    texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    return texture;
  }

  public void sendRobot(Robot robot, float power, float angle) {
    this.angle = angle;
    this.power = power;
    loadedRobot = robot;
    fire();
  }
}
