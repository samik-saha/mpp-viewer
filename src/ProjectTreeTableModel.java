import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JTree;

public class ProjectTreeTableModel extends AbstractTreeTableModel {
    // Column name.
    static protected String[] columnNames = { 
    	"ID",
    	"Task Name",
    	"Duration", 
    	"Start", 
    	"Finish", 
    	"% Complete", 
    	"Predecessors", 
    	"Resource Count",
    	"Resource Names"};

    // Column types.
    static protected Class<?>[] columnTypes = { 
    	Integer.class, 
    	TreeTableModel.class, 
    	String.class, 
    	Date.class, 
    	Date.class,
    	String.class,
    	String.class,
    	Integer.class,
    	String.class};

    // Keep track of sort state
    private int sortColumn = -1;
    private boolean ascending = true;

    public  ProjectTreeTableModel (DataNode rootNode) {
        super(rootNode);
        root = rootNode;
    }

    public Object getChild(Object parent, int index) {
        return ((DataNode) parent).getChildren().get(index);
    }


    public int getChildCount(Object parent) {
        return ((DataNode) parent).getChildren().size();
    }


    public  int  getColumnCount () {
        return columnNames.length;
    }


    public String getColumnName(int column) {
        return columnNames[column];
    }


    public  Class <?> getColumnClass ( int  column) {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        switch (column) {
        	case 0:
        		return ((DataNode) node).getID();
            case 1:
                return ((DataNode) node).getTask();
            case 2:
            	return ((DataNode) node).getDuration();
            case 3:
            	return ((DataNode) node).getStartDate();
            case 4:
            	return ((DataNode) node).getFinishDate();
            case 5:
            	return ((DataNode) node).getPercentageComplete();
            case 6:
            	return ((DataNode) node).getPredecessors();
            case 7:
            	return ((DataNode) node).getResources();
            case 8:
            	return ((DataNode) node).getResourceNames();
            default:
                break;
        }
        return  null;
    }

    public boolean isCellEditable(Object node, int column) {
    	/* make all columns non-editable except the task column */
        return true; // Important to activate TreeExpandListener
    }

    public void setValueAt(Object aValue, Object node, int column) {
    }

    /**
     * Sort the project data by a specific column
     * @param column The column index to sort by
     * @param ascending If true, sort in ascending order; otherwise descending
     * @param tree The JTree to maintain expansion state for
     */
    public void sortByColumn(int column, boolean ascending, JTree tree) {
        this.sortColumn = column;
        this.ascending = ascending;

        // Save expanded rows before sorting
        int[] expandedRows = null;
        if (tree != null) {
            // Save which rows are expanded
            int rowCount = tree.getRowCount();
            java.util.List<Integer> expandedRowsList = new java.util.ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                if (tree.isExpanded(i)) {
                    expandedRowsList.add(i);
                }
            }
            expandedRows = expandedRowsList.stream().mapToInt(i -> i).toArray();
        }

        // Sort the nodes
        sortNodeChildren(root, column, ascending);

        // Fire model changed event
        fireTreeStructureChanged(this, new Object[] { root }, null, null);

        // Restore expanded rows after sorting
        if (tree != null && expandedRows != null) {
            for (int row : expandedRows) {
                if (row < tree.getRowCount()) {
                    tree.expandRow(row);
                }
            }
        }
    }


    /**
     * Get the current sort column index
     * @return The column index, or -1 if not sorted
     */
    public int getSortColumn() {
        return sortColumn;
    }

    /**
     * Get the current sort direction
     * @return true if ascending, false if descending
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * Recursively sort children of a node based on a column value
     */
    private void sortNodeChildren(Object node, int column, boolean ascending) {
        DataNode dataNode = (DataNode) node;
        if (dataNode.getChildren() == null || dataNode.getChildren().isEmpty()) {
            return;
        }

        // Create an appropriate comparator based on column type
        Comparator<DataNode> comparator = createComparatorForColumn(column);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        // Sort this node's children
        Collections.sort(dataNode.getChildren(), comparator);

        // Recursively sort all children
        for (DataNode child : dataNode.getChildren()) {
            sortNodeChildren(child, column, ascending);
        }
    }

    /**
     * Create a comparator appropriate for the data type of the column
     */
    private Comparator<DataNode> createComparatorForColumn(int column) {
        switch (column) {
            case 0: // ID (Integer)
                return Comparator.comparing(node -> node.getID());

            case 1: // Task name (String)
                return Comparator.comparing(node -> {
                    String val = node.getTask();
                    return val == null ? "" : val;
                }, String.CASE_INSENSITIVE_ORDER);

            case 2: // Duration (String)
                return Comparator.comparing(node -> {
                    String val = node.getDuration();
                    return val == null ? "" : val;
                });

            case 3: // Start date
                return Comparator.comparing(node -> {
                    Date val = node.getStartDate();
                    return val == null ? new Date(0) : val;
                });

            case 4: // Finish date
                return Comparator.comparing(node -> {
                    Date val = node.getFinishDate();
                    return val == null ? new Date(0) : val;
                });

            case 5: // % Complete (String)
                return Comparator.comparing(node -> {
                    String val = node.getPercentageComplete();
                    if (val == null) return 0.0;
                    // Parse the percentage value
                    try {
                        return Double.parseDouble(val.replace("%", ""));
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                });

            case 6: // Predecessors (String)
                return Comparator.comparing(node -> {
                    String val = node.getPredecessors();
                    return val == null ? "" : val;
                });

            case 7: // Resource count (Integer)
                return Comparator.comparing(node -> node.getResources());

            case 8: // Resource names (String)
                return Comparator.comparing(node -> {
                    String val = node.getResourceNames();
                    return val == null ? "" : val;
                }, String.CASE_INSENSITIVE_ORDER);

            default:
                // Default to string comparison
                return Comparator.comparing(node -> {
                    Object value = getValueAt(node, column);
                    return value == null ? "" : value.toString();
                });
        }
    }
}
