package com.solt.game.gomoku;

import com.solt.game.player.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Gomoku extends Application {
    private static final Image OPIC = new Image(Gomoku.class.getResourceAsStream("/O.png"));
    private static final Image XPIC = new Image(Gomoku.class.getResourceAsStream("/X.png"));
    private static final boolean SAVE_MATCH = true;
    private static final long PLAY_SPEED = 2000;
    private Board board = SAVE_MATCH ? new SavableBoard() : new Board();
    private Label[][] cells;
    private Player player1;
    private Player player2;
    private volatile CompletableFuture<Point> currentMove;
    private Stage primaryStage;

    public Gomoku() throws IOException {
//        player1 = new ReplayPlayer(Replay.loadLastMatch("matchs/games"));
//        player2 = ((ReplayPlayer) player1).getOppositePlayer();
//        player1 = new HumanPlayer(Symbol.O, this);
//        player2 = new HumanPlayer(Symbol.X, this);
        player1 = new MctsPlayer(board, Symbol.O);
//        player1 = new AlphaGomoku(board, Symbol.O, "model/neuralnet1.zip"); //already got more train here
        player2 = new AlphaGomoku(board, Symbol.X, "model/neuralnet2.zip");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static Image getImage(Symbol symbol) {
        switch (symbol) {
            case O:
                return OPIC;
            case X:
                return XPIC;
            default:
                return null;
        }
    }

    private void initCellHandler(Label cell) {
        cell.setOnMouseClicked(event -> {
            Point point = (Point) cell.getUserData();
            if (isPicked(cell)) {
                return;
            } else if (currentMove != null) {
                currentMove.complete(point);
            }
        });
    }

    private boolean isPicked(Label cell) {
        return cell.getGraphic() != null;
    }

    public synchronized CompletableFuture<Point> getMove() {
        currentMove = new CompletableFuture<>();
        return currentMove;
    }

    private void endGame() {
        //save match for learning
        if (SAVE_MATCH && !(player1 instanceof ReplayPlayer)) {
            try {
                ((SavableBoard) board).saveMatch("matchs/games");
                if (player1 instanceof AIPlayer) {
                    ((AIPlayer) player1).learnLastMatch();
                }
                if (player2 instanceof AIPlayer) {
                    ((AIPlayer) player2).learnLastMatch();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Stage stage2 = new Stage();
        GridPane g2 = new GridPane();
        g2.setPadding(new Insets(20, 20, 20, 20));
        g2.setVgap(20);
        g2.setHgap(20);
        Label label2 = new Label();
        if (board.hasXWon()) {
            label2.setText("You better learn to play first, kid!");
            stage2.setTitle(board.getWonPlayer() + " win!");
        } else {
            label2.setText("You can't beat me, Stop trying!");
            stage2.setTitle(board.getWonPlayer() + " win!");
        }
        g2.add(label2, 0, 0, 2, 1);
        Button onceMore = new Button("Lemme play again!");
        Button quit = new Button("I'm tired. I quit!");
        g2.add(onceMore, 1, 1, 1, 1);
        g2.add(quit, 2, 1, 1, 1);
        onceMore.setOnMouseClicked(q -> {
            primaryStage.close();
            stage2.close();
            board.resetBoard();
            start(new Stage());
        });


        quit.setOnMouseClicked(q -> {
            try {
                if (player1 instanceof AIPlayer) {
                    ((AIPlayer) player1).saveModel();
                }
                if (player2 instanceof AIPlayer) {
                    ((AIPlayer) player2).saveModel();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
        Scene scene = new Scene(g2);
        scene.getStylesheets().addAll(this.getClass().getResource("/result.css").toExternalForm());
        stage2.setScene(scene);
        stage2.setOnCloseRequest(q -> {
            primaryStage.close();
        });
        stage2.show();

    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Stage stage = new Stage();
        GridPane g = new GridPane();
        g.setId("firstDialog");
        g.setPadding(new Insets(20, 20, 20, 20));
        g.setVgap(20);
        g.setHgap(20);

        //First Dialog Labels and Buttons
        Label label = new Label("Who will play first?");
        Button IWillPlay = new Button("Lemme play first!");
        Button YouPlay = new Button("You're a legend! you play first!");
        g.add(label, 0, 0, 2, 1);
        g.add(IWillPlay, 0, 1, 1, 1);
        g.add(YouPlay, 1, 1, 1, 1);

        //Scene for the firstDialog
        Scene sc = new Scene(g, 450, 200);
        g.setAlignment(Pos.CENTER);
        sc.getStylesheets().addAll(this.getClass().getResource("/firstDialog.css").toExternalForm());
        stage.setTitle("Choose turn");
        stage.setScene(sc);
        stage.setOnCloseRequest(e -> System.exit(0));

        //Board scene
        GridPane grid = new GridPane();

        cells = new Label[board.getSize()][board.getSize()];
        Label cell;
        for (int r = 0; r < cells.length; ++r) {
            for (int c = 0; c < cells[0].length; ++c) {
                cell = new Label();
                cell.setMinSize(32, 32);
                cell.setUserData(new Point(c, r));
                cells[r][c] = cell;
                initCellHandler(cell);
            }
            grid.addRow(r, cells[r]);
        }

        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(800, 800);
        grid.setGridLinesVisible(true);
        grid.setId("board");

        Scene scene = new Scene(grid);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(scene);

        scene.getStylesheets().addAll(this.getClass().getResource("/board.css").toExternalForm());

        //FirstWindow Action Listeners
        IWillPlay.setOnMouseClicked((event) -> {
            stage.close();
            play(player1);
        });

        YouPlay.setOnMouseClicked((event) -> {
            stage.close();
            play(player2);
        });
        stage.showAndWait();  //Tag1
        /*
         The placement position of this line (tag1) is important.
         If you place this line above the listeners, the listeners
         aren't gonna work
         */
        primaryStage.show();
    }

    private void play(Player player) {
        player.nextMove().thenAccept(p ->
                Platform.runLater(() -> {
                    Symbol symbol = player.getSymbol();
                    cells[p.y][p.x].setGraphic(new ImageView(getImage(symbol)));
                    board.performMove(p, symbol);
                    System.out.println(board);
                    System.out.println("Placed " + player.getSymbol().name() + " at: " + p);
                    if (board.isGameOver()) {
                        endGame();
                    } else {
                        nextPlayer(player);
                    }
                }));
    }

    private void nextPlayer(Player lastPlayer) {
        Player player = lastPlayer.getSymbol() == player1.getSymbol() ? player2 : player1;
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(PLAY_SPEED);
                play(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}