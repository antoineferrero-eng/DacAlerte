package AlerteServer.service;

import AlerteServer.entity.Alerte;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.AlerteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlerteService {

    @Autowired
    private AlerteRepository alerteRepository;

    public List<Alerte> getAll() {
        return alerteRepository.findAll();
    }

    public Alerte getById(int id) {
        return alerteRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Alerte not found: " + id));
    }
}