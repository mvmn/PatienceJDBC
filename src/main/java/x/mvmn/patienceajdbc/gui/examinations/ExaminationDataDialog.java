package x.mvmn.patienceajdbc.gui.examinations;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.springframework.context.MessageSource;

import x.mvmn.gui.generic.swing.JExtendedTabPane;
import x.mvmn.gui.generic.swing.GeneralisedJTable;
import x.mvmn.patienceajdbc.gui.GeneralisedMutableTableModel;
import x.mvmn.patienceajdbc.gui.IllnessSpecificPanel;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.medication.MedicationChooserDialog;
import x.mvmn.patienceajdbc.gui.medication.MedicationTableCellValueAdaptor;
import x.mvmn.patienceajdbc.gui.patients.patient.PatientDataDialog;
import x.mvmn.patienceajdbc.model.ExaminationData;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.IllnessPhase;
import x.mvmn.patienceajdbc.model.Medication;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.service.ExaminationsService;
import x.mvmn.patienceajdbc.service.IllnessesService;
import x.mvmn.patienceajdbc.service.PatientsService;
import x.mvmn.patienceajdbc.util.collections.MedicationIllnessFilter;
import x.mvmn.util.collections.CollectionsHelper;

public class ExaminationDataDialog extends JDialog implements LocaleChangeAware, Titled {

	private static final long serialVersionUID = 6500734325958188610L;

	private ExaminationData currentData;

	private final MessageSource messageSource;
	private volatile Locale currentLocale = Locale.US;

	private static final String LOCALIZATION_KEY_FIELD_BLOOD_ANALYSIS = "examination_dialog.field.blood_analysis";

	protected final TitledBorder lbDate = new TitledBorder("");
	protected final JTextField tfDate = new JTextField();
	protected final TitledBorder lbNumber = new TitledBorder("");
	protected final JTextField tfNumber = new JTextField();
	protected final TitledBorder lbMatherial = new TitledBorder("");
	protected final JTextField tfMatherial = new JTextField();
	protected final TitledBorder lbBlood = new TitledBorder("");
	protected final JTextField tfBlood = new JTextField();
	protected final TitledBorder lbMielogramm = new TitledBorder("");
	protected final JTextField tfMielogramm = new JTextField();
	protected final TitledBorder lbTreatment = new TitledBorder("");
	protected final JTextArea taTreatment = new JTextArea();
	protected final TitledBorder lbComments = new TitledBorder("");
	protected final JTextArea taComments = new JTextArea();

	protected final TitledBorder lbIllnessPhase = new TitledBorder("");
	protected final JComboBox<IllnessPhase> cbIllenssPhase;

	protected final TitledBorder lbCariotypeDesc = new TitledBorder("");
	protected final JTextField tfCariotypeDesc = new JTextField();
	protected final TitledBorder lbCariotypeComments = new TitledBorder("");
	protected final JTextArea taCariotypeComments = new JTextArea();

	protected final TitledBorder lbFishDesc = new TitledBorder("");
	protected final JTextField tfFishDesc = new JTextField();
	protected final TitledBorder lbFishComments = new TitledBorder("");
	protected final JTextArea taFishComments = new JTextArea();

	protected final JExtendedTabPane<IllnessSpecificPanel> illnessTabs = new JExtendedTabPane<IllnessSpecificPanel>();
	protected final ConcurrentHashMap<Long, IllnessSpecificPanel> illnessIdToIllnessTab = new ConcurrentHashMap<Long, IllnessSpecificPanel>();

	protected final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	protected final Map<Illness, GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>> medicationsTables = new ConcurrentHashMap<Illness, GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>>();
	protected final MedicationChooserDialog medChooserDialog;

	protected final PatientsService patientsService;
	protected final IllnessesService illnessesService;
	protected final ExaminationsService examinationsService;

	protected final PatientDataDialog patientDataDialog;

