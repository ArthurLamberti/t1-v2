package simulacao;

import config.Configuracao;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class ResultadoExecucao {
    private List<Fila> filas;
    private Double tempoGlobal;
    private Configuracao configuracao;

    public void printarResultados() {
        System.out.println("┌────────────────────────────────────────────────────────────────────────────────");
        System.out.println("│Tempo total de execucao: " + tempoGlobal + "\t│");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────\n");

        for (int i = 0; i < filas.size(); i++) {
            System.out.println("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
            System.out.println("│FILA F" + (i + 1) + " G/G/" + filas.get(i).getConfiguracoes().getQtdServidores() + "/" + filas.get(i).getConfiguracoes().getCapacidade());
            if (Objects.nonNull(filas.get(i).getConfiguracoes().getInicialChegada())) {
                System.out.println("│Chegada: " + filas.get(i).getConfiguracoes().getInicialChegada() + "..." + filas.get(i).getConfiguracoes().getFinalChegada());
            }
            System.out.println("│Servico: " + filas.get(i).getConfiguracoes().getInicialServico() + "..." + filas.get(i).getConfiguracoes().getFinalServico());
            System.out.println("│Printando tempo para fila: " + i);
            printarTempoFila(filas.get(i));

            System.out.println("│Quantidade de perdas: " + filas.get(i).getPerdas());

            System.out.println("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
        }
    }

    private void printarTempoFila(Fila fila) {
        int baseTexto = "│Quantidade de itens na fila\t│\tTempo na fila\t\t\t".length();
        System.out.println("│Quantidade de itens na fila\t│\tTempo na fila\t\t\t │\tProbabilidade da fila estar cheia");
        for (int i = 0; i < fila.getTempos().length; i++) {
            String tempo = String.format("%.4f", fila.getTempos()[i]);
            StringBuilder linha = new StringBuilder("│\t\t\t\t" + i + "\t\t\t\t│\t" + tempo);
            int baseTab = linha.length();
            String probabilidade = String.format("%.2f", fila.getTempos()[i] * 100.0 / tempoGlobal);
            linha.append("\t\t\t ").append("\t\t │\t\t").append(probabilidade).append("%");
            System.out.println(linha);
        }
    }
}
