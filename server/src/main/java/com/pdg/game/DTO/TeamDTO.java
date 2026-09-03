package com.pdg.game.DTO;

import java.util.ArrayList;

public record TeamDTO(
    String name, ArrayList<BlockDTO> blocks, ArrayList<RobotDTO> robots, KingDTO king) {}
