package com.solt.game.util;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Replay;
import com.solt.game.gomoku.Symbol;
import javafx.util.Pair;

public class GameUtils {
    public static Point i2p(Board board, int index) {
        return new Point(index % board.getSize(), index / board.getSize());
    }

    public static int p2i(Board board, Point point) {
        return point.y * board.getSize() + point.x;
    }

    public static void invertState(float[] state) {
        for (int i = 0; i < state.length; ++i) {
            if (state[i] != 0) {
                state[i] = -state[i];
            }
        }
    }

    public static Replay invertReplaySymbol(Replay replay) {
        Replay result = new Replay();
        result.setWonPlayer(replay.getWonPlayer() == Symbol.O ? Symbol.X : Symbol.O);
        replay.getHistory().forEach(p -> result.addMove(p.getLocation(), p.getSymbol() == Symbol.O ? Symbol.X : Symbol.O));
        return result;
    }

    public static Pair<Point, Point> findROI(Board board) {
        int topX = board.getSize(), topY = board.getSize(), botX = 0, botY = 0;
        for (int r = 0; r < board.getSize(); ++r) {
            for (int c = 0; c < board.getSize(); ++c) {
                if (board.get(r, c) == 0) {
                    continue;
                }
                if (r < topY) {
                    topY = r;
                }
                if (r > botY) {
                    botY = r;
                }
                if (c < topX) {
                    topX = c;
                }
                if (c > botX) {
                    botX = c;
                }
            }
        }
        return new Pair<>(new Point(topX, topY), new Point(botX, botY));
    }

    public static boolean equals(Board b1, Board b2) {
        if (b1.getSize() != b2.getSize()) {
            return false;
        }
        for (int r = 0; r < b1.getSize(); ++r) {
            for (int c = 0; c < b1.getSize(); ++c) {
                if (b1.get(r, c) != b2.get(r, c)) {
                    return false;
                }
            }
        }
        return true;
    }
}
