package com.solt.game.mcts;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Move;
import com.solt.game.gomoku.Symbol;
import com.solt.game.player.AIPlayer;

public class PromisingExpander implements Expander {
    private AIPlayer player;
    private int childNum;

    public PromisingExpander(AIPlayer player) {
        this.player = player;
        childNum = 20;
    }

    @Override
    public void expand(Node node) {
        Board board = node.getState().getBoard();
        Symbol symbol = node.getState().getOpponent();
        player.getPromisingMoves(node.getState().getBoard(), node.getState().getOpponent()).stream().limit(childNum).forEach(
                p -> {
                    State newState = new State(board, new Move(p.getKey(), symbol));
                    Node newNode = new Node(newState);
                    node.addChild(newNode);
                }
        );
    }
}
