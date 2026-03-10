package fr.ubo.kanban.services;

import fr.ubo.kanban.entities.Tableau;
import java.util.List;

public interface TableauService {
    List<Tableau> findAll();
    Tableau findById(Integer id);
    Tableau save(Tableau tableau);
    void delete(Integer id);
}