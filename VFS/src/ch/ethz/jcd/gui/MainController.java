package ch.ethz.jcd.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ch.ethz.jcd.dialog.SearchDialogController;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;


public class MainController {
    private Executor exec;


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<VObject> listViewFiles;

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

    private VDirectory selectedDirectory;

    private boolean ignoreSelectionChanged = false;

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
                    if (a instanceof VDirectory) {
                        final TreeItem<VDirectory> tmp = new TreeItem<VDirectory>((VDirectory) a);
                        tmp.setExpanded(true);
                        dirs.add(tmp);
                        currNode.getChildren().add(tmp);
                    }
                });
            }

            this.treeViewNavigation.setRoot(rootNode);
            this.treeViewNavigation.getRoot().setExpanded(true);
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
            this.treeViewNavigation.getSelectionModel().select(this.treeViewNavigation.getRoot());
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
            this.treeViewNavigation.getSelectionModel().select(this.treeViewNavigation.getRoot());
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
    void onActionMenuItemFind(ActionEvent event) {

    }

    @FXML
    void onTextChangedTextFieldPath(ActionEvent event) {

    }

    @FXML
    void onActionButtonGotoLocation(ActionEvent event) {

    }

    @FXML
    void onActionButtonImport(ActionEvent event) {

    }

    @FXML
    void onActionButtonExport(ActionEvent event) {

    }

    @FXML
    void onActionButtonDelete(ActionEvent event) {

    }

    @FXML
    void onActionButtonFind(ActionEvent event) {
        try{
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent)loader.load(SearchDialogController.class.getResource("SearchDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final SearchDialogController searchDialogController = loader.getController();

            searchDialogController.init(this.vdisk, this.selectedDirectory);

            dialogStage.setTitle("Search ... ");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    void onDragDetectedListViewFiles(MouseEvent event) {
        ObservableList<VObject> items = listViewFiles.getSelectionModel().getSelectedItems();
        Map<DataFormat, Object> files = new HashMap<>();
        List<File> aa = new ArrayList<File>();

        for(VObject d : items){
            try {
                if(d instanceof VFile){
                    File f = new File(System.getProperty("java.io.tmpdir") + "/" + d.getName());
                    vdisk.exportToHost((VFile)d, f);
                    aa.add(f);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        files.put(DataFormat.FILES, aa);

        Dragboard d = listViewFiles.startDragAndDrop(TransferMode.COPY);
        d.setContent(files);
    }

    @FXML
    void onDragDoneListViewFiles(DragEvent event) {
        System.out.println("MainController.onDragDoneListViewFiles");
    }

    @FXML
    void onDragDroppedListViewFiles(DragEvent event) {
        Dragboard db = event.getDragboard();

        boolean success = false;

        if (db.hasFiles()) {
            success = true;

            try {
                new Task<Void>(){
                    @Override
                    protected Void call() throws Exception {
                        Stack<Pair<File, VDirectory>> items = new Stack<Pair<File, VDirectory>>();

                        for(File file : db.getFiles()){
                            items.add(new Pair<File, VDirectory>(file, selectedDirectory));
                        }

                        //TODO: maybe better if we update the List/TreeView on the fly, and not after the complete operation is finished
                        while(!items.isEmpty()){
                            Pair<File, VDirectory> tmpItem = items.pop();
                            File tmpFile = tmpItem.getKey();
                            VDirectory tmpVDir = tmpItem.getValue();

                            if(tmpFile.isDirectory()){
                                VDirectory newVDir = vdisk.mkdir(tmpVDir, tmpFile.getName());

                                for(File file : tmpFile.listFiles()){
                                    items.add(new Pair<File, VDirectory>(file, newVDir));
                                }
                            }else{
                                VFile file = vdisk.importFromHost(tmpFile, tmpVDir);

                                //show progress
//                                if(tmpVDir.equals(selectedDirectory)){
//                                    refreshListView(selectedDirectory);
//                                }
                            }
                        }


                        ignoreSelectionChanged = true;
                        refreshTreeView();
                        ignoreSelectionChanged = false;

                        selectVDirectory(selectedDirectory);

                        return null;
                    }
                }.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private void selectVDirectory(VDirectory selected){
        Stack<VDirectory> path = new Stack<VDirectory>();
        VDirectory tmp = selected;

        path.add(tmp);
        while(tmp.getParent() != null){
            path.add(tmp.getParent());
            tmp = tmp.getParent();
        }

        TreeItem<VDirectory> dir = treeViewNavigation.getRoot();

        while(!path.isEmpty()){
            VDirectory t = path.pop();

            for(TreeItem<VDirectory> item : dir.getChildren()){
                if(item.getValue().equals(t)){
                    dir = item;
                    break;
                }
            }
        }

        treeViewNavigation.getSelectionModel().select(dir);
    }


    @FXML
    void onDragOverListViewFiles(DragEvent event) {
        Dragboard db = event.getDragboard();

        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    @FXML
    void initialize() {
        assert listViewFiles != null : "fx:id=\"paneContent\" was not injected: check your FXML file 'Main.fxml'.";
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

        treeViewNavigation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<VDirectory>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<VDirectory>> observable, TreeItem<VDirectory> oldValue, TreeItem<VDirectory> newValue) {
                if(!ignoreSelectionChanged){
                    selectedDirectory = newValue.getValue();
                    refreshListView(newValue.getValue());
                    selectVDirectory(selectedDirectory);
                }
            }
        });


        listViewFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listViewFiles.setCellFactory(new Callback<ListView<VObject>, ListCell<VObject>>() {
            @Override
            public ListCell<VObject> call(ListView<VObject> list) {
                return new DirectoryListCell();
            }
        });


//        listViewFiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<VObject>() {
//            @Override
//            public void changed(ObservableValue<? extends VObject> observableValue, VObject oldValue, VObject newValue) {
                //this approach doesn't work. If I just have this handler, i don't get events when i switch from a
                //  VDirectory-item to a VFile item and vis a vis
//            }
//        });

        listViewFiles.getSelectionModel().selectedItemProperty().addListener((a) ->{
            try {
                if(!ignoreSelectionChanged && ((ObservableValue<VObject>) a).getValue() != null) {
                    textFieldPath.setText(((ObservableValue<VObject>) a).getValue().getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: handle
            }
        });

        listViewFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(listViewFiles.getSelectionModel().getSelectedItems().size() == 1 && listViewFiles.getSelectionModel().getSelectedItem() instanceof VDirectory){
                    if(mouseEvent.getClickCount() == 2){
                        selectedDirectory = (VDirectory)listViewFiles.getSelectionModel().getSelectedItem();

                        selectVDirectory(selectedDirectory);
                    }
                }

            }
        });

        exec = Executors.newCachedThreadPool();

    }

    private void refreshListView(VDirectory dir) {
        try {
            textFieldPath.setText(dir.getPath());

            listViewFiles.getItems().clear();
            List<VObject> items = dir.getEntries();
            listViewFiles.getItems().addAll(items);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: handle
        }
    }

    public Node createFileIcon(VObject item){
        VBox box = new VBox();
        //Image icon = new Image();
        Label name = new Label();
        name.setWrapText(true);
        return box;
    }



    public class DirectoryListCell extends ListCell<VObject> {
        public DirectoryListCell() {
            super();
        }

        @Override
        protected void updateItem(VObject item, boolean empty) {
            super.updateItem(item, empty);

            if(!empty){
                try {
                    setText(item.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Font f = getFont();
                if(item instanceof VDirectory){
                    setFont(Font.font(f.getName(), FontWeight.BOLD, f.getSize()));
                }else{
                    setFont(Font.font(f.getName(), FontWeight.NORMAL, f.getSize()));
                }
            }else{
                setText("");
            }
        }
    }
}

