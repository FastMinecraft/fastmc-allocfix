package dev.fastmc.allocfix;

import it.unimi.dsi.fastutil.ints.IntComparator;

public class PrimitiveSortHelper implements IntComparator {
    private float[] distanceArray;
    private int[] sortArray;
    private int[] sortSuppArray;

    public void ensureCapacity(int count) {
        if (sortArray == null || sortArray.length < count) {
            sortArray = new int[count + count >> 1];
            sortSuppArray = new int[sortArray.length];
            distanceArray = new float[sortArray.length];
        }
    }

    public float[] getDistanceArray() {
        return distanceArray;
    }

    public int[] getSortArray() {
        return sortArray;
    }

    public int[] getSortSuppArray() {
        return sortSuppArray;
    }

    @Override
    public int compare(int k1, int k2) {
        return Float.compare(distanceArray[k2], distanceArray[k1]);
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return Float.compare(distanceArray[o2], distanceArray[o1]);
    }
}
