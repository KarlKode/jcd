package ch.ethz.jcd.gui;

import ch.ethz.jcd.main.exceptions.command.*;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by leo on 10.05.14.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler
{

    public static Throwable shortThrowable(Throwable ex)
    {
        return ex.getCause() instanceof InvocationTargetException ? ex.getCause().getCause() : ex;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        String title = "Error!";
        String message;

        e.printStackTrace();

        //http://stackoverflow.com/questions/17279050/javafx-bad-logs-invocation-target-exception
        e = shortThrowable(e);

        if (e instanceof CopyException)
        {
            message = e.getMessage();
        } else if (e instanceof DeleteException)
        {
            message = e.toString();
        } else if (e instanceof MoveException)
        {
            message = e.toString();
        } else if (e instanceof ExportException)
        {
            message = e.toString();
        } else if (e instanceof FindException)
        {
            message = e.toString();
        } else if (e instanceof ImportException)
        {
            message = e.toString();
        } else if (e instanceof ListException)
        {
            message = e.toString();
        } else if (e instanceof MkDirException)
        {
            message = e.toString();
        } else if (e instanceof MoveException)
        {
            message = e.toString();
        } else if (e instanceof RenameException)
        {
            message = e.toString();
        } else if (e instanceof ResolveException)
        {
            message = e.toString();
        } else if (e instanceof TouchException)
        {
            message = e.toString();
        } else if (e instanceof TouchException)
        {
            message = e.toString();
        } else
        {
            message = e.toString();
        }


        //if we are allowed to use 3rd party dialogs
        Dialogs.create()
                .owner(new Stage())
                .title("Exception dialog")
                .masthead("An exception occured ..")
                .message(e.getMessage())
                .showException(e);

        //otherwise we have to use the own created Dialogs
        /*
        try{
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent)loader.load(MessageDialogController.class.getResource("MessageDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final MessageDialogController controller = loader.getController();
            dialogStage.setTitle(title);
            controller.setMessage(e);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        }catch(IOException ex){
            ex.printStackTrace();
        }*/
    }

}
