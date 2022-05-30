package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfiguracaoFila {

    private String nome;

    private int qtdServidores;

    private int capacidade;

    private Double inicialChegada;

    private Double finalChegada;

    private Double inicialServico;

    private Double finalServico;


}
