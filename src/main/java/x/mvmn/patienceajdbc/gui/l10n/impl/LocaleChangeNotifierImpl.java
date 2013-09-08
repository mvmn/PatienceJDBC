package x.mvmn.patienceajdbc.gui.l10n.impl;

import java.util.Locale;

import x.mvmn.lang.ExceptionHandler;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeAware;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeEvent;
import x.mvmn.patienceajdbc.gui.l10n.LocaleChangeNotifier;
import x.mvmn.simpleeventbus.EventBus;
import x.mvmn.simpleeventbus.EventListener;
import x.mvmn.simpleeventbus.impl.EventBusImpl;

public class LocaleChangeNotifierImpl implements LocaleChangeNotifier {

	private EventBus eventBus = new EventBusImpl();
	private volatile Locale lastSetLocale = null;

	public LocaleChangeAware registerLocaleChangeListener(final LocaleChangeAware localeChangeAware) {
		eventBus.registerEventListener(LocaleChangeEvent.class, new EventListener<LocaleChangeEvent>() {

			public void handleEvent(LocaleChangeEvent event) {
				localeChangeAware.setLocale(event.getLocale());
			}
		}, new ExceptionHandler<Throwable>() {

			public void handleException(Throwable exception) {
				exception.printStackTrace();
			}
		});
		return localeChangeAware;
	}

	public void setLocale(Locale locale) {
		lastSetLocale = locale;
		eventBus.fireEventOfAutodetectedType(new LocaleChangeEvent(locale));
	}

	public Locale getLastSetLocale() {
		return lastSetLocale;
	}

}
