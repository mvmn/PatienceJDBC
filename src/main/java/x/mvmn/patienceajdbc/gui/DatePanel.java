package x.mvmn.patienceajdbc.gui;

import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import x.mvmn.lang.container.TupleOfThree;

public class DatePanel extends JPanel {

	private static final long serialVersionUID = 8004527163256281893L;

	protected JTextField tfYear = new JTextField("    ");
	protected final JComboBox<String> cbMonth = new JComboBox<String>(new String[] { "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" });
	protected final JComboBox<String> cbDay = new JComboBox<String>(new String[] { "", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" });

	public DatePanel() {
		this(new GridLayout(1, 3));
	}

	public DatePanel(LayoutManager layoutManager) {
		super(layoutManager);
		this.add(tfYear);
		this.add(cbMonth);
		this.add(cbDay);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		tfYear.setEnabled(enabled);
		cbMonth.setEnabled(enabled);
		cbDay.setEnabled(enabled);
	}

	public void setValues(final String year, final int month, final int day) {
		tfYear.setText(year != null ? year.trim() : "");

		if (month >= 0 && month <= 12) {
			cbMonth.setSelectedIndex(month);
		} else {
			cbMonth.setSelectedIndex(0);
		}

		if (day >= 0 && day <= 31) {
			cbDay.setSelectedIndex(day);
		} else {
			cbDay.setSelectedIndex(0);
		}
	}

	public void setValues(final String year, final Integer month, final Integer day) {
		setValues(year, month != null ? month.intValue() : 0, day != null ? day.intValue() : 0);
	}

	public void setValues(final Integer year, final Integer month, final Integer day) {
		setValues(year != null ? year.toString() : "", month, day);
	}

	public TupleOfThree<Integer, Integer, Integer> getEnteredDate() {
		Integer dateYear = null;
		Integer dateMonth = null;
		Integer dateDay = null;
		if (tfYear.getText().trim().length() > 0) {
			dateYear = Integer.parseInt(tfYear.getText().trim());
		}
		if (cbMonth.getSelectedIndex() > 0) {
			dateMonth = cbMonth.getSelectedIndex();
		}
		if (cbDay.getSelectedIndex() > 0) {
			dateDay = cbDay.getSelectedIndex();
		}
		return new TupleOfThree<Integer, Integer, Integer>(dateYear, dateMonth, dateDay);
	}
}
