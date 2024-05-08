/*
 * Created on Dec 30, 2003
 *
 * Modified June-August 2004
 * 
 */
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
/**
 * @author Jeff Lange
 *
 */
public class AboutTab extends Component{
	
	
	
	public Component make() {
		
		JPanel mainPanel = new JPanel(false);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(Box.createVerticalStrut(20));
		
		JLabel label = new JLabel("POSTest");
		Font f = label.getFont();
		Font bigfont = new Font(f.getName(), f.getStyle(), 28);
		label.setFont(bigfont);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		
		Font medfont = new Font(f.getName(), f.getStyle(), 20);
		label = new JLabel("The JavaPOS Device Tester.");
		label.setFont(medfont);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		
		label = new JLabel( POStest.version );
		label.setFont(medfont);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		mainPanel.add(Box.createVerticalStrut(20));
		
		label = new JLabel( "POStest is a community project under the direction of the JavaPOS working group.");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		
		label = new JLabel( "Original code donated by 360 Commerce.");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		mainPanel.add(Box.createVerticalStrut(20));
		
		label = new JLabel("Contributors:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		mainPanel.add(Box.createVerticalStrut(10));
		
		label = new JLabel("Jeff Lange - Ultimate Technology Corp.");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
				
		label = new JLabel("Rory K. Shaw - IBM/Raleigh RSS Drivers and Diagnostics");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label);
		return mainPanel;
	}
}
