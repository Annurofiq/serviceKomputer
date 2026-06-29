package com.servicekomputer.model;

public class Teknisi {
    private String idTeknisi;
    private String namaTeknisi;
    private String keahlian;
    private String noHp;

    public Teknisi() {}

    public Teknisi(String idTeknisi, String namaTeknisi, String keahlian, String noHp) {
        this.idTeknisi = idTeknisi;
        this.namaTeknisi = namaTeknisi;
        this.keahlian = keahlian;
        this.noHp = noHp;
    }

    public String getIdTeknisi() { return idTeknisi; }
    public void setIdTeknisi(String idTeknisi) { this.idTeknisi = idTeknisi; }

    public String getNamaTeknisi() { return namaTeknisi; }
    public void setNamaTeknisi(String namaTeknisi) { this.namaTeknisi = namaTeknisi; }

    public String getKeahlian() { return keahlian; }
    public void setKeahlian(String keahlian) { this.keahlian = keahlian; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    @Override
    public String toString() { return namaTeknisi; }
}
