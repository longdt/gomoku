package com.solt.game.mcts;

import com.solt.game.gomoku.Symbol;

import java.util.concurrent.ThreadLocalRandom;

public interface Simulator {
    Symbol play(Node node);

    default Node selectChild(Node node) {
        if (node.getChildren() == null) {
            //never occur
            return node;
        }
        return node.getChildren().get(ThreadLocalRandom.current().nextInt(node.getChildren().size()));
    }
}
