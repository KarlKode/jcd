package ch.ethz.jcd.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InputDialogController {
    private DialogResult result;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label labelInput;

    @FXML
    private Button buttonCancel;

    @FXML
    private Button buttonOk;

    @FXML
    private TextField textFieldInput;


    public DialogResult getResult(){
        return this.result;
    }

    public String getInput(){
        return this.textFieldInput.getText();
    }

    @FXML
    void onActionButtonOk(ActionEvent event) {
        this.result = DialogResult.OK;
        ((Stage)this.buttonCancel.getScene().getWindow()).close();
    }

    @FXML
    void onActionButtonCancel(ActionEvent event) {
        this.result = DialogResult.CANCEL;
        ((Stage)this.buttonCancel.getScene().getWindow()).close();
    }

    @FXML
    void initialize() {
        assert labelInput != null : "fx:id=\"labelInput\" was not injected: check your FXML file 'InputDialog.fxml'.";
        assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'InputDialog.fxml'.";
        assert buttonOk != null : "fx:id=\"buttonOk\" was not injected: check your FXML file 'InputDialog.fxml'.";
        assert textFieldInput != null : "fx:id=\"textFieldInput\" was not injected: check your FXML file 'InputDialog.fxml'.";
    }

    public void prepare(String labelString, String inputPromptText){
        this.textFieldInput.setPromptText(inputPromptText);
        this.labelInput.setText(labelString);
    }
}
