package ch.ethz.jcd.commands_test;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

import java.util.Observer;

/**
 * Created by leo on 10.05.14.
 */
public class NewDirectoryCommand extends AbstractObservableCommand
{
    private final VDisk vdisk;
    private final String workingDir;
    private final String newDirName;

    public NewDirectoryCommand(Observer observer, VDisk vdisk, String workingDir, String newDirName)
    {
        super(observer);

        this.vdisk = vdisk;
        this.workingDir = workingDir;
        this.newDirName = newDirName;
    }

    public void execute()
    {
        super.preExecution();

        vdisk.mkdir((VDirectory) vdisk.resolve(workingDir), newDirName);

        super.postExecution();
    }
}
