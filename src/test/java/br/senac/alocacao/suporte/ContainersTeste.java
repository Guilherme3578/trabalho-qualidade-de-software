package br.senac.alocacao.suporte;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersTeste {
    private static final String BANCO = "alocacao_teste";
    private static MySQLContainer container;

    @Bean
    @Primary
    RelogioTeste relogioTeste() {
        return new RelogioTeste();
    }

    record Conexao(String url, String usuario, String senha) {
    }

    static synchronized Conexao conexao() {
        String externo = System.getenv("MYSQL_URL_TESTE");
        if (externo != null && !externo.isBlank()) {
            return new Conexao(externo, variavel("MYSQL_USUARIO_TESTE", "alocacao"),
                    variavel("MYSQL_SENHA_TESTE", "alocacao"));
        }
        if (container == null) {
            MySQLContainer novo = new MySQLContainer(DockerImageName.parse("mysql:8.4"))
                    .withDatabaseName(BANCO)
                    .withUsername("alocacao")
                    .withPassword("alocacao");
            novo.start();
            container = novo;
        }
        if (!container.isRunning()) {
            throw new IllegalStateException("O container do MySQL parou. Logs:\n" + container.getLogs());
        }
        return new Conexao(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    private static String variavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
