package config;

import enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Configuracao {

    List<ConfiguracaoFila> filas;
    Double tempoInicial;
    Integer quantidadeExecucoes;
    List<Integer> sementes;
    TipoEvento eventoInicial;
    Double tempoEventoInicial;
    Integer multiplicador;
    Integer incremento;
    Integer mod;
    Integer qtdAleatorios;
    Boolean deveGerarAleatorio;
}
