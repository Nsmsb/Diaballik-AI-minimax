package Ia;

public class Action {
    int pos;
    int posNouveau;

    public Action(int pos, int posNouveau) {
        this.pos = pos;
        this.posNouveau = posNouveau;
    }

    public void inverser() {
        int temp = pos;
        pos = posNouveau;
        posNouveau = temp;
    }
}