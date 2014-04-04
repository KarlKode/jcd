package ch.ethz.jcd.main.utils;

import ch.ethz.jcd.main.blocks.SuperBlock;
import ch.ethz.jcd.main.layer.VDirectory;

public class VStats
{
    private VDisk vDisk;
    private VDirectory root;
    private SuperBlock superBlock;

    private int totalBlocks;

    public VStats(VDisk vDisk, VDirectory root, SuperBlock superBlock)
    {
        this.vDisk = vDisk;
        this.root = root;
        this.superBlock = superBlock;
    }

    public int diskSize( )
    {
        return 0;
    }

    public int freeSpace( )
    {
        return 0;
    }

    public int usedSpace( )
    {
        return 0;
    }

    public int freeBlocks( )
    {
        return 0;
    }

    public int usedBlocks()
    {
        return 0;
    }

    public int fileCount( )
    {
        return 0;
    }

    public int directoryCount( )
    {
        return 0;
    }
}