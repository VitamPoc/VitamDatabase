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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.waarp.common.database.exception.WaarpDatabaseException;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;
import org.waarp.common.logging.WaarpSlf4JLoggerFactory;

import ch.qos.logback.classic.Level;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbVitam2Database;
import fr.gouv.culture.vitam.database.utils.ConfigLoader;
import fr.gouv.culture.vitam.database.utils.FileExtensionFilter;
import fr.gouv.culture.vitam.database.utils.StaticValues;
import fr.gouv.culture.vitam.database.utils.Version;
import fr.gouv.culture.vitam.database.utils.swing.Dom4jTreeBuilder;
import fr.gouv.culture.vitam.writer.XmlWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Main class for the GUI tool
 * 
 * @author "Frederic Bregier"
 * 
 */
public class DatabaseGui extends JFrame implements PropertyChangeListener {
	/**
	 * Internal Logger
	 */
	private static WaarpInternalLogger logger;

	protected static final String VITAM_PNG = "/resources/img/vitam.png";
	private static final String VITAM64_PNG = "/resources/img/vitam64.png";
	private static final String VITAM32_PNG = "/resources/img/vitam32.png";
	private static final String RESOURCES_IMG_VALID_PNG = "/resources/img/valid.png";
	private static final String RESOURCES_IMG_CLEAR_PNG = "/resources/img/edit-clear.png";
	private static final String RESOURCES_IMG_COPY_PNG = "/resources/img/copy.png";
	private static final String RESOURCES_IMG_EXIT_PNG = "/resources/img/exit.png";
	private static final String RESOURCES_IMG_HELP_PNG = "/resources/img/help.png";
	private static final String RESOURCES_IMG_CONFIG_PNG = "/resources/img/settings.png";
	protected static final String RESOURCES_IMG_CHECKFILES_PNG = "/resources/img/find-files.png";
	private static final String RESOURCES_IMG_VALIDATEXML_PNG = "/resources/img/xml.png";
	private static final String RESOURCES_IMG_CSV_PNG = "/resources/img/csv.png";
	private static final String RESOURCES_IMG_CSV2XML_GIF = "/resources/img/csv-2-xml.gif";
	private static final String RESOURCES_IMG_TABLE_PNG = "/resources/img/table.png";
	private static final String RESOURCES_IMG_DBSELECT_GIF = "/resources/img/dbselect.gif";
	private static final String RESOURCES_IMG_SQL_GIF = "/resources/img/sql.gif";
	private static final long serialVersionUID = -8010724089786663345L;

	protected File current_file;
	protected String tempTablePrefix;
	protected File tempFile;
	protected JTextPane texteOut;
	protected JTextPane texteErr;
	protected JToolBar toolBar;
	protected JToolBar toolBarWest;
	protected JMenuBar mb;
	protected List<JMenuItem> listMenuItem;
	protected List<JButton> listButton;
	protected List<JMenuItem> listAllMenuItem;
	protected List<JButton> listAllButton;
	protected static DatabaseGui databaseGui;
	protected static JProgressBar progressBar;
	public ConfigLoader config;
	protected JFrame frameDialog;
	protected VitamConfigDialog configDialog;
	protected JFrame frameDatabase;
	protected VitamDatabaseDialog vitamDatabase;
	protected JFrame frameSelect;
	protected VitamDatabaseSelectDialog vitamSelect;
	protected JFrame frameSql;
	protected VitamDatabaseFreeSelectDialog vitamSql;
	protected XMLWriter writer;
	protected ConstanceIdentifier identifier;
	protected DbSchema schema;
	
