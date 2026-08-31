package pdg.game.DTO;

import pdg.game.Entity.King;

import java.util.ArrayList;

public record TeamDTO(String name, ArrayList<BlockDTO> blocks, ArrayList<RobotDTO> robots, KingDTO king) {
}
