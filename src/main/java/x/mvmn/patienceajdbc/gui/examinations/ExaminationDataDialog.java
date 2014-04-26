package x.mvmn.patienceajdbc.gui.examinations;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.springframework.context.MessageSource;

import x.mvmn.gui.generic.awt.event.DefaultWindowListener;
import x.mvmn.gui.generic.swing.GeneralisedJTable;
import x.mvmn.patienceajdbc.gui.GeneralisedMutableTableModel;
import x.mvmn.patienceajdbc.gui.SwingHelper;
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
import x.mvmn.patienceajdbc.service.ExaminationsService;
import x.mvmn.patienceajdbc.service.IllnessesService;
import x.mvmn.patienceajdbc.service.PatientsService;

public class ExaminationDataDialog extends JDialog implements LocaleChangeAware, Titled {

	private static final long serialVersionUID = 6500734325958188610L;

	private final MessageSource messageSource;
	private volatile Locale currentLocale = Locale.US;

	private static final String LOCALIZATION_KEY_FIELD_BLOOD_ANALYSIS = "examination_dialog.field.blood_analysis";
	private static final String LOCALIZATION_KEY_BUTTON_SAVE = "examination_details_window.button.save";
	private static final String LOCALIZATION_KEY_BUTTON_CANCEL = "examination_details_window.button.cancel";

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

	protected final JButton btnSave = new JButton();
	protected final JButton btnCancel = new JButton();

	protected final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	protected final GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>> medicationsTable = new GeneralisedJTable<GeneralisedMutableTableModel<Medication, String>>();
	protected final MedicationChooserDialog medChooserDialog;

	protected final PatientDataDialog patientDataDialog;

	protected final PatientsService patientsService;
	protected final IllnessesService illnessesService;
	protected final ExaminationsService examinationsService;

	protected ExaminationData currentData;
	protected List<Medication> treatment;
	protected long currentPatientId = -1;
	protected long currentIllnessId = -1;

