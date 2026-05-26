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

    public void createFakeOtsForToday() {
        List<Ot> existingOts = otRepository.findAll();
        java.util.Set<String> pairs = new java.util.HashSet<>();
        
        for (Ot ot : existingOts) {
            if (ot.getRessource() != null && ot.getEmplacement() != null) {
                String pairKey = ot.getRessource().getDkCode() + "-" + ot.getEmplacement().getDkCode();
                if (!pairs.contains(pairKey)) {
                    pairs.add(pairKey);
                    Ot fakeOt = new Ot();
                    fakeOt.setNumeroOt("FAKE-" + java.util.UUID.randomUUID().toString().substring(0, 8));
                    fakeOt.setCrDebutIntervention(java.time.LocalDateTime.now());
                    fakeOt.setRessource(ot.getRessource());
                    fakeOt.setEmplacement(ot.getEmplacement());
                    otRepository.save(fakeOt);
                }
            }
        }
    }
}