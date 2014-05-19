package ch.ethz.jcd.gui;

import ch.ethz.jcd.dialog.*;
import ch.ethz.jcd.main.exceptions.DiskFullException;
import ch.ethz.jcd.main.exceptions.command.ExportException;
import ch.ethz.jcd.main.exceptions.command.ImportException;
import ch.ethz.jcd.main.exceptions.command.MkDirException;
import ch.ethz.jcd.main.exceptions.command.ResolveException;
import ch.ethz.jcd.main.layer.VDirectory;
import ch.ethz.jcd.main.layer.VFile;
import ch.ethz.jcd.main.layer.VObject;
import ch.ethz.jcd.main.utils.VDisk;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;


public class MainController
{
    //reference to all selected files in the listview
    private final List<VObject> selectedFiles = new ArrayList<VObject>();
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
    private MenuItem menuItemPaste;
    @FXML
    private Label labelPath;
    @FXML
    private Button buttonNewDir;

    @FXML
    private Label labelVStats;

    private VDisk vdisk;
    private VDirectory selectedDirectory;
    private boolean ignoreSelectionChanged = false;
    private FileOperation fileOp;

    private Stage progressDialogStage;

    private ProgressDialogController progressDialogController;

    //true, if the drag&drag operation initialized in this application
    private boolean inAppDragOperation;

    private boolean operationInProgress = false;
    private ClipboardContent filesToCopyClipboard = new ClipboardContent();

