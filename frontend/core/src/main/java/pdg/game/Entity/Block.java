package pdg.game.Entity;

import static com.badlogic.gdx.math.MathUtils.*;
import static pdg.game.utils.StaticValues.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import pdg.game.DTO.BlockDTO;
import pdg.game.blocks.BlockSprite;
import pdg.game.blocks.HeavyBlockSprite;
import pdg.game.blocks.LightBlockSprite;
import pdg.game.blocks.MediumBlockSprite;

/**
 * * Représente un bloc physique du jeu. * *
 *
 * <p>Un bloc possède une représentation graphique {@link BlockSprite} ainsi qu'un corps physique *
 * {@link Body} géré par Box2D. * *
 *
 * <p>Les blocs peuvent être déplacés et tournés pendant la phase de construction. Une fois la *
 * physique activée, le bloc devient dynamique et est soumis à la gravité.
 */
public class Block extends Entity {

  private final long UUID;

  BlockSprite blockSprite;
  private boolean physicsEnabled =
      false; // false = phase construction (kinematic, immobile sauf drag)
  private final String type;
  private final Vector2 spawnPosition;
  private Rectangle buildZone; // zone autorisée pour ce bloc pendant la phase construction

  private Vector2 savedPosition;

  private float savedAngle;
  private int length;

  /**
   * * Crée un nouveau bloc. * *
   *
   * <p>Le type du bloc détermine le sprite utilisé ainsi que sa position de spawn. * * @param
   * sprite texture utilisée par l'entité * @param health points de vie du bloc * @param body corps
   * physique Box2D associé au bloc * @param mass masse du bloc * @param height hauteur du bloc
   * * @param width largeur du bloc * @param type type du bloc ({@code HEAVY}, {@code MEDIUM} ou
   * {@code LIGHT}) * @param length longueur du sprite du bloc
   */
  public Block(
      Texture sprite,
      long UUID,
      Integer health,
      Body body,
      int mass,
      float height,
      float width,
      String type,
      int length) {
    super(health, sprite, body, mass, height, width);
    this.type = type;
    this.UUID = UUID;
    this.length = length;

    switch (type) {
      case "HEAVY":
        blockSprite = new HeavyBlockSprite(length);
        spawnPosition = HEAVYSPAWN;
        break;

      case "MEDIUM":
        blockSprite = new MediumBlockSprite(length);
        spawnPosition = MEDIUMSPAWN;
        break;
      case "LIGHT":
        blockSprite = new LightBlockSprite(length);
        spawnPosition = LIGHTSPAWN;
        break;
      default:
        Gdx.app.error("Block", "Type de bloc inconnu ou null : '" + type + "'");
        blockSprite = null;
        spawnPosition = null;
        break;
    }

    if (blockSprite == null) return; // évite le NPE si le type ne matche rien

    blockSprite.resize(1);
    Vector2 worldPos = body.getPosition(); // centre du body, en mètres

    // Table.setPosition attend le coin bas-gauche, pas le centre
    blockSprite.setPosition(
        worldPos.x - blockSprite.getWidth() / 2f, worldPos.y - blockSprite.getHeight() / 2f);

    // rectangle for construction
    buildZone = new Rectangle(RECTX, RECTY, RECTWIDTH, RECTHEIGHT);

    setGravityEnabled(
        false); // phase construction par défaut : pas de gravité, pas de collision-push
    setupDragging();
  }

  /**
   * * Met à jour la position et la rotation du sprite afin qu'elles correspondent au corps physique
   * * Box2D. * *
   *
   * <p>La position du {@link Body} représente le centre du bloc tandis que la position du sprite *
   * correspond à son coin inférieur gauche.
   */
  public void updateSprite() {
    if (blockSprite == null) return;

    Vector2 worldPos = body.getPosition(); // centre du body, en mètres

    // Table.setPosition attend le coin bas-gauche, pas le centre
    blockSprite.setPosition(
        worldPos.x - blockSprite.getWidth() / 2f, worldPos.y - blockSprite.getHeight() / 2f);
    // body.getAngle() est en radians, setRotation() de Scene2D attend des degrés
    blockSprite.setRotation(radiansToDegrees * body.getAngle());
  }

  /**
   * Retourne le sprite graphique associé au bloc.
   *
   * @return sprite du bloc
   */
  public BlockSprite getBlockSprite() {
    return blockSprite;
  }

  // nécessaire vu qu'on utilise une table et non un sprite comme les entité
  @Override
  public void draw(SpriteBatch batch) {}

  /**
   * * Détruit le bloc ainsi que son corps physique Box2D. * * @param world monde Box2D contenant le
   * corps du bloc
   */
  public void destroy(World world) {
    if (blockSprite != null) {
      blockSprite.disposeTextures();
      blockSprite.remove();
    }
    world.destroyBody(body);
  }

