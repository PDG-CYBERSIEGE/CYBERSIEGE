package pdg.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** Scene2D group that renders the cannon structure and its animations. */
public class Cannon extends Group {

  private static final float LAUNCHER_FRAME_DURATION = .1f;
  private static final float SAW_FRAME_DURATION = .2f;

  // The launcher sprite sheet starts with a few non-firing frames before the actual
  // firing sequence, so the first real shot starts on frame 6.
  private static final int FIRST_ANIMATION_FRAME = 6;

  private boolean animateCannon = false;
  private boolean mirrored = false;

  private final Animation<TextureRegion> launcherAnimation;
  private final Animation<TextureRegion> sawAnimation;

  private final Image launcherImage;
  private final Image sawImage;

  private final TextureRegionDrawable launcherDrawable;
  private final TextureRegionDrawable sawDrawable;

  private float stateTimeLauncher = 0f;
  private float stateTimeSaw = 0f;

  /** Creates the cannon layout and loads its textures and animations. */
  public Cannon() {

    Texture barrier_left = new Texture(Gdx.files.internal("launcher/barrier_left.png"));
    Texture barrier_straight = new Texture(Gdx.files.internal("launcher/barrier_straight.png"));
    Texture barrier_left_double =
        new Texture(Gdx.files.internal("launcher/barrier_left_double.png"));

    Texture core_left = new Texture(Gdx.files.internal("launcher/core_left.png"));
    Texture core_right = new Texture(Gdx.files.internal("launcher/core_right.png"));
    Texture core_middle = new Texture(Gdx.files.internal("launcher/core_middle.png"));
    Texture core_top = new Texture(Gdx.files.internal("launcher/core_top.png"));

    Texture floor = new Texture(Gdx.files.internal("launcher/floor.png"));

    Texture pipe_left = new Texture(Gdx.files.internal("launcher/pipe_left.png"));
    Texture pipe_right = new Texture(Gdx.files.internal("launcher/pipe_right.png"));
    Texture pipe_up = new Texture(Gdx.files.internal("launcher/pipe_up.png"));

    Texture holder = new Texture(Gdx.files.internal("launcher/holder.png"));

    // =========================================================
    // LAUNCHER ANIMATION
    // =========================================================

    Texture launcherTexture = new Texture(Gdx.files.internal("launcher/launcher.png"));

    TextureRegion[][] launcherGrid = TextureRegion.split(launcherTexture, 64, 64);

    TextureRegion[] launcherFrames = new TextureRegion[launcherGrid[0].length];

    System.arraycopy(launcherGrid[0], 0, launcherFrames, 0, launcherFrames.length);

    launcherAnimation = new Animation<>(LAUNCHER_FRAME_DURATION, launcherFrames);

    // Create the drawable once and update its region during animation.
    launcherDrawable = new TextureRegionDrawable(launcherAnimation.getKeyFrame(0f));

    // =========================================================
    // SAW ANIMATION
    // =========================================================

    // The saw sprite sheet contains a few static frames at the beginning; they are
    // ignored so the blade starts spinning only when it is meant to turn.
    Texture sawTexture = new Texture(Gdx.files.internal("launcher/saw.png"));

    TextureRegion[][] sawGrid = TextureRegion.split(sawTexture, 32, 32);

    TextureRegion[] sawFrames = new TextureRegion[sawGrid[0].length - 2];

    System.arraycopy(sawGrid[0], 2, sawFrames, 0, sawFrames.length);

    sawAnimation = new Animation<>(SAW_FRAME_DURATION, sawFrames);

    sawAnimation.setPlayMode(Animation.PlayMode.LOOP);

    // Create the drawable once and update its region during animation.
    sawDrawable = new TextureRegionDrawable(sawAnimation.getKeyFrame(0f));

    Image image;

    // =========================================================
    // FIRST FLOOR
    // =========================================================

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(0, 1);
    addActor(image);

    for (int i = 0; i <= 4; i++) {
      image = new Image(floor);
      image.setSize(1, 1);
      image.setPosition(i, 0);
      addActor(image);
    }

    image = new Image(barrier_left_double);
    image.setSize(1, 1);
    image.setPosition(5, 0);
    addActor(image);

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setPosition(6, 0);
    addActor(image);

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setPosition(5, 1);
    addActor(image);

    // =========================================================
    // SECOND FLOOR BARRIER
    // =========================================================

    for (int i : new int[] {1, 3, 4}) {
      image = new Image(barrier_straight);
      image.setSize(1, 1);
      image.setPosition(i, 1);
      addActor(image);
    }

    // =========================================================
    // BACKGROUND TOWER OBJECTS
    // =========================================================

    sawImage = new Image(sawDrawable);
    sawImage.setSize(1, 1);
    sawImage.setPosition(1.5f, 4.5f);
    addActor(sawImage);

    image = new Image(holder);
    image.setSize(2, 2);
    image.setOrigin(1, 1);
    image.rotateBy(-90);
    image.setPosition(1, 3);
    addActor(image);

    // =========================================================
    // TOWER
    // =========================================================

    for (int i = 1; i <= 3; i++) {
      image = new Image(core_left);
      image.setSize(1, 1);
      image.setPosition(1, i);
      addActor(image);
    }

    image = new Image(core_top);
    image.setSize(1, 1);
    image.setPosition(1, 4);
    addActor(image);

    image = new Image(core_middle);
    image.setSize(1, 1);
    image.setPosition(2, 1);
    addActor(image);

    for (int i = 2; i <= 4; i++) {
      image = new Image(core_right);
      image.setSize(1, 1);
      image.setPosition(2, i);
      addActor(image);
    }

    image = new Image(core_top);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(2, 5);
    addActor(image);

    image = new Image(core_top);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(3, 1);
    addActor(image);

    // =========================================================
    // PIPE
    // =========================================================

    for (int i = 2; i <= 5; i++) {
      image = new Image(pipe_up);
      image.setSize(1, 1);
      image.setPosition(3, i);
      addActor(image);
    }

    image = new Image(pipe_left);
    image.setSize(1, 1);
    image.setPosition(3, 6);
    addActor(image);

    image = new Image(pipe_right);
    image.setSize(1, 1);
    image.setPosition(2, 6);
    addActor(image);

    // =========================================================
    // LAUNCHER
    // =========================================================

    launcherImage = new Image(launcherDrawable);
    launcherImage.setSize(2, 2);
    launcherImage.setOrigin(1, 1);
    launcherImage.rotateBy(90);
    launcherImage.setPosition(2, 3);
    addActor(launcherImage);

    // group does not auto update size based on childrens
    setSize(7, 7);
    setOrigin(0, 0);
  }

