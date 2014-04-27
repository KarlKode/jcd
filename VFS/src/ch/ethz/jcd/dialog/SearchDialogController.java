package ch.ethz.jcd.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchDialogController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField textFieldDirectory;

    @FXML
    private VBox main;

    @FXML
    private TextField textFieldFind;

    @FXML
    private Button buttonFind;

    @FXML
    private Button buttonCancel;

    @FXML
    private ListView<?> listViewResults;

    @FXML
    private CheckBox checkBoxSubdirectories;

    @FXML
    private CheckBox checkBoxCaseSensitive;

    private VDisk vdisk;

    private VDirectory directory;

    @FXML
    void onActionButtonFind(ActionEvent event) {

    }

    @FXML
    void onActionButtonCancel(ActionEvent event) {
        Stage parent = (Stage) this.buttonCancel.getScene().getWindow();
        parent.close();
    }

    @FXML
    void initialize() {
        assert textFieldDirectory != null : "fx:id=\"textFieldDirectory\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert textFieldFind != null : "fx:id=\"textFieldFind\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert buttonFind != null : "fx:id=\"buttonFind\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert listViewResults != null : "fx:id=\"listViewResults\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert checkBoxSubdirectories != null : "fx:id=\"checkBoxSubdirectories\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert checkBoxCaseSensitive != null : "fx:id=\"checkBoxCaseSensitive\" was not injected: check your FXML file 'SearchDialog.fxml'.";

    }

    public void init(VDisk vdisk, VDirectory directory) {
        this.vdisk = vdisk;
        this.directory = directory;
    }
}
