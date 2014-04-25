package x.mvmn.patienceajdbc.gui.examinations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import x.mvmn.patienceajdbc.gui.TableCellValueAdaptor;
import x.mvmn.patienceajdbc.model.ExaminationData;

public class ExaminationCellValueAdaptor implements TableCellValueAdaptor<ExaminationData, String> {

	protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static final ExaminationCellValueAdaptor INSTANCE = new ExaminationCellValueAdaptor();

	private ExaminationCellValueAdaptor() {
	}

	@Override
	public String getValueAt(ExaminationData examData, int columnIndex) {
		String value = "";
		switch (columnIndex) {
			case 0:
				value = examData.getExaminationDate() != null ? dateFormat.format(examData.getExaminationDate()) : "--";
			break;
			case 1:
				value = String.valueOf(examData.getNumber());
			break;
			case 2:
				value = examData.getComments() != null ? examData.getComments() : "";
			break;
		}

		return value;
	}

	@Override
	public String getEmptyValue() {
		return "";
	}

	@Override
	public int getColumnsCount() {
		return 3;
	}

	private final String[] COLUMN_NAMES = { "Date", "Number", "Comment" };

	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
	}

}
