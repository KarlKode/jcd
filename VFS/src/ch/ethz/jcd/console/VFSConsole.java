package ch.ethz.jcd.console;

import ch.ethz.jcd.console.commands.AbstractVFSCommand;
import ch.ethz.jcd.console.commands.VFSls;
import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class VFSConsole
{
    public static final String QUIT_CMD = "quit";
    public static final HashMap<String, AbstractVFSCommand> VFS_COMMANDS;
    static
    {
        VFS_COMMANDS = new HashMap<>();
        VFS_COMMANDS.put("ls", new VFSls());
    }

    private VDirectory current;
    private VDisk vDisk;

    public static void main(String[] args)
    {
        try
        {
            quitWithUsageIfLessThan(args, 2);
            File vdiskFile = new File(args[0]);
            int blockCount = Integer.parseInt(args[1]);
            VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * blockCount);
            new VFSConsole(new VDisk(vdiskFile));
        }
        catch (InvalidBlockAddressException | InvalidSizeException | InvalidBlockCountException | VDiskCreationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public VFSConsole(VDisk vDisk)
    {
        this.vDisk = vDisk;
        current = vDisk.resolve("/");

        while(true)
        {
            String[] args = prompt("> ");

            if(args != null)
            {
                if (args[0].equals(QUIT_CMD))
                {
                    break;
                }
                execute(args);
            }
        }
    }

    private String[] prompt( String prompt )
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

    private void execute(String[] args)
    {
        AbstractVFSCommand cmd = VFS_COMMANDS.get(args[0]);

        if(cmd != null)
        {
            cmd.execute(this, args);
        }
        else
        {
            usage();
        }
    }

    public VDisk getVDisk( )
    {
        return vDisk;
    }

    public VDirectory getCurrent( )
    {
        return current;
    }

    public void setCurrent(VDirectory dir)
    {
        this.current = dir;
    }

    private static void usage()
    {
        System.out.println("Usage: vdisk <command>[ arguments]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  help <command> - Get help for a certain command");
        System.out.println("  create <VDisk file> - Create a new VDisk at <file>");
        System.out.println("  destroy <VDisk file> - Destroy the existing VDisk at <file>");
        System.out.println("  ls <VDisk file> <VPath dir> - List contents of VDirectory at <dir>");
        System.out.println("  get <VDisk file> <VPath file> - Read contents of VFile at <file> and print it to stdout");
        System.out.println("  put <VDisk file> <VPath file> - Read from stdin into VFile <file>");
        System.out.println("  rm <VDisk file> <VPath file/dir> - Remove VFile or VDirectory at <file/dir>");
        System.out.println("  cp <VDisk file> <VPath src> <VPath dst> - Copy VFile or VDirectory from <src> to <dst>");
        System.out.println("  import <VDisk file> <Path src> <VPath dst> - Import file or directory from <src> to the VFile or VDirectory at <dst>");
        System.out.println("  export <VDisk file> <VPath src> <VPath dst> - Export the VFile VDirectory from <src> to file or directory at <dst>");
    }

    private static void quitWithUsageIfLessThan(String[] arguments, int minArgumentLength)
    {
        if (arguments.length < minArgumentLength)
        {
            usage();
            System.exit(1);
        }
    }
}
