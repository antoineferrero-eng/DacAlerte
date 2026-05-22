package AlerteServer.service;

import AlerteServer.entity.Departement;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {

    @Autowired
    private DepartementRepository departementRepository;

    public List<Departement> getAll() {
        return departementRepository.findAll();
    }

    public Departement getById(String id) {
        return departementRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Departement not found: " + id));
    }
}