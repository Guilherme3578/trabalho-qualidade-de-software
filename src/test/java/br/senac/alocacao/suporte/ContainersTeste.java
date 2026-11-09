package br.senac.alocacao.suporte;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersTeste {
    private static final String BANCO = "alocacao_teste";
    private static MongoDBContainer container;

    @Bean
    @Primary
    RelogioTeste relogioTeste() {
        return new RelogioTeste();
    }

    static synchronized String uriMongo() {
        String externo = System.getenv("MONGODB_URI_TESTE");
        if (externo != null && !externo.isBlank()) {
            return externo;
        }
        if (container == null) {
            MongoDBContainer novo = new MongoDBContainer(DockerImageName.parse("mongo:9.0")).withReplicaSet();
            novo.start();
            container = novo;
        }
        if (!container.isRunning()) {
            throw new IllegalStateException("O container do MongoDB parou. Logs:\n" + container.getLogs());
        }
        return container.getReplicaSetUrl(BANCO);
    }
}
