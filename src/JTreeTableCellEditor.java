import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;

public class JTreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree tree;
    private JTable table;

    public JTreeTableCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
        return tree;
    }

    public boolean isCellEditable (EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            if(me.getClickCount()==2){
            	int  colunm1 = 1;
            	int doubleClick = 2;
	            MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick, me.isPopupTrigger());
	            tree.dispatchEvent(newME);
            }
        }
        return  false ;
    }

    @Override
    public  Object getCellEditorValue() {
        return  null;
    }

}