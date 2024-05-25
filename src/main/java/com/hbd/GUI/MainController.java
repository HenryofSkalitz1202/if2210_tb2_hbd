package com.hbd.GUI;

import com.hbd.GUI.TampilanToko;
import com.hbd.Deck.Deck;
import com.hbd.Deck.Exception.DeckEmptyException;
import com.hbd.Deck.Exception.DeckOutOfBoundsException;
import com.hbd.Deck.Exception.DeckPenuhException;
import com.hbd.GameEngine;
import com.hbd.Kartu.Kartu;
import com.hbd.Kartu.Item.Item;
import com.hbd.Kartu.Makhluk.Makhluk;
import com.hbd.Kartu.Makhluk.Exception.ItemTidakAdaException;
import com.hbd.Kartu.Makhluk.Exception.SalahMakanException;
import com.hbd.Kartu.Produk.Produk;
import com.hbd.Pemain.Pemain;
import com.hbd.Pemain.Exception.BerusahaMemberiItemKeMakhlukGaibException;
import com.hbd.Pemain.Exception.IllegalItemException;
import com.hbd.PetakLadang.Exception.DiluarPetakException;
import com.hbd.PetakLadang.PetakLadang;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private BorderPane home;

    @FXML
    private void ButtonLadangku(ActionEvent event) {
        System.out.println("Test1");

        // Kode di bawah ini untuk mengganti view dari bagian tengah halaman
        // Ganti "testing" dengan nama file fxml asli, misal "ladangku"
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("testing");
        home.setCenter(view);
    }

    @FXML
    private void ButtonLadangLawan(ActionEvent event) {
        System.out.println("Test2");

        // Kode di bawah ini untuk mengganti view dari bagian tengah halaman
        // Ganti "testing" dengan nama file fxml asli, misal "ladangku"
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("draggable");
        home.setCenter(view);

    }

    @FXML
    private void ButtonToko(ActionEvent event) {
        TampilanToko toko = new TampilanToko();
        App.getPane().getChildren().add(toko);
    }

    @FXML
    private void ButtonSaveState(ActionEvent event) {
        System.out.println("Test4");
        Stage stage = new Stage();
        stage.setTitle("Save State");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("savestate");

        if (view != null) {
            Scene scene = new Scene(view, 600, 400);
            stage.setScene(scene);
            stage.show();
            System.out.println("Scene displayed successfully");
        } else {
            System.out.println("Failed to load view");
        }

    }

    @FXML
    private void ButtonLoadPlugin(ActionEvent event) {
        System.out.println("Test5");
        Stage stage = new Stage();
        stage.setTitle("Load Plugin");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("loadplugin");

        if (view != null) {
            Scene scene = new Scene(view, 600, 400);
            stage.setScene(scene);
            stage.show();
            System.out.println("Scene displayed successfully");
        } else {
            System.out.println("Failed to load view");
        }

    }

    @FXML
    private void ButtonLoadState(ActionEvent event) {
        System.out.println("Test6");
        Stage stage = new Stage();
        stage.setTitle("Load State");

        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("loadstate");

        if (view != null) {
            Scene scene = new Scene(view, 600, 400);
            stage.setScene(scene);
            stage.show();
            System.out.println("Scene displayed successfully");
        } else {
            System.out.println("Failed to load view");
        }
    }

    @FXML
    private void ButtonNext(ActionEvent event) {
        GameEngine.getInstance().next();
        if (getCurrentDeckAktif().remainingSlot() != 0) {
            App.getShuffleController().enterShuffle();
        } else {
            App.getMainPage().switchTo();
        }
    }

    public void LetGoHandler(Kartu kartu, int row, int column, int initialRow, int initialColumn, boolean fromLadang,
            boolean toLadang) throws DiluarPetakException {
        if (row == initialRow && column == initialColumn) {
            App.popupInfoController.popupCard(column, row, fromLadang);
            return;
        }
        if (toLadang) {
            if (fromLadang) {
                MoveInLadang(kartu, row, column, initialRow, initialColumn);
            } else /* From Hand */ {
                PlaceToLadang(kartu, row, column, initialRow, initialColumn);
            }
        } else /* To Hand */ {
            if (fromLadang) {
                WithdrawFromLadang(kartu, row, column, initialRow, initialColumn);
            } else /* From Hand */ {
                DeckReorder(kartu, row, column, initialRow, initialColumn);
            }
        }
    }

    private void MoveInLadang(Kartu kartu, int row, int column, int initialRow, int initialColumn)
            throws DiluarPetakException {
        if (getCurrentPetakLadang().getMakhluk(column, row) != null) {
            return;
        } else {
            getCurrentPetakLadang().setMakhluk(column, row, (Makhluk) kartu);
            getCurrentPetakLadang().setMakhluk(initialColumn, initialRow, null);
        }
    }

    private void PlaceToLadang(Kartu kartu, int row, int column, int initialRow, int initialColumn)
            throws DiluarPetakException {
        if (getCurrentPetakLadang().getMakhluk(column, row) != null) {
            if (kartu instanceof Item) {
                try {
                    getCurrentPemain().beriItemKeLadang(getCurrentPemain(), (Item) kartu, column, row);
                    getCurrentDeckAktif().takeKartuAt(initialColumn);

                } catch (DiluarPetakException | DeckPenuhException | DeckOutOfBoundsException | IllegalItemException
                        | ItemTidakAdaException | DeckEmptyException
                        | BerusahaMemberiItemKeMakhlukGaibException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (kartu instanceof Produk) {
                try {
                    getCurrentPemain().kasihMamam(getCurrentPemain(), (Produk) kartu, column, row);
                    getCurrentDeckAktif().takeKartuAt(initialColumn);
                } catch (DiluarPetakException | SalahMakanException | DeckEmptyException | DeckOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                }
            }
            return;
        } else {
            if (kartu instanceof Item || kartu instanceof Produk) {
                return;
            }
            getCurrentPetakLadang().setMakhluk(column, row, (Makhluk) kartu);
            try {
                getCurrentDeckAktif().takeKartuAt(initialColumn);
            } catch (Exception e) {
                ;
            }
        }
    }

    private void WithdrawFromLadang(Kartu kartu, int row, int column, int initialRow, int initialColumn)
            throws DiluarPetakException {
        if (getCurrentDeckAktif().remainingSlot() == 0) {
            return;
        } else {
            getCurrentPetakLadang().setMakhluk(initialColumn, initialRow, null);
            try {
                getCurrentDeckAktif().insertKartu(kartu);
            } catch (DeckPenuhException e) {
                /* Tidak Mungkin */}
        }
    }

    private void DeckReorder(Kartu kartu, int row, int column, int initialRow, int initialColumn)
            throws DiluarPetakException {
        try {
            getCurrentDeckAktif().takeKartuAt(initialColumn);
            getCurrentDeckAktif().insertKartu(kartu);
        } catch (Exception e) {
            /* Tidak akan terjadi */}
    }

    public void initializeGame() {
        GameEngine.getInstance().initializeDefault();
    }

    public PetakLadang getCurrentPetakLadang() {
        return GameEngine.getInstance().getCurrentPemain().getPetakLadang();
    }

    public Deck getCurrentDeckAktif() {
        return GameEngine.getInstance().getCurrentPemain().getDeckAktif();
    }

    public int getCurrentTurn() {
        return GameEngine.getInstance().getNomorTurn();
    }

    public int getCurrentPlayer1Duit() {
        return GameEngine.getInstance().getPlayer1Duit();
    }

    public int getCurrentPlayer2Duit() {
        return GameEngine.getInstance().getPlayer2Duit();
    }

    public Pemain getCurrentPemain() {
        return GameEngine.getInstance().getCurrentPemain();
    }
}