package com.solt.game.player;

import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

import java.util.concurrent.CompletableFuture;

public interface Player {
	CompletableFuture<Point> nextMove();
	Symbol getSymbol();
}
