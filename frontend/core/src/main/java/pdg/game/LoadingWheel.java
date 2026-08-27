package pdg.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** Animated arc rendered with a {@link ShapeRenderer}. */
public class LoadingWheel extends Actor {

  /** Renderer used to draw the wheel independently of the Scene2D batch. */
  private final ShapeRenderer shapeRenderer;

  /** Outer radius of the wheel in stage units. */
  private float radius = 50;

  /** Width of the arc ring in stage units. */
  private float thickness = 12;

  /** Current starting angle of the visible arc. */
  private float startAngle = 0;

  /** Size of the visible arc in degrees. */
  private float arcAngle = 90;

  /** Rotation speed in degrees per second. */
  private float rotationSpeed = 180;

  /** Creates a wheel whose actor size matches its outer diameter. */
  public LoadingWheel() {
    shapeRenderer = new ShapeRenderer();

    setSize(radius * 2, radius * 2);
  }

  /** Advances the arc angle according to the elapsed frame time. */
  @Override
  public void act(float delta) {
    super.act(delta);
    startAngle = (startAngle + rotationSpeed * delta) % 360f;
  }

  /** Draws the arc as adjacent filled triangles after pausing the Scene2D batch. */
  @Override
  public void draw(Batch batch, float parentAlpha) {
    batch.end();

    shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

    float centerX = getX() + getWidth() / 2f;
    float centerY = getY() + getHeight() / 2f;

    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(0, 0.8f, 1, parentAlpha);

    float innerRadius = radius - thickness;

    int segments = 50;

    // Build the ring from small quads for a smooth-looking arc.
    for (int i = 0; i < segments; i++) {

      float angle1 = startAngle + arcAngle * i / segments;
      float angle2 = startAngle + arcAngle * (i + 1) / segments;

      float x1Outer = centerX + radius * MathUtils.cosDeg(angle1);
      float y1Outer = centerY + radius * MathUtils.sinDeg(angle1);

      float x2Outer = centerX + radius * MathUtils.cosDeg(angle2);
      float y2Outer = centerY + radius * MathUtils.sinDeg(angle2);

      float x1Inner = centerX + innerRadius * MathUtils.cosDeg(angle1);
      float y1Inner = centerY + innerRadius * MathUtils.sinDeg(angle1);

      float x2Inner = centerX + innerRadius * MathUtils.cosDeg(angle2);
      float y2Inner = centerY + innerRadius * MathUtils.sinDeg(angle2);

      shapeRenderer.triangle(
          x1Outer, y1Outer,
          x2Outer, y2Outer,
          x1Inner, y1Inner);

      shapeRenderer.triangle(
          x2Outer, y2Outer,
          x2Inner, y2Inner,
          x1Inner, y1Inner);
    }

    shapeRenderer.end();

    batch.begin();
  }

  /** Releases the GPU resources owned by this actor. */
  public void dispose() {
    shapeRenderer.dispose();
  }
}
