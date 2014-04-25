package x.mvmn.gui.generic.swing;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class GeneralisedJTable<T extends TableModel> extends JTable {

	public GeneralisedJTable() {
		super();
	}

	public GeneralisedJTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public GeneralisedJTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public GeneralisedJTable(T dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public GeneralisedJTable(T dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public GeneralisedJTable(T dm) {
		super(dm);
	}

	public GeneralisedJTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}

	private static final long serialVersionUID = -6861027949309549032L;

	public void setTableModel(T model) {
		super.setModel(model);
	}

	@SuppressWarnings("unchecked")
	public T getTableModel() {
		return (T) super.getModel();
	}

}
