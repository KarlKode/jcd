package ch.ethz.jcd.application;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

public interface AbstractVFSApplication
{
    static final String OPTION_H = "-h";
    static final String OPTION_HELP = "--help";
    static final String OPTION_N = "-n";
    static final String OPTION_NEW_DISK = "--new_disk";
    static final String OPTION_C = "-c";
    static final String OPTION_COMPRESSED = "--compressed";
    static final String OPTION_S = "-s";
    static final String OPTION_SIZE = "--size";
    static final int DEFAULT_SIZE = 1024;

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
