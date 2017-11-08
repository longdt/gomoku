package com.solt.game.gomoku;


public enum Symbol {
    O((byte) 1),
    X((byte)-1);

    private byte value;
    
    Symbol(byte value) {
    	this.value = value;
	}
    
    public byte getValue() {
		return value;
	}

    public Symbol getOpponent() {
        return value == O.value ? X : O;
    }
}

