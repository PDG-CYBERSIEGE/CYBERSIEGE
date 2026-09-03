package com.pdg.match;

import com.pdg.game.DTO.BlockDTO;
import com.pdg.game.DTO.KingDTO;
import com.pdg.game.DTO.RobotDTO;
import com.pdg.game.DTO.TeamDTO;
import com.pdg.game.NewGameState;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a match between two players.
 *
 * <p>The server is authoritative for the game state of the match.
 */
public class Match {

  private PlayerConnection player1;
  private PlayerConnection player2;
  private long matchId = 0;

  private NewGameState gameState;
  private TeamDTO team1;
  private TeamDTO team2;
  private boolean player1BuildValidated;
  private boolean player2BuildValidated;

  private static final int MAX_ROUNDS = 3;
  private static final int ROUNDS_TO_WIN = MAX_ROUNDS / 2 + 1;

  private int currentRound = 0;
  private int player1Score = 0;
  private int player2Score = 0;
  private boolean roundOver = false;
  private boolean matchOver = false;
  private ArrayList<BlockDTO> availableBlocks;
  private ArrayList<RobotDTO> availableRobots;

  /** Global counter used to generate unique match identifiers. */
  public static AtomicLong nextMatchId = new AtomicLong(1);

  /** Counter used to generate unique ids for blocks/robots within this match. */
  private final AtomicInteger nextComponentId = new AtomicInteger(1);

  /**
   * Adds a player to the match.
   *
   * <p>The first player is stored as player 1. The second player is stored as player 2. Once two
   * players are present, a unique match ID is generated and sent to both players.
   *
   * @param player the player connection to add to the match
   */
  public synchronized void addPlayer(PlayerConnection player) {
    if (player1 == null) {
      player1 = player;
    } else if (player2 == null) {
      player2 = player;
      matchId = nextMatchId.getAndIncrement();
      sendStart(player1, String.valueOf(matchId), player2.playerName());
      sendStart(player2, String.valueOf(matchId), player1.playerName());
      startRound();
    } else {
      player.sendToPlayer(GameMessages.invalid());
    }
  }

  /**
   * Removes a player from the match.
   *
   * <p>If one player disconnects, the remaining player wins the match.
   *
   * @param playerConnection the player connection to remove
   */
  public synchronized void removePlayer(PlayerConnection playerConnection) {
    if (playerConnection == player1) {
      player1 = null;
      if (player2 != null) {
        sendWinner(player2);
      }
      disposeGame();
      return;
    }
    if (playerConnection == player2) {
      player2 = null;
      if (player1 != null) {
        sendWinner(player1);
      }
      disposeGame();
    }
  }

  /**
   * Processes an incoming game message.
   *
   * @param player player who sent the message
   * @param msg received message
   */
  public synchronized void receiveMsg(PlayerConnection player, String msg) {

    if (msg.equals("Hello !")) {
      player.sendToPlayer("Hello Back !");
      return;
    }

    System.out.println("[MATCH] Received message from player " + player.playerName() + ": " + msg);

    switch (GameMessages.type(msg)) {
      case "BUILD_VALIDATE":
        GameMessages.BuildValidate buildValidate = GameMessages.parseBuildValidate(msg);
        handleBuildValidate(player, buildValidate);

        System.out.println(
            "[MATCH] Build received: " + (buildValidate != null && buildValidate.team() != null));

        break;

      case "FIRE":
        GameMessages.Fire fire = GameMessages.parseFire(msg);
        handleFire(player, fire);
        break;

      default:
        player.sendToPlayer(GameMessages.invalid());
        break;
    }
  }

  /**
   * Starts a new round.
   *
   * <p>The same available components are sent to both players.
   */
  private void startRound() {

    if (matchOver || currentRound >= MAX_ROUNDS) {
      return;
    }

    // Reset the state of the previous round.
    gameState = null;
    team1 = null;
    team2 = null;
    player1BuildValidated = false;
    player2BuildValidated = false;
    roundOver = false;

    // Generate blocks and robots available for this round and send to players
    generateAvailableComponents();
    sendAvailableComponents(
        player1, availableBlocks, new KingDTO("kings/geraud.png", 0, 0, 100, 100), availableRobots);
    sendAvailableComponents(
        player2,
        availableBlocks,
        new KingDTO("kings/timothee.png", 0, 0, 100, 100),
        availableRobots);

    System.out.println("[MATCH] Starting round " + currentRound);
  }