	public ExaminationDataDialog(final PatientDataDialog patientDataDialog, final PatientsService patientsService, final IllnessesService illnessesService,
			final ExaminationsService examinationsService, final MedicationChooserDialog medChooserDialog, final MessageSource messageSource) {
		this.patientsService = patientsService;
		this.illnessesService = illnessesService;
		this.examinationsService = examinationsService;
		this.medChooserDialog = medChooserDialog;
		this.patientDataDialog = patientDataDialog;
		this.messageSource = messageSource;

		this.setModal(true);

		tfDate.setBorder(lbDate);
		tfNumber.setBorder(lbNumber);
		tfMatherial.setBorder(lbMatherial);
		tfBlood.setBorder(lbBlood);
		tfMielogramm.setBorder(lbMielogramm);

		tfCariotypeDesc.setBorder(lbCariotypeDesc);
		tfFishDesc.setBorder(lbFishDesc);

		cbIllenssPhase = new JComboBox<IllnessPhase>(new IllnessPhase[] { IllnessPhase.UNSET, IllnessPhase.CHRONIC, IllnessPhase.ACCELERATION,
				IllnessPhase.BLAST_CRISIS });
		cbIllenssPhase.setBorder(lbIllnessPhase);

		JPanel firstFieldsSubPanel = new JPanel(new GridLayout(1, 4));
		firstFieldsSubPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		{
			JPanel generalFieldsPanel = new JPanel(new GridLayout(3, 1));
			generalFieldsPanel.add(tfDate);
			generalFieldsPanel.add(tfNumber);
			generalFieldsPanel.add(tfMatherial);
			firstFieldsSubPanel.add(generalFieldsPanel);
		}
		{
			JPanel secondaryFieldsPanel = new JPanel(new GridLayout(3, 1));
			secondaryFieldsPanel.add(tfBlood);
			secondaryFieldsPanel.add(tfMielogramm);
			secondaryFieldsPanel.add(cbIllenssPhase);
			firstFieldsSubPanel.add(secondaryFieldsPanel);
		}
		{
			JScrollPane taTreatmentTextScrollPane = new JScrollPane(taTreatment);
			taTreatmentTextScrollPane.setPreferredSize(taTreatmentTextScrollPane.getMinimumSize());
			taTreatmentTextScrollPane.setBorder(lbTreatment);
			firstFieldsSubPanel.add(taTreatmentTextScrollPane);

		}
		{
			JScrollPane taCommentsScrollPane = new JScrollPane(taComments);
			taCommentsScrollPane.setPreferredSize(taCommentsScrollPane.getMinimumSize());
			taCommentsScrollPane.setBorder(lbComments);
			firstFieldsSubPanel.add(taCommentsScrollPane);
		}
		JPanel secondFieldsSubPanel = new JPanel(new GridLayout(2, 2));
		secondFieldsSubPanel.add(tfCariotypeDesc);
		secondFieldsSubPanel.add(tfFishDesc);
		{
			JScrollPane taCariotypeCommentsScrollPane = new JScrollPane(taCariotypeComments);
			taCariotypeCommentsScrollPane.setPreferredSize(taCariotypeCommentsScrollPane.getMinimumSize());
			taCariotypeCommentsScrollPane.setBorder(lbCariotypeComments);
			secondFieldsSubPanel.add(taCariotypeCommentsScrollPane);
		}
		{
			JScrollPane taFishCommentsScrollPane = new JScrollPane(taFishComments);
			taFishCommentsScrollPane.setPreferredSize(taFishCommentsScrollPane.getMinimumSize());
			taFishCommentsScrollPane.setBorder(lbFishComments);
			secondFieldsSubPanel.add(taFishCommentsScrollPane);
		}

		JPanel fieldsPanel = new JPanel(new GridLayout(2, 1));
		fieldsPanel.add(firstFieldsSubPanel);
		fieldsPanel.add(secondFieldsSubPanel);

		JPanel buttonsPanel = new JPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(fieldsPanel, BorderLayout.NORTH);
		this.getContentPane().add(illnessTabs.getUnderlyingComponent(), BorderLayout.CENTER);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		this.pack();
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier localeChangeNotifier) {
		localeChangeNotifier.registerLocaleChangeListener(this);
		setLocale(localeChangeNotifier.getLastSetLocale());
	}

