package x.mvmn.patienceajdbc.gui.patients;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import org.springframework.context.MessageSource;

import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.model.PatientStatsData;

public class PatientsListTableModel extends AbstractTableModel implements LocaleChangeAware {
	private static final long serialVersionUID = -1702487989268809882L;

	private final List<PatientStatsData> patientsStatData;
	private final MessageSource messageSource;

	private Locale locale = Locale.ENGLISH;

	public PatientsListTableModel(final List<PatientStatsData> patientsData, final MessageSource messageSource) {
		this.messageSource = messageSource;
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

	private static final String[] COLUMN_NAMES = { "patients_per_illness_list.column.id", "patients_per_illness_list.column.name",
			"patients_per_illness_list.column.date_of_birth", "patients_per_illness_list.column.address", "patients_per_illness_list.column.first_examination",
			"patients_per_illness_list.column.visits" };

	public String getColumnName(int columnIndex) {
		return columnIndex < COLUMN_NAMES.length ? messageSource.getMessage(COLUMN_NAMES[columnIndex], null, locale) : "";
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

	@Override
	public void setLocale(Locale locale) {
		if (locale != null) {
			this.locale = locale;
			this.fireTableStructureChanged();
		}
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier) {
		notifier.registerLocaleChangeListener(this);
	}

}
