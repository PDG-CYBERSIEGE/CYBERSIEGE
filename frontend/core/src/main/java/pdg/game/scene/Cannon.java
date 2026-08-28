package pdg.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** Scene2D group representing the cannon, tower, barriers, and animations. */
public class Cannon extends Group {

  boolean animateCannon = false;

  Animation<TextureRegion> launcherAnimation;
  Image launcherImage;
  TextureRegionDrawable launcherDrawable;

  float launcherFrameDuration = .1f;

  Animation<TextureRegion> sawAnimation;
  Image sawImage;
  TextureRegionDrawable sawDrawable;

  float stateTimeLauncher = 0;
  float stateTimeSaw = 0;

  static final int FIRST_ANIMATION_FRAME = 6;

  /** Creates the cannon layout and loads its textures and animations. */
  public Cannon() {

    Texture barrier_left = new Texture(Gdx.files.internal("launcher/barrier_left.png"));
    Texture barrier_straight = new Texture(Gdx.files.internal("launcher/barrier_straight.png"));
    Texture barrier_left_double = new Texture(Gdx.files.internal("launcher/barrier_left_double.png"));

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

    Texture texture = new Texture(Gdx.files.internal("launcher/launcher.png"));

    TextureRegion[][] tmp = TextureRegion.split(texture, 64, 64);

    TextureRegion[] launcher_frames = new TextureRegion[tmp[0].length];

    System.arraycopy(tmp[0], 0, launcher_frames, 0, launcher_frames.length);

    launcherAnimation = new Animation<>(launcherFrameDuration, launcher_frames);

    // Create the drawable once and update its region during animation.
    launcherDrawable = new TextureRegionDrawable(launcherAnimation.getKeyFrame(0));

    // =========================================================
    // SAW ANIMATION
    // =========================================================

    texture = new Texture(Gdx.files.internal("launcher/saw.png"));

    tmp = TextureRegion.split(texture, 32, 32);

    TextureRegion[] saw_frames = new TextureRegion[tmp[0].length - 2];

    // Ignore the first two frames cause saw is not turning on them.
    System.arraycopy(tmp[0], 2, saw_frames, 0, saw_frames.length);

    sawAnimation = new Animation<>(.2f, saw_frames);

    sawAnimation.setPlayMode(Animation.PlayMode.LOOP);

    // Create the drawable once and update its region during animation.
    sawDrawable = new TextureRegionDrawable(sawAnimation.getKeyFrame(0));

    Image image;

    // =========================================================
    // FIRST FLOOR
    // =========================================================

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(0, 0);
    addActor(image);

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(1, 1);
    addActor(image);

    image = new Image(barrier_left_double);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(1, 0);
    addActor(image);

    for (int i = 2; i <= 6; i++) {
      image = new Image(floor);
      image.setSize(1, 1);
      image.setPosition(i, 0);
      addActor(image);
    }

    image = new Image(barrier_left_double);
    image.setSize(1, 1);
    image.setPosition(7, 0);
    addActor(image);

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setPosition(8, 0);
    addActor(image);

    image = new Image(barrier_left);
    image.setSize(1, 1);
    image.setPosition(7, 1);
    addActor(image);

    // =========================================================
    // SECOND FLOOR BARRIER
    // =========================================================

    for (int i : new int[] {2, 3, 5, 6}) {
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
    sawImage.setPosition(3.5f, 4.5f);
    addActor(sawImage);

    image = new Image(holder);
    image.setSize(2, 2);
    image.setOrigin(1, 1);
    image.rotateBy(-90);
    image.setPosition(3, 3);
    addActor(image);

    // =========================================================
    // TOWER
    // =========================================================

    for (int i = 1; i <= 3; i++) {
      image = new Image(core_left);
      image.setSize(1, 1);
      image.setPosition(3, i);
      addActor(image);
    }

    image = new Image(core_top);
    image.setSize(1, 1);
    image.setPosition(3, 4);
    addActor(image);

    image = new Image(core_middle);
    image.setSize(1, 1);
    image.setPosition(4, 1);
    addActor(image);

    for (int i = 2; i <= 4; i++) {
      image = new Image(core_right);
      image.setSize(1, 1);
      image.setPosition(4, i);
      addActor(image);
    }

    image = new Image(core_top);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(4, 5);
    addActor(image);

    image = new Image(core_top);
    image.setSize(1, 1);
    image.setOrigin(.5f, .5f);
    image.setScaleX(-1);
    image.setPosition(5, 1);
    addActor(image);

    // =========================================================
    // PIPE
    // =========================================================

    image = new Image(pipe_up);
    image.setSize(1, 1);
    image.setPosition(5, 2);
    addActor(image);

    image = new Image(pipe_up);
    image.setSize(1, 1);
    image.setPosition(5, 5);
    addActor(image);

    image = new Image(pipe_left);
    image.setSize(1, 1);
    image.setPosition(5, 6);
    addActor(image);

    image = new Image(pipe_right);
    image.setSize(1, 1);
    image.setPosition(4, 6);
    addActor(image);

    // =========================================================
    // LAUNCHER
    // =========================================================

    launcherImage = new Image(launcherDrawable);
    launcherImage.setSize(2, 2);
    launcherImage.setOrigin(1, 1);
    launcherImage.rotateBy(90);
    launcherImage.setPosition(4, 3);
    addActor(launcherImage);
  }

  // =============================================================
  // SHOOT
  // =============================================================

  /** Starts the launcher firing animation if it is not already running. */
  public void shoot() {

    if (animateCannon) return;

    animateCannon = true;

    // Start at frame 6.
    stateTimeLauncher = launcherFrameDuration * FIRST_ANIMATION_FRAME;
  }

  // =============================================================
  // CHARGEMENT
  // =============================================================

  /**
   * Updates the launcher preview to match the loading progress.
   *
   * @param loadingRatio loading progress from {@code 0} to {@code 1}
   */
  public void setLoadingStage(float loadingRatio) {

    if (animateCannon) return;

    // 0 -> frame 0
    // 1 -> frame 5
    int frame = (int) (loadingRatio * (FIRST_ANIMATION_FRAME - 1));

    stateTimeLauncher = frame * launcherFrameDuration;

    // Only change the drawable region.
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

    // ---------------------------------------------------------
    // SAW: always rotating
    // ---------------------------------------------------------

    stateTimeSaw += delta;

    sawDrawable.setRegion(sawAnimation.getKeyFrame(stateTimeSaw));

    // ---------------------------------------------------------
    // LAUNCHER
    // ---------------------------------------------------------

    if (!animateCannon) return;

    stateTimeLauncher += delta;

    launcherDrawable.setRegion(launcherAnimation.getKeyFrame(stateTimeLauncher));

    // Animation finished.
    if (launcherAnimation.isAnimationFinished(stateTimeLauncher)) {

      animateCannon = false;

      stateTimeLauncher = 0;

      // Reset the launcher to frame 0.
      launcherDrawable.setRegion(launcherAnimation.getKeyFrame(0));
    }
  }
}
