package com.solt.game.gomoku;

public class Move {
	private final Point location;
	private final Symbol symbol;

	public Move(int x, int y, Symbol symbol) {
		this(new Point(x, y), symbol);
	}
	
	public Move(Point location, Symbol symbol) {
		this.location = location;
		this.symbol = symbol;
	}
	
	public Point getLocation() {
		return location;
	}

	public Symbol getSymbol() {
		return symbol;
	}
}
