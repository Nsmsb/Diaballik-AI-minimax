package Ia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {

    public static final int JOUEUR_A = 2;
    public static final int JOUEUR_B = 4;

    // properties
    Support[] supportsA;
    Support[] supportsB;
    Support[][] plateau;
    
    /**
     * A partir d'une configuration, creer un objet Plateau, pour simuler les Coups réels,
     * dans le plateau, une position de support est un entier entre [0-46] (ex: pos 7 == plateau[7/7][7%7] == plateau[1][0])
     * @param conf une Configuration
     */
    public Plateau(Configuration conf) {  

        // checking params
        if(conf.posSuppA.length != 8 || conf.posSuppB.length != 8)
            throw new IllegalStateException("invalid positions");

        this.supportsA = new Support[7];
        this.supportsB = new Support[7];
        this.plateau = new Support[7][7];

        Support tempA, tempB;
        for (int i = 0; i < 7; i++) {
            supportsA[i] = new Support(conf.posSuppA[i], JOUEUR_A);
            supportsB[i] = new Support(conf.posSuppB[i], JOUEUR_B);
            tempA = supportsA[i];
            tempB = supportsB[i];
            plateau[tempA.position/7][tempA.position%7] = tempA;
            plateau[tempB.position/7][tempB.position%7] = tempB;
        }

        // update ball holder
        supportsA[conf.posSuppA[7]].val |= 1;
        supportsB[conf.posSuppB[7]].val |= 1;
    }

    /**
     * getter pour un suppor
     * @param pos la position du Support
     * @return un réference du Support dan la position plateau[pos/7][pos%7]
     */
    public Support getSupport(int pos) {
        return plateau[pos/7][pos%7];
    }

    /**
     * test si un Supprt à la bille
     * @param support réference d'un objet Support
     * @return booléen
     */
    public boolean aBille(Support support) {
        return (support.val&1) == 1;
    }

    /**
     * get la configuration actuelle du plateau
     * @return objet Configuration
     */
    public Configuration getConfiguration() {
        int posBilleA = 3, posBilleB = 3;
        int a[] = new int[8];
        int b[] = new int[8];

        Support tempA, tempB;
        for (int i = 0; i < 7; i++) {
            tempA = supportsA[i];
            tempB = supportsB[i];

            a[i] = tempA.val & ~1;
            b[i] = tempB.val & ~1;

            if(aBille(tempA))
                posBilleA = i;

            if(aBille(tempB))
                posBilleB = i;
        }

        a[7] = posBilleA;
        b[7] = posBilleB;

        return new Configuration(a, b);
    }

    /**
     * calculer les Coups possible à jouer
     * @param joueur qui a le tour
     * @return List des Coups
     */
    public List<Coup> getCoupsPossible(int joueur) {
        List<Coup> result = new ArrayList<Coup>();
        List<Coup> uneAction = new ArrayList<Coup>();
        List<Coup> deuxActions = new ArrayList<Coup>();
        Coup coupNouv;
        boolean passe;
        
        // Coups d'une seule action 
        // un seul deplqacement
        for (Action action : getDepPossibles(joueur)) {
            coupNouv = new Coup();
            coupNouv.ajouterAction(action);
            uneAction.add(coupNouv);
            result.add(coupNouv);
        }

        // une seule passe
        for (Action action : getPassePossibles(joueur)) {
            coupNouv = new Coup();
            coupNouv.ajouterAction(action);
            uneAction.add(coupNouv);
            result.add(coupNouv);
        }
        
        // Coups de deux actions
        for (Coup coup : uneAction) {

            passe = jouer(coup);
            
            if(!passe) {
                for (Action action : getPassePossibles(joueur)) {
                    coupNouv = coup.copier();
                    coupNouv.ajouterAction(action);
                    deuxActions.add(coupNouv);
                    result.add(coupNouv);
                }
            }
            
            for (Action action : getDepPossibles(joueur)) {
                coupNouv = coup.copier();
                coupNouv.ajouterAction(action);
                deuxActions.add(coupNouv);
                result.add(coupNouv);
            }

            annuler(coup);
        }

        // Coups de 3 actions
        for (Coup coup : deuxActions) {

            passe = jouer(coup);
            
            if(!passe) {
                for (Action action : getPassePossibles(joueur)) {
                    coupNouv = coup.copier();
                    coupNouv.ajouterAction(action);
                    result.add(coupNouv);
                }
            } else {
                for (Action action : getDepPossibles(joueur)) {
                    coupNouv = coup.copier();
                    coupNouv.ajouterAction(action);
                    result.add(coupNouv);
                }
            }


            annuler(coup);
        }


        return result;
    }

    public boolean jouer(Coup coup) {
        boolean contienPasse = false;
        for(Action action : coup.actions) {
            if(jouerAction(action))
                contienPasse = true;
        }
        return contienPasse;
    }


    
    public void annuler(Coup coup) {
        Action temp;
        for (int i = coup.actions.size()-1; i >= 0; i--) {
            temp = coup.actions.get(i);
            temp.inverser();
            jouerAction(temp);
            temp.inverser();
        }
    }

    public boolean aBilleAuBut(int joueur) {
        int position = 6;
        Support[] supports;

        if(joueur == Plateau.JOUEUR_A)
            supports = supportsA;
        else if(joueur == Plateau.JOUEUR_B) {
            supports = supportsB;
            position = 0;
        }
        else
            throw new IllegalArgumentException("Wrong player parameter passeed");
        
        for (Support support : supports) {
            if(support.position/7 == position && aBille(support))
                return true;
        }

        return false;   
    }
    

    public boolean aGangeContreJeu(int joueur) {
        Support[] supports;
        boolean ligne = false;
        int i = 0, posSuc;

        if(joueur == Plateau.JOUEUR_A)
            supports = supportsB;
        else if(joueur == Plateau.JOUEUR_B)
            supports = supportsA;
        else
            throw new IllegalArgumentException("Wrong player parameter passeed");

        Arrays.sort(supports);

        // test ligne supports du joueur_B
        while(i<6 && supports[i].position%7 == i) {
            posSuc = supports[i].position/7 - supports[i+1].position/7;
            if(posSuc > -2 && posSuc < 2)
                i++;
            else
                break;
        }
        if(i == 6)
            ligne = true;

        i = 0;
        if(ligne) {
            for (Support support : supports) {
                if(joueur == Plateau.JOUEUR_A){
                    if(estJoueur(support.position/7-1,support.position%7, joueur))
                        i++;                    
                }
                else {
                    if(estJoueur(support.position/7+1,support.position%7, joueur))
                        i++;
                }
            }
        }

        if(i >= 3 && ligne)
            return true;

        return false;
    }

    // private methodes, used in public methodes

    private boolean jouerAction(Action action) {
        Support sup, supNouv, temp;
        boolean contienPasse = false;
        sup = getSupport(action.pos);
        supNouv = getSupport(action.posNouveau);

        // if ball holder, then passeBall(), else perform a deplacment
        if(aBille(sup)) {
            sup.val &= ~1;
            supNouv.val |= 1;
            contienPasse = true;
        } else {
            temp = getSupport(action.posNouveau);
            sup.position = action.posNouveau;
            plateau[action.posNouveau/7][action.posNouveau%7] = sup;
            plateau[action.pos/7][action.pos%7] = temp;
        }

        return contienPasse;
    }

     List<Action> getDepPossibles(int joueur) {
        List<Action> result = new ArrayList<Action>();
        Support[] sups;
        int i,j;

        if (joueur == JOUEUR_A)
            sups = supportsA;
        else if (joueur == JOUEUR_B)
            sups = supportsB;
        else
            throw new IllegalArgumentException("Wrong player parameter passeed");

        for (Support sup : sups) {
            if (aBille(sup))
                continue;
            
            i = sup.position/7;
            j = sup.position%7;

            //UP
            if(estVide(i-1, j))
                result.add(new Action(sup.position, sup.position-7));
            
            //DOWN
            if(estVide(i+1, j))
            result.add(new Action(sup.position, sup.position+7));

            //LEFT
            if(estVide(i, j-1))
                result.add(new Action(sup.position, sup.position-1));

            //RIGHT
            if(estVide(i, j+1))
                result.add(new Action(sup.position, sup.position+1));
        }

        return result;
    }

     List<Action> getPassePossibles(int joueur) {
        List<Action> result = new ArrayList<Action>();
        Support[] sups;
        Support supBille;
        int i,j, iPasse, jPasse;

        
        if (joueur == JOUEUR_A)
            sups = supportsA;
        else if (joueur == JOUEUR_B)
            sups = supportsB;
        else
            throw new IllegalArgumentException("Wrong player parameter passeed");
        
        i = 0;
        supBille = sups[i];
        while (!aBille(supBille) && i < 7)
            supBille = sups[++i];

        i = supBille.position/7;
        j = supBille.position%7;
        iPasse = i;
        jPasse = j;

        // LEFT
        jPasse--;
        while(jPasse >= 0) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            jPasse--;
        }

        // RIGHT
        jPasse = j+1;
        while(jPasse < 7) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            jPasse++;
        }

        // UP
        jPasse = j;
        iPasse = i-1;
        while(iPasse>=0) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            iPasse--;
        }

        // DOWNM
        iPasse = i+1;
        while(iPasse < 7) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            iPasse++;
        }

        // LEFT UP
        iPasse = i-1;
        jPasse = j-1;
        while(iPasse >= 0 && jPasse >= 0) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            iPasse--;
            jPasse--;
        }

        // RIGHT UP
        iPasse = i-1;
        jPasse = j+1;
        while(iPasse >= 0 && jPasse < 7) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            iPasse--;
            jPasse++;
        }

        // LEFT DOWN
        iPasse = i+1;
        jPasse = j-1;
        while(iPasse < 7 && jPasse >= 0) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            iPasse++;
            jPasse--;
        }

        // RIGHT DOWN
        iPasse = i+1;
        jPasse = j+1;
        while(iPasse < 7 && jPasse < 7) {
            if(estAdversaire(iPasse, jPasse, joueur))
                break;
            if(estJoueur(iPasse, jPasse, joueur))
                result.add(new Action(supBille.position, iPasse*7 + jPasse));
            iPasse++;
            jPasse++;
        }
        
        
        return result;
    }

    public boolean estVide(int i, int j) {
        if(i > 6 || i < 0 || j > 6 || j < 0)
            return false;
        
        return plateau[i][j] == null;
    }

    public boolean estAdversaire(int i, int j, int joueur) {
        if(i > 6 || i < 0 || j > 6 || j < 0)
            return false;
        if(estVide(i, j))
            return false;
        
        return ((plateau[i][j].val & joueur) & ~1) == 0;
    }

    public boolean estJoueur(int i, int j, int joueur) {
        if(i > 6 || i < 0 || j > 6 || j < 0)
            return false;
        if(estVide(i, j))
            return false;
        
        return (plateau[i][j].val & joueur) != 0;
    }


}