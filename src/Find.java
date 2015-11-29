import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;


public class Find extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	MainWindow mainWindow;
	private JButton btnFind;
	private JLabel labelStatus;
	private JButton btnFindPrevious;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Find dialog = new Find(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Find(Component c) {
		super((MainWindow)c);
		setResizable(false);
		setTitle("Find");
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	Find.this.dispose();
                mainWindow.lblStatus.setText("Done");
            }
        });
		this.mainWindow=(MainWindow) c;
		setBounds(100, 100, 307, 133);
		setLocationRelativeTo(mainWindow);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			textField = new JTextField();
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search(true);
				}
			});
			textField.setColumns(20);
		}
		{
			btnFind = new JButton("Find Next");
			btnFind.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search(true);
				}
			});
		}
		{
			labelStatus = new JLabel("                                         ");
			labelStatus.setFont(new Font("Tahoma", Font.PLAIN, 11));
		}
		{
			btnFindPrevious = new JButton("Find Previous");
			btnFindPrevious.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					search(false);
				}
			});
		}
		
		JLabel lblFind = new JLabel("Search for:");
		lblFind.setFont(new Font("Tahoma", Font.BOLD, 11));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(labelStatus, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addComponent(btnFindPrevious)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFind))
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
						.addComponent(lblFind))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(lblFind)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnFind)
						.addComponent(btnFindPrevious))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(labelStatus))
		);
		contentPanel.setLayout(gl_contentPanel);
	}

	private void search(boolean forward){
		String searchWith=textField.getText().trim().toLowerCase();
		if(searchWith.isEmpty()) return;
		
		boolean isFound=false;
		int currentRow=mainWindow.treeTable.getSelectedRow();
		currentRow=currentRow==-1?0:currentRow;
		int rowCount=mainWindow.treeTable.getModel().getRowCount();
		
		String task="";
		
		if(forward)
			for(int i=currentRow+1;i<rowCount;i++){
				task=(String) mainWindow.treeTable.getModel().getValueAt(i, 1);
				if(task.toLowerCase().contains(searchWith)){
					mainWindow.treeTable.setRowSelectionInterval(i, i);
					mainWindow.treeTable.scrollRectToVisible(new Rectangle(mainWindow.treeTable.getCellRect(i, 0, true)));
					isFound=true;
					break;
				}
			}
		else
			for(int i=currentRow-1;i>=0;i--){
				task=(String) mainWindow.treeTable.getModel().getValueAt(i, 1);
				if(task.toLowerCase().contains(searchWith)){
					mainWindow.treeTable.setRowSelectionInterval(i, i);
					mainWindow.treeTable.scrollRectToVisible(new Rectangle(mainWindow.treeTable.getCellRect(i, 0, true)));
					isFound=true;
					break;
				}
			}
		if(!isFound)
			mainWindow.lblStatus.setText("No match found!");
		else mainWindow.lblStatus.setText("");
		
	}
}
