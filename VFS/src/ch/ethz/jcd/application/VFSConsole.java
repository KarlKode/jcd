package ch.ethz.jcd.application;

import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.application.commands.*;

import java.io.*;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

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
public class VFSConsole extends Observable implements AbstractVFSApplication, Runnable
{
    private boolean quit = false;

    /**
     * The VDisk is the receiver in the command pattern, while the VFSConsole
     * is the invoker that holds the command history
     */
    private final VDisk vDisk;
    private final Queue<AbstractVFSCommand> history = new LinkedList<>();
    protected VDirectory current;

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
        new VFSConsole(VFSApplicationPreProcessor.prepareDisk(args));
    }

    /**
     * Instantiate a new console application to operate on the loaded VDisk
     * passed through the arguments described above
     *
     * @param vDisk to operate on
     */
    public VFSConsole(VDisk vDisk)
    {
        commands.put("cd", new VFScd());
        commands.put("cp", new VFScp());
        commands.put("export", new VFSexport());
        commands.put("find", new VFSfind());
        commands.put("help", new VFShelp());
        commands.put("import", new VFSimport());
        commands.put("ls", new VFSls());
        commands.put("mkdir", new VFSmkdir());
        commands.put("mv", new VFSmv());
        commands.put("pwd", new VFSpwd());
        commands.put("quit", new VFSQuit());
        commands.put("rm", new VFSrm());
        commands.put("touch", new VFStouch());

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
        while(!quit)
        {
            String[] args = prompt("> ");
            AbstractVFSCommand cmd = commands.get(args[0]);
            cmd.setArgs(args);
            execute(cmd);
        }
    }

    @Override
    public Queue<AbstractVFSCommand> getHistory()
    {
        return this.history;
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

    @Override
    public void quit( )
    {
        this.quit = true;
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
            System.out.print(prompt);
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            return bufferRead.readLine().split("\\s+");
        }
        catch (IOException e)
        {
            return null;
        }
    }

    /**
     * Executes the entered command.
     *
     * @param cmd to execute
     */
    private void execute(AbstractVFSCommand cmd)
    {
        this.setChanged();
        this.notifyObservers(cmd);
        history.add(cmd);

        if(cmd != null)
        {
            try
            {
                cmd.execute(this);
            }
            catch (CommandException e)
            {
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
    private void usage()
    {
        try
        {
            commands.get("help").execute(this);
        }
        catch (CommandException e)
        {
            commands.get("help").error(e.getCause().getMessage());
        }
    }
}
