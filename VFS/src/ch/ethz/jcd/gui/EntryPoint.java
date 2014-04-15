package ch.ethz.jcd.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by leo on 15/04/14.
 */
public class EntryPoint extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("VFS Explorer");

        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent)loader.load(getClass().getResource("Main.fxml").openStream());
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        final MainController mainController = loader.getController();
        Scene scene = new Scene(root);

        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
