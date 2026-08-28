package com.pdg.match;

import java.util.concurrent.atomic.AtomicLong;

public class Match {

  private PlayerConnection player1;
  private PlayerConnection player2;
  private long matchId = 0;
  public static AtomicLong nextMatchId = new AtomicLong(1);

  public void addPlayer(PlayerConnection player) {
    if (player1 == null) {
      player1 = player;
    } else {
      player2 = player;
      matchId = nextMatchId.getAndIncrement();
      sendMsgToPlayer(player1, "MATCH_ID:" + matchId);
      sendMsgToPlayer(player2, "MATCH_ID:" + matchId);
    }
  }

  void removePlayer(PlayerConnection playerConnection) {}

  void receiveMsg(PlayerConnection player, String msg) {
    if (msg.equals("Hello !")) {
      sendMsgToPlayer(player, "Hello Back !");
    }
  }

  public void sendMsgToPlayer(PlayerConnection player, String msg) {
    player.sendToPlayer(msg);
  }
}
