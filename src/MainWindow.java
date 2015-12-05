import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.sf.mpxj.ProjectFile;

public class MainWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2857584114744701387L;
	protected static Color COLOR_rowSelectionBackground=new Color(210, 215, 255, 100);
	protected static Color COLOR_highlight100pct = new Color(204, 255, 204);
	protected static boolean highlight100pct = false;
	protected static Color COLOR_highlightDelayedTasks = new Color(255, 178, 178);
	protected static boolean highlightDelayedTasks = false;
	private JPanel contentPane;
	JTable table;
	private JFileChooser fc;
	ProjectFile projectFile;
	JTreeTable treeTable;
	JScrollPane scrollPane;
	private static File mppFile;
	final String APP_TITLE = "MPP Viewer (Beta)";
	private JCheckBoxMenuItem mntmCompletedTasks;
	private JCheckBoxMenuItem mntmDelayedTasks;
	private JToggleButton btnHighlight;
	private JToggleButton btnHighlightDelayedTasks;
	JLabel lblStatus;
	JProgressBar progressBar;
	private Find findDialog;
	private JButton btnFind;
	private JMenuItem mntmFind;
	private JMenuItem mntmSelectAll;
	private JMenuItem mntmCopy;
	private JMenuItem mntmCollapseAllTasks;
	private JMenuItem mntmExpandAllTasks;
	private JMenuItem mntmShowSubtasksCollapsed;
	private JMenu mnHighlight;
	private JMenuItem mntmExportToExcel;
	private JButton btnExportToExcel;
	private JButton btnExpandAll;
	private JButton btnCollapseAll;
	private JButton btnCollapsedSubtasks;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		if (args.length > 0)
			mppFile = new File(args[0]);

		

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow(mppFile);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow(File file) {
		UIManager.put("Tree.paintLines", Boolean.FALSE);
		UIManager.put("Tree.rendererFillBackground", false);
		//UIManager.put("TableHeader.cellBorder",BorderFactory.createLineBorder(SystemColor.darkGray));
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/res/icon32.png")));
		setTitle("MPP Viewer (beta)");
		fc = new JFileChooser(".");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpen.setMnemonic(KeyEvent.VK_O);
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openBtnClicked();
			}
		});
		mnFile.add(mntmOpen);
		
		mntmExportToExcel = new JMenuItem("Export to Excel");
		mntmExportToExcel.setEnabled(false);
		mntmExportToExcel.setMnemonic(KeyEvent.VK_E);
		mntmExportToExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportToExcel();
			}
		});
		mnFile.add(mntmExportToExcel);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		mnEdit.setMnemonic(KeyEvent.VK_E);
		menuBar.add(mnEdit);

		mntmCopy = new JMenuItem("Copy");
		mntmCopy.setEnabled(false);
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Robot robot=new Robot();
					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.keyPress(KeyEvent.VK_C);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					robot.keyRelease(KeyEvent.VK_C);
				} catch (AWTException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.setEnabled(false);
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				treeTable.selectAll();
			}
		});
		mnEdit.add(mntmSelectAll);
		mnEdit.add(mntmCopy);
		
		mntmFind = new JMenuItem("Find...");
		mntmFind.setEnabled(false);
		mntmFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(findDialog == null)
					findDialog=new Find(MainWindow.this);
				findDialog.setVisible(true);
			}
		});
		mnEdit.add(mntmFind);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		mntmExpandAllTasks = new JMenuItem("Expand All Tasks");
		mntmExpandAllTasks.setEnabled(false);
		mntmExpandAllTasks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (treeTable != null)
					treeTable.expandAll(true);
			}
		});
		mnView.add(mntmExpandAllTasks);

		mntmCollapseAllTasks = new JMenuItem("Collapse All Tasks");
		mntmCollapseAllTasks.setEnabled(false);
		mntmCollapseAllTasks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (treeTable != null)
					treeTable.expandAll(false);
			}
		});
		mnView.add(mntmCollapseAllTasks);

		mntmShowSubtasksCollapsed = new JMenuItem(
				"Show Collapsed Subtasks");
		mntmShowSubtasksCollapsed.setEnabled(false);
		mntmShowSubtasksCollapsed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (treeTable != null)
					treeTable.expandNextLevelSubTasks(false);
			}
		});
		mnView.add(mntmShowSubtasksCollapsed);

		mnHighlight = new JMenu("Highlight");
		mnHighlight.setEnabled(false);
		mnView.add(mnHighlight);

		mntmCompletedTasks = new JCheckBoxMenuItem(
				"Completed Tasks");
		mntmCompletedTasks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean isSelected= abstractButton.getModel().isSelected();
				btnHighlight.setSelected(isSelected);
				highlight100pct = isSelected;
				if (treeTable != null) {
					treeTable.validate();
					treeTable.repaint();
				}
			}
		});
		mnHighlight.add(mntmCompletedTasks);

		mntmDelayedTasks = new JCheckBoxMenuItem(
				"Late Tasks");
		mntmDelayedTasks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean isSelected = abstractButton.getModel().isSelected();
				btnHighlightDelayedTasks.setSelected(isSelected);
				highlightDelayedTasks = isSelected;
				if (treeTable != null) {
					treeTable.validate();
					treeTable.repaint();
				}
			}
		});
		mnHighlight.add(mntmDelayedTasks);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new About(MainWindow.this).setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);

		JMenuItem mntmFeedback = new JMenuItem("Feedback");
		mntmFeedback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FeedbackForm(MainWindow.this).setVisible(true);
			}
		});
		mnHelp.add(mntmFeedback);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setGridColor(new Color(220, 220, 220));
		table.setRowHeight(18);
		table.setEnabled(false);
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null, null, null, null, ""},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null},
			},
			new String[] {
				"ID", "Task Name", "Duration", "Start", "Finish", "% Complete", "Predecessors", "Resources"
			}
		));
		table.getColumnModel().getColumn(0).setPreferredWidth(26);
		table.getColumnModel().getColumn(1).setPreferredWidth(280);
		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getTableHeader().setDefaultRenderer(new MyTableHeaderRenderer());
		
		JToolBar toolBar_3 = new JToolBar();
		toolBar_3.setBorderPainted(false);
		toolBar_3.setFloatable(false);
		contentPane.add(toolBar_3, BorderLayout.NORTH);
		
				JToolBar toolBar = new JToolBar();
				toolBar.setBorder(null);
				toolBar.setBorderPainted(false);
				toolBar_3.add(toolBar);
				toolBar.setFloatable(false);
								
								JToolBar toolBar_4 = new JToolBar();
								toolBar_4.setBorderPainted(false);
								toolBar_4.setFloatable(false);
								toolBar.add(toolBar_4);
								
										JButton btnOpen = new JButton("");
										toolBar_4.add(btnOpen);
										btnOpen.setToolTipText("Open Project File");
										btnOpen.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent arg0) {
												openBtnClicked();
											}
										});
										btnOpen.setBorderPainted(false);
										btnOpen.setFocusPainted(false);
										btnOpen.setIcon(new ImageIcon(MainWindow.class
												.getResource("/res/Open-24.png")));
										
										btnExportToExcel = new JButton("");
										btnExportToExcel.setToolTipText("Export to Excel");
										btnExportToExcel.setEnabled(false);
										btnExportToExcel.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												exportToExcel();
											}
										});
										btnExportToExcel.setBorderPainted(false);
										btnExportToExcel.setFocusPainted(false);
										btnExportToExcel.setIcon(new ImageIcon(MainWindow.class.getResource("/res/page_white_excel.png")));
										toolBar_4.add(btnExportToExcel);
								
								btnFind = new JButton("");
								btnFind.setToolTipText("Find");
								btnFind.setEnabled(false);
								toolBar.add(btnFind);
								btnFind.setBorderPainted(false);
								btnFind.setFocusPainted(false);
								btnFind.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if(findDialog == null)
											findDialog=new Find(MainWindow.this);
										findDialog.setVisible(true);
									}
								});
								btnFind.setIcon(new ImageIcon(MainWindow.class.getResource("/res/Binoculars.gif")));
						
								JToolBar toolBar_1 = new JToolBar();
								toolBar_1.setBorderPainted(false);
								toolBar_1.setRollover(true);
								toolBar_1.setFloatable(false);
								toolBar.add(toolBar_1);
								
										btnExpandAll = new JButton("");
										btnExpandAll.setEnabled(false);
										btnExpandAll.setToolTipText("Expand All");
										toolBar_1.add(btnExpandAll);
										btnExpandAll.setFocusPainted(false);
										btnExpandAll.setBorderPainted(false);
										btnExpandAll.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												if (treeTable != null)
													treeTable.expandAll(true);
											}
										});
										btnExpandAll.setIcon(new ImageIcon(MainWindow.class
												.getResource("/res/ExpandAll.gif")));
										
												btnCollapseAll = new JButton("");
												btnCollapseAll.setEnabled(false);
												btnCollapseAll.addActionListener(new ActionListener() {
													public void actionPerformed(ActionEvent arg0) {
														if (treeTable != null)
															treeTable.expandAll(false);
													}
												});
												btnCollapseAll.setToolTipText("Collapse All");
												toolBar_1.add(btnCollapseAll);
												btnCollapseAll.setBorderPainted(false);
												btnCollapseAll.setFocusPainted(false);
												btnCollapseAll.setIcon(new ImageIcon(MainWindow.class
														.getResource("/res/CollapseAll.gif")));
												
														btnCollapsedSubtasks = new JButton("");
														btnCollapsedSubtasks.setEnabled(false);
														btnCollapsedSubtasks.setToolTipText("Show Collapsed Subtasks");
														toolBar_1.add(btnCollapsedSubtasks);
														btnCollapsedSubtasks.setFocusPainted(false);
														btnCollapsedSubtasks.addActionListener(new ActionListener() {
															public void actionPerformed(ActionEvent arg0) {
																if (treeTable != null)
																	treeTable.expandNextLevelSubTasks(false);
															}
														});
														btnCollapsedSubtasks.setBorderPainted(false);
														btnCollapsedSubtasks.setIcon(new ImageIcon(MainWindow.class
																.getResource("/res/PartialExpand.gif")));
														
																JToolBar toolBar_2 = new JToolBar();
																toolBar_2.setBorderPainted(false);
																toolBar_2.setRollover(true);
																toolBar_2.setFloatable(false);
																toolBar.add(toolBar_2);
																
																		btnHighlight = new JToggleButton("");
																		btnHighlight.setToolTipText("Highlight Completed Tasks");
																		btnHighlight.setBorderPainted(false);
																		toolBar_2.add(btnHighlight);
																		btnHighlight.setIcon(new ImageIcon(MainWindow.class
																				.getResource("/res/100percent.png")));
																		
																				btnHighlightDelayedTasks = new JToggleButton("");
																				btnHighlightDelayedTasks.setToolTipText("Highlight Late Tasks");
																				btnHighlightDelayedTasks.addActionListener(new ActionListener() {
																					public void actionPerformed(ActionEvent e) {
																						AbstractButton abstractButton = (AbstractButton) e.getSource();
																						boolean isSelected = abstractButton.getModel().isSelected();
																						mntmDelayedTasks.setSelected(isSelected);
																						highlightDelayedTasks = isSelected;
																						if (treeTable != null) {
																							treeTable.validate();
																							treeTable.repaint();
																						}
																					}
																				});
																				btnHighlightDelayedTasks.setBorderPainted(false);
																				btnHighlightDelayedTasks.setIcon(new ImageIcon(MainWindow.class
																						.getResource("/res/delayed.png")));
																				toolBar_2.add(btnHighlightDelayedTasks);
																				
																				Component horizontalGlue = Box.createHorizontalGlue();
																				toolBar_3.add(horizontalGlue);
																				
																				JButton btnFeedback = new JButton("Feedback");
																				btnFeedback.setToolTipText("Provide feedback");
																				btnFeedback.addActionListener(new ActionListener() {
																					public void actionPerformed(ActionEvent e) {
																						new FeedbackForm(MainWindow.this).setVisible(true);
																					}
																				});
																				btnFeedback.setFocusPainted(false);
																				btnFeedback.setBorderPainted(false);
																				btnFeedback.setForeground(new Color(30, 144, 255));
																				toolBar_3.add(btnFeedback);
																				
																				JPanel panel = new JPanel();
																				panel.setBorder(new EmptyBorder(2, 2, 2, 2));
																				panel.setPreferredSize(new Dimension(10, 20));
																				contentPane.add(panel, BorderLayout.SOUTH);
																				panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
																				
																				lblStatus = new JLabel("Use File -> Open to open an MPP file");
																				lblStatus.setBorder(null);
																				lblStatus.setFont(new Font("Dialog", Font.PLAIN, 12));
																				panel.add(lblStatus);
																				
																				progressBar = new JProgressBar();
																				progressBar.setMaximumSize(new Dimension(150, 14));
																				progressBar.setPreferredSize(new Dimension(100, 14));
																				progressBar.setVisible(false);
																				
																				Component horizontalStrut = Box.createHorizontalStrut(20);
																				panel.add(horizontalStrut);
																				panel.add(progressBar);
																				btnHighlight.addActionListener(new ActionListener() {
																					public void actionPerformed(ActionEvent e) {
																						AbstractButton abstractButton = (AbstractButton) e.getSource();
																						boolean isSelected= abstractButton.getModel().isSelected();
																						mntmCompletedTasks.setSelected(isSelected);
																						highlight100pct = isSelected;
																						if (treeTable != null) {
																							treeTable.validate();
																							treeTable.repaint();
																						}
																					}
																				});

		if (file != null)
			open(file);
		//else
			//open(new File("C:\\Users\\220222\\Downloads\\BONBT_VB_Conversion_Project_Plan_v1.2.mpp"));
	}

	protected void exportToExcel() {
		fc.resetChoosableFileFilters();
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Excel Workbook (*.xlsx)", "xlsx");
		fc.setFileFilter(filter);
		String mppFileName=mppFile.getName();
		String suggestedFilename="";
		if(mppFileName.endsWith(".mpp"))
			suggestedFilename = mppFileName.substring(0, mppFileName.length()-4)+".xlsx";
		else suggestedFilename=mppFileName;
		fc.setSelectedFile(new File(suggestedFilename));
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File outputFile=fc.getSelectedFile();
			if(!outputFile.getName().endsWith(".xlsx")){
				outputFile=new File(outputFile.getAbsolutePath()+".xlsx");
			}
			ExcelOutput task = new ExcelOutput(this,projectFile, outputFile);
			task.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress" == evt.getPropertyName()) {
						int progress = (Integer) evt.getNewValue();
						progressBar.setValue(progress);
					}

				}
			});
			progressBar.setVisible(true);
			progressBar.setValue(0);
			lblStatus.setText("Creating excel ouput "+outputFile.getName()+". Please wait...");
			task.execute();	    
		}
	}

	private void openBtnClicked() {
		fc.resetChoosableFileFilters();
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Project Files", "mpp");
		fc.setFileFilter(filter);
		fc.setSelectedFile(null);
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			mppFile = fc.getSelectedFile();
			open(mppFile);
		}
	}

	private void open(File file) {
		MPPFileReader task = new MPPFileReader(this, file);
		task.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress" == evt.getPropertyName()) {
					int progress = (Integer) evt.getNewValue();
					progressBar.setValue(progress);
				}

			}
		});
		progressBar.setVisible(true);
		progressBar.setValue(0);
		lblStatus.setText("Opening file "+file.getName()+". Please wait...");
		task.execute();
	}

	void enableControls(){
		mntmExportToExcel.setEnabled(true);
		mntmFind.setEnabled(true);
		btnFind.setEnabled(true);
		mntmSelectAll.setEnabled(true);
		mntmCopy.setEnabled(true);
		mntmExpandAllTasks.setEnabled(true);
		mntmCollapseAllTasks.setEnabled(true);
		mntmShowSubtasksCollapsed.setEnabled(true);
		mnHighlight.setEnabled(true);
		btnExportToExcel.setEnabled(true);
		btnExpandAll.setEnabled(true);
		btnCollapseAll.setEnabled(true);
		btnCollapsedSubtasks.setEnabled(true);
	}
	
	public static String fmt(double d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
		else
			return String.format("%s", d);
	}
	
	
	
	
	public static Color blend(Color c0, Color c1) {
	    double totalAlpha = c0.getAlpha() + c1.getAlpha();
	    double weight0 = c0.getAlpha() / totalAlpha;
	    double weight1 = c1.getAlpha() / totalAlpha;

	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = Math.max(c0.getAlpha(), c1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	  }
	

	
}
