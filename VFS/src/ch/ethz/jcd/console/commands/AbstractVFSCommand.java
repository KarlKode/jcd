package ch.ethz.jcd.console.commands;

import ch.ethz.jcd.console.VFSConsole;

public abstract class AbstractVFSCommand
{
    public abstract void execute(VFSConsole console, String[] args);

    public abstract void usage( );
}