  // =============================================================
  // SHOOT
  // =============================================================

  /** Starts the launcher firing animation if it is not already running. */
  public void shoot() {
    if (animateCannon) {
      return;
    }

    animateCannon = true;

    // Start the firing animation on the first active firing frame.
    stateTimeLauncher = FIRST_ANIMATION_FRAME * LAUNCHER_FRAME_DURATION;
  }

  // =============================================================
  // CHARGEMENT
  // =============================================================

  /**
   * Updates the launcher preview to match the current loading progress.
   *
   * @param loadingRatio normalized loading value between 0 and 1
   */
  public void setLoadingStage(float loadingRatio) {
    if (animateCannon) {
      return;
    }

    float clampedRatio = Math.max(0f, Math.min(loadingRatio, 1f));

    // The loading preview only covers the first 5 frames of the firing sequence.
    int frame = (int) (clampedRatio * (FIRST_ANIMATION_FRAME - 1));
    stateTimeLauncher = frame * LAUNCHER_FRAME_DURATION;

    launcherDrawable.setRegion(launcherAnimation.getKeyFrame(stateTimeLauncher));
  }

  // =============================================================
  // ANIMATION
  // =============================================================

  /**
   * Advances the saw and launcher animations.
   *
   * @param delta elapsed time since the previous frame, in seconds
   */
  public void animate(float delta) {
    // The saw is constantly spinning while the cannon is visible.
    stateTimeSaw += delta;
    sawDrawable.setRegion(sawAnimation.getKeyFrame(stateTimeSaw));

    // annimate the cannon if user activated it
    if (!animateCannon) {
      return;
    }

    stateTimeLauncher += delta;
    launcherDrawable.setRegion(launcherAnimation.getKeyFrame(stateTimeLauncher));

    if (launcherAnimation.isAnimationFinished(stateTimeLauncher)) {
      animateCannon = false;
      stateTimeLauncher = 0f;
      launcherDrawable.setRegion(launcherAnimation.getKeyFrame(0f));
    }
  }

  /** Mirrors the cannon horizontally and keeps its world position coherent. */
  public void setMirrored(boolean mirrored) {
    if (this.mirrored == mirrored) {
      return;
    }

    float x = getX();

    if (mirrored) {
      setScaleX(-1);
      setX(x + getWidth());
    } else {
      setScaleX(1);
      setX(x - getWidth());
    }

    this.mirrored = mirrored;
  }

  /** Returns the center of the launcher in stage coordinates. */
  public Vector2 getLauncherCenter() {
    Vector2 position = new Vector2(launcherImage.getWidth() / 2f, launcherImage.getHeight() / 2f);

    launcherImage.localToStageCoordinates(position);
    return position;
  }

  /** Returns the launcher image node. */
  public Image getLauncher() {
    return launcherImage;
  }
}
