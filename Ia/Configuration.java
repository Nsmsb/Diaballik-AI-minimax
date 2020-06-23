package Ia;

public class Configuration {

    int[] posSuppA;
    int[] posSuppB;

    /**
     * Configuration contient les position des Supports et des billes
     * @param posSuppA tableau de 8 entiers, les 7 premiers sont les position des support de 1er joueur, le 8eme est l'indice du support qui à la bille dans le tableau 
     * @param posSuppB tableau de 8 entiers, les 7 premiers sont les position des support de 2eme joueur, le 8eme est l'indice du support qui à la bille dans le tableau 
     */
    public Configuration(int posSuppA[], int posSuppB[]) {
        this.posSuppA = posSuppA;
        this.posSuppB = posSuppB;
    }

}