package x.mvmn.patienceajdbc.gui.medication;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import x.mvmn.patienceajdbc.gui.SwingHelper;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.service.MedicationService;

public class MedicationChooserDialog extends JDialog implements LocaleChangeAware, ActionListener {
	private static final long serialVersionUID = -969143905927974355L;

	private static final String ACT_COMMAND_BTN_OK = "btnActionOk";
	private static final String ACT_COMMAND_BTN_CANCEL = "btnActionCancel";
	private static final String ACT_COMMAND_BTN_CREATE = "btnCreateMedication";

	protected final MedicationService medicationService;
	protected final DefaultComboBoxModel<MedicationDisplay> cbxModelMedications;
	protected final JComboBox<MedicationDisplay> cbxMedications;
	protected final JButton okButton = new JButton();
	protected final JButton cancelButton = new JButton();
	protected final JButton createButton = new JButton();

	private volatile boolean accepted = false;

	// private volatile long currentIllnessId = -1;

	public MedicationChooserDialog(final MedicationService medicationService) {
		this.medicationService = medicationService;

		this.cbxModelMedications = new DefaultComboBoxModel<MedicationDisplay>();
		this.cbxMedications = new JComboBox<MedicationDisplay>(cbxModelMedications);

		this.okButton.addActionListener(this);
		this.okButton.setActionCommand(ACT_COMMAND_BTN_OK);
		this.cancelButton.addActionListener(this);
		this.cancelButton.setActionCommand(ACT_COMMAND_BTN_CANCEL);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.cbxMedications, BorderLayout.CENTER);
		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.add(okButton, BorderLayout.EAST);
		btnPanel.add(cancelButton, BorderLayout.WEST);
		this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		this.pack();
		SwingHelper.moveToScreenCenter(this);
	}

	public Medication chooseMedications(final long illnessId) {
		Medication result = null;
		accepted = false;
		// currentIllnessId = illnessId;
		cbxModelMedications.removeAllElements();

		List<Medication> medicationsForIllness = medicationService.list(illnessId);
		if (medicationsForIllness != null) {
			for (Medication medication : medicationsForIllness) {
				cbxModelMedications.addElement(new MedicationDisplay(medication));
			}
		}
		this.setVisible(true);
		if (accepted) {
			result = ((MedicationDisplay) this.cbxMedications.getSelectedItem()).getWrappedMedication();
		}

		return result;
	}

	private class MedicationDisplay {
		private final Medication medication;

		public MedicationDisplay(final Medication medication) {
			this.medication = medication;
		}

		public String toString() {
			return this.medication.getName();
		}

		public Medication getWrappedMedication() {
			return this.medication;
		}
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier) {
		notifier.registerLocaleChangeListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ACT_COMMAND_BTN_OK)) {
			accepted = true;
			this.setVisible(false);
		} else if (e.getActionCommand().equals(ACT_COMMAND_BTN_CANCEL)) {
			accepted = false;
			this.setVisible(false);
		} else if (e.getActionCommand().equals(ACT_COMMAND_BTN_CREATE)) {

		}
	}
}
