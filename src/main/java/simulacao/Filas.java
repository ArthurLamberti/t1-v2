package simulacao;

import config.Configuracao;
import config.ConfiguracaoFila;
import config.Destino;
import config.DestinoCalculado;
import enums.TipoEvento;
import utils.GeradorNumAleatorio;

import java.util.*;

public class Filas {

    private List<Fila> filas;
    private Configuracao configuracao;
    private List<Double> numerosAleatorios;
    private Double tempoGlobal;
    private Integer idEvento;
    private ArrayList<Evento> filaExecucaoEventos;
    private ArrayList<Evento> historicoEventos;
    private Double diferencaTempo;
    private GeradorNumAleatorio geradorNumAleatorio;

    private int qtdSaida, qtdChegada, qtdPassagem;

    public Filas(Configuracao configuracao, List<Double> numerosAleatorios) {
        this.configuracao = configuracao;
        this.numerosAleatorios = numerosAleatorios;
        this.tempoGlobal = 0D;
        this.idEvento = 0;
        filas = new ArrayList<>();
        this.filaExecucaoEventos = new ArrayList<>();
        this.historicoEventos = new ArrayList<>();
        for (ConfiguracaoFila configuracaoFila : configuracao.getFilas()) {
            Fila fila = new Fila(configuracaoFila);
            filas.add(fila);
        }
        geradorNumAleatorio = new GeradorNumAleatorio();
    }

    public ResultadoExecucao run() {
        //CONFIGURAR EVENTO INICIAL
        Evento eventoInicial = new Evento().agendarChegada(idEvento, this.configuracao.getTempoEventoInicial(), this.configuracao.getEventoInicial(), filas.get(0));
        this.historicoEventos.add(eventoInicial);
        this.filaExecucaoEventos.add(eventoInicial);
        //ENQUANTO HOUVER NUMEROS ALEATORIOS, EXECUTA
        try {
            while (!numerosAleatorios.isEmpty()) {
                if (numerosAleatorios.size() % 10000 == 0) {
                    System.out.println(numerosAleatorios.size());
                }
                efetuarEvento();
            }
        } catch (ArrayStoreException e) {
            System.out.println(e.getMessage());
        }

        ResultadoExecucao resultadoExecucao = new ResultadoExecucao(filas, tempoGlobal, configuracao);
        resultadoExecucao.printarResultados();
        return resultadoExecucao;
    }

    private void efetuarEvento() {
        this.filaExecucaoEventos.sort(Comparator.comparing(Evento::getTempo)); //pra garantir que ta ordenado antes de pegar
        Evento evento = filaExecucaoEventos.remove(0); //pega o proximo evento a ser executado
        //TODO CALCULAR TEMPO
        Double tempoGlobalAntigo = this.tempoGlobal;
//        this.diferencaTempo = evento.getTempo() - tempoGlobal;
        this.tempoGlobal = evento.getTempo();
        //CONTABILIZAR TEMPO NAS FILAS
        if (TipoEvento.CHEGADA.equals(evento.getTipoEvento())) {
            realizarChegada(evento.getTempo(), tempoGlobalAntigo); //tempo evento antigo,
        } else if (TipoEvento.SAIDA.equals(evento.getTipoEvento())) {
            realizarSaida(evento.getOrigem(), evento.getTempo(), tempoGlobalAntigo);
        } else if (TipoEvento.PASSAGEM.equals(evento.getTipoEvento())) {
            realizarPassagem(evento.getOrigem(), evento.getDestino(), evento.getTempo(), tempoGlobalAntigo);
        }
    }

    private void realizarChegada(Double tempoEvento, Double tempoGlobalAntigo) { //TODO ta chegando apenas na posicao 0 e 1, investigar
        atualizarTempos(tempoEvento, tempoGlobalAntigo);
        Fila fila1 = this.filas.get(0);
        //CONTABILIZA TEMPOS
        if (fila1.getConfiguracoes().isFilaInfinita() || fila1.getQtdNaFila() < fila1.getConfiguracoes().getCapacidade()) {
            fila1.chegouNaFila(); //FILA++
            //TODO se for infinitio, nao verificar os servidores
            if (fila1.getQtdNaFila() <= fila1.getConfiguracoes().getQtdServidores()) {
                Fila filaDestino = buscarFilaDestino(fila1);
                agendarPassagem(fila1, filaDestino); //P12
            }
        } else {
            fila1.adicionarPerda();
        }

        agendarChegada(fila1); //CH1
    }

    private Destino sortearDestino(Fila fila1) {
        List<DestinoCalculado> destinoCalculados = fila1.getConfiguracoes().getDestinoCalculados();
        Double prob = geradorNumAleatorio.numAleatorio();
        Optional<DestinoCalculado> destino = destinoCalculados.stream().filter(dest ->
                dest.getMinProbabilidade() <= prob && dest.getMaxProbabilidade() > prob
        ).findFirst();
        if (destino.isPresent()) {
            return fila1.getConfiguracoes().getDestinos().stream().filter(dest -> dest.getFila().equals(destino.get().getFila())).findFirst().get();
        }
        return null;
    }

