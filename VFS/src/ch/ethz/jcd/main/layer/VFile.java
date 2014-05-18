package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DataBlock;
import ch.ethz.jcd.main.blocks.DataBlockListBlock;
import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

/**
 * VFile is a concrete implementation of VObject coming with additional features such as
 * read(), write(), copy(), delete()
 */
public class VFile extends VObject<FileBlock> {
    /**
     * Instantiation of a new VFile.
     *
     * @param block  containing the byte structure of the VFile
     * @param parent of the VFile
     */
    public VFile(FileBlock block, VDirectory parent) {
        super(block, parent);
    }

    /**
     * This Method copies the VFile to the given destination
     *
     * @param vUtil       used to allocate Blocks
     * @param destination where to put the copied VObject
     * @throws BlockFullException
     * @throws IOException
     * @throws InvalidBlockAddressException
     * @throws DiskFullException
     */
    @Override
    public VObject copy(VUtil vUtil, VDirectory destination, String name)
            throws BlockFullException, IOException, InvalidBlockAddressException, DiskFullException, InvalidBlockSizeException, InvalidNameException {
        FileBlock fileBlock = vUtil.allocateFileBlock();

        for (DataBlock src : this.block.getDataBlockList()) {
            DataBlock dest = vUtil.allocateDataBlock();
            dest.setContent(src.getContent());
            try {
                fileBlock.addDataBlock(dest, VUtil.BLOCK_SIZE);
            } catch (BlockFullException e) {
                fileBlock.addDataBlockListBlock(vUtil.allocateDataBlockListBlock());
                fileBlock.addDataBlock(dest, VUtil.BLOCK_SIZE);
            }
        }

        fileBlock.setSize(block.size());
        VFile copy = new VFile(fileBlock, destination);
        copy.setName(name);
        destination.addEntry(copy);
        return copy;
    }

    /**
     * This Method deletes the VFile
     *
     * @param vUtil used to free the corresponding Blocks
     * @throws IOException
     */
    @Override
    public void delete(VUtil vUtil)
            throws IOException {
        for (DataBlock b : block.getDataBlockList()) {
            vUtil.free(b);
        }
        for (DataBlockListBlock dataBlockListBlock : block.getDataBlockListBlockList()) {
            vUtil.free(dataBlockListBlock);
        }
        this.parent.removeEntry(this);
        vUtil.free(block);
    }

    /**
     * This method recursively resolves the given path.
     *
     * @param path to resolveDirectory
     * @return the resolved object, null if no object found
     * @throws IOException
     */
    @Override
    public VObject resolve(String path)
            throws IOException {
        if (path == null || path.length() <= 0 || path.startsWith(VDisk.PATH_SEPARATOR) || path.endsWith(VDisk.PATH_SEPARATOR)) {
            return null;
        }

        String[] split = path.split(VDisk.PATH_SEPARATOR);
        return (split.length == 1) ? this : null;
    }

    /**
     * This method compare the given object to this VFile and checks if they
     * are equal or not. For the equality of two VFile the following
     * properties must be equal.
     * - name, path, size
     * <p>
     * WARNING: due to performance reasons, checking the underlying structure
     * of its equality is skipped
     *
     * @param obj to compare with
     * @return true if the given object ist equal to this, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        boolean equal = true;

        if (obj != null && obj instanceof VFile) {
            try {
                equal = this.getPath().equals(((VFile) obj).getPath());
                equal = equal && this.block.size() == ((VFile) obj).block.size();
                equal = equal && this.getName().equals(((VFile) obj).getName());
            } catch (IOException e) {
                equal = false;
            }
        }

        return equal;
    }

    /**
     * This method instantiate an input stream to buffered write into this file.
     *
     * @return the input stream
     */
    public VFileInputStream inputStream(VUtil vUtil, boolean compressed)
            throws IOException {
        if (compressed) {
            return new CompressedVFileInputStream(vUtil, this);
        }
        return new VFileInputStream(vUtil, this);
    }

    /**
     * This method instantiate an iterator to buffered read this file.
     *
     * @return the iterator
     */
    public VFileOutputStream iterator() {
        return new VFileOutputStream(this);
    }

    /**
     * The VFileOutputStream is used to buffered write into a file stored in
     * the virtual file system. Useful when doing imports to avoid loading big
     * files completely into memory.
     */
    public class VFileInputStream {
        protected VUtil vUtil;
        protected VFile vFile;

        /**
         * Visibility is set to private to ensure correct instantiation
         *
         * @param vUtil used to allocate Blocks
         * @param vFile to apply the input stream
         */
        private VFileInputStream(VUtil vUtil, VFile vFile)
                throws IOException {
            this.vUtil = vUtil;
            this.vFile = vFile;

            // TODO why the fuck this?
            /*
            for (DataBlock b : this.vFile.block.getDataBlockList())
            {
                this.vUtil.free(b);
            }*/
        }

