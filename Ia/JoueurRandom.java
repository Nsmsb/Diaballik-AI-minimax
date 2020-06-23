package Ia;

import java.util.List;
import java.util.Random;

public class JoueurRandom extends JoueurIA {

    Random random;

    public JoueurRandom(Configuration conf) {
        super(conf);
        this.random = new Random();
    }

    @Override
    public Coup choisirCoup(int joueur) {
        List<Coup> coups = plateau.getCoupsPossible(joueur);
        return coups.get(random.nextInt(coups.size()));
    }
}