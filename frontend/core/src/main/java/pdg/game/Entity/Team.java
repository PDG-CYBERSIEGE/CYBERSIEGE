package pdg.game.Entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import pdg.game.Canon;
import pdg.game.DTO.BlockDTO;
import pdg.game.DTO.KingDTO;
import pdg.game.DTO.RobotDTO;
import pdg.game.DTO.TeamDTO;
import pdg.game.network.websocket.GameMessages;
import pdg.game.ui.RobotChoiceButton;

public class Team {

  // bouton coordinate
  float initialX = 420;
  float initialY = 80;
  float offset = 50;

  private static final float RECONCILIATION_THRESHOLD = 0.5f; // en mètres, à ajuster selon la tolérance voulue


  // gravity
  private boolean gravity = false;

  public String user;
  public ArrayList<Block> tower = new ArrayList<>();
  public King king;
  public ArrayList<Robot> robots = new ArrayList<>();
  public Canon canon;
  private World world;
  private Stage itemStage;
  private boolean receivedState;

  public Team(GameMessages.AvailableComponents availableComponents, World world, Stage itemStage, Vector2 canonPos) {
    canon = new Canon(world, itemStage, canonPos, new Texture("launcher/launcher.png"));
    this.world = world;
    this.itemStage = itemStage;
    this.receivedState = false;
    createKing(availableComponents.king);
    createBlocks(availableComponents.blocks);
    createRobot(availableComponents.robots);
  }

  /** A appeler ENTRE batch.begin() et batch.end() dans FightScreen.render(). */
  public void draw(SpriteBatch batch) {
    for (Block block : tower) {
      block.draw(batch);
    }

    if (king != null) {
      king.draw(batch);
    }

    for (Robot robot : robots) {
      robot.draw(batch);
    }

    if (canon != null) {
      canon.draw(batch);
    }
  }

  public void updateBlocks(World world) {
    removeDeadEntities(world);
    for (Block b : tower) {
      b.updateSprite();
    }
  }

  public void removeDeadEntities(World world) {
    tower.removeIf(
        block -> {
          if (block.isDead()) {
            block.destroy(world);
            return true;
          }
          return false;
        });
  }

  public void changeGravity() {
    gravity = !gravity;
    for (Block block : tower) {
      block.setGravityEnabled(gravity);
      if (gravity) {
        block.savePosition();
      } else {
        block.restorePosition();
      }
    }

    if (king != null) {
      king.setGravityEnabled(gravity);
      if (gravity) {
        king.savePosition();
      } else {
        king.restorePosition();
      }
    }
  }

  private void createKing(KingDTO kingDTO) {
    Rectangle kingRect = new Rectangle(-100, -100, 1, 1);
    Body kingBody = createDynamicBody(kingRect, kingDTO.mass());
    Texture kingTexture = new Texture(kingDTO.sprite());
    King newKing =
        new King(kingTexture, kingDTO.health(), kingBody, kingDTO.mass(), 1, 1, itemStage);
    kingBody.setUserData(newKing);
    this.king = newKing;
  }

  private void createBlocks(BlockDTO[] blocks) {
    for (BlockDTO blockDTO : blocks) {
      Rectangle rect = new Rectangle(-100, -100, blockDTO.length(), 1);
      Body body = createDynamicBody(rect, blockDTO.mass());
      pdg.game.Entity.Block block =
          new pdg.game.Entity.Block(
              null,
              blockDTO.uuid(),
              blockDTO.health(),
              body,
              blockDTO.mass(),
              1,
              1,
              blockDTO.type(),
              blockDTO.length());
      body.setUserData(block);
      this.tower.add(block);
      if (block.getBlockSprite() != null) {
        itemStage.addActor(block.getBlockSprite());
      }
    }
  }

