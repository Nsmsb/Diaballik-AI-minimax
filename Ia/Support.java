package Ia;

public class Support implements Comparable<Support> {

    int position;
    int val;

    public Support(int position, int val) {
        this.position = position;
        this.val = val;
    }

    @Override
    public int compareTo(Support support) {
        return this.position%7 - support.position%7;
    }

}