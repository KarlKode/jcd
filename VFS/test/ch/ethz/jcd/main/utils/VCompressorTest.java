package ch.ethz.jcd.main.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class VCompressorTest
{
    @Before
    public void setUp()
    {

    }

    @Test
    public void testCompress()
    {
        VCompressor compressor = new VCompressor();
        //byte[] bytes = new byte[]{(byte) 0xF0, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        //byte[] bytes = new byte[]{(byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55};
        //byte[] bytes = new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,  (byte) 0xFE, (byte) 0xFF, (byte) 0xAF, (byte) 0xFF, (byte) 0xCF, (byte) 0xF3};
        //byte[] bytes = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0xFF};
        /*byte[] simple = new byte[]{(byte) 0x1B, (byte) 0x00};
        byte[] a = compressor.compress(simple);
        byte[] b = compressor.decompress(a);
        assertArrayEquals(simple, b);
        */
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

    @Test
    public void testDecompress()
    {

    }
}
