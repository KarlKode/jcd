package ch.ethz.jcd.commands_test;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;

import java.util.Observer;

/**
 * Created by leo on 10.05.14.
 */
public class TouchCommand extends AbstractObservableCommand
{
    private final VDisk vdisk;
    private final String workingDir;
    private final String newFileName;

    public TouchCommand(Observer observer, VDisk vdisk, String workingDir, String newFileName)
    {
        super(observer);

        this.vdisk = vdisk;
        this.workingDir = workingDir;
        this.newFileName = newFileName;
    }

    public void execute()
    {
        super.preExecution();

        vdisk.touch((VDirectory) vdisk.resolve(workingDir), newFileName);

        super.postExecution();
    }
}
