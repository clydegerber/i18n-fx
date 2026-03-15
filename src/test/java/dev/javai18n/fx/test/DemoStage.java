/*
 * Copyright 2026 Clyde Gerber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.javai18n.fx.test;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BooleanSupplier;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import dev.javai18n.core.Resource;
import dev.javai18n.fx.FXLogger;
import dev.javai18n.fx.LabeledPropertyBundle;
import dev.javai18n.fx.LocalizableStage;
import dev.javai18n.fx.ResourcefulButton;
import dev.javai18n.fx.ResourcefulCheckBox;
import dev.javai18n.fx.ResourcefulCheckMenuItem;
import dev.javai18n.fx.ResourcefulChoiceBox;
import dev.javai18n.fx.ResourcefulComboBox;
import dev.javai18n.fx.ResourcefulHyperlink;
import dev.javai18n.fx.ResourcefulLabel;
import dev.javai18n.fx.ResourcefulListView;
import dev.javai18n.fx.ResourcefulMenu;
import dev.javai18n.fx.ResourcefulMenuBar;
import dev.javai18n.fx.ResourcefulMenuItem;
import dev.javai18n.fx.ResourcefulPagination;
import dev.javai18n.fx.ResourcefulPasswordField;
import dev.javai18n.fx.ResourcefulProgressBar;
import dev.javai18n.fx.ResourcefulProgressIndicator;
import dev.javai18n.fx.ResourcefulRadioMenuItem;
import dev.javai18n.fx.ResourcefulScrollPane;
import dev.javai18n.fx.ResourcefulSeparator;
import dev.javai18n.fx.ResourcefulSlider;
import dev.javai18n.fx.ResourcefulSpinner;
import dev.javai18n.fx.ResourcefulSplitPane;
import dev.javai18n.fx.ResourcefulTab;
import dev.javai18n.fx.ResourcefulTabPane;
import dev.javai18n.fx.ResourcefulTableColumn;
import dev.javai18n.fx.ResourcefulTableView;
import dev.javai18n.fx.ResourcefulTextField;
import dev.javai18n.fx.ResourcefulTextArea;
import dev.javai18n.fx.ResourcefulTitledPane;
import dev.javai18n.fx.ResourcefulToggleButton;
import dev.javai18n.fx.ResourcefulToolBar;

/**
 * A file-explorer DemoStage that showcases all locale-aware JavaFX component
 * types from the i18n-fx library.
 *
 * <p>All menus, toolbar buttons, tab labels, table column headers, tooltips,
 * and widget labels update automatically when the locale is changed via
 * Preferences &gt; Locale. The Widgets tab demonstrates every Resourceful
 * component type provided by the library.</p>
 */
public class DemoStage extends LocalizableStage
{
    static
    {
        FXTestModuleRegistrar.ensureRegistered();
    }

    /**
     * Create and initialize a new DemoStage.
     * Must be called on the JavaFX Application Thread.
     *
     * @return A DemoStage with the UI built and the home directory loaded.
     */
    public static DemoStage create()
    {
        DemoStage stage = new DemoStage();
        Locale best = bestMatchLocale(stage.getAvailableLocales(), Locale.getDefault());
        if (!best.equals(stage.getBundleLocale()))
        {
            stage.setBundleLocale(best);
        }
        stage.buildUI();
        stage.updateLocaleSpecificValues();
        stage.populateLocaleMenu();
        stage.navigateTo(new File(System.getProperty("user.home")));

        try (var is = DemoStage.class.getResourceAsStream("app-icon.png"))
        {
            if (is != null) stage.getIcons().add(new javafx.scene.image.Image(is));
        }
        catch (IOException ignored) {}

        // On macOS the native application-menu "About" item fires the Desktop APP_ABOUT handler.
        // In a pure JavaFX app the AWT toolkit is never initialised, so isSupported(APP_ABOUT)
        // returns false.  Forcing toolkit initialisation first enables the handler registration.
        // The callback arrives on the macOS event thread, so dispatch to the FX thread.
        if (Desktop.isDesktopSupported())
        {
            try
            {
                java.awt.Toolkit.getDefaultToolkit();
                if (Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT))
                    Desktop.getDesktop().setAboutHandler(e -> Platform.runLater(stage::showAboutDialog));
            }
            catch (Exception ignored) {}
        }

