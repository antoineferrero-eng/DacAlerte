package AlerteServer.controller;

import AlerteServer.dto.DepartementDTO;
import AlerteServer.entity.Departement;
import AlerteServer.service.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departements")
public class DepartementController {

    @Autowired
    private DepartementService departementService;

    @GetMapping
    public List<DepartementDTO> getAll() {
        return departementService.getAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public DepartementDTO getById(@PathVariable String id) {
        return mapToDTO(departementService.getById(id));
    }

    private DepartementDTO mapToDTO(Departement departement) {
        return new DepartementDTO(
                departement.getNum(),
                departement.getLat(),
                departement.getLongitude()
        );
    }
}