  private void createRobot(RobotDTO[] robots) {
    for (RobotDTO robotDTO : robots) {
      Rectangle rect = new Rectangle(-100, -100, 1, 1);
      Body body = createDynamicBody(rect, robotDTO.mass());
      Texture texture = new Texture(robotDTO.sprite());
      Robot robot =
          new Robot(texture, robotDTO.id(), robotDTO.health(), body, robotDTO.mass(), robotDTO.cooldown(), 1, 1);
      body.setUserData(robot);
      this.robots.add(robot);
    }
  }

  private Body createDynamicBody(Rectangle rect, int mass) {
    BodyDef bdef = new BodyDef();
    bdef.type = BodyDef.BodyType.DynamicBody;
    bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

    Body body = world.createBody(bdef);

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);

    FixtureDef fdef = new FixtureDef();
    fdef.shape = shape;
    fdef.density = mass / 1000f;
    fdef.friction = 0.2f;
    fdef.restitution = 0f;

    body.createFixture(fdef);
    shape.dispose();
    return body;
  }

  public ArrayList<RobotChoiceButton> setupChoiceButton() {
    ArrayList<RobotChoiceButton> btns = new ArrayList<>();
    for (Robot robot : robots) {
      // bouton pour choisir le robot a envoyer
      RobotChoiceButton btn = new RobotChoiceButton(robot);

      btn.setPosition(initialX, initialY);

      initialX += offset;

      btn.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              canon.loadNextRobot(btn);
            }
          });
      btns.add(btn);
    }
    return btns;
  }

  public Map<String, Integer> countBlocksAtSpawn() {
    Map<String, Integer> counts = new HashMap<>();
    for (Block block : tower) {
      if (block.isAtSpawn()) {
        counts.merge(block.getType(), 1, Integer::sum);
      }
    }
    return counts;
  }

  public void saveTower() {
    for (Block b : tower) {
      b.savePosition();
    }
    king.savePosition();
  }

  public void setup() {
    for (Block b : tower) {
      b.initialState();
    }
    king.initialState();
  }

  public boolean isGravityEnabled() {
    return gravity;
  }

  public boolean isFullyPlaced() {
    boolean allBlocksPlaced = countBlocksAtSpawn().values().stream().allMatch(count -> count == 0);
    boolean kingPlaced = king == null || !king.isAtSpawn();
    return allBlocksPlaced && kingPlaced;
  }

  public void restoreHealth() {
    for (Block b : tower) {
      b.restoreHealth();
    }
    king.restoreHealth();
  }

  public boolean isReceived() {
    return receivedState;
  }

  private void received(){
    receivedState = true;
  }

  public void setupToDate(TeamDTO teamDTO){

    for (BlockDTO blockDTO : teamDTO.blocks()){
      for (Block b : tower) {
        if (b.getUUID() == blockDTO.uuid()){
          b.body.setTransform(new Vector2(blockDTO.x(), blockDTO.y()), blockDTO.angle());
          b.updateSprite();
        }
      }
    }
    king.body.setTransform(new Vector2(teamDTO.king().x(), teamDTO.king().y()), 0);

    received();
  }

  public void checkchanges(TeamDTO teamDTO) {
    for (BlockDTO blockDTO : teamDTO.blocks()) {
      for (Block b : tower) {
        if (b.getUUID() == blockDTO.uuid()) {
          Vector2 serverPos = new Vector2(blockDTO.x(), blockDTO.y());
          if (b.body.getPosition().dst(serverPos) > RECONCILIATION_THRESHOLD) {
            b.body.setTransform(serverPos, blockDTO.angle());
            b.updateSprite();
          }
        }
      }
    }

    if (king != null) {
      Vector2 serverKingPos = new Vector2(teamDTO.king().x(), teamDTO.king().y());
      if (king.body.getPosition().dst(serverKingPos) > RECONCILIATION_THRESHOLD) {
        king.body.setTransform(serverKingPos, 0);
      }
    }
  }

  public Robot getRobot(int id) {
    for (Robot robot : robots) {
      if (robot.getId() == id) {
        return robot;
      }
    }
    return null;
  }
}
