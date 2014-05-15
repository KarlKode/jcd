package ch.ethz.jcd.application;

import ch.ethz.jcd.application.commands.AbstractVFSCommand;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

import java.util.HashMap;
import java.util.Observer;
import java.util.Queue;

public interface AbstractVFSApplication
{
    public final HashMap<String, AbstractVFSCommand> commands = new HashMap<>();

    public Queue<AbstractVFSCommand> getHistory();

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

    public void quit();
}
