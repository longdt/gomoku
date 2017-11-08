package com.solt.game.gomoku;

import java.io.IOException;

public class SavableBoard extends Board {
    private Replay replay;

    public SavableBoard() {
        replay = new Replay();
    }

    @Override
    public synchronized void performMove(Point point, Symbol playerSymbol) {
        super.performMove(point, playerSymbol);
        replay.addMove(point, playerSymbol);
        if (getWonPlayer() != null) {
            replay.setWonPlayer(getWonPlayer());
        }
    }

    @Override
    public synchronized void undo() {
        if (getLastMove() != null) {
            super.undo();
            replay.undo();
        }

    }

    @Override
    public synchronized void resetBoard() {
        super.resetBoard();
        replay.reset();
    }

    public synchronized Replay getReplay() {
        return replay;
    }

    public synchronized void saveMatch(String filesave) throws IOException {
        replay.saveMatch(filesave);
    }
}
