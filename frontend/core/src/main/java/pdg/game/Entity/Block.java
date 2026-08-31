package pdg.game.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import pdg.game.blocks.BlockSprite;
import pdg.game.blocks.HeavyBlockSprite;
import pdg.game.blocks.LightBlockSprite;
import pdg.game.blocks.MediumBlockSprite;

import static com.badlogic.gdx.math.MathUtils.*;
import static pdg.game.utils.StaticValues.*;

public class Block extends Entity {

    BlockSprite block;

    public Block(Texture sprite, Integer health, Body body, int mass, float height, float width, String type, int length){
        super(health,sprite, body, mass, height, width);

        switch (type){
          case "HEAVY" -> block = new HeavyBlockSprite(length);
          case "MEDIUM" -> block = new MediumBlockSprite(length);
          case "LIGHT" -> block = new LightBlockSprite(length);
          case null, default -> {
            Gdx.app.error("Block", "Type de bloc inconnu ou null : '" + type + "'");
            block = null;
          }
        }

      if (block == null) return; // évite le NPE si le type ne matche rien

      block.resize(1);
      Vector2 worldPos = body.getPosition(); // centre du body, en mètres

      // Table.setPosition attend le coin bas-gauche, pas le centre
      block.setPosition(worldPos.x - block.getWidth() / 2f, worldPos.y - block.getHeight() / 2f);

    }

    public void updateSprite(){
      if (block == null) return;

      Vector2 worldPos = body.getPosition(); // centre du body, en mètres

      // Table.setPosition attend le coin bas-gauche, pas le centre
      block.setPosition(worldPos.x - block.getWidth() / 2f, worldPos.y - block.getHeight() / 2f);
      // body.getAngle() est en radians, setRotation() de Scene2D attend des degrés
      block.setRotation(radiansToDegrees * body.getAngle());
    }

    @Override
    public void draw(SpriteBatch batch) {
    }

  public BlockSprite getBlockSprite() {
    return block;
  }

  public void destroy(World world) {
    if (block != null) {
      block.remove(); // retire l'acteur de son Stage (itemStage)
    }
    world.destroyBody(body);
  }
}
