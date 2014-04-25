package x.mvmn.patienceajdbc.gui;

public interface TableCellValueAdaptor<T, R> {

	public R getValueAt(T data, int columnIndex);

	public R getEmptyValue();

	public int getColumnsCount();

	public String getColumnName(int columnIndex);
}
