package com.solt.game.player;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;
import com.solt.game.mcts.AllExpander;
import com.solt.game.mcts.MonteCarloTreeSearch;
import com.solt.game.mcts.RandomSimulator;

import java.util.concurrent.CompletableFuture;

public class MctsPlayer extends BasePlayer {
    private MonteCarloTreeSearch mcts;
    private Board board;

    public MctsPlayer(Board board, Symbol symbol) {
        this(board, symbol, new MonteCarloTreeSearch(new AllExpander(), new RandomSimulator()));
    }

    public MctsPlayer(Board board, Symbol symbol, MonteCarloTreeSearch mcts) {
        super(symbol);
        this.board = board;
        this.mcts = mcts;
    }

    @Override
    public synchronized CompletableFuture<Point> nextMove() {
        if (board.isNewGame() || board.getTotalMoves() == 1) {
            mcts.reset();
        }
        if (board.isNewGame()) {
            return CompletableFuture.completedFuture(new Point(board.getSize() / 2, board.getSize() / 2));
        }
        return CompletableFuture.completedFuture(mcts.findNextMove(board, symbol)).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
}
