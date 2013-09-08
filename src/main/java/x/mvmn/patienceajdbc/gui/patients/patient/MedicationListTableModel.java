package x.mvmn.patienceajdbc.gui.patients.patient;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import x.mvmn.patienceajdbc.model.Medication;

public class MedicationListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1290919351447104824L;

	protected final List<Medication> medications;

	public MedicationListTableModel(final List<Medication> medications) {
		this.medications = medications;
	}

	@Override
	public int getRowCount() {
		return medications.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	private static final String COLUMN_NAMES[] = { "ID", "Medication" };

	@Override
	public String getColumnName(int columnIndex) {
		// FIXME: Localize
		return COLUMN_NAMES[columnIndex];
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		String result = "";
		if (rowIndex >= medications.size()) {
			Medication medication = medications.get(rowIndex);
			switch (columnIndex) {
				case 0:
					result = String.valueOf(medication.getId());
				break;
				case 1:
					result = medication.getName();
				break;
			}
		}

		return result;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
}
