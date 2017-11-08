package com.solt.game.player;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;

public abstract class AIPlayer extends BasePlayer {

    public AIPlayer(Symbol symbol) {
        super(symbol);
    }

    public abstract List<Pair<Point, Float>> getPromisingMoves(Board board, Symbol player);

    public abstract void learnLastMatch();

    public abstract AIPlayer saveModel() throws IOException;
}
