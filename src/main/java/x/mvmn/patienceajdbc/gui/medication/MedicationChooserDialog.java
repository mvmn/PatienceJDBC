package x.mvmn.patienceajdbc.gui.medication;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.context.MessageSource;

import x.mvmn.patienceajdbc.gui.SwingHelper;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.service.IllnessesService;
import x.mvmn.patienceajdbc.service.MedicationService;

public class MedicationChooserDialog extends JDialog implements LocaleChangeAware, ActionListener {
	private static final long serialVersionUID = -969143905927974355L;

	private static final String ACT_COMMAND_BTN_OK = "btnActionOk";
	private static final String ACT_COMMAND_BTN_CANCEL = "btnActionCancel";
	private static final String ACT_COMMAND_BTN_CREATE = "btnCreateMedication";

	private static final String LOCALIZATION_KEY_BUTTON_OK = "meidcation_chooser_dialog.button.ok";
	private static final String LOCALIZATION_KEY_BUTTON_CANCEL = "meidcation_chooser_dialog.button.cancel";

	protected final MessageSource messageSource;
	protected final MedicationService medicationService;
	protected final IllnessesService illnessesService;

	protected final DefaultComboBoxModel<MedicationDisplay> cbxModelMedications;
	protected final JComboBox<MedicationDisplay> cbxMedications;
	protected final JButton okButton = new JButton();
	protected final JButton cancelButton = new JButton();
	protected final JButton createButton = new JButton("+");

	private volatile boolean accepted = false;

	private volatile long currentIllnessId = -1;

	public MedicationChooserDialog(final MedicationService medicationService, final IllnessesService illnessesService, final MessageSource messageSource) {
		this.messageSource = messageSource;
		this.medicationService = medicationService;
		this.illnessesService = illnessesService;

		this.cbxModelMedications = new DefaultComboBoxModel<MedicationDisplay>();
		this.cbxMedications = new JComboBox<MedicationDisplay>(cbxModelMedications);

		this.okButton.addActionListener(this);
		this.okButton.setActionCommand(ACT_COMMAND_BTN_OK);
		this.cancelButton.addActionListener(this);
		this.cancelButton.setActionCommand(ACT_COMMAND_BTN_CANCEL);

		this.createButton.addActionListener(this);
		this.createButton.setActionCommand(ACT_COMMAND_BTN_CREATE);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.cbxMedications, BorderLayout.CENTER);
		this.getContentPane().add(this.createButton, BorderLayout.EAST);
		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.add(okButton, BorderLayout.EAST);
		btnPanel.add(cancelButton, BorderLayout.WEST);
		this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		this.pack();
		SwingHelper.moveToScreenCenter(this);
	}

	public Medication chooseMedications(final long illnessId) {
		Medication result = null;

		this.currentIllnessId = illnessId;

		accepted = false;
		resetMedicationsComboBox(illnessId);

		this.setModal(true);
		this.pack();
		SwingHelper.moveToScreenCenter(this);
		this.setVisible(true);
		if (accepted && this.cbxMedications.getSelectedItem() != null) {
			result = ((MedicationDisplay) this.cbxMedications.getSelectedItem()).getWrappedMedication();
		}

		return result;
	}

	protected void resetMedicationsComboBox(final long illnessId) {
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

		public Medication getWrappedMedication() {
			return this.medication;
		}
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier) {
		notifier.registerLocaleChangeListener(this);
		setLocale(notifier.getLastSetLocale());
	}

	public void setLocale(final Locale locale) {
		super.setLocale(locale);
		if (messageSource != null && locale != null) {
			this.okButton.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_OK, null, locale));
			this.cancelButton.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_CANCEL, null, locale));
		}
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
			// TODO: localize
			String medicationName = JOptionPane.showInputDialog(this, "Enter name of new medication", "Create medication for for illness "
					+ illnessesService.getIllness(this.currentIllnessId).getName(), JOptionPane.QUESTION_MESSAGE);
			if (medicationName != null && medicationName.trim().length() > 0) {
				if (medicationService.findByName(currentIllnessId, medicationName.trim()) == null) {
					medicationService.create(this.currentIllnessId, medicationName.trim());
					resetMedicationsComboBox(currentIllnessId);
				} else {
					// TODO: localize
					JOptionPane.showMessageDialog(this, "Name already taken", "Medication not created", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
