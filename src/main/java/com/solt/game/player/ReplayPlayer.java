package com.solt.game.player;

import com.solt.game.gomoku.Move;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Replay;
import com.solt.game.gomoku.Symbol;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReplayPlayer extends BasePlayer {
    private List<Move> history;
    private Iterator<Move> iterator;
    private ReplayPlayer opponentPlayer;
    private Symbol player;

    public ReplayPlayer(Replay replay) {
        super(null);

        history = replay.getHistory();
        iterator = history.iterator();
        opponentPlayer = new ReplayPlayer(history, iterator, this);
    }

    private ReplayPlayer(List<Move> history, Iterator<Move> iterator, ReplayPlayer opponentPlayer) {
        super(null);
        this.history = history;
        this.iterator = iterator;
        this.opponentPlayer = opponentPlayer;
    }

    @Override
    public synchronized CompletableFuture<Point> nextMove() {
        if (iterator.hasNext()) {
            Move move = iterator.next();
            if (player == null) {
                player = move.getSymbol();
            }
            if (move.getSymbol() == player) {
                return CompletableFuture.completedFuture(move.getLocation());
            } else if (!iterator.hasNext()) {
                return CompletableFuture.completedFuture(null);
            } else {
                return CompletableFuture.completedFuture(iterator.next().getLocation());
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    public ReplayPlayer getOppositePlayer() {
        return opponentPlayer;
    }

    @Override
    public Symbol getSymbol() {
        return player;
    }
}
