package ch.ethz.jcd.application;

import ch.ethz.jcd.main.exceptions.*;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;
import ch.ethz.jcd.application.commands.*;

import java.io.*;

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
public class VFSConsole implements AbstractVFSApplication, Runnable
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

    public static final String QUIT_CMD = "quit";

    protected VDirectory current;
    protected VDisk vDisk;

    private InputStream inputStream;
    private OutputStream outputStream;

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
            new VFSConsole(new VDisk(vdiskFile), System.in, System.out);
        }
        catch (InvalidBlockAddressException | InvalidSizeException | InvalidBlockCountException | VDiskCreationException | IOException e)
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
    public VFSConsole(VDisk vDisk, InputStream inputStream, OutputStream outputStream)
    {
        commands.put("cd", new VFScd(this));
        commands.put("cp", new VFScp(this));
        commands.put("export", new VFSexport(this));
        commands.put("find", new VFSfind(this));
        commands.put("help", new VFShelp(this));
        commands.put("import", new VFSimport(this));
        commands.put("ls", new VFSls(this));
        commands.put("mkdir", new VFSmkdir(this));
        commands.put("mv", new VFSmv(this));
        commands.put("pwd", new VFSpwd(this));
        commands.put("rm", new VFSrm(this));
        commands.put("touch", new VFStouch(this));

        this.inputStream = inputStream;
        this.outputStream = outputStream;
        try
        {
            current = (VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR);
        }
        catch (ResolveException e)
        {
            e.printStackTrace();
        }
        this.vDisk = vDisk;
        new Thread(this).start();
    }

    @Override
    public void run()
    {
        String[] args = prompt("> ");

        while(args != null && !args[0].equals(QUIT_CMD))
        {
            execute(args);
            println("\0");
            args = prompt("> ");
        }
        println("\0");
    }

    /**
     *
     * @return the vDisk
     */
    @Override
    public VDisk getVDisk( )
    {
        return vDisk;
    }

    /**
     *
     * @return the current/working directory
     */
    @Override
    public VDirectory getCurrent( )
    {
        return current;
    }

    /**
     * Sets the current/working directory
     *
     * @param dir to set
     */
    @Override
    public void setCurrent(VDirectory dir)
    {
        this.current = dir;
    }

    /**
     * Prints the prompt of the console application and reads the command
     * entered by the user.
     *
     * TODO make fancy
     *
     * @param prompt to output
     * @return read aguments
     */
    private String[] prompt(String prompt)
    {
        try
        {
            this.print(prompt);
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(inputStream));
            return bufferRead.readLine().split("\\s+");
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @Override
    public void print(String s)
    {
        try
        {
            outputStream.write(s.getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void println(String line)
    {
        this.print(line + "\n");
    }

    /**
     * Executes the entered command.
     *
     * @param args to pass
     */
    private void execute(String[] args)
    {
        AbstractVFSCommand cmd = commands.get(args[0]);

        if(cmd != null)
        {
            try
            {
                cmd.execute(args);
            }
            catch (CommandException e)
            {
                // TODO luege wege causes und message
                cmd.error(e.getCause().getMessage());
            }
        }
        else
        {
            usage();
        }
    }

    /**
     * Prints the usage of the console application
     */
    private static void usage()
    {
        System.out.println("Usage: [OPTIONS] vdisk");
        /*System.out.println();
        System.out.println("Commands:");
        for(AbstractVFSCommand cmd : commands.values())
        {
            cmd.help();
        }*/
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
