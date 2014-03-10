package x.mvmn.gui.generic.swing;

import java.awt.Component;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import x.mvmn.patienceajdbc.gui.Titled;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.patienceajdbc.gui.l10n.Localizable;

/**
 * This class completely delegates all JComponent methods to underlying instance of JTabbedPane
 * 
 * @param <P>
 *            tabs type
 */
public class JExtendedTabPane<P extends Component> implements LocaleChangeAware {

	protected final JTabbedPane wrappedTabbedPane;

	public JExtendedTabPane() {
		wrappedTabbedPane = new JTabbedPane();
	}

	public void setLocale(Locale locale) {
		wrappedTabbedPane.setLocale(locale);
		for (int i = 0; i < wrappedTabbedPane.getTabCount(); i++) {
			P tab = this.getComponentAt(i);
			if (tab instanceof Titled && tab instanceof Localizable) {
				Localizable ltab = (Localizable) tab;
				ltab.setLocale(locale);
				String newTabTitle = ((Titled) tab).getTitle();
				wrappedTabbedPane.setTitleAt(i, newTabTitle);
			}
		}
	}

	public int getTabCount() {
		return wrappedTabbedPane.getTabCount();
	}

	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier) {
		notifier.registerLocaleChangeListener(this);
	}

	public void removeTabAt(int index) {
		wrappedTabbedPane.removeTabAt(index);
	}

	public void addTab(String title, P tab) {
		wrappedTabbedPane.addTab(title, tab);
	}

	public void addTab(String title, Icon icon, P tab) {
		wrappedTabbedPane.addTab(title, icon, tab);
	}

	public void addTab(String title, Icon icon, P tab, String tip) {
		wrappedTabbedPane.addTab(title, icon, tab, tip);
	}

	@SuppressWarnings("unchecked")
	public P getComponentAt(int index) {
		return (P) wrappedTabbedPane.getComponentAt(index);
	}

	@SuppressWarnings("unchecked")
	public P getSelectedComponent() {
		return (P) wrappedTabbedPane.getSelectedComponent();
	}

	public int indexOfComponent(P tab) {
		return wrappedTabbedPane.indexOfComponent(tab);
	}

	public void insertTab(String title, Icon icon, P tab, String tip, int index) {
		wrappedTabbedPane.insertTab(title, icon, tab, tip, index);
	}

	public void setSelectedComponent(P tab) {
		wrappedTabbedPane.setSelectedComponent(tab);
	}

	public void setTabComponentAt(int index, P tab) {
		wrappedTabbedPane.setTabComponentAt(index, tab);
	}

	public Component getUnderlyingComponent() {
		return wrappedTabbedPane;
	}

	public void removeAll() {
		wrappedTabbedPane.removeAll();
	}

	public void remove(P component) {
		wrappedTabbedPane.remove(component);
	}
}
