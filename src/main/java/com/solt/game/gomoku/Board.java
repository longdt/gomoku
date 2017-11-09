package com.solt.game.gomoku;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int DEFAULT_BOARD_SIZE = 11;
    private byte[][] board;
    private Symbol wonPlayer;
    private int numAvailableState;
    private Move lastMove;

    public Board() {
        this(DEFAULT_BOARD_SIZE);
    }

    public Board(int size) {
        board = new byte[size][size];
        numAvailableState = size * size;
    }

    public Board(Board board) {
        this.board = new byte[board.getSize()][board.getSize()];
        for (int r = 0; r < board.getSize(); ++r) {
            for (int c = 0; c < board.getSize(); ++c) {
                this.board[r][c] = board.get(r, c);
            }
        }
        numAvailableState = board.numAvailableState;
        wonPlayer = board.wonPlayer;
    }

    public int getSize() {
        return board.length;
    }

    public boolean isStandardSize() {
        return getSize() == DEFAULT_BOARD_SIZE;
    }

    public synchronized boolean isGameOver() {
        return wonPlayer != null || numAvailableState == 0;
    }

    public synchronized int getTotalMoves() {
        return getSize() * getSize() - numAvailableState;
    }

    public synchronized boolean isValidMove(Point p) {
        return board[p.y][p.x] == 0;
    }

    private synchronized boolean checkWon(Point point, int player) {
        //find chieu ngang
        int counter = 0;
        for (int c = point.x - 1; c >= 0 && board[point.y][c] == player; --c) {
            ++counter;
        }
        for (int c = point.x + 1; c < board[0].length && board[point.y][c] == player; ++c) {
            ++counter;
        }
        if (counter >= 4) {
            return true;
        }
        //find chieu doc
        counter = 0;
        for (int r = point.y - 1; r >= 0 && board[r][point.x] == player; --r) {
            ++counter;
        }
        for (int r = point.y + 1; r < board.length && board[r][point.x] == player; ++r) {
            ++counter;
        }
        if (counter >= 4) {
            return true;
        }
        //find chieu cheo -
        counter = 0;
        for (int r = point.y - 1, c = point.x - 1; r >= 0 && c >= 0 && board[r][c] == player; ++counter) {
            --r;
            --c;
        }
        for (int r = point.y + 1, c = point.x + 1; r < board.length && c < board[0].length && board[r][c] == player; ++counter) {
            ++r;
            ++c;
        }
        if (counter >= 4) {
            return true;
        }
        //find chieu -+
        counter = 0;
        for (int r = point.y - 1, c = point.x + 1; r >= 0 && c < board[0].length && board[r][c] == player; ++counter) {
            --r;
            ++c;
        }
        for (int r = point.y + 1, c = point.x - 1; r < board.length && c >= 0 && board[r][c] == player; ++counter) {
            ++r;
            --c;
        }
        if (counter >= 4) {
            return true;
        }
        return false;
    }

    public synchronized byte get(int r, int c) {
        return board[r][c];
    }

    public synchronized Symbol getWonPlayer() {
        return wonPlayer;
    }

    public synchronized boolean hasXWon() {
        return wonPlayer == Symbol.X;
    }

    public synchronized boolean hasOWon() {
        return wonPlayer == Symbol.O;
    }

    public synchronized Move getLastMove() {
        return lastMove;
    }

    public synchronized List<Point> getEmptyPositions() {
        List<Point> availablePoints = new ArrayList<>();
        for (int r = 0; r < board.length; ++r) {
            for (int c = 0; c < board[0].length; ++c) {
                if (this.board[r][c] != 0) continue;
                availablePoints.add(new Point(c, r));
            }
        }
        return availablePoints;
    }

    public synchronized void performMove(Move move) {
        --numAvailableState;
        lastMove = move;
        Point point = move.getLocation();
        Symbol playerSymbol = move.getSymbol();
        this.board[point.y][point.x] = playerSymbol.getValue();
        if (checkWon(point, playerSymbol.getValue())) {
            wonPlayer = playerSymbol;
        }
    }

    public synchronized void performMove(Point point, Symbol playerSymbol) {
        performMove(new Move(point, playerSymbol));
    }

    public synchronized void undo() {
        if (lastMove == null) {
            return;
        }
        ++numAvailableState;
        wonPlayer = null;
        Point point = lastMove.getLocation();
        lastMove = null;
        this.board[point.y][point.x] = 0;
    }


    @Override
    public synchronized String toString() {
        StringBuilder result = new StringBuilder();
        for (int r = 0; r < board.length; ++r) {
            for (int c = 0; c < board[0].length; ++c) {
                char cell = '_';
                if (this.board[r][c] == Symbol.X.getValue()) {
                    cell = Symbol.X.name().charAt(0);
                } else if (this.board[r][c] == Symbol.O.getValue()) {
                    cell = Symbol.O.name().charAt(0);
                }
                result.append(cell).append(' ');
            }
            result.append('\n');
        }
        return result.toString();
    }

    public synchronized void resetBoard() {
        if (isNewGame()) {
            return;
        }
        int size = getSize();
        for (int r = 0; r < size; ++r) {
            for (int c = 0; c < size; ++c) {
                this.board[r][c] = 0;
            }
        }
        numAvailableState = size * size;
        wonPlayer = null;
        lastMove = null;
    }

    public synchronized int getNumAvailableState() {
        return numAvailableState;
    }

    public synchronized boolean isNewGame() {
        return numAvailableState == board.length * board[0].length;
    }

    public synchronized void copyStateTo(float[] out) {
        int index = 0;
        for (int r = 0; r < board.length; ++r) {
            for (int c = 0; c < board[0].length; ++c) {
                out[index] = board[r][c];
                ++index;
            }
        }
    }

    public synchronized void copyStateTo(int[] out) {
        int index = 0;
        for (int r = 0; r < board.length; ++r) {
            for (int c = 0; c < board[0].length; ++c) {
                out[index] = board[r][c];
                ++index;
            }
        }
    }
}