	public ExaminationDataDialog(final PatientDataDialog patientDataDialog, final PatientsService patientsService, final IllnessesService illnessesService,
			final ExaminationsService examinationsService, final MedicationChooserDialog medChooserDialog, final MessageSource messageSource) {
		super(patientDataDialog);
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
		JPanel secondFieldsSubPanel = new JPanel(new GridLayout(1, 2));
		secondFieldsSubPanel.add(tfCariotypeDesc);
		secondFieldsSubPanel.add(tfFishDesc);

		JPanel thirdFieldsSubPanel = new JPanel(new GridLayout(1, 2));
		{
			JScrollPane taCariotypeCommentsScrollPane = new JScrollPane(taCariotypeComments);
			taCariotypeCommentsScrollPane.setPreferredSize(taCariotypeCommentsScrollPane.getMinimumSize());
			taCariotypeCommentsScrollPane.setBorder(lbCariotypeComments);
			thirdFieldsSubPanel.add(taCariotypeCommentsScrollPane);
		}
		{
			JScrollPane taFishCommentsScrollPane = new JScrollPane(taFishComments);
			taFishCommentsScrollPane.setPreferredSize(taFishCommentsScrollPane.getMinimumSize());
			taFishCommentsScrollPane.setBorder(lbFishComments);
			thirdFieldsSubPanel.add(taFishCommentsScrollPane);
		}

		JPanel fieldsPanel = new JPanel(new GridLayout(2, 1));
		fieldsPanel.add(firstFieldsSubPanel);
		JPanel lastFieldsSubPanel = new JPanel(new BorderLayout());
		lastFieldsSubPanel.add(secondFieldsSubPanel, BorderLayout.NORTH);
		lastFieldsSubPanel.add(thirdFieldsSubPanel, BorderLayout.CENTER);
		fieldsPanel.add(lastFieldsSubPanel);

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
		{
			buttonsPanel.add(btnCancel);
			buttonsPanel.add(btnSave);
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ExaminationDataDialog.this.resetDataAndClose();
				}
			});
			btnSave.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						ExaminationData examData = ExaminationDataDialog.this.currentData;
						int number = Integer.parseInt(tfNumber.getText().trim());
						Date date = null;
						if (tfDate.getText().trim().length() > 0) {
							date = dateFormat.parse(tfDate.getText().trim());
						}
						if (examData == null) {
							examData = examinationsService.create(currentPatientId, currentIllnessId, number, tfMatherial.getText(), tfBlood.getText(),
									tfMielogramm.getText(), taTreatment.getText(), taComments.getText(), date,
									cbIllenssPhase.getModel().getElementAt(cbIllenssPhase.getSelectedIndex()));
							ExaminationDataDialog.this.currentData = examData;
						} else {
							examData.setNumber(number);
							examData.setMatherial(tfMatherial.getText());
							examData.setBlood(tfBlood.getText());
							examData.setMielogramm(tfMielogramm.getText());
							examData.setTreatmentDescription(taTreatment.getText());
							examData.setComments(taComments.getText());
							examData.setExaminationDate(date);
							examData.setPhase(cbIllenssPhase.getModel().getElementAt(cbIllenssPhase.getSelectedIndex()));
						}
						examData.setTreatment(treatment);
						examData.getCariotypeExaminationResults().setNomenclaturalDescription(tfCariotypeDesc.getText());
						examData.getCariotypeExaminationResults().setComments(taCariotypeComments.getText());
						examData.getFishExaminationResults().setNomenclaturalDescription(tfFishDesc.getText());
						examData.getFishExaminationResults().setComments(taFishComments.getText());
						examinationsService.update(examData, true);

						// TODO: signalize success
						ExaminationDataDialog.this.patientDataDialog.recreateIllnessTabs();
						ExaminationDataDialog.this.patientDataDialog.switchIllnessTab(ExaminationDataDialog.this.currentIllnessId);
						resetDataAndClose();
					} catch (Exception saveError) {
						saveError.printStackTrace();
						JOptionPane.showMessageDialog(ExaminationDataDialog.this, saveError.getClass().getSimpleName() + ": " + saveError.getMessage(),
								"Error occurred", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new DefaultWindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				ExaminationDataDialog.this.resetDataAndClose();
			}
		});

		JPanel medicationsPanel = new JPanel(new BorderLayout());
		{
			medicationsPanel.add(new JScrollPane(medicationsTable), BorderLayout.CENTER);
			JPanel medButtonsPanel = new JPanel(new BorderLayout());
			JButton addMedication = new JButton("+"); // TODO: localize
			addMedication.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Illness illness = illnessesService.getIllness(currentIllnessId);
					Medication med = medChooserDialog.chooseMedications(illness);
					if (med != null) {
						if (!treatment.contains(med)) {
							treatment.add(med);
							medicationsTable.getTableModel().add(med);
						}
					}
				}
			});
			JButton removeMedication = new JButton("-"); // TODO: localize
			removeMedication.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (medicationsTable.getSelectedColumns() != null) {
						int[] selectedIndicies = medicationsTable.getSelectedRows();
						for (int i = selectedIndicies.length - 1; i >= 0; i--) {
							int selectedIndex = selectedIndicies[i];
							Medication med = medicationsTable.getTableModel().remove(selectedIndex);
							treatment.remove(med);
						}
					}
				}
			});
			medButtonsPanel.add(removeMedication, BorderLayout.WEST);
			medButtonsPanel.add(addMedication, BorderLayout.EAST);
			medicationsPanel.add(medButtonsPanel, BorderLayout.SOUTH);
		}

		this.getContentPane().setLayout(new BorderLayout());
		fieldsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.getContentPane().add(fieldsPanel, BorderLayout.NORTH);
		medicationsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.getContentPane().add(medicationsPanel, BorderLayout.CENTER);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		this.setData(null);
		this.pack();
		SwingHelper.moveToScreenCenter(this);
	}

	protected void resetDataAndClose() {
		setData(-1, -1, null);
		this.setVisible(false);
	}

	@Override
	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier localeChangeNotifier) {
		localeChangeNotifier.registerLocaleChangeListener(this);
		setLocale(localeChangeNotifier.getLastSetLocale());
	}

	public void setData(long patientId, long illnessId) {
		setData(patientId, illnessId, null);
	}

	public void setData(ExaminationData data) {
		setData(data != null ? data.getPatientId() : -1, data != null ? data.getIllnessId() : -1, data);
	}

	protected void setData(long patientId, long illnessId, ExaminationData data) {
		this.currentData = data;
		this.currentPatientId = patientId;
		this.currentIllnessId = illnessId;

		this.tfDate.setText(data != null && data.getExaminationDate() != null ? dateFormat.format(data.getExaminationDate()) : "");
		this.tfNumber.setText(data != null ? String.valueOf(data.getNumber()) : String.valueOf(examinationsService.getLastExaminationNumber() + 1));
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

		populateMedicationsTable();
	}

	protected void populateMedicationsTable() {
		if (currentData != null) {
			treatment = examinationsService.get(currentData.getId(), true).getTreatment();
		} else {
			treatment = new ArrayList<Medication>();
		}
		medicationsTable.setModel(new GeneralisedMutableTableModel<Medication, String>(treatment, MedicationTableCellValueAdaptor.INSTANCE));
	}

	public void setLocale(Locale locale) {
		super.setLocale(locale);
		this.currentLocale = locale;
		if (messageSource != null && locale != null) {
			lbBlood.setTitle(messageSource.getMessage(LOCALIZATION_KEY_FIELD_BLOOD_ANALYSIS, null, locale));

			btnSave.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_SAVE, null, locale));
			btnCancel.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_CANCEL, null, locale));
		}
	}

	public void viewData(long patientId, long illnessId, ExaminationData examinationData) {
		setData(patientId, illnessId, examinationData != null ? examinationsService.get(examinationData.getId(), true) : null);
		this.pack();
		this.setVisible(true);
	}
}
