package algorithms;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm {

    protected final int numberOfClusters;

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public Clusterer(int k) {
        if (k < 2)
            k = 2;
        else if (k > 4)
            k = 4;
        numberOfClusters = k;
    }

    public LinkedBlockingQueue<Map<String, String>> getQueue() {
        return queue;
    }

    protected LinkedBlockingQueue<Map<String, String>> queue = new LinkedBlockingQueue();

    public AtomicBoolean producerIsIsDone() {
        return producerIsIsDone;
    }

    public void setproducerIsIsDone(boolean value) {
        producerIsIsDone.set(value);
    }

    protected AtomicBoolean producerIsIsDone = new AtomicBoolean();
}