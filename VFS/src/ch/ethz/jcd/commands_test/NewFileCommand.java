package ch.ethz.jcd.commands_test;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;

import java.io.*;
import java.util.Observer;

/**
 * Created by leo on 10.05.14.
 */
public class NewFileCommand extends AbstractObservableCommand implements Serializable{
    private final VDisk vdisk;
    private final String workingDir;
    private final String newFileName;
    private final File file;

    public NewFileCommand(Observer observer, VDisk vdisk, String workingDir, String newFileName, File file){
        super(observer);

        this.vdisk = vdisk;
        this.workingDir = workingDir;
        this.newFileName = newFileName;
        this.file = file;
    }

    public void execute(){
        super.preExecution();

        vdisk.importFromHost(file, (VDirectory)vdisk.resolve(workingDir));

        super.postExecution();
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.writeChars(workingDir);
        os.writeChars(newFileName);

        VUtil.copyStream(new FileInputStream(file), os);
    }
}
