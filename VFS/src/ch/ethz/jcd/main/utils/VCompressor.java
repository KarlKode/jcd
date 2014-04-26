package ch.ethz.jcd.main.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class VCompressor
{
    public static final int DECOMPRESSED_WORD_SIZE = 16; // in bytes
    public static final int COMPRESSED_WORD_SIZE = (Double.SIZE / 8) + 1;

    public byte[] compress(byte[] bytes)
    {
        int len = (int) Math.ceil(bytes.length / DECOMPRESSED_WORD_SIZE) * COMPRESSED_WORD_SIZE;
        byte[] buf = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        int k = 0;

        while (k < bytes.length)
        {
            // probability of 0 in percent
            int probability = 100 - (int) ((bitCount(bytes, k, DECOMPRESSED_WORD_SIZE) / (double) (DECOMPRESSED_WORD_SIZE * 8)) * 100);
            double value = 0.0;

            // probability p of 0 in decimal
            double p = (double) probability / 100;
            double u = 0;
            double v = 1;

            do
            {
                for (int i = 7; i >= 0; i--)
                {
                    int bit = getBit(bytes[k], i);
                    double bound = u + (v - u) * p;

                    if (bit == 0)
                    {
                        v = bound;
                    }
                    else
                    {
                        u = bound;
                    }
                }
                k++;
            }
            while (k % DECOMPRESSED_WORD_SIZE != 0 && k < bytes.length);

            value = u + (v - u) * p;
            // prepend probability
            buffer.put((byte) probability);
            // prepend compressed block
            buffer.putDouble(value);
        }

        return buffer.array();
    }

    public byte[] decompress(byte[] bytes)
    {
        int len = bytes.length / COMPRESSED_WORD_SIZE * DECOMPRESSED_WORD_SIZE;
        byte[] buf = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        int k = 0;

        while (k < bytes.length)
        {
            // probability of 0 in percent
            int probability = (int) bytes[k];
            // increase index
            k++;
            // probability of 0 in decimal
            double p = (double) probability / 100;
            double u = 0;
            double v = 1;
            double value = ByteBuffer.wrap(Arrays.copyOfRange(bytes, k, k + Double.SIZE / 8)).getDouble();

            for (int j = 0; j < DECOMPRESSED_WORD_SIZE; j++)
            {
                int b = 0;

                for (int i = 7; i >= 0; i--)
                {
                    double bound = u + (v - u) * p;

                    if (value <= bound)
                    {
                        v = bound;
                    }
                    else
                    {
                        b += (int) Math.pow(2, i);
                        u = bound;
                    }
                }

                buffer.put((byte) b);
            }
            k += Double.SIZE / 8;
        }

        return buffer.array();
    }

    public static int bitCount(byte[] bytes, int index, int len)
    {
        return BitSet.valueOf(Arrays.copyOfRange(bytes, index, index + len)).cardinality();
    }

    public static int getBit(byte b, int pos)
    {
        return (b >> pos) & 1;
    }
}
