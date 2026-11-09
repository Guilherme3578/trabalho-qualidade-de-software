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
            container = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
            container.start();
        }
        return container.getReplicaSetUrl(BANCO);
    }
}
