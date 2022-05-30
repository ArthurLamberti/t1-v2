package simulacao;

import enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Evento {
    private int id; //Pra ter controle dos eventos
    private Double tempo;
    private TipoEvento tipoEvento;
    private Fila origem;
    private Fila destino;

    private String origemSaida;

    public Evento agendarChegada(int id, Double tempo, TipoEvento evento, Fila fila){
        if(evento.equals(TipoEvento.CHEGADA)){
            this.id = id;
            this.tempo = tempo;
            this.tipoEvento = evento;
            this.origem = fila;
        }
        return this;
    }

    public Evento agendarSaida(int id, Double tempo, TipoEvento evento, Fila fila){
        if(evento.equals(TipoEvento.SAIDA)){
            this.id = id;
            this.tempo = tempo;
            this.tipoEvento = evento;
            this.origem = fila;
        }
        return this;
    }

    public Evento agendarPassagem(int id, Double tempo, TipoEvento evento, Fila origem, Fila destino) {
        if(evento.equals(TipoEvento.PASSAGEM)){
            this.id = id;
            this.tempo = tempo;
            this.tipoEvento = evento;
            this.origem = origem;
            this.destino = destino;
        }
        return this;
    }
}
