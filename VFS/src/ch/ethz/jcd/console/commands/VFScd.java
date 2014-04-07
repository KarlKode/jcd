package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

public class VFScd extends AbstractVFSCommand
{
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 1:
            {
                console.setCurrent((VDirectory) vDisk.resolve(VDisk.PATH_SEPARATOR));
            }
            case 2:
            {

                VDirectory destination = resolveDirectory(console, args[1]);
                if (destination != null)
                {
                    console.setCurrent(destination);
                    break;
                }
            }
            default:
            {
                usage();
                break;
            }
        }
    }

    @Override
    public void help()
    {
        System.out.println("\tcd [DEST]");
    }
}
