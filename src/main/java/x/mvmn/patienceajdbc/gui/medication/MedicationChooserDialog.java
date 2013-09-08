package x.mvmn.patienceajdbc.gui.medication;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import x.mvmn.patienceajdbc.gui.SwingHelper;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.service.MedicationService;

public class MedicationChooserDialog extends JDialog implements LocaleChangeAware, ActionListener {
	private static final long serialVersionUID = -969143905927974355L;

	private static final String ACT_COMMAND_BTN_OK = "btnActionOk";
	private static final String ACT_COMMAND_BTN_CANCEL = "btnActionCancel";

	protected final MedicationService medicationService;
	protected final DefaultComboBoxModel<MedicationDisplay> cbxModelMedications;
	protected final JComboBox<MedicationDisplay> cbxMedications;
	protected final JButton okButton = new JButton();
	protected final JButton cancelButton = new JButton();

	public MedicationChooserDialog(final MedicationService medicationService) {
		this.medicationService = medicationService;

		this.cbxModelMedications = new DefaultComboBoxModel<MedicationDisplay>();
		this.cbxMedications = new JComboBox<MedicationDisplay>(cbxModelMedications);

		this.okButton.addActionListener(this);
		this.okButton.setActionCommand(ACT_COMMAND_BTN_OK);
		this.cancelButton.addActionListener(this);
		this.cancelButton.setActionCommand(ACT_COMMAND_BTN_CANCEL);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.cbxMedications, BorderLayout.NORTH);
		this.pack();
		SwingHelper.moveToScreenCenter(this);
	}

	protected void initMedicationsCheckbox(final long illnessId) {
		cbxModelMedications.removeAllElements();

		List<Medication> medicationsForIllness = medicationService.list(illnessId);
		if (medicationsForIllness != null) {
			for (Medication medication : medicationsForIllness) {
				cbxModelMedications.addElement(new MedicationDisplay(medication));
			}
		}
	}

	private class MedicationDisplay {
		private final Medication medication;

		public MedicationDisplay(final Medication medication) {
			this.medication = medication;
		}

		public String toString() {
			return this.medication.getName();
		}
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier) {
		notifier.registerLocaleChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ACT_COMMAND_BTN_OK)) {
			//
		} else if (e.getActionCommand().equals(ACT_COMMAND_BTN_CANCEL)) {
			//
		}
	}
}
