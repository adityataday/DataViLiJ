package clustering;

import algorithms.Clusterer;
import data.DataSet;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClusterer extends Clusterer {

    private static final Random RAND = new Random();


    private DataSet dataset;
    private List<Point2D> centroids;

    private final int maxIterations;
    private final int updateInterval;
    private final AtomicBoolean tocontinue;


    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
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

    @Override
    public void run() {
        int iteration = 0;
        try {
            while (iteration++ < maxIterations) {
                if (Thread.interrupted())
                    throw new InterruptedException();

                assignLabels();

                if (iteration % updateInterval == 0) {

                    HashMap<String, String> newData = new HashMap<>(dataset.getLabels());
                    queue.add(newData);
                    System.out.println("Producer Thread: Insertion in Queue. " + "Current Queue Size is: " + queue.size());

                }

            }
            producerIsIsDone.set(true);
        } catch (InterruptedException ex) {
            System.out.println("Producer Thread: Interrupted");
        }
    }

    private void assignLabels() {
        dataset.getLabels().forEach((key, value) -> dataset.getLabels().replace(key, String.valueOf(RAND.nextInt(numberOfClusters))));

    }
}