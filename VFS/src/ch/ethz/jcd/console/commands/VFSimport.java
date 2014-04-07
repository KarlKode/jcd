package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.File;

public class VFSimport extends AbstractVFSCommand
{
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 3:
            {
                File file = new File(args[1]);
                args[2] = normPath(console, args[2]);
                VDirectory destination = (VDirectory) vDisk.resolve(args[2]);

                if(destination == null || !file.exists())
                {
                    usage();
                    break;
                }

                vDisk.importFromHost(file, destination);
                break;
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
        System.out.println("\timport PATH_TO_HOST_FILE DEST");
    }
}
