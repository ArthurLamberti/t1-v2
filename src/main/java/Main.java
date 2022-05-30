import config.Configuracao;
import config.LoadConfig;
import simulacao.Simulador;

import static java.util.Objects.isNull;

public class Main {

    public static void main(String[] args) {
        LoadConfig loadConfig = new LoadConfig();
        Configuracao configuracao = loadConfig.carregarConfiguracao();
        if(isNull(configuracao)){
            System.out.println("Erro ao ler arquivo de configuracao");
            return;
        }

        Simulador simulador = new Simulador(configuracao);

        simulador.run();

    }
}
