package com.solt.game.mcts;


import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSimulator implements Simulator {

    @Override
    public Symbol play(Node node) {
        Node tempNode = node.clone();
        State tempState = tempNode.getState();
        Symbol player = tempState.getPlayerNo();
        Board board = tempState.getBoard();
        while (!tempState.getBoard().isGameOver()) {
            player = player.getOpponent();
            List<Point> availablePositions = board.getEmptyPositions();
            int selectRandom = ThreadLocalRandom.current().nextInt(availablePositions.size());
            board.performMove(availablePositions.get(selectRandom), player);
        }
        return tempState.getBoard().getWonPlayer();
    }
}
