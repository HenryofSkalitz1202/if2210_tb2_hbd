package com.hbd.Kartu.Makhluk;

public class Tanaman extends Makhluk {

    public Tanaman(String nama, int maksPanen){
        super(nama, maksPanen);
    }

    public int getUmur(){
        return this.getProgressPanen();
    }

    public void tambahUmurSatu(){
        this.setProgressPanen(this.getProgressPanen() + 1);
    }

    public int getUmurPanen(){
        return this.getMaksPanen();
    }

}