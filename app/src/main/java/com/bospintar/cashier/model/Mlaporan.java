package com.bospintar.cashier.model;

public class Mlaporan {
    private String idpetugas,nama_petugas,level , total_transaksi, total_penjualan,idtoko;


    public Mlaporan(String idpetugas,String nama_petugas,String level, String total_transaksi, String total_penjualan,String idtoko) {
        this.idpetugas = idpetugas;
        this.nama_petugas = nama_petugas;
        this.level = level;
        this.total_transaksi=total_transaksi;
        this.total_penjualan = total_penjualan;
        this.idtoko = idtoko;
    }

    public String getIdpetugas() {
        return idpetugas;
    }

    public void setIdpetugas(String idpetugas) {
        this.idpetugas = idpetugas;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public void setNama_petugas(String nama_petugas) {
        this.nama_petugas = nama_petugas;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTotal_transaksi() {
        return total_transaksi;
    }

    public void setTotal_transaksi(String total_transaksi) {
        this.total_transaksi = total_transaksi;
    }

    public String getTotal_penjualan() {
        return total_penjualan;
    }

    public void setTotal_penjualan(String total_penjualan) {
        this.total_penjualan = total_penjualan;
    }

    public String getIdtoko() {
        return idtoko;
    }

    public void setIdtoko(String idtoko) {
        this.idtoko = idtoko;
    }
}
