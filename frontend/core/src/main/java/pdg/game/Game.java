package pdg.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.Entity.Team;
import pdg.game.Screen.ArenaScreen;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Game extends com.badlogic.gdx.Game {
  private SpriteBatch batch;

  private Texture image;

  @Override
  public void create() {
    batch = new SpriteBatch();

    // set mainScreen

    // test avec le screen de jeu

      ArrayList<BlockDTO> blocks = new ArrayList<>();
      BlockDTO block = new BlockDTO("sprites/onlyVersion.png", 50, 100, true, 0,0 );
      blocks.add(block);
      blocks.add(block);
      blocks.add(block);
      blocks.add(block);
      blocks.add(block);

      ArrayList<RobotDTO> robots = new ArrayList<>();
      RobotDTO robot1 = new RobotDTO("sprites/base.png", 50, 100, 120);
      RobotDTO robot2 = new RobotDTO("sprites/green.png", 50, 100, 120);
      RobotDTO robot3 = new RobotDTO("sprites/black.png", 50, 100, 120);
      robots.add(robot1);
      robots.add(robot2);
      robots.add(robot3);

      KingDTO ennemyKing = new KingDTO("sprites/timothee.png", 0,0,100, 100);
      KingDTO onwKing = new KingDTO("sprites/timothee.png", 0,0,100, 100);

      TeamDTO ennemyTeam = new TeamDTO(1, blocks, robots, ennemyKing);
      TeamDTO ownTeam = new TeamDTO(2, blocks, robots, onwKing);
      setScreen(new ArenaScreen(this, ownTeam, ennemyTeam));

  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    batch.dispose();
  }
}
