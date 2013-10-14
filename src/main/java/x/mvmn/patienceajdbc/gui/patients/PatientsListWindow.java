package x.mvmn.patienceajdbc.gui.patients;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.springframework.context.MessageSource;

import x.mvmn.gui.generic.swing.JExtendedTabPane;
import x.mvmn.patienceajdbc.PatienceA;
import x.mvmn.patienceajdbc.gui.Menued;
import x.mvmn.patienceajdbc.gui.SwingHelper;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.patients.patient.PatientDataDialog;
import x.mvmn.patienceajdbc.model.Illness;
import x.mvmn.patienceajdbc.model.PatientData;
import x.mvmn.patienceajdbc.model.PatientStatsData;
import x.mvmn.patienceajdbc.service.IllnessesService;
import x.mvmn.patienceajdbc.service.MedicationService;
import x.mvmn.patienceajdbc.service.PatientsService;

public class PatientsListWindow extends JFrame implements IllnessesService.IllnessesChangeListener, LocaleChangeAware, Menued, Titled {

	private static final long serialVersionUID = -8458654717343536602L;

	private static final String LOCALIZATION_KEY_WINDOW_TITLE = "patients_list_window.title";
	private static final String LOCALIZATION_KEY_FILTER_DATE_FROM = "patients_list_window.filter.date.from";
	private static final String LOCALIZATION_KEY_FILTER_DATE_TO = "patients_list_window.filter.date.to";
	private static final String LOCALIZATION_KEY_FILTER_KEYWORD = "patients_list_window.filter.keyword";
	private static final String LOCALIZATION_KEY_FILTER_APPLY = "patients_list_window.button.filter.apply";
	private static final String LOCALIZATION_KEY_ADD_PATIENT = "patients_list_window.button.patient.add";
	private static final String LOCALIZATION_KEY_EDIT_PATIENT = "patients_list_window.button.patient.edit";
	private static final String LOCALIZATION_KEY_DELETE_PATIENT = "patients_list_window.button.patient.delete";

	private static final String LOCALIZATION_KEY_MENU_DATABASE = "patients_list_window.menu.database";
	private static final String LOCALIZATION_KEY_MENU_DATABASE_ITEM_DISCONNECT = "patients_list_window.menu.database.disconnect";
	private static final String LOCALIZATION_KEY_MENU_DATABASE_ITEM_QUIT = "patients_list_window.menu.database.quit";
	private static final String LOCALIZATION_KEY_MENU_ILLNESSES = "patients_list_window.menu.illnesses";
	private static final String LOCALIZATION_KEY_MENU_ILLNESSES_ITEM_ADD = "patients_list_window.menu.illnesses.add";
	private static final String LOCALIZATION_KEY_MENU_INTERFACE = "patients_list_window.menu.interface";
	private static final String LOCALIZATION_KEY_MENU_INTERFACE_ITEM_LANGUAGE_SELECT = "patients_list_window.menu.interface.language";
	private static final String LOCALIZATION_KEY_MENU_INTERFACE_ITEM_LANGUAGE_EN = "patients_list_window.menu.language.en";
	private static final String LOCALIZATION_KEY_MENU_INTERFACE_ITEM_LANGUAGE_UA = "patients_list_window.menu.language.ua";

	private static final String LOCALIZATION_KEY_QUIT_CONFIRM_DIALOG_TITLE = "patients_list_window.quitconfirmdialog.title";
	private static final String LOCALIZATION_KEY_QUIT_CONFIRM_DIALOG_MESSAGE = "patients_list_window.quitconfirmdialog.message";

	private static final String LOCALIZATION_KEY_ADD_ILLNESSES_DIALOG_TITLE = "patients_list_window.addillnessdialog.title";
	private static final String LOCALIZATION_KEY_ADD_ILLNESSES_DIALOG_MESSAGE = "patients_list_window.addillnessdialog.message";

	private static final String LOCALIZATION_KEY_ILLNESSES_ADDED_MESSAGE = "patients_list_window.addillnessaction.message.success";

	private static final String LOCALIZATION_KEY_DELETE_PATIENTS_DIALOG_TITLE = "delete_patients_dialog.title";
	private static final String LOCALIZATION_KEY_DELETE_PATIENTS_DIALOG_MESSAGE = "delete_patients_dialog.message";

	private final MessageSource messageSource;
	private final IllnessesService illnessesService;

	private final JExtendedTabPane<PatientsListPerIllness> illnessTabs;
	private final JLabel lblFilterDateFrom;
	private final JLabel lblFilterDateTo;
	private final JLabel lblFilterKeyword;
	private final JTextField tfExaminationDateFromFilter;
	private final JTextField tfExaminationDateToFilter;
	private final JTextField tfSearchKeywordFilter;
	private final JButton btnDoFilter;

