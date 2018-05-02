package classification;

import algorithms.Classifier;
import data.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    public LinkedBlockingQueue<List<Integer>> getQueue() {
        return queue;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    @Override
    public void run() {
        for (int i = 1; i <= maxIterations; i++) {
            int xCoefficient = new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant = RAND.nextInt(11);

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {

                queue.add(output);

                //For the purpose of internal viewing
                /* System.out.printf("Iteration number %d: ", i); //
                flush();
                 */
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {

                queue.add(output);

                //For the purpose of internal viewing
                /*
                System.out.printf("Iteration number %d: ", i);
                flush();
                 */
                break;
            }
//            try {
//                System.out.println("IN" + " " + queue.size());
//                Thread.sleep(1000);
//
//            } catch (InterruptedException ex) {
//                Logger.getLogger(RandomClassifier.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /**
     * A placeholder main method to just make sure this code runs smoothly
     */
    public static void main(String... args) throws IOException {
        DataSet dataset = DataSet.fromTSDFile(Paths.get("data-vilij/resources/data/sample-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }
}
