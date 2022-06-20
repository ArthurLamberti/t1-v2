package simulacao;

import config.ConfiguracaoFila;
import config.Destino;
import config.DestinoCalculado;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Fila {
    ConfiguracaoFila configuracoes;

    private int perdas;
    private int qtdNaFila;
    private double[] tempos;
    private boolean filaInfinita;
    private int maiorQtdNaFila;

    public Fila(ConfiguracaoFila configuracaoFila) {
        this.configuracoes = configuracaoFila;
        configurarDestinos(configuracaoFila);
        inicializarTempos();
    }

    private void configurarDestinos(ConfiguracaoFila configuracaoFila) {
        Double max = 1D;
        Double min = 0D;
        List<DestinoCalculado> destinoCalculados = new ArrayList<>();
        for (Destino destino : configuracaoFila.getDestinos()) {
            max = destino.getProbabilidade() + min;

            destinoCalculados.add(new DestinoCalculado(min, max, destino.getFila()));
            min = max;
        }
        configuracaoFila.setDestinoCalculados(destinoCalculados);
    }
    public void inicializarTempos() {
        if(this.configuracoes.isFilaInfinita()){
            this.tempos = new double[1];
        } else {
            this.tempos = new double[configuracoes.getCapacidade() + 1];
        }
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
        if(this.qtdNaFila >= this.tempos.length){
            double[] temposTemp = new double[this.tempos.length + 1];
            for (int i = 0; i < temposTemp.length; i++) {
                try {
                    temposTemp[i] = this.tempos[i];
                } catch (Exception e) {
                    temposTemp[i] = 0D;
                }
            }
            this.tempos = temposTemp;
        }
        this.tempos[this.qtdNaFila] += tempoEvento;
        if(this.qtdNaFila > this.maiorQtdNaFila) {
            this.maiorQtdNaFila = this.qtdNaFila;
        }
    }
}
