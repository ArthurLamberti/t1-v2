package config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

public class LoadConfig {

    public Configuracao carregarConfiguracao() {
        InputStream inputStream = getFile();

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.findAndRegisterModules();

            return mapper.readValue(inputStream, Configuracao.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    ;

    private InputStream getFile() {
        return this.getClass().getClassLoader().getResourceAsStream("t1.yml");
    }

}
