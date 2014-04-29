package ch.ethz.jcd.main.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class VCompressorCopy2
{
    public static final int WORD_SIZE = 4; // in bits
    public static final int WORD_COUNT = (int) Math.pow(2, WORD_SIZE);
    public static final int DECOMPRESSED_SEQUENCE_SIZE = 12; // in bytes
    public static final int COMPRESSED_SEQUENCE_SIZE = (Double.SIZE / 8) + WORD_COUNT;

    public byte[] compress(byte[] bytes)
    {
        int len = (int) Math.ceil(bytes.length / DECOMPRESSED_SEQUENCE_SIZE) * COMPRESSED_SEQUENCE_SIZE;
        byte[] buf = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        int k = 0;

        while (k < bytes.length)
        {
            int[] probabilities = probabilities(bytes);
            double[] prob = new double[WORD_COUNT];

            for (int i = 0; i < WORD_COUNT; i++)
            {
                prob[i] = (double) probabilities[i] / 100;
            }

            double lower = 0;
            double upper = 1;

            do
            {
                int highNibble = (bytes[k] >> 4) & 0x0f;
                double L = arraySum(prob, 0, highNibble);
                double U = arraySum(prob, 0, highNibble + 1);
                double S = upper - lower;
                upper = lower + S * U;
                lower = lower + S * L;

                System.out.println("["+lower+":"+upper+")");

                int lowNibble = bytes[k] & 0x0f;
                L = arraySum(prob, 0, lowNibble);
                U = arraySum(prob, 0, lowNibble + 1);
                S = upper - lower;
                upper = lower + S * U;
                lower = lower + S * L;

                System.out.println("["+lower+":"+upper+")");

                k++;
            }
            while (k % DECOMPRESSED_SEQUENCE_SIZE != 0 && k < bytes.length);

            // prepend probability
            for (int i = 0; i < WORD_COUNT; i++)
            {
                buffer.put((byte) probabilities[i]);
            }

            // prepend compressed block
            buffer.putDouble(lower);
        }

        return buffer.array();
    }

    public byte[] decompress(byte[] bytes)
    {
        int len = bytes.length / COMPRESSED_SEQUENCE_SIZE * DECOMPRESSED_SEQUENCE_SIZE;
        byte[] buf = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        int k = 0;

        while (k < bytes.length)
        {
            // probability of 0 in percent
            int[] probabilities = new int[WORD_COUNT];
            double[] prob = new double[WORD_COUNT];

            for (int i = 0; i < WORD_COUNT; i++)
            {
                probabilities[i] = bytes[k];
                // increase index
                k++;
            }

            for (int i = 0; i < WORD_COUNT; i++)
            {
                prob[i] = (double) probabilities[i] / 100;
            }

            double lower = 0;
            double upper = 1;
            double value = ByteBuffer.wrap(Arrays.copyOfRange(bytes, k, k + Double.SIZE / 8)).getDouble();

            for (int j = 0; j < DECOMPRESSED_SEQUENCE_SIZE; j++)
            {
                int highNibble = 0;

                while (highNibble < WORD_COUNT)
                {
                    double p = arraySum(prob, 0, highNibble + 1);
                    double u = lower + (upper - lower) * p;
                    if(value < u) break;
                    highNibble++;
                }

                double L = arraySum(prob, 0, highNibble);
                double U = arraySum(prob, 0, highNibble + 1);
                double S = upper - lower;
                upper = lower + S * U;
                lower = lower + S * L;
                System.out.println("["+lower+":"+upper+")");

                int lowNibble = 0;

                while (lowNibble < WORD_COUNT)
                {
                    double p = arraySum(prob, 0, lowNibble + 1);
                    double u = lower + (upper - lower) * p;
                    if(value < u) break;
                    lowNibble++;
                }

                L = arraySum(prob, 0, lowNibble);
                U = arraySum(prob, 0, lowNibble + 1);
                S = upper - lower;
                upper = lower + S * U;
                lower = lower + S * L;
                System.out.println("["+lower+":"+upper+")");
                int b = (highNibble << 4) + lowNibble;
                buffer.put((byte) b);
            }
            k += Double.SIZE / 8;
        }

        return buffer.array();
    }

    public int[] probabilities(byte[] bytes)
    {
        int[] prob = new int[WORD_COUNT];

        for (byte b : bytes)
        {
            int lowNibble = b & 0x0f;
            int highNibble = (b >> 4) & 0x0f;
            prob[lowNibble]++;
            prob[highNibble]++;
        }

        for (int i = 0; i < prob.length; i++)
        {
            prob[i] = (int) ((double) prob[i] / ((double) bytes.length * (8 / WORD_SIZE)) * 100);
        }

        return prob;
    }

    public double arraySum(double[] array, int start, int end)
    {
        double sum = 0;

        for (int i = start; i < end; i++)
        {
            sum += array[i];
        }
        return sum;
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
