package ch.ethz.jcd.dialog;

import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SearchDialogController
{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField textFieldDirectory;


    @FXML
    private TextField textFieldFind;

    @FXML
    private Button buttonFind;

    @FXML
    private Button buttonCancel;

    @FXML
    private ListView<VObject> listViewResults;

    @FXML
    private CheckBox checkBoxSubdirectories;

    @FXML
    private CheckBox checkBoxCaseSensitive;

    private VDisk vdisk;

    private VDirectory directory;

    @FXML
    void onActionButtonFind(ActionEvent event)
    {
        Pattern pattern;

        if(checkBoxCaseSensitive.isSelected()){
            pattern = Pattern.compile(textFieldFind.getText());
        }else{
            pattern = Pattern.compile(textFieldFind.getText(), Pattern.CASE_INSENSITIVE);
        }


        HashMap<VFile, String> result = vdisk.find(pattern, ((VDirectory) vdisk.resolve(textFieldDirectory.getText())), checkBoxSubdirectories.isSelected());

        listViewResults.getItems().clear();
        listViewResults.getItems().addAll(result.keySet());
    }

    @FXML
    void onActionButtonCancel(ActionEvent event)
    {
        Stage parent = (Stage) this.buttonCancel.getScene().getWindow();
        parent.close();
    }

    @FXML
    void initialize()
    {
        assert textFieldDirectory != null : "fx:id=\"textFieldDirectory\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert textFieldFind != null : "fx:id=\"textFieldFind\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert buttonFind != null : "fx:id=\"buttonFind\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert buttonCancel != null : "fx:id=\"buttonCancel\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert listViewResults != null : "fx:id=\"listViewResults\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert checkBoxSubdirectories != null : "fx:id=\"checkBoxSubdirectories\" was not injected: check your FXML file 'SearchDialog.fxml'.";
        assert checkBoxCaseSensitive != null : "fx:id=\"checkBoxCaseSensitive\" was not injected: check your FXML file 'SearchDialog.fxml'.";

        this.listViewResults.setCellFactory(new Callback<ListView<VObject>, ListCell<VObject>>() {
            @Override
            public ListCell<VObject> call(ListView<VObject> param) {
                return new SearchResultListCell();
            }
        });
    }

    public void init(VDisk vdisk, VDirectory directory)
    {
        this.vdisk = vdisk;
        this.directory = directory;

        try
        {
            this.textFieldDirectory.setText(directory.getPath());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public class SearchResultListCell extends ListCell<VObject>
    {
        public SearchResultListCell()
        {
            super();
        }

        @Override
        protected void updateItem(VObject item, boolean empty)
        {
            super.updateItem(item, empty);

            if (!empty)
            {
                try
                {
                    setText(item.getPath());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                Font f = getFont();
                if (item instanceof VDirectory)
                {
                    setFont(Font.font(f.getName(), FontWeight.BOLD, f.getSize()));
                } else
                {
                    setFont(Font.font(f.getName(), FontWeight.NORMAL, f.getSize()));
                }
            } else
            {
                setText("");
            }
        }
    }

}
