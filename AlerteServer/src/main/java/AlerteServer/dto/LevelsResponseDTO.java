package AlerteServer.dto;

import java.util.List;

public record LevelsResponseDTO(String status, List<String> activeLevels) {}
