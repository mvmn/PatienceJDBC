package x.mvmn.patienceajdbc.gui.startup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.springframework.context.MessageSource;

import x.mvmn.patienceajdbc.PatienceA;
import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.l10n.impl.LocaleSelectionComboBoxImpl;

public class ConnectionPropertiesDialog extends JDialog implements LocaleChangeAware, Titled {

	private static final long serialVersionUID = 4062011138230072395L;

	private static final String LOCALIZATION_KEY_DIALOG_TITLE = "connection_properties_dialog.title";

	private static final String LOCALIZATION_KEY_INPUT_DB_HOST = "connection_properties_dialog.label.dbhost";
	private static final String LOCALIZATION_KEY_INPUT_DB_NAME = "connection_properties_dialog.label.dbname";
	private static final String LOCALIZATION_KEY_INPUT_DB_LOGIN = "connection_properties_dialog.label.dblogin";
	private static final String LOCALIZATION_KEY_INPUT_DB_PASSWORD = "connection_properties_dialog.label.dbpassword";
	private static final String LOCALIZATION_KEY_BUTTON_CONNECT = "connection_properties_dialog.button.connect";
	private static final String LOCALIZATION_KEY_SELECT_LOCALE = "generic.select.locale";

	private final MessageSource messageSource;

	private final JPanel pnlMain = new JPanel();
	private final TitledBorder lblDbHost = new TitledBorder("");
	private final JTextField txfDbHost = new JTextField("localhost");
	private final TitledBorder lblDbName = new TitledBorder("");
	private final JTextField txfDbName = new JTextField("patience");
	private final TitledBorder lblDbUser = new TitledBorder("");
	private final JTextField txfDbUser = new JTextField();
	private final TitledBorder lblDbPass = new TitledBorder("");
	private final JPasswordField psfDbPass = new JPasswordField();
	private final JButton btnDoConnect = new JButton("");

	private final LocaleSelectionComboBoxImpl cbxLocaleSelect;
	private final TitledBorder lblLocaleSelect = new TitledBorder("");

	public ConnectionPropertiesDialog(LocaleSelectionComboBoxImpl localeSelectionComboBox, MessageSource messageSource) {
		super();
		this.cbxLocaleSelect = localeSelectionComboBox;
		this.messageSource = messageSource;
		init();
	}

	protected void init() {
		pnlMain.setLayout(new GridLayout(4, 1));
		pnlMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(pnlMain, BorderLayout.NORTH);
		txfDbHost.setBorder(lblDbHost);
		txfDbName.setBorder(lblDbName);
		txfDbUser.setBorder(lblDbUser);
		psfDbPass.setBorder(lblDbPass);
		cbxLocaleSelect.setBorder(lblLocaleSelect);

		txfDbHost.setPreferredSize(new Dimension(400, txfDbHost.getPreferredSize().height));
		pnlMain.add(txfDbHost);
		pnlMain.add(txfDbName);
		pnlMain.add(txfDbUser);
		pnlMain.add(psfDbPass);
		// pnlMain.add(btnDoConnect);
		// pnlMain.add(cbxLocaleSelect);
		this.getContentPane().add(btnDoConnect, BorderLayout.CENTER);
		this.getContentPane().add(cbxLocaleSelect, BorderLayout.SOUTH);

		btnDoConnect.setActionCommand("doConnect");

		btnDoConnect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actEvent) {
				if ("doConnect".equals(actEvent.getActionCommand())) {
					btnDoConnect.setEnabled(false);
					btnDoConnect.revalidate();
					new Thread(new Runnable() {
						public void run() {
							try {
								PatienceA.handleConnectClick(ConnectionPropertiesDialog.this);
							} catch (final Exception e) {
								e.printStackTrace();
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										JOptionPane.showMessageDialog(ConnectionPropertiesDialog.this, e.getClass().getSimpleName() + ": " + e.getMessage(),
												"Error occurred", JOptionPane.ERROR_MESSAGE);
									}
								});
							}
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									// Revert dialog state back for re-use
									btnDoConnect.setEnabled(true);
									btnDoConnect.revalidate();
								}
							});
						}
					}).start();
				}
			}
		});

		this.getRootPane().setDefaultButton(btnDoConnect);

		setLocale(Locale.US);
	}

	public void setLocale(Locale locale) {
		super.setLocale(locale);
		if (messageSource != null && locale != null) {
			this.setTitle(messageSource.getMessage(LOCALIZATION_KEY_DIALOG_TITLE, null, locale));
			this.lblDbHost.setTitle(messageSource.getMessage(LOCALIZATION_KEY_INPUT_DB_HOST, null, locale));
			this.lblDbName.setTitle(messageSource.getMessage(LOCALIZATION_KEY_INPUT_DB_NAME, null, locale));
			this.lblDbUser.setTitle(messageSource.getMessage(LOCALIZATION_KEY_INPUT_DB_LOGIN, null, locale));
			this.lblDbPass.setTitle(messageSource.getMessage(LOCALIZATION_KEY_INPUT_DB_PASSWORD, null, locale));
			this.btnDoConnect.setText(messageSource.getMessage(LOCALIZATION_KEY_BUTTON_CONNECT, null, locale));
			this.lblLocaleSelect.setTitle(messageSource.getMessage(LOCALIZATION_KEY_SELECT_LOCALE, null, locale));

			cbxLocaleSelect.setSelectedLocale(locale);

			this.invalidate();
			this.repaint();
		}
	}

	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier) {
		notifier.registerLocaleChangeListener(this);
	}

	public String getDbHost() {
		return txfDbHost.getText();
	}

	public String getDbName() {
		return txfDbName.getText();
	}

	public String getDbUser() {
		return txfDbUser.getText();
	}

	public char[] getDbPassword() {
		return psfDbPass.getPassword();
	}

	public boolean getProfileSql() {
		return false;
	}
}
