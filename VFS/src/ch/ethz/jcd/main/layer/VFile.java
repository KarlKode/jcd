package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.FileBlock;
import ch.ethz.jcd.main.exceptions.InvalidDataBlockOffsetException;
import ch.ethz.jcd.main.utils.FileManager;
import ch.ethz.jcd.main.utils.VUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VFile extends VObject<FileBlock>
{
    public void write(int offset, byte[] bytes)
    {
        // TODO
        throw new NotImplementedException();
    }

    public byte[] read(int offset, int length) throws IOException, InvalidDataBlockOffsetException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            long maxBlock = Math.min(this.block.getSize(), offset + length);

            int dataBlockIndexStart = (int) Math.floor(offset / VUtil.BLOCK_SIZE);
            int inBlockOffsetStart = offset % VUtil.BLOCK_SIZE;

            int dataBlockIndexEnd = (int) Math.floor(maxBlock / VUtil.BLOCK_SIZE);
            int inBlockOffsetEnd = (int) maxBlock % VUtil.BLOCK_SIZE;

            if (dataBlockIndexStart == dataBlockIndexEnd) {
                return this.block.getDataBlock(dataBlockIndexStart).getContent(inBlockOffsetStart, inBlockOffsetEnd);
            } else if (dataBlockIndexEnd - dataBlockIndexStart == 1) {
                out.write(this.block.getDataBlock(dataBlockIndexStart).getContent(inBlockOffsetStart));
                out.write(this.block.getDataBlock(dataBlockIndexEnd).getContent(0, inBlockOffsetEnd));
            } else {
                out.write(this.block.getDataBlock(dataBlockIndexStart).getContent(inBlockOffsetStart));
                for (int i = dataBlockIndexStart+1; i < dataBlockIndexEnd; i++) {
                    out.write(this.block.getDataBlock(i).getContent());
                }
                out.write(this.block.getDataBlock(dataBlockIndexEnd).getContent(0, inBlockOffsetEnd));
            }

            return out.toByteArray();
        }
    }
}
