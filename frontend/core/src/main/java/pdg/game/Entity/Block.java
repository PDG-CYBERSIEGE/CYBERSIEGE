package pdg.game.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import pdg.game.blocks.BlockSprite;
import pdg.game.blocks.HeavyBlockSprite;
import pdg.game.blocks.LightBlockSprite;
import pdg.game.blocks.MediumBlockSprite;

import static com.badlogic.gdx.math.MathUtils.*;
import static pdg.game.utils.StaticValues.*;

public class Block extends Entity {

  BlockSprite blockSprite;
  private boolean physicsEnabled = false; // false = phase construction (kinematic, immobile sauf drag)
  private final String type;
  private final Vector2 spawnPosition;
  private static final float SPAWN_THRESHOLD = 0.3f; // marge en mètres pour considérer "toujours au spawn"

  private Vector2 savedPosition;
  private float savedAngle;


  public Block(Texture sprite, Integer health, Body body, int mass, float height, float width, String type, int length){
        super(health,sprite, body, mass, height, width);
        this.type = type;

    switch (type){
      case "HEAVY" -> {
        blockSprite =
          new HeavyBlockSprite(length);
        spawnPosition = HEAVYSPAWN;
      }

      case "MEDIUM" -> {
        blockSprite =
          new MediumBlockSprite(length);
        spawnPosition = MEDIUMSPAWN;
      }
      case "LIGHT" -> {
            blockSprite =
              new LightBlockSprite(length);
            spawnPosition = LIGHTSPAWN;
          }
          case null, default -> {
            Gdx.app.error("Block", "Type de bloc inconnu ou null : '" + type + "'");
            blockSprite = null;
            spawnPosition = null;
          }
        }

      if (blockSprite == null) return; // évite le NPE si le type ne matche rien

      blockSprite.resize(1);
      Vector2 worldPos = body.getPosition(); // centre du body, en mètres

      // Table.setPosition attend le coin bas-gauche, pas le centre
      blockSprite.setPosition(worldPos.x - blockSprite.getWidth() / 2f, worldPos.y - blockSprite.getHeight() / 2f);

    setGravityEnabled(false); // phase construction par défaut : pas de gravité, pas de collision-push
    setupDragging();
    }

    public void updateSprite(){
      if (blockSprite == null) return;

      Vector2 worldPos = body.getPosition(); // centre du body, en mètres

      // Table.setPosition attend le coin bas-gauche, pas le centre
      blockSprite.setPosition(worldPos.x - blockSprite.getWidth() / 2f, worldPos.y - blockSprite.getHeight() / 2f);
      // body.getAngle() est en radians, setRotation() de Scene2D attend des degrés
      blockSprite.setRotation(radiansToDegrees * body.getAngle());
    }

    @Override
    public void draw(SpriteBatch batch) {
    }

  public BlockSprite getBlockSprite() {
    return blockSprite;
  }

  public void destroy(World world) {
    if (blockSprite != null) {
      blockSprite.remove();
    }
    world.destroyBody(body);
  }

  /** Active/désactive la gravité et la physique dynamique de ce bloc. */
  public void setGravityEnabled(boolean enabled) {
    physicsEnabled = enabled;
    if (enabled && !isAtSpawn()) {
      body.setType(BodyDef.BodyType.DynamicBody);
      body.setGravityScale(1f);
    } else {
      body.setLinearVelocity(0, 0);
      body.setType(BodyDef.BodyType.KinematicBody);
    }
  }

  private void setupDragging() {

    // --- Clic droit : déplacement ---
    DragListener moveListener = new DragListener() {
      @Override
      public void drag(InputEvent event, float x, float y, int pointer) {
        if (physicsEnabled) return;
        Vector2 newPos = body.getPosition().cpy().add(getDeltaX(), getDeltaY());
        body.setTransform(newPos, body.getAngle());
      }
    };
    moveListener.setButton(Input.Buttons.RIGHT);
    blockSprite.addListener(moveListener);

    // --- Clic gauche : rotation ---
    DragListener rotateListener = new DragListener() {
      @Override
      public void drag(InputEvent event, float x, float y, int pointer) {
        if (physicsEnabled) return;

        // x,y sont en coordonnées locales à l'acteur -> on les convertit en coordonnées itemStage
        // (mêmes unités que body.getPosition())
        Vector2 stageCoords = blockSprite.localToStageCoordinates(new Vector2(x, y));
        Vector2 center = body.getPosition();

        float angle = MathUtils.atan2(stageCoords.y - center.y, stageCoords.x - center.x);
        body.setTransform(center, angle);
      }
    };
    rotateListener.setButton(Input.Buttons.LEFT);
    blockSprite.addListener(rotateListener);
  }

  public String getType() {
    return type;
  }

  /** Vrai si ce bloc est toujours (à peu près) à sa position de spawn d'origine. */
  public boolean isAtSpawn() {
    return body.getPosition().dst(spawnPosition) < SPAWN_THRESHOLD;
  }

  /** Capture la position/angle actuels, à appeler juste avant d'activer la gravité. */
  public void savePosition() {
    savedPosition = body.getPosition().cpy();
    savedAngle = body.getAngle();
  }

  /** Replace le bloc à sa dernière position sauvegardée. Repasse en kinematic (gravité désactivée). */
  public void restorePosition() {
    if (savedPosition == null) return; // rien à restaurer si jamais sauvegardé

    setGravityEnabled(false); // repasse en kinematic avant de forcer la position
    body.setTransform(savedPosition, savedAngle);
    updateSprite(); // évite d'attendre la prochaine frame pour voir le résultat
  }
}
