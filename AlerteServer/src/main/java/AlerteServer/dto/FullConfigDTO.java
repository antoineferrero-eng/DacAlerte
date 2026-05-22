package AlerteServer.dto;

import java.util.List;

public record FullConfigDTO(
    List<String> activeLevels,
    List<String> activeTypes,
    String mailTime,
    String updateTime
) {}
