package ch.ethz.jcd.dialog;

/**
 * Created by leo on 30.04.14.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ResourceBundle;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class NewVDiskController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label labelSize;

    @FXML
    private Button buttonCancel;

    @FXML
    private Button buttonCreate;

    @FXML
    private Button buttonChoose;

    @FXML
    private TextField textFieldFile;

    @FXML
    private Slider sliderFileSize;

    private long size;

    private DialogResult result;


    @FXML
    void onActionButtonChoose(ActionEvent event) {
        final FileChooser fchooser = new FileChooser();
        fchooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fchooser.setTitle("Create new VDisk.. ");

        File fileVdisk = fchooser.showSaveDialog(null);

        if(fileVdisk != null) {
            textFieldFile.setText(fileVdisk.getAbsolutePath());
        }else{
            textFieldFile.setText("");
        }
    }

    @FXML
    void onActionButtonCreate(ActionEvent event) {
        result = DialogResult.OK;
        ((Stage) this.buttonChoose.getScene().getWindow()).close();
    }

    @FXML
    void onActionButtonCancel(ActionEvent event) {
        result = DialogResult.CANCEL;
        ((Stage) this.buttonChoose.getScene().getWindow()).close();
    }

    @FXML
    void initialize() {
        assert labelSize != null : "fx:id=\"labelSize\" was not injected: check your FXML file 'NewVDiskDialog.fxml'.";
        assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'NewVDiskDialog.fxml'.";
        assert buttonCreate != null : "fx:id=\"buttonCreate\" was not injected: check your FXML file 'NewVDiskDialog.fxml'.";
        assert buttonChoose != null : "fx:id=\"buttonChoose\" was not injected: check your FXML file 'NewVDiskDialog.fxml'.";
        assert textFieldFile != null : "fx:id=\"textFieldFile\" was not injected: check your FXML file 'NewVDiskDialog.fxml'.";
        assert sliderFileSize != null : "fx:id=\"sliderFileSize\" was not injected: check your FXML file 'NewVDiskDialog.fxml'.";

        textFieldFile.setText(System.getProperty("user.dir"));
        sliderFileSize.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                labelSize.setText(new Double((newValue.doubleValue() * 1e3  * VUtil.BLOCK_SIZE)/1e6d).toString());
            }
        });

        sliderFileSize.setValue(5);
    }

    public File getVDiskFile() {
        if(new File(textFieldFile.getText()).isDirectory()){
            return new File(textFieldFile.getText() + "unnamed.vdisk");
        }
        return new File(textFieldFile.getText());
    }

    public DialogResult getResult(){
        return this.result;
    }

    public long getSize(){
        return Math.round(sliderFileSize.getValue() * 1e3  * VUtil.BLOCK_SIZE);
    }
}
