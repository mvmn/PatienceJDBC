package x.mvmn.patienceajdbc.gui.patients;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.springframework.context.MessageSource;

import x.mvmn.lang.Identifiable;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.l10n.Localizable;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.PatientStatsData;
import x.mvmn.patienceajdbc.service.PatientsService;

public class PatientsListPerIllness extends JPanel implements Localizable, Titled, Identifiable<Long> {

	private static final long serialVersionUID = 7130221932695416110L;
	private static final String LOCALIZATION_KEY_ILLNESS_TAB_NAME_ALL = "patients_per_illness_list_component.tabs.all.title";

	private final Long id;

	protected final PatientsListWindow patientsListWindow;

	protected final PatientsService patientsService;
	protected final Illness illnessToFilterBy;
	protected final JTable tblPatientsList;
	protected final PatientsListTableModel patientsListTableModel;

	protected final MessageSource messageSource;
	protected volatile Locale locale = Locale.US;

	public PatientsListPerIllness(final PatientsListWindow patientsListWindow, final MessageSource messageSource, final PatientsService patientsService,
			final Illness illnessToFilterBy) {
		this.patientsListWindow = patientsListWindow;
		this.messageSource = messageSource;
		this.patientsService = patientsService;
		this.illnessToFilterBy = illnessToFilterBy;
		if (illnessToFilterBy != null) {
			id = illnessToFilterBy.getId();
		} else {
			id = -1L;
		}

		List<PatientStatsData> patientsStatsData = loadData(illnessToFilterBy);
		patientsListTableModel = new PatientsListTableModel(patientsStatsData);
		tblPatientsList = new JTable(patientsListTableModel);
		tblPatientsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		tblPatientsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					PatientStatsData patientData = PatientsListPerIllness.this.getSingleSelectedItem();
					if (patientData != null) {
						patientsListWindow.openPatientDetail(patientData.getPatientId());
					}
				}
			}
		});

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tblPatientsList), BorderLayout.CENTER);
	}

	public PatientStatsData getSingleSelectedItem() {
		PatientStatsData result = null;
		int rowIndex = tblPatientsList.getSelectedRow();
		if (rowIndex >= 0 && rowIndex < patientsListTableModel.getRowCount()) {
			result = patientsListTableModel.getValue(rowIndex);
		}
		return result;
	}

	public PatientStatsData getPatientStatsData(int rowIndex) {
		return patientsListTableModel.getValue(rowIndex);
	}

	public void reloadData() {
		List<PatientStatsData> patientsStatsData = loadData(illnessToFilterBy);
		tblPatientsList.setModel(new PatientsListTableModel(patientsStatsData));
		// revalidate and repaint are already called by javax.swing.JTable
		// inside setModel method
	}

	protected List<PatientStatsData> loadData(Illness illnessToFilterBy) {
		long illnessId = illnessToFilterBy != null ? illnessToFilterBy.getId() : -1;
		List<PatientStatsData> patientsStatsData = this.patientsService.listStats(illnessId, null, null, 1);
		return patientsStatsData;
	}

	public Illness getIllnessToFilterBy() {
		return illnessToFilterBy;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		super.setLocale(locale);
	}

	public void setTitle(String title) {
	}

	@Override
	public String getTitle() {
		String title;
		if (this.illnessToFilterBy != null) {
			title = illnessToFilterBy.getName();
		} else {
			title = messageSource.getMessage(LOCALIZATION_KEY_ILLNESS_TAB_NAME_ALL, null, this.locale);
		}
		return title;
	}

	@Override
	public Long getId() {
		return id;
	}

	public JTable getTblPatientsList() {
		return tblPatientsList;
	}
}
