package pdg.game;

import static pdg.game.utils.StaticValues.*;

import com.badlogic.gdx.Game;
import java.util.ArrayList;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.screens.FightScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
  @Override
  public void create() {

    // test avec le screen de jeu

    ArrayList<BlockDTO> blocks = new ArrayList<>();
    BlockDTO block = new BlockDTO(HEAVY, 50, 100, true, 30, 4, 3);
    blocks.add(block);
    blocks.add(new BlockDTO(MEDIUM, 50, 800, true, 26, 2, 3));
    blocks.add(new BlockDTO(LIGHT, 50, 800, true, 28, 5, 3));

    ArrayList<RobotDTO> robots = new ArrayList<>();
    RobotDTO robot1 = new RobotDTO("throwables/base.png", 50, 100, 2);
    RobotDTO robot2 = new RobotDTO("throwables/green.png", 50, 500, 10);
    RobotDTO robot3 = new RobotDTO("throwables/black.png", 50, 800, 50);
    robots.add(robot1);
    robots.add(robot2);
    robots.add(robot3);

    KingDTO ennemyKing = new KingDTO("kings/timothee.png", 26, 6, 10000, 100);
    KingDTO onwKing = new KingDTO("kings/geraud.png", 0, 0, 10000, 100);

    TeamDTO ennemyTeam = new TeamDTO("player1", blocks, robots, ennemyKing);
    TeamDTO ownTeam = new TeamDTO("player2", blocks, robots, onwKing);

    setScreen(new FightScreen(this, ownTeam, ennemyTeam));
  }
}