  /**
   * * Active ou désactive la gravité et la physique dynamique du bloc. * *
   *
   * <p>Lorsque la physique est activée et que le bloc n'est pas à sa position de spawn, son corps *
   * devient un {@link BodyDef.BodyType#DynamicBody} et est soumis à la gravité. * *
   *
   * <p>Dans le cas contraire, le corps devient {@link BodyDef.BodyType#KinematicBody} et sa *
   * vitesse est remise à zéro. * * @param enabled {@code true} pour activer la physique dynamique,
   * {@code false} pour la * désactiver
   */
  public void setGravityEnabled(boolean enabled) {
    physicsEnabled = enabled;
    if (enabled && !isAtSpawn()) {
      body.setType(BodyDef.BodyType.DynamicBody);
      body.setGravityScale(1f);
    } else {
      body.setLinearVelocity(0, 0);
      body.setAngularVelocity(0);
      body.setType(BodyDef.BodyType.KinematicBody);
    }
  }

  /**
   * * Configure les interactions permettant au joueur de déplacer et de faire tourner le bloc. * *
   *
   * <p>Le clic gauche permet de déplacer le bloc tandis que le clic droit permet de le faire *
   * tourner.
   */
  private void setupDragging() {

    // --- Clic gauche : déplacement ---
    DragListener moveListener =
        new DragListener() {

          private final Vector2 startBodyPos = new Vector2();
          private final Vector2 startTouchStage = new Vector2();

          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            boolean started = super.touchDown(event, x, y, pointer, button);
            if (started) {
              startBodyPos.set(body.getPosition());
              startTouchStage.set(event.getStageX(), event.getStageY());
            }
            return started;
          }

          @Override
          public void drag(InputEvent event, float x, float y, int pointer) {
            if (physicsEnabled) return;

            Vector2 currentTouchStage = new Vector2(event.getStageX(), event.getStageY());
            Vector2 offset = currentTouchStage.sub(startTouchStage);
            Vector2 newPos = startBodyPos.cpy().add(offset);

            body.setTransform(newPos, body.getAngle());
          }

          @Override
          public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.touchUp(event, x, y, pointer, button);
            if (physicsEnabled) return;

            if (buildZone != null && !buildZone.contains(body.getPosition())) {
              initialState();
              updateSprite();
            }
          }
        };
    moveListener.setButton(Input.Buttons.LEFT);
    moveListener.setTapSquareSize(0.05f);
    blockSprite.addListener(moveListener);

    // --- Clic droit : rotation ---
    DragListener rotateListener =
        new DragListener() {
          @Override
          public void drag(InputEvent event, float x, float y, int pointer) {
            if (physicsEnabled) return;

            // x,y sont en coordonnées locales à l'acteur -> on les convertit en coordonnées
            // itemStage
            // (mêmes unités que body.getPosition())
            Vector2 stageCoords = blockSprite.localToStageCoordinates(new Vector2(x, y));
            Vector2 center = body.getPosition();

            float angle = MathUtils.atan2(stageCoords.y - center.y, stageCoords.x - center.x);
            body.setTransform(center, angle);
          }
        };
    rotateListener.setButton(Input.Buttons.RIGHT);
    rotateListener.setTapSquareSize(0.05f);
    blockSprite.addListener(rotateListener);
  }

  /** * Retourne le type du bloc. * * @return type du bloc */
  public String getType() {
    return type;
  }

  /**
   * * Vérifie si le bloc se trouve toujours à proximité de sa position de spawn initiale. *
   * * @return {@code true} si le bloc est suffisamment proche de sa position de spawn, sinon *
   * {@code false}
   */
  public boolean isAtSpawn() {
    return body.getPosition().dst(spawnPosition) < SPAWN_THRESHOLD;
  }

  /**
   * * Sauvegarde la position et la rotation actuelles du bloc. * *
   *
   * <p>Cette méthode est notamment utilisée avant d'activer la gravité afin de pouvoir revenir *
   * ultérieurement à l'état précédent.
   */
  public void savePosition() {
    savedPosition = body.getPosition().cpy();
    savedAngle = body.getAngle();
  }

  /**
   * * Restaure la dernière position et rotation sauvegardées du bloc. * *
   *
   * <p>Après restauration, la gravité est désactivée et le bloc repasse en mode cinématique.
   */
  public void restorePosition() {
    if (savedPosition == null) return; // rien à restaurer si jamais sauvegardé

    setGravityEnabled(false); // repasse en kinematic avant de forcer la position
    body.setTransform(savedPosition, savedAngle);
    updateSprite(); // évite d'attendre la prochaine frame pour voir le résultat
  }

  /** * Replace le bloc dans son état initial, à sa position de spawn avec une rotation nulle. */
  public void initialState() {
    body.setTransform(spawnPosition, 0);
  }

  public long getUUID() {
    return UUID;
  }

  public BlockDTO getDTO() {
    return new BlockDTO(
        type,
        UUID,
        health,
        mass,
        true,
        body.getPosition().x - width / 2f,
        body.getPosition().y - height / 2f,
        savedAngle,
        length);
  }
}
