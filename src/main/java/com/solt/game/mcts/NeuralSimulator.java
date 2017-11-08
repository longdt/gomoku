package com.solt.game.mcts;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;
import com.solt.game.player.AIPlayer;
import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NeuralSimulator implements Simulator {
    private static int TOP_MOVE = 5;
    private AIPlayer aiPlayer;

    public NeuralSimulator(AIPlayer aiPlayer) {
        this.aiPlayer = aiPlayer;
    }

    @Override
    public Symbol play(Node node) {
        Node tempNode = node.clone();
        State tempState = tempNode.getState();
        Symbol player = tempState.getPlayerNo();
        Board board = tempState.getBoard();
        while (!tempState.getBoard().isGameOver()) {
            player = player.getOpponent();
            List<Pair<Point, Float>> promisingMoves = aiPlayer.getPromisingMoves(board, player);
            int selectRandom = ThreadLocalRandom.current().nextInt(promisingMoves.size() > TOP_MOVE ? TOP_MOVE : promisingMoves.size());
            board.performMove(promisingMoves.get(selectRandom).getKey(), player);
        }
        return tempState.getBoard().getWonPlayer();
    }
}
