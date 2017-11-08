package com.solt.game.player;

import com.solt.game.gomoku.Symbol;

public abstract class BasePlayer implements Player {
    protected final Symbol symbol;

    public BasePlayer(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }
}
