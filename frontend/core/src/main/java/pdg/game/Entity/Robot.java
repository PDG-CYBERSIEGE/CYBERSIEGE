package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Robot extends Entity {

  private int id;
  private int cooldown;
  private int currentCooldown;

  // Offset entre l'orientation "au repos" du sprite (0° = pointe vers la droite,
  // convention standard) et l'angle réel du dessin sur la texture.
  // Ex: si le sprite pointe vers le haut au repos, mettre 90f.
  protected float spriteBaseAngleOffset = 90f;

  // En dessous de cette vitesse (m/s), on garde le dernier angle connu au lieu
  // de recalculer depuis la vélocité, pour éviter que le sprite tremble/tourne
  // au hasard quand le robot est quasi immobile (vélocité proche de zéro,
  // direction non significative).
  private static final float MIN_SPEED_FOR_ROTATION = 0.1f;

  private float lastFacingAngleDeg = 0f;

  public Robot(
      Texture sprite,
      int id,
      Integer health,
      Body body,
      int mass,
      int cooldown,
      float height,
      float width) {
    super(health, sprite, body, mass, height, width);
    this.cooldown = cooldown;
    this.currentCooldown = 0;
  }

  public void reduceCooldown() {
    if (currentCooldown > 0) currentCooldown--;
  }

  public void startCooldown() {
    currentCooldown = cooldown;
  }

  public boolean isReady() {
    return currentCooldown == 0;
  }

  /** A appeler ENTRE batch.begin() et batch.end(). */
  @Override
  public void draw(SpriteBatch batch) {
    Vector2 worldPos = body.getPosition(); // déjà en mètres, unités itemStage

    Vector2 velocity = body.getLinearVelocity();
    if (velocity.len() > MIN_SPEED_FOR_ROTATION) {
      lastFacingAngleDeg = velocity.angleDeg();
    }

    batch.draw(
        sprite,
        worldPos.x - width / 2f,
        worldPos.y - height / 2f,
        width / 2f,
        height / 2f,
        width,
        height,
        1f,
        1f,
        lastFacingAngleDeg - spriteBaseAngleOffset,
        0,
        0,
        sprite.getWidth(),
        sprite.getHeight(),
        false,
        false);
  }

  public int getId(){
    return id;
  }
}
