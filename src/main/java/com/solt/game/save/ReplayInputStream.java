package com.solt.game.save;

import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Replay;
import com.solt.game.gomoku.Symbol;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class ReplayInputStream implements Closeable {
    private static final short WON_MASK = (short) (1 << 15);
    private static final short SIZE_MASK = Short.MAX_VALUE >> 1;
    private InputStream in;

    public ReplayInputStream(InputStream in) {
        this.in = in;
    }

    private Symbol bitIndicator2Symbol(int bitIndicator) {
        return bitIndicator == 0 ? Symbol.O : Symbol.X;
    }

    public Replay read() throws IOException {
        int firstByte = in.read();
        if (firstByte == -1) {
            return null;
        }
        Symbol wonPlayer = bitIndicator2Symbol(firstByte & ReplayOutputStream.SYMBOL_MASK);
        int secondByte = in.read();
        if (secondByte == -1) {
            return null;
        }
        int size = ((firstByte & ReplayOutputStream.VALUE_MASK) << 8) | secondByte;
        Replay replay = new Replay();
        replay.setWonPlayer(wonPlayer);
        for (int i = 0; i < size; i += 2) {
            firstByte = in.read();
            if (firstByte == -1) {
                return null;
            }
            Symbol player = bitIndicator2Symbol(firstByte & ReplayOutputStream.SYMBOL_MASK);
            int x = firstByte & ReplayOutputStream.VALUE_MASK;
            int y = in.read();
            if (y == -1) {
                return null;
            }
            replay.addMove(new Point(x, y), player);
        }
        return replay;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
