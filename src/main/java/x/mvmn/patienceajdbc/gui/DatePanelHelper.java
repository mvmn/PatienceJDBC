package x.mvmn.patienceajdbc.gui;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import x.mvmn.lang.container.TupleOfThree;

public class DatePanelHelper {

	public static JPanel createDatePanel(JTextField tfYear, JComboBox<String> cbMonth, JComboBox<String> cbDay, TitledBorder borderLabel) {
		JPanel result = new JPanel(new GridLayout(1, 3));
		if (borderLabel != null) {
			result.setBorder(borderLabel);
		}
		result.add(tfYear);
		result.add(cbMonth);
		result.add(cbDay);
		return result;
	}

	public static TupleOfThree<Integer, Integer, Integer> extractDate(JTextField tfYear, JComboBox<String> cbMonth, JComboBox<String> cbDay) {
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
