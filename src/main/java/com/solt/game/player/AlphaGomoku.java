package com.solt.game.player;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;
import com.solt.game.mcts.MonteCarloTreeSearch;
import com.solt.game.mcts.NeuralSimulator;
import com.solt.game.mcts.PromisingExpander;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AlphaGomoku extends AIPlayer {
    private MctsPlayer mctsPlayer;
    private AIPlayer aiPlayer;

    public AlphaGomoku(Board board, Symbol symbol, String model) {
        super(symbol);
        aiPlayer = new NeuralPlayer(board, symbol, model);
        mctsPlayer = new MctsPlayer(board, symbol, new MonteCarloTreeSearch(new PromisingExpander(aiPlayer), new NeuralSimulator(aiPlayer), 500));
//        mctsPlayer = new MctsPlayer(board, symbol, new MonteCarloTreeSearch(new PromisingExpander(aiPlayer), new RandomSimulator()));
    }

    @Override
    public synchronized List<Pair<Point, Float>> getPromisingMoves(Board board, Symbol player) {
        return aiPlayer.getPromisingMoves(board, player);
    }

    @Override
    public synchronized void learnLastMatch() {
        aiPlayer.learnLastMatch();
    }

    @Override
    public synchronized AIPlayer saveModel() throws IOException {
        return aiPlayer.saveModel();
    }

    @Override
    public synchronized CompletableFuture<Point> nextMove() {
        return mctsPlayer.nextMove();
    }
}
