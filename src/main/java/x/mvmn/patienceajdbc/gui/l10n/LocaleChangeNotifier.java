package x.mvmn.patienceajdbc.gui.l10n;

import java.util.Locale;

public interface LocaleChangeNotifier {

	public LocaleChangeAware registerLocaleChangeListener(LocaleChangeAware localeChangeAware);

	public void setLocale(Locale locale);

	public Locale getLastSetLocale();
}
