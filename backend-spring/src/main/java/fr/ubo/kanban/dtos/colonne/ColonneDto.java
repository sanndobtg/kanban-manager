package fr.ubo.kanban.dtos.colonne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColonneDto {
    private Long id;
    private String nom;
    private Long idTableau;
}