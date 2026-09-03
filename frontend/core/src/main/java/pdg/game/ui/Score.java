package pdg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/** Displays the players, timer and round scores in a Scene2D window. */
public class Score extends Table {

  /** Window containing the score display. */
  private Window window;

  /** Table exposing the score content to callers. */
  private Table content;

  /** Visual indicators for player one's completed rounds. */
  private Image[] scoreDisplay1;

  /** Visual indicators for player two's completed rounds. */
  private Image[] scoreDisplay2;

  /** Number of rounds currently displayed for player one. */
  private int scoreValue1 = 0;

  /** Number of rounds currently displayed for player two. */
  private int scoreValue2 = 0;

  /** Texture used when a round is completed. */
  private Texture scoreTexture;

  /** Horizontal space reserved around the score content. */
  private final int padding = 40;

  /** Creates a score display with default player names and two rounds. */
  public Score() {
    this("Player1", "Player2", 2);
  }

  /**
   * Creates a score display with optional explicit dimensions.
   *
   * @param p1 first player name
   * @param p2 second player name
   * @param width requested width
   * @param height requested height
   * @param nbManches number of rounds
   */
  public Score(String p1, String p2, int width, int height, int nbManches) {
    this(p1, p2, nbManches);
    if (width <= 0 || height <= 0) return;
    setSize(width, height);
  }

  /**
   * Creates and sizes the complete score window from its content.
   *
   * @param p1 first player name
   * @param p2 second player name
   * @param nbManches number of rounds
   */
  public Score(String p1, String p2, int nbManches) {

    // =========================
    // TEST PARAMETERS
    // =========================

    if (p1 == null || p2 == null || nbManches <= 0) return;

    // =========================
    // SKIN
    // =========================

    Skin skin = new Skin(Gdx.files.internal("futuristic_ui/uiskin.json"));

    // =========================
    // WINDOW
    // =========================

    window = new Window("", skin, "score");
    window.setMovable(false);

    // =========================
    // BACKGROUND
    // =========================

    Texture frameTexture =
        new Texture(Gdx.files.internal("futuristic_ui/frame_background/border_frame.9.png"));

    NinePatch ninePatch = new NinePatch(frameTexture, 10, 10, 10, 10);

    Table windowContent = new Table();
    windowContent.setBackground(new NinePatchDrawable(ninePatch));

    // =========================
    // TABLES
    // =========================

    content = new Table();

    Table players = new Table();
    Table scores = new Table();

    Table score1 = new Table();
    Table score2 = new Table();

    // =========================
    // LABELS
    // =========================

    Label player1 = new Label(p1, skin);
    Label player2 = new Label(p2, skin);
    Label timer = new Label("0:00", skin);
    Label vs = new Label("vs", skin);

    // =========================
    // SCORE IMAGES
    // =========================

    Texture emptyScoreTexture = new Texture(Gdx.files.internal("futuristic_ui/bars/empty_bar.png"));

    scoreTexture = new Texture(Gdx.files.internal("futuristic_ui/bars/full_bar.png"));

    scoreDisplay1 = new Image[nbManches];
    scoreDisplay2 = new Image[nbManches];

    for (int i = 0; i < nbManches; i++) {
      scoreDisplay1[i] = new Image(emptyScoreTexture);
      scoreDisplay2[i] = new Image(emptyScoreTexture);

      score1.add(scoreDisplay1[i]).height(15);

      score2.add(scoreDisplay2[i]).height(15);
    }

    // =========================
    // WIDTH
    // =========================

    float playerWidth = Math.max(player1.getPrefWidth(), player2.getPrefWidth());

    float playersWidth = playerWidth * 2 + vs.getPrefWidth();

    float scoresWidth =
        scoreDisplay1.length * scoreDisplay1[0].getPrefWidth() * 2 + timer.getPrefWidth();

    float requiredWidth = Math.max(playersWidth, scoresWidth) + padding;

    // =========================
    // PLAYERS
    // =========================

    players.add(player1).left().width(playerWidth);

    players.add(vs).center().expandX();

    players.add(player2).right().width(playerWidth);

    players.setWidth(requiredWidth);

    // =========================
    // SCORES
    // =========================

    scores.add(score1).left().expandX();

    scores.add(timer).center().expandX();

    scores.add(score2).right().expandX();

    scores.setWidth(requiredWidth);

    // =========================
    // CONTENT
    // =========================

    content.add(players).width(requiredWidth).top();

    content.row();

    content.add().height(20);

    content.row();

    content.add(scores).width(requiredWidth);

    content.pack();

    // =========================
    // WINDOW
    // =========================

    windowContent.add(content).pad(10);

    windowContent.setWidth(requiredWidth);
    windowContent.pack();

    window.add(windowContent).pad(-1);

    window.setWidth(requiredWidth);
    window.pack();

    add(window);
  }

  /** Marks the next available round as won by player one. */
  public void addScoreP1() {
    if (scoreValue1 >= scoreDisplay1.length) {
      return;
    }

    scoreDisplay1[scoreValue1].setDrawable(new Image(scoreTexture).getDrawable());

    scoreValue1++;
  }

  /** Marks the next available round as won by player two. */
  public void addScoreP2() {
    if (scoreValue2 >= scoreDisplay2.length) {
      return;
    }

    scoreDisplay2[scoreValue2].setDrawable(new Image(scoreTexture).getDrawable());

    scoreValue2++;
  }

  public int getScoreValue1() {
    return scoreValue1;
  }

  public int getScoreValue2() {
    return scoreValue2;
  }
}
