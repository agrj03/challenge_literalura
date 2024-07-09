package com.alura.Literalura.model;

public enum Idioma {

    ESPANOL("es"),
    INGLES("en"),
    FRANCES("fr");

    private String IdiomaEsp;

    Idioma(String language){
        this.IdiomaEsp = language;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.IdiomaEsp.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ninguna Idioma encontrada: " + text);
    }

    public static Idioma fromEspanol(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.IdiomaEsp.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
