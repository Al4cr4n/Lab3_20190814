package com.example.lab3;

import java.util.List;

public class Question {
    private String texto;
    private String correcta;
    private List<String> opciones;

    public Question(String texto, String correcta, List<String> opciones) {
        this.texto = texto;
        this.correcta = correcta;
        this.opciones = opciones;
    }

    public String getTexto() { return texto; }
    public String getCorrecta() { return correcta; }
    public List<String> getOpciones() { return opciones; }
}
