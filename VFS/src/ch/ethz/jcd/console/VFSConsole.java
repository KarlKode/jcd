package ch.ethz.jcd.console;

import ch.ethz.jcd.console.commands.*;
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
        VFS_COMMANDS.put("cd", new VFScd());
        VFS_COMMANDS.put("cp", new VFScp());
        VFS_COMMANDS.put("export", new VFSexport());
        VFS_COMMANDS.put("import", new VFSimport());
        VFS_COMMANDS.put("ls", new VFSls());
        VFS_COMMANDS.put("mkdir", new VFSmkdir());
        VFS_COMMANDS.put("mv", new VFSmv());
        VFS_COMMANDS.put("pwd", new VFSpwd());
        VFS_COMMANDS.put("rm", new VFSrm());
        VFS_COMMANDS.put("touch", new VFStouch());
    }

    private VDirectory current;
    private VDisk vDisk;

    public static void main(String[] args)
    {
        try
        {
            quitWithUsageIfLessThan(args, 1);
            File vdiskFile = new File(args[0]);
            if(args.length > 1)
            {
                VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * Integer.parseInt(args[1]));
            }
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
        current = (VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR);

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
        for(AbstractVFSCommand cmd : VFS_COMMANDS.values())
        {
            cmd.help();
        }
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
