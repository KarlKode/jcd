package ch.ethz.jcd.application;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Console application to operate on the VFS.
 *
 * To easily see the VFS in action, it comes with a simple console application.
 * The usage of this command line tool is simple:
 *
 *      1) open a terminal
 *      2) navigate to the VFS root directory
 *      3) launch the console by typing the following command into your prompt
 *
 *          > java -jar VFS.jar data/console.vdisk
 *
 * The command above make the console loading an existing VDisk. If you want to
 * create a new one, you have to pass the number of blocks to trigger the
 * console to create a new VDisk. The command therefore is
 *
 *          > java -jar VFS.jar data/console.vdisk <number of block to allocate>
 *
 */
public class VFSServer implements AbstractVFSApplication
{
    private VFSConsole console;

    /**
     * Start the console and open an existing VDisk
     *
     *  > java -jar VFS.jar data/console.vdisk
     *
     *  Start the console and create a new VDisk
     *
     *  > java -jar VFS.jar data/console.vdisk <number of block to allocate>
     *
     * @param args passed to the console to behave in different ways
     */
    public static void main(String[] args)
    {
        try
        {
            quitWithUsageIfLessThan(args, 1);

            boolean newDisk = false;
            boolean compressed = false;
            int file = args.length - 1;
            int size = DEFAULT_SIZE;
            int i = 0;

            while(i < args.length)
            {
                if(args[i].equals(OPTION_H) || args[i].equals(OPTION_HELP))
                {
                    usage();
                    System.exit(1);
                }
                else if(args[i].equals(OPTION_C) || args[i].equals(OPTION_COMPRESSED))
                {
                    compressed = true;
                }
                else if(args[i].equals(OPTION_N) || args[i].equals(OPTION_NEW_DISK))
                {
                    newDisk = true;
                }
                else if(args[i].equals(OPTION_S) || args[i].equals(OPTION_SIZE))
                {
                    i++;

                    if(i >= args.length)
                    {
                        usage();
                        System.exit(1);
                    }
                    size = Integer.parseInt(args[i]);
                }
                else
                {
                    file = Math.min(i, file);
                }
                i++;
            }

            File vdiskFile = new File(args[file]);
            if(newDisk)
            {
                VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * size, compressed);
            }
            new VFSServer(new VDisk(vdiskFile));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Instantiate a new console application to operate on the loaded VDisk
     * passed through the arguments described above
     *
     * @param vDisk to operate on
     */
    public VFSServer(VDisk vDisk)
    {
        //console = new VFSConsole(vDisk, );
    }

    /**
     *
     * @return the vDisk
     */
    public VDisk getVDisk( )
    {
        return console.getVDisk();
    }

    /**
     *
     * @return the current/working directory
     */
    public VDirectory getCurrent( )
    {
        return console.getCurrent();
    }

    /**
     * Sets the current/working directory
     *
     * @param dir to set
     */
    public void setCurrent(VDirectory dir)
    {
        this.console.setCurrent(dir);
    }


    /**
     * Prints the usage of the console application
     */
    private static void usage()
    {
        System.out.println("Usage: [OPTIONS] vdisk");
    }

    /**
     * Checks if the minimum required arguments are passed, quit otherwise.
     *
     * @param arguments to check
     * @param minArgumentLength required
     */
    private static void quitWithUsageIfLessThan(String[] arguments, int minArgumentLength)
    {
        if (arguments.length < minArgumentLength)
        {
            usage();
            System.exit(1);
        }
    }
}
