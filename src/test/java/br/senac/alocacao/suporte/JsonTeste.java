package br.senac.alocacao.suporte;

import br.senac.alocacao.comum.Json;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class JsonTeste {
    private JsonTeste() {
    }

    public static Map<String, Object> mapa(Object... chavesEValores) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < chavesEValores.length; i += 2) {
            m.put((String) chavesEValores[i], chavesEValores[i + 1]);
        }
        return m;
    }

    public static String json(Object valor) {
        if (valor == null) {
            return "null";
        }
        if (valor instanceof Map<?, ?> m) {
            return m.entrySet().stream()
                    .map(e -> Json.texto(String.valueOf(e.getKey())) + ":" + json(e.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        }
        if (valor instanceof Collection<?> c) {
            return c.stream().map(JsonTeste::json).collect(Collectors.joining(",", "[", "]"));
        }
        if (valor instanceof Number || valor instanceof Boolean) {
            return valor.toString();
        }
        return Json.texto(valor.toString());
    }
}
