package main.java;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
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
	private File file;
	private MainWindow mainWindow;

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

			/* Autofit columns to contents */
			mainWindow.treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			for (int column = 0; column < mainWindow.treeTable.getColumnCount(); column++) {
				TableColumn tableColumn = mainWindow.treeTable.getColumnModel().getColumn(column);
				int preferredWidth = tableColumn.getMinWidth();
				int maxWidth = tableColumn.getMaxWidth();

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
			if (predecessors != null && predecessors.isEmpty() == false) {
				for (Relation relation : predecessors) {
					predecessorsStr = predecessorsStr + relation.getTargetTask().getID() + ",";
				}
				predecessorsStr = predecessorsStr.substring(0, predecessorsStr.length() - 1);
			}

			List<ResourceAssignment> resourceAssignments = child.getResourceAssignments();
			int resourceCount = 0;

			/* Create Comma Seperated List of resource names */
			String resourceNames = "";
			if (resourceAssignments != null && resourceAssignments.isEmpty() == false) {
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
