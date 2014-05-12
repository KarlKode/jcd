package ch.ethz.jcd.console;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

public interface AbstractVFSApplication
{
    /**
     * @return the vDisk
     */
    public VDisk getVDisk();

    /**
     * @return the current/working directory
     */
    public VDirectory getCurrent();

    /**
     * Sets the current/working directory
     *
     * @param dir to set
     */
    public void setCurrent(VDirectory dir);
}