    private void refreshTreeView() throws ResolveException, IOException
    {
        VDirectory root = (VDirectory) vdisk.resolve("/");

        Stack<TreeItem<VDirectory>> dirs = new Stack<TreeItem<VDirectory>>();
        TreeItem<VDirectory> rootNode = new TreeItem<VDirectory>(root);
        dirs.add(rootNode);

        while (!dirs.isEmpty()) {
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
    void onActionMenuItemCreateVFS(ActionEvent event)
    {
        newVDisk();
    }

    @FXML
    void onActionMenuItemLoadVFS(ActionEvent event)
    {
        try {
            openVDisk();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionMenuItemClose(ActionEvent event)
    {
        Platform.exit();
    }

    @FXML
    void onActionMenuItemAdd(ActionEvent event)
    {
        importFiles();
    }

    @FXML
    void onActionMenuItemDelete(ActionEvent event)
    {
        deleteSelectedFiles();
    }

    @FXML
    void onActionMenuItemRename(ActionEvent event)
    {
        renameSelectedFile();
    }

    @FXML
    void onActionMenuItemFind(ActionEvent event) {
        openFindDialog();
    }

    @FXML
    void onTextChangedTextFieldPath(ActionEvent event) {

    }

    @FXML
    void onActionButtonGotoLocation(ActionEvent event)
    {

    }

    @FXML
    void onActionMenuItemCopy(ActionEvent event)
    {
        copySelectedFiles();
    }

    @FXML
    void onActionMenuItemPaste(ActionEvent event) throws IOException
    {
        pasteSelectedFiles();
    }

    @FXML
    void onActionButtonImport(ActionEvent event)
    {
        importFiles();
    }

    @FXML
    void onActionButtonExport(ActionEvent event)
    {
        try {
            exportFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionMenuItemExport(ActionEvent event) throws IOException
    {
        exportFiles();
    }

    @FXML
    void onActionMenuItemMove(ActionEvent event) throws IOException
    {
        moveSelectedFiles();
    }

    @FXML
    void onActionButtonDelete(ActionEvent event)
    {
        deleteSelectedFiles();
    }

    @FXML
    void onActionButtonFind(ActionEvent event)
    {
        openFindDialog();
    }

    @FXML
    void onActionButtonRename(ActionEvent event)
    {
        renameSelectedFile();
    }

    @FXML
    void onActionButtonNewFile(ActionEvent event) {
        showInputDialog("New File ... ", "Filename:", "Filename", filename -> {
            this.vdisk.touch(selectedDirectory, filename);
            refreshListView(selectedDirectory);
        });
    }

    @FXML
    void OnMouseClickedlabelVStats(MouseEvent event) {
        if (vdisk != null) {
            this.labelVStats.setText(this.vdisk.stats().toString());
        }
    }

    @FXML
    void onActionButtonNewDir(ActionEvent event)
    {
        showInputDialog("New Directory ... ", "Directory name:", "Directory name", dirname -> {
            this.vdisk.mkdir(selectedDirectory, dirname);

            try {
                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void onKeyPressedMainPane(KeyEvent event) throws IOException {
        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.C)
            {
                copySelectedFiles();
            } else if (event.getCode() == KeyCode.X) {
                moveSelectedFiles();
            } else if (event.getCode() == KeyCode.V) {
                pasteSelectedFiles();
            } else if (event.getCode() == KeyCode.D) {
                deleteSelectedFiles();
            } else if (event.getCode() == KeyCode.R) {
                renameSelectedFile();
            } else if (event.getCode() == KeyCode.F) {
                openFindDialog();
            } else if (event.getCode() == KeyCode.I) {
                importFiles();
            } else if (event.getCode() == KeyCode.E) {
                exportFiles();
            } else if (event.getCode() == KeyCode.ENTER) {
                enterDirectory();
            } else if (event.getCode() == KeyCode.O) {
                openVDisk();
            } else if (event.getCode() == KeyCode.N) {
                newVDisk();
            }
        } else if (event.getCode() == KeyCode.DELETE)
        {
            deleteSelectedFiles();
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            gotoParent();
        } else if (event.getCode() == KeyCode.ENTER) {
            enterDirectory();
        }
    }

    private void newVDisk() {
        try
        {
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(SearchDialogController.class.getResource("NewVDiskDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final NewVDiskController controller = loader.getController();

            dialogStage.setTitle("New VDisk ... ");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (controller.getResult() == DialogResult.OK)
            {
                VDisk.format(controller.getVDiskFile(), controller.getSize(), false, false);
                vdisk = new VDisk(controller.getVDiskFile());

                refreshTreeView();
                this.treeViewNavigation.getSelectionModel().select(this.treeViewNavigation.getRoot());
                this.toolBarInterface.setDisable(false);

                labelVStats.setText(this.vdisk.stats().toString());
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void openVDisk() throws ResolveException, IOException
    {
        final FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Load VDisk.. ");

        final File fileVdisk = fchooser.showOpenDialog(null);
        if (fileVdisk != null)
        {
            try {
                this.vdisk = new VDisk(fileVdisk);
                refreshTreeView();
                this.treeViewNavigation.getSelectionModel().select(this.treeViewNavigation.getRoot());
                this.toolBarInterface.setDisable(false);

                labelVStats.setText(this.vdisk.stats().toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //no file was chosen / user pressed esc
        }
    }

    private void gotoParent()
    {
        if (selectedDirectory.getParent() != null)
        {
            selectedDirectory = selectedDirectory.getParent();
            selectVDirectory(selectedDirectory);
        }
    }

    private void enterDirectory() {
        if (listViewFiles.getSelectionModel().getSelectedItems().size() == 1 && listViewFiles.getSelectionModel().getSelectedItem() instanceof VDirectory) {
            selectedDirectory = (VDirectory) listViewFiles.getSelectionModel().getSelectedItem();
            selectVDirectory(selectedDirectory);
        }
    }

    private void moveSelectedFiles() {
        fileOp = FileOperation.MOVE;

        this.selectedFiles.clear();
        this.selectedFiles.addAll(this.listViewFiles.getSelectionModel().getSelectedItems());

        this.menuItemPaste.setDisable(false);
    }

    private void importFiles() {
        final FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Import files .. ");

        final List<File> filesToImport = fchooser.showOpenMultipleDialog(null);

        if (filesToImport != null)
        {
            try
            {
                new Task<Void>()
                {
                    @Override
                    protected Void call() throws Exception
                    {
                        importFiles(filesToImport);
                        return null;
                    }
                }.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
        {
            //no file was chosen / user pressed esc
        }
    }

    private void exportFiles() throws IOException, ExportException {
        final DirectoryChooser dchooser = new DirectoryChooser();
        dchooser.setTitle("Export files .. ");

        final File exportDir = dchooser.showDialog(null);

        if (exportDir != null) {
            new Task<Void>() {
                @Override
                protected Void call() throws IOException, ExportException {
                    final ObservableList<VObject> items = listViewFiles.getSelectionModel().getSelectedItems();

                    for (VObject d : items) {
                        if (d instanceof VFile) {
                            File f = new File(exportDir.getAbsolutePath() + "/" + d.getName());
                            vdisk.exportToHost((VFile) d, f);
                        }
                    }

                    return null;
                }
            }.call();
        } else {
            //no file was chosen / user pressed esc
        }
    }

    private void copySelectedFiles() {
        fileOp = FileOperation.COPY;

        this.selectedFiles.clear();
        this.selectedFiles.addAll(this.listViewFiles.getSelectionModel().getSelectedItems());

        this.menuItemPaste.setDisable(false);
    }

    private void pasteSelectedFiles() throws IOException
    {
        for (VObject vobj : this.selectedFiles) {
            try {
                if (fileOp == FileOperation.COPY) {
                    if (selectedDirectory.getEntries().contains(vobj)) {
                        DialogResult res = showMessageDialog("Information", "File already exists!", "Rename it, and copy it?");

                        if (res == DialogResult.OK) {
                            showInputDialog("New Filename ... ", "Filename: ", "filename", (filename) -> {
                                vdisk.copy(vobj, selectedDirectory, filename);
                            });
                        }
                    } else {
                        vdisk.copy(vobj, selectedDirectory, vobj.getName());
                    }
                } else {
                    //moving the selectedFiles to the same directoy is senseless, thus we doesn't allow it
                    if (!vobj.getParent().equals(selectedDirectory)) {
                        if (selectedDirectory.getEntries().contains(vobj)) {
                            DialogResult res = showMessageDialog("Information", "File already exists!", "Rename it, and copy it?");

                            if (res == DialogResult.OK) {
                                showInputDialog("New Filename ... ", "Filename: ", "filename", (filename) -> {
                                    vdisk.move(vobj, selectedDirectory, filename);
                                });
                            }
                        } else {
                            vdisk.move(vobj, selectedDirectory, vobj.getName());
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.selectedFiles.clear();
        this.menuItemPaste.setDisable(true);
        this.fileOp = FileOperation.NULL;

        updateUI();
    }

    private void updateUI() throws IOException
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ignoreSelectionChanged = true;
                try {
                    refreshTreeView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ignoreSelectionChanged = false;

                selectVDirectory(selectedDirectory);
            }
        });
    }

    private void openFindDialog() {
        try
        {
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(SearchDialogController.class.getResource("SearchDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final SearchDialogController searchDialogController = loader.getController();

            searchDialogController.init(this.vdisk, this.selectedDirectory);

            dialogStage.setTitle("Search ... ");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    void onDragDetectedListViewFiles(MouseEvent event)
    {
        inAppDragOperation = true;

        //for in Application Move/Copy Operation
        copySelectedFiles();

        ObservableList<VObject> selectedVFiles = listViewFiles.getSelectionModel().getSelectedItems();
        Map<DataFormat, Object> dragDropMap = new HashMap<>();
        List<File> tmpFilesForExport = new ArrayList<File>();

        // export just selected VFiles, our API doesn't support VDirectories
        for (VObject vobject : selectedVFiles) {
            if (vobject instanceof VFile) {
                File tempFile = null;

                try {
                    tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + vobject.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                vdisk.exportToHost((VFile) vobject, tempFile);
                tmpFilesForExport.add(tempFile);
            }
        }

        dragDropMap.put(DataFormat.FILES, tmpFilesForExport);

        TransferMode mode;

        if (Keys.isCtrlPressed()) {
            mode = TransferMode.COPY;
        } else {
            mode = TransferMode.MOVE;
        }

        Dragboard dragboard = listViewFiles.startDragAndDrop(mode);
        dragboard.setContent(dragDropMap);
    }

    @FXML
    void onDragDoneListViewFiles(DragEvent event) {
        inAppDragOperation = false;
        System.out.println("MainController.onDragDoneListViewFiles");
    }

    private void importFiles(List<File> files) throws MkDirException, ImportException, ResolveException, IOException, DiskFullException {
        Stack<Pair<File, VDirectory>> items = new Stack<Pair<File, VDirectory>>();

        for (File file : files) {
            items.add(new Pair<File, VDirectory>(file, selectedDirectory));
        }

        while (!items.isEmpty()) {
            Pair<File, VDirectory> tmpItem = items.pop();
            File tmpFile = tmpItem.getKey();
            VDirectory tmpVDir = tmpItem.getValue();
            TreeItem<VDirectory> treeVDir = getTreeItem(tmpVDir);

            if (tmpFile.isDirectory()) {
                VDirectory newVDir = vdisk.mkdir(tmpVDir, tmpFile.getName());
                treeVDir.getChildren().add(new TreeItem<VDirectory>(newVDir));

                for (File file : tmpFile.listFiles()) {
                    items.add(new Pair<File, VDirectory>(file, newVDir));
                }
            } else {
                VFile file = vdisk.importFromHost(tmpFile, tmpVDir);
                if (tmpVDir.equals(selectedDirectory)) {
                    listViewFiles.getItems().add(file);
                }
            }
        }
    }

    @FXML
    void onDragDroppedListViewFiles(DragEvent event)
    {
        Dragboard db = event.getDragboard();
        System.out.println("MainController.onDragDroppedListViewFiles");

        boolean success = false;

        if (db.hasFiles())
        {
            success = true;

            new Task<Void>()
            {
                @Override
                protected Void call() throws DiskFullException {
                    try{
                        if (inAppDragOperation)
                        {
                            //dirty, should be removed with Copy Command
                            if (event.getTransferMode() == TransferMode.COPY)
                            {
                                fileOp = FileOperation.COPY;
                            } else
                            {
                                fileOp = FileOperation.MOVE;
                            }

                            pasteSelectedFiles();
                        } else
                        {
                            importFiles(db.getFiles());
                        }
                    }catch(IOException ex) {
                        ex.printStackTrace();
                    }

                    return null;
                }
            }.call();
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private TreeItem<VDirectory> getTreeItem(VDirectory vdir)
    {
        Stack<VDirectory> path = new Stack<VDirectory>();
        VDirectory tmp = vdir;
        TreeItem<VDirectory> dir = treeViewNavigation.getRoot();

        path.add(tmp);
        while (tmp.getParent() != null)
        {
            path.add(tmp.getParent());
            tmp = tmp.getParent();
        }

        while (!path.isEmpty())
        {
            VDirectory t = path.pop();

            for (TreeItem<VDirectory> item : dir.getChildren())
            {
                if (item.getValue().equals(t))
                {
                    dir = item;
                    break;
                }
            }
        }

        return dir;
    }


    private void selectVDirectory(VDirectory selected)
    {
        treeViewNavigation.getSelectionModel().select(getTreeItem(selected));
    }


    @FXML
    void onDragOverTreeViewNavigation(DragEvent event)
    {
        Dragboard db = event.getDragboard();

        if (db.hasFiles())
        {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        } else
        {
            event.consume();
        }
    }

    @FXML
    void onDragOverListViewFiles(DragEvent event)
    {
        Dragboard db = event.getDragboard();

        if (db.hasFiles())
        {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        } else
        {
            event.consume();
        }
    }

    @FXML
    void initialize()
    {
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

        //i had to implement the drag drop logic in TreeCell, otherwise i'd have no chance to get the target directory
        treeViewNavigation.setCellFactory(new Callback<TreeView<VDirectory>, TreeCell<VDirectory>>()
        {
            @Override
            public TreeCell<VDirectory> call(TreeView<VDirectory> directoryTreeView)
            {
                TreeCell<VDirectory> treeCell = new TreeCell<VDirectory>()
                {
                    protected void updateItem(VDirectory item, boolean empty)
                    {
                        super.updateItem(item, empty);

                        if (!empty && item != null)
                        {
                            try
                            {
                                setText(item.getName());
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            setGraphic(getTreeItem().getGraphic());
                        } else
                        {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };

                treeCell.setOnDragOver(new EventHandler<DragEvent>()
                {
                    @Override
                    public void handle(DragEvent event)
                    {
                        Dragboard db = event.getDragboard();

                        if (db.hasFiles())
                        {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        } else
                        {
                            event.consume();
                        }
                    }
                });

                treeCell.setOnDragDropped(new EventHandler<DragEvent>()
                {

                    @Override
                    public void handle(DragEvent event)
                    {
                        Dragboard db = event.getDragboard();

                        boolean success = false;

                        if (db.hasFiles())
                        {
                            success = true;

                            selectedDirectory = treeCell.getItem();

                            try
                            {
                                new Task<Void>()
                                {
                                    @Override
                                    protected Void call() throws Exception
                                    {
                                        if (inAppDragOperation)
                                        {
                                            //dirty, should be removed with Copy Command
                                            if (event.getTransferMode() == TransferMode.COPY)
                                            {
                                                fileOp = FileOperation.COPY;
                                            } else
                                            {
                                                fileOp = FileOperation.MOVE;
                                            }

                                            pasteSelectedFiles();
                                        } else
                                        {
                                            importFiles(db.getFiles());
                                        }

                                        updateUI();

                                        return null;
                                    }
                                }.call();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        event.setDropCompleted(success);
                        event.consume();
                    }
                });

                return treeCell;
            }
        });


        treeViewNavigation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<VDirectory>>()
        {
            @Override
            public void changed(ObservableValue<? extends TreeItem<VDirectory>> observable, TreeItem<VDirectory> oldValue, TreeItem<VDirectory> newValue)
            {
                if (!ignoreSelectionChanged)
                {
                    //sometime got nullpointerexception, no clue why ..
                    if (newValue != null)
                    {
                        selectedDirectory = newValue.getValue();
                        refreshListView(newValue.getValue());
                        selectVDirectory(selectedDirectory);
                    }
                }
            }
        });


        listViewFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewFiles.setCellFactory(new Callback<ListView<VObject>, ListCell<VObject>>()
        {
            @Override
            public ListCell<VObject> call(ListView<VObject> list)
            {
                return new DirectoryListCell();
            }
        });
        listViewFiles.getSelectionModel().selectedItemProperty().addListener((a) -> {
            try
            {
                if (!ignoreSelectionChanged && ((ObservableValue<VObject>) a).getValue() != null)
                {
                    textFieldPath.setText(((ObservableValue<VObject>) a).getValue().getPath());
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        listViewFiles.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if (mouseEvent.getClickCount() == 2)
                {
                    enterDirectory();
                }
            }
        });

        this.menuItemPaste.setDisable(true);

        try
        {
            progressDialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(ProgressDialogController.class.getResource("ProgressDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            progressDialogController = loader.getController();

            progressDialogStage.setTitle("In Progress .. ");
            progressDialogStage.initModality(Modality.APPLICATION_MODAL);
            progressDialogStage.initStyle(StageStyle.UNDECORATED);
            progressDialogStage.setScene(new Scene(root));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private void renameSelectedFile()
    {
        if (listViewFiles.getSelectionModel().getSelectedIndices().size() > 1)
        {
            showMessageDialog("Information", "Too many files selected", "Please select only one file!");
            return;
        } else
        {
            String objectname = "file";

            if (listViewFiles.getSelectionModel().getSelectedItem() instanceof VDirectory)
            {
                objectname = "directory";
            }

            showInputDialog("Rename " + objectname + "... ", "New " + objectname + "name: ", "new " + objectname + "name", (filename) -> {
                vdisk.rename(listViewFiles.getSelectionModel().getSelectedItem(), filename);

                try
                {
                    updateUI();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    private void deleteSelectedFiles()
    {
        try
        {
            new Task<Void>()
            {
                @Override
                protected Void call() throws Exception
                {
                    final ObservableList<VObject> items = listViewFiles.getSelectionModel().getSelectedItems();

                    for (VObject d : items)
                    {
                        if (d instanceof VFile)
                        {
                            vdisk.delete(d);
                            selectedFiles.remove(d);
                        }
                    }

                    return null;
                }
            }.call();

            updateUI();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void refreshListView(VDirectory dir)
    {
        try
        {
            textFieldPath.setText(dir.getPath());

            listViewFiles.getItems().clear();
            List<VObject> items = dir.getEntries();
            listViewFiles.getItems().addAll(items);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private DialogResult showMessageDialog(String title, String message, String info)
    {
        DialogResult result = DialogResult.CANCEL;

        try
        {
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(MessageDialogController.class.getResource("MessageDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final MessageDialogController controller = loader.getController();

            dialogStage.setTitle(title);
            controller.init(message, info, true);

            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            result = controller.getResult();
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return result;
    }

    private void showInputDialog(String title, String labelString, String promptString, Consumer<String> actionOk)
    {
        try
        {
            Stage dialogStage = new Stage();

            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(InputDialogController.class.getResource("InputDialog.fxml").openStream());
            loader.setBuilderFactory(new JavaFXBuilderFactory());

            final InputDialogController inputDialogController = loader.getController();

            inputDialogController.prepare(labelString, promptString);

            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (inputDialogController.getResult() == DialogResult.OK)
            {
                actionOk.accept(inputDialogController.getInput());
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public class DirectoryListCell extends ListCell<VObject>
    {
        public DirectoryListCell()
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
                    setText(item.getName());
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

