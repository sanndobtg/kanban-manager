package fr.ubo.kanban.model.mongoDB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "commentaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {

    @Id
    private String id;

    private Long idTache;
    private Long idUtilisateur;
    private String contenu;
    private LocalDateTime dateCreation;
    private List<String> piecesJointesIds = new ArrayList<>();
}