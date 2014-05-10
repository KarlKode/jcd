//package ch.ethz.jcd.console.commands;
//
//import ch.ethz.jcd.console.VFSConsole;
//import ch.ethz.jcd.main.layer.VFile;
//import ch.ethz.jcd.main.utils.VDisk;
//
//import java.util.HashMap;
//import java.util.regex.Pattern;
//
///**
// * This command provides functionality to the VFS similar to the unix command find.
// */
//public class VFSfind extends AbstractVFSCommand
//{
//    protected static final String OPTION_R = "-r";
//    protected static final String OPTION_RECURSIVE = "--recursive";
//
//    protected static final String OPTION_I = "-i";
//    protected static final String OPTION_CASE_INSENSITIVE = "--case-insensitive";
//
//    /**
//     * NAME
//     *      find - search for files in a directory hierarchy
//     * SYNOPSIS
//     *      find [-r] [-i] [expression]
//     * DESCRIPTION
//     *      find searches the directory tree rooted at each given starting-point by
//     *      evaluating the given expression from left to right.
//     *
//     *      -h, --help
//     *          prints information about usage
//     *      -i, --case-insensitive
//     *          enables case insensitive search
//     *      -r, --recursive
//     *          enables recursive search
//     *
//     * @param console that executes the command
//     * @param args    passed with the command
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
//                if (args[1].equals(AbstractVFSCommand.OPTION_H) || args[1].equals(AbstractVFSCommand.OPTION_HELP))
//                {
//                    help();
//                    break;
//                }
//                else
//                {
//                    Pattern pattern = Pattern.compile("\\w*"+args[1]+"\\w*");
//                    out(vDisk.find(pattern, console.getCurrent(), false));
//                    break;
//                }
//            }
//            case 3:
//            case 4:
//            {
//                boolean recursive = false;
//                boolean insensitive = false;
//                int expr = 3;
//
//                for(int i = 1; i < args.length; i++)
//                {
//                    if(args[i].equals(OPTION_R) || args[i].equals(OPTION_RECURSIVE))
//                    {
//                        recursive = true;
//                    }
//                    else if(args[i].equals(OPTION_I) || args[i].equals(OPTION_CASE_INSENSITIVE))
//                    {
//                        insensitive = true;
//                    }
//                    else
//                    {
//                        expr = Math.min(i, expr);
//                    }
//                }
//                Pattern pattern = insensitive ? Pattern.compile(args[expr], Pattern.CASE_INSENSITIVE) : Pattern.compile(args[expr]);
//                out(vDisk.find(pattern, console.getCurrent(), recursive));
//                break;
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
//        System.out.println("\tfind OPTIONS EXPRESSION");
//    }
//    /**
//     * Prints a list of the given objects on the console.
//     *
//     * @param list to print
//     */
//    private void out(HashMap<VFile, String> list)
//    {
//        for(String value : list.values())
//        {
//            System.out.println(value);
//        }
//    }
//}
