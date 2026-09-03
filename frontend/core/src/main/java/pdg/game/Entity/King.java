package pdg.game.Entity;

import static pdg.game.utils.StaticValues.*;
import static pdg.game.utils.StaticValues.RECTHEIGHT;
import static pdg.game.utils.StaticValues.RECTWIDTH;
import static pdg.game.utils.StaticValues.RECTY;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class King extends Entity {

  private final Stage itemStage; // pour unproject() les touch input, comme Canon
  private boolean physicsEnabled = false;
  private boolean isDragging = false;
  private Rectangle buildZone; // zone autorisée pour ce bloc pendant la phase construction

  private final Vector2 spawnPosition;

  private static final float DRAG_RADIUS =
      1f; // rayon de détection en mètres, à ajuster selon la taille du king

  private InputProcessor cachedInputProcessor;

  private Vector2 savedPosition;

  public King(
      Texture sprite,
      Integer health,
      Body body,
      int mass,
      float height,
      float width,
      Stage itemStage) {
    super(health, sprite, body, mass, height, width);
    this.itemStage = itemStage;
    this.spawnPosition = KINGSPAWN;

    // rectangle for construction
    this.buildZone = new Rectangle(RECTX, RECTY, RECTWIDTH, RECTHEIGHT);

    setGravityEnabled(false);
  }

  /** Active/désactive la gravité et la physique dynamique du king. */
  public void setGravityEnabled(boolean enabled) {
    physicsEnabled = enabled;
    if (enabled && !isAtSpawn()) {
      body.setType(BodyDef.BodyType.DynamicBody);
    } else {
      body.setLinearVelocity(0, 0);
      body.setType(BodyDef.BodyType.KinematicBody);
    }
  }

  public InputProcessor getInputProcessor() {
    if (cachedInputProcessor != null) return cachedInputProcessor;

    cachedInputProcessor =
        new InputAdapter() {
          @Override
          public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (physicsEnabled) return false; // plus draggable une fois la gravité activée

            Vector2 worldPos = itemStage.getViewport().unproject(new Vector2(screenX, screenY));
            if (worldPos.dst(body.getPosition()) > DRAG_RADIUS) return false;

            isDragging = true;
            return true;
          }

          @Override
          public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (!isDragging) return false;

            Vector2 worldPos = itemStage.getViewport().unproject(new Vector2(screenX, screenY));
            body.setTransform(worldPos, 0);
            return true;
          }

          @Override
          public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            isDragging = false;

            if (buildZone != null && !buildZone.contains(body.getPosition())) {
              initialState();
            }
            return true;
          }
        };

    return cachedInputProcessor;
  }

  /** Capture la position/angle actuels, à appeler juste avant d'activer la gravité. */
  public void savePosition() {
    savedPosition = body.getPosition().cpy();
  }

  /**
   * Replace le bloc à sa dernière position sauvegardée. Repasse en kinematic (gravité désactivée).
   */
  public void restorePosition() {
    if (savedPosition == null) return; // rien à restaurer si jamais sauvegardé

    setGravityEnabled(false); // repasse en kinematic avant de forcer la position
    body.setTransform(savedPosition, 0);
  }

  public boolean isAtSpawn() {
    return body.getPosition().dst(spawnPosition) < SPAWN_THRESHOLD;
  }

  public void initialState() {
    body.setTransform(spawnPosition, 0);
  }
}
