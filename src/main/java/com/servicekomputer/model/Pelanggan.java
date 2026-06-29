package com.servicekomputer.model;

public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String noHp;
    private String alamat;

    public Pelanggan() {}

    public Pelanggan(String idPelanggan, String nama, String noHp, String alamat) {
        this.idPelanggan = idPelanggan;
        this.nama = nama;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    public String getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(String idPelanggan) { this.idPelanggan = idPelanggan; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    @Override
    public String toString() { return nama; }
}
