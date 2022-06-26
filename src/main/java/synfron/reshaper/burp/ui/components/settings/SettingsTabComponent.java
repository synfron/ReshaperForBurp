package synfron.reshaper.burp.ui.components.settings;

import burp.BurpExtender;
import com.alexandriasoftware.swing.JSplitButton;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;
import synfron.reshaper.burp.core.messages.Encoder;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.settings.GeneralSettings;
import synfron.reshaper.burp.core.settings.SettingsManager;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.utils.TextUtils;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;
import synfron.reshaper.burp.ui.components.IFormComponent;
import synfron.reshaper.burp.ui.utils.FocusActionListener;
import synfron.reshaper.burp.ui.utils.TableCellRenderer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsTabComponent extends JPanel implements IFormComponent {

    private JCheckBox overwriteDuplicates;
    private DefaultTableModel exportRulesModel;
    private DefaultTableModel exportVariablesModel;
    private final SettingsManager settingsManager = BurpExtender.getConnector().getSettingsManager();
    private final GeneralSettings generalSettings = BurpExtender.getGeneralSettings();
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
    private JCheckBox spider;
    private JCheckBox target;
    private JCheckBox extender;
    private ButtonGroup importMethod;

    public SettingsTabComponent() {
        initComponent();
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

        container.add(getMiscOptions());
        container.add(getCaptureTrafficOptions(), "top");
        return container;
    }

    private Component getMiscOptions() {
        JPanel container = new JPanel(new MigLayout());

        enableEventDiagnostics = new JCheckBox("Enable Event Diagnostics");
        diagnosticValueMaxLength = createTextField(false);
        enableSanityCheckWarnings = new JCheckBox("Enable Sanity Check Warnings");
        logInExtenderOutput = new JCheckBox("Replicate Logs in Extender Output");
        logTabCharacterLimit = createTextField(false);
        defaultEncoding = createComboBox(Encoder.getEncodings().toArray(new String[0]));
        JButton resetData = new JButton("Reset Data");

        enableEventDiagnostics.setSelected(generalSettings.isEnableEventDiagnostics());
        diagnosticValueMaxLength.setText(Objects.toString(generalSettings.getDiagnosticValueMaxLength()));
        enableSanityCheckWarnings.setSelected(generalSettings.isEnableSanityCheckWarnings());
        logInExtenderOutput.setSelected(generalSettings.isLogInExtenderOutput());
        logTabCharacterLimit.setText(Objects.toString(generalSettings.getLogTabCharacterLimit()));
        defaultEncoding.setSelectedItem(generalSettings.getDefaultEncoding());

        enableEventDiagnostics.addActionListener(this::onEnableEventDiagnosticsChanged);
        diagnosticValueMaxLength.addFocusListener(new FocusActionListener(this::onDiagnosticValueMaxLengthFocusChanged));
        enableSanityCheckWarnings.addActionListener(this::onEnableSanityCheckWarningsChanged);
        logInExtenderOutput.addActionListener(this::onLogInExtenderOutputChanged);
        logTabCharacterLimit.addFocusListener(new FocusActionListener(this::onLogTabCharacterLimitFocusChanged));
        defaultEncoding.addActionListener(this::onSetDefaultEncodingChanged);
        resetData.addActionListener(this::onResetData);


        container.add(enableEventDiagnostics, "wrap");
        container.add(getLabeledField("Diagnostic Value Max Length", diagnosticValueMaxLength), "wrap");
        container.add(enableSanityCheckWarnings, "wrap");
        container.add(logInExtenderOutput, "wrap");
        container.add(getLabeledField("Logs Tab Character Limit", logTabCharacterLimit), "wrap");
        container.add(getLabeledField("Default Encoding", defaultEncoding), "wrap");
        container.add(resetData, "wrap");
        return container;
    }

    private Component getCaptureTrafficOptions() {
        JPanel container = new JPanel(new MigLayout());
        container.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        proxy = new JCheckBox("Proxy");
        repeater = new JCheckBox("Repeater");
        intruder = new JCheckBox("Intruder");
        scanner = new JCheckBox("Scanner");
        spider = new JCheckBox("Spider");
        target = new JCheckBox("Target");
        extender = new JCheckBox("Extender");

        proxy.setSelected(generalSettings.isCaptureProxy());
        repeater.setSelected(generalSettings.isCaptureRepeater());
        intruder.setSelected(generalSettings.isCaptureIntruder());
        scanner.setSelected(generalSettings.isCaptureScanner());
        spider.setSelected(generalSettings.isCaptureSpider());
        target.setSelected(generalSettings.isCaptureTarget());
        extender.setSelected(generalSettings.isCaptureExtender());

        proxy.addActionListener(this::onProxyChanged);
        repeater.addActionListener(this::onRepeaterChanged);
        intruder.addActionListener(this::onIntruderChanged);
        scanner.addActionListener(this::onScannerChanged);
        spider.addActionListener(this::onSpiderChanged);
        target.addActionListener(this::onTargetChanged);
        extender.addActionListener(this::onExtenderChanged);

        container.add(new JLabel("Capture Traffic From:"), "wrap");
        container.add(proxy);
        container.add(repeater, "wrap");
        container.add(intruder);
        container.add(scanner, "wrap");
        container.add(spider);
        container.add(target, "wrap");
        container.add(extender);
        return container;
    }

    private void onResetData(ActionEvent actionEvent) {
        try {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset data? This will remove all rules and variables.", "Reset Data", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                settingsManager.resetData();
                refreshLists();
            }
        } catch (Exception e) {
            Log.get().withMessage("Error resetting data").withException(e).logErr();

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

    private void onSpiderChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureSpider(spider.isSelected());
    }

    private void onTargetChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureTarget(target.isSelected());
    }

    private void onExtenderChanged(ActionEvent actionEvent) {
        generalSettings.setCaptureExtender(extender.isSelected());
    }

    private Component getExportSettings() {
        JPanel container = new JPanel(new MigLayout());
        container.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder("Export"),
                BorderFactory.createEmptyBorder(4,4,4,4))
        );

        JButton refresh = new JButton("Refresh Lists");
        JButton exportData = new JButton("Export Data");

        refresh.addActionListener(this::onRefresh);
        exportData.addActionListener(this::onExportData);

        container.add(new JLabel("Items to Export"), "wrap");
        container.add(getExportRulesTable());
        container.add(getExportVariablesTable(), "wrap");
        container.add(getExportActions());
        return container;
    }

    private Component getExportActions() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refresh = new JButton("Refresh Lists");
        JButton exportData = new JButton("Export Data");

        refresh.addActionListener(this::onRefresh);
        exportData.addActionListener(this::onExportData);

        container.add(refresh);
        container.add(exportData);
        return container;
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
        JSplitButton importData = new JSplitButton("Import Data    ");

        JPopupMenu importOptions = new JPopupMenu();

        importMethod = new ButtonGroup();
        JRadioButtonMenuItem importFromFile = new JRadioButtonMenuItem("From File");
        JRadioButtonMenuItem importFromUrl = new JRadioButtonMenuItem("From URL");

        importFromFile.setSelected(generalSettings.getImportMethod() == GeneralSettings.ImportMethod.File);
        importFromFile.setActionCommand(GeneralSettings.ImportMethod.File.name());
        importFromUrl.setSelected(generalSettings.getImportMethod() == GeneralSettings.ImportMethod.Url);
        importFromUrl.setActionCommand(GeneralSettings.ImportMethod.Url.name());

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
    }

    private JFileChooser createFileChooser(String title) {
        FileNameExtensionFilter fileFiler = new FileNameExtensionFilter("JSON backup file", "json");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(fileFiler);
        fileChooser.addChoosableFileFilter(fileFiler);
        return fileChooser;
    }

    private void onExportData(ActionEvent actionEvent) {
        try {
            JFileChooser fileChooser = createFileChooser("Export");
            fileChooser.setSelectedFile(new File("~/ReshaperBackup.json"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                settingsManager.exportSettings(
                        fileChooser.getSelectedFile(),
                        exportVariablesModel.getDataVector().stream()
                                .filter(row -> (boolean)row.get(0))
                                .map(row -> (Variable)row.get(1))
                                .collect(Collectors.toList()),
                        exportRulesModel.getDataVector().stream()
                                .filter(row -> (boolean)row.get(0))
                                .map(row -> (Rule)row.get(1))
                                .collect(Collectors.toList())
                );

                JOptionPane.showMessageDialog(this,
                        "Export successful",
                        "Export",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (Exception e) {
            Log.get().withMessage("Error exporting data").withException(e).logErr();

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
            JFileChooser fileChooser = createFileChooser("Import");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile().getAbsolutePath();
                settingsManager.importSettings(fileChooser.getSelectedFile(), overwriteDuplicates.isSelected());
                refreshLists();

                JOptionPane.showMessageDialog(this,
                        "Import successful",
                        "Import",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (Exception e) {
            Log.get().withMessage("Error importing data from file").withException(e).logErr();

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
                settingsManager.importSettings(settingsJson, overwriteDuplicates.isSelected());
                refreshLists();

                JOptionPane.showMessageDialog(this,
                        "Import successful",
                        "Import",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        } catch (Exception e) {
            Log.get().withMessage("Error importing data from URL").withException(e).logErr();

            JOptionPane.showMessageDialog(this,
                    String.format("Error importing data from URL: %s", url),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshLists() {
        for (int row = exportRulesModel.getRowCount() - 1; row >= 0; row--) {
            exportRulesModel.removeRow(row);
        }
        for (int row = exportVariablesModel.getRowCount() - 1; row >= 0; row--) {
            exportVariablesModel.removeRow(row);
        }
        Stream.of(getExportRulesData()).forEach(row -> exportRulesModel.addRow(row));
        Stream.of(getExportVariablesData()).forEach(row -> exportVariablesModel.addRow(row));
    }

    private void onRefresh(ActionEvent actionEvent) {
        refreshLists();
    }

    private Component getExportRulesTable() {
        JTable exportRulesTable = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            }
        };
        exportRulesTable.setDefaultRenderer(Object.class, new TableCellRenderer());
        JScrollPane scrollPane = new JScrollPane(exportRulesTable);
        exportRulesModel = createTableModel(getExportRulesData(), new Object[] { "Export", "Rule Name" });
        exportRulesTable.setModel(exportRulesModel);
        exportRulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return scrollPane;
    }

    private Component getExportVariablesTable() {
        JTable exportVariablesTable = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            }
        };
        exportVariablesTable.setDefaultRenderer(Object.class, new TableCellRenderer());
        JScrollPane scrollPane = new JScrollPane(exportVariablesTable);
        exportVariablesModel = createTableModel(getExportVariablesData(), new Object[] { "Export", "Variable Name" });
        exportVariablesTable.setModel(exportVariablesModel);
        exportVariablesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return scrollPane;
    }

    private DefaultTableModel createTableModel(Object[][] data, Object[] columnNames) {
        return new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return Object.class;
            }
        };
    }

    private Object[][] getExportRulesData() {
        return BurpExtender.getConnector().getRulesEngine().getRulesRegistry().exportRules().stream()
                .map(rule -> new Object[] { true, rule })
                .toArray(Object[][]::new);
    }

    private Object[][] getExportVariablesData() {
        return GlobalVariables.get().exportVariables().stream()
                .map(variable -> new Object[] { true, variable })
                .toArray(Object[][]::new);
    }
}

