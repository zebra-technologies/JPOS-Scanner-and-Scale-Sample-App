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
// ScalePanel.java - The Scale panel of POStest
//
//------------------------------------------------------------------------------
// contribution of interface/implementation    Rory K. Shaw/Raleigh/IBM   7/7/04
//------------------------------------------------------------------------------
// framework added for o-c-e 7-14-2004
//------------------------------------------------------------------------------
package com.jpos.POStest;

import com.zebra.jpos.serviceonscale.directio.DirectIOCommand;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class ScalePanel extends Component implements StatusUpdateListener,
        ActionListener {

    protected MainButtonPanel mainButtonPanel;

    private Scale scale;

    private String defaultLogicalName = "ZebraScale";
    private String logicalName = "";

    private JCheckBox deviceEnabledCB;

    private JTextArea statusTextArea;

    boolean scaleOpened = false;

    private JButton scaleReadWeightButton = new JButton("Read Weight");
    private JTextField scaleCurrWeight = new JTextField(10);

    private JButton scaleDisplayTextButton = new JButton("Display Text");
    private JButton scaleZeroScaleButton = new JButton("Zero Scale");
    
    private JTextField healthCheckStatus = new JTextField();
    private JButton internalHealthCheckButton = new JButton("Get Internal Health Check Text");
    private JButton externalHealthCheckButton = new JButton("Get External Health Check Text");

    JComboBox comboDIOOpcodes;
    private JTextField jtxtCSStatus;
    private JTextArea jtxtDIOInXml;
    private JTextArea jtxtDIOoutXml;

    public ScalePanel() {
        scale = new Scale();
    }

    public Component make() {

        JPanel mainPanel = new JPanel(false);
        //mainPanel.setBackground(Color.red);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);

        // MethodListener methodListener = new MethodListener();
        mainButtonPanel = new MainButtonPanel(this, defaultLogicalName);
        mainPanel.add(mainButtonPanel);

        JPanel subPanel = new JPanel();
        //subPanel.setBackground(Color.GREEN);
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

        JPanel propPanel = new JPanel();
        //propPanel.setBackground(Color.MAGENTA);
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));

        deviceEnabledCB = new JCheckBox("Device enabled");
        deviceEnabledCB.setEnabled(false);
        propPanel.add(deviceEnabledCB);
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);

        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);

        mainPanel.add(subPanel);

        JPanel scalePanel = new JPanel();
        scalePanel.setMinimumSize(new Dimension(300, Short.MAX_VALUE));
        //scalePanel.setBackground(Color.ORANGE);
        scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));

        JPanel scalemethods = new JPanel();
        //scalemethods.setBackground(Color.PINK);
        scalemethods.setLayout(new BoxLayout(scalemethods, BoxLayout.Y_AXIS));
        scalemethods.setBorder(new TitledBorder("Method"));

        JPanel jp1 = new JPanel();
        jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
        jp1.add(scaleReadWeightButton);
        jp1.add(Box.createHorizontalGlue());
        scaleCurrWeight.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        scaleCurrWeight.setEditable(false);
        scaleCurrWeight.setFont(scalePanel.getFont().deriveFont(Font.BOLD));
        jp1.add(scaleCurrWeight);
        scalemethods.add(jp1);
        scalemethods.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel jp2 = new JPanel();
        jp2.setLayout(new BoxLayout(jp2, BoxLayout.X_AXIS));
        jp2.add(Box.createHorizontalGlue());
        jp2.add(scaleZeroScaleButton);
        scalemethods.add(jp2);
        scalemethods.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel jp4 = new JPanel();
        jp4.setLayout(new BoxLayout(jp4, BoxLayout.X_AXIS));
        JLabel healthCheckStatusLbl = new JLabel("Scale Health: ");
        healthCheckStatus.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        jp4.add(healthCheckStatusLbl);
        jp4.add(healthCheckStatus);
        scalemethods.add(jp4);
        scalemethods.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel jp3 = new JPanel();
        jp3.setLayout(new BoxLayout(jp3, BoxLayout.X_AXIS));
        jp3.add(internalHealthCheckButton);
        jp3.add(Box.createHorizontalGlue());
        scalemethods.add(jp3);
        scalemethods.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JPanel jp5 = new JPanel();
        jp5.setLayout(new BoxLayout(jp5, BoxLayout.X_AXIS));
        jp5.add(externalHealthCheckButton);
        jp5.add(Box.createHorizontalGlue());
        scalemethods.add(jp5);
        
        scalePanel.add(scalemethods);

        scalePanel.add(Box.createVerticalGlue());

        subPanel.add(scalePanel);

        JPanel directIOPanel = new JPanel();
        directIOPanel.setLayout(new BoxLayout(directIOPanel, BoxLayout.Y_AXIS));
        directIOPanel.setBorder(BorderFactory.createTitledBorder("Direct I/O"));

        JPanel dioSubPanel = new JPanel();
        dioSubPanel.setLayout(new BoxLayout(dioSubPanel, BoxLayout.X_AXIS));
        dioSubPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel labelDIOCommand = new JLabel("DIO Op-Code");
        dioSubPanel.add(labelDIOCommand);
        dioSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        String[] dioOpcodes = {"GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_SET", "RSM_ATTR_STORE", "NCR_DIO_SCAL_LIVE_WEIGHT"};
        comboDIOOpcodes = new JComboBox(dioOpcodes);
        comboDIOOpcodes.setAlignmentX(LEFT_ALIGNMENT);
        comboDIOOpcodes.setMaximumSize(new Dimension(200, 25));
        comboDIOOpcodes.addActionListener(this);

        dioSubPanel.add(comboDIOOpcodes);
        dioSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));

      
        dioSubPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        jtxtCSStatus = new JTextField(30);
        jtxtCSStatus.setMaximumSize(new Dimension(25, 25));
        jtxtCSStatus.setEditable(false);
        dioSubPanel.add(jtxtCSStatus);

        directIOPanel.add(dioSubPanel);

        JLabel DIOInxmlLabel = new JLabel("Direct IO InXml");
        DIOInxmlLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        DIOInxmlLabel.setAlignmentX(LEFT_ALIGNMENT);
        directIOPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        directIOPanel.add(DIOInxmlLabel);

        jtxtDIOInXml = new JTextArea();
        JScrollPane scrlPaneForInXml = new JScrollPane(jtxtDIOInXml);
        scrlPaneForInXml.setMinimumSize(new Dimension(150, 150));
        scrlPaneForInXml.setAlignmentX(LEFT_ALIGNMENT);
        directIOPanel.add(scrlPaneForInXml);
        

        JPanel dioSubPanel2 = new JPanel();
        dioSubPanel2.setLayout(new BoxLayout(dioSubPanel2, BoxLayout.X_AXIS));
        dioSubPanel2.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnDIO = new JButton("Execute");
        btnDIO.setActionCommand("DIO");
        btnDIO.addActionListener(this);
        dioSubPanel2.add(btnDIO);
        
        dioSubPanel2.add(Box.createRigidArea(new Dimension(20, 0)));

        JButton btnDIOClear = new JButton("Clear");
        btnDIOClear.setActionCommand("DIOClear");
        btnDIOClear.addActionListener(this);
        dioSubPanel2.add(btnDIOClear);
        directIOPanel.add(dioSubPanel2);

        JLabel DIOOutxmlLabel = new JLabel("Direct IO OutXml");
        //DIOOutxmlLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        DIOOutxmlLabel.setAlignmentX(LEFT_ALIGNMENT);
        directIOPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        directIOPanel.add(DIOOutxmlLabel);

        jtxtDIOoutXml = new JTextArea();
        jtxtDIOoutXml.setEditable(false);
	jtxtDIOoutXml.setWrapStyleWord(true);
        jtxtDIOoutXml.setLineWrap(true);
        JScrollPane scrlPaneForOutXml = new JScrollPane(jtxtDIOoutXml);
        scrlPaneForOutXml.setMinimumSize(new Dimension(150, 150));
        scrlPaneForOutXml.setAlignmentX(LEFT_ALIGNMENT);
	scrlPaneForOutXml.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        directIOPanel.add(scrlPaneForOutXml);

        subPanel.add(directIOPanel);

        //subPanel.add(Box.createHorizontalGlue());
        scaleReadWeightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    int weight[] = new int[1];
                    scale.readWeight(weight, 3000);
                    float fWeight = (float) (weight[0]) / 1000;
                    int units = scale.getWeightUnit();
                    String sUnits = "";
                    switch (units) {
                        case ScaleConst.SCAL_WU_GRAM:
                            sUnits = " g";
                            break;
                        case ScaleConst.SCAL_WU_KILOGRAM:
                            sUnits = " kg";
                            break;
                        case ScaleConst.SCAL_WU_OUNCE:
                            sUnits = " oz";
                            break;
                        case ScaleConst.SCAL_WU_POUND:
                            sUnits = " lb";
                            break;
                    }
                    scaleCurrWeight.setText(Float.toString(fWeight) + " " + sUnits);
                } catch (JposException e) {
                    //handleJposException(e);
                    JOptionPane.showMessageDialog(null, "Failed to Read Weight \"" + logicalName
                            + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });
        
        internalHealthCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String healthText = "";
                try{
                    scale.checkHealth(JposConst.JPOS_CH_INTERNAL);
                    healthText = scale.getCheckHealthText();
                } catch(Exception ex) {
                    healthText = ex.getMessage();
                    System.err.println("Jpos exception: " + ex.getMessage());
                }
                healthCheckStatus.setText(healthText);
            }
        });
        
        externalHealthCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String healthText = "";
                try{
                    scale.checkHealth(JposConst.JPOS_CH_EXTERNAL);
                    healthText = scale.getCheckHealthText();
                } catch(Exception ex) {
                    healthText = ex.getMessage();
                    System.err.println("Jpos exception: " + ex.getMessage());
                }
                healthCheckStatus.setText(healthText);
            }
        });

        scaleDisplayTextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String text = "";
                    scale.displayText(text);
                } catch (JposException e) {
                    //handleJposException(e);
                    JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName
                            + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });

        scaleZeroScaleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    scale.zeroScale();
                } catch (JposException e) {
                    //handleJposException(e);
                    JOptionPane.showMessageDialog(null, "Failed to Zero \"" + logicalName
                            + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Jpos exception " + e);
                }
            }
        });

        return mainPanel;
    }

    public void statusUpdateOccurred(StatusUpdateEvent sue) {
        System.out.println("Scale received status update event.");
    }
