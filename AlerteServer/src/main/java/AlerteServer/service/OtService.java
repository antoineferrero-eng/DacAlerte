package AlerteServer.service;

import AlerteServer.entity.Ot;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.OtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtService {

    @Autowired
    private OtRepository otRepository;

    public List<Ot> getAll() {
        return otRepository.findAll();
    }

    public Ot getById(String id) {
        return otRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Ot not found: " + id));
    }
}