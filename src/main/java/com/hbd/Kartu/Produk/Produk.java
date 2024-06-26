package com.hbd.Kartu.Produk;

import com.hbd.Kartu.Kartu;
import javafx.scene.shape.Rectangle;

public class Produk extends Kartu {
    private final int harga;
    private final int tambahanBerat;

    public Produk(String nama, int harga, int tambahanBerat) {
        super(nama, new Rectangle());
        this.harga = harga;
        this.tambahanBerat = tambahanBerat;
    }

    public int getHarga() {
        return this.harga;
    }

    public int getTambahanBerat() {
        return this.tambahanBerat;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Produk produk = (Produk) obj;

        if (harga != produk.harga)
            return false;
        return getNama() != null ? getNama().equals(produk.getNama()) : produk.getNama() == null;
    }
}
