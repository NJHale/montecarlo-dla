package edu.njit.math450.matrix;

public class Locale {
    public int i, j;

    public Locale() {}

    public Locale(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public Locale(Locale locale) {
        i = locale.i;
        j = locale.j;
    }

    public void setCoords(int i, int j) {
        this.i=i;
        this.j=j;
    }
}