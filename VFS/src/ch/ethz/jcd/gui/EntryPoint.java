package ch.ethz.jcd.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * Created by leo on 15/04/14.
 */
public class EntryPoint extends Application {


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("VFS Explorer");

        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        try {
            root = (Parent)loader.load(EntryPoint.class.getResource("Main.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        final MainController mainController = loader.getController();
        Scene scene = new Scene(root);

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            Keys.setCtrlPressed(event.isControlDown());
            Keys.setAltPressed(event.isAltDown());
        });

        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            Keys.setCtrlPressed(event.isControlDown());
            Keys.setAltPressed(event.isAltDown());
        });

        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @Override
    public void stop(){
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
