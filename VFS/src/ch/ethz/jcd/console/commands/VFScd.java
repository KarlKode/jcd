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
            case 2:
            {
                VDirectory destination = vDisk.resolve(args[0]);

                if(destination != null)
                {
                    console.setCurrent(destination);
                }
                else
                {
                    usage();
                }
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
    public void usage()
    {
        System.out.println("Error");
    }
}
