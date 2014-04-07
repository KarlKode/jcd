package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;

public class VFShelp extends AbstractVFSCommand
{
    @Override
    public void execute(VFSConsole console, String[] args)
    {
        System.out.println("Commands:");
        for(AbstractVFSCommand cmd : VFSConsole.VFS_COMMANDS.values())
        {
            cmd.help();
        }
    }

    @Override
    public void help()
    {
        System.out.println("\thelp");
    }
}
