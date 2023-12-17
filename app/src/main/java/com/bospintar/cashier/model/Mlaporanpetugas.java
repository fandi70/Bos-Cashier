package com.bospintar.cashier.model;

public class Mlaporanpetugas {
    private String id,nota,tanggal , totalbayar, statusbayar,jbayar,idtoko;


    public Mlaporanpetugas(String id,String nota,String tanggal, String totalbayar, String statusbayar,String jbayar,String idtoko) {
        this.id = id;
        this.nota = nota;
        this.tanggal = tanggal;
        this.totalbayar=totalbayar;

        this.statusbayar = statusbayar;
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTotalbayar() {
        return totalbayar;
    }

    public void setTotalbayar(String totalbayar) {
        this.totalbayar = totalbayar;
    }

    public String getStatusbayar() {
        return statusbayar;
    }

    public void setStatusbayar(String statusbayar) {
        this.statusbayar = statusbayar;
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
}
