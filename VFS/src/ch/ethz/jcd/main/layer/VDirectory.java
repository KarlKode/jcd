package ch.ethz.jcd.main.layer;

import ch.ethz.jcd.main.blocks.DirectoryBlock;
import ch.ethz.jcd.main.blocks.ObjectBlock;

import java.util.ArrayList;
import java.util.List;

public class VDirectory extends VObject<DirectoryBlock>
{
    public List<VObject> listChildren()
    {
        List<VObject> children = new ArrayList<>();
        for (ObjectBlock childBlock : block.getChildren())
        {
            if (childBlock.getType() == ObjectBlock.TYPE_DIRECTORY)
            {
                children.add(new VDirectory());
            } else
            {
                children.add(new VFile());
            }
        }

        return children;
    }

    public void addChild(VObject object)
    {
        block.addChild(object.block);
    }

    public void removeChild(VObject object)
    {
        block.removeChild(object.block);
    }
}
