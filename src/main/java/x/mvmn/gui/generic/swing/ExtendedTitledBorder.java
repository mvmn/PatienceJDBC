package x.mvmn.gui.generic.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ExtendedTitledBorder extends TitledBorder implements MouseListener {

	private static final long serialVersionUID = -8724444722869519199L;

	private final Component titleComponent;
	private final Container parent;

	private volatile int titleComponentOffsetXLastDrawn = 0;
	private volatile int titleComponentOffsetYLastDrawn = 0;
	private volatile int titleComponentWidthLastDrawn = 0;
	private volatile int titleComponentHeightLastDrawn = 0;

	public ExtendedTitledBorder(Component component, Container parent) {
		super("");
		this.titleComponent = component;
		this.parent = parent;
		parent.addMouseListener(this);

		// Partial support for container capabilities
		try {
			component.addNotify();
			Field parentField = Component.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			parentField.set(component, parent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Dispatch mouse events
	// (thanks to Santosh Kumar T for code sample of this:
	// http://www.javalobby.org/java/forums/t33048.html )
	private void dispatchEvent(MouseEvent me) {
		Rectangle rect = new Rectangle(titleComponentOffsetXLastDrawn, titleComponentOffsetYLastDrawn, titleComponentWidthLastDrawn
				+ titleComponentOffsetXLastDrawn, titleComponentHeightLastDrawn + titleComponentOffsetYLastDrawn);
		if (rect != null && rect.contains(me.getX(), me.getY())) {
			Point pt = me.getPoint();
			pt.translate(-titleComponentOffsetXLastDrawn, -titleComponentOffsetYLastDrawn);
			// comp.setBounds(rect);
			this.titleComponent.dispatchEvent(new MouseEvent(this.titleComponent, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(),
					me.isPopupTrigger(), me.getButton()));
			this.titleComponent.revalidate();
			this.titleComponent.repaint();
			this.parent.revalidate();
			this.parent.repaint();
		}
	}

	public void mouseClicked(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseEntered(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseExited(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mousePressed(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseReleased(MouseEvent me) {
		dispatchEvent(me);
	}

	// Overwrite TitledBorder methods
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Border border = getBorder();
		if (titleComponent != null) {
			int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;
			Dimension size = titleComponent.getPreferredSize();
			Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));

			int borderX = x + edge;
			int borderY = y + edge;
			int borderW = width - edge - edge;
			int borderH = height - edge - edge;

			int labelY = y;
			int labelH = size.height;
			int position = getPosition();
			switch (position) {
				case ABOVE_TOP:
					insets.left = 0;
					insets.right = 0;
					borderY += labelH - edge;
					borderH -= labelH - edge;
				break;
				case TOP:
					insets.top = edge + insets.top / 2 - labelH / 2;
					if (insets.top < edge) {
						borderY -= insets.top;
						borderH += insets.top;
					} else {
						labelY += insets.top;
					}
				break;
				case BELOW_TOP:
					labelY += insets.top + edge;
				break;
				case ABOVE_BOTTOM:
					labelY += height - labelH - insets.bottom - edge;
				break;
				case BOTTOM:
					labelY += height - labelH;
					insets.bottom = edge + (insets.bottom - labelH) / 2;
					if (insets.bottom < edge) {
						borderH += insets.bottom;
					} else {
						labelY -= insets.bottom;
					}
				break;
				case BELOW_BOTTOM:
					insets.left = 0;
					insets.right = 0;
					labelY += height - labelH;
					borderH -= labelH - edge;
				break;
			}
			insets.left += edge + TEXT_INSET_H;
			insets.right += edge + TEXT_INSET_H;

			int labelX = x;
			int labelW = width - insets.left - insets.right;
			if (labelW > size.width) {
				labelW = size.width;
			}
			switch (getJustification(c)) {
				case LEFT:
					labelX += insets.left;
				break;
				case RIGHT:
					labelX += width - insets.right - labelW;
				break;
				case CENTER:
					labelX += (width - labelW) / 2;
				break;
			}

			if (border != null) {
				if ((position != TOP) && (position != BOTTOM)) {
					border.paintBorder(c, g, borderX, borderY, borderW, borderH);
				} else {
					Graphics g2 = g.create();
					if (g2 instanceof Graphics2D) {
						Graphics2D g2d = (Graphics2D) g2;
						Path2D path = new Path2D.Float();
						path.append(new Rectangle(borderX, borderY, borderW, labelY - borderY), false);
						path.append(new Rectangle(borderX, labelY, labelX - borderX - TEXT_SPACING, labelH), false);
						path.append(new Rectangle(labelX + labelW + TEXT_SPACING, labelY, borderX - labelX + borderW - labelW - TEXT_SPACING, labelH), false);
						path.append(new Rectangle(borderX, labelY + labelH, borderW, borderY - labelY + borderH - labelH), false);
						g2d.clip(path);
					}
					border.paintBorder(c, g2, borderX, borderY, borderW, borderH);
					g2.dispose();
				}
			}
			g.translate(labelX, labelY);
			titleComponentOffsetXLastDrawn = labelX;
			titleComponentOffsetYLastDrawn = labelY;
			titleComponentWidthLastDrawn = labelW;
			titleComponentHeightLastDrawn = labelH;
			titleComponent.setSize(labelW, labelH);
			titleComponent.paint(g);
			g.translate(-labelX, -labelY);
		} else if (border != null) {
			border.paintBorder(c, g, x, y, width, height);
		}
	}

	private static Insets getBorderInsets(Border border, Component c, Insets insets) {
		if (border == null) {
			insets.set(0, 0, 0, 0);
		} else if (border instanceof AbstractBorder) {
			AbstractBorder ab = (AbstractBorder) border;
			insets = ab.getBorderInsets(c, insets);
		} else {
			Insets i = border.getBorderInsets(c);
			insets.set(i.top, i.left, i.bottom, i.right);
		}
		return insets;
	}

	private int getPosition() {
		int position = getTitlePosition();
		if (position != DEFAULT_POSITION) {
			return position;
		}
		Object value = UIManager.get("TitledBorder.position");
		if (value instanceof Integer) {
			int i = (Integer) value;
			if ((0 < i) && (i <= 6)) {
				return i;
			}
		} else if (value instanceof String) {
			String s = (String) value;
			if (s.equalsIgnoreCase("ABOVE_TOP")) {
				return ABOVE_TOP;
			}
			if (s.equalsIgnoreCase("TOP")) {
				return TOP;
			}
			if (s.equalsIgnoreCase("BELOW_TOP")) {
				return BELOW_TOP;
			}
			if (s.equalsIgnoreCase("ABOVE_BOTTOM")) {
				return ABOVE_BOTTOM;
			}
			if (s.equalsIgnoreCase("BOTTOM")) {
				return BOTTOM;
			}
			if (s.equalsIgnoreCase("BELOW_BOTTOM")) {
				return BELOW_BOTTOM;
			}
		}
		return TOP;
	}

	private int getJustification(Component c) {
		int justification = getTitleJustification();
		if ((justification == LEADING) || (justification == DEFAULT_JUSTIFICATION)) {
			return c.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT;
		}
		if (justification == TRAILING) {
			return c.getComponentOrientation().isLeftToRight() ? RIGHT : LEFT;
		}
		return justification;
	}

	public Insets getBorderInsets(Component c, Insets insets) {
		Border border = getBorder();
		insets = getBorderInsets(border, c, insets);

		if (this.titleComponent != null) {
			int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;

			Dimension size = this.titleComponent.getPreferredSize();

			switch (getPosition()) {
				case ABOVE_TOP:
					insets.top += size.height - edge;
				break;
				case TOP: {
					if (insets.top < size.height) {
						insets.top = size.height - edge;
					}
					break;
				}
				case BELOW_TOP:
					insets.top += size.height;
				break;
				case ABOVE_BOTTOM:
					insets.bottom += size.height;
				break;
				case BOTTOM: {
					if (insets.bottom < size.height) {
						insets.bottom = size.height - edge;
					}
					break;
				}
				case BELOW_BOTTOM:
					insets.bottom += size.height - edge;
				break;
			}
			insets.top += edge + TEXT_SPACING;
			insets.left += edge + TEXT_SPACING;
			insets.right += edge + TEXT_SPACING;
			insets.bottom += edge + TEXT_SPACING;
		}
		return insets;
	}

	public int getBaseline(Component c, int width, int height) {
		if (c == null) {
			throw new NullPointerException("Must supply non-null component");
		}
		if (width < 0) {
			throw new IllegalArgumentException("Width must be >= 0");
		}
		if (height < 0) {
			throw new IllegalArgumentException("Height must be >= 0");
		}
		Border border = getBorder();

		if (this.titleComponent != null) {
			int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;
			Dimension size = this.titleComponent.getPreferredSize();
			Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));

			int baseline = this.titleComponent.getBaseline(size.width, size.height);
			switch (getPosition()) {
				case ABOVE_TOP:
					return baseline;
				case TOP:
					insets.top = edge + (insets.top - size.height) / 2;
					return (insets.top < edge) ? baseline : baseline + insets.top;
				case BELOW_TOP:
					return baseline + insets.top + edge;
				case ABOVE_BOTTOM:
					return baseline + height - size.height - insets.bottom - edge;
				case BOTTOM:
					insets.bottom = edge + (insets.bottom - size.height) / 2;
					return (insets.bottom < edge) ? baseline + height - size.height : baseline + height - size.height + insets.bottom;
				case BELOW_BOTTOM:
					return baseline + height - size.height;
			}
		}
		return -1;
	}

	public static void main(String args[]) {
		// Demo of the component
		JFrame mainWindow = new JFrame("Component boder demo");

		JTextField tfOne = new JTextField("Hello");
		JTextField tfTwo = new JTextField("There");
		JTextField tfThree = new JTextField("Comrade");
		JTextField tfFour = new JTextField("Hey there");
		JTextField tfFive = new JTextField("Hey here");

		JComboBox<String> comboBox = new JComboBox<String>(new String[] { "One", "Two", "Three" });
		JCheckBox checkBox = new JCheckBox("A checkbox!");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 1));

		tfOne.setBorder(BorderFactory.createTitledBorder("Usual titled border"));
		tfTwo.setBorder(new ExtendedTitledBorder(checkBox, tfTwo));
		tfThree.setBorder(new ExtendedTitledBorder(comboBox, tfThree));
		tfFive.setBorder(new ExtendedTitledBorder(tfFour, tfFive));

		panel.add(tfOne);
		panel.add(tfTwo);
		panel.add(tfThree);
		panel.add(tfFive);

		mainWindow.getContentPane().setLayout(new BorderLayout());
		mainWindow.getContentPane().add(panel, BorderLayout.CENTER);

		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.pack();
		{
			// Move to screen center
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension componentSize = mainWindow.getSize();
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
			mainWindow.setLocation(newComponentX, newComponentY);
		}

		mainWindow.setVisible(true);

	}
}
