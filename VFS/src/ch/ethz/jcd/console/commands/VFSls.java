//package ch.ethz.jcd.console.commands;
//
//import ch.ethz.jcd.console.VFSConsole;
//import ch.ethz.jcd.main.layer.VDirectory;
//import ch.ethz.jcd.main.layer.VObject;
//import ch.ethz.jcd.main.utils.VDisk;
//
//import java.util.HashMap;
//
///**
// * This command provides functionality to the VFS similar to the unix command ls.
// */
//public class VFSls extends AbstractVFSCommand
//{
//    /**
//     * NAME
//     *      ls - list directory contents
//     * SYNOPSIS
//     *      ls [OPTION]... [FILE]
//     * DESCRIPTION
//     *      Outputs the objects that the directory at given path contains. If no path
//     *      is given the current directory is used as destination.
//     *
//     *      -h, --help
//     *          prints information about usage
//     *
//     * @param console that executes the command
//     * @param args passed with the command
//     */
//    @Override
//    public void execute(VFSConsole console, String[] args)
//    {
//        VDisk vDisk = console.getVDisk();
//
//        switch (args.length)
//        {
//            case 1:
//            {
//                out(vDisk.list(console.getCurrent()));
//                break;
//            }
//            case 2:
//            {
//                if(args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
//                {
//                    help();
//                    break;
//                }
//
//                VDirectory destination = resolveDirectory(console, args[1]);
//
//                if(destination != null)
//                {
//                    out(vDisk.list(destination));
//                    break;
//                }
//            }
//            default:
//            {
//                usage();
//                break;
//            }
//        }
//    }
//
//    /**
//     * Prints the help of the concrete command.
//     */
//    @Override
//    public void help()
//    {
//        System.out.println("\tls [DEST]");
//    }
//
//    /**
//     * Prints a list of the given objects on the console.
//     *
//     * @param list to print
//     */
//    private void out(HashMap<String, VObject> list)
//    {
//        for(String key : list.keySet())
//        {
//            System.out.println(key);
//        }
//    }
//}
