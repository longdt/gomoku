package com.solt.game.gomoku;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    void getSize() {
        assertEquals(new Board().getSize(), Board.DEFAULT_BOARD_SIZE);
        assertEquals(new Board(11).getSize(), 11);
        assertEquals(new Board(32).getSize(), 32);
        assertEquals(new SavableBoard().getSize(), Board.DEFAULT_BOARD_SIZE);
    }

    @Test
    void isStandardSize() {
        assertTrue(new Board().isStandardSize());
        assertTrue(new SavableBoard().isStandardSize());
        Board board = new Board(11);
        assertEquals(board.isStandardSize(), board.getSize() == Board.DEFAULT_BOARD_SIZE);
    }

    public static void randFill(Board board) {
        List<Point> empties = board.getEmptyPositions();
        Point p = empties.get(ThreadLocalRandom.current().nextInt(empties.size()));
        Move move = board.getLastMove();
        Symbol player = move == null ? Symbol.O : move.getSymbol().getOpponent();
        board.performMove(p, player);
    }

    @Test
    void isGameOver() {
        Board board = new Board(11);
        assertFalse(board.isGameOver());
        randFill(board);
        assertFalse(board.isGameOver());
        randFill(board);
        assertFalse(board.isGameOver());
        randFill(board);
        assertFalse(board.isGameOver());
        randFill(board);
        assertFalse(new Board(11).isGameOver());
    }

    @Test
    void getTotalMoves() {
        Board board = new Board(11);
        assertEquals(board.getTotalMoves(), 0);
        randFill(board);
        assertEquals(board.getTotalMoves(), 1);
        randFill(board);
        assertEquals(board.getTotalMoves(), 2);
        randFill(board);
        assertEquals(board.getTotalMoves(), 3);
        randFill(board);
        assertEquals(board.getTotalMoves(), 4);
    }

    @Test
    void isValidMove() {
        Board board = new Board(11);
        assertTrue(board.isValidMove(new Point(0, 0)));
        assertTrue(board.isValidMove(new Point(0, 10)));
        assertTrue(board.isValidMove(new Point(10, 0)));
        assertTrue(board.isValidMove(new Point(5, 5)));
        randFill(board);
        assertFalse(board.isValidMove(board.getLastMove().getLocation()));
    }

    @Test
    void get() {
    }

    @Test
    void getWonPlayer() {
    }

    @Test
    void hasXWon() {
    }

    @Test
    void hasOWon() {
    }

    @Test
    void getLastMove() {
    }

    @Test
    void getEmptyPositions() {
    }

    @Test
    void performMove() {
    }

    @Test
    void performMove1() {
    }

    @Test
    void undo() {
    }

    @Test
    void btoString() {
    }

    @Test
    void resetBoard() {
    }

    @Test
    void getNumAvailableState() {
    }

    @Test
    void isNewGame() {
    }

    @Test
    void copyStateTo() {
    }

    @Test
    void copyStateTo1() {
    }

}