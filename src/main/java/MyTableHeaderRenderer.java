package main.java;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class MyTableHeaderRenderer extends JLabel implements TableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		setText("<HTML><div style='font-weight:normal'>"+value.toString()+"</div></HTML>");
		setPreferredSize(new Dimension(10, 32));
		setVerticalAlignment(TOP);
		setOpaque(true);
		setBackground(new Color(223,227,232));
		return this;
	}
	
}