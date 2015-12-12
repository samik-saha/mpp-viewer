package main.java;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class FeedbackForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JEditorPane dtrpnComments;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FeedbackForm dialog = new FeedbackForm(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FeedbackForm(Component f) {
		setResizable(false);
		setTitle("Feedback");
		setModal(true);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(f);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JRadioButton rdbtnQuestion = new JRadioButton("Question");
				rdbtnQuestion.setActionCommand("Question");
				rdbtnQuestion.setSelected(true);
				panel.add(rdbtnQuestion);
				buttonGroup.add(rdbtnQuestion);
			}
			{
				JRadioButton rdbtnBugReport = new JRadioButton("Bug Report");
				rdbtnBugReport.setActionCommand("Bug Report");
				buttonGroup.add(rdbtnBugReport);
				panel.add(rdbtnBugReport);
			}
			{
				JRadioButton rdbtnSuggestion = new JRadioButton("Suggestion");
				rdbtnSuggestion.setActionCommand("Suggestion");
				buttonGroup.add(rdbtnSuggestion);
				panel.add(rdbtnSuggestion);
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				dtrpnComments = new JEditorPane();
				dtrpnComments.setText("Please write your comments here");
				scrollPane.setViewportView(dtrpnComments);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Send");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sendFeedback();
						dispose();
					}
				});
				okButton.setToolTipText("Opens your default mail program");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public void sendFeedback() {
		Desktop desktop;
		String category = buttonGroup.getSelection().getActionCommand();
		String body = dtrpnComments.getText();

		if (Desktop.isDesktopSupported()
				&& (desktop = Desktop.getDesktop())
						.isSupported(Desktop.Action.MAIL)) {
			URI mailto;
			try {
				mailto = new URI(
						"mailto:samiksaha88@gmail.com?subject=Feedback"
								+ URLEncoder.encode("|", "UTF-8")
								+ "Simple%20Project%20Viewer"
								+ URLEncoder.encode("|", "UTF-8")
								+ URLEncoder.encode(category, "UTF-8").replace(
										"+", "%20")
								+ "&body="
								+ URLEncoder.encode(body, "UTF-8").replace("+",
										"%20"));

				desktop.mail(mailto);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException(
					"Desktop doesn't support mailto; mail is dead anyway ;)");
		}
	}

}
