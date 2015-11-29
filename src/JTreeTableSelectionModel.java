import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;

public class JTreeTableSelectionModel extends DefaultTreeSelectionModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTreeTableSelectionModel() {
        super();

        getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public  void  valueChanged (ListSelectionEvent e) {

            }
        });
    }

    ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
}