package com.bospintar.cashier.model;

public class Mpelanggan {
    private String id , nama, alamat,nohp,idtoko;
//

    public Mpelanggan(String id, String nama, String alamat, String nohp, String idtoko) {
        this.id = id;
        this.nama=nama;
        this.alamat = alamat;
        this.nohp = nohp;
        this.idtoko = idtoko;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getIdtoko() {
        return idtoko;
    }

    public void setIdtoko(String idtoko) {
        this.idtoko = idtoko;
    }
}
