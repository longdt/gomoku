package com.solt.game.player;

import com.solt.game.gomoku.Gomoku;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

import java.util.concurrent.CompletableFuture;

public class HumanPlayer extends BasePlayer {
    private Gomoku gomoku;

    public HumanPlayer(Symbol symbol, Gomoku gomoku) {
        super(symbol);
        this.gomoku = gomoku;
    }

    @Override
    public synchronized CompletableFuture<Point> nextMove() {
        return gomoku.getMove();
    }
}
