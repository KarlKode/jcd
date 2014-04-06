package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;

import java.util.HashMap;

public class VFSls extends AbstractVFSCommand
{
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        VDisk vDisk = console.getVDisk();

        switch (args.length)
        {
            case 1:
            {
                vDisk.list(console.getCurrent());
                break;
            }
            case 2:
            {
                VDirectory dir = vDisk.resolve(args[1]);
                if(dir == null)
                {
                    usage();
                }
                out(vDisk.list(dir));
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

    private void out(HashMap<String, VObject> list)
    {
        for(String key : list.keySet())
        {
            System.out.println(key);
        }
    }
}