        /**
         * @param bytes to put into dataBlock
         * @throws DiskFullException
         * @throws IOException
         * @throws InvalidBlockAddressException
         * @throws InvalidBlockSizeException
         * @throws BlockFullException
         */
        public void put(byte[] bytes)
                throws DiskFullException, IOException, InvalidBlockAddressException, InvalidBlockSizeException, BlockFullException, BlockEmptyException {
            // adding a new DataBlock
            DataBlock dataBlock = vUtil.allocateDataBlock();
            dataBlock.setContent(bytes);
            try {
                vFile.block.addDataBlock(dataBlock, bytes.length);
            } catch (BlockFullException e) {
                vFile.block.addDataBlockListBlock(vUtil.allocateDataBlockListBlock());
                vFile.block.addDataBlock(dataBlock, bytes.length);
            }
        }
    }

    /**
     * The VFileOutputStream is used to buffered write into a file stored in
     * the virtual file system. Useful when doing imports to avoid loading big
     * files completely into memory.
     */
    public class CompressedVFileInputStream extends VFileInputStream {
        /**
         * Visibility is set to private to ensure correct instantiation
         *
         * @param vUtil used to allocate Blocks
         * @param vFile to apply the input stream
         */
        private CompressedVFileInputStream(VUtil vUtil, VFile vFile)
                throws IOException {
            super(vUtil, vFile);
        }

        /**
         * @param bytes to put into dataBlock
         * @throws DiskFullException
         * @throws IOException
         * @throws InvalidBlockAddressException
         * @throws InvalidBlockSizeException
         * @throws BlockFullException
         */
        public void put(byte[] bytes)
                throws DiskFullException, IOException, InvalidBlockAddressException, InvalidBlockSizeException, BlockFullException, BlockEmptyException {
            int lastBlockSize = (int) block.size() % VUtil.BLOCK_SIZE;
            int freeBytes = VUtil.BLOCK_SIZE - lastBlockSize;
            int index = 0;

            if (lastBlockSize != 0) {
                // fill last DataBlock
                index = Math.min(freeBytes, bytes.length);
                DataBlock dataBlock = vFile.block.removeLastDataBlock();
                byte[] content = new byte[Math.min(VUtil.BLOCK_SIZE, lastBlockSize + bytes.length)];
                ByteBuffer buf = ByteBuffer.wrap(content);
                buf.put(dataBlock.getContent(), 0, lastBlockSize);
                buf.put(bytes, 0, index);
                dataBlock.setContent(buf.array());
                try {
                    vFile.block.addDataBlock(dataBlock, content.length);
                } catch (BlockFullException e) {
                    vFile.block.addDataBlockListBlock(vUtil.allocateDataBlockListBlock());
                    vFile.block.addDataBlock(dataBlock, content.length);
                }
            }

            // adding DataBlocks until no bytes left to store
            while (index < bytes.length) {
                int len = Math.min(VUtil.BLOCK_SIZE, bytes.length - index);
                super.put(Arrays.copyOfRange(bytes, index, index + len));
                index += len;
            }
        }
    }

    /**
     * The VFileOutputStream is used to buffered read a file stored in the virtual
     * file system. Useful when doing exports to avoid loading big files completely
     * into memory.
     */
    public class VFileOutputStream implements Iterator<ByteBuffer> {
        protected VFile vFile;
        protected int next = 0;
        protected int byteOffset = 0;

        /**
         * Visibility is set to private to ensure the iterator is instantiated
         * correctly
         *
         * @param vFile to apply the output stream
         */
        private VFileOutputStream(VFile vFile) {
            this.vFile = vFile;
        }

        /**
         * @return true if there is a next element, false otherwise
         */
        @Override
        public boolean hasNext() {
            try {
                return next < vFile.block.count();
            } catch (IOException e) {
                return false;
            }
        }

        /**
         * This method read the next DataBlock and extract the bytes stored in
         * it.
         *
         * @return the read bytes
         */
        @Override
        public ByteBuffer next() {
            try {
                int len = VUtil.BLOCK_SIZE - byteOffset;

                if (vFile.block.isLastDataBlock(next)) {
                    int remainder = (int) vFile.block.size() % VUtil.BLOCK_SIZE;
                    len = remainder > 0 ? remainder - byteOffset : len;
                }

                ByteBuffer buf = ByteBuffer.wrap(block.getDataBlock(next).getContent(byteOffset, len));
                next++;
                return buf;
            } catch (IOException e) {
                return ByteBuffer.wrap(null);
            }
        }

        /**
         * This method is not supported thus calling it will throw a runtime
         * exception.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}