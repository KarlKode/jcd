package ch.ethz.jcd.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.ethz.jcd.dialog.DialogResult;
import ch.ethz.jcd.dialog.InputDialogController;
import ch.ethz.jcd.dialog.NewVDiskController;
import ch.ethz.jcd.dialog.SearchDialogController;
import ch.ethz.jcd.main.exceptions.command.*;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
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
    private ToolBar toolBarInterface;

    @FXML
    private MenuItem menuItemImport;

    @FXML
    private MenuItem menuItemDelete;

    @FXML
    private ListView<VObject> listViewFiles;

    @FXML
    private Text textStatus;

    @FXML
    private BorderPane panePath;

    @FXML
    private Button buttonExport;

    @FXML
    private Button buttonGotoLocation;

    @FXML
    private MenuItem menuItemCreateVFS;

    @FXML
    private MenuItem menuItemRename;

    @FXML
    private TreeView<VDirectory> treeViewNavigation;

    @FXML
    private Button buttonImport;

    @FXML
    private MenuItem menuItemCopy;

    @FXML
    private Button buttonFind;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonNewFile;

    @FXML
    private MenuItem menuItemLoadVFS;

    @FXML
    private TextField textFieldPath;

    @FXML
    private VBox mainPane;

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private MenuItem menuItemExport;

    @FXML
    private MenuItem menuItemFind;

    @FXML
    private Label labelPath;

    @FXML
    private Button buttonNewDir;

    private VDisk vdisk;

    private VDirectory selectedDirectory;

    private boolean ignoreSelectionChanged = false;


    public MainController() {

    }

    private void refreshTreeView() throws ResolveException, IOException {
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
    }

    @FXML
    void onActionMenuItemCreateVFS(ActionEvent event) {
        newVDisk();
    }

    @FXML
    void onActionMenuItemLoadVFS(ActionEvent event) {
        try {
            openVDisk();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionMenuItemClose(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void onActionMenuItemAdd(ActionEvent event) {
        importFiles();
    }

    @FXML
    void onActionMenuItemDelete(ActionEvent event) {
        deleteSelectedFiles();
    }

    @FXML
    void onActionMenuItemFind(ActionEvent event)
    {
        openFindDialog();
        throw new FindException(null);
    }

    @FXML
    void onTextChangedTextFieldPath(ActionEvent event) {

    }

    @FXML
    void onActionButtonGotoLocation(ActionEvent event) {

    }

    @FXML
    void onActionMenuItemCopy(ActionEvent event) {

    }

    @FXML
    void onActionButtonImport(ActionEvent event) {
        importFiles();
    }

    @FXML
    void onActionButtonExport(ActionEvent event) {
        try {
            exportFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionButtonDelete(ActionEvent event) {
        deleteSelectedFiles();
    }

    @FXML
    void onActionButtonFind(ActionEvent event) {
       openFindDialog();
    }


    @FXML
    void onActionButtonNewFile(ActionEvent event) {
        showInputDialog("New File ... ", "Filename:", "Filename", filename -> {
            this.vdisk.touch(selectedDirectory, filename);
            refreshListView(selectedDirectory);
        });
    }

    @FXML
    void onActionButtonNewDir(ActionEvent event) {
        showInputDialog("New Directory ... ", "Directory name:", "Directory name", dirname -> {
            this.vdisk.mkdir(selectedDirectory, dirname);

            ignoreSelectionChanged = true;

            try {
                refreshTreeView();
                refreshListView(selectedDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ignoreSelectionChanged = false;

        });
    }


    @FXML
    void onKeyPressedMainPane(KeyEvent event) throws IOException {
        if(event.isControlDown()){
            if(event.getCode() == KeyCode.C){
                copySelectedFiles();
            }else if(event.getCode() == KeyCode.V){
                pasteSelectedFiles();
            }else if(event.getCode() == KeyCode.D) {
                deleteSelectedFiles();
            }else if(event.getCode() == KeyCode.R) {
                renameSelectedFile();
            }else if(event.getCode() == KeyCode.F) {
                openFindDialog();
            }else if(event.getCode() == KeyCode.I) {
                importFiles();
            }else if(event.getCode() == KeyCode.E) {
                exportFiles();
            }else if(event.getCode() == KeyCode.ENTER) {
                enterDirectory();
            }else if(event.getCode() == KeyCode.O) {
                openVDisk();
            }else if(event.getCode() == KeyCode.N) {
                newVDisk();
            }
        }else if(event.getCode() == KeyCode.DELETE) {
            deleteSelectedFiles();
        }else if(event.getCode() == KeyCode.BACK_SPACE) {
            gotoParent();
        }else if(event.getCode() == KeyCode.ENTER) {
            enterDirectory();
        }
    }

    private void newVDisk() {
        try{
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent)loader.load(SearchDialogController.class.getResource("NewVDiskDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final NewVDiskController controller = loader.getController();

            dialogStage.setTitle("New VDisk ... ");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            VDisk.format(controller.getVDiskFile(), controller.getSize(), false);
            vdisk = new VDisk(controller.getVDiskFile());

            refreshTreeView();
            this.treeViewNavigation.getSelectionModel().select(this.treeViewNavigation.getRoot());
            this.toolBarInterface.setDisable(false);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void openVDisk() throws ResolveException, IOException {
        final FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Load VDisk.. ");

        final File fileVdisk = fchooser.showOpenDialog(null);
        if(fileVdisk != null) {
            try {
                this.vdisk = new VDisk(fileVdisk);
                refreshTreeView();
                this.treeViewNavigation.getSelectionModel().select(this.treeViewNavigation.getRoot());
                this.toolBarInterface.setDisable(false);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            //no file was chosen / user pressed esc
        }
    }

    private void gotoParent(){
        if(selectedDirectory.getParent() != null){
            selectedDirectory = selectedDirectory.getParent();
            selectVDirectory(selectedDirectory);
        }
    }

    private void enterDirectory(){
        if(listViewFiles.getSelectionModel().getSelectedItems().size() == 1 && listViewFiles.getSelectionModel().getSelectedItem() instanceof VDirectory){
            selectedDirectory = (VDirectory)listViewFiles.getSelectionModel().getSelectedItem();
            selectVDirectory(selectedDirectory);
        }
    }


    private void importFiles() {
        final FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Import files .. ");

        final List<File> filesToImport = fchooser.showOpenMultipleDialog(null);

        if(filesToImport != null) {
            try {
                new Task<Void>(){
                    @Override
                    protected Void call() throws Exception {
                        importFiles(filesToImport);
                        return null;
                    }
                }.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //no file was chosen / user pressed esc
        }
    }

    private void exportFiles() throws IOException, ExportException {
        final DirectoryChooser dchooser = new DirectoryChooser();
        dchooser.setTitle("Export files .. ");

        final File exportDir = dchooser.showDialog(null);

        if(exportDir != null) {
            new Task<Void>(){
                @Override
                protected Void call() throws IOException, ExportException {
                    final ObservableList<VObject> items = listViewFiles.getSelectionModel().getSelectedItems();

                    for(VObject d : items){
                        if(d instanceof VFile){
                            File f = new File(exportDir.getAbsolutePath() + "/" + d.getName());
                            vdisk.exportToHost((VFile)d, f);
                        }
                    }

                    return null;
                }
            }.call();
        }else{
            //no file was chosen / user pressed esc
        }
    }

    private void copySelectedFiles() {

    }

    private void pasteSelectedFiles() {

    }

    private void openFindDialog() {
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
            if(d instanceof VFile){
                File f = null;
                try {
                    f = new File(System.getProperty("java.io.tmpdir") + "/" + d.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                vdisk.exportToHost((VFile)d, f);
                aa.add(f);
            }
        }

        files.put(DataFormat.FILES, aa);

        Dragboard d = listViewFiles.startDragAndDrop(TransferMode.MOVE);
        d.setContent(files);
    }

    @FXML
    void onDragDoneListViewFiles(DragEvent event) {
        System.out.println("MainController.onDragDoneListViewFiles");
    }

    private void importFiles(List<File> files) throws MkDirException, ImportException, ResolveException, IOException {
        Stack<Pair<File, VDirectory>> items = new Stack<Pair<File, VDirectory>>();

        for(File file : files){
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
//                if(tmpVDir.equals(selectedDirectory)){
//                    refreshListView(selectedDirectory);
//                }
            }
        }

        ignoreSelectionChanged = true;
        refreshTreeView();
        ignoreSelectionChanged = false;

        selectVDirectory(selectedDirectory);
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
                    protected Void call() throws IOException {
                        importFiles(db.getFiles());
                        return null;
                    }
                }.call();
            } catch (IOException e) {
                throw new ImportException(e);

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
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        } else {
            event.consume();
        }
    }

    @FXML
    void initialize() {
        assert toolBarInterface != null : "fx:id=\"toolBarInterface\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemImport != null : "fx:id=\"menuItemImport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemDelete != null : "fx:id=\"menuItemDelete\" was not injected: check your FXML file 'Main.fxml'.";
        assert listViewFiles != null : "fx:id=\"listViewFiles\" was not injected: check your FXML file 'Main.fxml'.";
        assert textStatus != null : "fx:id=\"textStatus\" was not injected: check your FXML file 'Main.fxml'.";
        assert panePath != null : "fx:id=\"panePath\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonExport != null : "fx:id=\"buttonExport\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonGotoLocation != null : "fx:id=\"buttonGotoLocation\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemCreateVFS != null : "fx:id=\"menuItemCreateVFS\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemRename != null : "fx:id=\"menuItemRename\" was not injected: check your FXML file 'Main.fxml'.";
        assert treeViewNavigation != null : "fx:id=\"treeViewNavigation\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonImport != null : "fx:id=\"buttonImport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemCopy != null : "fx:id=\"menuItemCopy\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonFind != null : "fx:id=\"buttonFind\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonDelete != null : "fx:id=\"buttonDelete\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonNewFile != null : "fx:id=\"buttonNewFile\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemLoadVFS != null : "fx:id=\"menuItemLoadVFS\" was not injected: check your FXML file 'Main.fxml'.";
        assert textFieldPath != null : "fx:id=\"textFieldPath\" was not injected: check your FXML file 'Main.fxml'.";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemClose != null : "fx:id=\"menuItemClose\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemExport != null : "fx:id=\"menuItemExport\" was not injected: check your FXML file 'Main.fxml'.";
        assert menuItemFind != null : "fx:id=\"menuItemFind\" was not injected: check your FXML file 'Main.fxml'.";
        assert labelPath != null : "fx:id=\"labelPath\" was not injected: check your FXML file 'Main.fxml'.";
        assert buttonNewDir != null : "fx:id=\"buttonNewDir\" was not injected: check your FXML file 'Main.fxml'.";

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
        listViewFiles.getSelectionModel().selectedItemProperty().addListener((a) ->{
            try {
                if(!ignoreSelectionChanged && ((ObservableValue<VObject>) a).getValue() != null) {
                    textFieldPath.setText(((ObservableValue<VObject>) a).getValue().getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        listViewFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2) {
                    enterDirectory();
                }
            }
        });
    }

    private void renameSelectedFile() {

    }

    private void deleteSelectedFiles() {
        try {
            new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    final ObservableList<VObject> items = listViewFiles.getSelectionModel().getSelectedItems();

                    for(VObject d : items){
                        if(d instanceof VFile){
                            System.out.println(d.getName());
                            vdisk.delete(d);
                        }
                    }

                    return null;
                }
            }.call();

            ignoreSelectionChanged = true;
            refreshTreeView();
            ignoreSelectionChanged = false;

            selectVDirectory(selectedDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    private void showInputDialog(String title, String labelString, String promptString, Consumer<String> actionOk){
        try{
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent)loader.load(InputDialogController.class.getResource("InputDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final InputDialogController inputDialogController = loader.getController();

            inputDialogController.prepare(labelString, promptString);

            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if(inputDialogController.getResult() == DialogResult.OK){
                actionOk.accept(inputDialogController.getInput());
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

