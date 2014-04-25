package x.mvmn.patienceajdbc.gui.medication;

import x.mvmn.patienceajdbc.gui.TableCellValueAdaptor;
import x.mvmn.patienceajdbc.model.Medication;

public class MedicationTableCellValueAdaptor implements TableCellValueAdaptor<Medication, String> {

	public static final MedicationTableCellValueAdaptor INSTANCE = new MedicationTableCellValueAdaptor();

	private static final String COLUMN_NAMES[] = { "ID", "Medication" };

	private MedicationTableCellValueAdaptor() {
	};

	@Override
	public String getColumnName(int columnIndex) {
		// FIXME: Localize
		return COLUMN_NAMES[columnIndex];
	}

	@Override
	public String getValueAt(Medication medication, int columnIndex) {
		String result = "";
		switch (columnIndex) {
			case 0:
				result = String.valueOf(medication.getId());
			break;
			case 1:
				result = medication.getName();
			break;
		}
		return result;
	}

	@Override
	public String getEmptyValue() {
		return "";
	}

	@Override
	public int getColumnsCount() {
		return 2;
	}
}
