package dev.fastmc.allocfix;

public class PrimitiveSortHelper {
    private float[] distanceArray;
    private int[] sortArray;

    public void ensureCapacity(int count) {
        if (sortArray == null || sortArray.length < count) {
            sortArray = new int[count + count >> 1];
            distanceArray = new float[sortArray.length];
        }
    }

    public float[] getDistanceArray() {
        return distanceArray;
    }

    public int[] getSortArray() {
        return sortArray;
    }
}
