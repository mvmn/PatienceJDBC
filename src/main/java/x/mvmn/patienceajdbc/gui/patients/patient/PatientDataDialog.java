package x.mvmn.patienceajdbc.gui.patients.patient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.springframework.context.MessageSource;

import x.mvmn.gui.generic.awt.event.DefaultWindowListener;
import x.mvmn.gui.generic.swing.ExtendedTitledBorder;
import x.mvmn.gui.generic.swing.JExtendedTabPane;
import x.mvmn.gui.generic.swing.JExtendedTable;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.medication.MedicationChooserDialog;
import x.mvmn.patienceajdbc.gui.medication.MedicationListTableModel;
import x.mvmn.patienceajdbc.gui.patients.PatientsListWindow;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.service.IllnessesService;
import x.mvmn.patienceajdbc.service.MedicationService;
import x.mvmn.patienceajdbc.service.PatientsService;
import x.mvmn.patienceajdbc.util.collections.MedicationIllnessFilter;
import x.mvmn.util.collections.CollectionsHelper;

public class PatientDataDialog extends JDialog implements LocaleChangeAware, Titled {

	private static final long serialVersionUID = 6758533501782946639L;

	private static final String LOCALIZATION_KEY_WINDOW_TITLE = "patient_details_window.title";
	private static final String LOCALIZATION_KEY_NEW_PATIENT = "patient_details_window.title.new_patient";

	private static final String LOCALIZATION_KEY_FIELD_NAME_FIRST = "patient_details_window.field.name.first";
	private static final String LOCALIZATION_KEY_FIELD_NAME_LAST = "patient_details_window.field.name.last";
	private static final String LOCALIZATION_KEY_FIELD_NAME_PATRONYMIC = "patient_details_window.field.name.patronymic";

	private static final String LOCALIZATION_KEY_FIELD_BIRTHDATE = "patient_details_window.field.birthdate";
	private static final String LOCALIZATION_KEY_FIELD_DIAGNOSE_DATE = "patient_details_window.field.diagnose_date";
	private static final String LOCALIZATION_KEY_FIELD_DEATHDATE = "patient_details_window.field.deathdate";
	private static final String LOCALIZATION_KEY_FIELD_IS_DEAD = "patient_details_window.field.is_dead";

	private static final String LOCALIZATION_KEY_FIELD_ADDRESS = "patient_details_window.field.address";
	private static final String LOCALIZATION_KEY_FIELD_ANAMNESIS = "patient_details_window.field.anamnesis";

	private static final String LOCALIZATION_KEY_BUTTON_SAVE = "patient_details_window.button.save";
	private static final String LOCALIZATION_KEY_BUTTON_CANCEL = "patient_details_window.button.cancel";

	private final MessageSource messageSource;
	private volatile Locale currentLocale = Locale.US;

	protected final JTextField tfFirstName = new JTextField();
	protected final JTextField tfLastName = new JTextField();
	protected final JTextField tfPatronymicName = new JTextField();

	protected final TitledBorder lblFirstName = new TitledBorder("");
	protected final TitledBorder lblLastName = new TitledBorder("");
	protected final TitledBorder lblPatronymicName = new TitledBorder("");

	protected final JTextField tfBirthDate = new JTextField();
	protected final JTextField tfDiagnoseDate = new JTextField();
	protected final JTextField tfDeathDate = new JTextField();
	protected final JCheckBox cbDead = new JCheckBox();

	protected final TitledBorder lblBirthDate = new TitledBorder("");
	protected final TitledBorder lblDiagnoseDate = new TitledBorder("");
	protected final JLabel lblDeathDate = new JLabel();
	protected final TitledBorder lblDead = new TitledBorder("");

	protected final JTextArea taAddressText = new JTextArea();
	protected final JTextArea taAnamnesis = new JTextArea();

	protected final TitledBorder lblAddressText = new TitledBorder("");
	protected final TitledBorder lblAnamnesis = new TitledBorder("");

	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	protected final JButton btnSave = new JButton();
	protected final JButton btnCancel = new JButton();

	protected volatile long currentPatientId = -1;

