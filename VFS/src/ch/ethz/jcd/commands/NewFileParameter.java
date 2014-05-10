package ch.ethz.jcd.commands;

import ch.ethz.jcd.main.layer.VDirectory;

import java.io.File;

/**
 * Created by leo on 10.05.14.
 */
public class NewFileParameter {
    public final File file;
    public final String dir;

    public NewFileParameter(File file, String dir){
        this.file = file;
        this.dir = dir;
    }
}
