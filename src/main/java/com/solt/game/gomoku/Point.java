package com.solt.game.gomoku;


public class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int hashCode() {
    	return (y << 16) ^ x;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Point) {
    		Point p = (Point) obj;
    		return p.y == y && p.x == x;
    	}
    	return false;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}