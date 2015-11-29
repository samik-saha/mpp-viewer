import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class JTreeTableCellRenderer extends JTree implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	/** The last row that has been rendered. */
	protected int visibleRow, visibleColumn;

	protected JTreeTable treeTable;

	public JTreeTableCellRenderer(JTreeTable treeTable, TreeModel model) {
		super(model);

		this.treeTable = treeTable;

		JTreeCellRenderer mr = new JTreeCellRenderer();
		setCellRenderer(mr);
		// Set the row height for the JTable
		// Must be called explicitly, because TreeTable yet
		// Zero when super (model) SetRowHeight calls!
		setRowHeight(getRowHeight());

		/* Do not display the root element */
		setRootVisible(false);

	}


	/**
	 * Tree and table must have the same height.
	 */
	public void setRowHeight(int rowHeight) {
		if (rowHeight > 0) {
			super.setRowHeight(rowHeight);
			if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
				treeTable.setRowHeight(getRowHeight());
			}
		}
	}

	/**
	 * Expand all nodes in the tree
	 */
	public void expandAllNodes() {
		int j = getRowCount();
		int i = 0;
		while (i < j) {
			expandRow(i);
			i += 1;
			j = getRowCount();
		}
	}

	/**
	 * Collapse all nodes in the tree
	 */
	public void collapseAllNodes() {
		int j = getRowCount();
		int i = 0;
		while (i < j) {
			collapseRow(j);
			j -= 1;
		}
	}

	/**
	 * Expand or collapse all subtasks of the given task
	 */
	public void expandNextLevelSubTasks(TreePath path, boolean expand) {
		DataNode node = (DataNode) path.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			List<DataNode> children = node.getChildren();
			ListIterator<DataNode> e = children.listIterator();
			while (e.hasNext()) {
				DataNode treeNode = (DataNode) e.next();
				if (expand) {
					expandPath(path.pathByAddingChild(treeNode));
				} else {
					collapsePath(path.pathByAddingChild(treeNode));
				}
			}
		}
	}

	/**
	 * Tree must have the same height have as Table.
	 */
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, treeTable.getHeight());
	}

	/**
	 * Provides for indentation of the folder.
	 */
	public void paint(Graphics g) {
		g.translate(0, -visibleRow * getRowHeight());

		super.paint(g);

		/* Show Grid lines on the tree */
		/*
		 * if (treeTable.getShowHorizontalLines() &&
		 * treeTable.getShowVerticalLines()) { Rectangle r =
		 * treeTable.getCellRect(visibleRow, visibleColumn, false);
		 * g.setColor(treeTable.getGridColor()); g.drawRect(r.x -
		 * treeTable.getCellRect(visibleRow, 0, false).width - 1, r.y - 1,
		 * r.width, r.height); }
		 */

	}

	/**
	 * Returns the renderer back with the appropriate background color.
	 */

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		boolean bgSet = false;

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			// setBorder(BorderFactory.createLineBorder(Color.CYAN,4));//not
			// working
			bgSet = true;
			setSelectionRow(row);
		} else {
			if (MainWindow.highlight100pct) {
				String status = (String) table.getModel().getValueAt(row, 5);
				if ("100%".equals(status)) {
					bgSet = true;
					setBackground(MainWindow.COLOR_highlight100pct);
				}
			}

			if (MainWindow.highlightDelayedTasks) {
				Date date = (Date) table.getModel().getValueAt(row, 4);
				String status = (String) table.getModel().getValueAt(row, 5);
				if ((!status.equals("100%")) && date.before(new Date())) {
					setBackground(MainWindow.COLOR_highlightDelayedTasks);
					bgSet = true;
				}
			}
		}
		if (!bgSet)
			setBackground(table.getBackground());

		visibleRow = row;
		visibleColumn = column;
		return this;
	}
}