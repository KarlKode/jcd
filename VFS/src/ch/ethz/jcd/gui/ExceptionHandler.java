package ch.ethz.jcd.gui;

import ch.ethz.jcd.main.exceptions.*;

/**
 * Created by leo on 10.05.14.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String title = "Error!";
        String message;

        if(e instanceof CopyException) {
            message = e.getMessage();
        } else if(e instanceof DeleteException){
            message = e.toString();
        } else if(e instanceof MoveException){
            message = e.toString();
        } else if(e instanceof ExportException){
            message = e.toString();
        } else if(e instanceof FindException){
            message = e.toString();
        } else if(e instanceof ImportException){
            message = e.toString();
        } else if(e instanceof ListException){
            message = e.toString();
        } else if(e instanceof MkDirException){
            message = e.toString();
        } else if(e instanceof MoveException){
            message = e.toString();
        } else if(e instanceof RenameException){
            message = e.toString();
        } else if(e instanceof ResolveException){
            message = e.toString();
        } else if(e instanceof TouchException){
            message = e.toString();
        }else{
            message = e.toString();
        }

        System.out.println("ExceptionHandler.uncaughtException");

        e.printStackTrace();
    }
}
