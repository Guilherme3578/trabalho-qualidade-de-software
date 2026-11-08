package br.senac.alocacao.suporte;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Ambiente realista: MongoDB 8 real em container (replica set de um nó, necessário para transações).
 * O @ServiceConnection injeta a URI do container na aplicação automaticamente.
 */
@TestConfiguration(proxyBeanMethods = false)
public class ContainersTeste {

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
    }

    @Bean
    @Primary
    RelogioTeste relogioTeste() {
        return new RelogioTeste();
    }
}
