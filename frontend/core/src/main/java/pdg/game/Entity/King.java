package pdg.game.Entity;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class King extends Entity {

  private final Stage itemStage; // pour unproject() les touch input, comme Canon
  private boolean physicsEnabled = false;
  private boolean isDragging = false;

  private static final float DRAG_RADIUS = 1f; // rayon de détection en mètres, à ajuster selon la taille du king

  private InputProcessor cachedInputProcessor;

  private Vector2 savedPosition;

    public King(Texture sprite, Integer health, Body body, int mass, float height, float width, Stage itemStage){
        super(health, sprite, body, mass, height, width);
        this.itemStage = itemStage;

      setGravityEnabled(false);
    }

  /** Active/désactive la gravité et la physique dynamique du king. */
  public void setGravityEnabled(boolean enabled) {
    physicsEnabled = enabled;
    if (enabled) {
      body.setType(BodyDef.BodyType.DynamicBody);
    } else {
      body.setLinearVelocity(0, 0);
      body.setType(BodyDef.BodyType.KinematicBody);
    }
  }

  public InputProcessor getInputProcessor() {
    if (cachedInputProcessor != null) return cachedInputProcessor;

    cachedInputProcessor = new InputAdapter() {
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
        return true;
      }
    };

    return cachedInputProcessor;
  }

  /** Capture la position/angle actuels, à appeler juste avant d'activer la gravité. */
  public void savePosition() {
    savedPosition = body.getPosition().cpy();
  }

  /** Replace le bloc à sa dernière position sauvegardée. Repasse en kinematic (gravité désactivée). */
  public void restorePosition() {
    if (savedPosition == null) return; // rien à restaurer si jamais sauvegardé

    setGravityEnabled(false); // repasse en kinematic avant de forcer la position
    body.setTransform(savedPosition, 0);
  }
}
