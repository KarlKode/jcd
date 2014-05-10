//package ch.ethz.jcd.console.commands;
//
//import ch.ethz.jcd.console.VFSConsole;
//import ch.ethz.jcd.main.layer.VDirectory;
//import ch.ethz.jcd.main.utils.VDisk;
//
///**
// * This command provides functionality to the VFS similar to the unix command rm.
// */
//public class VFSrm extends AbstractVFSCommand
//{
//    /**
//     * NAME
//     *      rm - remove files or directories
//     * SYNOPSIS
//     *      rm [OPTION]... FILE...
//     * DESCRIPTION
//     *      Recursively removes the given file or directory and its underlying structure
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
//            case 2:
//            {
//                if(args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
//                {
//                    help();
//                    break;
//                }
//                VDirectory destination = resolveDirectory(console, args[1]);
//                if(destination != null)
//                {
//                    vDisk.delete(destination);
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
//        System.out.println("\trm FILE");
//    }
//}
