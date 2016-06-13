package hw7;

import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Random;

public class KMeans {

    private int k;
    private Instances instances;
    private Instances centroids;
    private ArrayList<Instances> clusters;

    KMeans(int k, Instances instances) {
        this.k = k;
        this.instances = instances;
        this.centroids = new Instances(instances, k);
        this.clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            clusters.add(new Instances(instances, instances.numInstances()));
        }
    }

    public void buildClusterModel() {
        initializeCentroids();
        findKMeansCentroids();
    }

    private void initializeCentroids() {
        instances.randomize(new Random());
        for (int i = 0; i < k; i++) {
            centroids.add(i, instances.get(i));
        }
    }

    private void findKMeansCentroids() {
        for (int i = 0; i < 40; i++) {
            assignment();
            chooseRepresentative();
            //quantize();
        }
    }

    private void assignment() {
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.get(i);
            int closestCentroid = findClosestCentroid(instance);
            clusters.get(closestCentroid).add(instance);
        }
    }

    private void chooseRepresentative() {
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
            Instances cluster = clusters.get(clusterIndex);
            // Skip first attribute
            for (int attributeIndex = 1; attributeIndex < cluster.numAttributes(); attributeIndex++) {
                double sum = 0;
                for (int instanceIndex = 0; instanceIndex < cluster.numInstances(); instanceIndex++) {
                    sum += cluster.get(instanceIndex).value(attributeIndex);
                }
                double average = sum / cluster.numInstances();
                centroids.get(clusterIndex).setValue(attributeIndex, average);
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

    private void quantize() {

    }
}
