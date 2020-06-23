package Ia;

abstract public class JoueurIA {

    Plateau plateau;

    public JoueurIA(Configuration conf) {
        this.plateau = new Plateau(conf);
    }

    public void updatePlateau(Configuration conf) {
        this.plateau = new Plateau(conf);
    }

    abstract public Coup choisirCoup(int joueur);

}