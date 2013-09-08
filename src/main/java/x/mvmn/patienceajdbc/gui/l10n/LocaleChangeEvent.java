package x.mvmn.patienceajdbc.gui.l10n;

import java.util.Locale;

import x.mvmn.simpleeventbus.AbstractEvent;

public class LocaleChangeEvent extends AbstractEvent {

	protected Locale locale;

	public LocaleChangeEvent(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("Locale should not be null");
		}
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}
}
