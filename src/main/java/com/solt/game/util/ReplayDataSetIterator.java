package com.solt.game.util;

import com.solt.game.gomoku.Board;
import com.solt.game.gomoku.Move;
import com.solt.game.gomoku.Replay;
import com.solt.game.save.ReplayInputStream;
import javafx.util.Pair;
import org.deeplearning4j.datasets.fetchers.BaseDataFetcher;
import org.deeplearning4j.datasets.iterator.BaseDatasetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ReplayDataSetIterator extends BaseDatasetIterator {
    private static final int DEFAULT_NUM_OUTCOMES = Board.DEFAULT_BOARD_SIZE * Board.DEFAULT_BOARD_SIZE;

    public ReplayDataSetIterator(int batch, int numExamples, String matchs) throws IOException {
        super(batch, numExamples, new ReplayDataFetcher(matchs));
    }

    public ReplayDataSetIterator(Replay replay) {
        super(181, -1, new ReplayDataFetcher(replay));
    }

    private static class ReplayDataFetcher extends BaseDataFetcher {
        private List<Replay> replays;
        private Iterator<Replay> replayIterator;
        private Iterator<Pair<float[], Integer>> moveIterator;

        public ReplayDataFetcher(String matchs) throws IOException {
            this(loadReplays(matchs));
        }

        public ReplayDataFetcher(Replay replay) {
            this(Collections.singletonList(replay));
        }

        public ReplayDataFetcher(List<Replay> replays) {
            this.replays = replays;
            computeTotalExample();
            numOutcomes = DEFAULT_NUM_OUTCOMES;
            inputColumns = DEFAULT_NUM_OUTCOMES;
        }

        private static List<Replay> loadReplays(String matchs) throws IOException {
            List<Replay> result = new ArrayList<>();
            try (ReplayInputStream ris = new ReplayInputStream(new BufferedInputStream(new FileInputStream(matchs)))) {
                Replay replay;
                while ((replay = ris.read()) != null) {
                    if (replay.getWonPlayer().getValue() < 0) {
                        replay = GameUtils.invertReplaySymbol(replay);
                    }
                    result.add(replay);
                }
            }
            return result;
        }

        private void computeTotalExample() {
            int total = 0;
            for (Replay replay : replays) {
                total += (replay.getHistory().size() + 1) / 2;
            }
            totalExamples = total;
        }

        @Override
        public void fetch(int numExamples) {
            if (!hasMore()) {
                throw new IllegalStateException("Unable to getFromOrigin more; there are no more images");
            }
            float[][] featureData = new float[numExamples][0];
            float[][] labelData = new float[numExamples][0];

            int actualExamples = 0;
            if (replayIterator == null) {
                replayIterator = replays.iterator();
            }

            for (int i = 0; i < numExamples; i++, cursor++) {
                if (!hasMore())
                    break;
                if (moveIterator == null || !moveIterator.hasNext()) {
                    moveIterator = new MoveReplayIterator(replayIterator.next());
                }
                Pair<float[], Integer> data = moveIterator.next();
                int label = data.getValue();
                featureData[actualExamples] = data.getKey();
                labelData[actualExamples] = new float[numOutcomes];
                labelData[actualExamples][label] = 1.0f;
                actualExamples++;
            }

            if (actualExamples < numExamples) {
                featureData = Arrays.copyOfRange(featureData, 0, actualExamples);
                labelData = Arrays.copyOfRange(labelData, 0, actualExamples);
            }

            INDArray features = Nd4j.create(featureData);
            INDArray labels = Nd4j.create(labelData);
            curr = new DataSet(features, labels);
        }

        @Override
        public void reset() {
            cursor = 0;
            curr = null;
            replayIterator = null;
            moveIterator = null;
        }
    }

    private static class MoveReplayIterator implements Iterator<Pair<float[], Integer>> {
        private final Iterator<Move> iterator;
        private final Board boardState = new Board();

        public MoveReplayIterator(Replay replay) {
            iterator = replay.getHistory().iterator();
            if (replay.getHistory().size() % 2 == 0) {
                moveLoser();
            }

        }

        private void moveLoser() {
            boardState.performMove(iterator.next());
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Pair<float[], Integer> next() {
            float[] data = new float[DEFAULT_NUM_OUTCOMES];
            boardState.copyStateTo(data);
            Move move = iterator.next();
            if (hasNext()) {
                moveLoser();
            }
            return new Pair<>(data, GameUtils.p2i(boardState, move.getLocation()));
        }


    }
}
