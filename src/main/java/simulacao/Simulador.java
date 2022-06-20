package simulacao;

import config.Configuracao;
import config.ConfiguracaoFila;
import lombok.Data;
import utils.GeradorNumAleatorio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
        temposMedios = new double[this.configuracao.getFilas().size()][15];
        for (int i = 0; i < this.configuracao.getFilas().size(); i++) {
            for (int j = 0; j < 15; j++) {
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
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./resultado.txt"));

            bufferedWriter.append("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
            bufferedWriter.append("│TEMPO TOTAL MEDIO DE EXECUCAO: " + tempoMedioTotalExecucao + "\n");
            bufferedWriter.append("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
            //MEDIA DOS RESULTADOS
            for (int fila = 0; fila < temposMedios.length; fila++) {
                bufferedWriter.append("┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
                ConfiguracaoFila fila1 = this.configuracao.getFilas().get(fila);
                if(fila1.getCapacidade() == 0){
                    bufferedWriter.append("│FILA F" + (fila + 1) + " G/G/" + fila1.getQtdServidores()+"\n");
                } else {
                    bufferedWriter.append("│FILA F" + (fila + 1) + " G/G/" + fila1.getQtdServidores() + "/" + fila1.getCapacidade()+"\n");
                }

                if (Objects.nonNull(fila1.getInicialChegada())) {
                    bufferedWriter.append("│Chegada: " + fila1.getInicialChegada() + "..." + fila1.getFinalChegada()+"\n");
                }
                bufferedWriter.append("│Servico: " + fila1.getInicialServico() + "..." + fila1.getFinalServico()+"\n");
                bufferedWriter.append("│Printando tempo para fila: " + fila1 + 1+"\n");
                printarTempoFila(fila, bufferedWriter);

                bufferedWriter.append("│Quantidade media de perdas: " + perdasMedia[fila] / this.configuracao.getQuantidadeExecucoes() +"\n");

                bufferedWriter.append("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────\n");
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    private void printarTempoFila(int fila, BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.append("│Quantidade de itens na fila\t│\tTempo na fila\t\t\t │\tProbabilidade da fila estar cheia"+"\n");
        for (int tempo1 = 0; tempo1 < temposMedios[fila].length; tempo1++) {
            if (temposMedios[fila][tempo1] != 0D) {

                double media = temposMedios[fila][tempo1] / Double.valueOf(this.configuracao.getQuantidadeExecucoes());
                String tempo = String.format("%.4f", media);
                StringBuilder linha = new StringBuilder("│\t\t\t\t" + tempo1 + "\t\t\t\t│\t" + tempo);
                int baseTab = linha.length();
                String probabilidade = String.format("%.2f", media * 100.0 / this.tempoMedioTotalExecucao);
                linha.append("\t\t\t ").append("\t\t │\t\t").append(probabilidade).append("%"+"\n");
                bufferedWriter.append(linha);
            }
        }
    }


}
