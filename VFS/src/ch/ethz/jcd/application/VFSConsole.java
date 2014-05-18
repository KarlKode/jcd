package ch.ethz.jcd.application;

import ch.ethz.jcd.application.commands.AbstractVFSCommand;
import ch.ethz.jcd.application.commands.CommandFactory;
import ch.ethz.jcd.application.commands.VFShelp;
import ch.ethz.jcd.main.exceptions.command.CommandException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

/**
 * Console application to operate on the VFS.
 * <p>
 * To easily see the VFS in action, it comes with a simple console application.
 * The usage of this command line tool is simple:
 * <p>
 * 1) open a terminal
 * 2) navigate to the VFS root directory
 * 3) launch the console by typing the following command into your prompt
 * <p>
 * > java -jar VFS.jar data/console.vdisk
 * <p>
 * The command above make the console loading an existing VDisk. If you want to
 * create a new one, you have to pass the number of blocks to trigger the
 * console to create a new VDisk. The command therefore is
 * <p>
 * > java -jar VFS.jar data/console.vdisk <number of block to allocate>
 */
public class VFSConsole extends Observable implements AbstractVFSApplication, Runnable
{
    /**
     * The VDisk is the receiver in the command pattern, while the VFSConsole
     * is the invoker that holds the command history
     */
    private final VDisk vDisk;
    private final Queue<AbstractVFSCommand> history = new LinkedList<>();
    protected VDirectory current;
    private boolean quit = false;

    /**
     * Instantiate a new console application to operate on the loaded VDisk
     * passed through the arguments described above
     *
     * @param vDisk to operate on
     */
    public VFSConsole(VDisk vDisk)
    {
        try
        {
            current = (VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR);
        } catch (ResolveException e)
        {
            e.printStackTrace();
        }
        this.vDisk = vDisk;
        new Thread(this).start();
    }

    /**
     * Start the console and open an existing VDisk
     * <p>
     * > java -jar VFS.jar data/console.vdisk
     * <p>
     * Start the console and create a new VDisk
     * <p>
     * > java -jar VFS.jar data/console.vdisk <number of block to allocate>
     *
     * @param args passed to the console to behave in different ways
     */
    public static void main(String[] args)
    {
        new VFSConsole(VFSApplicationPreProcessor.prepareDisk(args));
    }

    @Override
    public void run()
    {
        while (!quit)
        {
            String[] args = prompt("> ");
            AbstractVFSCommand cmd = CommandFactory.create(args);
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
     * @return the vDisk
     */
    @Override
    public VDisk getVDisk()
    {
        return vDisk;
    }

    /**
     * @return the current/working directory
     */
    @Override
    public VDirectory getCurrent()
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
    public void quit()
    {
        this.quit = true;
    }

    /**
     * Prints the prompt of the console application and reads the command
     * entered by the user.
     * <p>
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
        } catch (IOException e)
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

        if (cmd != null)
        {
            try
            {
                cmd.execute(this);
            } catch (CommandException e)
            {
                cmd.error(e.getCause().getMessage());
            }
        } else
        {
            usage();
        }
    }

    /**
     * Prints the usage of the console application
     */
    private void usage()
    {
        VFShelp cmd = new VFShelp(null);
        try
        {
            cmd.execute(this);
        } catch (CommandException e)
        {
            cmd.error(e.getCause().getMessage());
        }
    }
}
