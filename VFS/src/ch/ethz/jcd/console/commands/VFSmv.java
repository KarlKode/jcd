//package ch.ethz.jcd.console.commands;
//
//import ch.ethz.jcd.console.VFSConsole;
//import ch.ethz.jcd.main.layer.VDirectory;
//import ch.ethz.jcd.main.layer.VFile;
//import ch.ethz.jcd.main.utils.VDisk;
//
///**
// * This command provides functionality to the VFS similar to the unix command mv.
// */
//public class VFSmv extends AbstractVFSCommand
//{
//    /**
//     * NAME
//     *      mv - move (rename) files or directory
//     * SYNOPSIS
//     *      ls [OPTION]... SOURCE... DEST
//     * DESCRIPTION
//     *      Rename SOURCE to DEST, or move SOURCE(s) to DIRECTORY.
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
//            }
//            case 3:
//            {
//                String name = args[1];
//                VFile file = resolveFile(console, args[1]);
//                VDirectory destination = console.getCurrent();
//                if(args[1].split(VDisk.PATH_SEPARATOR).length > 1)
//                {
//                    name = args[1].substring(args[1].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
//                    destination = resolveDirectory(console, args[1].substring(0, args[1].lastIndexOf(VDisk.PATH_SEPARATOR)));
//                }
//
//                if(destination != null && file != null)
//                {
//                    vDisk.move(file, destination, name);
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
//        System.out.println("\tmv SOURCE DEST");
//    }
//}
