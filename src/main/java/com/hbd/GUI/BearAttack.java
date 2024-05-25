package com.hbd.GUI;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.hbd.Deck.Deck;
import com.hbd.Deck.Exception.DeckPenuhException;
import com.hbd.Kartu.FactoryKartu;
import com.hbd.Kartu.Makhluk.Makhluk;
import com.hbd.PetakLadang.Exception.DiluarPetakException;
import com.hbd.PetakLadang.PetakLadang;
import com.hbd.Clock;

public class BearAttack {

    private static Label timeLabel;
    public static volatile boolean timerRunning = true;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Future<?> inputTask;

    public static void setTimeLabel(Label label) {
        timeLabel = label;
    }

    public static List<int[]> generateAttackedCells() {
        List<int[]> attackedCells = new ArrayList<>();
        Random rand = new Random();

        int maxNumRow = 3;
        int minNumRow = 1;
        int maxNumCol = 3;
        int minNumCol = 1;
        int numRow = rand.nextInt(maxNumRow - minNumRow + 1) + minNumRow;
        if (numRow == 3) {
            maxNumCol = 2;
        }
        int numCol = rand.nextInt(maxNumCol - minNumCol + 1) + minNumCol;
        int startRow = rand.nextInt(4 - numRow + 1);
        int startCol = rand.nextInt(5 - numCol + 1);

        System.out.println("Cells attacked:");

        for (int i = startRow; i < startRow + numRow; i++) {
            for (int j = startCol; j < startCol + numCol; j++) {
                System.out.printf("(%d, %d)\n", j, i);
                attackedCells.add(new int[]{i, j});
            }
        }

        return attackedCells;
    }

    public static void attack(PetakLadang ladang, Deck deckAktif, List<int[]> attackedCells) throws DiluarPetakException, DeckPenuhException {
        boolean trapped = false;

        for (int[] cell : attackedCells) {
            Makhluk makhlukCell = ladang.getMakhluk(cell[1], cell[0]);
            if (makhlukCell == null) {
                System.out.printf("(%d, %d) is empty\n", cell[1], cell[0]);
            } else {
                boolean isProtected = false;
                for (String effect : makhlukCell.getEffect()) {
                    if (effect.equals("Protect")) {
                        System.out.printf("(%d, %d) is protected\n", cell[1], cell[0]);
                        isProtected = true;
                        break;
                    } else if (effect.equals("Trap")) {
                        System.out.printf("(%d, %d) traps beruang!!\n", cell[1], cell[0]);
                        if (!deckAktif.isFull()) {
                            deckAktif.insertKartu(FactoryKartu.getKartu("Beruang"));
                        }
                        trapped = true;
                        break;
                    }
                }

                if (trapped) {
                    break;
                }

                if (!isProtected) {
                    System.out.printf("(%d, %d) is attacked\n", cell[1], cell[0]);
                    ladang.setNull(cell[1], cell[0]);
                }
            }
        }
    }

    public static void startAttack(int countdownSeconds, MainPage mainPage) {
        timerRunning = true;

        // Start the countdown clock
        Thread runner = new Thread(new Clock(countdownSeconds));
        runner.start();

        // Update cards and handle attack after countdown
        inputTask = executorService.submit(() -> {
            while (timerRunning) {
                try {
                    Thread.sleep(1000); // Wait 1 second
                    Platform.runLater(() -> mainPage.loadCard()); // Update UI
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Execute the attack on the JavaFX thread
            Platform.runLater(() -> {
                try {
                    List<int[]> attackedCells = generateAttackedCells();
                    attack(mainPage.getController().getCurrentPetakLadang(), mainPage.getController().getCurrentDeckAktif(), attackedCells);
                    mainPage.loadCard(); // Refresh the UI after the attack
                } catch (Exception e) {
                    System.out.println("Error during bear attack: " + e.getMessage());
                }
            });
        });
    }

    public static void updateLabel(String text) {
        Platform.runLater(() -> {
            if (timeLabel != null) {
                timeLabel.setText(text);
            }
        });
    }

    public static void stopTimer() {
        timerRunning = false;
        if (inputTask != null && !inputTask.isDone()) {
            inputTask.cancel(true); // Interrupt the input thread
        }
    }
}