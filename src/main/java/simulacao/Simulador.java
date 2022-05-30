package simulacao;

import config.Configuracao;
import config.ConfiguracaoFila;
import lombok.Data;
import utils.GeradorNumAleatorio;

import java.util.*;

@Data
public class Simulador {

    Configuracao configuracao;
    List<ResultadoExecucao> resultadoExecucaos;
    List<Double> numerosAleatorios;
    private GeradorNumAleatorio geradorNumAleatorio;
    Filas filas;
    double[][] temposMedios;
    double[] perdasMedia;
    double tempoMedioTotalExecucao;

    public Simulador(Configuracao configuracao) {
        this.configuracao = configuracao;
        geradorNumAleatorio = new GeradorNumAleatorio();
        resultadoExecucaos = new ArrayList<>();
        tempoMedioTotalExecucao = 0;
        perdasMedia = new double[this.configuracao.getFilas().size()];
        temposMedios = new double[this.configuracao.getFilas().size()][this.configuracao.getFilas().get(0).getCapacidade() + 1];
        for (int i = 0; i < this.configuracao.getFilas().size(); i++) {
            for (int j = 0; j < this.configuracao.getFilas().get(i).getCapacidade(); j++) {
                temposMedios[i][j] = 0;
            }
        }

        for (int i = 0; i < this.configuracao.getFilas().size(); i++) {
            perdasMedia[i] = 0;
        }
    }

    public void run() {
        for (int exec = 0; exec < configuracao.getQuantidadeExecucoes(); exec++) {
            //GERAR NUMEROS ALEATORIOS
            numerosAleatorios = geradorNumAleatorio.gerar(configuracao);
            //INICIALIZA FILAS
            filas = new Filas(configuracao, numerosAleatorios);

            ResultadoExecucao resultadoExecucao = filas.run();
            resultadoExecucaos.add(resultadoExecucao);
            calcularMedia(resultadoExecucao);
        }
        tempoMedioTotalExecucao = tempoMedioTotalExecucao / Double.valueOf(this.configuracao.getQuantidadeExecucoes());
        System.out.println("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.println("│TEMPO TOTAL MEDIO DE EXECUCAO: " + tempoMedioTotalExecucao);
        System.out.println("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
        //MEDIA DOS RESULTADOS
        for (int fila = 0; fila < temposMedios.length; fila++) {
            System.out.println("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────");
            ConfiguracaoFila fila1 = this.configuracao.getFilas().get(fila);
            System.out.println("│FILA F" + (fila + 1) + " G/G/" + fila1.getQtdServidores() + "/" + fila1.getCapacidade());
            if (Objects.nonNull(fila1.getInicialChegada())) {
                System.out.println("│Chegada: " + fila1.getInicialChegada() + "..." + fila1.getFinalChegada());
            }
            System.out.println("│Servico: " + fila1.getInicialServico() + "..." + fila1.getFinalServico());
            System.out.println("│Printando tempo para fila: " + fila1 + 1);
            printarTempoFila(fila);

            System.out.println("│Quantidade media de perdas: " + perdasMedia[fila] / this.configuracao.getQuantidadeExecucoes());

            System.out.println("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
        }
    }

    private void calcularMedia(ResultadoExecucao resultadoExecucao) {
        for (int fila = 0; fila < resultadoExecucao.getFilas().size(); fila++) {
            for (int tempo = 0; tempo < resultadoExecucao.getFilas().get(fila).getTempos().length; tempo++) {
                temposMedios[fila][tempo] += resultadoExecucao.getFilas().get(fila).getTempos()[tempo];
            }
            perdasMedia[fila] += resultadoExecucao.getFilas().get(fila).getPerdas();
        }
        tempoMedioTotalExecucao += resultadoExecucao.getTempoGlobal();
    }

    private void printarTempoFila(int fila) {
        System.out.println("│Quantidade de itens na fila\t│\tTempo na fila\t\t\t │\tProbabilidade da fila estar cheia");
        for (int tempo1 = 0; tempo1 < temposMedios[fila].length; tempo1++) {
            double media = temposMedios[fila][tempo1] / Double.valueOf(this.configuracao.getQuantidadeExecucoes());
            String tempo = String.format("%.4f", media);
            StringBuilder linha = new StringBuilder("│\t\t\t\t" + fila + "\t\t\t\t│\t" + tempo);
            int baseTab = linha.length();
            String probabilidade = String.format("%.2f", media * 100.0 / this.tempoMedioTotalExecucao);
            linha.append("\t\t\t ").append("\t\t │\t\t").append(probabilidade).append("%");
            System.out.println(linha);
        }
    }


}
