package AlerteServer.dto;

import java.time.LocalDateTime;

public record OtDTO(String numeroOt, LocalDateTime crDebutIntervention, String dkCodeRessource, String dkCodeEmplacement) {}