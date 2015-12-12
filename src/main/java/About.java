package main.java;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class About extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					About dialog = new About(null);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public About(Component f) {
		setTitle("About");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 354, 186);
		setLocationRelativeTo(f);
		
		JLabel lblCognizantProjectViewer = new JLabel("MPP Viewer");
		lblCognizantProjectViewer.setBorder(new EmptyBorder(10, 10, 5, 5));
		lblCognizantProjectViewer.setHorizontalAlignment(SwingConstants.LEFT);
		lblCognizantProjectViewer.setFont(new Font("Droid Sans Fallback", Font.BOLD, 16));
		
		JLabel lblDevelopedByCognizant = new JLabel("Developed by: Samik S.");
		lblDevelopedByCognizant.setBorder(new EmptyBorder(5, 10, 5, 5));
		lblDevelopedByCognizant.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblVersionbeta = new JLabel("Version: 0.5-beta");
		lblVersionbeta.setBorder(new EmptyBorder(5, 10, 5, 5));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setMaximumSize(new Dimension(32767, 30));
		verticalStrut.setPreferredSize(new Dimension(0, 30));
		getContentPane().add(verticalStrut);
		getContentPane().add(lblCognizantProjectViewer);
		getContentPane().add(lblVersionbeta);
		getContentPane().add(lblDevelopedByCognizant);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
