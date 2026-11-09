package br.senac.alocacao.suporte;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersTeste {
    @Bean
    @ServiceConnection
    @ConditionalOnExpression("'${MONGODB_URI_TESTE:}' == ''")
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
    }

    @Bean
    @Primary
    RelogioTeste relogioTeste() {
        return new RelogioTeste();
    }
}
