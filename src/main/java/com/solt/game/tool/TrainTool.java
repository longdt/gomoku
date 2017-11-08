package com.solt.game.tool;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.SavableBoard;
import com.solt.game.gomoku.Symbol;
import com.solt.game.player.AIPlayer;
import com.solt.game.player.AlphaGomoku;
import com.solt.game.player.MctsPlayer;
import com.solt.game.player.Player;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class TrainTool {
    private Board board;
    private Player player1;
    private Player player2;
    private boolean saveMatch;
    private volatile boolean shutdown = false;
    private Player whoPlayFirst;

    public TrainTool(boolean saveMatch) {
        board = saveMatch ? new SavableBoard() : new Board();
        player1 = new AlphaGomoku(board, Symbol.O, "model/neuralnet1.zip"); //already got more train here
        player2 = new MctsPlayer(board, Symbol.X);
        this.saveMatch = saveMatch;
    }

    public static void main(String[] args) throws IOException {
        new TrainTool(false).run();
    }

    public void run() throws IOException {
        CompletableFuture.runAsync(() -> {
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).whenComplete((v, e) -> shutdown = true);
        while (!shutdown) {
            newGame();
            playGame();
            endGame();
        }
        if (player1 instanceof AIPlayer)
            ((AIPlayer) player1).saveModel();
        if (player2 instanceof AIPlayer)
            ((AIPlayer) player2).saveModel();
    }

    private void playGame() {
        if (whoPlayFirst != player1) {
            whoPlayFirst = player1;
            play(player1);
        } else {
            whoPlayFirst = player2;
            play(player2);
        }

    }

    private void play(Player player) {
        player.nextMove().thenAccept(p -> {
            Symbol symbol = player.getSymbol();
            board.performMove(p, symbol);
            System.out.println(board);
            System.out.println("Placed " + player.getSymbol().name() + " at: " + p);
            if (board.isGameOver()) {
                endGame();
            } else {
                nextPlayer(player);
            }
        });
    }

    private void nextPlayer(Player lastPlayer) {
        Player player = lastPlayer.getSymbol() == player1.getSymbol() ? player2 : player1;
        play(player);
    }

    private void endGame() {
        try {
            if (saveMatch)
                ((SavableBoard) board).saveMatch("matchs/games");
            if (player1 instanceof AIPlayer)
                ((AIPlayer) player1).learnLastMatch();
            if (player2 instanceof AIPlayer)
                ((AIPlayer) player2).learnLastMatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newGame() {
        board.resetBoard();
    }
}
