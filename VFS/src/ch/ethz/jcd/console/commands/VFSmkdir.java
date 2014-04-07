package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

public class VFSmkdir extends AbstractVFSCommand
{
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 2:
            {
                VDirectory destination = console.getCurrent();
                if(args[1].split(VDisk.PATH_SEPARATOR).length > 1)
                {
                    args[1] = args[1].substring(args[1].lastIndexOf(VDisk.PATH_SEPARATOR) + 1);
                    destination = resolveDirectory(console, args[1].substring(0, args[1].lastIndexOf(VDisk.PATH_SEPARATOR)+1));
                }

                if(destination != null)
                {
                    vDisk.mkdir(destination, args[1]);
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
        System.out.println("\tmkdir DEST");
    }
}
