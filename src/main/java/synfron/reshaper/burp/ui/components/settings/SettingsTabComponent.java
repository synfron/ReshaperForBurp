package synfron.reshaper.burp.ui.components.settings;

import burp.BurpExtender;
import net.miginfocom.swing.MigLayout;
import synfron.reshaper.burp.core.rules.Rule;
import synfron.reshaper.burp.core.settings.SettingsManager;
import synfron.reshaper.burp.core.utils.Log;
import synfron.reshaper.burp.core.vars.GlobalVariables;
import synfron.reshaper.burp.core.vars.Variable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsTabComponent extends JPanel {

    private JTable exportRulesTable;
    private JTable exportVariablesTable;
    private JCheckBox overrideDuplicates;
    private DefaultTableModel exportRulesModel;
    private DefaultTableModel exportVariablesModel;
    private SettingsManager settingsManager = new SettingsManager();

    public SettingsTabComponent() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(getExportSettings());
        add(getImportSettings());
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

        overrideDuplicates = new JCheckBox("Override Duplicates");
        JButton importData = new JButton("Import Data");

        importData.addActionListener(this::onImportData);

        container.add(overrideDuplicates);
        container.add(importData);
        return container;
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
        try {
            JFileChooser fileChooser = createFileChooser("Import");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                settingsManager.importSettings(fileChooser.getSelectedFile(), overrideDuplicates.isSelected());
            }
            refreshLists();

            JOptionPane.showMessageDialog(this,
                    "Import successful",
                    "Import",
                    JOptionPane.PLAIN_MESSAGE
            );
        } catch (Exception e) {
            Log.get().withMessage("Error importing data").withException(e).logErr();

            JOptionPane.showMessageDialog(this,
                    "Error importing data",
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
        exportRulesTable = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            }
        };
        JScrollPane scrollPane = new JScrollPane(exportRulesTable);
        exportRulesModel = createTableModel(getExportRulesData(), new Object[] { "Export", "Rule Name" });
        exportRulesTable.setModel(exportRulesModel);
        exportRulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return scrollPane;
    }

    private Component getExportVariablesTable() {
        exportVariablesTable = new JTable() {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            }
        };
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
                switch (columnIndex) {
                    case 0:
                        return Boolean.class;
                    default:
                        return Object.class;
                }
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
