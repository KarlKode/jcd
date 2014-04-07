package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;

import java.io.IOException;

public class VFSpwd extends AbstractVFSCommand
{
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        switch (args.length)
        {
            case 1:
            {
                try
                {
                    System.out.println(console.getCurrent().getPath());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
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
    public void help()
    {
        System.out.println("\tpwd");
    }
}
