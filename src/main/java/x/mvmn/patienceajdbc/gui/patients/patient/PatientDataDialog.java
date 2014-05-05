package x.mvmn.patienceajdbc.gui.patients.patient;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.springframework.context.MessageSource;

import x.mvmn.gui.generic.awt.event.DefaultWindowListener;
import x.mvmn.gui.generic.swing.ExtendedTitledBorder;
import x.mvmn.gui.generic.swing.GeneralisedJTable;
import x.mvmn.gui.generic.swing.JExtendedTabPane;
import x.mvmn.lang.container.TupleOfThree;
import x.mvmn.patienceajdbc.gui.DatePanel;
import x.mvmn.patienceajdbc.gui.GeneralisedMutableTableModel;
import x.mvmn.patienceajdbc.gui.IllnessSpecificPanel;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.examinations.ExaminationCellValueAdaptor;
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

	protected final DatePanel birthDatePanel = new DatePanel();
	protected final DatePanel diagnosisDatePanel = new DatePanel();
	protected final DatePanel deathDatePanel = new DatePanel();

	protected final JCheckBox cbDead = new JCheckBox();

	protected final TitledBorder lblBirthDate = new TitledBorder("");
	protected final TitledBorder lblDiagnosisDate = new TitledBorder("");
	protected final JLabel lblDeathDate = new JLabel();
	protected final TitledBorder lblDead = new TitledBorder("");

	protected final JTextArea taAddressText = new JTextArea();
	protected final JTextArea taAnamnesis = new JTextArea();

	protected final TitledBorder lblAddressText = new TitledBorder("");
	protected final TitledBorder lblAnamnesis = new TitledBorder("");

	protected final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	protected final JButton btnSave = new JButton();
	protected final JButton btnCancel = new JButton();

	protected volatile PatientData currentPatientData;

	protected final PatientsService patientsService;
	protected final IllnessesService illnessesService;
	protected final ExaminationsService examinationsService;

	protected final JExtendedTabPane<IllnessSpecificPanel> illnessTabs;
	protected final ConcurrentHashMap<Long, IllnessSpecificPanel> illnessIdToIllnessTab = new ConcurrentHashMap<Long, IllnessSpecificPanel>();

	protected final Map<Illness, GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>> medicationsTables = new ConcurrentHashMap<Illness, GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>>();
	protected final MedicationChooserDialog medChooserDialog;
	protected final ExaminationDataDialog examinationDataDialog;
	protected final PatientsListWindow patientsListWindow;

	private long lastSelectedIllnessId = 0;

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
				shortFieldsPanel.add(tfLastName);
				shortFieldsPanel.add(tfFirstName);
				shortFieldsPanel.add(tfPatronymicName);
				fieldsPanel.add(shortFieldsPanel);
			}

			JPanel dateFieldsPanel = new JPanel(new GridLayout(3, 1));
			birthDatePanel.setBorder(lblBirthDate);
			dateFieldsPanel.add(birthDatePanel);
			diagnosisDatePanel.setBorder(lblDiagnosisDate);
			dateFieldsPanel.add(diagnosisDatePanel);

			{
				deathDatePanel.setBorder(new ExtendedTitledBorder(cbDead, deathDatePanel));
				dateFieldsPanel.add(deathDatePanel);
			}

			fieldsPanel.add(dateFieldsPanel);
		}

		// {
		// Dimension prefSize = tfDeathDateYear.getPreferredSize();
		// prefSize.setSize(100, prefSize.getHeight());
		// tfDeathDateYear.setPreferredSize(prefSize); // Superficial? Maybe.
		// }

		cbDead.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actEvent) {
				deathDatePanel.setEnabled(cbDead.isSelected());
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
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
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
						String firstName = tfFirstName.getText();
						String lastName = tfLastName.getText();
						String patronymicName = tfPatronymicName.getText();
						TupleOfThree<Integer, Integer, Integer> birthDate = birthDatePanel.getEnteredDate();
						TupleOfThree<Integer, Integer, Integer> diagnosisDate = diagnosisDatePanel.getEnteredDate();
						TupleOfThree<Integer, Integer, Integer> deathDate = deathDatePanel.getEnteredDate();
						boolean dead = cbDead.isSelected();
						String address = taAddressText.getText();
						String anamnesis = taAnamnesis.getText();

						boolean newlyCreated = false;

						PatientData patientData = currentPatientData;
						if (patientData != null && patientData.getId() >= 0) {
							patientData.setLastName(lastName);
							patientData.setFirstName(firstName);
							patientData.setPatronymicName(patronymicName);
							patientData.setAddress(address);
							patientData.setAnamnesis(anamnesis);

							patientData.setBirthDateYear(birthDate.getFirst());
							patientData.setBirthDateMonth(birthDate.getSecond());
							patientData.setBirthDateDay(birthDate.getThird());

							patientData.setDiagnosisDateYear(diagnosisDate.getFirst());
							patientData.setDiagnosisDateMonth(diagnosisDate.getSecond());
							patientData.setDiagnosisDateDay(diagnosisDate.getThird());

							patientData.setDeathDateYear(deathDate.getFirst());
							patientData.setDeathDateMonth(deathDate.getSecond());
							patientData.setDeathDateDay(deathDate.getThird());

							patientData.setDead(dead);
							patientsService.update(patientData, false);
						} else {
							newlyCreated = true;
							patientData = patientsService.create(lastName, firstName, patronymicName, address, birthDate.getFirst(), birthDate.getSecond(),
									birthDate.getThird(), diagnosisDate.getFirst(), diagnosisDate.getSecond(), diagnosisDate.getThird(), deathDate.getFirst(),
									deathDate.getSecond(), deathDate.getThird(), dead, anamnesis);
							PatientDataDialog.this.setData(patientData);
						}

						// TODO: Signalize successfull save
						patientsListWindow.recreatePerIllnessListsTabs();
						if (!newlyCreated) {
							resetDataAndClose();
						} else {
							PatientDataDialog.this.pack();
							switchIllnessTab(lastSelectedIllnessId);
						}
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

	public void recreateIllnessTabs() {
		illnessTabs.removeAll();
		medicationsTables.clear();
		illnessIdToIllnessTab.clear();
		if (currentPatientData != null) {
			Collection<Illness> illnessess = illnessesService.getAllIllnesses();
			for (Illness illness : illnessess) {
				List<Medication> patientsMedicationsForCurrentIllness;
				if (currentPatientData != null) {
					List<Medication> allMedications = currentPatientData.getPreviousTreatments();
					patientsMedicationsForCurrentIllness = CollectionsHelper.createFilteredList(allMedications, new MedicationIllnessFilter(illness));
				} else {
					patientsMedicationsForCurrentIllness = Collections.emptyList();
				}
				GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>> medicationsPerIllness = new GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>(
						new GeneralisedMutableTableModel<Medication, String>(patientsMedicationsForCurrentIllness, MedicationTableCellValueAdaptor.INSTANCE));
				medicationsTables.put(illness, medicationsPerIllness);
			}
			for (final Illness illness : illnessess) {
				IllnessSpecificPanel tabContent = new IllnessSpecificPanel(illness, new GridLayout(2, 1));
				{
					JPanel medicationsPanel = new JPanel(new BorderLayout());
					final GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>> table = medicationsTables.get(illness);
					medicationsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
					JPanel buttonsPanel = new JPanel(new BorderLayout());
					JButton addMedication = new JButton("+"); // TODO: localize
					addMedication.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Medication med = medChooserDialog.chooseMedications(illness);
							if (med != null && currentPatientData != null) {
								PatientData patientData = patientsService.get(currentPatientData.getId(), true);
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
					JButton removeMedication = new JButton("-"); // TODO: localize
					removeMedication.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (table.getSelectedColumns() != null) {
								int[] selectedIndicies = table.getSelectedRows();
								for (int i = selectedIndicies.length - 1; i >= 0; i--) {
									int selectedIndex = selectedIndicies[i];
									Medication med = table.getTableModel().remove(selectedIndex);
									if (currentPatientData != null) {
										PatientData patientData = patientsService.get(currentPatientData.getId(), true);
										if (patientData != null) {
											List<Medication> medications = patientData.getPreviousTreatments();
											medications.remove(med);
											patientsService.update(patientData, true);
										}
									}
								}
							}
						}
					});
					buttonsPanel.add(removeMedication, BorderLayout.EAST);
					buttonsPanel.add(addMedication, BorderLayout.WEST);
					medicationsPanel.add(buttonsPanel, BorderLayout.SOUTH);

					tabContent.add(medicationsPanel);
				}

				{
					JPanel examinationsPanel = new JPanel(new BorderLayout());
					long patientId = -1;
					if (currentPatientData != null) {
						patientId = currentPatientData.getId();
					}
					final List<ExaminationData> examinations = examinationsService.getByPatientAndIllness(patientId, illness.getId(), false);
					final GeneralisedJTable<GeneralisedMutableTableModel<ExaminationData, String>> examinationsTable = new GeneralisedJTable<GeneralisedMutableTableModel<ExaminationData, String>>(
							new GeneralisedMutableTableModel<ExaminationData, String>(examinations, ExaminationCellValueAdaptor.INSTANCE));
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

					examinationsPanel.add(new JScrollPane(examinationsTable), BorderLayout.CENTER);
					JPanel buttonsPanel = new JPanel(new BorderLayout());
					JButton addExamination = new JButton("+"); // TODO: localize
					JButton removeExamination = new JButton("-"); // TODO: localize

					addExamination.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent actEvent) {
							PatientDataDialog.this.openExaminationDataDetails(null);
						}
					});
					removeExamination.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int[] selectedIndicies = examinationsTable.getSelectedRows();
							for (int i = selectedIndicies.length - 1; i >= 0; i--) {
								int selectedIndex = selectedIndicies[i];
								ExaminationData examinationData = examinationsTable.getTableModel().remove(selectedIndex);
								examinationsService.delete(examinationData.getId());
							}
						}
					});

					buttonsPanel.add(removeExamination, BorderLayout.EAST);
					buttonsPanel.add(addExamination, BorderLayout.WEST);
					examinationsPanel.add(buttonsPanel, BorderLayout.SOUTH);

					tabContent.add(examinationsPanel);
				}
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
		long illnessId = illnessTabs.getSelectedComponent().getIllness().getId();
		examinationDataDialog.viewData(currentPatientData.getId(), illnessId, examinationData);
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
		this.setTitle(titlePrefix + ": "
				+ (currentPatientData != null && currentPatientData.getId() >= 0 ? String.valueOf(currentPatientData.getId()) : newPatientMessage));
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
			lblDiagnosisDate.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_DIAGNOSE_DATE, null, locale));
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
		this.currentPatientData = patientData;
		String firstName = "";
		String lastName = "";
		String patronymicName = "";
		boolean dead = false;
		String address = "";
		String anamnesis = "";

		if (patientData != null) {
			firstName = patientData.getFirstName();
			lastName = patientData.getLastName();
			patronymicName = patientData.getPatronymicName();
			dead = patientData.isDead();
			address = patientData.getAddress();
			anamnesis = patientData.getAnamnesis();

			birthDatePanel.setValues(patientData.getBirthDateYear(), patientData.getBirthDateMonth(), patientData.getBirthDateDay());
			diagnosisDatePanel.setValues(patientData.getDiagnosisDateYear(), patientData.getDiagnosisDateMonth(), patientData.getDiagnosisDateDay());
			deathDatePanel.setValues(patientData.getDeathDateYear(), patientData.getDeathDateMonth(), patientData.getDeathDateDay());
		} else {
			birthDatePanel.setValues("", 0, 0);
			diagnosisDatePanel.setValues("", 0, 0);
			deathDatePanel.setValues("", 0, 0);
		}

		recreateIllnessTabs();

		tfFirstName.setText(firstName);
		tfLastName.setText(lastName);
		tfPatronymicName.setText(patronymicName);
		cbDead.setSelected(dead);
		deathDatePanel.setEnabled(dead);
		taAddressText.setText(address);
		taAnamnesis.setText(anamnesis);

		updateTitle(currentLocale);
	}

	public void switchIllnessTab(long illnessId) {
		this.lastSelectedIllnessId = illnessId;
		IllnessSpecificPanel tab = illnessIdToIllnessTab.get(illnessId);
		if (tab != null) {
			this.illnessTabs.setSelectedComponent(tab);
		}
	}

}
