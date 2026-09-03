package com.pdg.logic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pdg.game.DTO.TeamDTO;
import com.pdg.game.Entity.Block;
import com.pdg.game.Entity.Entity;
import com.pdg.game.Entity.King;
import com.pdg.game.Entity.Robot;
import com.pdg.game.Entity.Team;
import com.pdg.game.GameContactListener;

public class ArenaSimulation {

  public static final float PPM = 100f;

  private static final float ARENA_WIDTH_PX = 960f;
  private static final float ARENA_HEIGHT_PX = 608f;
  private static final float BORDER_THICKNESS_PX = 20f;

  private final World world;

  private Team team1;
  private Team team2;

  private boolean gameOver;
  private boolean team1Won;

  public ArenaSimulation(TeamDTO team1DTO, TeamDTO team2DTO) {

    world = new World(new Vector2(0, -9.81f), true);
    world.setContactListener(new GameContactListener());

    createWorldBorders();

    team1 = createTeam(team1DTO);
    team2 = createTeam(team2DTO);
  }

  /**
   * Avance la simulation.
   *
   * @param delta temps écoulé
   */
  public void update(float delta) {

    if (gameOver) {
      return;
    }

    // Simulation Box2D à 60 FPS.
    world.step(1f / 60f, 6, 2);

    updateTeam(team1, false, delta);
    updateTeam(team2, true, delta);
  }

  public void setNewGameState(TeamDTO team1DTO, TeamDTO team2DTO) {
    team1 = createTeam(team1DTO);
    team2 = createTeam(team2DTO);

    gameOver = false;
    team1Won = false;
  }

  private void updateTeam(Team team, boolean isTeam2, float delta) {

    if (team.king.isDead()) {

      destroy(team.king);

      gameOver = true;

      // Si le roi de team2 est mort, team1 gagne.
      team1Won = isTeam2;

      return;
    }

    for (Block block : team.tower) {

      if (block.isDead()) {
        destroy(block);
      }
    }

    for (Robot robot : team.robots) {
      robot.reduceCooldown();
    }
  }

  /**
   * Tire un robot.
   *
   * <p>Le client envoie par exemple la vitesse calculée à partir de la direction du canon.
   */
  public void shootRobot(Robot robot, float velocityX, float velocityY) {

    if (gameOver || robot.isDead()) {
      return;
    }

    Body body = robot.getBody();

    body.setActive(true);
    body.setLinearVelocity(velocityX, velocityY);
  }

  /** Ajoute une force à un robot. */
  public void applyForce(Robot robot, float forceX, float forceY) {

    Body body = robot.getBody();

    body.applyForceToCenter(new Vector2(forceX, forceY), true);
  }

  private void createWorldBorders() {

    // Sol
    createStaticBody(new Rectangle(0, -BORDER_THICKNESS_PX, ARENA_WIDTH_PX, BORDER_THICKNESS_PX));

    // Plafond
    createStaticBody(new Rectangle(0, ARENA_HEIGHT_PX, ARENA_WIDTH_PX, BORDER_THICKNESS_PX));

    // Mur gauche
    createStaticBody(new Rectangle(-BORDER_THICKNESS_PX, 0, BORDER_THICKNESS_PX, ARENA_HEIGHT_PX));

    // Mur droit
    createStaticBody(new Rectangle(ARENA_WIDTH_PX, 0, BORDER_THICKNESS_PX, ARENA_HEIGHT_PX));
  }

  private void createStaticBody(Rectangle rect) {

    BodyDef bodyDef = new BodyDef();

    bodyDef.type = BodyDef.BodyType.StaticBody;

    bodyDef.position.set((rect.x + rect.width / 2f) / PPM, (rect.y + rect.height / 2f) / PPM);

    Body body = world.createBody(bodyDef);

    PolygonShape shape = new PolygonShape();

    shape.setAsBox(rect.width / 2f / PPM, rect.height / 2f / PPM);

    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
    fixtureDef.friction = 0.5f;
    fixtureDef.restitution = 0.2f;

    body.createFixture(fixtureDef);

    shape.dispose();
  }

  private Body createDynamicBody(Rectangle rect) {

    BodyDef bodyDef = new BodyDef();

    bodyDef.type = BodyDef.BodyType.DynamicBody;

    bodyDef.position.set((rect.x + rect.width / 2f) / PPM, (rect.y + rect.height / 2f) / PPM);

    Body body = world.createBody(bodyDef);

    PolygonShape shape = new PolygonShape();

    shape.setAsBox(rect.width / 2f / PPM, rect.height / 2f / PPM);

    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
    fixtureDef.density = 0.5f;
    fixtureDef.friction = 0.2f;
    fixtureDef.restitution = 0f;

    body.createFixture(fixtureDef);

    shape.dispose();

    return body;
  }

  private Team createTeam(TeamDTO teamDTO) {
    Team team = new Team(teamDTO, world);

    /// TODO: Implement team creation logic

    return team;
  }

  private void destroy(Entity entity) {

    Body body = entity.getBody();

    if (body != null) {

      body.setActive(false);

      world.destroyBody(body);
    }
  }

  public World getWorld() {
    return world;
  }

  public Team getTeam1() {
    return team1;
  }

  public Team getTeam2() {
    return team2;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public boolean didTeam1Win() {
    return team1Won;
  }

  public void dispose() {
    world.dispose();
  }

  public boolean isMoving(Team team) {
    for (Robot robot : team.robots) {
      if (robot.getBody().getLinearVelocity().len() > 0.01f) {
        return true;
      }
    }
    for (Block block : team.tower) {
      if (block.getBody().getLinearVelocity().len() > 0.01f) {
        return true;
      }
    }
    King king = team.king;
    if (king.getBody().getLinearVelocity().len() > 0.01f) {
      return true;
    }
    return false;
  }
}
