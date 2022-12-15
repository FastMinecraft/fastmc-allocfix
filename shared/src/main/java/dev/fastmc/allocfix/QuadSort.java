package dev.fastmc.allocfix;

import dev.fastmc.common.BufferUtils;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public final class QuadSort implements IntComparator {
    private final BitSet bitset = new BitSet();
    private float[] distances;
    private int[] sortArray;
    private int[] sortSuppArray;
    private ByteBuffer tempBuffer;

    public void sortQuads(
        ByteBuffer rawBuffer,
        ByteBuffer temp,
        int bufferOffset,
        float cameraX,
        float cameraY,
        float cameraZ,
        int vertexCount,
        int vertexByteSize
    ) {
        if (vertexCount == 0) return;

        int quadByteSize = vertexByteSize * 4;
        int quadCount = vertexCount / 4;

        if (sortArray == null || sortArray.length < quadCount) {
            sortArray = new int[quadCount + quadCount >> 1];
            sortSuppArray = new int[sortArray.length];
            distances = new float[sortArray.length];
        }

        for (int i = 0; i < quadCount; ++i) {
            distances[i] = getDistanceSq(
                rawBuffer,
                cameraX,
                cameraY,
                cameraZ,
                vertexByteSize,
                bufferOffset + i * quadByteSize
            );
            sortArray[i] = i;
            sortSuppArray[i] = i;
        }

        IntArrays.mergeSort(sortArray, 0, quadCount, this, sortSuppArray);

        if (tempBuffer == null || tempBuffer.capacity() < quadByteSize) {
            tempBuffer = BufferUtils.allocateByte(quadByteSize);
            tempBuffer.order(ByteOrder.nativeOrder());
        }

        bitset.clear();
        for (int indexFrom = bitset.nextClearBit(0); indexFrom < quadCount; indexFrom = bitset.nextClearBit(indexFrom + 1)) {
            int indexTo = sortArray[indexFrom];

            if (indexTo != indexFrom) {
                rawBuffer.limit(bufferOffset + indexTo * quadByteSize + quadByteSize);
                rawBuffer.position(bufferOffset + indexTo * quadByteSize);

                tempBuffer.clear();
                tempBuffer.put(rawBuffer);
                tempBuffer.flip();

                int swapToIndex = indexTo;

                for (int swapFromIndex = sortArray[indexTo]; swapToIndex != indexFrom; swapFromIndex = sortArray[swapFromIndex]) {
                    temp.limit(bufferOffset + swapFromIndex * quadByteSize + quadByteSize);
                    temp.position(bufferOffset + swapFromIndex * quadByteSize);

                    rawBuffer.limit(bufferOffset + swapToIndex * quadByteSize + quadByteSize);
                    rawBuffer.position(bufferOffset + swapToIndex * quadByteSize);
                    rawBuffer.put(temp);

                    bitset.set(swapToIndex);
                    swapToIndex = swapFromIndex;
                }

                rawBuffer.limit(bufferOffset + indexFrom * quadByteSize + quadByteSize);
                rawBuffer.position(bufferOffset + indexFrom * quadByteSize);
                rawBuffer.put(tempBuffer);
            }

            bitset.set(indexFrom);
        }
    }

    private static float getDistanceSq(
        ByteBuffer buffer,
        float cameraX,
        float cameraY,
        float cameraZ,
        int vertexByteSize,
        int bufferOffset
    ) {
        float x1 = buffer.getFloat(bufferOffset);
        float y1 = buffer.getFloat(bufferOffset + 4);
        float z1 = buffer.getFloat(bufferOffset + 8);

        float x2 = buffer.getFloat(bufferOffset + vertexByteSize);
        float y2 = buffer.getFloat(bufferOffset + vertexByteSize + 4);
        float z2 = buffer.getFloat(bufferOffset + vertexByteSize + 8);

        float x3 = buffer.getFloat(bufferOffset + vertexByteSize * 2);
        float y3 = buffer.getFloat(bufferOffset + vertexByteSize * 2 + 4);
        float z3 = buffer.getFloat(bufferOffset + vertexByteSize * 2 + 8);

        float x4 = buffer.getFloat(bufferOffset + vertexByteSize * 3);
        float y4 = buffer.getFloat(bufferOffset + vertexByteSize * 3 + 4);
        float z4 = buffer.getFloat(bufferOffset + vertexByteSize * 3 + 8);

        float xDiff = (x1 + x2 + x3 + x4) * 0.25F - cameraX;
        float yDiff = (y1 + y2 + y3 + y4) * 0.25F - cameraY;
        float zDiff = (z1 + z2 + z3 + z4) * 0.25F - cameraZ;

        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    @Override
    public int compare(int k1, int k2) {
        return Float.compare(distances[k2], distances[k1]);
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return Float.compare(distances[o2], distances[o1]);
    }
}
