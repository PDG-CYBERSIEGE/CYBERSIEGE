package pdg.game.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class StaticValues {

  public static int DAMAGETRESHOLD = 10;
  public static String HEAVY = "HEAVY";
  public static String MEDIUM = "MEDIUM";
  public static String LIGHT = "LIGHT";

  // block spawn
  public static Vector2 HEAVYSPAWN = new Vector2(-5f, 9f);
  public static Vector2 MEDIUMSPAWN = new Vector2(-5f, 7f);
  public static Vector2 LIGHTSPAWN = new Vector2(-5f, 5f);

  public static Vector2 KINGSPAWN = new Vector2(-5f, 3f);

  public static Vector2 ALLYCANONSPAWN = new Vector2(7f, 3f);
  public static Vector2 ENNEMYCANONSPAWN = new Vector2(24f, 3f);

  public static final float SPAWN_THRESHOLD = 0.3f;

  // rectangle values for construction area phase 1
  public static float RECTX = 1f;
  public static float RECTY = 2f;
  public static float RECTWIDTH = 5f;
  public static float RECTHEIGHT = 10f;
  public static float RECTOFFSET = 25f;

  public static Rectangle OWNBUILDINGZONE = new Rectangle(1f, 2f, 5f, 10f);
}
