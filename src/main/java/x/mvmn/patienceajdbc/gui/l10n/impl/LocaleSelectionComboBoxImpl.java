package x.mvmn.patienceajdbc.gui.l10n.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;

public class LocaleSelectionComboBoxImpl extends JComboBox<String> {

	private static final long serialVersionUID = -5985482522164435570L;

	public static class LocaleNamePair {
		private final Locale locale;
		private final String name;

		public LocaleNamePair(Locale locale, String name) {
			super();
			this.locale = locale;
			this.name = name;
		}

		public Locale getLocale() {
			return locale;
		}

		public String getName() {
			return name;
		}
	}

	private final List<LocaleNamePair> supportedLocales;
	private final Map<String, Locale> localesMap = new HashMap<String, Locale>();
	private final LocaleChangeNotifier localeChangeNotifier;

	LocaleSelectionComboBoxImpl(List<LocaleNamePair> supportedLocales, LocaleChangeNotifier localeChangeNotifier) {
		if (supportedLocales == null || supportedLocales.isEmpty())
			throw new IllegalArgumentException("supportedLocales cannot be empty");
		this.supportedLocales = supportedLocales;
		this.localeChangeNotifier = localeChangeNotifier;
		for (LocaleNamePair localeNamePair : supportedLocales) {
			localesMap.put(localeNamePair.getName(), localeNamePair.getLocale());
		}

		this.setModel(new DefaultComboBoxModel<String>() {

			private static final long serialVersionUID = -7111335760278976272L;

			public int getSize() {
				return LocaleSelectionComboBoxImpl.this.supportedLocales.size();
			}

			public String getElementAt(int idx) {
				String result = "";
				if (idx < getSize()) {
					result = LocaleSelectionComboBoxImpl.this.supportedLocales.get(idx).getName();
				}
				return result;
			}
		});
		this.setSelectedIndex(0);

		if (localeChangeNotifier != null) {
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Locale selectedLocale = LocaleSelectionComboBoxImpl.this.getSelectedLocale();
					LocaleSelectionComboBoxImpl.this.localeChangeNotifier.setLocale(selectedLocale);
				}
			});
		}
	}

	public void setSelectedLocale(Locale locale) {
		for (Map.Entry<String, Locale> availableLocale : localesMap.entrySet()) {
			if (availableLocale.getValue().equals(locale)) {
				this.setSelectedItem(availableLocale.getKey());
				break;
			}
		}
	}

	public Locale getSelectedLocale() {
		Locale result = supportedLocales.get(0).getLocale();
		Object selectedItem = super.getSelectedItem();
		if (selectedItem != null) {
			Locale selectedLocale = localesMap.get(selectedItem.toString());
			if (selectedLocale != null) {
				result = selectedLocale;
			}
		}
		return result;
	}
}
