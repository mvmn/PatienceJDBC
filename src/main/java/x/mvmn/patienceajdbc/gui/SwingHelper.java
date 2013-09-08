package x.mvmn.patienceajdbc.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class SwingHelper {

	public static void moveToScreenCenter(Component component) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension componentSize = component.getSize();
		int newComponentX = screenSize.width - componentSize.width;
		if (newComponentX >= 0)
			newComponentX = newComponentX / 2;
		else
			newComponentX = 0;
		int newComponentY = screenSize.height - componentSize.height;
		if (newComponentY >= 0)
			newComponentY = newComponentY / 2;
		else
			newComponentY = 0;
		component.setLocation(newComponentX, newComponentY);
	}

	public static void resizeToScreenProportions(Component component, double xProportion, double yProportion) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension componentNewSize = new Dimension((int) (screenSize.width * yProportion), (int) (screenSize.height * xProportion));
		component.setSize(componentNewSize);
	}

	public static void resizeToScreenProportions(Component component, double proportion) {
		resizeToScreenProportions(component, proportion, proportion);
	}

	public static JFrame enframeComponent(Component component, String title) {
		JFrame result;
		if (title != null)
			result = new JFrame(title);
		else
			result = new JFrame();
		result.getContentPane().add(component);
		result.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		result.pack();
		resizeToScreenProportions(result, 0.7);
		moveToScreenCenter(result);
		return result;
	}

	public static JFrame enframeComponent(Component component) {
		String title = null;
		if (component instanceof Titled)
			title = ((Titled) component).getTitle();
		JFrame frame = enframeComponent(component, title);
		if (component instanceof Menued) {
			JMenuBar menuBar = ((Menued) component).getJMenuBar();
			frame.setJMenuBar(menuBar);
		}
		return frame;
	}
}
