package ch.ethz.jcd.dialog;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressDialogController implements Observer{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button buttonCancel;

    @FXML
    private ProgressBar progressBarStatus;

    @FXML
    private Label labelMessage;

    @FXML
    private Label labelInformation;

    @FXML
    void onActionButtonCancel(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'ProgressDialog.fxml'.";
        assert progressBarStatus != null : "fx:id=\"progressBarStatus\" was not injected: check your FXML file 'ProgressDialog.fxml'.";
        assert labelMessage != null : "fx:id=\"labelMessage\" was not injected: check your FXML file 'ProgressDialog.fxml'.";
        assert labelInformation != null : "fx:id=\"labelInformation\" was not injected: check your FXML file 'ProgressDialog.fxml'.";

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void setStatus(double status) {
        this.progressBarStatus.setProgress(status);
    }
}