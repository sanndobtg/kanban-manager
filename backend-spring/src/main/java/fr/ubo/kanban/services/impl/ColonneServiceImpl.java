package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.entities.Colonne;
import fr.ubo.kanban.repositories.ColonneRepository;
import fr.ubo.kanban.services.ColonneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColonneServiceImpl implements ColonneService {

    private final ColonneRepository colonneRepository;

    @Override
    public List<Colonne> findByTableauId(Integer tableauId) {
        return colonneRepository.findByTableauId(tableauId);
    }

    @Override
    public Colonne findById(Integer id) {
        return colonneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colonne non trouvée : " + id));
    }

    @Override
    public Colonne save(Colonne colonne) {
        return colonneRepository.save(colonne);
    }

    @Override
    public void delete(Integer id) {
        colonneRepository.deleteById(id);
    }
}