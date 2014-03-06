package ethz.jcd.blocks;

/**
 * Created by phgamper on 3/6/14.
 */
public class SuperBlock extends Directory
{
    public static int SUPER_BLOCK_ADDRESS = 1;

    public static SuperBlock instance;

    public static SuperBlock getInstance( )
    {
        if(instance == null)
        {
            instance = new SuperBlock();
        }

        return instance;
    }

    private SuperBlock( )
    {
        address = SUPER_BLOCK_ADDRESS;
    }
}