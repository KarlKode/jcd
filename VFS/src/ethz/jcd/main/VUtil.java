package ethz.jcd.main;

import ethz.jcd.main.blocks.Block;
<<<<<<< HEAD
import ethz.jcd.main.blocks.SuperBlock;
=======
import ethz.jcd.main.exceptions.InvalidBlockSize;
import ethz.jcd.main.exceptions.InvalidSize;
import ethz.jcd.main.exceptions.VDiskCreationException;
>>>>>>> 6a0ce169231ecbd989bb646903ce38863ac383e1
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
<<<<<<< HEAD
import java.util.LinkedList;
import java.util.Stack;
=======
>>>>>>> 6a0ce169231ecbd989bb646903ce38863ac383e1

public class VUtil
{
    private RandomAccessFile raf;

    private final String vDiskFile;

    private final SuperBlock root;

    private final Allocator<Stack<Block>> allocator;

    public VUtil( String vDiskFile ) throws FileNotFoundException
    {
        this.vDiskFile = vDiskFile;
<<<<<<< HEAD
        File fp = new File(this.vDiskFile);

        raf = new RandomAccessFile(fp, "rw");
=======
        raf = new RandomAccessFile(vDiskFile, "rwd");
>>>>>>> 6a0ce169231ecbd989bb646903ce38863ac383e1

        root = loadSuperBlock();

        allocator = new Allocator<Stack<Block>>(this.loadFreeList());
    }

    public VUtil( String vDiskFile, long size, int blockSize ) throws InvalidSize, InvalidBlockSize, VDiskCreationException {
        this.vDiskFile = vDiskFile;

        // Check size and blockSize for validity
        if (size <= 0 || size % blockSize != 0) {
            throw new InvalidSize();
        }
        // TODO: check for minimum block size (to fit at least the superblock)
        if (blockSize <= 0) {
            throw new InvalidBlockSize();
        }

        File fp = new File(this.vDiskFile);
        try {
            if (!fp.createNewFile()) {
                throw new VDiskCreationException();
            }
        } catch (IOException e) {
            throw new VDiskCreationException();
        }

        throw new NotImplementedException();
    }

    private SuperBlock loadSuperBlock( )
    {
        byte[] bytes = new byte[Config.VFS_SUPER_BLOCK_SIZE];

        try
        {
            raf.read(bytes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return SuperBlock.getInstance(bytes);
    }

    public Stack<Block> loadFreeList( )
    {
        Stack<Block> freeList = new Stack<Block>();

        read(root.startOfFreeList());

        byte[] flags = new byte[Config.VFS_BLOCK_SIZE];

        try
        {
            raf.read(flags);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(int i = 0; i < Config.VFS_BLOCK_COUNT; i++)
        {

        }
        return null;
    }

    public void storeFreeList( LinkedList<Integer> list )
    {
        throw new NotImplementedException();
    }

    /**
     * This method writes a given Block in the VFS and returns the address
     * of the allocated Block
     *
     * @param block Block to store in the VFS
     * @return blockAddress
     */
    public Integer write( Block block )
    {
        //TODO do mitem allocater platz mache, denn ineschriebe, write passiert den entsprechend Block type

        throw new NotImplementedException();
    }

    public Block read( Integer blockAddress )
    {
        throw new NotImplementedException();
    }

    public SuperBlock getRoot( )
    {
        return root;
    }

}
