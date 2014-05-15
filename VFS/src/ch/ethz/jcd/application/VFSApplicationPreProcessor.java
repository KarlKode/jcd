package ch.ethz.jcd.application;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.File;
import java.io.IOException;

public class VFSApplicationPreProcessor
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

    public static VDisk prepareDisk(String[] args)
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

            return new VDisk(vdiskFile);
        }
        catch (InvalidBlockAddressException | InvalidSizeException | InvalidBlockCountException | VDiskCreationException | IOException e)
        {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
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

    /**
     * Prints the usage of the console application
     */
    private static void usage()
    {
        System.out.println("Usage: [OPTIONS] vdisk");
    }
}