private void populateInXmlTextField()
    {
    String selectedOpCode =  (String)comboDIOOpcodes.getSelectedItem();
            if("GET_SCANNERS".equals(selectedOpCode))
            {
                jtxtDIOInXml.setText("");
            }else if("RSM_ATTR_GETALL".equals(selectedOpCode))
            {
                jtxtDIOInXml.setText("<inArgs>\n"+
                                     "  <scannerID>1</scannerID>\n" +
                                     "</inArgs>");
            }else if("RSM_ATTR_GET".equals(selectedOpCode))
            {
                jtxtDIOInXml.setText("<inArgs>\n" +
                                        " <scannerID>1</scannerID>\n" +
                                        " <cmdArgs>\n" +
                                        "  <arg-xml>\n" +
                                        "   <attrib_list>1</attrib_list>\n" +
                                        "  </arg-xml>\n" +
                                        " </cmdArgs>\n" +
                                        "</inArgs>");

            }else if("RSM_ATTR_SET".equals(selectedOpCode))
            {
                jtxtDIOInXml.setText("<inArgs>\n" +
                                    " <scannerID>1</scannerID>\n" +
                                    " <cmdArgs>\n" +
                                    "  <arg-xml>\n" +
                                    "   <attrib_list>\n" +
                                    "    <attribute>\n" +
                                    "     <id>6000</id>\n" +
                                    "     <datatype>X</datatype>\n" +
                                    "     <value>2</value>\n" +
                                    "    </attribute>\n" +
                                    "   </attrib_list>\n" +
                                    "  </arg-xml>\n" +
                                    " </cmdArgs>\n" +
                                    "</inArgs>");
            }else if("RSM_ATTR_STORE".equals((String)comboDIOOpcodes.getSelectedItem()))
            {
                jtxtDIOInXml.setText("<inArgs>\n" +
                                    " <scannerID>1</scannerID>\n" +
                                    " <cmdArgs>\n" +
                                    "  <arg-xml>\n" +
                                    "   <attrib_list>\n" +
                                    "    <attribute>\n" +
                                    "     <id>6000</id>\n" +
                                    "     <datatype>X</datatype>\n" +
                                    "     <value>2</value>\n" +
                                    "    </attribute>\n" +
                                    "   </attrib_list>\n" +
                                    "  </arg-xml>\n" +
                                    " </cmdArgs>\n" +
                                    "</inArgs>");
            }
    }
    /**
     * Listens to the method buttons.
     */
    public void actionPerformed(ActionEvent ae) {
        mainButtonPanel.action(ae);
        String logicalName = mainButtonPanel.getLogicalName();
        if(ae.getSource().equals(comboDIOOpcodes))
        {
            populateInXmlTextField();
        }
        if (ae.getActionCommand().equals("open")) {
            try {
                if (logicalName.equals("")) {
                    logicalName = defaultLogicalName;
                }
                scale.addStatusUpdateListener(this);
                scale.open(logicalName);
                deviceEnabledCB.setEnabled(false);
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("claim")) {
            try {
                scale.claim(0);
                deviceEnabledCB.setEnabled(true);
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to claim \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("release")) {
            try {
                scale.release();
                deviceEnabledCB.setEnabled(false);

            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to release \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("close")) {
            try {
                scale.close();
                deviceEnabledCB.setEnabled(false);

            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to close \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("info")) {
            try {
                String ver = Integer.toString(scale.getDeviceServiceVersion());
                String msg = "Service Description: " + scale.getDeviceServiceDescription();
                msg = msg + "\nService Version: v" + new Integer(ver.substring(0, 1)) + "." + new Integer(ver.substring(1, 4)) + "." + new Integer(ver.substring(4, 7));
                ver = Integer.toString(scale.getDeviceControlVersion());
                msg += "\n\nControl Description: " + scale.getDeviceControlDescription();
                msg += "\nControl Version: v" + new Integer(ver.substring(0, 1)) + "." + new Integer(ver.substring(1, 4)) + "." + new Integer(ver.substring(4, 7));
                msg += "\n\nPhysical Device Name: " + scale.getPhysicalDeviceName();
                msg += "\nPhysical Device Description: " + scale.getPhysicalDeviceDescription();

                msg += "\n\nProperties:\n------------------------";

                msg += "\nCapPowerReporting: " + (scale.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (scale.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));

                msg += "\nCapDisplay: " + scale.getCapDisplay();
                msg += "\nCapDisplayText: " + scale.getCapDisplayText();
                msg += "\nCapPriceCalculating: " + scale.getCapPriceCalculating();
                msg += "\nCapTareWeight: " + scale.getCapTareWeight();
                msg += "\nCapZeroScale: " + scale.getCapZeroScale();

                JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);

            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("oce")) {
            try {
                if (logicalName.equals("")) {
                    logicalName = defaultLogicalName;
                }
                scale.addStatusUpdateListener(this);
                scale.open(logicalName);
                scale.claim(0);
                deviceEnabledCB.setEnabled(true);
                scale.setDeviceEnabled(true);

            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to claim \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("DIO")) {
            StringBuffer inOutXml = new StringBuffer();
            String inxml = jtxtDIOInXml.getText();
            int dioCommand = 0;
            if ("GET_SCANNERS".equals((String) comboDIOOpcodes.getSelectedItem())) {
                dioCommand = DirectIOCommand.GET_SCANNERS;
            } else {
                if ("RSM_ATTR_GETALL".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_GETALL;
                } else if ("RSM_ATTR_GET".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_GET;
                } else if ("RSM_ATTR_SET".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_SET;
                } else if ("RSM_ATTR_STORE".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_STORE;
                } else if ("NCR_DIO_SCAL_LIVE_WEIGHT".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.NCR_DIO_SCAL_LIVE_WEIGHT;
                }
                inOutXml.append(jtxtDIOInXml.getText());
            }

            int[] directIOStatus = new int[1];
            directIOStatus[0] = -1;

            try {
                scale.directIO(dioCommand, directIOStatus, inOutXml);
            } catch (JposException ex) {
                System.err.println("Jpos exception: " + ex.getMessage());
            }
            if(directIOStatus[0] == 0 && !(inOutXml.toString().equals("null")))
            {
                jtxtDIOoutXml.setText(inOutXml.toString());
            }else
            {
                jtxtDIOoutXml.setText("");
            }
            jtxtCSStatus.setText(String.valueOf(directIOStatus[0]));
        }else if(ae.getActionCommand().equals("DIOClear")){
            populateInXmlTextField();
            jtxtDIOoutXml.setText("");
            jtxtCSStatus.setText("");
        }
        try {
            if ((!ae.getActionCommand().equals("open")) && (!ae.getActionCommand().equals("close"))) {
                if (scale.getClaimed()) {
                    deviceEnabledCB.setSelected(scale.getDeviceEnabled());
                } else {
                    deviceEnabledCB.setSelected(false);
                }
            }
        } catch (JposException je) {
            System.err.println("ScalePanel: MethodListener: JposException");
        }
    }

    class CheckBoxListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            try {
                if (source == deviceEnabledCB) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        scale.setDeviceEnabled(false);
                    } else {
                        scale.setDeviceEnabled(true);
                    }
                }
            } catch (JposException je) {
                System.err.println("ScalePanel: CheckBoxListener: Jpos Exception" + e);
            }
        }
    }
}
