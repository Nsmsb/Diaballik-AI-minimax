package Ia;

import java.util.ArrayList;
import java.util.List;

public class Coup {

    List<Action> actions;
    
    /**
     * Coup est une list d'Actions (decplacements ou passes)
     */
    public Coup() {
        this.actions = new ArrayList<Action>();
    }

    /**
     * Constricteur prendre une Lists d'Actions
     * @param actions List d'actions, chaque action contien pos et posNouveau du support/bille
     */
    public Coup(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * Ajouter une action à la list des actions du Coup
     * @param action
     */
    public void ajouterAction(Action action) {
        this.actions.add(action);
    }

    /**
     * Copier le Coup
     * @return un Objet Coup, avec une nouvelle list qui contient les méme réferences des Actions
     */
    public Coup copier() {
        List<Action> copy = new ArrayList<Action>();
        for (Action action : actions) {
            copy.add(action);
        }
        return new Coup(copy);
    }
}