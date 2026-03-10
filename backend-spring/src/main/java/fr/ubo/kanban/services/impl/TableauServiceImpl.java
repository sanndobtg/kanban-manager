package fr.ubo.kanban.services.impl;

import fr.ubo.kanban.common.exception.NotFoundException;
import fr.ubo.kanban.entities.Tableau;
import fr.ubo.kanban.repositories.TableauRepository;
import fr.ubo.kanban.services.TableauService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableauServiceImpl implements TableauService {

    private final TableauRepository tableauRepository;

    @Override
    public List<Tableau> findAll() {
        return tableauRepository.findAll();
    }

    @Override
    public Tableau findById(Integer id) {
        return tableauRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tableau non trouvé : " + id));
    }

    @Override
    public Tableau save(Tableau tableau) {
        return tableauRepository.save(tableau);
    }

    @Override
    public void delete(Integer id) {
        tableauRepository.deleteById(id);
    }
}