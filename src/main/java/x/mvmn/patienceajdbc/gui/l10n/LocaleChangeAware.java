package x.mvmn.patienceajdbc.gui.l10n;

public interface LocaleChangeAware extends Localizable {

	public void setSelfAsLocaleChangeListener(LocaleChangeNotifier notifier);

}
