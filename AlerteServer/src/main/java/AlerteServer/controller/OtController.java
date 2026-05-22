package AlerteServer.controller;

import AlerteServer.dto.OtDTO;
import AlerteServer.entity.Ot;
import AlerteServer.service.OtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ordre-de-travails")
public class OtController {

    @Autowired
    private OtService otService;

    @GetMapping
    public List<OtDTO> getAll() {
        return otService.getAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public OtDTO getById(@PathVariable String id) {
        return mapToDTO(otService.getById(id));
    }

    private OtDTO mapToDTO(Ot ot) {
        String ressourceId = (ot.getRessource() != null) ? ot.getRessource().getDkCode() : null;
        String emplacementId = (ot.getEmplacement() != null) ? ot.getEmplacement().getDkCode() : null;
        return new OtDTO(ot.getNumeroOt(), ot.getCrDebutIntervention(), ressourceId, emplacementId);
    }
}