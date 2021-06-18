package com.ellalan.certifiedparent.model;

import java.io.Serializable;

public class Question implements Serializable{

    private String Q = "";
    private String A ="";
    private String B = "";
    private String C = "";
    private String D = "";
    private String R = "";
    private String I = "";
    private String T = "";
    public Question(){

    }

    public String getT() {
        return T;
    }

    public void setT(String t) {
        T = t;
    }

    public String getQ() {
        return Q;
    }

    public void setQ(String q) {
        Q = q;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getR() {
        return R;
    }

    public void setR(String r) {
        R = r;
    }

    public String getI() {
        return I;
    }

    public void setI(String i) {
        I = i;
    }
}
