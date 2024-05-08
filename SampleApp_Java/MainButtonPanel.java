//------------------------------------------------------------------------------
//
// This software is provided "AS IS".  360Commerce MAKES NO
// REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NON-INFRINGEMENT. 360Commerce shall not be liable for
// any damages suffered as a result of using, modifying or distributing this
// software or its derivatives. Permission to use, copy, modify, and distribute
// the software and its documentation for any purpose is hereby granted.
//
// MainButtonPanel.java - A common panel used by the other panels to hold buttons.
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6/28/04
//------------------------------------------------------------------------------
// final framework completed 7-14-2004
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import jpos.*;

public class MainButtonPanel extends JPanel {

    private JComboBox logicalNameComboBox;
    public  JTextField logicalNameTextField;
    public  JLabel currentStatus;
    private JButton openButton;
    private JButton claimButton;
    private JButton releaseButton;
    private JButton closeButton;
    private JButton infoButton;
    private JButton oceButton;
    private JButton exitButton;

    public MainButtonPanel(ActionListener actionListener, String defaultSelection)
    {
        JLabel logicalNameLabel = new JLabel("Logical name: ");
        add(logicalNameLabel);

        logicalNameTextField = new JTextField(15);
        logicalNameTextField.setText(defaultSelection);
        add(logicalNameTextField);

        currentStatus = new JLabel("unknown");
        //add(currentStatus);

        openButton = new JButton("Open");
        openButton.setActionCommand("open");
        openButton.addActionListener(actionListener);
        openButton.setToolTipText("open a device");			// 6/21
        add(openButton);

        claimButton = new JButton("Claim");
        claimButton.setActionCommand("claim");
        claimButton.addActionListener(actionListener);	
        claimButton.setToolTipText("claim a device");		// 6/21
        add(claimButton);

        releaseButton = new JButton("Release");
        releaseButton.setActionCommand("release");
        releaseButton.addActionListener(actionListener);
		releaseButton.setToolTipText("release a device");	// 6/21
        add(releaseButton);

        closeButton = new JButton("Close");
        closeButton.setActionCommand("close");
        closeButton.addActionListener(actionListener);
		closeButton.setToolTipText("close a device");		// 6/21
        add(closeButton);

        infoButton = new JButton("Info");
        infoButton.setActionCommand("info");
        infoButton.addActionListener(actionListener);
		infoButton.setToolTipText("info of device");		// 6/21
        add(infoButton);
        
        oceButton = new JButton("Fast mode");
        oceButton.setActionCommand("oce");
        oceButton.addActionListener(actionListener);
        oceButton.setToolTipText("open claim enable <fast mode>");
        add(oceButton);
        
		exitButton = new JButton("EXIT"); 			        // 6/21
		exitButton.setActionCommand("exit");
		exitButton.addActionListener(actionListener);
		exitButton.setToolTipText("exit program");
		add(exitButton);

        setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); 


    }

    public void action(ActionEvent ae) {
        if(ae.getActionCommand().equals("open")){
       //     currentStatus.setText("Open");
       //     logicalNameTextField.setEnabled(false);
        }
        if(ae.getActionCommand().equals("claim")){
       //     currentStatus.setText("Claim");
        }
        if(ae.getActionCommand().equals("release")){
       //     currentStatus.setText("Release");
        }
        if(ae.getActionCommand().equals("close")){
       //     currentStatus.setText("Close");
       //     logicalNameTextField.setEnabled(true);
        }
		if(ae.getActionCommand().equals("info")){			
		//     currentStatus.setText("Info");
		}
		if(ae.getActionCommand().equals("oce")){			// added 7/14
		//     currentStatus.setText("O-C-E");
		}
        if(ae.getActionCommand().equals("exit")){			// added 6/21
        	   System.out.println("...Exit JavaPOS Tester...");
        	   System.exit(0);        	  
        }
    }

    public String getLogicalName()
    {
        String logicalName = (String)logicalNameTextField.getText();
        return logicalName;
    }

    public static String getErrorName(int errorCode){
    	String val;
    	switch (errorCode){
    		case JposConst.JPOS_SUCCESS:
    			val = "JPOS_SUCCESS";
    			break;
    		case JposConst.JPOS_E_CLOSED:
    			val = "JPOS_E_CLOSED";
    			break;
    		case JposConst.JPOS_E_CLAIMED:
    			val = "JPOS_E_CLAIMED";
    			break;
    		case JposConst.JPOS_E_NOTCLAIMED:
    			val = "JPOS_E_NOTCLAIMED";
    			break;
    		case JposConst.JPOS_E_NOSERVICE:
    			val = "JPOS_E_NOSERVICE";
    			break;
    		case JposConst.JPOS_E_DISABLED:
    			val = "JPOS_E_DISABLED";
    			break;
    		case JposConst.JPOS_E_ILLEGAL:
    			val = "JPOS_E_ILLEGAL";
    			break;
    		case JposConst.JPOS_E_NOHARDWARE:
    			val = "JPOS_E_NOHARDWARE";
    			break;
    		case JposConst.JPOS_E_OFFLINE:
    			val = "JPOS_E_OFFLINE";
    			break;
    		case JposConst.JPOS_E_NOEXIST:
    			val = "JPOS_E_NOEXIST";
    			break;
    		case JposConst.JPOS_E_EXISTS:
    			val = "JPOS_E_EXISTS";
    			break;
    		case JposConst.JPOS_E_FAILURE:
    			val = "JPOS_E_FAILURE";
    			break;
    		case JposConst.JPOS_E_TIMEOUT:
    			val = "JPOS_E_TIMEOUT";
    			break;
    		case JposConst.JPOS_E_BUSY:
    			val = "JPOS_E_BUSY";
    			break;
    		case JposConst.JPOS_E_EXTENDED:
    			val = "JPOS_E_EXTENDED";
    			break;
    		default:
    			val="Unknown";
    			break;
    	}
    	return val;
    }
}

 
