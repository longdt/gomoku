package com.solt.game.mcts;


import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Move;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class State {
    private Board board;
    private Symbol playerNo;
    private int visitCount;
    private double winScore;
    private Point move;

    /**
     * construct a State which create new board with a given move
     * @param board
     * @param move
     */
    public State(Board board, Move move) {
        this.board = new Board(board);
        this.board.performMove(move);
        this.move = move.getLocation();
        this.playerNo = move.getSymbol();
    }

    public State(Board board, Symbol playerNo) {
        this.board = new Board(board);
        this.playerNo = playerNo;
    }

    private State(State state) {
        this.board = new Board(state.getBoard());
        this.playerNo = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
        this.move = state.move;
    }

    public State clone() {
        return new State(this);
    }

    public Board getBoard() {
        return board;
    }

    Symbol getPlayerNo() {
        return playerNo;
    }

    Symbol getOpponent() {
        return playerNo.getOpponent();
    }

    public int getVisitCount() {
        return visitCount;
    }

    double getWinScore() {
        return winScore;
    }

    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public List<State> getAllPossibleStates() {
        return this.board.getEmptyPositions().stream().map(p -> new State(board, new Move(p, getOpponent()))).collect(Collectors.toList());
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    public Point getMove() {
        return move;
    }
}