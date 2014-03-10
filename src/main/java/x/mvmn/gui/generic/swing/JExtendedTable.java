package x.mvmn.gui.generic.swing;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class JExtendedTable<T extends TableModel> extends JTable {

	public JExtendedTable() {
		super();
	}

	public JExtendedTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	public JExtendedTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}

	public JExtendedTable(T dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	public JExtendedTable(T dm, TableColumnModel cm) {
		super(dm, cm);
	}

	public JExtendedTable(T dm) {
		super(dm);
	}

	public JExtendedTable(Vector<?> rowData, Vector<?> columnNames) {
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
