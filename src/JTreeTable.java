import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class JTreeTable extends JTable {

	private static final long serialVersionUID = 1L;
	private JTreeTableCellRenderer tree;

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		JComponent jc = (JComponent) c;

		jc.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		boolean highlight100pct = false, highlightDelayedTasks=false, bgSet=false ;

		if (MainWindow.highlight100pct) {
			String status = (String) getModel().getValueAt(row, 5);
			if ("100%".equals(status)) {
				highlight100pct = true;
				jc.setBackground(MainWindow.COLOR_highlight100pct);
			}
		}

		if (MainWindow.highlightDelayedTasks) {
			Date date = (Date) getModel().getValueAt(row, 4);
			String status = (String) getModel().getValueAt(row, 5);
			if ((!status.equals("100%")) && date.before(new Date())) {
				jc.setBackground(MainWindow.COLOR_highlightDelayedTasks);
				highlightDelayedTasks = true;
			}
		}

		if (isRowSelected(row)) {
			// int top = (row > 0 && isRowSelected(row-1))?1:2;
			// int left = column == 0?2:0;
			// int bottom = (row < getRowCount()-1 && isRowSelected(row +
			// 1))?1:2;
			// int right = column == getColumnCount()-1?2:0;
			// jc.setBorder(BorderFactory.createMatteBorder(top, left, bottom,
			// right, this.getSelectionBackground()));

			if(highlight100pct)
				jc.setBackground(MainWindow.blend(MainWindow.COLOR_highlight100pct,
					MainWindow.COLOR_rowSelectionBackground));
			else if(highlightDelayedTasks)
				jc.setBackground(MainWindow.blend(MainWindow.COLOR_highlightDelayedTasks,
						MainWindow.COLOR_rowSelectionBackground));
			else jc.setBackground(MainWindow.COLOR_rowSelectionBackground);
			bgSet = true;
		}

		if (isCellSelected(row, column)) {
			jc.setBackground(getSelectionBackground());
			bgSet = true;
		}

		if (!bgSet && !highlight100pct && !highlightDelayedTasks)
			jc.setBackground(getBackground());

		return c;
	}

	public JTreeTable(AbstractTreeTableModel treeTableModel) {
		super();

		// Create a JTree.
		tree = new JTreeTableCellRenderer(this, treeTableModel);

		// Set the model.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Simultaneously selecting for Tree and Table.
		JTreeTableSelectionModel selectionModel = new JTreeTableSelectionModel();
		selectionModel.getListSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		selectionModel
				.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		// tree.setSelectionModel(selectionModel);
		setSelectionModel(selectionModel.getListSelectionModel()); // table
		// setDefaultRenderer(String.class, new StringRenderer());
		// setDefaultRenderer(Date.class, new DateRenderer());
		// setDefaultRenderer(Integer.class, new StringRenderer());
		// setDefaultRenderer(Double.class, new StringRenderer());

		// Set renderer to tree
		setDefaultRenderer(TreeTableModel.class, tree);
		// Editor for the TreeTable
		setDefaultEditor(TreeTableModel.class, new JTreeTableCellEditor(tree,
				this));

		/* Set renderer for Table Header */
		getTableHeader().setDefaultRenderer(new MyTableHeaderRenderer());
		getTableHeader().setReorderingAllowed(false);

		/* Show Grid Lines */
		setShowGrid(true);

		/* Set Grid Color */
		setGridColor(new Color(220, 220, 220));

		setIntercellSpacing(new Dimension(1, 1));

		setColumnSelectionAllowed(true);

	}

	/**
	 * Overridden to message super and forward the method to the tree. Since the
	 * tree is not actually in the component hieachy it will never receive this
	 * unless we forward it in this manner.
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		if (tree != null) {
			tree.updateUI();
		}
		// Use the tree's default foreground and background colors in the
		// table.
		LookAndFeel.installColorsAndFont(this, "Tree.background",
				"Tree.foreground", "Tree.font");
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to paint the
	 * renderers and editors and overriding setBounds() below is not the right
	 * thing to do for an editor. Returning -1 for the editing row in this case,
	 * ensures the editor is never painted.
	 */
	@Override
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1
				: editingRow;
	}

	/**
	 * Overridden to pass the new rowHeight to the tree.
	 */
	@Override
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		if (tree != null && tree.getRowHeight() != rowHeight) {
			tree.setRowHeight(getRowHeight());
		}
	}

	/**
	 * Returns the tree that is being shared between the model.
	 */
	public JTree getTree() {
		return tree;
	}

	public void expandAll(boolean expand) {
		if (expand)
			tree.expandAllNodes();
		else
			tree.collapseAllNodes();
	}

	public void expandNextLevelSubTasks(boolean expand) {
		TreePath path = tree.getSelectionPath();
		if (path != null)
			tree.expandNextLevelSubTasks(path, expand);
	}
	
	
}
/*
 * class StringRenderer extends DefaultTableCellRenderer { private static final
 * long serialVersionUID = 1L;
 * 
 * @Override public Component getTableCellRendererComponent(JTable table, Object
 * value, boolean isSelected, boolean hasFocus, int row, int col) {
 * super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
 * col); setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
 * 
 * boolean bgSet = false;
 * 
 * if (isSelected) { setBackground(table.getSelectionBackground()); bgSet =
 * true; } else {
 * 
 * if (MainWindow.highlight100pct) { String status = (String)
 * table.getModel().getValueAt(row, 5); if ("100%".equals(status)) { bgSet =
 * true; setBackground(MainWindow.COLOR_highlight100pct); } }
 * 
 * if (MainWindow.highlightDelayedTasks) { Date date = (Date)
 * table.getModel().getValueAt(row, 4); String status = (String)
 * table.getModel().getValueAt(row, 5); if ((!status.equals("100%")) &&
 * date.before(new Date())) {
 * setBackground(MainWindow.COLOR_highlightDelayedTasks); bgSet = true; } } } if
 * (!bgSet) setBackground(table.getBackground());
 * 
 * return this; } }
 * 
 * class DateRenderer extends DefaultTableCellRenderer { private static final
 * long serialVersionUID = 1L; DateFormat formatter;
 * 
 * public DateRenderer() { super(); }
 * 
 * @Override public void setValue(Object value) { if (formatter == null) {
 * formatter = DateFormat.getDateInstance(); } setText((value == null) ? "" :
 * formatter.format(value)); }
 * 
 * @Override public Component getTableCellRendererComponent(JTable table, Object
 * value, boolean isSelected, boolean hasFocus, int row, int col) {
 * super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
 * col); setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
 * 
 * boolean bgSet = false;
 * 
 * if (isSelected) { setBackground(table.getSelectionBackground()); bgSet =
 * true; } else { if (MainWindow.highlight100pct) { String status = (String)
 * table.getModel().getValueAt(row, 5); if ("100%".equals(status)) { bgSet =
 * true; setBackground(MainWindow.COLOR_highlight100pct); } }
 * 
 * if (MainWindow.highlightDelayedTasks) { Date date = (Date)
 * table.getModel().getValueAt(row, 4); String status = (String)
 * table.getModel().getValueAt(row, 5); if ((!status.equals("100%")) &&
 * date.before(new Date())) {
 * setBackground(MainWindow.COLOR_highlightDelayedTasks); bgSet = true; } } }
 * 
 * if (!bgSet) setBackground(table.getBackground());
 * 
 * return this; }
 * 
 * }
 */
