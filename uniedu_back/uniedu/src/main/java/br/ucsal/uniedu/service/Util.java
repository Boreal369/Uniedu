package br.ucsal.uniedu.service;

public final class Util {
    private Util() { }

    public static boolean vazio(String s) { return s == null || s.isBlank(); }

    public static Double arredondar1(double v) { return Math.round(v * 10.0) / 10.0; }
}
