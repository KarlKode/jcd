//package ch.ethz.jcd.console.commands;
//
//import ch.ethz.jcd.console.VFSConsole;
//
//import java.io.IOException;
//
///**
// * This command provides functionality to the VFS similar to the unix command pwd.
// */
//public class VFSpwd extends AbstractVFSCommand
//{
//    /**
//     * NAME
//     *      pwd - print the name of current/working directory
//     * SYNOPSIS
//     *      pwd [OPTION]...
//     * DESCRIPTION
//     *      Print the full filename of the current working directory.
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
//        switch (args.length)
//        {
//            case 1:
//            {
//                try
//                {
//                    System.out.println(console.getCurrent().getPath());
//                    break;
//                }
//                catch (IOException ignored)
//                {
//                }
//            }
//            case 2:
//            {
//                if(args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
//                {
//                    help();
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
//        System.out.println("\tpwd");
//    }
//}
