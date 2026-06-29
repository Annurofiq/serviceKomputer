package com.servicekomputer.model;

public class Servis {
    private String idServis;
    private String idPelanggan;
    private String idTeknisi;
    private String jenisPerangkat;
    private String merk;
    private String kerusakan;
    private double biaya;
    private String tanggalMasuk;
    private String tanggalSelesai;
    private String status;
    private String catatan;

    private String namaPelanggan;
    private String namaTeknisi;

    public Servis() {}

    public Servis(String idServis, String idPelanggan, String idTeknisi,
                  String jenisPerangkat, String merk, String kerusakan,
                  double biaya, String tanggalMasuk, String tanggalSelesai,
                  String status, String catatan) {
        this.idServis = idServis;
        this.idPelanggan = idPelanggan;
        this.idTeknisi = idTeknisi;
        this.jenisPerangkat = jenisPerangkat;
        this.merk = merk;
        this.kerusakan = kerusakan;
        this.biaya = biaya;
        this.tanggalMasuk = tanggalMasuk;
        this.tanggalSelesai = tanggalSelesai;
        this.status = status;
        this.catatan = catatan;
    }

    public String getIdServis() { return idServis; }
    public void setIdServis(String idServis) { this.idServis = idServis; }

    public String getIdPelanggan() { return idPelanggan; }
    public void setIdPelanggan(String idPelanggan) { this.idPelanggan = idPelanggan; }

    public String getIdTeknisi() { return idTeknisi; }
    public void setIdTeknisi(String idTeknisi) { this.idTeknisi = idTeknisi; }

    public String getJenisPerangkat() { return jenisPerangkat; }
    public void setJenisPerangkat(String jenisPerangkat) { this.jenisPerangkat = jenisPerangkat; }

    public String getMerk() { return merk; }
    public void setMerk(String merk) { this.merk = merk; }

    public String getKerusakan() { return kerusakan; }
    public void setKerusakan(String kerusakan) { this.kerusakan = kerusakan; }

    public double getBiaya() { return biaya; }
    public void setBiaya(double biaya) { this.biaya = biaya; }

    public String getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(String tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }

    public String getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(String tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getNamaTeknisi() { return namaTeknisi; }
    public void setNamaTeknisi(String namaTeknisi) { this.namaTeknisi = namaTeknisi; }
}
