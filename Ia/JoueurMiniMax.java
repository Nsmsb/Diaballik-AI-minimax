package Ia;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class JoueurMiniMax extends JoueurOffensif {
    
    private int profondeur;

    public JoueurMiniMax(Configuration conf, int profondeur) {
        super(conf);
        this.profondeur = profondeur;
    }

    @Override
    public Coup choisirCoup(int joueur) {

        List<Coup> coups = getCoups(joueur);
        Coup meilleurCoup = coups.get(new Random().nextInt(coups.size()));
        int score, scoreMeilleurCoup = Integer.MIN_VALUE;

        for (Coup coup : coups) {
            plateau.jouer(coup);
            score = miniMax(profondeur, Integer.MAX_VALUE, Integer.MIN_VALUE, true, joueur);
            plateau.annuler(coup);
            if(scoreMeilleurCoup < score) {
                scoreMeilleurCoup = score;
                meilleurCoup = coup.copier();
            }
        }

        return meilleurCoup;
    }

    public int miniMax(int profondeur, int alpha, int beta, boolean estMax, int joueur) {
        List<Coup> coups;

        int meilleurScore, scoreCoup;

        if(profondeur == 0 || estFin())
            return evaluation(joueur);

        // coups = plateau.getCoupsPossible(joueur);
        coups = getCoups(joueur);


        if (estMax) {
            meilleurScore = Integer.MIN_VALUE;

            for (Coup coup : coups) {

                plateau.jouer(coup);
                scoreCoup = miniMax(profondeur-1, alpha, beta, !estMax, joueur%4+2);
                plateau.annuler(coup);

                meilleurScore = Integer.max(meilleurScore, scoreCoup);
                alpha = Integer.max(alpha, meilleurScore);

                if(beta <= alpha) 
                    break;
            }

            return meilleurScore;
            

        } else {
            meilleurScore = Integer.MAX_VALUE;

            for (Coup coup : coups) {

                plateau.jouer(coup);
                scoreCoup = miniMax(profondeur-1, alpha, beta, !estMax, joueur%4+2);
                plateau.annuler(coup);

                meilleurScore = Integer.min(meilleurScore, scoreCoup);
                alpha = Integer.min(alpha, meilleurScore);

                if(beta <= alpha)
                    break;
            }
            

            return meilleurScore;



        }
        
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

        // return evalPosition(joueur) - evalPosition(joueur%4+2);
        return evalPosition(joueur) - evalPosition(joueur%4+2) + evalAttaque(joueur) - evalAttaque(joueur%4+2);
    }

    public int evalAttaque(int joueur) {
        int jg, jd, ii, i, j, dep, iPass, jPass, score = 0;
        Support[] supports;
        Support supBille = null;

        supports = joueur==Plateau.JOUEUR_A ? plateau.supportsA : plateau.supportsB;
        for (Support support : supports) {
            if(plateau.aBille(support)) {
                supBille = support;
                break;
            }    
        }

        i = supBille.position/7;
        j = supBille.position%7;

        jd = joueur==Plateau.JOUEUR_A ? j+i : j+(6-i);
        jg = joueur==Plateau.JOUEUR_A ? j-i : j-(6-i);
        ii = joueur==Plateau.JOUEUR_A ? 6 : 0;
        dep = joueur==Plateau.JOUEUR_A ? 1 : -1;

        // vertical passe
        iPass = i + dep;
        System.out.println(ii + " ajajaja " + iPass);

        if(estBilleAcc(ii, j, dep, joueur)) {
            while(!plateau.estAdversaire(iPass, j, joueur) && iPass < 6 && iPass > 0)
            iPass += dep;
        }
        if(iPass == ii)
            score += 200;
        System.out.println(ii + " ajajaja " + iPass);

        // left pass
        iPass = i + dep;
        jPass = j - 1;
        if(estBilleAcc(ii, jg, dep, joueur)) {
            while(!plateau.estAdversaire(iPass, jPass, joueur) && iPass < 6 && iPass > 0 && jPass < 6 && jPass > 0 ) {
                iPass += dep;
                jPass--;
            }
        }
        if(iPass == ii && jPass == jg)
            score += 200;

         // right pass
         iPass = i + dep;
         jPass = j + 1;
         if(estBilleAcc(ii, jd, dep, joueur)) {
             while(!plateau.estAdversaire(iPass, jPass, joueur) && iPass < 6 && iPass > 0 && jPass < 6 && jPass > 0 ) {
                 iPass += dep;
                 jPass++;
             }
         }
         if(iPass == ii && jPass == jd)
             score += 200;

        return score;

    }

    public boolean estFin() {
        return (
            plateau.aBilleAuBut(Plateau.JOUEUR_A) || plateau.aBilleAuBut(Plateau.JOUEUR_B) ||
            plateau.aGangeContreJeu(Plateau.JOUEUR_A) || plateau.aGangeContreJeu(Plateau.JOUEUR_B)
        );
    }

    private List<Coup> getCoups(int joueur) {
        List<Coup> coups = plateau.getCoupsPossible(joueur);
        Collections.sort(coups, new Comparator<Coup>() {
            @Override
            public int compare(Coup c1, Coup c2) {
                return evaluerCoup(joueur, c2) - evaluerCoup(joueur, c1);
            }
        });

        return coups;

    }

    private int evaluerCoup(int joueur, Coup coup) {
        int score = 0;
        int[] points = {5, 12, 21, 32, 45, 60, 77};
        for (Action action : coup.actions) {
            score += joueur==Plateau.JOUEUR_A ? points[action.posNouveau/7]-points[action.pos/7] : 
            points[6-action.posNouveau/7]-points[6-action.pos/7];
        }
        return score;
    }

    private boolean estBilleAcc(int i, int j, int dep, int joueur) {

        boolean accessible = plateau.estJoueur(i, j, joueur) || ( plateau.estVide(i,j) && (
            plateau.estJoueur(i-dep,j, joueur) ||
            plateau.estJoueur(i,j+1, joueur) ||
            plateau.estJoueur(i,j-1, joueur) ||
            plateau.estJoueur(i-dep,j+1, joueur) ||
            plateau.estJoueur(i-dep,j-1, joueur)
            )
        );

        return accessible;
    }


}