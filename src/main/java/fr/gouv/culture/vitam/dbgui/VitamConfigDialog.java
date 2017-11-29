/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fr.gouv.culture.vitam.dbgui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Types;

import javax.swing.border.CompoundBorder;

import fr.gouv.culture.vitam.constance.ConstanceDataType;
import fr.gouv.culture.vitam.database.utils.FileExtensionFilter;
import fr.gouv.culture.vitam.database.utils.StaticValues;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dimension;

/**
 * Dialog to handle configuration change in the GUI
 * 
 * @author "Frederic Bregier"
 * 
 */
public class VitamConfigDialog extends JPanel {
	private static final long serialVersionUID = 5129887729538501977L;
	JFrame frame;
	private DatabaseGui databaseGui;
	private static boolean fromMain = false;
	
	private JTextField separator;
	private JTextField commonTableName;
	private JComboBox databaseType;
	private JTextField databaseJDBC_Start;
	private JTextField databaseJDBC_Option;
	private JTextField databasePosition;
	private JTextField databaseUser;
	private JTextField databasePassword;
	private JCheckBox chckbxExportDataEn;
	private JCheckBox chckbxUpdateConfigurationFile;
	private JTextField textInteger;
	private JTextField textFloat;
	private JTextField textShortString;
	private JTextField databaseReadUser;
	private JTextField databaseReadPassword;
	
