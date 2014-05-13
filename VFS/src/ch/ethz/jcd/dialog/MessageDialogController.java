package ch.ethz.jcd.dialog;

/**
 * Created by leo on 10.05.14.
 */
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MessageDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private Label detailsLabel;

    @FXML
    private Button actionButton;

    @FXML
    private HBox actionParent;

    @FXML
    private Button okButton;

    @FXML
    private HBox okParent;

    @FXML
    private Label messageLabel;

    @FXML
    void initialize() {
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'MessageDialog.fxml'.";
        assert detailsLabel != null : "fx:id=\"detailsLabel\" was not injected: check your FXML file 'MessageDialog.fxml'.";
        assert actionButton != null : "fx:id=\"actionButton\" was not injected: check your FXML file 'MessageDialog.fxml'.";
        assert actionParent != null : "fx:id=\"actionParent\" was not injected: check your FXML file 'MessageDialog.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'MessageDialog.fxml'.";
        assert okParent != null : "fx:id=\"okParent\" was not injected: check your FXML file 'MessageDialog.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'MessageDialog.fxml'.";


        cancelButton.setVisible(false);
        actionButton.setVisible(false);

        okButton.setOnAction((param) -> {
            ((Stage)this.actionButton.getScene().getWindow()).close();
        });
    }

    public void setMessage(Throwable ex){
        this.messageLabel.setText(ex.getClass().getSimpleName());
        this.detailsLabel.setText(getInnerCause(ex).getMessage());
    }

    private Throwable getInnerCause(Throwable ex){
        while(ex.getCause() != null){
            ex = ex.getCause();
        }
        return ex;
    }



}