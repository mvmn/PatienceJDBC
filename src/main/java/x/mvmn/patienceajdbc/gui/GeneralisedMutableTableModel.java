package x.mvmn.patienceajdbc.gui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;

public class GeneralisedMutableTableModel<T, A> extends AbstractTableModel {

	private static final long serialVersionUID = 1769140544667339258L;
	protected final CopyOnWriteArrayList<T> rows;
	protected final TableCellValueAdaptor<T, A> adaptor;

	public GeneralisedMutableTableModel(final List<T> rows, TableCellValueAdaptor<T, A> adaptor) {
		this.rows = new CopyOnWriteArrayList<T>(rows);
		this.adaptor = adaptor;
	}

	public T remove(int index) {
		T result = null;
		if (index < rows.size()) {
			result = rows.remove(index);
			fireTableRowsDeleted(index, index);
		}
		return result;
	}

	public void add(T medication) {
		if (!rows.contains(medication)) {
			rows.add(medication);
			int lastIndex = rows.size() - 1;
			fireTableRowsInserted(lastIndex, lastIndex);
		}
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return adaptor.getColumnsCount();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return adaptor.getColumnName(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public A getValueAt(int rowIndex, int columnIndex) {
		A result = adaptor.getEmptyValue();
		if (rowIndex < rows.size()) {
			T medication = rows.get(rowIndex);
			result = adaptor.getValueAt(medication, columnIndex);
		}

		return result;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
}
