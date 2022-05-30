package simulacao;

import config.Configuracao;
import config.ConfiguracaoFila;
import enums.TipoEvento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Filas {

    private List<Fila> filas;
    private Configuracao configuracao;
    private List<Double> numerosAleatorios;
    private Double tempoGlobal;
    private Integer idEvento;
    private ArrayList<Evento> filaExecucaoEventos;
    private ArrayList<Evento> historicoEventos;
    private Double diferencaTempo;

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
    }

    public ResultadoExecucao run() {
        //CONFIGURAR EVENTO INICIAL
        Evento eventoInicial = new Evento().agendarChegada(idEvento, this.configuracao.getTempoEventoInicial(), this.configuracao.getEventoInicial(), filas.get(0));
        this.historicoEventos.add(eventoInicial);
        this.filaExecucaoEventos.add(eventoInicial);
        //ENQUANTO HOUVER NUMEROS ALEATORIOS, EXECUTA
        try{
            while (!numerosAleatorios.isEmpty()) {
                if(numerosAleatorios.size() % 10000 == 0) {
                    System.out.println(numerosAleatorios.size());
                }
                efetuarEvento();
            }
        } catch (ArrayStoreException e){
            System.out.println(e.getMessage());
        }

        ResultadoExecucao resultadoExecucao = new ResultadoExecucao(filas,tempoGlobal,configuracao);
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
            realizarSaida(evento.getOrigem(),evento.getTempo(),tempoGlobalAntigo);
        } else if (TipoEvento.PASSAGEM.equals(evento.getTipoEvento())) {
            realizarPassagem(evento.getOrigem(), evento.getDestino(),evento.getTempo(),tempoGlobalAntigo);
        }
    }

    private void realizarChegada(Double tempoEvento, Double tempoGlobalAntigo) {
        atualizarTempos(tempoEvento, tempoGlobalAntigo);
        Fila fila1 = this.filas.get(0);
        //CONTABILIZA TEMPOS
        if (fila1.getQtdNaFila() < fila1.getConfiguracoes().getCapacidade()) {
            fila1.chegouNaFila(); //FILA++

            if (fila1.getQtdNaFila() <= fila1.getConfiguracoes().getQtdServidores()) {
                agendarPassagem(this.filas.get(0), this.filas.get(1)); //P12
            }
        } else {
            fila1.adicionarPerda();
        }

        agendarChegada(fila1); //CH1
    }

    private void atualizarTempos(Double tempoEvento, Double tempoGlobalAntigo) {
        for(Fila fila: this.filas) {
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

    private void realizarPassagem(Fila fila1, Fila fila2, Double tempoEvento, Double tempoGlobalAntigo) {
        atualizarTempos(tempoEvento, tempoGlobalAntigo);
        fila1.saiuDaFila();

        if (fila1.getQtdNaFila() >= fila1.getConfiguracoes().getQtdServidores()) {
            agendarPassagem(fila1, fila2); //P12
        }

        if (fila2.getQtdNaFila() < fila2.getConfiguracoes().getCapacidade()) {
            fila2.chegouNaFila();
//
            if(fila2.getQtdNaFila() <= fila2.getConfiguracoes().getQtdServidores()) {
                agendarSaida(fila2, "realizar passagem");
            }
        } else {
            fila2.adicionarPerda();
        }

    }

    private void agendarPassagem(Fila origem, Fila destino) {
        if(this.numerosAleatorios.isEmpty()){
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
        this.historicoEventos.sort(Comparator.comparing(Evento::getTempo));//ordena eventos a serem executados pelo tempo
    }

    private void agendarSaida(Fila origem, String origemSaida) {
        if(this.numerosAleatorios.isEmpty()){
            throw new ArrayStoreException("Acabou os numeros aleatorios");
        }
        qtdSaida++;
        Double tempoEvento = calcularTempo(origem.getConfiguracoes().getInicialServico(), origem.getConfiguracoes().getFinalServico(), this.numerosAleatorios.remove(0), this.tempoGlobal);
        this.idEvento++;
        Evento evento = new Evento(this.idEvento, tempoEvento, TipoEvento.SAIDA, origem, null, origemSaida);//Nao tem fila de destino pq eh saida
        adicionarEventoNaFila(evento);
    }

    private void agendarChegada(Fila destino) {
        if(this.numerosAleatorios.isEmpty()){
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