	/**
	 * @param frame the parent frame
	 * @param databaseGui the DatabaseGui associated
	 */
	public VitamConfigDialog(JFrame frame, DatabaseGui databaseGui) {
		super(new BorderLayout());
		this.databaseGui = databaseGui;
		this.frame = frame;
		setBorder(new CompoundBorder());

		JPanel buttonPanel = new JPanel();
		GridBagLayout buttons = new GridBagLayout();
		buttons.columnWidths = new int[] { 194, 124, 0, 0, 0 };
		buttons.rowHeights = new int[] { 0, 0 };
		buttons.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		buttons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		buttonPanel.setLayout(buttons);
		add(buttonPanel, BorderLayout.SOUTH);

		JButton btnSaveConfig = new JButton(StaticValues.LBL.button_save.get());
		btnSaveConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
		GridBagConstraints gbc_btnSaveConfig = new GridBagConstraints();
		gbc_btnSaveConfig.insets = new Insets(0, 0, 0, 5);
		gbc_btnSaveConfig.gridx = 0;
		gbc_btnSaveConfig.gridy = 0;
		buttonPanel.add(btnSaveConfig, gbc_btnSaveConfig);

		String text = StaticValues.LBL.button_cancel.get();
		if (fromMain) {
			text += " " + StaticValues.LBL.button_exit.get();
		}
		JButton btnCancel = new JButton(text);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 0;
		buttonPanel.add(btnCancel, gbc_btnCancel);
		
		chckbxUpdateConfigurationFile = new JCheckBox(StaticValues.LBL.button_update.get());
		GridBagConstraints gbc_chckbxUpdateConfigurationFile = new GridBagConstraints();
		gbc_chckbxUpdateConfigurationFile.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxUpdateConfigurationFile.gridx = 2;
		gbc_chckbxUpdateConfigurationFile.gridy = 0;
		chckbxUpdateConfigurationFile.setSelected(true);
		buttonPanel.add(chckbxUpdateConfigurationFile, gbc_chckbxUpdateConfigurationFile);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		databasePanel(tabbedPane);
		typePanel(tabbedPane);
	}
	private void databasePanel(JTabbedPane tabbedPane) {
		JPanel xmlFilePanel = new JPanel();
		tabbedPane.addTab("Database configuration", null, xmlFilePanel, null);
		GridBagLayout gbl_xmlFilePanel = new GridBagLayout();
		gbl_xmlFilePanel.columnWidths = new int[] { 21, 38, 86, 0, 45, 0, 86, 72, 34, 0 };
		gbl_xmlFilePanel.rowHeights = new int[] { 0, 20, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_xmlFilePanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0,
				Double.MIN_VALUE };
		gbl_xmlFilePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		xmlFilePanel.setLayout(gbl_xmlFilePanel);
		
		JLabel lblSeparateurCsv = new JLabel("Separateur CSV");
		GridBagConstraints gbc_lblSeparateurCsv = new GridBagConstraints();
		gbc_lblSeparateurCsv.anchor = GridBagConstraints.EAST;
		gbc_lblSeparateurCsv.insets = new Insets(0, 0, 5, 5);
		gbc_lblSeparateurCsv.gridx = 1;
		gbc_lblSeparateurCsv.gridy = 0;
		xmlFilePanel.add(lblSeparateurCsv, gbc_lblSeparateurCsv);
		
		separator = new JTextField();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridx = 2;
		gbc_separator.gridy = 0;
		xmlFilePanel.add(separator, gbc_separator);
		separator.setColumns(10);
		
		JLabel lblPrfixeTable = new JLabel("Préfixe Table (vide de préférence)");
		GridBagConstraints gbc_lblPrfixeTable = new GridBagConstraints();
		gbc_lblPrfixeTable.anchor = GridBagConstraints.EAST;
		gbc_lblPrfixeTable.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrfixeTable.gridx = 1;
		gbc_lblPrfixeTable.gridy = 1;
		xmlFilePanel.add(lblPrfixeTable, gbc_lblPrfixeTable);
		
		commonTableName = new JTextField();
		GridBagConstraints gbc_commonTableName = new GridBagConstraints();
		gbc_commonTableName.gridwidth = 5;
		gbc_commonTableName.insets = new Insets(0, 0, 5, 5);
		gbc_commonTableName.fill = GridBagConstraints.HORIZONTAL;
		gbc_commonTableName.gridx = 2;
		gbc_commonTableName.gridy = 1;
		xmlFilePanel.add(commonTableName, gbc_commonTableName);
		commonTableName.setColumns(10);
		
		JLabel lblDbType = new JLabel("DB Type");
		GridBagConstraints gbc_lblDbType = new GridBagConstraints();
		gbc_lblDbType.anchor = GridBagConstraints.EAST;
		gbc_lblDbType.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbType.gridx = 1;
		gbc_lblDbType.gridy = 2;
		xmlFilePanel.add(lblDbType, gbc_lblDbType);
		
		databaseType = new JComboBox();
		databaseType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String get = (String) databaseType.getSelectedItem();
				if (get.equals(StaticValues.TYPEORACLE)) {
					databaseJDBC_Start.setText(StaticValues.JDBCORACLE);
					databaseJDBC_Option.setText("");
				} else if (get.equals(StaticValues.TYPEMYSQL)) {
					databaseJDBC_Start.setText(StaticValues.JDBCMYSQL);
					databaseJDBC_Option.setText("");
				} else if (get.equals(StaticValues.TYPEPOSTGRE)) {
					databaseJDBC_Start.setText(StaticValues.JDBCPOSTGRE);
					databaseJDBC_Option.setText("");
				} else {
					databaseJDBC_Start.setText(StaticValues.JDBCH2);
					databaseJDBC_Option.setText(StaticValues.DEFAULT_JDBC_OPTION);
				}
			}
		});
		databaseType.setModel(new DefaultComboBoxModel(new String[] {
				StaticValues.TYPEH2, StaticValues.TYPEPOSTGRE, StaticValues.TYPEMYSQL, 
				StaticValues.TYPEORACLE}));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 2;
		xmlFilePanel.add(databaseType, gbc_comboBox);
		
		JLabel lblJdbcStartAccess = new JLabel("JDBC chaîne de connection");
		GridBagConstraints gbc_lblJdbcStartAccess = new GridBagConstraints();
		gbc_lblJdbcStartAccess.anchor = GridBagConstraints.EAST;
		gbc_lblJdbcStartAccess.insets = new Insets(0, 0, 5, 5);
		gbc_lblJdbcStartAccess.gridx = 1;
		gbc_lblJdbcStartAccess.gridy = 3;
		xmlFilePanel.add(lblJdbcStartAccess, gbc_lblJdbcStartAccess);
		
		databaseJDBC_Start = new JTextField();
		GridBagConstraints gbc_databaseJDBC_Start = new GridBagConstraints();
		gbc_databaseJDBC_Start.gridwidth = 5;
		gbc_databaseJDBC_Start.insets = new Insets(0, 0, 5, 5);
		gbc_databaseJDBC_Start.fill = GridBagConstraints.HORIZONTAL;
		gbc_databaseJDBC_Start.gridx = 2;
		gbc_databaseJDBC_Start.gridy = 3;
		xmlFilePanel.add(databaseJDBC_Start, gbc_databaseJDBC_Start);
		databaseJDBC_Start.setColumns(10);
		
		JLabel lblJdbcDbAccess = new JLabel("JDBC accès à la base");
		GridBagConstraints gbc_lblJdbcDbAccess = new GridBagConstraints();
		gbc_lblJdbcDbAccess.anchor = GridBagConstraints.EAST;
		gbc_lblJdbcDbAccess.insets = new Insets(0, 0, 5, 5);
		gbc_lblJdbcDbAccess.gridx = 1;
		gbc_lblJdbcDbAccess.gridy = 4;
		xmlFilePanel.add(lblJdbcDbAccess, gbc_lblJdbcDbAccess);
		
		databasePosition = new JTextField();
		GridBagConstraints gbc_databasePosition = new GridBagConstraints();
		gbc_databasePosition.gridwidth = 5;
		gbc_databasePosition.insets = new Insets(0, 0, 5, 5);
		gbc_databasePosition.fill = GridBagConstraints.HORIZONTAL;
		gbc_databasePosition.gridx = 2;
		gbc_databasePosition.gridy = 4;
		xmlFilePanel.add(databasePosition, gbc_databasePosition);
		databasePosition.setColumns(10);
		
		JButton btnFile = new JButton();
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = openFile(databasePosition.getText(), "H2 Database", "db");
				if (file != null) {
					databasePosition.setText(file.getAbsolutePath());
				}
			}
		});
		btnFile.setMargin(new Insets(2, 2, 2, 2));
		btnFile.setIcon(new ImageIcon(VitamConfigDialog.class
				.getResource(DatabaseGui.RESOURCES_IMG_CHECKFILES_PNG)));
		GridBagConstraints gbc_btnFile = new GridBagConstraints();
		gbc_btnFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnFile.gridx = 7;
		gbc_btnFile.gridy = 4;
		xmlFilePanel.add(btnFile, gbc_btnFile);
		
		JLabel lblpourHPeut = new JLabel("(pour H2: peut être le chemin d'accès)");
		GridBagConstraints gbc_lblpourHPeut = new GridBagConstraints();
		gbc_lblpourHPeut.anchor = GridBagConstraints.WEST;
		gbc_lblpourHPeut.gridwidth = 5;
		gbc_lblpourHPeut.insets = new Insets(0, 0, 5, 5);
		gbc_lblpourHPeut.gridx = 2;
		gbc_lblpourHPeut.gridy = 5;
		xmlFilePanel.add(lblpourHPeut, gbc_lblpourHPeut);
		
		JLabel lblJdbcDbOption = new JLabel("JDBC DB Option");
		GridBagConstraints gbc_lblJdbcDbOption = new GridBagConstraints();
		gbc_lblJdbcDbOption.anchor = GridBagConstraints.EAST;
		gbc_lblJdbcDbOption.insets = new Insets(0, 0, 5, 5);
		gbc_lblJdbcDbOption.gridx = 1;
		gbc_lblJdbcDbOption.gridy = 6;
		xmlFilePanel.add(lblJdbcDbOption, gbc_lblJdbcDbOption);
		
		databaseJDBC_Option = new JTextField();
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.gridwidth = 5;
		gbc_textField_4.insets = new Insets(0, 0, 5, 5);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 2;
		gbc_textField_4.gridy = 6;
		xmlFilePanel.add(databaseJDBC_Option, gbc_textField_4);
		databaseJDBC_Option.setColumns(10);
		
		JLabel lblJdbcAccess = new JLabel("JDBC User");
		GridBagConstraints gbc_lblJdbcAccess = new GridBagConstraints();
		gbc_lblJdbcAccess.anchor = GridBagConstraints.EAST;
		gbc_lblJdbcAccess.insets = new Insets(0, 0, 5, 5);
		gbc_lblJdbcAccess.gridx = 1;
		gbc_lblJdbcAccess.gridy = 7;
		xmlFilePanel.add(lblJdbcAccess, gbc_lblJdbcAccess);
		
		databaseUser = new JTextField();
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.gridwidth = 2;
		gbc_textField_5.insets = new Insets(0, 0, 5, 5);
		gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_5.gridx = 2;
		gbc_textField_5.gridy = 7;
		xmlFilePanel.add(databaseUser, gbc_textField_5);
		databaseUser.setColumns(10);
		
		JLabel lblReadUser = new JLabel("Read User");
		lblReadUser.setMinimumSize(new Dimension(150, 14));
		GridBagConstraints gbc_lblReadUser = new GridBagConstraints();
		gbc_lblReadUser.anchor = GridBagConstraints.EAST;
		gbc_lblReadUser.insets = new Insets(0, 0, 5, 5);
		gbc_lblReadUser.gridx = 4;
		gbc_lblReadUser.gridy = 7;
		xmlFilePanel.add(lblReadUser, gbc_lblReadUser);
		
		databaseReadUser = new JTextField();
		databaseReadUser.setMinimumSize(new Dimension(150, 20));
		databaseReadUser.setColumns(10);
		GridBagConstraints gbc_databaseReadUser = new GridBagConstraints();
		gbc_databaseReadUser.gridwidth = 2;
		gbc_databaseReadUser.insets = new Insets(0, 0, 5, 5);
		gbc_databaseReadUser.fill = GridBagConstraints.HORIZONTAL;
		gbc_databaseReadUser.gridx = 5;
		gbc_databaseReadUser.gridy = 7;
		xmlFilePanel.add(databaseReadUser, gbc_databaseReadUser);
		
		JLabel lblJdbcPassword = new JLabel("JDBC Password");
		GridBagConstraints gbc_lblJdbcPassword = new GridBagConstraints();
		gbc_lblJdbcPassword.anchor = GridBagConstraints.EAST;
		gbc_lblJdbcPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblJdbcPassword.gridx = 1;
		gbc_lblJdbcPassword.gridy = 8;
		xmlFilePanel.add(lblJdbcPassword, gbc_lblJdbcPassword);
		
		databasePassword = new JTextField();
		GridBagConstraints gbc_textField_6 = new GridBagConstraints();
		gbc_textField_6.gridwidth = 2;
		gbc_textField_6.insets = new Insets(0, 0, 5, 5);
		gbc_textField_6.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_6.gridx = 2;
		gbc_textField_6.gridy = 8;
		xmlFilePanel.add(databasePassword, gbc_textField_6);
		databasePassword.setColumns(10);
		
		JLabel lblReadPassword = new JLabel("Read Password");
		GridBagConstraints gbc_lblReadPassword = new GridBagConstraints();
		gbc_lblReadPassword.anchor = GridBagConstraints.EAST;
		gbc_lblReadPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblReadPassword.gridx = 4;
		gbc_lblReadPassword.gridy = 8;
		xmlFilePanel.add(lblReadPassword, gbc_lblReadPassword);
		
		databaseReadPassword = new JTextField();
		databaseReadPassword.setMinimumSize(new Dimension(150, 20));
		databaseReadPassword.setColumns(10);
		GridBagConstraints gbc_databaseReadPassword = new GridBagConstraints();
		gbc_databaseReadPassword.gridwidth = 2;
		gbc_databaseReadPassword.insets = new Insets(0, 0, 5, 5);
		gbc_databaseReadPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_databaseReadPassword.gridx = 5;
		gbc_databaseReadPassword.gridy = 8;
		xmlFilePanel.add(databaseReadPassword, gbc_databaseReadPassword);
		
		chckbxExportDataEn = new JCheckBox("Export Data en XML");
		chckbxExportDataEn.setMinimumSize(new Dimension(200, 23));
		GridBagConstraints gbc_chckbxExportDataEn = new GridBagConstraints();
		gbc_chckbxExportDataEn.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxExportDataEn.gridx = 2;
		gbc_chckbxExportDataEn.gridy = 9;
		xmlFilePanel.add(chckbxExportDataEn, gbc_chckbxExportDataEn);
	}
	private void typePanel(JTabbedPane tabbedPane) {
		JPanel panel = new JPanel();
		tabbedPane.addTab("Association des Types", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblInteger = new JLabel("Entier");
		GridBagConstraints gbc_lblInteger = new GridBagConstraints();
		gbc_lblInteger.anchor = GridBagConstraints.EAST;
		gbc_lblInteger.insets = new Insets(0, 0, 5, 5);
		gbc_lblInteger.gridx = 1;
		gbc_lblInteger.gridy = 1;
		panel.add(lblInteger, gbc_lblInteger);
		
		textInteger = new JTextField();
		GridBagConstraints gbc_textField;
		gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		panel.add(textInteger, gbc_textField);
		textInteger.setColumns(10);
		
		JLabel lblNombreVirgule = new JLabel("Nombre à virgule");
		GridBagConstraints gbc_lblNombreVirgule = new GridBagConstraints();
		gbc_lblNombreVirgule.anchor = GridBagConstraints.EAST;
		gbc_lblNombreVirgule.insets = new Insets(0, 0, 5, 5);
		gbc_lblNombreVirgule.gridx = 1;
		gbc_lblNombreVirgule.gridy = 2;
		panel.add(lblNombreVirgule, gbc_lblNombreVirgule);
		
		textFloat = new JTextField();
		GridBagConstraints gbc_textField_1;
		gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 2;
		panel.add(textFloat, gbc_textField_1);
		textFloat.setColumns(10);
		
		JLabel lblChanesDeCaractres = new JLabel("Chaînes de caractères");
		GridBagConstraints gbc_lblChanesDeCaractres = new GridBagConstraints();
		gbc_lblChanesDeCaractres.anchor = GridBagConstraints.EAST;
		gbc_lblChanesDeCaractres.insets = new Insets(0, 0, 5, 5);
		gbc_lblChanesDeCaractres.gridx = 1;
		gbc_lblChanesDeCaractres.gridy = 3;
		panel.add(lblChanesDeCaractres, gbc_lblChanesDeCaractres);
		
		textShortString = new JTextField();
		GridBagConstraints gbc_textShortString = new GridBagConstraints();
		gbc_textShortString.insets = new Insets(0, 0, 5, 0);
		gbc_textShortString.fill = GridBagConstraints.HORIZONTAL;
		gbc_textShortString.gridx = 2;
		gbc_textShortString.gridy = 3;
		panel.add(textShortString, gbc_textShortString);
		textShortString.setColumns(10);
		
		JLabel lblTitle = new JLabel("Vitam Configuration");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTitle, BorderLayout.NORTH);
		initValue();
	}

	public void initValue() {
		separator.setText(StaticValues.config.separator);
		commonTableName.setText(StaticValues.config.commonTableName);
		databaseType.setSelectedItem(StaticValues.config.databaseType);
		databaseJDBC_Start.setText(StaticValues.config.databaseJDBC_Start);
		databaseJDBC_Option.setText(StaticValues.config.databaseJDBC_Option);
		databasePosition.setText(StaticValues.config.databasePosition);
		databaseUser.setText(StaticValues.config.databaseUser);
		databasePassword.setText(StaticValues.config.databasePassword);
		databaseReadUser.setText(StaticValues.config.databaseReadUser);
		databaseReadPassword.setText(StaticValues.config.databaseReadPassword);
		chckbxExportDataEn.setSelected(StaticValues.config.exportFullData);
		String sint = "";
		String sfloat = "";
		String sstring = "";
		for (String name : ConstanceDataType.types.keySet()) {
			int type = ConstanceDataType.types.get(name);
			switch (type) {
				case Types.BIGINT:
					sint += name + ",";
					break;
				case Types.DOUBLE:
					sfloat += name + ",";
					break;
				case Types.VARCHAR:
					sstring += name + ",";
			}
		}
		sint = sint.substring(0, sint.length()-1);
		sfloat = sfloat.substring(0, sfloat.length()-1);
		sstring = sstring.substring(0, sstring.length()-1);
		textInteger.setText(sint);
		textFloat.setText(sfloat);
		textShortString.setText(sstring);
	}

	public void saveConfig() {
		if (!StaticValues.config.separator.equals(separator.getText())) {
			StaticValues.config.separator = separator.getText();
		}
		if (!StaticValues.config.commonTableName.equals(commonTableName.getText())) {
			StaticValues.config.commonTableName = commonTableName.getText();
		}
		if (!StaticValues.config.databaseType.equals(databaseType.getSelectedItem())) {
			StaticValues.config.databaseType = (String) databaseType.getSelectedItem();
		}
		if (!StaticValues.config.databaseJDBC_Start.equals(databaseJDBC_Start.getText())) {
			StaticValues.config.databaseJDBC_Start = databaseJDBC_Start.getText();
		}
		if (!StaticValues.config.databaseJDBC_Option.equals(databaseJDBC_Option.getText())) {
			StaticValues.config.databaseJDBC_Option = databaseJDBC_Option.getText();
		}
		if (!StaticValues.config.databasePosition.equals(databasePosition.getText())) {
			StaticValues.config.databasePosition = databasePosition.getText();
		}
		if (!StaticValues.config.databaseUser.equals(databaseUser.getText())) {
			StaticValues.config.databaseUser = databaseUser.getText();
		}
		if (!StaticValues.config.databasePassword.equals(databasePassword.getText())) {
			StaticValues.config.databasePassword = databasePassword.getText();
		}
		if (!StaticValues.config.databaseReadUser.equals(databaseReadUser.getText())) {
			StaticValues.config.databaseReadUser = databaseReadUser.getText();
		}
		if (!StaticValues.config.databaseReadPassword.equals(databaseReadPassword.getText())) {
			StaticValues.config.databaseReadPassword = databaseReadPassword.getText();
		}
		StaticValues.config.exportFullData = chckbxExportDataEn.isSelected();
		String [] sint = textInteger.getText().split(",");
		String [] sfloat = textFloat.getText().split(",");
		String [] sstring = textShortString.getText().split(",");
		if (sint.length > 0 && sfloat.length > 0 && sstring.length > 0) {
			ConstanceDataType.types.clear();
			for (String string : sint) {
				ConstanceDataType.types.put(string, Types.BIGINT);
			}
			for (String string : sfloat) {
				ConstanceDataType.types.put(string, Types.DOUBLE);
			}
			for (String string : sstring) {
				ConstanceDataType.types.put(string, Types.VARCHAR);
			}
		}
		if (chckbxUpdateConfigurationFile.isSelected()) {
			StaticValues.config.saveConfig();
		}
		if (fromMain) {

		} else {
			this.databaseGui.setEnabled(true);
			this.databaseGui.requestFocus();
			this.frame.setVisible(false);
		}
	}

	public void cancel() {
		if (fromMain) {
			this.frame.dispose();
			System.exit(0);
		} else {
			this.databaseGui.setEnabled(true);
			this.databaseGui.requestFocus();
			this.frame.setVisible(false);
		}
	}

	public File openFile(String currentValue, String text, String extension) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file).getParentFile();
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			if (StaticValues.config.technicalFile != null) {
				chooser = new JFileChooser(new File(StaticValues.config.technicalFile).getParentFile());
			} else {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			}
		}
		if (extension != null) {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		}
		chooser.setDialogTitle(text);
		if (extension == null) {
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				return chooser.getSelectedFile();
			}
		} else {
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				return chooser.getSelectedFile();
			}
		}
		return null;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Vitam Configuration");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Create and set up the content pane.
		fromMain = true;
		VitamConfigDialog newContentPane = new VitamConfigDialog(frame, null);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		StaticValues.initialize();
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
