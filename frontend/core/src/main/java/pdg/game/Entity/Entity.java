package pdg.game.Entity;

import static pdg.game.utils.StaticValues.DAMAGETRESHOLD;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity {

  private int maxHealth;
  protected int health;
  protected Texture sprite;
  protected Body body;
  protected int mass;

  // Taille d'affichage du sprite, en mètres (world units).
  protected float width;
  protected float height;

  public Entity(int health, Texture sprite, Body body, int mass, float height, float width) {
    this.maxHealth = health;
    this.health = health;
    this.sprite = sprite;
    this.body = body;
    this.mass = mass;
    this.height = height;
    this.width = width;
  }

  /** A appeler ENTRE batch.begin() et batch.end(). */
  public void draw(SpriteBatch batch) {
    Vector2 worldPos = body.getPosition(); // déjà en mètres, unités itemStage

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
        0,
        0,
        0,
        sprite.getWidth(),
        sprite.getHeight(),
        false,
        false);
  }

  public void takeDamage(int health) {
    this.health -= health;
  }

  public boolean isDead() {
    return health <= 0;
  }

  public int damageoutput() {
    int damage = Math.round(this.body.getLinearVelocity().len() * mass);
    return damage > DAMAGETRESHOLD ? damage : 0;
  }

  public Texture getSprite() {
    return sprite;
  }

  public Body getBody() {
    return body;
  }

  public void restoreHealth() {
    health = maxHealth;
  }
}
