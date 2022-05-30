package simulacao;

import config.ConfiguracaoFila;
import lombok.Data;

import java.util.Arrays;

@Data
public class Fila {
    ConfiguracaoFila configuracoes;

    private int perdas;
    private int qtdNaFila;
    private double[] tempos;
    private boolean filaInfinita;

    public Fila(ConfiguracaoFila configuracaoFila) {
        this.configuracoes = configuracaoFila;
        inicializarTempos();
    }

    public void inicializarTempos() {
        this.tempos = new double[configuracoes.getCapacidade() + 1];
        Arrays.fill(tempos, 0);
    }

    public void chegouNaFila() {
        //contabilizar tempo
        this.qtdNaFila++;
    }

    public void saiuDaFila() {
        this.qtdNaFila--;
    }

    public void adicionarPerda() {
        this.perdas++;
    }

    public void contabilizarTempo(Double tempoEvento) {
        this.tempos[this.qtdNaFila] += tempoEvento;
    }
}