	public void setData(ExaminationData data) {
		this.currentData = data;

		this.tfDate.setText(data != null ? dateFormat.format(data.getExaminationDate()) : "");
		this.tfNumber.setText(data != null ? String.valueOf(data.getNumber()) : "");
		this.tfMatherial.setText(data != null ? data.getMatherial() : "");
		this.tfBlood.setText(data != null ? data.getBlood() : "");
		this.tfMielogramm.setText(data != null ? data.getMielogramm() : "");
		this.taTreatment.setText(data != null ? data.getTreatmentDescription() : "");
		this.taComments.setText(data != null ? data.getComments() : "");

		this.cbIllenssPhase.setSelectedItem(data != null ? data.getPhase() : IllnessPhase.UNSET);

		this.tfCariotypeDesc.setText("");
		this.taCariotypeComments.setText("");
		if (data != null && data.getCariotypeExaminationResults() != null) {
			this.tfCariotypeDesc.setText(data.getCariotypeExaminationResults().getNomenclaturalDescription());
			this.taCariotypeComments.setText(data.getCariotypeExaminationResults().getNomenclaturalDescription());
		}

		this.tfFishDesc.setText("");
		this.taFishComments.setText("");
		if (data != null && data.getFishExaminationResults() != null) {
			this.tfFishDesc.setText(data.getFishExaminationResults().getNomenclaturalDescription());
			this.taFishComments.setText(data.getFishExaminationResults().getNomenclaturalDescription());
		}

		recreateIllnessTabs();
	}

	protected void recreateIllnessTabs() {
		illnessTabs.removeAll();
		illnessIdToIllnessTab.clear();
		medicationsTables.clear();
		Collection<Illness> illnessess = illnessesService.getAllIllnesses();
		for (Illness illness : illnessess) {
			List<Medication> patientsMedicationsForCurrentIllness = Collections.emptyList();
			if (currentData != null) {
				PatientData patientData = patientsService.get(currentData.getPatientId(), true);
				if (patientData != null) {
					List<Medication> allMedications = patientData.getPreviousTreatments();
					patientsMedicationsForCurrentIllness = CollectionsHelper.createFilteredList(allMedications, new MedicationIllnessFilter(illness));
				}
			}
			GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>> medicationsPerIllness = new GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>(
					new GeneralisedMutableTableModel<Medication, String>(patientsMedicationsForCurrentIllness, MedicationTableCellValueAdaptor.INSTANCE));
			medicationsTables.put(illness, medicationsPerIllness);
		}
		if (currentData != null) {
			for (final Illness illness : illnessess) {
				IllnessSpecificPanel medicationsPanel = new IllnessSpecificPanel(illness, new BorderLayout());
				final GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>> table = medicationsTables.get(illness);
				medicationsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
				JPanel buttonsPanel = new JPanel(new BorderLayout());
				JButton addMedication = new JButton("+"); // FIXME: localize
				addMedication.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Medication med = medChooserDialog.chooseMedications(illness);
						if (med != null) {
							PatientData patientData = patientsService.get(currentData.getPatientId(), true);
							if (patientData != null) {
								List<Medication> medications = patientData.getPreviousTreatments();
								if (!medications.contains(med)) {
									medications.add(med);
									patientsService.update(patientData, true);
									table.getTableModel().add(med);
									ExaminationDataDialog.this.patientDataDialog.recreateIllnessTabs(patientData);
									ExaminationDataDialog.this.patientDataDialog.switchIllnessTab(med.getIllnessId());
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
								PatientData patientData = patientsService.get(currentData.getPatientId(), true);
								if (patientData != null) {
									List<Medication> medications = patientData.getPreviousTreatments();
									medications.remove(med);
									patientsService.update(patientData, true);
									ExaminationDataDialog.this.patientDataDialog.recreateIllnessTabs(patientData);
									ExaminationDataDialog.this.patientDataDialog.switchIllnessTab(med.getIllnessId());
								}
							}
						}
					}
				});
				buttonsPanel.add(removeMedication, BorderLayout.WEST);
				buttonsPanel.add(addMedication, BorderLayout.EAST);
				medicationsPanel.add(buttonsPanel, BorderLayout.SOUTH);

				illnessTabs.addTab(illness.getName(), medicationsPanel);
				illnessIdToIllnessTab.put(illness.getId(), medicationsPanel);
			}
		}
	}

	public void switchIllnessTab(long illnessId) {
		IllnessSpecificPanel tab = illnessIdToIllnessTab.get(illnessId);
		if (tab != null) {
			this.illnessTabs.setSelectedComponent(tab);
		}
	}

	public void setLocale(Locale locale) {
		super.setLocale(locale);
		this.currentLocale = locale;
		if (messageSource != null && locale != null) {
			lbBlood.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_BLOOD_ANALYSIS, null, locale));
		}
	}

	public void viewData(ExaminationData examinationData, Long selectedIllnessId) {
		setData(examinationData);
		if (selectedIllnessId != null) {
			switchIllnessTab(selectedIllnessId.longValue());
		}
		this.pack();
		this.setVisible(true);
	}
}
