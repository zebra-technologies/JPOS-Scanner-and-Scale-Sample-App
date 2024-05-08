/*
 * Created on Jun 21, 2004
  */
  
package com.jpos.POStest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ExitPanel extends Component {
					   
 	public Component make() {
		
		JPanel subPanel = new JPanel(false);
		JLabel  jlabel = new JLabel("Exit the Program");
		subPanel.add(jlabel);
				
		JButton exit = new JButton("EXIT");
		exit.setToolTipText("Terminate");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				exitProgram();
			}
		});
				
		subPanel.add(exit);
		
		return subPanel;
	}
	
	private void exitProgram() {
		System.out.println("...Exit JavaPOS Tester...\n");
		System.exit(0);
	}

}
