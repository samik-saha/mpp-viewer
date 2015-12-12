package main.java;
import java.util.Date;

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
        return column==1; // Important to activate TreeExpandListener
    }

    public void setValueAt(Object aValue, Object node, int column) {
    }

}