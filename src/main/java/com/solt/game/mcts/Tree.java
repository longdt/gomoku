package com.solt.game.mcts;


import com.solt.game.gomoku.Move;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;

public class Tree {
    Node root;

    public Tree() {
        root = new Node();
    }

    public Node getRoot() {
        return root;
    }

    public Node createNewRoot(Point point, Symbol player) {
        Node node = root.getChild(point, player);
        if (node != null) {
            node.parent = null;
            root = node;
        }
        return node;
    }

    public Node createNewRoot(Move move) {
       return createNewRoot(move.getLocation(), move.getSymbol());
    }

    public void setRoot(Node root) {
        this.root = root;
        root.parent = null;
    }

}
