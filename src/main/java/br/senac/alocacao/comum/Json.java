package br.senac.alocacao.comum;

import java.util.Map;
import java.util.stream.Collectors;

public final class Json {
    private Json() {
    }

    public static String objeto(Map<String, ?> campos) {
        return campos.entrySet().stream()
                .map(e -> texto(e.getKey()) + ":" + valor(e.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private static String valor(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof Number || v instanceof Boolean) {
            return v.toString();
        }
        return texto(v.toString());
    }

    public static String texto(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }
}
