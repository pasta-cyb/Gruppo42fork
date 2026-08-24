package model;

import java.util.ArrayList;
import java.util.List;

public class Stanza {
    private int numeroStanza;
    private List<Letto> letti;

    public Stanza (int numeroStanza){
        this.numeroStanza = numeroStanza;
        this.letti = new ArrayList<>();
    }
}
