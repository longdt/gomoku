package com.solt.game.player;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPlayer extends BasePlayer {
    private Board board;

    public RandomPlayer(Symbol symbol, Board board) {
        super(symbol);
        this.board = board;
    }

    @Override
    public synchronized CompletableFuture<Point> nextMove() {
        List<Point> availables = board.getEmptyPositions();
        if (availables.isEmpty()) {
            return null;
        }
        Point point = availables.get(ThreadLocalRandom.current().nextInt(
                availables.size()));
        return CompletableFuture.completedFuture(point);
    }
}
