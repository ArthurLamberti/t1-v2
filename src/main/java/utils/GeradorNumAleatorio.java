package utils;

import config.Configuracao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeradorNumAleatorio {
    Random random;

    public GeradorNumAleatorio (){
        random = new Random();
    }

    public List<Double> gerar(Configuracao configuracao) {
        if (!configuracao.getDeveGerarAleatorio()) {
            return mock();
        }
        List<Double> aleatorios = new ArrayList<>();
        Integer ultimoGerado = configuracao.getSementes().remove(0);

        for (int i = 0; i < configuracao.getQtdAleatorios(); i++) {
            ultimoGerado = getAleatorio(ultimoGerado,
                    configuracao.getMultiplicador(),
                    configuracao.getIncremento(),
                    configuracao.getMod());

            Double ultimoGeradoDouble = getGeradoEntre0E1(ultimoGerado, configuracao.getMod());
            aleatorios.add(ultimoGeradoDouble);
        }
        return aleatorios;
    }

    public Double numAleatorio() {
        return random.nextDouble();
    }


    private List<Double> mock() {
        List<Double> mock = new ArrayList<>();
        mock.add(0.9921D);
        mock.add(0.0004D);
        mock.add(0.5534D);
        mock.add(0.2761D);
        mock.add(0.3398D);
        mock.add(0.8963D);
        mock.add(0.9023D);
        mock.add(0.0132D);
        mock.add(0.4569D);
        mock.add(0.5121D);
        mock.add(0.9208D);
        mock.add(0.0171D);
        mock.add(0.2299D);
        mock.add(0.8545D);
        mock.add(0.6001D);
        mock.add(0.2921D);
        return mock;
    }

    private Double getGeradoEntre0E1(Integer ultimoGerado, Integer mod) {
        return ultimoGerado / Double.valueOf(mod);
    }

    private Integer getAleatorio(Integer ultimoGerado, Integer multiplicador, Integer incremento, Integer mod) {
        return (ultimoGerado * multiplicador + incremento) % mod;
    }

}
