package ch.ethz.jcd.main.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class implements entropy compression using the Huffan coding algorithm
 * with a block size of two bits.
 */
public class VCompressor
{
    /**
     * Code values used according to the occurrence probability
     * e.g.
     *                              0
     * 00 - 0.4 ------------------------+------+
     *                                  |
     *                      10      1   |
     * 11 - 0.3 ----------------+-------+
     *                          |
     *              110     11  |
     * 01 - 0.2 --------+-------+
     *                  |
     *              111 |
     * 10 - 0.1 --------+
     */
    public static final int[] CODE_BOOK = {0, 2, 6, 7};

    /**
     * Compresses the given byte array
     *
     * @param bytes to compress
     *
     * @return compressed bytes
     */
    public byte[] compress(byte[] bytes)
    {
        Dictionary dict = new Dictionary(bytes);
        BitCompressStream bits = new BitCompressStream();

        for (byte b : bytes)
        {
            int i = 4;

            while (i > 0)
            {
                i--;
                int word = (b >> 2 * i) & 0x03;
                bits.append((byte) dict.encode(word));
            }
        }

        byte[] buf = new byte[9 + bits.size()];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        buffer.putInt(buffer.array().length);
        buffer.putInt(bytes.length);
        buffer.put(dict.toByte());
        buffer.put(bits.bytes());
        return buffer.array();
    }

    /**
     * Decompresses the given byte array
     *
     * @param bytes to decompress
     *
     * @return decompressed bytes
     */
    public byte[] decompress(byte[] bytes)
    {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        // length of compressed byte array
        buffer.getInt();
        // length of the decompressed byte array
        int len = buffer.getInt();
        Dictionary dict = new Dictionary(buffer.get());
        BitDecompressStream bits = new BitDecompressStream(Arrays.copyOfRange(bytes, 9, bytes.length));
        byte[] out = new byte[len];

        for (int q = 0; !bits.hasNext() && q < len; q++)
        {
            byte b = 0;

            for (int i = 3; i >= 0 && !bits.hasNext(); i--)
            {
                int code = 0;
                int k = 0;
                do
                {
                    int value = bits.next() ? 1 : 0;
                    code = (code << 1) | value;
                    k++;
                }
                while (!dict.isCode(code) && k < 3);

                b = (byte) ((b << 2) | dict.decode(code));
            }
            out[q] = b;
        }
        return out;
    }

    /**
     * Bit stream of the compressed bytes
     */
    private class BitDecompressStream implements Iterator<Boolean>
    {
        private LinkedList<Boolean> bits = new LinkedList<>();

        public BitDecompressStream(byte[] bytes)
        {
            for (byte b : bytes)
            {
                for (int i = 7; i >= 0; i--)
                {
                    boolean value = ((b >> i) & 1) == 1 ? true : false;
                    bits.add(value);
                }
            }
        }

        /**
         * @return next bit
         */
        public Boolean next()
        {
            return bits.pop();
        }

        /**
         * @return true if the stream is not empty
         */
        public boolean hasNext()
        {
            return !(bits.size() > 0);
        }

        /**
         * not implemented
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Bit stream of bytes being compressed
     */
    private class BitCompressStream
    {
        private LinkedList<Boolean> bits = new LinkedList<>();

        /**
         * appends the given code to the stream
         *
         * @param word to append
         */
        public void append(byte word)
        {
            for (int i = 7; i >= 0; i--)
            {
                boolean value = ((word >> i) & 1) == 1 ? true : false;

                if (value || i == 0)
                {
                    bits.add(value);
                }
            }
        }

        /**
         * @return size of the bit set in bytes
         */
        public int size()
        {
            int value = (bits.size() % 8) != 0 ? 8 - (bits.size() % 8) : 0;
            return (bits.size() + value) / 8;
        }

        /**
         * maps the bit stream into a byte array
         *
         * @return byte array
         */
        public byte[] bytes()
        {
            byte[] bytes = new byte[size()];
            int i = 0;
            Iterator<Boolean> it = bits.iterator();

            while (it.hasNext())
            {
                int k = 8;
                byte b = 0;

                while (it.hasNext() && k > 0)
                {
                    k--;
                    boolean bit = it.next();
                    int value = bit ? 1 : 0;
                    b = (byte) ((value << k) | b);
                }

                bytes[i] = b;
                i++;
            }
            return bytes;
        }
    }

    /**
     * Dictionary to provide mapping of the Huffman codes
     */
    private class Dictionary
    {
        private int[] dictionary = new int[4];
        private int[] order = new int[4];

        /**
         * Instantiate a new Dictionary and generate the mapping based on a
         * statistical analysis of the bytes to being compressed.
         *
         * @param bytes to compress
         */
        public Dictionary(byte[] bytes)
        {
            int[] count = new int[4];

            for (byte b : bytes)
            {
                for (int i = 0; i < 4; i++)
                {
                    int word = (b >> 2 * i) & 0x03;
                    count[word]++;
                }
            }

            for (int i = 0; i < 4; i++)
            {
                int max = 0;

                for (int j = 0; j < 4; j++)
                {
                    if (count[j] > count[max])
                    {
                        max = j;
                    }
                }
                count[max] = -1;
                dictionary[max] = CODE_BOOK[i];
                order[i] = max;
            }
        }

        /**
         * Instantiate a new Dictionary based on the given one
         *
         * @param b given dictionary
         */
        public Dictionary(byte b)
        {
            for (int i = 0; i < 4; i++)
            {
                int word = (b >> 2 * i) & 0x03;
                dictionary[word] = CODE_BOOK[i];
            }
        }

        /**
         * @return dictionary represented as single byte
         */
        public byte toByte()
        {
            byte b = 0;

            for (int i = 0; i < 4; i++)
            {
                b |= (byte) (order[i] << (2 * i));
            }
            return b;
        }

        /**
         * @param word to encode
         *
         * @return code that maps to the given word
         */
        public int encode(int word)
        {
            return dictionary[word];
        }

        /**
         * @param code to decode
         *
         * @return word decoded from the given code
         */
        public int decode(int code)
        {
            for (int i = 0; i < dictionary.length; i++)
            {
                if (dictionary[i] == code)
                {
                    return i;
                }
            }
            return Integer.MIN_VALUE;
        }

        /**
         * @param code to check
         *
         * @return true if the given code was found in the dictionary, false otherwise
         */
        public boolean isCode(int code)
        {
            for (int value : dictionary)
            {
                if (value == code)
                {
                    return true;
                }
            }
            return false;
        }
    }
}
