package fr.ubo.kanban.model.mongoDB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "pieces_jointes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceJointe {

    @Id
    private String id;

    private String nomFichier;
    private String contentType;
    private long taille;
    private byte[] contenu;          // le fichier brut, max ~16 Mo
    private Long idUtilisateur;
    private LocalDateTime dateUpload;
}