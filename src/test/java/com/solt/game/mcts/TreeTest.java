package com.solt.game.mcts;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.BoardTest;
import com.solt.game.gomoku.Symbol;
import com.solt.game.util.GameUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TreeTest {

    @Test
    void createNewRoot() {
        Board board = new Board();
        Tree tree = new Tree();
        Node rootNode = board.isNewGame() ? null : tree.createNewRoot(board.getLastMove());
        if (rootNode == null) {
            Symbol opponent = board.getLastMove() == null ? Symbol.X : board.getLastMove().getSymbol().getOpponent();
            rootNode = new Node(new State(board, opponent));
        }
        assertTrue(GameUtils.equals(board, rootNode.getState().getBoard()));
        tree.setRoot(rootNode);
        BoardTest.randFill(board);

        rootNode = board.isNewGame() ? null : tree.createNewRoot(board.getLastMove());
        if (rootNode == null) {
            Symbol opponent = board.getLastMove() == null ? Symbol.X : board.getLastMove().getSymbol().getOpponent();
            rootNode = new Node(new State(board, opponent));
        }
        assertTrue(GameUtils.equals(board, rootNode.getState().getBoard()));
        tree.setRoot(rootNode);
        BoardTest.randFill(board);

        rootNode = board.isNewGame() ? null : tree.createNewRoot(board.getLastMove());
        if (rootNode == null) {
            Symbol opponent = board.getLastMove() == null ? Symbol.X : board.getLastMove().getSymbol().getOpponent();
            rootNode = new Node(new State(board, opponent));
        }
        assertTrue(GameUtils.equals(board, rootNode.getState().getBoard()));
        tree.setRoot(rootNode);
        BoardTest.randFill(board);


    }

    @Test
    void createNewRoot1() {
    }

}