package com.bospintar.cashier.model;

public class Mpending {
    private String idtrnasaksi;
    private String tanggal;
    private String totalbayar;
    private String ket;

    public Mpending(String idtrnasaksi2, String totalbayar2, String tanggal2,String ket) {
        this.idtrnasaksi = idtrnasaksi2;
        this.tanggal = tanggal2;
        this.totalbayar = totalbayar2;
        this.ket = ket;
    }

    public String getKet() {
        return ket;
    }

    public String getTanggal() {
        return this.tanggal;
    }

    public String getIdtrnasaksi() {
        return this.idtrnasaksi;
    }

    public String getTotalbayar() {
        return this.totalbayar;
    }
}