  /**
   * Validates and stores the player's structure.
   *
   * <p>The structure is only stored as the authoritative state if it passes the server-side
   * validation.
   *
   * @param player player who sent the structure
   * @param buildValidate structure validation request
   */
  private void handleBuildValidate(
      PlayerConnection player, GameMessages.BuildValidate buildValidate) {

    if (buildValidate == null) {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    TeamDTO team = buildValidate.team();

    if (team == null) {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    // The player must belong to this match.
    if (player == player1) {

      // TODO: validate structure
      // For now, we consider every structure valid.
      boolean valid = true;

      if (!valid) {
        player.sendToPlayer(GameMessages.invalid());
        return;
      }

      // Store the validated structure as the authoritative server state.
      team1 = team;
      player1BuildValidated = true;

    } else if (player == player2) {

      // TODO: validate structure
      // For now, we consider every structure valid.
      boolean valid = true;

      if (!valid) {
        player.sendToPlayer(GameMessages.buildValidate(false));
        return;
      }

      // Store the validated structure as the authoritative server state.
      team2 = team;
      player2BuildValidated = true;

    } else {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    // Tell the client that the structure was accepted.
    player.sendToPlayer(GameMessages.buildValidate(true));

    // We cannot start the battle until both structures have been validated.
    if (!player1BuildValidated || !player2BuildValidated) {
      return;
    }

    // Both structures are valid.
    // Simulate gravity so that the structures reach their final positions.
    simulateBuild();
  }

  /**
   * Simulates both structures until they stop moving.
   *
   * <p>The resulting state becomes the authoritative state of the match.
   */
  private void simulateBuild() {

    if (gameState == null) {
      gameState = new NewGameState();
    }

    // Give the first structure to the simulation.
    gameState.simulateGravity(team1, 1);

    // Give the second structure to the simulation.
    // The simulation starts once both structures are available.
    TeamDTO[] result = gameState.simulateGravity(team2, 2);

    if (result == null) {
      return;
    }

    // Save the stabilized state.
    team1 = result[0];
    team2 = result[1];

    // Send the authoritative state to both players.
    sendTeam(player1, team1);
    sendTeam(player2, team2);
    sendTeam(player1, team2);
    sendTeam(player2, team1);
  }

  /**
   * Validates and simulates a robot throw.
   *
   * <p>The server performs the complete physical simulation until all movement has stopped, then
   * sends the resulting authoritative state to both players.
   *
   * @param player player who fired
   * @param fire fire request
   */
  private void handleFire(PlayerConnection player, GameMessages.Fire fire) {

    if (fire == null) {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    if (gameState == null || team1 == null || team2 == null) {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    int playerIndex;

    if (player == player1) {
      playerIndex = 1;
    } else if (player == player2) {
      playerIndex = 2;
    } else {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    float power = fire.power();
    float angle = fire.angle();
    int robotIndex = fire.robot();

    // TODO: validate  shot

    boolean valid = true;

    if (!valid) {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    // Send fire to opponent
    PlayerConnection enemyPlayer = playerIndex == 1 ? player2 : player1;

    if (enemyPlayer != null) {
      sendFire(enemyPlayer, fire.power(), fire.angle(), fire.robot());
    }

    try {

      // Run the complete simulation until nothing is moving anymore.
      TeamDTO enemyTeam = gameState.simulateThrow(power, angle, robotIndex, playerIndex);

      if (enemyTeam == null) {
        player.sendToPlayer(GameMessages.invalid());
        return;
      }

      // The current implementation of simulateThrow() only returns the
      // enemy team. The complete updated state will need to be exposed
      // by NewGameState as well.
      if (playerIndex == 1) {
        team2 = enemyTeam;
      } else {
        team1 = enemyTeam;
      }

    } catch (RuntimeException e) {
      player.sendToPlayer(GameMessages.invalid());
      return;
    }

    // The simulation is finished.
    // Send the authoritative final state to both players.
    sendTeam(player1, team1);
    sendTeam(player2, team2);

    checkGameOver();
  }

  /**
   * Checks whether the current round has ended (a king died), updates the score, and either ends
   * the match (if a player has won enough rounds, or the max number of rounds has been reached) or
   * starts the next round.
   */
  private void checkGameOver() {

    if (roundOver || matchOver || team1 == null || team2 == null) {
      return;
    }

    boolean team1KingDead = team1.king().health() <= 0;
    boolean team2KingDead = team2.king().health() <= 0;

    if (!team1KingDead && !team2KingDead) {
      return;
    }

    // Round is over
    roundOver = true;
    currentRound++;

    if (team2KingDead) {
      player1Score++;
      System.out.println(
          "[MATCH] Player 1 wins round "
              + currentRound
              + " (score "
              + player1Score
              + "-"
              + player2Score
              + ")");
    } else {
      player2Score++;
      System.out.println(
          "[MATCH] Player 2 wins round "
              + currentRound
              + " (score "
              + player1Score
              + "-"
              + player2Score
              + ")");
    }

    // Player won match
    if (player1Score >= ROUNDS_TO_WIN) {
      endMatch(player1, player2);
      return;
    }
    if (player2Score >= ROUNDS_TO_WIN) {
      endMatch(player2, player1);
      return;
    }

    // Match not over, next round
    startRound();
  }

  /**
   * Generates the set of blocks and robots available for the upcoming round.
   *
   * <p>Each block/robot gets a unique id (scoped to this match) via {@link #nextComponentId}.
   * Blocks are created "at rest" ({@code alive = true}) with a neutral position/angle since they
   * haven't been placed by the player yet; the client is responsible for positioning them during
   * the build phase.
   */
  private void generateAvailableComponents() {

    availableBlocks = new ArrayList<>();
    availableRobots = new ArrayList<>();

    //  Blocks
    String[] blockTypes = {"HEAVY", "MEDIUM", "LIGHT"};
    int blocksPerType = 5;
    int blockHealth = 100;
    int blockMass = 10;
    int blockLength = 3;
    // should be base on DB

    for (String type : blockTypes) {
      for (int i = 0; i < blocksPerType; i++) {
        availableBlocks.add(
            new BlockDTO(
                type,
                nextComponentId.getAndIncrement(),
                blockHealth,
                blockMass,
                true,
                0f,
                0f,
                0f,
                blockLength));
      }
    }

    // Robots
    String[] robotSprites = {"throwables/base.png", "throwables/black.png", "throwables/green.png"};
    int robotsPerType = 2;
    int robotHealth = 100;
    int robotMass = 20;
    int robotCooldown = 0;
    // should be base on DB

    for (String sprite : robotSprites) {
      for (int i = 0; i < robotsPerType; i++) {
        availableRobots.add(
            new RobotDTO(
                sprite, nextComponentId.getAndIncrement(), robotHealth, robotMass, robotCooldown));
      }
    }
  }

  /** Ends the match, notifying the winner and the loser. */
  private void endMatch(PlayerConnection winner, PlayerConnection loser) {
    matchOver = true;
    sendWinner(winner);
    sendLoser(loser);
    disposeGame();
  }

  /** Clears the game state. */
  private void disposeGame() {
    gameState = null;
    team1 = null;
    team2 = null;
    player1BuildValidated = false;
    player2BuildValidated = false;
  }

  /**
   * Sends the start message to a player.
   *
   * @param player target player
   * @param matchId match identifier
   * @param opponentName opponent name
   */
  public void sendStart(PlayerConnection player, String matchId, String opponentName) {
    player.sendToPlayer(GameMessages.start(matchId, opponentName));
  }

  /**
   * Sends the available components to a player.
   *
   * @param player target player
   * @param blocks available blocks
   * @param king player's king
   * @param robots available robots
   */
  public void sendAvailableComponents(
      PlayerConnection player,
      ArrayList<BlockDTO> blocks,
      KingDTO king,
      ArrayList<RobotDTO> robots) {
    player.sendToPlayer(GameMessages.availableComponents(blocks, king, robots));
  }

  /**
   * Sends a timer update to a player.
   *
   * @param player target player
   * @param timeInSecRemaining remaining time in seconds
   */
  public void sendTimer(PlayerConnection player, int timeInSecRemaining) {
    player.sendToPlayer(GameMessages.timer(timeInSecRemaining));
  }

  /**
   * Sends the opponent's structure to a player.
   *
   * @param player target player
   * @param blocks opponent structure blocks
   * @param king opponent's king
   */
  public void sendOpponentStructure(
      PlayerConnection player, ArrayList<BlockDTO> blocks, KingDTO king) {
    player.sendToPlayer(GameMessages.opponentStructure(blocks, king));
  }

  /**
   * Sends a winner message to a player.
   *
   * @param player target player
   */
  public void sendWinner(PlayerConnection player) {
    player.sendToPlayer(GameMessages.winner());
  }

  /**
   * Sends a loser message to a player.
   *
   * @param player target player
   */
  public void sendLoser(PlayerConnection player) {
    player.sendToPlayer(GameMessages.loser());
  }

  /**
   * Sends a team message to a player.
   *
   * @param player target player
   * @param team team to send
   */
  public void sendTeam(PlayerConnection player, TeamDTO team) {
    player.sendToPlayer(GameMessages.team(team));
  }

  /**
   * Sends a fire message to a player.
   *
   * @param player player receiving the fire
   * @param power firing power
   * @param angle firing angle
   * @param robot robot used for the attack
   */
  public void sendFire(PlayerConnection player, int power, float angle, int robot) {
    player.sendToPlayer(GameMessages.fire(power, angle, robot));
  }
}
