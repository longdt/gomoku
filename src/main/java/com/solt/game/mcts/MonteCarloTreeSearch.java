package com.solt.game.mcts;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Point;
import com.solt.game.gomoku.Symbol;
import com.solt.game.util.GameUtils;


public class MonteCarloTreeSearch {
    private static final int WIN_BONUS = 10;
    private static final int WIN_SCORE = 10000000;
    private static final int LOSE_SCORE = -10000000;
    private final int numRound;
    private int level;
    private Tree tree;
    private Expander expander;
    private Simulator simulator;

    public MonteCarloTreeSearch(Expander expander, Simulator simulator) {
        this(expander, simulator, 40000);
    }

    public MonteCarloTreeSearch(Expander expander, Simulator simulator, int numRound) {
        this.level = 150;
        tree = new Tree();
        this.expander = expander;
        this.simulator = simulator;
        this.numRound = numRound;
    }

    private int getMillisForCurrentLevel() {
        return 2 * (this.level - 1) + 1;
    }

    public synchronized Point findNextMove(Board board, Symbol playerNo) {
        long start = System.currentTimeMillis();
        long end = start + 60 * getMillisForCurrentLevel();

        Symbol opponent = playerNo.getOpponent();
        Node rootNode = board.isNewGame() ? null : tree.createNewRoot(board.getLastMove());
        if (rootNode == null) {
            rootNode = new Node(new State(board, opponent));
        }
        if (!GameUtils.equals(board, rootNode.getState().getBoard())) {
            System.err.println("ERROR");
            System.exit(1);
        }
        int counter = 0;
        while (counter++ < numRound || System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
            // Phase 2 - Expansion
            if (!promisingNode.getState().getBoard().isGameOver())
                expander.expand(promisingNode);

            // Phase 3 - Simulation
            Node nodeToExplore = simulator.selectChild(promisingNode);
            Symbol playoutResult = nodeToExplore.getState().getBoard().getWonPlayer();
            if (playoutResult == opponent) {
                nodeToExplore.getParent().getState().addScore(LOSE_SCORE);
            } else if (playoutResult == playerNo) {
                nodeToExplore.getState().addScore(WIN_SCORE);
            } else {
                playoutResult = simulator.play(nodeToExplore);
            }
            // Phase 4 - Update
            backPropogation(nodeToExplore, playoutResult);
//            System.out.println("num loop: " + counter);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getMove();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildren() != null) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void backPropogation(Node nodeToExplore, Symbol playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null && tempNode != tree.getRoot()) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_BONUS);
            tempNode = tempNode.getParent();
        }
    }

    public synchronized void reset() {
        tree = new Tree();
    }

}

