package ch.ethz.jcd.commands;

import ch.ethz.jcd.main.utils.VDisk;

import java.util.Observer;

/**
 * Created by leo on 10.05.14.
 */
public class NewFileCommand extends AbstractObservableCommand<NewFileParameter>{
    private final VDisk vdisk;

    public NewFileCommand(Observer observer, VDisk vdisk){
        this.vdisk = vdisk;
        this.addObserver(observer);
    }

    public void execute(NewFileParameter parameter){
        super.preExecution();


        super.postExecution();
    }
}
