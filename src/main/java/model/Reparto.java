package model;

import java.util.ArrayList;
import java.util.List;

public class Reparto {
    private String nome;
    private List<Stanza> stanze;
    private List<Medico> medici;

    public Reparto(String nome){
        this.nome = nome;
        this.stanze = new ArrayList<>();
        this.medici = new ArrayList<>();
    }
}
