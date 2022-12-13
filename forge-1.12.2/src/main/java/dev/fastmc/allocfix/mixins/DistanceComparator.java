package dev.fastmc.allocfix.mixins;

import it.unimi.dsi.fastutil.ints.IntComparator;

public final class DistanceComparator implements IntComparator {
    private float[] distances = new float[0];

    @Override
    public int compare(int k1, int k2) {
        return Float.compare(distances[k2], distances[k1]);
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return Float.compare(distances[o2], distances[o1]);
    }

    public void setDistances(float[] distances) {
        this.distances = distances;
    }

    public float[] getDistances() {
        return distances;
    }
}
