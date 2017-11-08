package com.solt.game.player;

import com.solt.game.gomoku.*;
import com.solt.game.util.GameUtils;
import com.solt.game.util.ReplayDataSetIterator;
import javafx.util.Pair;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.LearningRatePolicy;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class NeuralPlayer extends AIPlayer {
    private Board board;
    private ComputationGraph net;
    private float[] state;
    private String model;

    public NeuralPlayer(Board board, Symbol symbol) {
        this(board, symbol, null);
    }

    public NeuralPlayer(Board board, Symbol symbol, String model) {
        super(symbol);
        if (!board.isStandardSize()) {
            throw new IllegalArgumentException("board must be standard size");
        }
        this.board = board;
        state = new float[board.getSize() * board.getSize()];
        if (model == null) {
            this.net = initComputationGraph(board.getSize(), board.getSize());
        } else {
            try {
                loadModel(model);
            } catch (FileNotFoundException e) {
                net = initComputationGraph(board.getSize(), board.getSize());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.model = model;
        }
    }

    private ComputationGraph initComputationGraph(int width, int height) {
        // learning rate schedule in the form of <Iteration #, Learning Rate>
        Map<Integer, Double> lrSchedule = new HashMap<>();
        lrSchedule.put(0, 0.01);
        lrSchedule.put(1000, 0.003);
        lrSchedule.put(3000, 0.001);

        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
//                .seed(seed)
                .iterations(1) // Training iterations as above
                .regularization(true).l2(0.0005)
                /*
                    Uncomment the following for learning decay and bias
                 */
                .learningRate(.01)//.biasLearningRate(0.02)
                /*
                    Alternatively, you can use a learning rate schedule.

                    NOTE: this LR schedule defined here overrides the rate set in .learningRate(). Also,
                    if you're using the Transfer Learning API, this same override will carry over to
                    your new model configuration.
                */
                .learningRateDecayPolicy(LearningRatePolicy.Schedule)
                .learningRateSchedule(lrSchedule)
                /*
                    Below is an example of using inverse policy rate decay for learning rate
                */
                //.learningRateDecayPolicy(LearningRatePolicy.Inverse)
                //.lrPolicyDecayRate(0.001)
                //.lrPolicyPower(0.75)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.NESTEROVS) //To configure: .updater(new Nesterovs(0.9))
                .graphBuilder()
                .addInputs("input")
                .addLayer("C1", new ConvolutionLayer.Builder(5, 5)
                        //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
                        .nIn(1)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.IDENTITY)
                        .build(), "input")
//                .addLayer("S2", new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
//                        .kernelSize(2, 2)
//                        .stride(2, 2)
//                        .build(), "C1")
                .addLayer("C3", new ConvolutionLayer.Builder(5, 5)
                        //Note that nIn need not be specified in later layers
                        .stride(1, 1)
                        .nOut(50)
                        .activation(Activation.IDENTITY)
                        .build(), "C1")
//                .addLayer("S4", new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
//                        .kernelSize(2, 2)
//                        .stride(2, 2)
//                        .build(), "C3")
                .addLayer("F5", new DenseLayer.Builder().activation(Activation.RELU)
                        .nOut(500).build(), "C3")
                .addLayer("F6", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(height * width)
                        .activation(Activation.SOFTMAX)
                        .build(), "F5")
                .setOutputs("F6")
                .setInputTypes(InputType.convolutionalFlat(height, width, 1)) //See note below
                .backprop(true).pretrain(false)
                .build();

        ComputationGraph model = new ComputationGraph(conf);
        model.init();
        return model;
    }

    public synchronized AIPlayer loadModel(String file) throws IOException {
        net = ModelSerializer.restoreComputationGraph(file);
        return this;
    }

    public synchronized AIPlayer saveModel(String file) throws IOException {
        ModelSerializer.writeModel(net, file, true);
        return this;
    }

    public synchronized AIPlayer saveModel() throws IOException {
        ModelSerializer.writeModel(net, model, true);
        return this;
    }

    @Override
    public synchronized CompletableFuture<Point> nextMove() {
        board.copyStateTo(state);
        if (symbol.getValue() < 0) {
            GameUtils.invertState(state);
        }
        INDArray input = Nd4j.create(state);
        INDArray predictMove = net.outputSingle(input);
        Point bestMove = null;
        float bestScore = Float.MIN_VALUE;
        Point move;
        for (int i = 0; i < predictMove.length(); ++i) {
            float score = predictMove.getFloat(i);
            if (score > bestScore) {
                move = GameUtils.i2p(board, i);
                if (board.isValidMove(move)) {
                    bestMove = move;
                    bestScore = score;
                }
            }
        }
        if (bestMove == null && !board.isGameOver()) {
            List<Point> availables = board.getEmptyPositions();
            bestMove = availables.get(ThreadLocalRandom.current().nextInt(
                    availables.size()));
        }
        return CompletableFuture.completedFuture(bestMove);
    }

    public synchronized List<Pair<Point, Float>> getPromisingMoves(Board board, Symbol player) {
        board.copyStateTo(state);
        if (player.getValue() < 0) {
            GameUtils.invertState(state);
        }
        INDArray input = Nd4j.create(state);
        INDArray predictMove = net.outputSingle(input);
        List<Pair<Point, Float>> result = new ArrayList<>();
        Point move;
        for (int i = 0; i < predictMove.length(); ++i) {
            float score = predictMove.getFloat(i);
            move = GameUtils.i2p(board, i);
            if (board.isValidMove(move)) {
                result.add(new Pair<>(move, score));
            }
        }

        result.sort((p1, p2) -> Float.compare(p2.getValue(), p1.getValue()));
        return result;
    }

    public synchronized void train(String matchs) {
        try {
            ReplayDataSetIterator iter = new ReplayDataSetIterator(181, -1, matchs);
            net.fit(iter);
            Evaluation eval = net.evaluate(iter);
            System.out.println(eval);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void learnLastMatch() {
        if (board.getWonPlayer() != symbol.getOpponent() || !(board instanceof SavableBoard)) {
            return;
        }

        Replay replay = ((SavableBoard) board).getReplay();
        if (replay.getWonPlayer().getValue() < 0) {
            replay = GameUtils.invertReplaySymbol(replay);
        }
        net.fit(new ReplayDataSetIterator(replay));
        Evaluation eval = net.evaluate(new ReplayDataSetIterator(replay));
        System.out.println(eval);
    }
}
