package x.mvmn.patienceajdbc.gui.patients;

import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import x.mvmn.patienceajdbc.model.PatientStatsData;

public class PatientsListTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -1702487989268809882L;

	private final List<PatientStatsData> patientsStatData;

	public PatientsListTableModel(final List<PatientStatsData> patientsData) {
		if (patientsData != null) {
			this.patientsStatData = patientsData;
		} else {
			this.patientsStatData = Collections.emptyList();
		}
	}

	public int getColumnCount() {
		return 5;
	}

	public int getRowCount() {
		return patientsStatData.size();
	}

	public long getPatientIdAtRow(int row) {
		return patientsStatData.get(row).getPatientId();
		// Index out of bounds? You deserve it!
	}

	private static final String[] COLUMN_NAMES = { "ID", "Name", "Date of Birth", "Address", "First Examination", "Visits" };

	public String getColumnName(int columnIndex) {
		return columnIndex < COLUMN_NAMES.length ? COLUMN_NAMES[columnIndex] : "";
	}

	public PatientStatsData getValue(int rowIndex) {
		return patientsStatData.get(rowIndex);
	}

	public Object getValueAt(int row, int column) {
		Object result = null;
		if (row < patientsStatData.size() && column < 6) {
			PatientStatsData rowModel = patientsStatData.get(row);

			switch (column) {
				case 0:
					result = rowModel.getPatientId();
				break;
				case 1:
					result = rowModel.getFullName();
				break;
				case 2:
					result = rowModel.getDateOfBirth();
				break;
				case 3:
					result = rowModel.getAddress();
				break;
				case 4:
					result = rowModel.getDateOfFirstExamination();
				break;
				case 5:
					result = rowModel.getVisitsCount();
				break;
			}
		}

		return result;
	}

}
