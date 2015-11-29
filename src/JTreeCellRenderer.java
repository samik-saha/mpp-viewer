import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Custom renderer to add padding to cells
 */
class JTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (!leaf) {
			// same font but bold
			Font boldFont = new Font("Arial", Font.BOLD, 12);
			setFont(boldFont);
		} else {
			Font plainFont = new Font("Arial", Font.PLAIN, 12);
			setFont(plainFont);
		}
		return this;
	}
	

	@Override
	public java.awt.Color getBackgroundSelectionColor() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public java.awt.Color getBackgroundNonSelectionColor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public java.awt.Color getBackground() {
	    return (null);
	}
}