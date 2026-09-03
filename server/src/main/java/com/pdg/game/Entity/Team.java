package com.pdg.game.Entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pdg.game.DTO.BlockDTO;
import com.pdg.game.DTO.KingDTO;
import com.pdg.game.DTO.RobotDTO;
import com.pdg.game.DTO.TeamDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Team {

  public String user;

  public ArrayList<Block> tower = new ArrayList<>();

  public King king;

  public ArrayList<Robot> robots = new ArrayList<>();

  public Canon canon;

  private World world;

  private boolean gravity = false;

  public Team(TeamDTO teamDTO, World world) {

    canon = new Canon(world, new Vector2(24f, 3f));

    this.world = world;
    this.user = teamDTO.name();

    createKing(teamDTO);

    createBlocks(teamDTO);

    createRobot(teamDTO);
  }

  public void changeGravity() {

    gravity = !gravity;

    for (Block block : tower) {

      block.setGravityEnabled(gravity);
    }

    if (king != null) {

      king.setGravityEnabled(gravity);
    }
  }

  private void createKing(TeamDTO teamDTO) {

    KingDTO kingDTO = teamDTO.king();

    Rectangle kingRect = new Rectangle(kingDTO.x(), kingDTO.y(), 1, 1);

    Body kingBody = createDynamicBody(kingRect, kingDTO.mass());

    King newKing = new King(kingDTO.sprite(), kingDTO.health(), kingBody, kingDTO.mass(), 1, 1);

    kingBody.setUserData(newKing);

    this.king = newKing;
  }

  private void createBlocks(TeamDTO teamDTO) {

    for (BlockDTO blockDTO : teamDTO.blocks()) {

      Rectangle rect = new Rectangle(blockDTO.x(), blockDTO.y(), blockDTO.length(), 1);

      Body body = createDynamicBody(rect, blockDTO.mass());

      Block block =
          new Block(
              blockDTO.type(), blockDTO.health(), body, blockDTO.mass(), 1, 1, blockDTO.length());

      body.setUserData(block);

      this.tower.add(block);
    }
  }

  private void createRobot(TeamDTO teamDTO) {

    for (RobotDTO robotDTO : teamDTO.robots()) {

      Rectangle rect = new Rectangle(-100, -100, 1, 1);

      Body body = createDynamicBody(rect, robotDTO.mass());

      Robot robot =
          new Robot(
              robotDTO.sprite(), robotDTO.health(), body, robotDTO.mass(), robotDTO.cooldown(), 1, 1);

      body.setUserData(robot);

      robots.add(robot);
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

  public void setupBlocks() {

    for (Block b : tower) {

      b.initialState();
    }
  }

  public TeamDTO getDTO() {

    ArrayList<BlockDTO> blockDTOs = new ArrayList<>();

    for (Block block : tower) {

      blockDTOs.add(block.getDTO());
    }

    ArrayList<RobotDTO> robotDTOs = new ArrayList<>();

    for (Robot robot : robots) {

      robotDTOs.add(robot.getDTO());
    }

    return new TeamDTO(user, blockDTOs, robotDTOs, king.getDTO());
  }
}
