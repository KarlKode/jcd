package ethz.jcd.main.blocks;

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