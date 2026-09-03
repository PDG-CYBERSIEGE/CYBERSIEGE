package pdg.game;

import static pdg.game.utils.StaticValues.*;

import com.badlogic.gdx.*;

import java.util.ArrayList;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.screens.FightScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import pdg.game.network.websocket.GameWebSocket;
import pdg.game.scene.Background;
import pdg.game.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

  private final GameWebSocket gameWebSocket;

  public Main(GameWebSocket gameWebSocket) {
    this.gameWebSocket = gameWebSocket;
  }

  @Override
  public void create() {

    // test avec le screen de jeu

    ArrayList<BlockDTO> blocks = new ArrayList<>();
    BlockDTO block = new BlockDTO(HEAVY, 1,  50, 100, true, 30, 4, 3);
    blocks.add(block);
    blocks.add(new BlockDTO(MEDIUM, 2 ,50, 800, true, 26, 2, 3));
    blocks.add(new BlockDTO(LIGHT, 3,50, 800, true, 28, 5, 3));

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
    TeamDTO ownTeam = new TeamDTO("sky", blocks, robots, onwKing);

    Screen screen1 = new FightScreen(this, "sky");
    screen1.

    setScreen(screen1);


    /*
    Background background = new Background();
    Stage backgroundStage = new Stage(new FitViewport(1920, 1080));
    background.apply(backgroundStage);
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    setScreen(new MainMenuScreen(this, backgroundStage, gameWebSocket));

     */
  }
}
