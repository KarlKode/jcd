package ch.ethz.jcd.main.utils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class VCompressorTest
{
    @Test
    public void test()
    {
        VCompressor compressor = new VCompressor();
        long comp = 0;
        long total = 0;
        int count = 1024;
        byte[] bytes = new byte[count];

        for (int i = 0; i < count; i++)
        {
            for (int k = 0; k < 256; k++)
            {
                byte[] compressed = compressor.compress(bytes);
                byte[] decompressed = compressor.decompress(compressed);
                assertArrayEquals(bytes, decompressed);
                bytes[i] = (byte) (bytes[i] + 1);
                total += bytes.length;
                comp += compressed.length;
            }
            System.out.println(i + " / " + count);
        }

        bytes = new byte[count];

        for (int i = 0; i < 256; i++)
        {
            for (int k = 0; k < count; k++)
            {
                byte[] compressed = compressor.compress(bytes);
                byte[] decompressed = compressor.decompress(compressed);
                assertArrayEquals(bytes, decompressed);
                bytes[k] = (byte) (bytes[k] + 1);
                total += bytes.length;
                comp += compressed.length;
            }
            System.out.println(i + " / " + 256);
        }

        System.out.println("-------------");
        System.out.println("total size: " + total);
        System.out.println("compressed size: " + comp);
        System.out.println("bytes saved: " + (total - comp)/(1024*265*2) );
    }
}
