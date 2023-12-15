package com.bospintar.cashier.model;

public class MpendingDetail {
    private String hargajual;
    private String nama;
    private String qty;

    public MpendingDetail(String nama2, String qty2, String hargajual2) {
        this.qty = qty2;
        this.hargajual = hargajual2;
        this.nama = nama2;
    }

    public String getNama() {
        return this.nama;
    }

    public String getQty() {
        return this.qty;
    }

    public String getHargajual() {
        return this.hargajual;
    }
}