	private final JMenuBar mainMenuBar;

	private final JMenu menuDataBase;
	private final JMenuItem miDisconnect;
	private final JMenuItem miQuit;

	private final JMenu menuIllnesses;
	private final JMenuItem miAddIllness;

	private final JMenu menuInterface;
	private final JMenu msubLanguageSelect;
	private final ButtonGroup languageSelectRadioButtonGroup;
	private final JRadioButtonMenuItem miRadioLanguageEng;
	private final JRadioButtonMenuItem miRadioLanguageUkr;
	private final JButton btnAddPatient;
	private final JButton btnEditPatient;
	private final JButton btnDeletePatient;

	private final ConcurrentHashMap<Long, PatientsListPerIllness> patientsListsPerIllnessById = new ConcurrentHashMap<Long, PatientsListPerIllness>();

	private LocaleChangeNotifier localeChangeNotifier;

	private final PatientsService patientsService;

	private volatile Locale currentLocale = Locale.US;

	private final PatientDataDialog patientDataDialog;

	public PatientsListWindow(final IllnessesService illnessesService, final PatientsService patientsService, final MedicationService medicationService,
			final MessageSource messageSource) {
		this.messageSource = messageSource;
		this.illnessesService = illnessesService;
		this.patientsService = patientsService;

		patientDataDialog = new PatientDataDialog(patientsService, illnessesService, medicationService, this, messageSource);

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel();
		illnessTabs = new JExtendedTabPane<PatientsListPerIllness>();

		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(illnessTabs.getUnderlyingComponent(), BorderLayout.CENTER);

		recreatePerIllnessListsTabs();

		tfExaminationDateFromFilter = new JTextField();
		Dimension filterTextFieldPreferredSize = new Dimension(100, tfExaminationDateFromFilter.getPreferredSize().height);
		tfExaminationDateFromFilter.setPreferredSize(filterTextFieldPreferredSize);
		tfExaminationDateToFilter = new JTextField();
		tfExaminationDateToFilter.setPreferredSize(filterTextFieldPreferredSize);
		tfSearchKeywordFilter = new JTextField();
		tfSearchKeywordFilter.setPreferredSize(filterTextFieldPreferredSize);
		btnDoFilter = new JButton();

		btnAddPatient = new JButton();
		btnAddPatient.setFocusable(false);
		btnEditPatient = new JButton();
		btnEditPatient.setFocusable(false);
		btnDeletePatient = new JButton();
		btnDeletePatient.setFocusable(false);

		lblFilterDateFrom = new JLabel();
		lblFilterDateTo = new JLabel();
		lblFilterKeyword = new JLabel();

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(lblFilterDateFrom);
		topPanel.add(tfExaminationDateFromFilter);
		topPanel.add(lblFilterDateTo);
		topPanel.add(tfExaminationDateToFilter);
		topPanel.add(lblFilterKeyword);
		topPanel.add(tfSearchKeywordFilter);
		topPanel.add(btnDoFilter);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(btnDeletePatient, BorderLayout.WEST);
		bottomPanel.add(btnEditPatient, BorderLayout.CENTER);
		bottomPanel.add(btnAddPatient, BorderLayout.EAST);

		contentPane.add(new JScrollPane(topPanel), BorderLayout.NORTH);
		contentPane.add(centerPanel, BorderLayout.CENTER);
		contentPane.add(new JScrollPane(bottomPanel), BorderLayout.SOUTH);

		menuDataBase = new JMenu();

		miDisconnect = new JMenuItem();
		miQuit = new JMenuItem();
		menuDataBase.add(miDisconnect);
		menuDataBase.add(miQuit);

		menuIllnesses = new JMenu();
		miAddIllness = new JMenuItem();
		menuIllnesses.add(miAddIllness);

		miAddIllness.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actEvent) {
				String newIllnessName = JOptionPane.showInputDialog(PatientsListWindow.this,
						messageSource.getMessage(LOCALIZATION_KEY_ADD_ILLNESSES_DIALOG_MESSAGE, null, currentLocale),
						messageSource.getMessage(LOCALIZATION_KEY_ADD_ILLNESSES_DIALOG_TITLE, null, currentLocale), JOptionPane.INFORMATION_MESSAGE);
				if (newIllnessName != null && newIllnessName.trim().length() > 0) {
					Illness illness = illnessesService.createNewIllness(newIllnessName);
					JOptionPane.showMessageDialog(PatientsListWindow.this,
							messageSource.getMessage(LOCALIZATION_KEY_ILLNESSES_ADDED_MESSAGE, null, currentLocale));
					PatientsListWindow.this.addPerIllnessPatientsListTab(illness);
				}
			}
		});

		menuInterface = new JMenu();
		msubLanguageSelect = new JMenu();
		languageSelectRadioButtonGroup = new ButtonGroup();
		{
			miRadioLanguageEng = new JRadioButtonMenuItem();
			miRadioLanguageUkr = new JRadioButtonMenuItem();
			languageSelectRadioButtonGroup.add(miRadioLanguageEng);
			languageSelectRadioButtonGroup.add(miRadioLanguageUkr);

			miRadioLanguageEng.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actEvent) {
					if (PatientsListWindow.this.localeChangeNotifier != null) {
						PatientsListWindow.this.localeChangeNotifier.setLocale(Locale.ENGLISH);
					}
				}
			});
			miRadioLanguageUkr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actEvent) {
					if (PatientsListWindow.this.localeChangeNotifier != null) {
						PatientsListWindow.this.localeChangeNotifier.setLocale(new Locale("ua", "UA"));
					}
				}
			});
		}
		menuInterface.add(msubLanguageSelect);
		msubLanguageSelect.add(miRadioLanguageEng);
		msubLanguageSelect.add(miRadioLanguageUkr);

		mainMenuBar = new JMenuBar();
		mainMenuBar.add(menuDataBase);
		mainMenuBar.add(menuIllnesses);
		mainMenuBar.add(menuInterface);

		this.setJMenuBar(mainMenuBar);

		this.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent actEvent) {
			}

			public void windowIconified(WindowEvent actEvent) {
			}

			public void windowDeiconified(WindowEvent actEvent) {
			}

			public void windowDeactivated(WindowEvent actEvent) {
			}

			public void windowClosing(WindowEvent actEvent) {
				PatientsListWindow.this.confirmAndPerformClose();
			}

			public void windowClosed(WindowEvent actEvent) {
			}

			public void windowActivated(WindowEvent actEvent) {
			}
		});

		miQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actEvent) {
				PatientsListWindow.this.confirmAndPerformClose();
			}
		});
		miQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.META_MASK));

		miDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actEvent) {
				PatienceA.restart();
			}
		});

		btnAddPatient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PatientsListWindow.this.openPatientDetail(-1);
			}
		});

		btnEditPatient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PatientsListPerIllness patientsList = illnessTabs.getSelectedComponent();
				if (patientsList != null) {
					PatientStatsData patientData = patientsList.getSingleSelectedItem();
					if (patientData != null) {
						PatientsListWindow.this.openPatientDetail(patientData.getPatientId());
					}
				}
			}
		});

		btnDeletePatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actEvent) {
				// TODO: take away from Swing Event Dispatching Thread
				// TODO: handle errors
				PatientsListPerIllness listOfPatientsPerIllness = PatientsListWindow.this.illnessTabs.getSelectedComponent();
				int[] selectedRows = listOfPatientsPerIllness.getTblPatientsList().getSelectedRows();
				if (selectedRows != null && selectedRows.length > 0) {
					String deletePatientsMessage = PatientsListWindow.this.messageSource.getMessage(LOCALIZATION_KEY_DELETE_PATIENTS_DIALOG_MESSAGE,
							new Object[] { selectedRows.length }, PatientsListWindow.this.getLocale());
					String deletePatientsDialogTitle = PatientsListWindow.this.messageSource.getMessage(LOCALIZATION_KEY_DELETE_PATIENTS_DIALOG_TITLE, null,
							PatientsListWindow.this.getLocale());
					if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(PatientsListWindow.this, deletePatientsMessage, deletePatientsDialogTitle,
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						long[] patientsToDelete = new long[selectedRows.length];
						for (int i = 0; i < selectedRows.length; i++) {
							patientsToDelete[i] = listOfPatientsPerIllness.getPatientStatsData(selectedRows[i]).getPatientId();
						}
						patientsService.delete(patientsToDelete);
						PatientsListWindow.this.recreatePerIllnessListsTabs();
					}
				}
			}
		});
	}

	public void confirmAndPerformClose() {
		String message = PatientsListWindow.this.messageSource.getMessage(LOCALIZATION_KEY_QUIT_CONFIRM_DIALOG_MESSAGE, null,
				PatientsListWindow.this.getLocale());
		String title = PatientsListWindow.this.messageSource.getMessage(LOCALIZATION_KEY_QUIT_CONFIRM_DIALOG_TITLE, null, PatientsListWindow.this.getLocale());
		int confirmDialogResponse = JOptionPane.showConfirmDialog(PatientsListWindow.this, message, title, JOptionPane.YES_NO_OPTION);
		if (confirmDialogResponse == JOptionPane.YES_OPTION) {
			this.setVisible(false);
			this.dispose();
			PatienceA.shutdown();
		}
	}

	protected void addPerIllnessPatientsListTab(Illness illnessToFilterBy) {
		PatientsListPerIllness patientsListComponent = new PatientsListPerIllness(this, messageSource, patientsService, illnessToFilterBy);
		illnessTabs.addTab(patientsListComponent.getTitle(), patientsListComponent);
		patientsListsPerIllnessById.put(patientsListComponent.getId(), patientsListComponent);
	}

	public void recreatePerIllnessListsTabs() {
		// TODO: run outside event-dispatching thread (maybe with progress
		// indicator)
		illnessTabs.removeAll();
		addPerIllnessPatientsListTab(null);
		Collection<Illness> illnesses = illnessesService.getAllIllnesses();
		for (Illness illness : illnesses) {
			addPerIllnessPatientsListTab(illness);
		}
	}

	public void setSelfAsLocaleChangeListener(final LocaleChangeNotifier localeChangeNotifier) {
		this.illnessTabs.setSelfAsLocaleChangeListener(localeChangeNotifier);
		this.patientDataDialog.setSelfAsLocaleChangeListener(localeChangeNotifier);

		this.localeChangeNotifier = localeChangeNotifier;
		localeChangeNotifier.registerLocaleChangeListener(this);
		setLocale(localeChangeNotifier.getLastSetLocale());
		this.pack();
		SwingHelper.moveToScreenCenter(this);
	}

	public void setLocale(Locale locale) {
		super.setLocale(locale);
		this.currentLocale = locale;
		if (messageSource != null && locale != null) {
			this.setTitle(messageSource.getMessage(LOCALIZATION_KEY_WINDOW_TITLE, null, locale));
			btnDoFilter.setText(messageSource.getMessage(LOCALIZATION_KEY_FILTER_APPLY, null, locale));
			btnAddPatient.setText(messageSource.getMessage(LOCALIZATION_KEY_ADD_PATIENT, null, locale));
			btnEditPatient.setText(messageSource.getMessage(LOCALIZATION_KEY_EDIT_PATIENT, null, locale));
			btnDeletePatient.setText(messageSource.getMessage(LOCALIZATION_KEY_DELETE_PATIENT, null, locale));

			lblFilterDateFrom.setText(messageSource.getMessage(LOCALIZATION_KEY_FILTER_DATE_FROM, null, locale));
			lblFilterDateTo.setText(messageSource.getMessage(LOCALIZATION_KEY_FILTER_DATE_TO, null, locale));
			lblFilterKeyword.setText(messageSource.getMessage(LOCALIZATION_KEY_FILTER_KEYWORD, null, locale));

			menuDataBase.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_DATABASE, null, locale));
			menuIllnesses.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_ILLNESSES, null, locale));
			miAddIllness.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_ILLNESSES_ITEM_ADD, null, locale));

			menuInterface.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_INTERFACE, null, locale));
			miDisconnect.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_DATABASE_ITEM_DISCONNECT, null, locale));
			miQuit.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_DATABASE_ITEM_QUIT, null, locale));
			msubLanguageSelect.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_INTERFACE_ITEM_LANGUAGE_SELECT, null, locale));
			miRadioLanguageEng.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_INTERFACE_ITEM_LANGUAGE_EN, null, locale));
			miRadioLanguageUkr.setText(messageSource.getMessage(LOCALIZATION_KEY_MENU_INTERFACE_ITEM_LANGUAGE_UA, null, locale));

			if (locale.getDisplayLanguage().equalsIgnoreCase("ua")) {
				miRadioLanguageUkr.setSelected(true);
			} else {
				miRadioLanguageEng.setSelected(true);
			}
		}
	}

	public void onIllnessEntityCreation(Illness illness) {
		if (patientsListsPerIllnessById.get(illness.getId()) == null) {
			addPerIllnessPatientsListTab(illness);
		}
	}

	public void onIllnessEntityUpdate(Illness illness) {
		patientsListsPerIllnessById.get(illness.getId()).reloadData();
	}

	public void openPatientDetail(long patientId) {
		PatientData patientData = null;
		if (patientId >= 0) {
			patientData = patientsService.get(patientId, true);
		}
		patientDataDialog.setData(patientData);
		patientDataDialog.pack();
		SwingHelper.moveToScreenCenter(patientDataDialog);
		patientDataDialog.setVisible(true);
	}
}
