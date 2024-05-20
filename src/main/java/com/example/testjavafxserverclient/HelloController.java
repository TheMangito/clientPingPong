package com.example.testjavafxserverclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HelloController {

    @FXML private Label winner;
    private int scorep1 = 0;
    private int scorep2 = 0;
    @FXML private AnchorPane panel;
    private String inputLine;
    private PrintWriter out;
    private BufferedReader in;
    private Circle pelota;
    private int posicionInicialPelotaX = 453;
    private int posicionInicialPelotaY = 200;
    private int posicionPelotaX = 440;
    private int posicionPelotaY = 200;

    @FXML private Label player1score;
    @FXML private Label player2score;


    @FXML private Rectangle player1;
    @FXML private Pane player2;
    public void parsePosicion(String input) {
        if (input.length()>4){
            int[] posiciones = new int[2];
            try {
                String[] partes = input.split(",");
                posiciones[0] = Integer.parseInt(partes[0].trim());
                posiciones[1] = Integer.parseInt(partes[1].trim());
                Platform.runLater(() -> {
                    pelota.setCenterX(posiciones[0]);
                    pelota.setCenterY(posiciones[1]);

                });
            }catch (NumberFormatException e){
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    public void movementPlayers(String inputLine){
        String[] partes = inputLine.split(":");
        String movimiento = partes[0];
        int posicion = Integer.parseInt(partes[1].trim());
        switch (movimiento){
            case "UPP1" -> {
                if (!(player1.getLayoutY() + 20 < 40))
                    Platform.runLater(() -> player1.setLayoutY(posicion));
            }
            case "DWP1" ->{
                if (!(player1.getLayoutY() + 20 > 300))
                    Platform.runLater(() -> player1.setLayoutY(posicion));
            }
            case "UPP2" ->{
                if (!(player2.getLayoutY() + 20 < 40))
                    Platform.runLater(() -> player2.setLayoutY(posicion));
            }
            case "DWP2" ->{
                if (!(player2.getLayoutY() + 20 > 300))
                    Platform.runLater(() -> player2.setLayoutY(posicion));
            }

        }
    }
    public void initialize() {
        panel.requestFocus();
        pelota = new Circle(10);
        setupPelota();
        try {
            Socket client = new Socket("localhost", 6666);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t1 = new Thread(() -> {
            while (true) {
                try {
                    String inputLine = in.readLine();
                    if (inputLine != null) {
                        if (inputLine.contains("UP") || inputLine.contains("DW")) {
                            movementPlayers(inputLine);
                        }else if (inputLine.contains("SCOP")){
                            switch (inputLine){
                                case "SCOP1" -> {
                                    scorep1 += 1;
                                    Platform.runLater(()->{
                                        player1score.setText(String.valueOf(scorep1));
                                    });
                                }
                                case "SCOP2" -> {
                                    scorep2 += 1;
                                    Platform.runLater(()->{
                                        player2score.setText(String.valueOf(scorep2));
                                    });
                                }
                            }
                        }else if (inputLine.contains("WIN")){
                            switch (inputLine){
                                case "P1WIN" -> {
                                    winner.setText("PLAYER  1 WINS!");
                                    winner.setVisible(true);
                                }
                                case "P2WIN" -> {
                                    winner.setText("PLAYER  2 WINS!");
                                    winner.setVisible(true);
                                }
                            }

                        }else if (inputLine.contains(",")) {
                            parsePosicion(inputLine);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
        panel.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            System.out.println(event.getCode());
            switch (event.getCode()) {
                case W -> {
                    out.println("W");
                }
                case S -> {
                    out.println("S");
                }
                case J -> {
                    out.println("UP");
                }
                case K -> {
                    out.println("DOWN");
                }
                case A -> {
                    out.println("A");
                }

            }
        });
    }
    private void setupPelota() {
        if (!panel.getChildren().contains(pelota)) {
            panel.getChildren().add(pelota);
        }
        posicionPelotaX = posicionInicialPelotaX;
        posicionPelotaY = posicionInicialPelotaY;
        pelota.setCenterX(posicionPelotaX);
        pelota.setCenterY(posicionPelotaY);
    }

    public void start(){

    }

}