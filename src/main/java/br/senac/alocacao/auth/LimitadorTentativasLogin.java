package br.senac.alocacao.auth;

import br.senac.alocacao.comum.CodigoErro;
import br.senac.alocacao.comum.NegocioException;
import br.senac.alocacao.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class LimitadorTentativasLogin {
    private record Janela(int falhas, Instant inicio) {
    }

    private final Map<String, Janela> tentativas = new ConcurrentHashMap<>();
    private final int maximo;
    private final Duration duracao;
    private final Supplier<Instant> relogio;

    @Autowired
    public LimitadorTentativasLogin(AppProperties props) {
        this(props.login().maxTentativas(), props.login().janelaBloqueio(), Instant::now);
    }

    LimitadorTentativasLogin(int maximo, Duration duracao, Supplier<Instant> relogio) {
        this.maximo = maximo;
        this.duracao = duracao;
        this.relogio = relogio;
    }

    public void verificar(String email) {
        Janela j = tentativas.get(chave(email));
        if (j != null && !expirada(j) && j.falhas() >= maximo) {
            throw new NegocioException(HttpStatus.TOO_MANY_REQUESTS, CodigoErro.MUITAS_TENTATIVAS,
                    "Muitas tentativas de login sem sucesso. Aguarde alguns minutos e tente novamente.");
        }
    }

    public void registrarFalha(String email) {
        tentativas.compute(chave(email), (k, j) -> j == null || expirada(j)
                ? new Janela(1, relogio.get())
                : new Janela(j.falhas() + 1, j.inicio()));
    }

    public void limpar(String email) {
        tentativas.remove(chave(email));
    }

    private boolean expirada(Janela j) {
        return relogio.get().isAfter(j.inicio().plus(duracao));
    }

    private static String chave(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