	public static void main(String[] args) {
		WaarpInternalLoggerFactory.setDefaultFactory(new WaarpSlf4JLoggerFactory(Level.WARN));
		logger = WaarpInternalLoggerFactory.getLogger(DatabaseGui.class);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				databaseGui = new DatabaseGui();
				databaseGui.setVisible(true);
			}
		});
	}

	/**
	 * Main GUI constructor
	 */
	public DatabaseGui() {
		StaticValues.initialize();
		List<Image> images = new ArrayList<Image>();
		images.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(VITAM64_PNG)));
		images.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(VITAM32_PNG)));
		setIconImages(images);
		try {
			writer = new XMLWriter(System.out, StaticValues.defaultOutputFormat);
		} catch (UnsupportedEncodingException e1) {
			logger.warn(StaticValues.LBL.error_writer.get() + e1.toString());
			quit();
			return;
		}
		listMenuItem = new ArrayList<JMenuItem>();
		listButton = new ArrayList<JButton>();
		listAllMenuItem = new ArrayList<JMenuItem>();
		listAllButton = new ArrayList<JButton>();
		getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.current_file = null;
		this.config = StaticValues.config;

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		getContentPane().setLayout(borderLayout);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Hashtable<String, String> listMenuItems = new Hashtable<String, String>();
		listMenuItems.put("menu", "menu.file/menu.edit/menu.tools/menu.help");
		listMenuItems.put("menu.file", "file.import/tool.csvimport/tool.xmlimport/-/file.quit");
		listMenuItems.put("menu.edit", "edit.copy/edit.clear");
		listMenuItems
				.put("menu.tools",
						"tool.export/tool.visual/tool.select/tool.sql");
		listMenuItems.put("menu.help", "help.about/help.config");

		setTitle(StaticValues.LBL.appName.get());
		setBackground(Color.white);

		/*
		 * Toolbars
		 */
		toolBar = new JToolBar("Toolbar");
		toolBar.setFloatable(false);
		toolBarWest = new JToolBar("Toolbar");
		toolBarWest.setFloatable(false);
		toolBarWest.setOrientation(JToolBar.VERTICAL);
		mb = new JMenuBar();
		createMenuBar(listMenuItems);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(toolBarWest, BorderLayout.WEST);
		setJMenuBar(mb);
		changeButtonMenu(false);

		Dimension screenSize = new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		int width = screenSize.width / 2;
		if (width < 600) {
			width = 600;
		}
		int height = (int) (screenSize.getHeight() / 2);
		if (height < 500) {
			height = 500;
		}
		screenSize.setSize(width, height);
		setSize(screenSize);

		texteOut = new JTextPane();
		texteOut.setEditable(false);
		texteErr = new JTextPane();
		texteErr.setEditable(false);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splitPane.setDividerSize(2);
		splitPane.setAutoscrolls(true);
		JScrollPane outPane = new JScrollPane(texteOut);
		outPane.setViewportBorder(UIManager.getBorder("TextPane.border"));
		JScrollPane errPane = new JScrollPane(texteErr);

		splitPane.setLeftComponent(outPane);
		splitPane.setRightComponent(errPane);
		splitPane.setDividerLocation((screenSize.height - 100) / 2);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Redirection of System.out and System.err
		ConsoleOutputStream cos = new ConsoleOutputStream(texteOut, null);
		System.setOut(new PrintStream(cos, true));
		//logger.warn("Syserr to real syserr");
		ConsoleOutputStream coserr = new ConsoleOutputStream(texteErr, Color.RED);
		System.setErr(new PrintStream(coserr, true));

		progressBar = new JProgressBar(0, 10);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, BorderLayout.PAGE_END);
		endProgressBar();

		frameDialog = new JFrame("Vitam Configuration");
		frameDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				databaseGui.setEnabled(true);
				databaseGui.requestFocus();
				frameDialog.setVisible(false);
			}
		});
		configDialog = new VitamConfigDialog(frameDialog, this);
		configDialog.setOpaque(true); // content panes must be opaque
		frameDialog.setContentPane(configDialog);
		frameDialog.pack();
		frameDialog.setVisible(false);

		frameDatabase = new JFrame("Vitam Database Viewer");
		frameDatabase.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				databaseGui.setEnabled(true);
				databaseGui.requestFocus();
				frameDatabase.setVisible(false);
			}
		});
		vitamDatabase = new VitamDatabaseDialog(frameDatabase, this);
		vitamDatabase.setOpaque(true); // content panes must be opaque
		frameDatabase.setContentPane(vitamDatabase);
		frameDatabase.pack();
		frameDatabase.setVisible(false);

		frameSelect = new JFrame("Vitam Database Select");
		frameSelect.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				databaseGui.setEnabled(true);
				databaseGui.requestFocus();
				frameSelect.setVisible(false);
			}
		});
		vitamSelect = new VitamDatabaseSelectDialog(frameSelect, this);
		vitamSelect.setOpaque(true); // content panes must be opaque
		frameSelect.setContentPane(vitamSelect);
		frameSelect.pack();
		frameSelect.setVisible(false);

		frameSql = new JFrame("Vitam Database Select");
		frameSql.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				databaseGui.setEnabled(true);
				databaseGui.requestFocus();
				frameSql.setVisible(false);
			}
		});
		vitamSql = new VitamDatabaseFreeSelectDialog(frameSql, this);
		vitamSql.setOpaque(true); // content panes must be opaque
		frameSql.setContentPane(vitamSql);
		frameSql.pack();
		frameSql.setVisible(false);
	}

	/**
	 * Quit application
	 */
	private void quit() {
		dispose();
		System.exit(0);
	}

	/**
	 * This method allows or disallows some button/menu items
	 * 
	 * @param valid
	 */
	protected final void changeButtonMenu(boolean valid) {
		for (JButton button : listButton) {
			button.setEnabled(valid);
		}
		for (JMenuItem menu : listMenuItem) {
			menu.setEnabled(valid);
		}
	}

	/**
	 * This method allows or disallows all button/menu items
	 * 
	 * @param valid
	 */
	protected final void changeAllButtonMenu(boolean valid) {
		for (JButton button : listAllButton) {
			button.setEnabled(valid);
		}
		for (JMenuItem menu : listAllMenuItem) {
			menu.setEnabled(valid);
		}
	}

	/**
	 * create MenuBar
	 * 
	 * @param hash
	 */
	protected final void createMenuBar(Hashtable<String, String> hash) {
		String liste = hash.get("menu");
		StringTokenizer menuKeys = new StringTokenizer(liste, "/");
		while (menuKeys.hasMoreTokens()) {
			String name = menuKeys.nextToken();
			JMenu m = createMenu(name, hash);
			if (m != null) {
				mb.add(m);
				if (name.equals("+")) {
					toolBarWest.addSeparator();
				} else {
					toolBar.addSeparator();
				}
			}
		}
	}

	/**
	 * Create one MenuItem
	 * 
	 * @param cmd
	 * @return the MenuItem
	 */
	protected final JMenuItem createMenuItem(String cmd) {
		JMenuItem mi = new JMenuItem(StaticValues.LABELS.get(cmd));
		return mi;
	}

	/**
	 * Create one MenuItem with an icon
	 * 
	 * @param cmd
	 * @param icon
	 * @return the MenuItem
	 */
	protected final JMenuItem createMenuItem(String cmd, Icon icon) {
		JMenuItem mi = createMenuItem(cmd);
		mi.setIcon(icon);
		return mi;
	}

	/**
	 * Create sub Menu
	 * 
	 * @param key
	 * @param hash
	 * @return the sub Menu
	 */
	private final JMenu createMenu(String key, Hashtable<String, String> hash) {
		final JMenu menu = new JMenu(StaticValues.LABELS.get(key));
		String s = (String) hash.get(key);
		StringTokenizer menuItems = new StringTokenizer(s, "/");

		while (menuItems.hasMoreTokens()) {
			String name = menuItems.nextToken();
			String newname = name.replaceFirst("\\.", "_");
			try {
				final StaticValues.LBL label = StaticValues.LBL.valueOf(newname);
				JMenuItem mi = null;
				JButton button = null;
				ImageIcon img = null;
				ActionListener actionListener = null;
				switch (label) {
					case edit_clear:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CLEAR_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								texteOut.setText("");
								texteErr.setText("");
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case edit_copy:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_COPY_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								texteOut.copy();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case file_import:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CHECKFILES_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File file = openTxtFile();
								if (file != null) {
									initProgressBar(0);
									String prefix = config.commonTableName;
									if (prefix == null || prefix.trim().length() == 0) {
										prefix = file.getName();
									}
									int pos = prefix.indexOf('.');
									if (pos > 0) {
										prefix = prefix.substring(0, pos);
									}
									String s = (String)JOptionPane.showInputDialog(
											databaseGui,
						                    "Table prefix",
						                    "Parametre Database",
						                    JOptionPane.PLAIN_MESSAGE,
						                    null,
						                    null,
						                    prefix);
									if (s == null || s.length() == 0) {
										s = "AN_";
									}
									tempTablePrefix = s;
									config.technicalFile = file.getAbsolutePath();
									current_file = file;
									runCommand(label);
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tool_export:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CSV2XML_GIF));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tool_visual:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_TABLE_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								showDatabase();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tool_select:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DBSELECT_GIF));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								showSelect();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tool_sql:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_SQL_GIF));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								showSql();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tool_xmlimport:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_VALIDATEXML_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File file = openXmlFile();
								if (file != null) {
									initProgressBar(3);
									String prefix = config.commonTableName;
									if (prefix == null || prefix.trim().length() == 0) {
										prefix = file.getName();
									}
									int pos = prefix.indexOf('.');
									if (pos > 0) {
										prefix = prefix.substring(0, pos);
									}
									String s = (String)JOptionPane.showInputDialog(
											databaseGui,
						                    "Table prefix",
						                    "Parametre Database",
						                    JOptionPane.PLAIN_MESSAGE,
						                    null,
						                    null,
						                    prefix);
									if (s == null || s.length() == 0) {
										s = "AN_";
									}
									tempTablePrefix = s;
									config.technicalFile = file.getAbsolutePath();
									current_file = file;
									runCommand(label);
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tool_csvimport:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CSV_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File file = openTxtFile();
								if (file != null) {
									initProgressBar(6);
									String prefix = config.commonTableName;
									if (prefix == null || prefix.trim().length() == 0) {
										prefix = file.getName();
									}
									int pos = prefix.indexOf('.');
									if (pos > 0) {
										prefix = prefix.substring(0, pos);
									}
									String s = (String)JOptionPane.showInputDialog(
											databaseGui,
						                    "Table prefix",
						                    "Parametre Database",
						                    JOptionPane.PLAIN_MESSAGE,
						                    null,
						                    null,
						                    prefix);
									if (s == null || s.length() == 0) {
										s = "AN_";
									}
									tempTablePrefix = s;
									config.technicalFile = file.getAbsolutePath();
									current_file = file;
									runCommand(label);
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case file_quit:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_EXIT_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								quit();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case help_config:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CONFIG_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								config();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case help_about:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_HELP_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								about();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					default:
						set_ExtraMenu(menu, name);
						break;
				}
			} catch (Exception e) {
				if (name.equals("-")) {
					toolBar.addSeparator();
					menu.addSeparator();
				} else if (name.equals("+")) {
					toolBarWest.addSeparator();
					menu.addSeparator();
				}

			}
		}
		return menu;
	}

	/**
	 * Method to enable other menu creation if extended
	 * 
	 * @param menu
	 * @param name
	 */
	protected void set_ExtraMenu(JMenu menu, String name) {
	}

	/**
	 * Init the progressBar from config.nbDocument
	 */
	protected void initProgressBar(int step) {
		if (step == 0) {
			step = config.step;
		}
		if (step > 0) {
			progressBar.setValue(0);
			progressBar.setMaximum(step);
			progressBar.setVisible(true);
		}
	}

	/**
	 * Init the progressBar in indeterminate mode
	 */
	protected void initIndeterminateProgressBar() {
		progressBar.setIndeterminate(true);
		progressBar.setVisible(true);
	}

	/**
	 * Finalize the progressBar
	 */
	protected void endProgressBar() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
	}

	/**
	 * Ask for the configuration window
	 * 
	 */
	private void config() {
		this.setEnabled(false);
		frameDialog.setVisible(true);
		configDialog.initValue();
	}

	/**
	 * 
	 * @param currentValue
	 *            current path where the parent path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @return the chosen file or null if cancel
	 */
	private File openFile(String currentValue, String text, String extension) {
		return openFile(currentValue, text, extension, true);
	}

	/**
	 * 
	 * @param currentValue
	 *            current path where the exact path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @param parent
	 *            if true currentValue will be changed to parent, else no change
	 * @return the chosen file or null if cancel
	 */
	private File openFile(String currentValue, String text, String extension, boolean parent) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file);
				if (parent) {
					ffile = ffile.getParentFile();
				}
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			if (current_file != null) {
			chooser = new JFileChooser(current_file.getParentFile());
			} else {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			}
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		}
		chooser.setDialogTitle(text);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * 
	 * @param path
	 * @return a path if any
	 */
	private String getPreviousPath(String path) {
		if (path == null) {
			if (config.technicalFile != null) {
				return config.technicalFile;
			}
		}
		return path;
	}

	/**
	 * Open a Structure Definition file
	 * 
	 * @return the file
	 */
	private File openTxtFile() {
		String path = getPreviousPath(config.technicalFile);
		File file = openFile(path, StaticValues.LBL.file_import.get(), "txt");
		if (file != null && file.isFile()) {
			return file;
		}
		return null;
	}

	/**
	 * Open a XML Structure Definition file
	 * 
	 * @return the file
	 */
	private File openXmlFile() {
		String path = getPreviousPath(config.technicalFile);
		File file = openFile(path, StaticValues.LBL.file_import.get(), "xml");
		if (file != null && file.isFile()) {
			return file;
		}
		return null;
	}

	/**
	 * About and license
	 */
	public void about() {
		StringBuffer content = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream(StaticValues.RESOURCES_LICENSE_TXT)));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		}

		final JPanel panel = new JPanel(new BorderLayout());
		String credits = "<html><p><center>Copyright (c) 2012 Ministere de la Culture et de la Communication<br>"
				+
				"Sous-Direction du Systeme d'Information<br>Projet Vitam</center></p><p></p><p><center>Version: "
				+ Version.ID
				+
				"</center></p><p></p><p>Contributeurs: <br><i>Frederic Bregier</i></p><p></p>"
				+
				"<p>Site web: <a href='http://www.archivesnationales.culture.gouv.fr/'>http://www.archivesnationales.culture.gouv.fr/</a></p></html>";
		panel.add(new JLabel(credits), BorderLayout.NORTH);
		JTextArea textArea = new JTextArea(content.toString());
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(550, 500));
		// TIP: Make the JOptionPane resizable using the HierarchyListener
        panel.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog)window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
		JOptionPane.showConfirmDialog(this, panel,
				StaticValues.LBL.label_about.get(),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	private void showDatabase() {
		DbVitam2Database database = null;
		String jdbcPosition = config.databasePosition;
		if (config.databaseType.equals(StaticValues.TYPEH2)) {
			int pos = jdbcPosition.indexOf(".h2.db");
			if (pos > 0) {
				jdbcPosition = jdbcPosition.substring(0, pos);
			}
		}
		String jdbc = config.databaseJDBC_Start+
				jdbcPosition+
				config.databaseJDBC_Option;
		System.out.println("Connect: " + jdbc);
		try {
			database = new DbVitam2Database(config.databaseType, 
						jdbc, 
						config.databaseUser, 
						config.databasePassword);
			vitamDatabase.setDbVitam2Database(database, schema);
			this.setEnabled(false);
			frameDatabase.setVisible(true);
		} catch (WaarpDatabaseNoConnectionException e) {
		}
	}

	private void showSelect() {
		DbVitam2Database database = null;
		String jdbcPosition = config.databasePosition;
		if (config.databaseType.equals(StaticValues.TYPEH2)) {
			int pos = jdbcPosition.indexOf(".h2.db");
			if (pos > 0) {
				jdbcPosition = jdbcPosition.substring(0, pos);
			}
		}
		String jdbc = config.databaseJDBC_Start+
				jdbcPosition+
				config.databaseJDBC_Option;
		System.out.println("Connect: " + jdbc);
		try {
			database = new DbVitam2Database(config.databaseType, 
						jdbc, 
						config.databaseUser, 
						config.databasePassword);
			vitamSelect.setDbVitam2Database(database, schema);
			this.setEnabled(false);
			frameSelect.setVisible(true);
		} catch (WaarpDatabaseNoConnectionException e) {
		}
	}

	private void showSql() {
		DbVitam2Database database = null;
		String jdbcPosition = config.databasePosition;
		if (config.databaseType.equals(StaticValues.TYPEH2)) {
			int pos = jdbcPosition.indexOf(".h2.db");
			if (pos > 0) {
				jdbcPosition = jdbcPosition.substring(0, pos);
			}
		}
		String jdbc = config.databaseJDBC_Start+
				jdbcPosition+
				config.databaseJDBC_Option;
		System.out.println("Connect: " + jdbc);
		try {
			database = new DbVitam2Database(config.databaseType, 
						jdbc, 
						config.databaseUser, 
						config.databasePassword);
			vitamSql.setDbVitam2Database(database, schema);
			this.setEnabled(false);
			frameSql.setVisible(true);
		} catch (WaarpDatabaseNoConnectionException e) {
		}
	}
	
	/**
	 * Import one File
	 * 
	 * @param task
	 */
	private void importFile(RunnerLongTask task) {
		identifier = new ConstanceIdentifier(current_file, 
				config.separator, tempTablePrefix);
		schema = null;
		DbVitam2Database database = null;
		try {
			int currank = 0;
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			String jdbcPosition = config.databasePosition;
			if (config.databaseType.equals(StaticValues.TYPEH2)) {
				int pos = jdbcPosition.indexOf(".h2.db");
				if (pos > 0) {
					jdbcPosition = jdbcPosition.substring(0, pos);
				}
			}
			String jdbc = config.databaseJDBC_Start+
					jdbcPosition+
					config.databaseJDBC_Option;
			System.out.println("Connect: " + jdbc);
			database = new DbVitam2Database(config.databaseType, 
						jdbc, 
						config.databaseUser, 
						config.databasePassword);
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			identifier.loadTechnicalDescription();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			identifier.printStructure();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			schema = identifier.getSimpleSchema();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			schema.createBuildOrder();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			schema.printOrder();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			database.dropDatabases(schema);
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			database.createDatabases(schema);
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			database.fillDatabases(schema, identifier);
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			database.close();
			texteOut.insertIcon(new ImageIcon(getClass().getResource(
					RESOURCES_IMG_VALID_PNG)));
			System.out
					.println(StaticValues.LBL.action_import.get());
			return;
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} finally {
			if (database != null) {
				database.close();
			}
		}
	}

	/**
	 * Import Only Structure
	 * 
	 * @param task
	 */
	private void importStructureFile(RunnerLongTask task) {
		identifier = new ConstanceIdentifier(current_file, 
				config.separator, tempTablePrefix);
		schema = null;
		try {
			int currank = 0;
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			identifier.loadTechnicalDescription();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			identifier.printStructure();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			schema = identifier.getSimpleSchema();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			schema.createBuildOrder();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			schema.printOrder();
			if (task != null) {
				currank++;
				task.setProgressExternal(currank);
			}
			texteOut.insertIcon(new ImageIcon(getClass().getResource(
					RESOURCES_IMG_VALID_PNG)));
			System.out
					.println(StaticValues.LBL.action_import.get());
			return;
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		}
	}
	/**
	 * Import Only Structure from XML
	 * 
	 * @param task
	 */
	private void importSchemaFile(RunnerLongTask task) {
		int currank = 0;
		SAXReader reader = new SAXReader();
		if (task != null) {
			currank++;
			task.setProgressExternal(currank);
		}
        Document document;
		try {
			document = reader.read(current_file);
		} catch (DocumentException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return;
		}
		if (task != null) {
			currank++;
			task.setProgressExternal(currank);
		}
		schema = DbSchema.buildFromXml(current_file, document.getRootElement());
		identifier = schema.identifier;
		if (task != null) {
			currank++;
			task.setProgressExternal(currank);
		}
		texteOut.insertIcon(new ImageIcon(getClass().getResource(
				RESOURCES_IMG_VALID_PNG)));
		System.out
				.println(StaticValues.LBL.action_import.get());
	}

	private void saveSchemaToXml(RunnerLongTask task) {
		if (identifier == null) {
			logger.warn("Faire l'import avant");
			return;
		}
		File file = null;
		if (config.exportFullData) {
			file = new File(current_file.getParentFile(), current_file.getName()+"_export_data.xml");	
		} else {
			file = new File(current_file.getParentFile(), current_file.getName()+"_export.xml");	
		}
		System.out.println("Fichier XML: " + file.getAbsolutePath());
		XmlWriter writer = new XmlWriter(file, "export");
		if (! config.exportFullData) {
			if (schema != null) {
				try {
					writer.add(schema);
					writer.write();
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out
							.println(StaticValues.LBL.action_export.get());
				} catch (IOException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
				}
			} else {
				try {
					writer.add(identifier.getSimpleSchema());
					writer.write();
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out
							.println(StaticValues.LBL.action_export.get());
				} catch (WaarpDatabaseSqlException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
				} catch (IOException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
				}
			}
		} else {
			if (schema != null) {
				try {
					writer.add(schema);
					writer.write();
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out
							.println(StaticValues.LBL.action_export.get());
				} catch (IOException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
				}
			} else {
				try {
					writer.add(identifier.getFullSchema());
					writer.write();
					texteOut.insertIcon(new ImageIcon(getClass().getResource(
							RESOURCES_IMG_VALID_PNG)));
					System.out
							.println(StaticValues.LBL.action_export.get());
				} catch (WaarpDatabaseSqlException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
				} catch (IOException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
				}
			}
		}
		
	}

	/**
	 * Run a defined command according to label through RunnerLongTask
	 * 
	 * @param label
	 */
	protected final void runCommand(StaticValues.LBL label) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		changeAllButtonMenu(false);
		RunnerLongTask task = new RunnerLongTask(label, this);
		task.addPropertyChangeListener(this);
		task.execute();
	}

	/**
	 * Used to extend model
	 * 
	 * @param label
	 */
	protected void runSwingWorkerExtraCommand(StaticValues.LBL label) {
		logger.warn("SWExtraCommand: " + label.label);
	}

	/**
	 * Used to extend model
	 * 
	 * @param label
	 */
	protected void runEndExtraCommand(StaticValues.LBL label) {
		logger.warn("ExtraCommand: " + label.label);
	}

	/**
	 * Class to allow asynchronous execution of tasks
	 * 
	 * @author "Frederic Bregier"
	 * 
	 */
	public static class RunnerLongTask extends SwingWorker<Object, Void> {
		StaticValues.LBL label;
		DatabaseGui gui;

		private RunnerLongTask(StaticValues.LBL label, DatabaseGui gui) {
			super();
			this.label = label;
			this.gui = gui;
		}

		@Override
		protected void done() {
			boolean showSchema = false;
			try {
				get();
				gui.setCursor(null); // turn off the wait cursor
				switch (label) {
					case file_import:
					case tool_csvimport:
					case tool_xmlimport:
						showSchema = true;
					case tool_export:
						break;
					default:
						this.gui.runEndExtraCommand(label);
						break;
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} finally {
				gui.endProgressBar();
				gui.changeAllButtonMenu(true);
				if (gui.current_file == null) {
					gui.changeButtonMenu(false);
				}
			}
			if (gui.schema != null && showSchema) {
				Element eschema = gui.schema.getElement(true, false);
				Dom4jTreeBuilder treeBuilder = new Dom4jTreeBuilder(eschema);
				DefaultMutableTreeNode top = treeBuilder.parseXml();
				final JTree tree = new JTree(top);
				JScrollPane scrollPane = new JScrollPane(tree);
				final JPanel panel = new JPanel(new BorderLayout());
				panel.add(scrollPane, BorderLayout.CENTER);
				panel.setPreferredSize(new Dimension(500, 500));
				// TIP: Make the JOptionPane resizable using the HierarchyListener
		        panel.addHierarchyListener(new HierarchyListener() {
		            public void hierarchyChanged(HierarchyEvent e) {
		                Window window = SwingUtilities.getWindowAncestor(panel);
		                if (window instanceof Dialog) {
		                    Dialog dialog = (Dialog)window;
		                    if (!dialog.isResizable()) {
		                        dialog.setResizable(true);
		                    }
		                }
		            }
		        });
		        JButton btnExpand = new JButton("Tout Ouvrir");
				btnExpand.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < tree.getRowCount(); i++) {
					         tree.expandRow(i);
						}
					}
				});
				panel.add(btnExpand, BorderLayout.NORTH);
		        JButton btnCollapse = new JButton("Tout Fermer");
		        btnCollapse.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < tree.getRowCount(); i++) {
					         tree.collapseRow(i);
						}
					}
				});
				panel.add(btnCollapse, BorderLayout.SOUTH);
				JOptionPane.showConfirmDialog(gui, panel,
						"Xml Result",
						JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
			}
		}

		public void setProgressExternal(final int progress) {
			setProgress(progress);
		}

		@Override
		protected Object doInBackground() throws Exception {
			setProgress(0);
			switch (label) {
				case file_import:
					gui.importFile(this);
					break;
				case tool_export:
					gui.saveSchemaToXml(this);
					break;
				case tool_csvimport:
					gui.importStructureFile(this);
					break;
				case tool_xmlimport:
					gui.importSchemaFile(this);
					break;
				default:
					gui.runSwingWorkerExtraCommand(label);
					break;
			}
			return true;
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
}
