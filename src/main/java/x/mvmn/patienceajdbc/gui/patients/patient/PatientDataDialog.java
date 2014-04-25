package x.mvmn.patienceajdbc.gui.patients.patient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import org.springframework.context.MessageSource;

import x.mvmn.gui.generic.awt.event.DefaultWindowListener;
import x.mvmn.gui.generic.swing.ExtendedTitledBorder;
import x.mvmn.gui.generic.swing.JExtendedTabPane;
import x.mvmn.gui.generic.swing.JExtendedTable;
import x.mvmn.patienceajdbc.gui.GeneralisedMutableTableModel;
import x.mvmn.patienceajdbc.gui.IllnessSpecificPanel;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.examinations.ExaminationDataDialog;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.medication.MedicationChooserDialog;
import x.mvmn.patienceajdbc.gui.medication.MedicationTableCellValueAdaptor;
import x.mvmn.patienceajdbc.gui.patients.PatientsListWindow;
import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.service.ExaminationsService;
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

	protected final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	protected final JButton btnSave = new JButton();
	protected final JButton btnCancel = new JButton();

	protected volatile long currentPatientId = -1;

	protected final PatientsService patientsService;
	protected final IllnessesService illnessesService;
	protected final ExaminationsService examinationsService;

	protected final JExtendedTabPane<IllnessSpecificPanel> illnessTabs;
	protected final ConcurrentHashMap<Long, IllnessSpecificPanel> illnessIdToIllnessTab = new ConcurrentHashMap<Long, IllnessSpecificPanel>();

	protected final Map<Illness, JExtendedTable<GeneralisedMutableTableModel<Medication, String>>> medicationsTables = new ConcurrentHashMap<Illness, JExtendedTable<GeneralisedMutableTableModel<Medication, String>>>();
	protected final MedicationChooserDialog medChooserDialog;
	protected final ExaminationDataDialog examinationDataDialog;
	protected final PatientsListWindow patientsListWindow;

	public PatientDataDialog(final PatientsService patientsService, final IllnessesService illnessesService, final MedicationService medicationService,
			final ExaminationsService examinationsService, final PatientsListWindow patientsListWindow, final MessageSource messageSource) {
		this.patientsService = patientsService;
		this.illnessesService = illnessesService;
		this.patientsListWindow = patientsListWindow;
		this.messageSource = messageSource;
		this.examinationsService = examinationsService;

		illnessTabs = new JExtendedTabPane<IllnessSpecificPanel>();

		setData(null);
		this.setModal(false);

		medChooserDialog = new MedicationChooserDialog(medicationService, messageSource);
		examinationDataDialog = new ExaminationDataDialog(this, patientsService, illnessesService, examinationsService, medChooserDialog, messageSource);

		JPanel fieldsPanel = new JPanel(new GridLayout(1, 4));
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
			{
				JScrollPane taAddressTextScrollPane = new JScrollPane(taAddressText);
				taAddressTextScrollPane.setPreferredSize(taAddressTextScrollPane.getMinimumSize());
				taAddressTextScrollPane.setBorder(lblAddressText);
				fieldsPanel.add(taAddressTextScrollPane);
			}
			{
				JScrollPane taAnamnesisScrollPane = new JScrollPane(taAnamnesis);
				taAnamnesisScrollPane.setPreferredSize(taAnamnesisScrollPane.getMinimumSize());
				taAnamnesisScrollPane.setBorder(lblAnamnesis);
				fieldsPanel.add(taAnamnesisScrollPane);
			}
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
						if (tfBirthDate.getText().trim().length() > 0) {
							try {
								birthDate = dateFormat.parse(tfBirthDate.getText());
							} catch (ParseException e1) {
								// TODO: Signalize validation error and return
								throw e1;
							}
						}
						Date diagnoseDate = null;
						if (tfDiagnoseDate.getText().trim().length() > 0) {
							try {
								diagnoseDate = dateFormat.parse(tfDiagnoseDate.getText());
							} catch (ParseException e1) {
								// TODO: Signalize validation error and return
								throw e1;
							}
						}
						Date deathDate = null;
						if (tfDeathDate.getText().trim().length() > 0) {
							try {
								deathDate = dateFormat.parse(tfDeathDate.getText());
							} catch (ParseException e1) {
								// TODO: Signalize validation error and return
								throw e1;
							}
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
						// TODO: localize
						saveError.printStackTrace();
						JOptionPane.showMessageDialog(PatientDataDialog.this, saveError.getClass().getSimpleName() + ": " + saveError.getMessage(),
								"Error occurred", JOptionPane.ERROR_MESSAGE);
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

	public void recreateIllnessTabs(PatientData patientData) {
		illnessTabs.removeAll();
		medicationsTables.clear();
		illnessIdToIllnessTab.clear();
		if (patientData != null) {
			Collection<Illness> illnessess = illnessesService.getAllIllnesses();
			for (Illness illness : illnessess) {
				List<Medication> patientsMedicationsForCurrentIllness;
				if (patientData != null) {
					List<Medication> allMedications = patientData.getPreviousTreatments();
					patientsMedicationsForCurrentIllness = CollectionsHelper.createFilteredList(allMedications, new MedicationIllnessFilter(illness));
				} else {
					patientsMedicationsForCurrentIllness = Collections.emptyList();
				}
				JExtendedTable<GeneralisedMutableTableModel<Medication, String>> medicationsPerIllness = new JExtendedTable<GeneralisedMutableTableModel<Medication, String>>(
						new GeneralisedMutableTableModel<Medication, String>(patientsMedicationsForCurrentIllness, new MedicationTableCellValueAdaptor()));
				medicationsTables.put(illness, medicationsPerIllness);
			}
			for (final Illness illness : illnessess) {
				JPanel medicationsPanel = new JPanel(new BorderLayout());
				final JExtendedTable<GeneralisedMutableTableModel<Medication, String>> table = medicationsTables.get(illness);
				medicationsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
				JPanel buttonsPanel = new JPanel(new BorderLayout());
				JButton addMedication = new JButton("+"); // FIXME: localize
				addMedication.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Medication med = medChooserDialog.chooseMedications(illness);
						if (med != null) {
							PatientData patientData = patientsService.get(currentPatientId, true);
							if (patientData != null) {
								List<Medication> medications = patientData.getPreviousTreatments();
								if (!medications.contains(med)) {
									medications.add(med);
									patientsService.update(patientData, true);
									table.getTableModel().add(med);
								}
							}
						}
					}
				});
				JButton removeMedication = new JButton("-"); // FIXME: localize
				removeMedication.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (table.getSelectedColumns() != null) {
							int[] selectedIndicies = table.getSelectedRows();
							for (int i = selectedIndicies.length - 1; i >= 0; i--) {
								int selectedIndex = selectedIndicies[i];
								Medication med = table.getTableModel().remove(selectedIndex);
								PatientData patientData = patientsService.get(currentPatientId, true);
								if (patientData != null) {
									List<Medication> medications = patientData.getPreviousTreatments();
									medications.remove(med);
									patientsService.update(patientData, true);
								}
							}
						}
					}
				});
				buttonsPanel.add(removeMedication, BorderLayout.WEST);
				buttonsPanel.add(addMedication, BorderLayout.EAST);
				medicationsPanel.add(buttonsPanel, BorderLayout.SOUTH);

				IllnessSpecificPanel tabContent = new IllnessSpecificPanel(illness, new GridLayout(2, 1));
				tabContent.add(medicationsPanel);

				// examinationsService.create(this.currentPatientId, illness.getId(), (int) examinationsService.countAll() + 1, "Matherial", "blood",
				// "mielogramm",
				// "treatmentDescription", "comments", new Date(), IllnessPhase.CHRONIC, "typeName", "nomenclaturalDescription", "examinationComments");
				final List<ExaminationData> examinations = examinationsService.getByPatientAndIllness(this.currentPatientId, illness.getId());
				final JTable examinationsTable = new JTable(new AbstractTableModel() {

					private static final long serialVersionUID = -824123649739620125L;

					@Override
					public Object getValueAt(int rowIndex, int columnIndex) {
						ExaminationData examData = examinations.get(rowIndex);
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
					public int getRowCount() {
						return examinations.size();
					}

					@Override
					public int getColumnCount() {
						return 3;
					}

					// TODO: localize
					private final String[] COLUMN_NAMES = { "Date", "Number", "Comment" };

					public String getColumnName(int col) {
						return COLUMN_NAMES[col];
					}
				});
				examinationsTable.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent mouseEvent) {
						if (mouseEvent.getClickCount() == 2) {
							int selectedRow = examinationsTable.getSelectedRow();
							if (selectedRow >= 0 && selectedRow < examinations.size()) {
								ExaminationData examinationData = examinations.get(examinationsTable.getSelectedRow());
								if (examinationsTable != null) {
									PatientDataDialog.this.openExaminationDataDetails(examinationData);
								}
							}
						}
					}
				});

				tabContent.add(new JScrollPane(examinationsTable));
				illnessTabs.addTab(illness.getName(), tabContent);
				illnessIdToIllnessTab.put(illness.getId(), tabContent);
			}
		} else {
			IllnessSpecificPanel panel = new IllnessSpecificPanel(null);
			// TODO: localize
			JLabel msgLabel = new JLabel("You need to save patient before you can add medications and examinations.");
			msgLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			msgLabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
			panel.setOpaque(false);
			panel.add(msgLabel);
			illnessTabs.addTab("Patient not saved", panel);
		}
	}

	protected void openExaminationDataDetails(ExaminationData examinationData) {
		Long illnessId = null;
		if (illnessTabs.getSelectedComponent() != null && illnessTabs.getSelectedComponent().getIllness() != null) {
			illnessId = illnessTabs.getSelectedComponent().getIllness().getId();
		}
		examinationDataDialog.viewData(examinationData, illnessId);
	}

	protected void resetDataAndClose() {
		this.setData(null);
		this.setVisible(false);
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier localeChangeNotifier) {
		localeChangeNotifier.registerLocaleChangeListener(this);
		medChooserDialog.setSelfAsLocaleChangeListener(localeChangeNotifier);
		examinationDataDialog.setSelfAsLocaleChangeListener(localeChangeNotifier);
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

			// medChooserDialog.setLocale(locale);
			// examinationDataDialog.setLocale(locale);
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

		recreateIllnessTabs(patientData);

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

	public void switchIllnessTab(long illnessId) {
		IllnessSpecificPanel tab = illnessIdToIllnessTab.get(illnessId);
		if (tab != null) {
			this.illnessTabs.setSelectedComponent(tab);
		}
	}

}
