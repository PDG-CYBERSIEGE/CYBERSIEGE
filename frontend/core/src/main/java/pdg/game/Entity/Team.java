package pdg.game.Entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import pdg.game.Canon;

import java.util.ArrayList;

public class Team {

    public String user;
    public ArrayList<Block> tower = new ArrayList<>();
    public King king;
    public ArrayList<Robot> robots = new ArrayList<>();
    public Canon canon;

    /** A appeler ENTRE batch.begin() et batch.end() dans FightScreen.render(). */
    public void draw(SpriteBatch batch) {
      for (Block block : tower) {
        block.draw(batch);
      }

      if (king != null) {
        king.draw(batch);
      }

      for (Robot robot : robots) {
        robot.draw(batch);
      }

      if (canon != null) {
        canon.draw(batch);
      }
    }

    public void updateBlocks(World world){
      removeDeadEntities(world);
      for (Block b : tower){
        b.updateSprite();
      }
    }

  public void removeDeadEntities(World world) {
    tower.removeIf(block -> {
      if (block.isDead()) {
        block.destroy(world);
        return true;
      }
      return false;
    });
  }
}
