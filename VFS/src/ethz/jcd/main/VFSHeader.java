package ethz.jcd.main;

import java.nio.ByteBuffer;

/**
 * Created by phgamper on 3/7/14.
 */
public class VFSHeader
{
    private byte[] bytes;

    private ByteBuffer header;

    public VFSHeader(byte[] bytes)
    {
        this.bytes = bytes;

        header = ByteBuffer.wrap(bytes);
    }

    public int blockSize( )
    {
        return header.getInt(0);
    }

    public int blockCount( )
    {
        return header.getInt(4);
    }

    public int startOfFreeList( )
    {
        return Config.VFS_HEADER_LEN;
    }

    public int startOfBlocks( )
    {
        return Config.VFS_HEADER_LEN + blockCount() * 4;
    }
}
