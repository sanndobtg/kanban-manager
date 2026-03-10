package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.entities.Tache;
import fr.ubo.kanban.repositories.TacheRepository;
import fr.ubo.kanban.services.TacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TacheServiceImpl implements TacheService {

    private final TacheRepository tacheRepository;

    @Override
    public List<Tache> findByColonneId(Integer colonneId) {
        return tacheRepository.findByColonneId(colonneId);
    }

    @Override
    public Tache findById(Integer id) {
        return tacheRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tâche non trouvée : " + id));
    }

    @Override
    public Tache save(Tache tache) {
        return tacheRepository.save(tache);
    }

    @Override
    public void delete(Integer id) {
        tacheRepository.deleteById(id);
    }
}
