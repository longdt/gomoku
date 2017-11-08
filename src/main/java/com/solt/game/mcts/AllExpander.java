package com.solt.game.mcts;

public class AllExpander implements Expander {

    @Override
    public void expand(Node node) {
        node.getState().getAllPossibleStates().forEach(state -> {
            Node newNode = new Node(state);
            node.addChild(newNode);
        });
    }
}
