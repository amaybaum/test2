package hw7;

import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans {

    private int k;
    private int n;
    private Instances instances;
    private Instances centroids;
    private List<List<Instance>> clusters;

    KMeans(int k, Instances instances) {
        this.k = k;
        this.n = instances.numInstances();
        this.instances = instances;
        this.centroids = new Instances(instances, k);
        this.clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            clusters.add(new ArrayList<>());
        }
    }

    public void buildClusterModel() {
        initializeCentroids();
        findKMeansCentroids();
    }

    public Instances quantize() {
        Instances quantized = new Instances(instances);
        for (int i = 0; i < n; i++) {
            Instance instance = instances.get(i);
            int centroidIndex = findClosestCentroid(instance);
            Instance centroid = centroids.get(centroidIndex);
            quantized.set(i, centroid);
        }
        return quantized;
    }

    private void initializeCentroids() {
        Instances copyOfInstances = new Instances(instances);
        copyOfInstances.randomize(new Random());
        for (int i = 0; i < k; i++) {
            centroids.add(i, copyOfInstances.get(i));
        }
    }
    

    private void findKMeansCentroids() {
        double error = calcAvgWSSSE();
        double difference = error;
        while (difference > 500) {
            error = calcAvgWSSSE();
            assignment();
            chooseRepresentative();
            double updatedError = calcAvgWSSSE();
            difference = Math.abs(error - updatedError);
            System.out.println(difference);
        }
    }

    private void assignment() {
        for (int i = 0; i < n; i++) {
            Instance instance = instances.get(i);
            int closestCentroid = findClosestCentroid(instance);
            clusters.get(closestCentroid).add(instance);
        }
    }

    private void chooseRepresentative() {
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
            List<Instance> cluster = clusters.get(clusterIndex);
            // Skip first attribute
            for (int attributeIndex = 1; attributeIndex < instances.numAttributes(); attributeIndex++) {
                double sum = 0;
                for (int instanceIndex = 0; instanceIndex < cluster.size(); instanceIndex++) {
                    sum += cluster.get(instanceIndex).value(attributeIndex);
                }
                double average = sum / cluster.size();
                Instance centroid = centroids.get(clusterIndex);
                centroid.setValue(attributeIndex, average);
            }
        }
    }

    private double calcSquaredDistance (Instance instance, Instance centroid) {
        double sum = 0;
        for (int i = 0; i < instance.numAttributes(); i++) {
            double difference = instance.value(i) - centroid.value(i);
            sum += Math.pow(difference, 2);
        }
        return Math.sqrt(sum);
    }

    private int findClosestCentroid(Instance instance) {
        int indexClosestCentroid = 0;
        for (int indexCentroid = 0; indexCentroid < k; indexCentroid++) {
            double distanceCentroid = calcSquaredDistance(instance, centroids.get(indexCentroid));
            double distanceClosestCentroid = calcSquaredDistance(instance, centroids.get(indexClosestCentroid));
            if (distanceCentroid < distanceClosestCentroid) {
                indexClosestCentroid = indexCentroid;
            }
        }
        return indexClosestCentroid;
    }

    private double calcAvgWSSSE() {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            Instance instance = instances.get(i);
            int centroidIndex = findClosestCentroid(instance);
            Instance centroid = centroids.get(centroidIndex);
            double distance = calcSquaredDistance(instance, centroid);
            sum += distance;
        }
        return Math.sqrt(sum);
    }
}
