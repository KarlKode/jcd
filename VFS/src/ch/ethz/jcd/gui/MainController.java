package ch.ethz.jcd.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import ch.ethz.jcd.main.exceptions.InvalidBlockAddressException;
import ch.ethz.jcd.main.exceptions.InvalidBlockCountException;
import ch.ethz.jcd.main.exceptions.InvalidSizeException;
import ch.ethz.jcd.main.exceptions.VDiskCreationException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;
import ch.ethz.jcd.main.utils.VUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.swing.plaf.FileChooserUI;


public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TilePane paneContent;

    @FXML
    private MenuItem menuItemDelete;

    @FXML
    private BorderPane panePath;

    @FXML
    private Button buttonGotoLocation;

    @FXML
    private MenuItem menuItemCreateVFS;

    @FXML
    private MenuItem menuItemAdd;

    @FXML
    private TreeView<VDirectory> treeViewNavigation;

    @FXML
    private MenuItem menuItemCopy;

    @FXML
    private VBox paneBrowser;

    @FXML
    private MenuItem menuItemLoadVFS;

    @FXML
    private TextField textFieldPath;

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private Label labelPath;

    private VDisk vdisk;

    public MainController() {
    }

    private void refreshTreeView(){
        try {
            VDirectory root = (VDirectory) vdisk.resolve("/");

            Stack<TreeItem<VDirectory>> dirs =  new Stack<TreeItem<VDirectory>>();
            TreeItem<VDirectory> rootNode = new TreeItem<VDirectory>(root);
            dirs.add(rootNode);

            while(!dirs.isEmpty()){
                final TreeItem<VDirectory> currNode = dirs.pop();

                currNode.getValue().getEntries().forEach(a -> {
                   if(a instanceof VDirectory){
                       TreeItem<VDirectory> tmp = new TreeItem<VDirectory>((VDirectory)a);
                       dirs.add(tmp);
                       currNode.getChildren().add(tmp);
                    }
                });
            }

            this.treeViewNavigation.setRoot(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: dialog
        }


    }

    @FXML
    void onActionMenuItemCreateVFS(ActionEvent event) {
        final FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Create new VDisk.. ");

        final File fileVdisk = fchooser.showSaveDialog(null);

        try {
            // hm.. wouldn't it be better to return the new Vdisk?
            VDisk.format(fileVdisk, 100*VUtil.BLOCK_SIZE);
            this.vdisk = new VDisk(fileVdisk);

            VDirectory root = (VDirectory) this.vdisk.resolve("/");

            VDirectory home = this.vdisk.mkdir(root, "home");
            VDirectory phgamper = this.vdisk.mkdir(home, "phgamper");

            this.vdisk.mkdir(phgamper, "Pictures");
            this.vdisk.mkdir(phgamper, "Videos");
            this.vdisk.mkdir(phgamper, "Documents");
            this.vdisk.mkdir(phgamper, "Dropbox");

            VFile cache = this.vdisk.touch(phgamper, ".cache");
            VFile xorg = this.vdisk.touch(phgamper, "xorg.conf");
            VFile bar = this.vdisk.touch(phgamper, "bar.db");
            VFile foo = this.vdisk.touch(root, "foo.c");

            refreshTreeView();
        } catch (FileNotFoundException e) {
            //TODO: create dialog
            e.printStackTrace();
        } catch (InvalidBlockAddressException e) {
            e.printStackTrace();
        } catch (InvalidSizeException e) {
            e.printStackTrace();
        } catch (InvalidBlockCountException e) {
            e.printStackTrace();
        } catch (VDiskCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionMenuItemLoadVFS(ActionEvent event) {
        final FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Load VDisk.. ");

        final File fileVdisk = fchooser.showOpenDialog(null);

        try {
            this.vdisk = new VDisk(fileVdisk);
            refreshTreeView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //TODO: show dialog
        }
    }

    @FXML
    void onActionMenuItemClose(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void onActionMenuItemAdd(ActionEvent event) {

    }

    @FXML
    void onActionMenuItemCopy(ActionEvent event) {

    }

    @FXML
    void onActionMenuItemDelete(ActionEvent event) {

    }

    @FXML
    void onTextChangedTextFieldPath(ActionEvent event) {

    }

    @FXML
    void onActionButtonGotoLocation(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert paneContent != null : "fx:id=\"paneContent\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemDelete != null : "fx:id=\"menuItemDelete\" was not injected: check your FXML file 'Main.fxml'.";
        assert panePath != null : "fx:id=\"panePath\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonGotoLocation != null : "fx:id=\"buttonGotoLocation\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemCreateVFS != null : "fx:id=\"menuItemCreateVFS\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemAdd != null : "fx:id=\"menuItemAdd\" was not injected: check your FXML file 'Main.fxml'.";
        assert treeViewNavigation != null : "fx:id=\"treeViewNavigation\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemCopy != null : "fx:id=\"menuItemCopy\" was not injected: check your FXML file 'Main.fxml'.";
        assert paneBrowser != null : "fx:id=\"paneBrowser\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemLoadVFS != null : "fx:id=\"menuItemLoadVFS\" was not injected: check your FXML file 'Main.fxml'.";
        assert textFieldPath != null : "fx:id=\"textFieldPath\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Main.fxml'.";
        assert labelPath != null : "fx:id=\"labelPath\" was not injected: check your FXML file 'Main.fxml'.";

    }
}

