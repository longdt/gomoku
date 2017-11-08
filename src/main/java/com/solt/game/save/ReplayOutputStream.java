package com.solt.game.save;

import com.solt.game.gomoku.Move;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Replay;
import com.solt.game.gomoku.Symbol;
import javafx.util.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReplayOutputStream implements Closeable {
    static final byte SYMBOL_MASK = (byte) (1 << 7);
    static final byte VALUE_MASK = Byte.MAX_VALUE;
    private OutputStream out;

    public ReplayOutputStream(OutputStream out) {
        this.out = out;
    }


    private int symbol2BitIndicator(Symbol symbol) {
        return symbol == Symbol.X ? SYMBOL_MASK : 0;
    }

    public void write(Replay replay) throws IOException {
        List<Move> history = replay.getHistory();
        int size = (history.size() * 2);
        int fistByte = (size >> 8) | symbol2BitIndicator(replay.getWonPlayer());
        int secondByte = size & VALUE_MASK;
        out.write(fistByte);
        out.write(secondByte);
        Point p;
        Symbol symbol;
        for (Move move : history) {
            p = move.getLocation();
            symbol = move.getSymbol();
            out.write(symbol2BitIndicator(symbol) | p.x);
            out.write(p.y);
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
