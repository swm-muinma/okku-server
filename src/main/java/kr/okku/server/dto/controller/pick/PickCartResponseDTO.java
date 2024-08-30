package kr.okku.server.dto.controller.pick;

import lombok.Data;

@Data
public class PickCartResponseDTO {
    private String name;
    private PickCartHostResponseDTO host;

    // Getters and Setters
}
