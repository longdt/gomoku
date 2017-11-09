package com.solt.game;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Symbol;
import com.solt.game.mcts.AllExpander;
import com.solt.game.mcts.MonteCarloTreeSearch;
import com.solt.game.mcts.RandomSimulator;

import java.util.concurrent.CompletableFuture;

public class TestOutOfMemory {
    public static void main(String[] args) {
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(new AllExpander(), new RandomSimulator());
        while (true) {
            CompletableFuture.completedFuture(mcts.findNextMove(new Board(), Symbol.O));

        }
    }
}
