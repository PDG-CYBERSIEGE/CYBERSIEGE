package com.pdg.logic.DTO;

//import com.pdg.logic.Entity.King;

import java.util.ArrayList;

public record TeamDTO(String name, ArrayList<BlockDTO> blocks, ArrayList<RobotDTO> robots, KingDTO king) {
}
