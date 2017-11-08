package com.solt.game.mcts;

import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Node {
    State state;
    Node parent;
    List<Node> children;

    public Node() {
    }

    public Node(State state) {
        this.state = state;
    }

    private Node(Node node) {
        this.parent = node.getParent();
        if (node.getState() != null) {
            this.state = node.getState().clone();
        }
        if (node.getChildren() != null) {
            this.children = new ArrayList<>();
            for (Node child : node.getChildren()) {
                this.children.add(new Node(child));
            }
        }
    }

    public Node clone() {
        return new Node(this);
    }

    public State getState() {
        return state;
    }


    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getChild(Point point, Symbol player) {
        if (children == null) {
            return null;
        }
        for (Node child : children) {
            if (child.getState().getMove().equals(point) && child.getState().getPlayerNo() == player) {
                return child;
            }
        }
        return null;
    }

    public Node getChildWithMaxScore() {
        return Collections.max(this.children, Comparator.comparing(c -> (c.getState().getWinScore() + 1.0) / (c.getState().getVisitCount() +  1.0)));
    }

    public void addChild(Node child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.parent = this;
    }
}
