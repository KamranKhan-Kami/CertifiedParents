package com.ellalan.certifiedparent.model;

import java.io.Serializable;

public class Answer implements Serializable {
    private int a;//answers
    private int t;//total

    public Answer() {
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
