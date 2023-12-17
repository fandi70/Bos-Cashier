package com.bospintar.cashier.model;

public class Mhome {
    private String id,idpetugas,nota , totalbayar, tanggal,jbayar,idtoko;


    public Mhome(String id,String idpetugas,String nota, String totalbayar, String tanggal, String jbayar, String idtoko) {
        this.id = id;
        this.idpetugas = idpetugas;
        this.nota = nota;
        this.totalbayar=totalbayar;
        this.tanggal = tanggal;
        this.jbayar = jbayar;
        this.idtoko = idtoko;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getTotalbayar() {
        return totalbayar;
    }

    public void setTotalbayar(String totalbayar) {
        this.totalbayar = totalbayar;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJbayar() {
        return jbayar;
    }

    public void setJbayar(String jbayar) {
        this.jbayar = jbayar;
    }

    public String getIdtoko() {
        return idtoko;
    }

    public void setIdtoko(String idtoko) {
        this.idtoko = idtoko;
    }

    public String getIdpetugas() {
        return idpetugas;
    }

    public void setIdpetugas(String idpetugas) {
        this.idpetugas = idpetugas;
    }
}