    private void atualizarTempos(Double tempoEvento, Double tempoGlobalAntigo) {
        for (Fila fila : this.filas) {
            fila.contabilizarTempo(tempoEvento - tempoGlobalAntigo);
        }
    }

    private void realizarSaida(Fila fila, Double tempoEvento, Double tempoGlobalAntigo) {
        atualizarTempos(tempoEvento, tempoGlobalAntigo);
        fila.saiuDaFila();
        if (fila.getQtdNaFila() >= fila.getConfiguracoes().getQtdServidores()) {
            agendarSaida(fila, "Realizar saida");
        }
    }

    private void realizarPassagem(Fila origem, Fila destino, Double tempoEvento, Double tempoGlobalAntigo) {
        atualizarTempos(tempoEvento, tempoGlobalAntigo);
        origem.saiuDaFila();
        Fila filaDestino;

        //TODO mudar logica pra sortear fila destino
        if (origem.getQtdNaFila() >= origem.getConfiguracoes().getQtdServidores()) {
            filaDestino = buscarFilaDestino(origem);
            if (Objects.isNull(filaDestino)) {
                agendarSaida(origem, "origem passagem");
            } else {
                agendarPassagem(origem, filaDestino); //P12
            }
        }
        //TODO VERIFICAR QUANDO F1 EH DESTINO
        if (destino.getConfiguracoes().isFilaInfinita() || destino.getQtdNaFila() < destino.getConfiguracoes().getCapacidade()) {
            destino.chegouNaFila();
//
            if (destino.getQtdNaFila() <= destino.getConfiguracoes().getQtdServidores()) {
                filaDestino = buscarFilaDestino(destino);
                if (Objects.isNull(filaDestino)) {
                    agendarSaida(destino, "destino passagem");
                } else {
                    agendarPassagem(destino, filaDestino); //P12
                }
            }
        } else {
            destino.adicionarPerda();
        }

    }

    private Fila buscarFilaDestino(Fila origem) {
        Destino novoDestino = sortearDestino(origem);
        Fila fila = null;
        if (Objects.nonNull(novoDestino)) {
            fila = this.filas.stream().filter(f -> f.getConfiguracoes().getNome().equals(novoDestino.getFila())).findFirst().get();
        }
        return fila;
    }

    private void agendarPassagem(Fila origem, Fila destino) {
        if (this.numerosAleatorios.isEmpty()) {
            throw new ArrayStoreException("Acabou os numeros aleatorios");
        }
        qtdPassagem++;
        Double tempoEvento = calcularTempo(origem.getConfiguracoes().getInicialServico(), origem.getConfiguracoes().getFinalServico(), this.numerosAleatorios.remove(0), this.tempoGlobal);
        this.idEvento++;
        Evento evento = new Evento(this.idEvento, tempoEvento, TipoEvento.PASSAGEM, origem, destino, null);
        adicionarEventoNaFila(evento);
    }

    private void adicionarEventoNaFila(Evento evento) {
        this.filaExecucaoEventos.add(evento);
        this.filaExecucaoEventos.sort(Comparator.comparing(Evento::getTempo));
        this.historicoEventos.add(evento);
//        this.historicoEventos.sort(Comparator.comparing(Evento::getTempo));//ordena eventos a serem executados pelo tempo
    }

    private void agendarSaida(Fila origem, String origemSaida) {
        if (this.numerosAleatorios.isEmpty()) {
            throw new ArrayStoreException("Acabou os numeros aleatorios");
        }
        qtdSaida++;
        Double tempoEvento = calcularTempo(origem.getConfiguracoes().getInicialServico(), origem.getConfiguracoes().getFinalServico(), this.numerosAleatorios.remove(0), this.tempoGlobal);
        this.idEvento++;
        Evento evento = new Evento(this.idEvento, tempoEvento, TipoEvento.SAIDA, origem, null, origemSaida);//Nao tem fila de destino pq eh saida
        adicionarEventoNaFila(evento);
    }

    private void agendarChegada(Fila destino) {
        if (this.numerosAleatorios.isEmpty()) {
            throw new ArrayStoreException("Acabou os numeros aleatorios");
        }
        qtdChegada++;
        Double tempoEvento = calcularTempo(destino.getConfiguracoes().getInicialChegada(), destino.getConfiguracoes().getFinalChegada(), this.numerosAleatorios.remove(0), this.tempoGlobal);
        this.idEvento++;
        Evento evento = new Evento(this.idEvento, tempoEvento, TipoEvento.CHEGADA, destino, null, null);//Nao tem fila de destino pq eh saida
        adicionarEventoNaFila(evento);
    }

    private Double calcularTempo(Double minValue, Double maxValue, Double randomNum, Double time) {
        Double timeCalculated = (minValue + ((maxValue - minValue) * randomNum));
        return timeCalculated + time;
    }

}