        return stage;
    }

    // === State ===
    private File currentDirectory;
    private final List<File> history = new ArrayList<>();
    private int historyIndex = -1;
    private boolean useAllLocales = false;
    private boolean useCurrentLocaleForLogging = false;
    private boolean showHiddenFiles = false;
    private boolean showFileExtensions = true;
    private String sortBy = "name";
    private boolean sortAscending = true;
    private boolean dirsOnly = false;
    private String fileType = "all";

    // === Locale menu items ===
    private final List<LocaleMenuItem> localeMenuItems = new ArrayList<>();
    private boolean localeMenuBuiltForAllLocales = false;

    // === Format cache ===
    private Locale formatCacheLocale;
    private final Map<String, MessageFormat> bundleStringFormatCache = new HashMap<>();
    private NumberFormat sizeNumberFormat;

    // === Menu components ===
    private ResourcefulMenu localeMenu;

    // === Toolbar buttons ===
    private ResourcefulButton backButton, forwardButton, upButton;

    // === Path field ===
    private ResourcefulTextField pathField;

    // === Tree ===
    private TreeView<File> directoryTree;
    /** True while navigateTo/Back/Forward is programmatically updating the tree selection,
     *  preventing the selection listener from triggering a redundant navigateTo call. */
    private boolean updatingTreeSelection = false;

    // === Files tab ===
    private final ObservableList<File> fileTableData = FXCollections.observableArrayList();
    private final ObservableList<File> fileListData = FXCollections.observableArrayList();
    private ResourcefulTableView<File> fileTable;
    private ResourcefulTableColumn<File, String> nameColumn, sizeColumn, typeColumn, dateColumn;
    private ListView<File> fileListView;
    private StackPane fileViewStack;
    private ResourcefulScrollPane tableScrollPane, listScrollPane;
    private Task<?> pendingFileTask;
    private final Map<File, Long> dirSizeMap = new HashMap<>();
    private Task<Void> dirSizeTask;
    private Label sizeStatusLabel;

    // === Context menu ===
    private ContextMenu fileContextMenu;

    // === Preview tab ===
    private ResourcefulTextArea previewTextArea;

    // === Properties tab ===
    private ResourcefulLabel fileNameLabel, fileSizeLabel, fileTypeLabel, fileModifiedLabel, filePathLabel;
    private ResourcefulCheckBox readOnlyCheckBox;
    private ResourcefulTitledPane quickInfoTitledPane;
    private ResourcefulLabel infoContentLabel;

    // === Status bar ===
    private ResourcefulLabel fileCountLabel;

    // === Tabs ===
    private ResourcefulTabPane contentTabPane;

    // === Widgets tab ===
    private ResourcefulPasswordField demoPasswordField;
    private ResourcefulTextField demoSearchField;
    private ResourcefulSpinner<Integer> demoSpinner;
    private ResourcefulSlider demoSlider;
    private ResourcefulChoiceBox demoChoiceBox;
    private ResourcefulComboBox demoComboBox;
    private final ObservableList<String> demoDayItems = FXCollections.observableArrayList();
    private ResourcefulListView<String> demoDayListView;
    private ResourcefulProgressBar demoProgressBar;
    private ResourcefulProgressIndicator demoProgressIndicator;
    private ResourcefulToggleButton demoToggleButton;
    private ResourcefulPagination demoPagination;
    private DemoPopup demoPopup;

    @Override
    public Locale[] getAvailableLocales()
    {
        return useAllLocales ? Locale.getAvailableLocales() : FXLogger.FX_LOGGER.getAvailableLocales();
    }

    @Override
    protected void updateLocaleSpecificValues()
    {
        super.updateLocaleSpecificValues();
        invalidateFormatCache();
        if (localeMenu != null)
        {
            populateLocaleMenu();
        }
        if (currentDirectory != null)
        {
            refreshFileTable();
        }
        if (!demoDayItems.isEmpty())
        {
            populateDemoList(getBundleLocale());
        }
        if (demoSlider != null)
        {
            updateSliderLabels(getBundleLocale());
        }
        if (demoPopup != null)
        {
            demoPopup.setBundleLocale(getBundleLocale());
        }
    }

    private void invalidateFormatCache()
    {
        if (!getBundleLocale().equals(formatCacheLocale))
        {
            bundleStringFormatCache.clear();
            sizeNumberFormat = null;
            formatCacheLocale = getBundleLocale();
        }
    }

    private String getBundleString(String key)
    {
        return getResourceBundle().getString(key);
    }

    private String formatBundleString(String key, Object... args)
    {
        invalidateFormatCache();
        MessageFormat mf = bundleStringFormatCache.computeIfAbsent(key,
                k -> new MessageFormat(getResourceBundle().getString(k), getBundleLocale()));
        return mf.format(args);
    }

    // =========================================================================
    // UI construction
    // =========================================================================

    private void buildUI()
    {
        BorderPane root = new BorderPane();

        VBox topBox = new VBox();
        topBox.getChildren().addAll(buildMenuBar(), buildToolBar(), buildPathBar());
        root.setTop(topBox);
        root.setCenter(buildContentArea());
        root.setBottom(buildStatusBar());

        setScene(new Scene(root, 1100, 750));
        setMinWidth(700);
        setMinHeight(500);
    }

    private ResourcefulMenuBar buildMenuBar()
    {
        ResourcefulMenuBar menuBar = ResourcefulMenuBar.create(new Resource(this, "menuBarProps"));
        menuBar.setUseSystemMenuBar(true);

        // File menu
        ResourcefulMenu fileMenu = ResourcefulMenu.create(new Resource(this, "fileMenuProps"));
        addMenuItem(fileMenu, "newFolderMenuItemProps", this::createNewFolder);
        addMenuItem(fileMenu, "deleteMenuItemProps", this::deleteSelected);
        fileMenu.getItems().add(new SeparatorMenuItem());
        addMenuItem(fileMenu, "exitMenuItemProps", () -> Platform.exit());
        menuBar.getMenus().add(fileMenu);

        // Edit menu
        ResourcefulMenu editMenu = ResourcefulMenu.create(new Resource(this, "editMenuProps"));
        addMenuItem(editMenu, "copyMenuItemProps", () -> {});
        addMenuItem(editMenu, "cutMenuItemProps", () -> {});
        addMenuItem(editMenu, "pasteMenuItemProps", () -> {});
        addMenuItem(editMenu, "selectAllMenuItemProps", () -> {});
        menuBar.getMenus().add(editMenu);

        // View menu
        ResourcefulMenu viewMenu = ResourcefulMenu.create(new Resource(this, "viewMenuProps"));
        addMenuItem(viewMenu, "refreshMenuItemProps", this::refreshFileTable);

        viewMenu.getItems().add(new SeparatorMenuItem());

        // Sort By submenu
        ResourcefulMenu sortByMenu = ResourcefulMenu.create(new Resource(this, "sortByMenuProps"));
        ToggleGroup sortGroup = new ToggleGroup();
        ResourcefulRadioMenuItem sortByNameRadio = ResourcefulRadioMenuItem.create(new Resource(this, "sortByNameRadioProps"));
        sortByNameRadio.setToggleGroup(sortGroup);
        sortByNameRadio.setSelected(true);
        sortByNameRadio.setOnAction(e -> { sortBy = "name"; refreshFileTable(); });
        ResourcefulRadioMenuItem sortBySizeRadio = ResourcefulRadioMenuItem.create(new Resource(this, "sortBySizeRadioProps"));
        sortBySizeRadio.setToggleGroup(sortGroup);
        sortBySizeRadio.setOnAction(e -> { sortBy = "size"; refreshFileTable(); });
        ResourcefulRadioMenuItem sortByDateRadio = ResourcefulRadioMenuItem.create(new Resource(this, "sortByDateRadioProps"));
        sortByDateRadio.setToggleGroup(sortGroup);
        sortByDateRadio.setOnAction(e -> { sortBy = "date"; refreshFileTable(); });

        ToggleGroup dirGroup = new ToggleGroup();
        ResourcefulRadioMenuItem sortAscendingRadio = ResourcefulRadioMenuItem.create(new Resource(this, "sortAscendingRadioProps"));
        sortAscendingRadio.setToggleGroup(dirGroup);
        sortAscendingRadio.setSelected(true);
        sortAscendingRadio.setOnAction(e -> { sortAscending = true; refreshFileTable(); });
        ResourcefulRadioMenuItem sortDescendingRadio = ResourcefulRadioMenuItem.create(new Resource(this, "sortDescendingRadioProps"));
        sortDescendingRadio.setToggleGroup(dirGroup);
        sortDescendingRadio.setOnAction(e -> { sortAscending = false; refreshFileTable(); });

        sortByMenu.getItems().addAll(sortByNameRadio, sortBySizeRadio, sortByDateRadio,
                new SeparatorMenuItem(), sortAscendingRadio, sortDescendingRadio);
        viewMenu.getItems().add(sortByMenu);

        viewMenu.getItems().add(new SeparatorMenuItem());
        ResourcefulCheckMenuItem showHiddenItem = ResourcefulCheckMenuItem.create(new Resource(this, "showHiddenMenuItemProps"));
        showHiddenItem.setOnAction(e -> { showHiddenFiles = showHiddenItem.isSelected(); refreshFileTable(); });
        ResourcefulCheckMenuItem showExtItem = ResourcefulCheckMenuItem.create(new Resource(this, "showExtensionsMenuItemProps"));
        showExtItem.setSelected(true);
        showExtItem.setOnAction(e -> { showFileExtensions = showExtItem.isSelected(); refreshFileTable(); });
        viewMenu.getItems().addAll(showHiddenItem, showExtItem);

        viewMenu.getItems().add(new SeparatorMenuItem());

        // Filter submenu
        ResourcefulMenu filterMenu = ResourcefulMenu.create(new Resource(this, "filterMenuProps"));
        ToggleGroup showGroup = new ToggleGroup();
        ResourcefulRadioMenuItem allFilesRadio = ResourcefulRadioMenuItem.create(new Resource(this, "allFilesRadioProps"));
        allFilesRadio.setToggleGroup(showGroup);
        allFilesRadio.setSelected(true);
        allFilesRadio.setOnAction(e -> { dirsOnly = false; refreshFileTable(); });
        ResourcefulRadioMenuItem dirsOnlyRadio = ResourcefulRadioMenuItem.create(new Resource(this, "dirsOnlyRadioProps"));
        dirsOnlyRadio.setToggleGroup(showGroup);
        dirsOnlyRadio.setOnAction(e -> { dirsOnly = true; refreshFileTable(); });
        filterMenu.getItems().addAll(allFilesRadio, dirsOnlyRadio, new SeparatorMenuItem());

        ToggleGroup typeGroup = new ToggleGroup();
        ResourcefulRadioMenuItem allTypesRadio = ResourcefulRadioMenuItem.create(new Resource(this, "allFileTypesRadioProps"));
        allTypesRadio.setToggleGroup(typeGroup);
        allTypesRadio.setSelected(true);
        allTypesRadio.setOnAction(e -> { fileType = "all"; refreshFileTable(); });
        ResourcefulRadioMenuItem textRadio = ResourcefulRadioMenuItem.create(new Resource(this, "textFilesRadioProps"));
        textRadio.setToggleGroup(typeGroup);
        textRadio.setOnAction(e -> { fileType = "text"; refreshFileTable(); });
        ResourcefulRadioMenuItem imageRadio = ResourcefulRadioMenuItem.create(new Resource(this, "imageFilesRadioProps"));
        imageRadio.setToggleGroup(typeGroup);
        imageRadio.setOnAction(e -> { fileType = "image"; refreshFileTable(); });
        filterMenu.getItems().addAll(allTypesRadio, textRadio, imageRadio);
        viewMenu.getItems().add(filterMenu);
        menuBar.getMenus().add(viewMenu);

        // Preferences menu
        ResourcefulMenu preferencesMenu = ResourcefulMenu.create(new Resource(this, "preferencesMenuProps"));

        // Locale submenu (populated dynamically)
        localeMenu = ResourcefulMenu.create(new Resource(this, "localeMenuProps"));
        preferencesMenu.getItems().add(localeMenu);

        preferencesMenu.getItems().add(new SeparatorMenuItem());

        // Locale Set submenu
        ResourcefulMenu localeSetMenu = ResourcefulMenu.create(new Resource(this, "localeSetMenuProps"));
        ToggleGroup localeSetGroup = new ToggleGroup();
        ResourcefulRadioMenuItem loggerLocalesRadio = ResourcefulRadioMenuItem.create(new Resource(this, "loggerLocalesRadioProps"));
        loggerLocalesRadio.setToggleGroup(localeSetGroup);
        loggerLocalesRadio.setSelected(true);
        loggerLocalesRadio.setOnAction(e -> { useAllLocales = false; populateLocaleMenu(); });
        ResourcefulRadioMenuItem allLocalesRadio = ResourcefulRadioMenuItem.create(new Resource(this, "allLocalesRadioProps"));
        allLocalesRadio.setToggleGroup(localeSetGroup);
        allLocalesRadio.setOnAction(e -> { useAllLocales = true; populateLocaleMenu(); });
        localeSetMenu.getItems().addAll(loggerLocalesRadio, allLocalesRadio);
        preferencesMenu.getItems().add(localeSetMenu);

        // Logger Locale submenu
        ResourcefulMenu loggerLocaleMenu = ResourcefulMenu.create(new Resource(this, "loggerLocaleMenuProps"));
        ToggleGroup loggerGroup = new ToggleGroup();
        ResourcefulRadioMenuItem jvmDefaultRadio = ResourcefulRadioMenuItem.create(new Resource(this, "jvmDefaultLocaleRadioProps"));
        jvmDefaultRadio.setToggleGroup(loggerGroup);
        jvmDefaultRadio.setSelected(true);
        jvmDefaultRadio.setOnAction(e -> { useCurrentLocaleForLogging = false; FXLogger.FX_LOGGER.setBundleLocale(Locale.getDefault()); });
        ResourcefulRadioMenuItem currentLocaleRadio = ResourcefulRadioMenuItem.create(new Resource(this, "currentLocaleRadioProps"));
        currentLocaleRadio.setToggleGroup(loggerGroup);
        currentLocaleRadio.setOnAction(e -> { useCurrentLocaleForLogging = true; FXLogger.FX_LOGGER.setBundleLocale(getBundleLocale()); });
        loggerLocaleMenu.getItems().addAll(jvmDefaultRadio, currentLocaleRadio);
        preferencesMenu.getItems().add(loggerLocaleMenu);
        menuBar.getMenus().add(preferencesMenu);

        // Help menu
        ResourcefulMenu helpMenu = ResourcefulMenu.create(new Resource(this, "helpMenuProps"));
        addMenuItem(helpMenu, "aboutMenuItemProps", this::showAboutDialog);
        helpMenu.getItems().add(new SeparatorMenuItem());
        addMenuItem(helpMenu, "fxmlDemoMenuItemProps", this::openFXMLDemo);
        addMenuItem(helpMenu, "swingInteropDemoMenuItemProps", this::openSwingInteropDemo);
        menuBar.getMenus().add(helpMenu);

        return menuBar;
    }

    private void addMenuItem(ResourcefulMenu menu, String resourceKey, Runnable action)
    {
        ResourcefulMenuItem item = ResourcefulMenuItem.create(new Resource(this, resourceKey));
        item.setOnAction(e -> action.run());
        menu.getItems().add(item);
    }

    private ResourcefulToolBar buildToolBar()
    {
        ResourcefulToolBar toolBar = ResourcefulToolBar.create(new Resource(this, "navToolBarProps"));

        backButton = ResourcefulButton.create(new Resource(this, "backButtonProps"));
        backButton.setOnAction(e -> navigateBack());
        backButton.setDisable(true);

        forwardButton = ResourcefulButton.create(new Resource(this, "forwardButtonProps"));
        forwardButton.setOnAction(e -> navigateForward());
        forwardButton.setDisable(true);

        upButton = ResourcefulButton.create(new Resource(this, "upButtonProps"));
        upButton.setOnAction(e -> navigateUp());
        upButton.setDisable(true);

        ResourcefulButton homeButton = ResourcefulButton.create(new Resource(this, "homeButtonProps"));
        homeButton.setOnAction(e -> navigateTo(new File(System.getProperty("user.home"))));

        ResourcefulButton refreshButton = ResourcefulButton.create(new Resource(this, "refreshButtonProps"));
        refreshButton.setOnAction(e -> refreshFileTable());

        ResourcefulButton newFolderButton = ResourcefulButton.create(new Resource(this, "newFolderButtonProps"));
        newFolderButton.setOnAction(e -> createNewFolder());

        ResourcefulSeparator sep = ResourcefulSeparator.create(new Resource(this, "toolBarSeparatorProps"));
        sep.setOrientation(Orientation.VERTICAL);

        ToggleGroup viewGroup = new ToggleGroup();
        ResourcefulToggleButton listToggle = ResourcefulToggleButton.create(new Resource(this, "listViewToggleProps"));
        listToggle.setToggleGroup(viewGroup);
        listToggle.setOnAction(e -> showListView());

        ResourcefulToggleButton detailToggle = ResourcefulToggleButton.create(new Resource(this, "detailViewToggleProps"));
        detailToggle.setToggleGroup(viewGroup);
        detailToggle.setSelected(true);
        detailToggle.setOnAction(e -> showDetailView());

        toolBar.getItems().addAll(
                backButton, forwardButton, upButton, homeButton, refreshButton, newFolderButton,
                sep, listToggle, detailToggle);

        return toolBar;
    }

    private HBox buildPathBar()
    {
        ResourcefulLabel pathLabel = ResourcefulLabel.create(new Resource(this, "pathLabelProps"));

        pathField = ResourcefulTextField.create(new Resource(this, "pathFieldProps"));
        HBox.setHgrow(pathField, Priority.ALWAYS);
        pathField.setOnAction(e ->
        {
            File dir = new File(pathField.getText());
            if (dir.isDirectory())
            {
                navigateTo(dir);
            }
        });

        HBox pathBar = new HBox(8, pathLabel, pathField);
        pathBar.setPadding(new Insets(4, 8, 4, 8));
        return pathBar;
    }

    private javafx.scene.control.SplitPane buildContentArea()
    {
        // Left: directory tree in scroll pane
        directoryTree = new TreeView<>();
        directoryTree.setShowRoot(false);
        TreeItem<File> treeRoot = new TreeItem<>(null);
        for (File fsRoot : File.listRoots())
        {
            treeRoot.getChildren().add(createDirItem(fsRoot));
        }
        directoryTree.setRoot(treeRoot);
        directoryTree.setCellFactory(tv -> new TreeCell<>()
        {
            @Override
            protected void updateItem(File file, boolean empty)
            {
                super.updateItem(file, empty);
                if (empty || file == null)
                {
                    setText(null);
                }
                else
                {
                    String name = file.getName();
                    setText(name.isEmpty() ? file.getPath() : name);
                }
            }
        });
        directoryTree.getSelectionModel().selectedItemProperty().addListener((obs, old, nv) ->
        {
            if (updatingTreeSelection) return;
            if (nv != null && nv.getValue() != null && nv.getValue().isDirectory())
                navigateTo(nv.getValue());
        });

        ResourcefulScrollPane treeScroll = ResourcefulScrollPane.create(new Resource(this, "treeScrollPaneProps"));
        treeScroll.setContent(directoryTree);
        treeScroll.setMinWidth(180);
        treeScroll.setPrefWidth(220);

        // Right: tab pane
        contentTabPane = ResourcefulTabPane.create(new Resource(this, "contentTabPaneProps"));
        ResourcefulTab filesTab = ResourcefulTab.create(new Resource(this, "filesTabProps"));
        filesTab.setContent(buildFilesTabContent());
        filesTab.setClosable(false);
        ResourcefulTab previewTab = ResourcefulTab.create(new Resource(this, "previewTabProps"));
        previewTab.setContent(buildPreviewTabContent());
        previewTab.setClosable(false);
        ResourcefulTab propertiesTab = ResourcefulTab.create(new Resource(this, "propertiesTabProps"));
        propertiesTab.setContent(buildPropertiesTabContent());
        propertiesTab.setClosable(false);
        ResourcefulTab widgetsTab = ResourcefulTab.create(new Resource(this, "widgetsTabProps"));
        widgetsTab.setContent(buildWidgetsTabContent());
        widgetsTab.setClosable(false);
        contentTabPane.getTabs().addAll(filesTab, previewTab, propertiesTab, widgetsTab);

        ResourcefulSplitPane splitPane = ResourcefulSplitPane.create(new Resource(this, "mainSplitPaneProps"));
        splitPane.getItems().addAll(treeScroll, contentTabPane);
        splitPane.setDividerPositions(0.22);
        javafx.scene.control.SplitPane.setResizableWithParent(treeScroll, false);

        return splitPane;
    }

    private TreeItem<File> createDirItem(File dir)
    {
        TreeItem<File> item = new TreeItem<>(dir);
        // Add a dummy child so the expand arrow appears
        item.getChildren().add(new TreeItem<>(null));
        item.expandedProperty().addListener((obs, wasExpanded, isExpanded) ->
        {
            if (isExpanded && item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null)
            {
                Task<List<File>> task = new Task<>()
                {
                    @Override
                    protected List<File> call()
                    {
                        return listSubdirectories(dir);
                    }
                };
                task.setOnSucceeded(e ->
                {
                    List<TreeItem<File>> children = new ArrayList<>();
                    for (File child : task.getValue())
                        children.add(createDirItem(child));
                    item.getChildren().setAll(children);
                });
                Thread dirLoader = new Thread(task, "DirLoader");
                dirLoader.setDaemon(true);
                dirLoader.start();
            }
        });
        return item;
    }

    private VBox buildFilesTabContent()
    {
        // TableView
        fileTable = ResourcefulTableView.create(new Resource(this, "fileTableProps"));
        fileTable.setItems(fileTableData);

        nameColumn = ResourcefulTableColumn.create(new Resource(this, "nameColumnProps"));
        nameColumn.setCellValueFactory(p ->
        {
            File f = p.getValue();
            String name = f.getName().isEmpty() ? f.getPath() : f.getName();
            if (!showFileExtensions && !f.isDirectory())
            {
                int dot = name.lastIndexOf('.');
                if (dot > 0) name = name.substring(0, dot);
            }
            return new ReadOnlyStringWrapper(name);
        });
        nameColumn.setPrefWidth(250);
        nameColumn.setComparator((a, b) -> Collator.getInstance(getBundleLocale()).compare(a, b));

        sizeColumn = ResourcefulTableColumn.create(new Resource(this, "sizeColumnProps"));
        sizeColumn.setCellValueFactory(p ->
        {
            File f = p.getValue();
            if (f.isDirectory())
            {
                Long cached = dirSizeMap.get(f);
                return new ReadOnlyStringWrapper(cached != null ? formatSize(cached) : "...");
            }
            return new ReadOnlyStringWrapper(formatSize(f.length()));
        });
        sizeColumn.setPrefWidth(80);

        typeColumn = ResourcefulTableColumn.create(new Resource(this, "typeColumnProps"));
        typeColumn.setCellValueFactory(p ->
        {
            File f = p.getValue();
            return new ReadOnlyStringWrapper(getFileType(f));
        });
        typeColumn.setPrefWidth(100);

        dateColumn = ResourcefulTableColumn.create(new Resource(this, "dateColumnProps"));
        dateColumn.setCellValueFactory(p ->
        {
            File f = p.getValue();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, getBundleLocale());
            return new ReadOnlyStringWrapper(df.format(new Date(f.lastModified())));
        });
        dateColumn.setPrefWidth(160);

        fileTable.getColumns().addAll(List.of(nameColumn, sizeColumn, typeColumn, dateColumn));
        fileTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nv) ->
        {
            if (nv != null) showProperties(nv);
        });
        fileTable.setOnMouseClicked(e ->
        {
            if (e.getClickCount() == 2)
            {
                File f = fileTable.getSelectionModel().getSelectedItem();
                if (f != null && f.isDirectory())
                {
                    navigateTo(f);
                }
                else if (f != null)
                {
                    previewFile(f);
                    contentTabPane.getSelectionModel().select(1);
                }
            }
        });

        // Context menu on table
        fileContextMenu = new ContextMenu();
        ResourcefulMenuItem popupRefresh = ResourcefulMenuItem.create(new Resource(this, "popupRefreshMenuItemProps"));
        popupRefresh.setOnAction(e -> refreshFileTable());
        ResourcefulMenuItem popupProperties = ResourcefulMenuItem.create(new Resource(this, "popupPropertiesMenuItemProps"));
        popupProperties.setOnAction(e -> contentTabPane.getSelectionModel().select(2));
        ResourcefulMenuItem popupDelete = ResourcefulMenuItem.create(new Resource(this, "popupDeleteMenuItemProps"));
        popupDelete.setOnAction(e -> deleteSelected());
        fileContextMenu.getItems().addAll(popupRefresh, popupProperties, popupDelete);
        fileTable.setContextMenu(fileContextMenu);

        tableScrollPane = ResourcefulScrollPane.create(new Resource(this, "tableScrollPaneProps"));
        tableScrollPane.setContent(fileTable);
        tableScrollPane.setFitToWidth(true);
        tableScrollPane.setFitToHeight(true);

        // ListView (icon-style list view)
        fileListView = new ListView<>(fileListData);
        fileListView.setCellFactory(lv -> new ListCell<File>()
        {
            @Override
            protected void updateItem(File file, boolean empty)
            {
                super.updateItem(file, empty);
                if (empty || file == null)
                {
                    setText(null);
                }
                else
                {
                    String name = file.getName().isEmpty() ? file.getPath() : file.getName();
                    if (!showFileExtensions && !file.isDirectory())
                    {
                        int dot = name.lastIndexOf('.');
                        if (dot > 0) name = name.substring(0, dot);
                    }
                    setText((file.isDirectory() ? "[DIR] " : "[FILE] ") + name);
                }
            }
        });
        fileListView.getSelectionModel().selectedItemProperty().addListener((obs, old, nv) ->
        {
            if (nv != null) showProperties(nv);
        });
        fileListView.setOnMouseClicked(e ->
        {
            if (e.getClickCount() == 2)
            {
                File f = fileListView.getSelectionModel().getSelectedItem();
                if (f != null && f.isDirectory()) navigateTo(f);
            }
        });

        listScrollPane = ResourcefulScrollPane.create(new Resource(this, "listScrollPaneProps"));
        listScrollPane.setContent(fileListView);
        listScrollPane.setFitToWidth(true);
        listScrollPane.setFitToHeight(true);
        listScrollPane.setVisible(false);
        listScrollPane.setManaged(false);

        fileViewStack = new StackPane(tableScrollPane, listScrollPane);
        VBox.setVgrow(fileViewStack, Priority.ALWAYS);

        return new VBox(fileViewStack);
    }

    private void showDetailView()
    {
        tableScrollPane.setVisible(true);
        tableScrollPane.setManaged(true);
        listScrollPane.setVisible(false);
        listScrollPane.setManaged(false);
    }

    private void showListView()
    {
        tableScrollPane.setVisible(false);
        tableScrollPane.setManaged(false);
        listScrollPane.setVisible(true);
        listScrollPane.setManaged(true);
    }

    private javafx.scene.control.ScrollPane buildPreviewTabContent()
    {
        previewTextArea = ResourcefulTextArea.create(new Resource(this, "previewTextAreaProps"));
        previewTextArea.setEditable(false);
        previewTextArea.setWrapText(true);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(previewTextArea);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        return scroll;
    }

    private javafx.scene.control.ScrollPane buildPropertiesTabContent()
    {
        fileNameLabel = ResourcefulLabel.create(new Resource(this, "fileNameLabelProps"));
        fileSizeLabel = ResourcefulLabel.create(new Resource(this, "fileSizeLabelProps"));
        fileTypeLabel = ResourcefulLabel.create(new Resource(this, "fileTypeLabelProps"));
        fileModifiedLabel = ResourcefulLabel.create(new Resource(this, "fileModifiedLabelProps"));
        filePathLabel = ResourcefulLabel.create(new Resource(this, "filePathLabelProps"));
        readOnlyCheckBox = ResourcefulCheckBox.create(new Resource(this, "readOnlyCheckBoxProps"));
        readOnlyCheckBox.setDisable(true);

        infoContentLabel = ResourcefulLabel.create(new Resource(this, "infoContentLabelProps"));
        quickInfoTitledPane = ResourcefulTitledPane.create(new Resource(this, "quickInfoTitledPaneProps"));
        quickInfoTitledPane.setContent(infoContentLabel);
        quickInfoTitledPane.setExpanded(true);
        Accordion accordion = new Accordion(quickInfoTitledPane);
        accordion.setExpandedPane(quickInfoTitledPane);

        VBox propsBox = new VBox(8,
                fileNameLabel, fileSizeLabel, fileTypeLabel, fileModifiedLabel, filePathLabel,
                readOnlyCheckBox, accordion);
        propsBox.setPadding(new Insets(12));

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(propsBox);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private javafx.scene.control.ScrollPane buildWidgetsTabContent()
    {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(14));
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(120);
        ColumnConstraints widgetCol = new ColumnConstraints();
        widgetCol.setHgrow(Priority.ALWAYS);
        widgetCol.setFillWidth(true);
        grid.getColumnConstraints().addAll(labelCol, widgetCol);

        int row = 0;

        // PasswordField
        demoPasswordField = ResourcefulPasswordField.create(new Resource(this, "demoPasswordFieldProps"));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoPasswordLabelProps")),
                demoPasswordField);

        // TextField
        demoSearchField = ResourcefulTextField.create(new Resource(this, "demoSearchFieldProps"));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoSearchLabelProps")),
                demoSearchField);

        // Spinner
        demoSpinner = ResourcefulSpinner.create(new Resource(this, "demoSpinnerProps"));
        demoSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 42));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoSpinnerLabelProps")),
                demoSpinner);

        // Slider
        demoSlider = ResourcefulSlider.create(new Resource(this, "demoSliderProps"));
        demoSlider.setMin(0);
        demoSlider.setMax(100);
        demoSlider.setValue(50);
        demoSlider.setMajorTickUnit(25);
        demoSlider.setMinorTickCount(4);
        demoSlider.setShowTickMarks(true);
        demoSlider.setShowTickLabels(true);
        updateSliderLabels(getBundleLocale());
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoSliderLabelProps")),
                demoSlider);

        // ChoiceBox
        demoChoiceBox = ResourcefulChoiceBox.create(new Resource(this, "demoChoiceBoxProps"));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoChoiceBoxLabelProps")),
                demoChoiceBox);

        // ComboBox
        demoComboBox = ResourcefulComboBox.create(new Resource(this, "demoComboBoxProps"));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoComboBoxLabelProps")),
                demoComboBox);

        // ListView (day names)
        populateDemoList(getBundleLocale());
        demoDayListView = ResourcefulListView.create(new Resource(this, "demoDayListViewProps"));
        demoDayListView.setItems(demoDayItems);
        demoDayListView.setPrefHeight(120);
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoDayListLabelProps")),
                demoDayListView);

        // ProgressBar + ProgressIndicator on the same row
        demoProgressBar = ResourcefulProgressBar.create(new Resource(this, "demoProgressBarProps"));
        demoProgressBar.setProgress(0.65);
        HBox.setHgrow(demoProgressBar, Priority.ALWAYS);
        demoProgressIndicator = ResourcefulProgressIndicator.create(new Resource(this, "demoProgressIndicatorProps"));
        demoProgressIndicator.setProgress(0.65);
        HBox progressRow = new HBox(8, demoProgressBar, demoProgressIndicator);
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoProgressBarLabelProps")),
                progressRow);

        // ToggleButton
        demoToggleButton = ResourcefulToggleButton.create(new Resource(this, "demoToggleButtonProps"));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoToggleButtonLabelProps")),
                demoToggleButton);

        // Pagination
        demoPagination = ResourcefulPagination.create(new Resource(this, "demoPaginationProps"));
        demoPagination.setPageCount(10);
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoPaginationLabelProps")),
                demoPagination);

        // Popup
        demoPopup = DemoPopup.create(getBundleLocale());
        ResourcefulButton showPopupButton = ResourcefulButton.create(new Resource(this, "showPopupButtonProps"));
        showPopupButton.setOnAction(e ->
        {
            if (!demoPopup.isShowing())
            {
                javafx.geometry.Bounds b = showPopupButton.localToScreen(showPopupButton.getBoundsInLocal());
                demoPopup.show(getScene().getWindow(), b.getMinX(), b.getMaxY() + 4);
            }
        });
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "showPopupLabelProps")),
                showPopupButton);

        // Hyperlink
        ResourcefulHyperlink hyperlink = ResourcefulHyperlink.create(new Resource(this, "demoHyperlinkProps"));
        grid.addRow(row++,
                ResourcefulLabel.create(new Resource(this, "demoHyperlinkLabelProps")),
                hyperlink);

        // Spacer at bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(grid);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private HBox buildStatusBar()
    {
        fileCountLabel = ResourcefulLabel.create(new Resource(this, "fileCountLabelProps"));
        sizeStatusLabel = new Label();
        HBox statusBar = new HBox(10, fileCountLabel, sizeStatusLabel);
        statusBar.setPadding(new Insets(3, 8, 3, 8));
        statusBar.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        return statusBar;
    }

    // =========================================================================
    // Locale menu
    // =========================================================================

    /**
     * Returns the best match from {@code available} for {@code target}: exact match first,
     * then language+country, then language-only, finally Locale.ROOT.
     */
    private static Locale bestMatchLocale(Locale[] available, Locale target)
    {
        Locale best = Locale.ROOT;
        int bestScore = 0;
        for (Locale l : available)
        {
            if (l.equals(target)) return l;
            if (!l.getLanguage().isEmpty() && l.getLanguage().equals(target.getLanguage()))
            {
                int score = l.getCountry().equals(target.getCountry()) ? 2 : 1;
                if (score > bestScore)
                {
                    best = l;
                    bestScore = score;
                }
            }
        }
        return best;
    }

    public void populateLocaleMenu()
    {
        localeMenuItems.clear();
        localeMenuBuiltForAllLocales = useAllLocales;
        Locale current = getBundleLocale();
        ToggleGroup localeGroup = new ToggleGroup();
        for (Locale locale : getAvailableLocales())
        {
            String displayText = Locale.ROOT.equals(locale)
                    ? "ROOT"
                    : locale.getDisplayName(current) + " [" + locale.getDisplayName(locale) + "]";
            LocaleMenuItem item = new LocaleMenuItem(locale, displayText);
            item.setToggleGroup(localeGroup);
            item.setOnAction(e ->
            {
                Locale newLocale = item.getLocale();
                if (!newLocale.equals(getBundleLocale()))
                {
                    setBundleLocale(newLocale);
                    if (useCurrentLocaleForLogging)
                    {
                        FXLogger.FX_LOGGER.setBundleLocale(newLocale);
                    }
                }
            });
            localeMenuItems.add(item);
        }

        Collator collator = Collator.getInstance(current);
        localeMenuItems.sort((a, b) -> collator.compare(a.getText(), b.getText()));

        // When the list is long, pin the currently selected locale at the top so it
        // is immediately visible when the menu opens, then list all locales (excluding
        // the pinned one) in sorted order below a separator.
        LocaleMenuItem pinnedItem = null;
        if (localeMenuItems.size() > 20)
        {
            for (LocaleMenuItem item : localeMenuItems)
            {
                if (item.getLocale().equals(current))
                {
                    pinnedItem = item;
                    break;
                }
            }
        }

        localeMenu.getItems().clear();
        if (pinnedItem != null)
        {
            pinnedItem.setSelected(true);
            localeMenu.getItems().add(pinnedItem);
            localeMenu.getItems().add(new SeparatorMenuItem());
        }
        for (LocaleMenuItem item : localeMenuItems)
        {
            if (item == pinnedItem) continue;
            item.setSelected(item.getLocale().equals(current));
            localeMenu.getItems().add(item);
        }
    }

    /** A RadioMenuItem that displays its locale name in the locale's own language. */
    private static class LocaleMenuItem extends RadioMenuItem
    {
        private final Locale locale;

        LocaleMenuItem(Locale locale, String selfName)
        {
            super(selfName);
            this.locale = locale;
        }

        Locale getLocale() { return locale; }
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    void navigateTo(File dir)
    {
        if (dir == null || !dir.isDirectory()) return;
        currentDirectory = dir;
        if (historyIndex < history.size() - 1)
        {
            history.subList(historyIndex + 1, history.size()).clear();
        }
        history.add(dir);
        historyIndex = history.size() - 1;
        pathField.setText(dir.getAbsolutePath());
        updateNavButtons();
        refreshFileTable();
        syncTreeSelection(dir);
    }

    private void navigateBack()
    {
        if (historyIndex > 0)
        {
            historyIndex--;
            currentDirectory = history.get(historyIndex);
            pathField.setText(currentDirectory.getAbsolutePath());
            updateNavButtons();
            refreshFileTable();
            syncTreeSelection(currentDirectory);
        }
    }

    private void navigateForward()
    {
        if (historyIndex < history.size() - 1)
        {
            historyIndex++;
            currentDirectory = history.get(historyIndex);
            pathField.setText(currentDirectory.getAbsolutePath());
            updateNavButtons();
            refreshFileTable();
            syncTreeSelection(currentDirectory);
        }
    }

    private void navigateUp()
    {
        if (currentDirectory != null && currentDirectory.getParentFile() != null)
        {
            navigateTo(currentDirectory.getParentFile());
        }
    }

    private void updateNavButtons()
    {
        backButton.setDisable(historyIndex <= 0);
        forwardButton.setDisable(historyIndex >= history.size() - 1);
        upButton.setDisable(currentDirectory == null || currentDirectory.getParentFile() == null);
    }

    /**
     * Expands the tree to {@code dir} and selects it, without triggering the
     * selection listener's navigateTo callback. Because tree children are loaded
     * lazily on background threads, the walk is driven asynchronously via
     * ListChangeListeners that fire once each level's children have been loaded.
     */
    private void syncTreeSelection(File dir)
    {
        List<File> segments = new ArrayList<>();
        for (File f = dir; f != null; f = f.getParentFile())
            segments.add(0, f);

        TreeItem<File> root = null;
        for (TreeItem<File> child : directoryTree.getRoot().getChildren())
            if (segments.get(0).equals(child.getValue())) { root = child; break; }
        if (root == null) return;

        updatingTreeSelection = true;
        syncTreeSelectionStep(root, segments, 1);
    }

    private void syncTreeSelectionStep(TreeItem<File> item, List<File> segments, int depth)
    {
        if (depth >= segments.size())
        {
            directoryTree.getSelectionModel().select(item);
            directoryTree.scrollTo(directoryTree.getRow(item));
            updatingTreeSelection = false;
            return;
        }

        File nextFile = segments.get(depth);

        // Children already loaded (no dummy placeholder) — descend immediately
        if (!(item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null))
        {
            item.setExpanded(true);
            for (TreeItem<File> child : item.getChildren())
                if (nextFile.equals(child.getValue()))
                { syncTreeSelectionStep(child, segments, depth + 1); return; }
            updatingTreeSelection = false; // segment not in tree (hidden dir, etc.)
            return;
        }

        // Children not yet loaded — attach a one-shot listener, then trigger expansion
        ListChangeListener<TreeItem<File>> listener = new ListChangeListener<>()
        {
            @Override
            public void onChanged(Change<? extends TreeItem<File>> c)
            {
                if (!item.getChildren().isEmpty() && item.getChildren().get(0).getValue() == null)
                    return; // dummy placeholder still present
                item.getChildren().removeListener(this);
                for (TreeItem<File> child : item.getChildren())
                    if (nextFile.equals(child.getValue()))
                    { syncTreeSelectionStep(child, segments, depth + 1); return; }
                updatingTreeSelection = false;
            }
        };
        item.getChildren().addListener(listener);
        if (!item.isExpanded())
            item.setExpanded(true); // fires the expand listener → starts background load
        // else: expansion already in progress; the listener above will fire when load completes
    }

    // =========================================================================
    // File operations
    // =========================================================================

    void refreshFileTable()
    {
        if (currentDirectory == null) return;
        if (pendingFileTask != null && pendingFileTask.isRunning())
        {
            pendingFileTask.cancel();
        }
        if (dirSizeTask != null && dirSizeTask.isRunning())
        {
            dirSizeTask.cancel();
            sizeStatusLabel.setText("");
        }
        final File dir = currentDirectory;
        final boolean capturedHidden = showHiddenFiles;
        final boolean capturedDirsOnly = dirsOnly;
        final String capturedFileType = fileType;
        final String capturedSortBy = sortBy;
        final boolean capturedAscending = sortAscending;
        final Locale capturedLocale = getBundleLocale();
        final Map<File, Long> capturedSizes = new HashMap<>(dirSizeMap);

        Task<List<File>> task = new Task<>()
        {
            @Override
            protected List<File> call()
            {
                File[] entries = dir.listFiles();
                if (entries == null) return new ArrayList<>();
                List<File> result = new ArrayList<>();
                for (File f : entries)
                {
                    if (isCancelled()) return result;
                    if (!capturedHidden && f.isHidden()) continue;
                    if (capturedDirsOnly && !f.isDirectory()) continue;
                    if (!"all".equals(capturedFileType) && !f.isDirectory())
                    {
                        String lname = f.getName().toLowerCase();
                        if ("text".equals(capturedFileType) && !lname.endsWith(".txt") && !lname.endsWith(".md")
                                && !lname.endsWith(".csv") && !lname.endsWith(".log")) continue;
                        if ("image".equals(capturedFileType) && !lname.endsWith(".jpg") && !lname.endsWith(".jpeg")
                                && !lname.endsWith(".png") && !lname.endsWith(".gif") && !lname.endsWith(".bmp")) continue;
                    }
                    result.add(f);
                }
                Collator col = Collator.getInstance(capturedLocale);
                Comparator<File> byName = (a, b) -> col.compare(a.getName(), b.getName());
                Comparator<File> secondary;
                switch (capturedSortBy)
                {
                    case "size": secondary = Comparator.comparingLong((File f) -> capturedSizes.getOrDefault(f, f.length())).thenComparing(byName); break;
                    case "date": secondary = Comparator.comparingLong(File::lastModified).thenComparing(byName); break;
                    default: secondary = byName;
                }
                Comparator<File> comparator = (a, b) ->
                {
                    boolean aDir = a.isDirectory(), bDir = b.isDirectory();
                    if (aDir && !bDir) return -1;
                    if (!aDir && bDir) return 1;
                    if (aDir)
                    {
                        if ("size".equals(capturedSortBy))
                        {
                            long sizeA = capturedSizes.getOrDefault(a, Long.MAX_VALUE);
                            long sizeB = capturedSizes.getOrDefault(b, Long.MAX_VALUE);
                            int cmp = Long.compare(sizeA, sizeB);
                            return cmp != 0 ? cmp : byName.compare(a, b);
                        }
                        return secondary.compare(a, b);
                    }
                    return secondary.compare(a, b);
                };
                result.sort(capturedAscending ? comparator : comparator.reversed());
                return result;
            }
        };
        task.setOnSucceeded(e ->
        {
            List<File> files = task.getValue();
            fileTableData.setAll(files);
            fileListData.setAll(files);
            fileCountLabel.setText(formatBundleString("ItemCountFormat", files.size()));
            startDirSizeComputation(files);
        });
        pendingFileTask = task;
        Thread fileLoader = new Thread(task, "FileLoader");
        fileLoader.setDaemon(true);
        fileLoader.start();
    }

    private void startDirSizeComputation(List<File> files)
    {
        List<File> dirs = new ArrayList<>();
        for (File f : files)
            if (f.isDirectory() && !dirSizeMap.containsKey(f))
                dirs.add(f);

        if (dirs.isEmpty())
        {
            sizeStatusLabel.setText("");
            return;
        }

        int total = dirs.size();

        dirSizeTask = new Task<>()
        {
            @Override
            protected Void call()
            {
                for (int i = 0; i < dirs.size() && !isCancelled(); i++)
                {
                    final int currentIdx = i + 1;
                    Platform.runLater(() -> sizeStatusLabel.setText(formatBundleString("ComputingSizesFormat", currentIdx, total)));
                    File dir = dirs.get(i);
                    long size = computeDirSize(dir, this::isCancelled);
                    if (isCancelled()) return null;
                    final long finalSize = size;
                    Platform.runLater(() ->
                    {
                        dirSizeMap.put(dir, finalSize);
                        fileTable.refresh();
                    });
                }
                return null;
            }
        };
        dirSizeTask.setOnSucceeded(e ->
        {
            sizeStatusLabel.setText("");
            if ("size".equals(sortBy)) refreshFileTable();
        });
        dirSizeTask.setOnCancelled(e -> sizeStatusLabel.setText(""));
        Thread dirSizeThread = new Thread(dirSizeTask, "DirSizeCompute");
        dirSizeThread.setDaemon(true);
        dirSizeThread.start();
    }

    private long computeDirSize(File dir)
    {
        return computeDirSize(dir, () -> false);
    }

    private long computeDirSize(File dir, BooleanSupplier isCancelled)
    {
        if (isCancelled.getAsBoolean()) return 0L;
        long[] total = {0L};
        try
        {
            Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes attrs)
                {
                    return isCancelled.getAsBoolean() ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                {
                    if (isCancelled.getAsBoolean()) return FileVisitResult.TERMINATE;
                    // Skip iCloud placeholder stubs (.FileName.icloud) to avoid blocking on network I/O
                    String name = file.getFileName().toString();
                    if (!(name.startsWith(".") && name.endsWith(".icloud")))
                        total[0] += attrs.size();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)
                {
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException ignored) {}
        return total[0];
    }

    private List<File> listSubdirectories(File dir)
    {
        File[] entries = dir.listFiles();
        if (entries == null) return new ArrayList<>();
        List<File> result = new ArrayList<>();
        for (File f : entries)
        {
            if (!f.isDirectory()) continue;
            if (!showHiddenFiles && f.isHidden()) continue;
            result.add(f);
        }
        Collator col = Collator.getInstance(getBundleLocale());
        result.sort((a, b) -> col.compare(a.getName(), b.getName()));
        return result;
    }

    void previewFile(File file)
    {
        if (file == null || file.isDirectory()) return;
        try
        {
            long size = file.length();
            if (size > 65536)
            {
                previewTextArea.setText(formatBundleString("FileTooLargeFormat", formatSize(size)));
            }
            else
            {
                String content = Files.readString(file.toPath(), java.nio.charset.StandardCharsets.UTF_8);
                previewTextArea.setText(content);
                previewTextArea.setScrollTop(0);
            }
        }
        catch (IOException ex)
        {
            previewTextArea.setText(formatBundleString("CannotReadFileFormat", ex.getMessage()));
        }
    }

    void showProperties(File file)
    {
        if (file == null) return;
        Locale locale = getBundleLocale();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        fileNameLabel.setText(formatBundleText("fileNameLabelProps", file.getName()));
        fileSizeLabel.setText(formatBundleText("fileSizeLabelProps", formatSize(file.length())));
        fileTypeLabel.setText(formatBundleText("fileTypeLabelProps", getFileType(file)));
        fileModifiedLabel.setText(formatBundleText("fileModifiedLabelProps", df.format(new Date(file.lastModified()))));
        String parent = file.getParent();
        filePathLabel.setText(formatBundleText("filePathLabelProps", parent != null ? parent : file.getAbsolutePath()));
        readOnlyCheckBox.setSelected(!file.canWrite());
        infoContentLabel.setText(file.getName() + "\n" + formatSize(file.length()) + " | " + getFileType(file));
    }

    private String formatBundleText(String key, Object... args)
    {
        LabeledPropertyBundle bundle = (LabeledPropertyBundle) getResourceBundle().getObject(key);
        String template = bundle.getText();
        if (template == null) return "";
        return new MessageFormat(template, getBundleLocale()).format(args);
    }

    private void createNewFolder()
    {
        if (currentDirectory == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(this);
        dialog.setTitle(getBundleString("FolderNamePromptText"));
        dialog.setHeaderText(null);
        dialog.setContentText(getBundleString("FolderNamePromptText"));
        dialog.getDialogPane().getButtonTypes().setAll(
                new ButtonType(getBundleString("OkButtonText"), ButtonBar.ButtonData.OK_DONE),
                new ButtonType(getBundleString("CancelButtonText"), ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.showAndWait().ifPresent(name ->
        {
            if (!name.isBlank())
            {
                new File(currentDirectory, name).mkdir();
                refreshFileTable();
            }
        });
    }

    private void deleteSelected()
    {
        File file = (File) fileTable.getSelectionModel().getSelectedItem();
        if (file == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(this);
        alert.setTitle(getBundleString("ConfirmDeleteTitle"));
        alert.setHeaderText(null);
        alert.setContentText(formatBundleString("ConfirmDeleteFormat", file.getName()));
        ButtonType yes = new ButtonType(getBundleString("YesText"), ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(getBundleString("NoText"), ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yes, no);
        alert.showAndWait().ifPresent(bt ->
        {
            if (bt == yes)
            {
                if (!file.delete())
                {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.initModality(Modality.APPLICATION_MODAL);
                    error.initOwner(this);
                    error.setTitle(getBundleString("DeleteFailedTitle"));
                    error.setHeaderText(null);
                    error.setContentText(formatBundleString("DeleteFailedFormat", file.getName()));
                    error.getButtonTypes().setAll(
                            new ButtonType(getBundleString("OkButtonText"), ButtonBar.ButtonData.OK_DONE));
                    error.showAndWait();
                }
                refreshFileTable();
            }
        });
    }

    private void showAboutDialog()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(this);
        alert.setTitle(getBundleString("AboutTitle"));
        alert.setHeaderText(getBundleString("AboutHeader"));
        alert.setContentText(getBundleString("AboutMessage"));
        alert.getButtonTypes().setAll(
                new ButtonType(getBundleString("OkButtonText"), ButtonBar.ButtonData.OK_DONE));
        alert.showAndWait();
    }

    private void openFXMLDemo()
    {
        try
        {
            FXMLContactStage demo = FXMLContactStage.create();
            demo.initOwner(this);
            demo.show();
        }
        catch (java.io.UncheckedIOException ex)
        {
            FXLogger.FX_LOGGER.log(System.Logger.Level.WARNING, "fxml.demo.open.failed", ex);
        }
    }

    private void openSwingInteropDemo()
    {
        SwingInteropStage demo = SwingInteropStage.create();
        demo.initOwner(this);
        demo.show();
    }

    // =========================================================================
    // Widget tab helpers
    // =========================================================================

    private void populateDemoList(Locale locale)
    {
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
        String[] weekdays = symbols.getWeekdays();
        demoDayItems.clear();
        for (int i = 1; i <= 7; i++)
        {
            demoDayItems.add(weekdays[i]);
        }
    }

    private void updateSliderLabels(Locale locale)
    {
        // JavaFX Slider doesn't support custom tick labels like Swing,
        // so we update the tooltip instead.
        demoSlider.setTooltip(new Tooltip(
                formatBundleString("DemoSliderTooltipFormat",
                        NumberFormat.getInstance(locale).format((long) demoSlider.getValue()))));
    }

    // =========================================================================
    // Formatting helpers
    // =========================================================================

    private String formatSize(long bytes)
    {
        invalidateFormatCache();
        if (sizeNumberFormat == null) sizeNumberFormat = NumberFormat.getInstance(getBundleLocale());
        if (bytes < 1024) return sizeNumberFormat.format(bytes) + getBundleString("BytesSuffix");
        if (bytes < 1024 * 1024) return sizeNumberFormat.format(bytes / 1024) + getBundleString("KilobytesSuffix");
        return sizeNumberFormat.format(bytes / (1024 * 1024)) + getBundleString("MegabytesSuffix");
    }

    private String getFileType(File file)
    {
        if (file.isDirectory()) return getBundleString("DirectoryTypeText");
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) return formatBundleString("FileTypeSuffixFormat", name.substring(dot + 1).toUpperCase());
        return getBundleString("GenericFileTypeText");
    }
}
