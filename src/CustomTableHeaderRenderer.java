import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class CustomTableHeaderRenderer extends JLabel implements TableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private int sortColumn = -1;
    private boolean ascending = true;

    public void setSortState(int sortColumn, boolean ascending) {
        this.sortColumn = sortColumn;
        this.ascending = ascending;
    }

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		String text = value == null ? "" : value.toString();
		// Add sorting indicator if this is the sorted column
		if (column == sortColumn) {
			String arrow = ascending ? " \u25B2" : " \u25BC"; // ? or ?
			text += arrow;
		}
		// Accent color
		Color accent = new Color(70, 130, 180); // Steel Blue
		String line = "<div style='color:rgb(" + accent.getRed() + "," + accent.getGreen() + "," + accent.getBlue() + ");font-weight:bold'>";
		setText("<html>" + line + text + "</div> </html>");
		setPreferredSize(new Dimension(10, 32));
		setVerticalAlignment(CENTER);
		setOpaque(true);
		setForeground(accent);
		return this;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw accent top line
		Color accent = new Color(70, 130, 180); // Steel Blue
		g.setColor(accent);
		// Draw top line
		g.fillRect(0, 0, getWidth(), 3);
		// Draw bottom line
		g.fillRect(0, getHeight() - 3, getWidth(), 3);
	}

}