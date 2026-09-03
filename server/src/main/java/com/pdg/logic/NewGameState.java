package com.pdg.logic;

import com.pdg.logic.DTO.TeamDTO;
import com.pdg.logic.Entity.Team;

public class NewGameState {

    private final static float GRAVITY_SIMULATION_STEP = 1f / 60f;
    private final static float maxVelocity = 100f; // vitesse maximum auhotisée

    ArenaSimulation arenaSimulation;
    TeamDTO[] teamsDTO = new TeamDTO[2];

    public TeamDTO[] simulateGravity(TeamDTO teamDTO, int player) {

        if (player < 1 || player > 2) {
            throw new IllegalArgumentException("Player must be either 1 or 2.");
        }
        teamsDTO[player - 1] = teamDTO;

        if(teamsDTO[0] == null || teamsDTO[1] == null) {
            return null; // On attend que les deux équipes soient prêtes.
        }
        if(arenaSimulation == null) {
            arenaSimulation = new ArenaSimulation(teamsDTO[0], teamsDTO[1]);
        } else {
            arenaSimulation.setNewGameState(teamsDTO[0], teamsDTO[1]);
        }

        while(arenaSimulation.isMoving(arenaSimulation.getTeam1()) || arenaSimulation.isMoving(arenaSimulation.getTeam2())) {
            arenaSimulation.update(GRAVITY_SIMULATION_STEP);
        }

        return new TeamDTO[] {arenaSimulation.getTeam1().getDTO(), arenaSimulation.getTeam2().getDTO() };
    }

    public TeamDTO simulateThrow(float power, float angle, int robotIndex, int player) {
        if(arenaSimulation == null) {
            throw new IllegalStateException("ArenaSimulation is not initialized. Call simulateGravity first.");
        }
        if (power > maxVelocity){
            power = maxVelocity; // On limite la puissance à la vitesse maximale autorisée
        }

        teamsDTO[0] = null; // On réinitialise les équipes pour la prochaine simulation de placement.
        teamsDTO[1] = null;

        Team team = (player == 1) ? arenaSimulation.getTeam1() : arenaSimulation.getTeam2();
        Team enemyTeam = (player == 1) ? arenaSimulation.getTeam2() : arenaSimulation.getTeam1();
        
        team.canon.loadRobot(team.robots.get(robotIndex));
        team.canon.fire(power, angle);
        

        while(arenaSimulation.isMoving(team) || arenaSimulation.isMoving(enemyTeam)) {
            arenaSimulation.update(GRAVITY_SIMULATION_STEP);
        }

        return enemyTeam.getDTO(); // On retourne l'état de l'équipe adverse après le tir.
    }
    
}
