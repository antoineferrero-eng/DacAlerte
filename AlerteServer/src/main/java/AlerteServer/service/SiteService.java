package AlerteServer.service;

import AlerteServer.entity.Site;
import AlerteServer.exception.IdNotFoundException;
import AlerteServer.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    public List<Site> getAll() {
        return siteRepository.findAll();
    }

    public Site getById(String id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Site not found: " + id));
    }
}