package br.senac.alocacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class MongoConfig {
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory fabrica) {
        return new MongoTransactionManager(fabrica);
    }

    @Bean
    public TransactionTemplate transactionTemplate(MongoTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
