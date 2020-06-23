package Ia;

import java.util.List;

public class JoueurOffensif extends JoueurIA {

    public JoueurOffensif(Configuration conf) {
        super(conf);
    }

    @Override
    public Coup choisirCoup(int joueur) {
        List<Coup> coups = plateau.getCoupsPossible(joueur);
        Coup meilleurCoup = coups.get(0);
        int score, meilleurScore = Integer.MIN_VALUE;
        
        for (Coup coup : coups) {
            plateau.jouer(coup);
            score = evalPosition(joueur);
            plateau.annuler(coup);
            if(score > meilleurScore) {
                meilleurCoup = coup;
                meilleurScore = score;
            }
        }

        return meilleurCoup;
    }

    public int evaluation(int joueur) {
        if(plateau.aBilleAuBut(joueur))
            return Integer.MAX_VALUE;
        if(plateau.aBilleAuBut(joueur%4+2))
            return Integer.MIN_VALUE;
        if(plateau.aGangeContreJeu(joueur))
            return Integer.MAX_VALUE;
        if(plateau.aGangeContreJeu(joueur%4+2))
            return Integer.MAX_VALUE;

        return evalPosition(joueur);
    }

    public int evalPosition(int joueur) {
        Support[] sups;
        int score, evaluation = 0;
        int[] points = {5, 12, 21, 32, 45, 60, 77};

        if(joueur == Plateau.JOUEUR_A)
            sups = plateau.supportsA;
        else if(joueur == Plateau.JOUEUR_B) {
            sups = plateau.supportsB;

        }
        else
            throw new IllegalArgumentException("Wrong player parameter passeed");
        
        for (Support support : sups) {
            score = (joueur == Plateau.JOUEUR_A) ? points[support.position/7] : points[6-support.position/7]; 
            if(plateau.aBille(support))
                evaluation +=  score * 1.2;
            else
                evaluation += score;
        }

        return evaluation;
    }
}