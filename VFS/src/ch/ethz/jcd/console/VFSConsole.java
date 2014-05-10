//package ch.ethz.jcd.console;
//
//import ch.ethz.jcd.console.commands.*;
//import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
//import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
//import ch.ethz.jcd.main.exceptions.InvalidSizeException;
//import ch.ethz.jcd.main.exceptions.VDiskCreationException;
//import ch.ethz.jcd.main.layer.VDirectory;
//import ch.ethz.jcd.main.utils.VDisk;
//import ch.ethz.jcd.main.utils.VUtil;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//
///**
// * Console application to operate on the VFS.
// *
// * To easily see the VFS in action, it comes with a simple console application.
// * The usage of this command line tool is simple:
// *
// *      1) open a terminal
// *      2) navigate to the VFS root directory
// *      3) launch the console by typing the following command into your prompt
// *
// *          > java -jar VFS.jar data/console.vdisk
// *
// * The command above make the console loading an existing VDisk. If you want to
// * create a new one, you have to pass the number of blocks to trigger the
// * console to create a new VDisk. The command therefore is
// *
// *          > java -jar VFS.jar data/console.vdisk <number of block to allocate>
// *
// */
//public class VFSConsole
//{
//    public static final String QUIT_CMD = "quit";
//    public static final HashMap<String, AbstractVFSCommand> VFS_COMMANDS;
//    static
//    {
//        VFS_COMMANDS = new HashMap<>();
//        VFS_COMMANDS.put("cd", new VFScd());
//        VFS_COMMANDS.put("cp", new VFScp());
//        VFS_COMMANDS.put("export", new VFSexport());
//        VFS_COMMANDS.put("find", new VFSfind());
//        VFS_COMMANDS.put("help", new VFShelp());
//        VFS_COMMANDS.put("import", new VFSimport());
//        VFS_COMMANDS.put("ls", new VFSls());
//        VFS_COMMANDS.put("mkdir", new VFSmkdir());
//        VFS_COMMANDS.put("mv", new VFSmv());
//        VFS_COMMANDS.put("pwd", new VFSpwd());
//        VFS_COMMANDS.put("rm", new VFSrm());
//        VFS_COMMANDS.put("touch", new VFStouch());
//    }
//
//    private VDirectory current;
//    private VDisk vDisk;
//
//    /**
//     * Start the console and open an existing VDisk
//     *
//     *  > java -jar VFS.jar data/console.vdisk
//     *
//     *  Start the console and create a new VDisk
//     *
//     *  > java -jar VFS.jar data/console.vdisk <number of block to allocate>
//     *
//     * @param args passed to the console to behave in different ways
//     */
//    public static void main(String[] args)
//    {
//        try
//        {
//            quitWithUsageIfLessThan(args, 1);
//            File vdiskFile = new File(args[0]);
//            if(args.length > 1)
//            {
//                VDisk.format(vdiskFile, VUtil.BLOCK_SIZE * Integer.parseInt(args[1]));
//            }
//            new VFSConsole(new VDisk(vdiskFile));
//        }
//        catch (InvalidBlockAddressException | InvalidSizeException | InvalidBlockCountException | VDiskCreationException | IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Instantiate a new console application to operate on the loaded VDisk
//     * passed through the arguments described above
//     *
//     * @param vDisk to operate on
//     */
//    public VFSConsole(VDisk vDisk)
//    {
//        this.vDisk = vDisk;
//        current = (VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR);
//
//        while(true)
//        {
//            String[] args = prompt("> ");
//
//            if(args != null)
//            {
//                if (args[0].equals(QUIT_CMD))
//                {
//                    break;
//                }
//                execute(args);
//            }
//        }
//    }
//
//    /**
//     * Prints the prompt of the console application and reads the command
//     * entered by the user.
//     *
//     * TODO make fancy
//     *
//     * @param prompt to output
//     * @return read aguments
//     */
//    private String[] prompt(String prompt)
//    {
//        try
//        {
//            System.out.print(prompt);
//            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//            return bufferRead.readLine().split("\\s+");
//        }
//        catch (IOException e)
//        {
//            return null;
//        }
//    }
//
//    /**
//     * Executes the entered command.
//     *
//     * @param args to pass
//     */
//    private void execute(String[] args)
//    {
//        AbstractVFSCommand cmd = VFS_COMMANDS.get(args[0]);
//
//        if(cmd != null)
//        {
//            cmd.execute(this, args);
//        }
//        else
//        {
//            usage();
//        }
//    }
//
//    /**
//     *
//     * @return the vDisk
//     */
//    public VDisk getVDisk( )
//    {
//        return vDisk;
//    }
//
//    /**
//     *
//     * @return the current/working directory
//     */
//    public VDirectory getCurrent( )
//    {
//        return current;
//    }
//
//    /**
//     * Sets the current/working directory
//     *
//     * @param dir to set
//     */
//    public void setCurrent(VDirectory dir)
//    {
//        this.current = dir;
//    }
//
//    /**
//     * Prints the usage of the console application
//     */
//    private static void usage()
//    {
//        System.out.println("Usage: vdisk <command>[ arguments]");
//        System.out.println();
//        System.out.println("Commands:");
//        for(AbstractVFSCommand cmd : VFS_COMMANDS.values())
//        {
//            cmd.help();
//        }
//    }
//
//    /**
//     * Checks if the minimum required arguments are passed, quit otherwise.
//     *
//     * @param arguments to check
//     * @param minArgumentLength required
//     */
//    private static void quitWithUsageIfLessThan(String[] arguments, int minArgumentLength)
//    {
//        if (arguments.length < minArgumentLength)
//        {
//            usage();
//            System.exit(1);
//        }
//    }
//}
