package Ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestJeu {

    static Plateau p;
    
    static public void afficherPlateau() {
        for (int i = 0; i < 7; i++) {
            System.out.print("\t");
            for (int j = 0; j < 7; j++) {
                if(p.estVide(i, j))
                    System.out.print(" . ");
                else if(p.estJoueur(i, j, Plateau.JOUEUR_A)) {
                    if(p.aBille(p.getSupport(i*7+j)))
                        System.out.print(" * ");
                    else
                        System.out.print(" O ");
                } else {
                    if(p.aBille(p.getSupport(i*7+j)))
                        System.out.print(" + ");
                    else
                        System.out.print(" X ");
                }
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {

        // Plateau p;
        JoueurIA joueur, joueur2;
        int in1, in2;
        boolean ia = false;

        int[] a = {0, 1, 2, 3, 4, 5, 6, 3};
        int[] b = {7*6+0, 7*6+1, 7*6+2, 7*6+3, 7*6+4, 7*6+5, 7*6+6, 3};


        Scanner scanner = new Scanner(System.in);

        p = new Plateau(new Configuration(a, b));

        System.out.println("modes:");
        System.out.println("  1> IA -- IA");
        System.out.println("  2> HU -- IA");
        System.out.print("\nEntrer le mode: ");
        
        joueur2 = new JoueurRandom(new Configuration(a, b));
        in1 = scanner.nextInt();

        if(in1 == 1) {
            ia = true;
            System.out.println("Niveau d'IA:");
            System.out.println("  1> IA Aléatoire");
            System.out.println("  2> IA Offensive");
            System.out.println("  3> IA Difficile");
            System.out.println("  2> HU -- IA");    
            System.out.print("Entre le mode d'IA: ");

            in2 = scanner.nextInt();

            if(in2 == 3)
                joueur2 = new JoueurMiniMax(new Configuration(a, b), 100);
            else if(in2 == 2)
                joueur2 = new JoueurOffensif(new Configuration(a, b));

        }
        
        joueur = new JoueurMiniMax(new Configuration(a, b), 100);
        // JoueurIA joueur2 = new JoueurOffensif(new Configuration(a, b));
        // JoueurIA joueur2 = new JoueurRandom(new Configuration(a, b));

        int joueurNum = Plateau.JOUEUR_A;
        
        int i = 0;
        while (true) {
            i++;
            
            Coup coupChox;

            if(joueurNum == Plateau.JOUEUR_A)
                coupChox = joueur.choisirCoup(joueurNum);
            else
                coupChox = joueur2.choisirCoup(joueurNum);

            System.out.println("\n\nJoueur " + (joueurNum%4+2)/2 + " coup " + i + " de taille: " + coupChox.actions.size());
            for (Action action : coupChox.actions) {
                System.out.println(action.pos + " --> " + action.posNouveau);
            }
            joueur.plateau.jouer(coupChox);
            joueur2.plateau.jouer(coupChox);
            p.jouer(coupChox);
            afficherPlateau();
            // joueur.updatePlateau(p.getConfiguration());

            if(p.aBilleAuBut(joueurNum) || p.aGangeContreJeu(joueurNum)) {
                System.out.println("FIN PARTIE !!");
                break;
            }

            joueurNum = joueurNum%4+2;
        }


    }

}