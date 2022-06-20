package config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DestinoCalculado {

    private Double minProbabilidade;
    private Double maxProbabilidade;
    private String fila;
}
