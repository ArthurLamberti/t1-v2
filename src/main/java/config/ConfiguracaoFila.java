package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private List<Destino> destinos;

    private boolean filaInfinita;

    private List<DestinoCalculado> destinoCalculados;

}
