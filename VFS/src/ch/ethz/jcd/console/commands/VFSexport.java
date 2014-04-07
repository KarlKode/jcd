package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.utils.VDisk;

import java.io.File;

public class VFSexport extends AbstractVFSCommand
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
                VFile source = resolveFile(console, args[2]);

                if(source != null)
                {
                    vDisk.exportToHost(source, file);
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
        System.out.println("\texport PATH_TO_HOST_FILE SOURCE");
    }
}
