package ch.ethz.jcd.console;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

public class AbstractVFSApplication
{
    protected VDirectory current;
    protected VDisk vDisk;

    /**
     *
     * @return the vDisk
     */
    public VDisk getVDisk( )
    {
        return vDisk;
    }

    /**
     *
     * @return the current/working directory
     */
    public VDirectory getCurrent( )
    {
        return current;
    }

    /**
     * Sets the current/working directory
     *
     * @param dir to set
     */
    public void setCurrent(VDirectory dir)
    {
        this.current = dir;
    }
}
