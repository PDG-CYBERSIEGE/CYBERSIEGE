package pdg.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import pdg.game.DTO.BlockDTO;import pdg.game.DTO.KingDTO;import pdg.game.DTO.RobotDTO;import pdg.game.DTO.TeamDTO;import pdg.game.network.websocket.GameMessages;import pdg.game.network.websocket.GameWebSocket;
import pdg.game.scene.Background;
import pdg.game.screens.FightScreen;import pdg.game.screens.MainMenuScreen;import java.util.ArrayList;import static pdg.game.utils.StaticValues.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

  private final GameWebSocket gameWebSocket;
  private MainMenuScreen mainMenuScreen;

  public Main(GameWebSocket gameWebSocket) {
    this.gameWebSocket = gameWebSocket;
  }

  @Override
  public void create() {

    // test avec le screen de jeu
    /*
    ArrayList<BlockDTO> blocks = new ArrayList<>();
    BlockDTO block = new BlockDTO(HEAVY, 1,  50, 100, true, 30, 4, 0, 3);
    blocks.add(block);
    blocks.add(new BlockDTO(MEDIUM, 2 ,50, 800, true, 26, 2, 0,3));
    blocks.add(new BlockDTO(LIGHT, 3,50, 800, true, 28, 5, 0,3));

    BlockDTO[] blok = {block, new BlockDTO(MEDIUM, 2 ,50, 800, true, 26, 2, 0,3), new BlockDTO(LIGHT, 3,50, 800, true, 28, 5, 0,3)};

    ArrayList<RobotDTO> robots = new ArrayList<>();
    RobotDTO robot1 = new RobotDTO(1,"throwables/base.png", 50, 100, 2);
    RobotDTO robot2 = new RobotDTO(2,"throwables/green.png", 50, 500, 10);
    RobotDTO robot3 = new RobotDTO(3,"throwables/black.png", 50, 800, 50);
    robots.add(robot1);
    robots.add(robot2);
    robots.add(robot3);

    RobotDTO[] roro = { robot1, robot2, robot3 };

    KingDTO ennemyKing = new KingDTO("kings/timothee.png", 26, 6, 10000, 100);
    KingDTO onwKing = new KingDTO("kings/geraud.png", 0, 0, 10000, 100);

    TeamDTO ennemyTeam = new TeamDTO("player1", blocks, robots, ennemyKing);
    TeamDTO ownTeam = new TeamDTO("sky", blocks, robots, onwKing);

    FightScreen screen1 = new FightScreen(this, ownTeam, ennemyTeam);
    GameMessages.AvailableComponents team11 = new GameMessages.AvailableComponents();
    team11.blocks = blok;
    team11.king = ennemyKing;
    team11.robots = roro;

    GameMessages.AvailableComponents team22 = new GameMessages.AvailableComponents();
    team22.blocks = blok;
    team22.king = onwKing;
    team22.robots = roro;

    screen1.receiveAvailableComponent(team11);
    screen1.receiveAvailableComponent(team22);

    setScreen(screen1);



     */


    Background background = new Background();
    Stage backgroundStage = new Stage(new FitViewport(1920, 1080));
    background.apply(backgroundStage);
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    mainMenuScreen = new MainMenuScreen(this, backgroundStage, gameWebSocket);
    setScreen(mainMenuScreen);


  }

  public MainMenuScreen getMainMenuScreen() {
    return mainMenuScreen;
  }
}