	protected final PatientsService patientsService;
	protected final IllnessesService illnessesService;
	protected final PatientsListWindow patientsListWindow;

	protected final JExtendedTabPane<JPanel> illnessTabs;

	protected final Map<Illness, JExtendedTable<MedicationListTableModel>> medicationsTables = new ConcurrentHashMap<Illness, JExtendedTable<MedicationListTableModel>>();
	protected final MedicationChooserDialog medChooserDialog;

	public PatientDataDialog(final PatientsService patientsService, final IllnessesService illnessesService, final MedicationService medicationService,
			final PatientsListWindow patientsListWindow, final MessageSource messageSource) {
		this.patientsService = patientsService;
		this.illnessesService = illnessesService;
		this.patientsListWindow = patientsListWindow;
		this.messageSource = messageSource;

		illnessTabs = new JExtendedTabPane<JPanel>();

		setData(null);
		this.setModal(false);

		medChooserDialog = new MedicationChooserDialog(medicationService, messageSource);

		JPanel fieldsPanel = new JPanel(new GridLayout(1, 4));
		// fieldsPanel.setLayout(new BorderLayout());
		fieldsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		{
			{
				JPanel shortFieldsPanel = new JPanel(new GridLayout(3, 1));

				tfFirstName.setBorder(lblFirstName);
				tfLastName.setBorder(lblLastName);
				tfPatronymicName.setBorder(lblPatronymicName);
				shortFieldsPanel.add(tfFirstName);
				shortFieldsPanel.add(tfLastName);
				shortFieldsPanel.add(tfPatronymicName);
				fieldsPanel.add(shortFieldsPanel);
			}

			JPanel dateFieldsPanel = new JPanel(new GridLayout(3, 1));
			{
				// tfBirthDate.setBorder(lblBirthDate);
				JPanel pnlBirthDate = new JPanel();
				pnlBirthDate.setBorder(lblBirthDate);
				pnlBirthDate.setLayout(new GridLayout(1, 1));
				pnlBirthDate.add(tfBirthDate);
				dateFieldsPanel.add(pnlBirthDate);
			}
			{
				JPanel pnlDiagnoseDate = new JPanel();
				pnlDiagnoseDate.setBorder(lblDiagnoseDate);
				pnlDiagnoseDate.setLayout(new GridLayout(1, 1));
				pnlDiagnoseDate.add(tfDiagnoseDate);
				// tfDiagnoseDate.setBorder(lblDiagnoseDate);
				dateFieldsPanel.add(pnlDiagnoseDate);

			}

			{
				JPanel deathDatePanel = new JPanel();
				// new ComponentTitledBorder(cbDead, deathDatePanel,
				// BorderFactory.createEmptyBorder())
				// deathDatePanel.setBorder(BorderFactory.createTitledBorder("Blah"));
				deathDatePanel.setBorder(new ExtendedTitledBorder(cbDead, deathDatePanel));
				deathDatePanel.setLayout(new BorderLayout());
				deathDatePanel.add(lblDeathDate, BorderLayout.WEST);
				deathDatePanel.add(tfDeathDate, BorderLayout.CENTER);
				dateFieldsPanel.add(deathDatePanel);
			}

			fieldsPanel.add(dateFieldsPanel);
		}

		{
			Dimension prefSize = tfDeathDate.getPreferredSize();
			prefSize.setSize(100, prefSize.getHeight());
			tfDeathDate.setPreferredSize(prefSize); // Superficial? Maybe.
		}

		cbDead.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actEvent) {
				tfDeathDate.setEnabled(cbDead.isSelected());
			}
		});

		{
			// JPanel longFieldsPanel = new JPanel(new GridLayout(1, 2));
			{
				JScrollPane taAddressTextScrollPane = new JScrollPane(taAddressText);
				taAddressTextScrollPane.setBorder(lblAddressText);
				fieldsPanel.add(taAddressTextScrollPane);
			}
			{
				JScrollPane taAnamnesisScrollPane = new JScrollPane(taAnamnesis);
				taAnamnesisScrollPane.setBorder(lblAnamnesis);
				fieldsPanel.add(taAnamnesisScrollPane);
			}
			// fieldsPanel.add(longFieldsPanel);
		}

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
		{
			buttonsPanel.add(btnSave);
			buttonsPanel.add(btnCancel);

			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PatientDataDialog.this.resetDataAndClose();
				}
			});

			btnSave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO: move off event dispatching thread
					try {
						long currentPatientId = PatientDataDialog.this.currentPatientId;
						PatientData patientData;

						String firstName = tfFirstName.getText();
						String lastName = tfLastName.getText();
						String patronymicName = tfPatronymicName.getText();
						Date birthDate = null;
						try {
							birthDate = dateFormat.parse(tfBirthDate.getText());
						} catch (ParseException e1) {
							// TODO: Signalize validation error and return
						}
						Date diagnoseDate = null;
						try {
							diagnoseDate = dateFormat.parse(tfDiagnoseDate.getText());
						} catch (ParseException e1) {
							// TODO: Signalize validation error and return
						}
						Date deathDate = null;
						try {
							deathDate = dateFormat.parse(tfDeathDate.getText());
						} catch (ParseException e1) {
							// TODO: Signalize validation error and return
						}
						boolean dead = cbDead.isSelected();
						String address = taAddressText.getText();
						String anamnesis = taAnamnesis.getText();

						if (currentPatientId >= 0) {
							patientData = patientsService.get(currentPatientId, false);
							patientData.setLastName(lastName);
							patientData.setFirstName(firstName);
							patientData.setPatronymicName(patronymicName);
							patientData.setAddress(address);
							patientData.setAnamnesis(anamnesis);
							patientData.setDateOfBirth(birthDate);
							patientData.setDateOfDiagnosis(diagnoseDate);
							patientData.setDateOfDeath(deathDate);
							patientData.setDead(dead);
							patientsService.update(patientData, false);
						} else {
							patientData = patientsService.create(lastName, firstName, patronymicName, address, birthDate, diagnoseDate, deathDate, dead,
									anamnesis);
						}

						// TODO: Signalize successfull save
						patientsListWindow.recreatePerIllnessListsTabs();
						resetDataAndClose();
					} catch (Exception saveError) {
						// TODO: Signalize error and return
					}
				}
			});
		}

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new DefaultWindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				PatientDataDialog.this.resetDataAndClose();
			}

		});

		this.getContentPane().add(fieldsPanel, BorderLayout.NORTH);
		this.getContentPane().add(illnessTabs.getUnderlyingComponent(), BorderLayout.CENTER);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}

	protected void recreateIllnessTabs() {
		illnessTabs.removeAll();
		Collection<Illness> illnessess = illnessesService.getAllIllnesses();
		for (final Illness illness : illnessess) {
			JPanel tabContent = new JPanel(new GridLayout(2, 1));
			JPanel medicationsPanel = new JPanel(new BorderLayout());
			final JExtendedTable<MedicationListTableModel> table = medicationsTables.get(illness);
			medicationsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
			JPanel buttonsPanel = new JPanel(new BorderLayout());
			JButton addMedication = new JButton("+"); // FIXME: localize
			addMedication.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// medChooserDialog.initMedicationsCheckbox(illness.getId());
					medChooserDialog.setVisible(true);
				}
			});
			JButton removeMedication = new JButton("-"); // FIXME: localize
			removeMedication.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedColumns() != null) {
						for (int selectedIndex : table.getSelectedColumns()) {
							table.getTableModel().removeMedication(selectedIndex);
						}
					}
				}
			});
			buttonsPanel.add(removeMedication, BorderLayout.WEST);
			buttonsPanel.add(addMedication, BorderLayout.EAST);
			medicationsPanel.add(buttonsPanel, BorderLayout.SOUTH);
			tabContent.add(medicationsPanel);

			tabContent.add(new JPanel());
			illnessTabs.addTab(illness.getName(), tabContent);
		}
	}

	protected void resetDataAndClose() {
		this.setData(null);
		this.setVisible(false);
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier localeChangeNotifier) {
		localeChangeNotifier.registerLocaleChangeListener(this);
		medChooserDialog.setSelfAsLocaleChangeListener(localeChangeNotifier);
		setLocale(localeChangeNotifier.getLastSetLocale());
	}

	protected void updateTitle(Locale locale) {
		String titlePrefix = messageSource.getMessage(LOCALIZATION_KEY_WINDOW_TITLE, null, locale);
		String newPatientMessage = messageSource.getMessage(LOCALIZATION_KEY_NEW_PATIENT, null, locale);
		this.setTitle(titlePrefix + ": " + (currentPatientId >= 0 ? String.valueOf(currentPatientId) : newPatientMessage));
	}

	public void setLocale(Locale locale) {
		super.setLocale(locale);
		this.currentLocale = locale;
		if (messageSource != null && locale != null) {
			updateTitle(locale);

			lblFirstName.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_NAME_FIRST, null, locale));
			lblLastName.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_NAME_LAST, null, locale));
			lblPatronymicName.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_NAME_PATRONYMIC, null, locale));

			lblBirthDate.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_BIRTHDATE, null, locale));
			lblDiagnoseDate.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_DIAGNOSE_DATE, null, locale));
			lblDeathDate.setText(messageSource.getMessage(LOCALIZATION_KEY_FIELD_DEATHDATE, null, locale));
			lblDead.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_IS_DEAD, null, locale));

			lblAddressText.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_ADDRESS, null, locale));
			lblAnamnesis.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_ANAMNESIS, null, locale));

			btnSave.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_SAVE, null, locale));
			btnCancel.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_CANCEL, null, locale));

			cbDead.setText(messageSource.getMessage(LOCALIZATION_KEY_FIELD_IS_DEAD, null, locale));
		}
	}

	/**
	 * @param patientData
	 *            if null, default (empty) values will be set
	 */
	public void setData(PatientData patientData) {
		String firstName = "";
		String lastName = "";
		String patronymicName = "";
		String birthDate = "";
		String diagnoseDate = "";
		String deathDate = "";
		boolean dead = false;
		String address = "";
		String anamnesis = "";
		currentPatientId = -1;

		if (patientData != null) {
			firstName = patientData.getFirstName();
			lastName = patientData.getLastName();
			patronymicName = patientData.getPatronymicName();
			if (patientData.getDateOfBirth() != null) {
				birthDate = dateFormat.format(patientData.getDateOfBirth());
			}
			if (patientData.getDateOfDiagnosis() != null) {
				diagnoseDate = dateFormat.format(patientData.getDateOfDiagnosis());
			}
			if (patientData.getDateOfDeath() != null) {
				deathDate = dateFormat.format(patientData.getDateOfDeath());
			}
			dead = patientData.isDead();
			address = patientData.getAddress();
			anamnesis = patientData.getAnamnesis();
			currentPatientId = patientData.getId();
		}

		medicationsTables.clear();
		Collection<Illness> illnessess = illnessesService.getAllIllnesses();
		for (Illness illness : illnessess) {
			List<Medication> patientsMedicationsForCurrentIllness;
			if (patientData != null) {
				List<Medication> allMedications = patientData.getPreviousTreatments();
				patientsMedicationsForCurrentIllness = CollectionsHelper.createFilteredList(allMedications, new MedicationIllnessFilter(illness));
			} else {
				patientsMedicationsForCurrentIllness = Collections.emptyList();
			}
			JExtendedTable<MedicationListTableModel> medicationsPerIllness = new JExtendedTable<MedicationListTableModel>(new MedicationListTableModel(
					patientsMedicationsForCurrentIllness));
			medicationsTables.put(illness, medicationsPerIllness);
		}
		recreateIllnessTabs();

		tfFirstName.setText(firstName);
		tfLastName.setText(lastName);
		tfPatronymicName.setText(patronymicName);
		tfBirthDate.setText(birthDate);
		tfDiagnoseDate.setText(diagnoseDate);
		tfDeathDate.setText(deathDate);
		cbDead.setSelected(dead);
		if (!dead) {
			tfDeathDate.setEnabled(false);
		} else {
			tfDeathDate.setEnabled(true);
		}
		taAddressText.setText(address);
		taAnamnesis.setText(anamnesis);

		updateTitle(currentLocale);
	}

}
