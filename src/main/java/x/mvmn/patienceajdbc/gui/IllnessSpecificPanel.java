package x.mvmn.patienceajdbc.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import x.mvmn.patienceajdbc.model.Illness;

public class IllnessSpecificPanel extends JPanel {

	private static final long serialVersionUID = 5417825585334620796L;

	protected final Illness illness;

	public IllnessSpecificPanel(final Illness illness) {
		super();
		this.illness = illness;
	}

	public IllnessSpecificPanel(final Illness illness, boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		this.illness = illness;
	}

	public IllnessSpecificPanel(final Illness illness, LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		this.illness = illness;
	}

	public IllnessSpecificPanel(final Illness illness, LayoutManager layout) {
		super(layout);
		this.illness = illness;
	}

	public Illness getIllness() {
		return illness;
	}
}
