import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.reader.ProjectReader;

public class MPPFileReader extends SwingWorker<Void, Void> {
	private final File file;
	private final MainWindow mainWindow;

	public MPPFileReader(MainWindow mw, File file) {
		super();
		mainWindow = mw;
		this.file = file;
	}

	@Override
	protected Void doInBackground() {
		ProjectReader reader = new MPPReader();
		try {
			mainWindow.projectFile = reader.read(file);
			setProgress(20);
			AbstractTreeTableModel treeTableModel = new ProjectTreeTableModel(createDataStructure());
			setProgress(60);
			mainWindow.treeTable = new JTreeTable(treeTableModel);
			mainWindow.treeTable.setShowGrid(true);
			mainWindow.treeTable.expandAll(true);

			// Create and set up the sortable header renderer by wrapping the existing one
			TableCellRenderer existingRenderer = mainWindow.treeTable.getTableHeader().getDefaultRenderer();
			final SortableHeaderRenderer sortableHeaderRenderer = new SortableHeaderRenderer(existingRenderer);
			mainWindow.treeTable.getTableHeader().setDefaultRenderer(sortableHeaderRenderer);

			// Add sorting by column header click with visual feedback
			javax.swing.table.JTableHeader header = mainWindow.treeTable.getTableHeader();
			header.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					int col = header.columnAtPoint(e.getPoint());
					if (col >= 0) {
						try {
							ProjectTreeTableModel model = (ProjectTreeTableModel) ((TreeTableModelAdapter) mainWindow.treeTable.getModel()).treeTableModel;

							// Debug info
							System.out.println("Sorting column: " + col);
							System.out.println("Current tree expansion state:");
							JTree tree = mainWindow.treeTable.getTree();
							System.out.println("Tree row count: " + tree.getRowCount());

							// Determine sort direction
							boolean ascending = true;
							if (model.getSortColumn() == col) {
								// If clicking on already sorted column, toggle direction
								ascending = !model.isAscending();
							}

							System.out.println("Sort direction: " + (ascending ? "ascending" : "descending"));

							// Sort the model while preserving expansion state
							model.sortByColumn(col, ascending, tree);
							// Always expand all nodes after sorting
							mainWindow.treeTable.expandAll(true);
							// Update the header renderer to show sort indicator
							sortableHeaderRenderer.setSortColumn(col, ascending);

							// Refresh UI
							mainWindow.treeTable.getTableHeader().repaint();
							mainWindow.treeTable.repaint();

							// Debug info after sort
							System.out.println("After sorting - Tree row count: " + tree.getRowCount());
						} catch (Exception ex) {
							System.err.println("Error during sorting: " + ex.getMessage());
							ex.printStackTrace();
						}
					}
				}
			});

			/* Autofit columns to contents */
			mainWindow.treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int column = 0; column < mainWindow.treeTable.getColumnCount(); column++) {
				TableColumn tableColumn = mainWindow.treeTable.getColumnModel().getColumn(column);
				int preferredWidth = tableColumn.getMinWidth();
				int maxWidth = tableColumn.getMaxWidth();

				// Consider header width for better column sizing
				TableCellRenderer headerRenderer = mainWindow.treeTable.getTableHeader().getDefaultRenderer();
				Component headerComp = headerRenderer.getTableCellRendererComponent(
						mainWindow.treeTable, tableColumn.getHeaderValue(), false, false, 0, column);
				int headerWidth = headerComp.getPreferredSize().width + mainWindow.treeTable.getIntercellSpacing().width + 10; // Add padding
				preferredWidth = Math.max(preferredWidth, headerWidth);

				for (int row = 0; row < mainWindow.treeTable.getRowCount(); row++) {
					TableCellRenderer cellRenderer = mainWindow.treeTable.getCellRenderer(row, column);
					Component c = mainWindow.treeTable.prepareRenderer(cellRenderer, row, column);
					int width = c.getPreferredSize().width + mainWindow.treeTable.getIntercellSpacing().width;
					preferredWidth = Math.max(preferredWidth, width);

					// We've exceeded the maximum width, no need to check
					// other rows
					if (preferredWidth >= maxWidth) {
						preferredWidth = maxWidth;
						break;
					}
				}

				// Add extra space for specific columns that need more room
				String columnName = tableColumn.getHeaderValue().toString();
				if (columnName.equals("Duration")) {
					preferredWidth += 27;
				} else if (columnName.equals("ID")) {
					preferredWidth += 10;
				}
				else if (columnName.equals("Predecessors")) {
					preferredWidth += 50;
				} else if (columnName.equals("% Complete")) {
					preferredWidth += 80;
				} else if (columnName.contains("Resource Count")) {
					preferredWidth += 190;
				}

				tableColumn.setPreferredWidth(preferredWidth);
			}

			/* set row height */
			mainWindow.treeTable.setRowHeight(25);
			((JTreeTableCellRenderer) mainWindow.treeTable.getCellRenderer(0, 1)).setRowHeight(25);

			setProgress(100);

		} catch (MPXJException e) {
			JOptionPane.showMessageDialog(mainWindow, "An error occurred while opening the file.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	private DataNode createDataStructure() {
		List<DataNode> rootNodes = new ArrayList<DataNode>();
		for (Task task : mainWindow.projectFile.getChildTasks()) {
			if (task.getName() == null)
				continue;
			rootNodes.add(new DataNode(task.getID(), task.getName(), task.getStart(), task.getFinish(),
					MainWindow.fmt(task.getDuration().getDuration()) + " days",
					MainWindow.fmt(task.getPercentageComplete().doubleValue()) + "%", "", 0, "",
					getChildNodes(task, 0)));

		}

		return new DataNode(0, "Root", null, null, null, null, null, 0, null, rootNodes);

	}

	private List<DataNode> getChildNodes(Task task, int lvl) {
		List<DataNode> dataNodes = new ArrayList<DataNode>();
		for (Task child : task.getChildTasks()) {
			if (child.getName() == null)
				continue;

			/* Get list of predecessors */
			List<Relation> predecessors = child.getPredecessors();

			/* Convert predecessor list to string of ids */
			String predecessorsStr = "";
			if (predecessors != null && !predecessors.isEmpty()) {
				for (Relation relation : predecessors) {
					predecessorsStr = predecessorsStr + relation.getTargetTask().getID() + ",";
				}
				predecessorsStr = predecessorsStr.substring(0, predecessorsStr.length() - 1);
			}

			List<ResourceAssignment> resourceAssignments = child.getResourceAssignments();
			int resourceCount = 0;

			/* Create Comma Seperated List of resource names */
			String resourceNames = "";
			if (resourceAssignments != null && !resourceAssignments.isEmpty()) {
				resourceCount = resourceAssignments.size();
				for (ResourceAssignment assignment : resourceAssignments) {
					Resource resource = assignment.getResource();
					resourceNames = resourceNames + (resource == null ? "" : ", "+resource.getName());
				}
				if (resourceNames.startsWith(", ")) resourceNames=resourceNames.substring(2);
			}

			String duration = MainWindow.fmt(child.getDuration().getDuration());
			if (duration.equals("1"))
				duration = duration + " day";
			else
				duration = duration + " days";

			// System.out.println(task.getID()+" : "+task.getName());
			/* add node to data node list */
			dataNodes.add(new DataNode(child.getID(), child.getName(), child.getStart(), child.getFinish(), duration,
					MainWindow.fmt(child.getPercentageComplete().doubleValue()) + "%", predecessorsStr, resourceCount,
					resourceNames, getChildNodes(child, lvl + 1)));

		}

		return dataNodes.isEmpty() ? null : dataNodes;
	}

	@Override
	protected void done() {
		super.done();
		mainWindow.scrollPane.remove(mainWindow.table);
		mainWindow.scrollPane.setViewportView(mainWindow.treeTable);
		//mainWindow.treeTable.setRowHeight(50);

		// Calculate the total width needed for the table
		int totalWidth = 0;
		for (int i = 0; i < mainWindow.treeTable.getColumnCount(); i++) {
			totalWidth += mainWindow.treeTable.getColumnModel().getColumn(i).getPreferredWidth();
		}
		// Add some padding for scrollbar and window borders
		totalWidth += 30;

		// Get current window dimensions
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int currentHeight = mainWindow.getHeight();

		// Set the new width, but don't exceed screen width
		int newWidth = Math.min(totalWidth, screenSize.width - 50);
		mainWindow.setSize(newWidth, currentHeight);

		// Center the window on screen
		mainWindow.setLocationRelativeTo(null);

		mainWindow.scrollPane.validate();
		mainWindow.scrollPane.repaint();
		mainWindow.setTitle(mainWindow.APP_TITLE + " - " + file.getName());
		mainWindow.progressBar.setVisible(false);
		mainWindow.lblStatus.setText("Done");
		mainWindow.enableControls();
		// System.out.println(mainWindow.treeTable.getModel().getValueAt(8, 1));
		// System.out.println(mainWindow.treeTable.getModel().getValueAt(9, 1));
		// System.out.println(mainWindow.treeTable.getModel().getValueAt(10,
		// 1));
	}
}
