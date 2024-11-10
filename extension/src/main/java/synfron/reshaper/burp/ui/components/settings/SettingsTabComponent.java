package synfron.reshaper.burp.ui.components.settings;

import com.alexandriasoftware.swing.JSplitButton;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import synfron.reshaper.burp.core.BurpTool;
import synfron.reshaper.burp.core.events.CollectionChangedArgs;
import synfron.reshaper.burp.core.events.IEventListener;
import synfron.reshaper.burp.core.events.PropertyChangedArgs;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.settings.*;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.ui.components.shared.IFormComponent;
import synfron.reshaper.burp.ui.components.workspaces.IWorkspaceDependentComponent;
import synfron.reshaper.burp.ui.models.settings.HideItemsModel;
import synfron.reshaper.burp.ui.utils.ComponentVisibilityManager;
import synfron.reshaper.burp.ui.utils.FocusActionListener;
import synfron.reshaper.burp.ui.utils.ModalPrompter;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SettingsTabComponent extends JPanel implements IFormComponent, HierarchyListener, IWorkspaceDependentComponent {
    @Getter
    private final Workspace workspace;
    private JCheckBox overwriteDuplicates;
    private SelectionTable<Rule> exportHttpRulesTable;
    private SelectionTable<Rule> exportWebSocketRulesTable;
    private SelectionTable<Variable> exportVariablesTable;
    private final GeneralSettings generalSettings;
    private JCheckBox enableEventDiagnostics;
    private JTextField diagnosticValueMaxLength;
    private JCheckBox enableSanityCheckWarnings;
    private JCheckBox logInExtenderOutput;
    private JTextField logTabCharacterLimit;
    private JComboBox<String> defaultEncoding;
    private JCheckBox proxy;
    private JCheckBox repeater;
    private JCheckBox intruder;
    private JCheckBox scanner;
    private JCheckBox target;
    private JCheckBox extender;
    private JCheckBox session;
    private JCheckBox webSockets;
    private ButtonGroup importMethod;
    private ButtonGroup exportMethod;
    private JSplitButton exportData;
    private JSplitButton importData;
    private final IEventListener<PropertyChangedArgs> workspacesPropertyChangedListener = this::onWorkspacesPropertyChanged;
    private final IEventListener<CollectionChangedArgs> workspacesChangedListener = this::onWorkspacesChangedListener;

    private JButton enableWorkspaces;
    private JPanel rightGeneralSettings;

    public SettingsTabComponent() {
        workspace = getHostedWorkspace(this);
        generalSettings = workspace.getGeneralSettings();
        initComponent();
        Workspaces.get().withListener(workspacesPropertyChangedListener)
                .withCollectionListener(workspacesChangedListener);
    }

    private void onWorkspacesPropertyChanged(PropertyChangedArgs propertyChangedArgs) {
        if (propertyChangedArgs.getName().equals("enabled")) {
            enableWorkspaces.setText(Workspaces.get().isEnabled() ? "Disable Workspaces" : "Enable Workspaces");
            ComponentVisibilityManager.updateVisibility(rightGeneralSettings, enableWorkspaces, !Workspaces.get().isEnabled() || Workspaces.get().getWorkspaces().size() == 1);
        } else if  (propertyChangedArgs.getName().equals("workspaces")) {
            ComponentVisibilityManager.updateVisibility(rightGeneralSettings, enableWorkspaces, !Workspaces.get().isEnabled() || Workspaces.get().getWorkspaces().size() == 1);
        }
    }

    private void onWorkspacesChangedListener(CollectionChangedArgs collectionChangedArgs) {
        ComponentVisibilityManager.updateVisibility(rightGeneralSettings, enableWorkspaces, !Workspaces.get().isEnabled() || Workspaces.get().getWorkspaces().size() == 1);
    }

    private void initComponent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(getGeneralSettings());
        add(getExportSettings());
        add(getImportSettings());
    }

    private Component getGeneralSettings() {
        JPanel container = new JPanel(new MigLayout());
        container.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("General"),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );

        container.add(LeftGeneralOptions());
        container.add(RightGeneralOptions(), "top");
        return container;
    }

    private void onEnableWorkspaces(ActionEvent actionEvent) {
        Workspaces.get().setEnabled(!Workspaces.get().isEnabled());
    }

    private void onHideFeatures(ActionEvent actionEvent) {
        HideItemsModel model = new HideItemsModel(generalSettings);
        ModalPrompter.open(model, new ModalPrompter.FormPromptArgs<>(
                "Hide Features",
                model,
                new HideItemsComponent(model)
        ));
    }

    private Component RightGeneralOptions() {
        rightGeneralSettings = new JPanel(new MigLayout());
        rightGeneralSettings.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        defaultEncoding = createComboBox(Encoder.getEncodings().toArray(new String[0]));

        defaultEncoding.setSelectedItem(generalSettings.getDefaultEncoding());

        defaultEncoding.addActionListener(this::onSetDefaultEncodingChanged);

        enableWorkspaces = new JButton(Workspaces.get().isEnabled() ? "Disable Workspaces" : "Enable Workspaces");
        enableWorkspaces.addActionListener(this::onEnableWorkspaces);

        rightGeneralSettings.add(getLabeledField("Default Encoding", defaultEncoding), "wrap");
        rightGeneralSettings.add(getCaptureTrafficOptions(), "wrap");
        rightGeneralSettings.add(enableWorkspaces, "wrap");

        ComponentVisibilityManager.updateVisibility(rightGeneralSettings, enableWorkspaces, !Workspaces.get().isEnabled() || Workspaces.get().getWorkspaces().size() == 1);
        return rightGeneralSettings;
    }

    private Component LeftGeneralOptions() {
        JPanel container = new JPanel(new MigLayout());

        enableEventDiagnostics = new JCheckBox("Enable Event Diagnostics");
        diagnosticValueMaxLength = createTextField(false);
        enableSanityCheckWarnings = new JCheckBox("Enable Sanity Check Warnings");
        logInExtenderOutput = new JCheckBox("Replicate Logs to Extension Output");
        logTabCharacterLimit = createTextField(false);
        JButton hideFeatures = new JButton("Hide Features");
        JButton resetData = new JButton("Reset Data");

        enableEventDiagnostics.setSelected(generalSettings.isEnableEventDiagnostics());
        diagnosticValueMaxLength.setText(Objects.toString(generalSettings.getDiagnosticValueMaxLength()));
        enableSanityCheckWarnings.setSelected(generalSettings.isEnableSanityCheckWarnings());
        logInExtenderOutput.setSelected(generalSettings.isLogInExtenderOutput());
        logTabCharacterLimit.setText(Objects.toString(generalSettings.getLogTabCharacterLimit()));

        enableEventDiagnostics.addActionListener(this::onEnableEventDiagnosticsChanged);
        diagnosticValueMaxLength.addFocusListener(new FocusActionListener(this::onDiagnosticValueMaxLengthFocusChanged));
        enableSanityCheckWarnings.addActionListener(this::onEnableSanityCheckWarningsChanged);
        logInExtenderOutput.addActionListener(this::onLogInExtenderOutputChanged);
        logTabCharacterLimit.addFocusListener(new FocusActionListener(this::onLogTabCharacterLimitFocusChanged));
        hideFeatures.addActionListener(this::onHideFeatures);
        resetData.addActionListener(this::onResetData);


        container.add(enableEventDiagnostics, "wrap");
        container.add(getLabeledField("Diagnostic Value Max Length", diagnosticValueMaxLength), "wrap");
        container.add(enableSanityCheckWarnings, "wrap");
        container.add(logInExtenderOutput, "wrap");
        container.add(getLabeledField("Logs Tab Character Limit", logTabCharacterLimit), "wrap");

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(hideFeatures);
        buttons.add(resetData);
        container.add(buttons);
        return container;
    }

    private Component getCaptureTrafficOptions() {
        JPanel container = new JPanel(new MigLayout());

        proxy = new JCheckBox(BurpTool.Proxy.toString());
        repeater = new JCheckBox(BurpTool.Repeater.toString());
        intruder = new JCheckBox(BurpTool.Intruder.toString());
        scanner = new JCheckBox(BurpTool.Scanner.toString());
        target = new JCheckBox(BurpTool.Target.toString());
        extender = new JCheckBox(BurpTool.Extender.toString());
        session = new JCheckBox(BurpTool.Session.toString());
        webSockets = new JCheckBox(BurpTool.WebSockets.toString());

        proxy.setSelected(generalSettings.isCaptureProxy());
        repeater.setSelected(generalSettings.isCaptureRepeater());
        intruder.setSelected(generalSettings.isCaptureIntruder());
        scanner.setSelected(generalSettings.isCaptureScanner());
        target.setSelected(generalSettings.isCaptureTarget());
        extender.setSelected(generalSettings.isCaptureExtender());
        session.setSelected(generalSettings.isCaptureSession());
        webSockets.setSelected(generalSettings.isCaptureWebSockets());

        proxy.addActionListener(this::onProxyChanged);
        repeater.addActionListener(this::onRepeaterChanged);
        intruder.addActionListener(this::onIntruderChanged);
        scanner.addActionListener(this::onScannerChanged);
        target.addActionListener(this::onTargetChanged);
        extender.addActionListener(this::onExtenderChanged);
        session.addActionListener(this::onSessionChanged);
        webSockets.addActionListener(this::onWebSocketsChanged);

        container.add(new JLabel("Capture Traffic From:"), "wrap");
        container.add(proxy);
        container.add(repeater, "wrap");
        container.add(intruder);
        container.add(scanner, "wrap");
        container.add(target);
        container.add(extender, "wrap");
        container.add(session);
        container.add(webSockets, "wrap");
        return container;
    }

    private void onResetData(ActionEvent actionEvent) {
        try {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset data? This will remove all rules and variables.", "Reset Data", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                SettingsManager.resetData(workspace);
                refreshLists();
            }
        } catch (Exception e) {
            Log.get(workspace).withMessage("Error resetting data").withException(e).logErr();

            JOptionPane.showMessageDialog(this,
                    "Error resetting data",
                    "Reset Data Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onSetDefaultEncodingChanged(ActionEvent actionEvent) {
        generalSettings.setDefaultEncoding((String) defaultEncoding.getSelectedItem());
    }

    private void onLogTabCharacterLimitFocusChanged(ActionEvent actionEvent) {
        if (actionEvent.getID() == FocusEvent.FOCUS_LOST && TextUtils.isInt(logTabCharacterLimit.getText())) {
            generalSettings.setLogTabCharacterLimit(Integer.parseInt(logTabCharacterLimit.getText()));
        }
    }

    private void onLogInExtenderOutputChanged(ActionEvent actionEvent) {
        generalSettings.setLogInExtenderOutput(logInExtenderOutput.isSelected());
    }

    private void onEnableEventDiagnosticsChanged(ActionEvent actionEvent) {
        generalSettings.setEnableEventDiagnostics(enableEventDiagnostics.isSelected());
    }

    private void onDiagnosticValueMaxLengthFocusChanged(ActionEvent actionEvent) {
        if (actionEvent.getID() == FocusEvent.FOCUS_LOST && TextUtils.isInt(diagnosticValueMaxLength.getText())) {
            generalSettings.setDiagnosticValueMaxLength(Integer.parseInt(diagnosticValueMaxLength.getText()));
        }
    }

    private void onEnableSanityCheckWarningsChanged(ActionEvent actionEvent) {
        generalSettings.setEnableSanityCheckWarnings(enableSanityCheckWarnings.isSelected());
    }

    private void onProxyChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureProxy(proxy.isSelected());
    }

    private void onRepeaterChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureRepeater(repeater.isSelected());
    }

    private void onIntruderChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureIntruder(intruder.isSelected());
    }

    private void onScannerChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureScanner(scanner.isSelected());
    }

    private void onTargetChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureTarget(target.isSelected());
    }

    private void onExtenderChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureExtender(extender.isSelected());
    }

    private void onSessionChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureSession(session.isSelected());
    }

    private void onWebSocketsChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureWebSockets(webSockets.isSelected());
    }

    private Component getExportSettings() {
        JPanel container = new JPanel(new MigLayout());
        container.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Export"),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );

        JButton exportData = new JButton("Export Data");
        exportHttpRulesTable = new SelectionTable<>(new String[] { "Export", "HTTP Rule Name" }, true);
        exportWebSocketRulesTable = new SelectionTable<>(new String[] { "Export", "WebSocket Rule Name" }, true);
        exportVariablesTable = new SelectionTable<>(new String[] { "Export", "Persisted Variable Name" }, true);

        exportData.addActionListener(this::onExportData);

        container.add(new JLabel("Items to Export"), "wrap");
        container.add(exportHttpRulesTable);
        container.add(exportWebSocketRulesTable);
        container.add(exportVariablesTable, "wrap");
        container.add(getExportDataButton());
        return container;
    }

    private JSplitButton getExportDataButton() {
        exportData = new JSplitButton("Export Data (JSON)    ");

        JPopupMenu exportOptions = new JPopupMenu();

        exportMethod = new ButtonGroup();
        JRadioButtonMenuItem exportFromJson = new JRadioButtonMenuItem("To JSON");
        JRadioButtonMenuItem exportFromYaml = new JRadioButtonMenuItem("To YAML");

        exportFromJson.setSelected(generalSettings.getExportMethod() == GeneralSettings.ExportMethod.Json);
        exportFromJson.setActionCommand(GeneralSettings.ExportMethod.Json.name());
        exportFromYaml.setSelected(generalSettings.getExportMethod() == GeneralSettings.ExportMethod.Yaml);
        exportFromYaml.setActionCommand(GeneralSettings.ExportMethod.Yaml.name());

        setExportDataSelection(exportFromYaml);

        exportData.addButtonClickedActionListener(this::onExportData);
        exportFromYaml.addItemListener(this::onExportMethodChange);

        exportMethod.add(exportFromJson);
        exportMethod.add(exportFromYaml);

        exportOptions.add(exportFromJson);
        exportOptions.add(exportFromYaml);

        exportData.setPopupMenu(exportOptions);

        return exportData;
    }

    private Component getImportSettings() {
        JPanel container = new JPanel(new MigLayout());
        container.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Import"),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );

        overwriteDuplicates = new JCheckBox("Overwrite Duplicates");
        JSplitButton importData = getImportDataButton();

        container.add(overwriteDuplicates);
        container.add(importData);
        return container;
    }

    private JSplitButton getImportDataButton() {
        importData = new JSplitButton("Import Data (File)    ");

        JPopupMenu importOptions = new JPopupMenu();

        importMethod = new ButtonGroup();
        JRadioButtonMenuItem importFromFile = new JRadioButtonMenuItem("From File");
        JRadioButtonMenuItem importFromUrl = new JRadioButtonMenuItem("From URL");

        importFromFile.setSelected(generalSettings.getImportMethod() == GeneralSettings.ImportMethod.File);
        importFromFile.setActionCommand(GeneralSettings.ImportMethod.File.name());
        importFromUrl.setSelected(generalSettings.getImportMethod() == GeneralSettings.ImportMethod.Url);
        importFromUrl.setActionCommand(GeneralSettings.ImportMethod.Url.name());

        setImportDataSelection(importFromFile);

        importData.addButtonClickedActionListener(this::onImportData);
        importFromFile.addItemListener(this::onImportMethodChange);

        importMethod.add(importFromFile);
        importMethod.add(importFromUrl);

        importOptions.add(importFromFile);
        importOptions.add(importFromUrl);

        importData.setPopupMenu(importOptions);

        return importData;
    }

    private void onImportMethodChange(ItemEvent itemEvent) {
        generalSettings.setImportMethod(GeneralSettings.ImportMethod.valueOf(importMethod.getSelection().getActionCommand()));
        setImportDataSelection((JRadioButtonMenuItem) itemEvent.getItem());
    }

    private void setImportDataSelection(JRadioButtonMenuItem fileMenuItem) {
        if (fileMenuItem.isSelected()) {
            importData.setText("Import Data (File)    ");
        } else {
            importData.setText("Import Data (URL)    ");
        }
    }


    private JFileChooser createFileChooser(String title, GeneralSettings.ExportMethod... allowedFileTypes) {
        String extension = switch (generalSettings.getExportMethod()) {
            case Json -> ".json";
            case Yaml -> ".yaml";
        };
        FileNameExtensionFilter fileFiler = new FileNameExtensionFilter(
                Arrays.stream(allowedFileTypes)
                        .map(type -> type.name().toUpperCase())
                        .collect(Collectors.joining("/")) + " backup file", Arrays.stream(allowedFileTypes)
                .map(type -> type.name().toLowerCase())
                .toArray(String[]::new)
        );
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(fileFiler);
        fileChooser.addChoosableFileFilter(fileFiler);
        fileChooser.setSelectedFile(new File("~/" + FilenameUtils.getBaseName(generalSettings.getLastExportFileName()) + extension));
        if (StringUtils.isNotEmpty(generalSettings.getLastExportPath())) {
            File lastImportPath = new File(generalSettings.getLastExportPath());
            if (lastImportPath.isDirectory()) {
                fileChooser.setCurrentDirectory(new File(generalSettings.getLastExportPath()));
            }
        }
        return fileChooser;
    }

    private void onExportMethodChange(ItemEvent itemEvent) {
        generalSettings.setExportMethod(GeneralSettings.ExportMethod.valueOf(exportMethod.getSelection().getActionCommand()));
        setExportDataSelection((JRadioButtonMenuItem)itemEvent.getItem());
    }

    private void setExportDataSelection(JRadioButtonMenuItem yamlMenuItem) {
        if (yamlMenuItem.isSelected()) {
            exportData.setText("Export Data (YAML)    ");
        } else {
            exportData.setText("Export Data (JSON)    ");
        }
    }

    private void onExportData(ActionEvent actionEvent) {
        try {
            JFileChooser fileChooser = createFileChooser("Export", generalSettings.getExportMethod());
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                generalSettings.setLastExportPath(FilenameUtils.getFullPath(fileChooser.getSelectedFile().getAbsolutePath()));
                generalSettings.setLastExportFileName(FilenameUtils.getName(fileChooser.getSelectedFile().getAbsolutePath()));
                SettingsManager.exportWorkspaceData(
                        workspace,
                        fileChooser.getSelectedFile(),
                        new WorkspaceDataExport(
                            exportHttpRulesTable.getSelectedValues(),
                            exportWebSocketRulesTable.getSelectedValues(),
                            exportVariablesTable.getSelectedValues()
                        )
                );

                JOptionPane.showMessageDialog(this,
                        "Export successful",
                        "Export",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (Exception e) {
            Log.get(workspace).withMessage("Error exporting data").withException(e).logErr();

            JOptionPane.showMessageDialog(this,
                    "Error exporting data",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onImportData(ActionEvent actionEvent) {
        switch (generalSettings.getImportMethod()) {
            case File -> onImportFromFile();
            case Url -> onImportFromUrl();
        }
    }

    private void onImportFromFile() {
        String file = null;
        try {
            JFileChooser fileChooser = createFileChooser("Import", GeneralSettings.ExportMethod.Json, GeneralSettings.ExportMethod.Yaml);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile().getAbsolutePath();
                generalSettings.setLastExportPath(FilenameUtils.getFullPath(file));
                generalSettings.setLastExportFileName(FilenameUtils.getName(file));
                SettingsManager.importSettings(workspace, fileChooser.getSelectedFile(), overwriteDuplicates.isSelected());
                refreshLists();

                JOptionPane.showMessageDialog(this,
                        "Import successful",
                        "Import",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (Exception e) {
            Log.get(workspace).withMessage("Error importing data from file").withException(e).logErr();

            JOptionPane.showMessageDialog(this,
                    String.format("Error importing data from file: %s", file),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onImportFromUrl() {
        String url = null;
        try {
            url = JOptionPane.showInputDialog("Import URL", generalSettings.getImportUrl());
            if (url != null) {
                generalSettings.setImportUrl(url);
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                String settingsJson = IOUtils.toString(connection.getInputStream(), Charset.defaultCharset());
                SettingsManager.importSettings(workspace, settingsJson, overwriteDuplicates.isSelected());
                refreshLists();

                JOptionPane.showMessageDialog(this,
                        "Import successful",
                        "Import",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (Exception e) {
            Log.get(workspace).withMessage("Error importing data from URL").withException(e).logErr();

            JOptionPane.showMessageDialog(this,
                    String.format("Error importing data from URL: %s", url),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addHierarchyListener(this);
    }

    @Override
    public void removeNotify() {
        removeHierarchyListener(this);
        super.removeNotify();
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
            refreshLists();
        }
    }

    private void refreshLists() {
        exportHttpRulesTable.setValues(getExportHttpRulesData());
        exportWebSocketRulesTable.setValues(getExportWebSocketRulesData());
        exportVariablesTable.setValues(getExportVariablesData());
    }

    private List<Rule> getExportHttpRulesData() {
        return workspace.getHttpRulesRegistry().exportRules();
    }

    private List<Rule> getExportWebSocketRulesData() {
        return workspace.getWebSocketRulesRegistry().exportRules();
    }

    private List<Variable> getExportVariablesData() {
        return workspace.getGlobalVariables().exportVariables();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Component & IFormComponent> T getComponent() {
        return (T) this;
    }
}

