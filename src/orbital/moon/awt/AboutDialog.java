/**
 * @(#)AboutDialog.java 0.9 2000/02/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * This class is an AboutDialog showing a nice informational dialog.
 * 
 * @version 0.9, 2000/02/01
 * @author  Andr&eacute; Platzer
 */
public
class AboutDialog {
	public static void showAboutDialog(Frame parent, String info, String title) {
		JOptionPane.showMessageDialog(parent, createPanel(info), title, JOptionPane.PLAIN_MESSAGE);
	}

	private static Component createPanel(String info) {
		JTextArea text = new JTextArea(info, 15, 42) {
			public void paintComponent(Graphics gg) {
				Graphics2D g = (Graphics2D) gg;
				Dimension  d = getSize();
				g.setPaint(new GradientPaint(0, 0, Color.green, d.width * 4 / 5, d.height * 10 / 11, Color.cyan));
				g.fillRect(0, 0, d.width, d.height);
				super.paintComponent(gg);
			} 
		};
		text.setEditable(false);
		text.setCaretPosition(0);
		text.setOpaque(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		return new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	} 
}
