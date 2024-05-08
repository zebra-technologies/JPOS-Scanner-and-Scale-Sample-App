//------------------------------------------------------------------------------
//
// This software is provided "AS IS".  360Commerce MAKES NO
// REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NON-INFRINGEMENT. 360Commerce shall not be liable for
// any damages suffered as directIOStatus result of using, modifying or distributing this
// software or its derivatives. Permission to use, copy, modify, and distribute
// the software and its documentation for any purpose is hereby granted.
//
// ScannerPanel.java - The Scanner panel for POStest
//
//------------------------------------------------------------------------------
// contribution of interface and implementation Rory K. Shaw/Raleigh/IBM 6-28-04
//------------------------------------------------------------------------------
// final framework completed 7-15-2004 (oce, clearData)
//------------------------------------------------------------------------------
package com.jpos.POStest;

import com.zebra.jpos.serviceonscanner.directio.DirectIOCommand;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jpos.*;
import jpos.events.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ScannerPanel extends Component implements DataListener,
        ErrorListener,
        ActionListener {

    protected MainButtonPanel mainButtonPanel;

    private Scanner scanner = null;
    private Scanner scanner2 = null;
    private boolean scn2Open = false;

    public String defaultLogicalName = "ZebraAllScanners";

    byte[] scanData = new byte[]{};
    byte[] scanDataLabel = new byte[]{};
    int scanDataType;
    int scanDataCount;
    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;
    boolean decodeData;
    boolean checkHealth;
    boolean deviceAutoEnable;

    boolean updateDevice = true;

    private JTextField scanDataTextField;
    private JTextField scanDataLabelTextField;
    private JTextField scanDataLabelHexTextField;
    private JTextField scanDataTypeTextField;
    private JTextField scanDataCountField;
    private JTextField scanHealthField;
    private JTextField statusParamField;
    private JTextField jpowerState;
    private JTextArea jtxtDIOInXml;
    private JTextArea jtxtDIOoutXml;
    private JTextField jtxtCSStatus;

    private JCheckBox claimedCB;
    private JCheckBox autoDisableCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox autoDataEventEnableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox decodeDataCB;
    private JCheckBox checkAutoEnableDeviceCB;
    private JCheckBox powerNotifyCB;

    private JButton clearDataButton;
    JComboBox comboDIOOpcodes;
//    JComboBox comboScannerID;


    private JLabel powerNotifyL;
    private JLabel indentPN;

    Runnable doUpdateGUI;

    public ScannerPanel() {
    }

    public Component make() {

        if (scanner == null) {
            scanner = new Scanner();
            scanner.addErrorListener(this);
            scanner.addDataListener(this);
            scanner2 = new Scanner();
            scanner2.addErrorListener(this);
            scanner2.addDataListener(this);
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainButtonPanel = new MainButtonPanel(this, defaultLogicalName);
        mainPanel.add(mainButtonPanel);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        claimedCB = new JCheckBox("Claimed");
        Font f = claimedCB.getFont();
        Font newf = new Font(f.getName(), Font.PLAIN, f.getSize());
        claimedCB.setFont(newf);
        propPanel.add(claimedCB);
        autoDisableCB = new JCheckBox("Auto disable");
        autoDisableCB.setFont(newf);
        propPanel.add(autoDisableCB);
        deviceEnabledCB = new JCheckBox("Device enabled");
        deviceEnabledCB.setFont(newf);
        propPanel.add(deviceEnabledCB);
        autoDataEventEnableCB = new JCheckBox("Auto data event enable");
        autoDataEventEnableCB.setFont(newf);
        propPanel.add(autoDataEventEnableCB);
        dataEventEnabledCB = new JCheckBox("Data event enabled");
        dataEventEnabledCB.setFont(newf);
        propPanel.add(dataEventEnabledCB);
        freezeEventsCB = new JCheckBox("Freeze events");
        freezeEventsCB.setFont(newf);
        propPanel.add(freezeEventsCB);
        decodeDataCB = new JCheckBox("Decode data");
        decodeDataCB.setFont(newf);
        propPanel.add(decodeDataCB);
        checkAutoEnableDeviceCB = new JCheckBox("Auto Enable Device");
        checkAutoEnableDeviceCB.setFont(newf);
        //checkAutoEnableDeviceCB.hide();
        propPanel.add(checkAutoEnableDeviceCB);
        
        powerNotifyCB = new JCheckBox("Power Notify Enable");
        powerNotifyCB.setFont(newf);
        propPanel.add(powerNotifyCB);
        propPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton buttonPowerState = new JButton("Get Power State");
        buttonPowerState.setActionCommand("PowerState");
        buttonPowerState.addActionListener(this);
        propPanel.add(buttonPowerState);
        propPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton buttonInternalHealthCheck = new JButton("Get Internal Health Check Text");
        buttonInternalHealthCheck.setActionCommand("InternalHealthCheck");
        buttonInternalHealthCheck.addActionListener(this);
        propPanel.add((buttonInternalHealthCheck));
        propPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        JButton buttonExternalHealthCheck = new JButton("Get External Health Check Text");
        buttonExternalHealthCheck.setActionCommand("ExternalHealthCheck");
        buttonExternalHealthCheck.addActionListener(this);
        propPanel.add((buttonExternalHealthCheck));
        propPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        JButton buttonInteractiveHealthCheck = new JButton("Get Interactive Health Check Text");
        buttonInteractiveHealthCheck.setActionCommand("InteractiveHealthCheck");
        buttonInteractiveHealthCheck.addActionListener(this);
        propPanel.add((buttonInteractiveHealthCheck));
        propPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        JButton buttonClearInputProp = new JButton("Clear Input Properties");
        buttonClearInputProp.setActionCommand("ClearInputProperties");
        buttonClearInputProp.addActionListener(this);
        propPanel.add((buttonClearInputProp));
        propPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        statusParamField = new JTextField(30);
        statusParamField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        propPanel.add(statusParamField);
        
        JButton buttonGetStat = new JButton("Get Statistics");
        buttonGetStat.setActionCommand("GetStatistics");
        buttonGetStat.addActionListener(this);
        propPanel.add((buttonGetStat));
        
        JButton buttonResetStat = new JButton("Reset Statistics");
        buttonResetStat.setActionCommand("ResetStatistics");
        buttonResetStat.addActionListener(this);
        propPanel.add((buttonResetStat));

        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);

        claimedCB.setEnabled(false);
        autoDisableCB.setEnabled(true);
        deviceEnabledCB.setEnabled(true);
        autoDataEventEnableCB.setEnabled(true);
        dataEventEnabledCB.setEnabled(true);
        freezeEventsCB.setEnabled(true);
        decodeDataCB.setEnabled(true);

        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        decodeDataCB.addItemListener(cbListener);
        checkAutoEnableDeviceCB.addItemListener(cbListener);
        powerNotifyCB.addItemListener(cbListener);



        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setMaximumSize(new Dimension(1000, Short.MAX_VALUE));
        
        JLabel scanDataLabel1 = new JLabel("Scan Data: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        scanDataLabel1 = new JLabel("Scan Data Type: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        scanDataLabel1 = new JLabel("Scan Data Label: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        scanDataLabel1 = new JLabel("Scan Data Label [Hex]: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        scanDataLabel1 = new JLabel("Scan Data Count: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        scanDataLabel1 = new JLabel("Scanner Health: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        scanDataLabel1 = new JLabel("Power State: ");
        scanDataLabel1.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        labelPanel.add(scanDataLabel1);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        subPanel.add(labelPanel);
        labelPanel.add(Box.createVerticalGlue());

        JPanel tfPanel = new JPanel();
        tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.Y_AXIS));

        scanDataTextField = new JTextField(30);
        scanDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(scanDataTextField);
        scanDataTypeTextField = new JTextField(30);
        scanDataTypeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(scanDataTypeTextField);
        scanDataLabelTextField = new JTextField(30);
        scanDataLabelTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(scanDataLabelTextField);
        scanDataLabelHexTextField = new JTextField(30);
        scanDataLabelHexTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(scanDataLabelHexTextField);
        scanDataCountField = new JTextField(30);
        scanDataCountField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(scanDataCountField);
        scanHealthField = new JTextField(30);
        scanHealthField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(scanHealthField);
        jpowerState = new JTextField(30);
        jpowerState.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tfPanel.add(jpowerState);

        clearDataButton = new JButton("Clear Data");
        clearDataButton.setAlignmentX(LEFT_ALIGNMENT);
        tfPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        tfPanel.add(clearDataButton);
        tfPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel dioPanel = new JPanel();
        dioPanel.setAlignmentX(LEFT_ALIGNMENT);
        dioPanel.setLayout(new BoxLayout(dioPanel, BoxLayout.Y_AXIS));
        //dioPanel.setBackground(Color.red);
        dioPanel.setBorder(BorderFactory.createTitledBorder("DIrect I/O"));
        
        JPanel dioSubPanel = new JPanel();
        dioSubPanel.setLayout(new BoxLayout(dioSubPanel, BoxLayout.X_AXIS));
        dioSubPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel labelDIOCommand = new JLabel("DIO Op-Code");
        dioSubPanel.add(labelDIOCommand);
        dioSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        String[] dioOpcodes = {"GET_SCANNERS","RSM_ATTR_GETALL","RSM_ATTR_GET","RSM_ATTR_SET","RSM_ATTR_STORE","DIO_NCR_SCANNER_NOF", "DIO_NCR_SCANNER_TONE", "DIO_SCANNER_NOT_ON_FILE", "DIO_SCANNER_DIO_NOF"};
        comboDIOOpcodes = new JComboBox(dioOpcodes);
        comboDIOOpcodes.setAlignmentX(LEFT_ALIGNMENT);
        comboDIOOpcodes.setMaximumSize(new Dimension(200, 25));
        comboDIOOpcodes.addActionListener(this);
        dioSubPanel.add(comboDIOOpcodes);
        
        dioSubPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        
        dioSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        jtxtCSStatus = new JTextField(30);
        jtxtCSStatus.setMaximumSize(new Dimension(25,25));
        jtxtCSStatus.setEditable(false);
        dioSubPanel.add(jtxtCSStatus);


        dioPanel.add(dioSubPanel);
        
        JLabel DIOInxmlLabel = new JLabel("Direct IO InXml");
        DIOInxmlLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        DIOInxmlLabel.setAlignmentX(LEFT_ALIGNMENT);
        dioPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        dioPanel.add(DIOInxmlLabel);
        
        jtxtDIOInXml = new JTextArea();
        JScrollPane scrlPaneForInXml = new JScrollPane(jtxtDIOInXml);
        scrlPaneForInXml.setAlignmentX(LEFT_ALIGNMENT);
        scrlPaneForInXml.setMinimumSize(new Dimension(100, 100));
        dioPanel.add(scrlPaneForInXml);
        
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
        dioPanel.add(dioSubPanel2);
        
        JLabel DIOOutxmlLabel = new JLabel("Direct IO OutXml");
        //DIOOutxmlLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        DIOOutxmlLabel.setAlignmentX(LEFT_ALIGNMENT);
        dioPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dioPanel.add(DIOOutxmlLabel);
        
        jtxtDIOoutXml = new JTextArea();
        jtxtDIOoutXml.setEditable(false);
	jtxtDIOoutXml.setWrapStyleWord(true);
        jtxtDIOoutXml.setLineWrap(true);
        //jtxtDIOoutXml.setMinimumSize(new Dimension(100, 100));
        JScrollPane scrlPaneForOutXml = new JScrollPane(jtxtDIOoutXml);
        scrlPaneForOutXml.setMinimumSize(new Dimension(100, 100));
        scrlPaneForOutXml.setAlignmentX(LEFT_ALIGNMENT);
	scrlPaneForOutXml.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);       
        dioPanel.add(scrlPaneForOutXml);
        


        //dioPanel.setBackground(Color.BLUE);
        tfPanel.add(dioPanel);
        
        //tfPanel.setBackground(Color.red);

        //tfPanel.add(Box.createVerticalGlue());

        subPanel.add(tfPanel);
        
        //subPanel.setBackground(Color.GREEN);

        mainPanel.add(subPanel);
        
        //mainPanel.setBackground(Color.MAGENTA);
        //mainPanel.add(Box.createVerticalGlue());

        doUpdateGUI = new Runnable() {
            public void run() {
                updateGUI();
            }
        };

        clearDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                scanData = new byte[]{};
                scanDataLabel = new byte[]{};
                scanDataType = 0;
                scanDataTextField.setText(new String(scanData));
                scanDataLabelTextField.setText("");
                scanDataLabelHexTextField.setText("");
                scanDataTypeTextField.setText(Integer.toString(scanDataType));
                scanHealthField.setText("");
                jpowerState.setText("");
                
                try {
                    if (scanner.getClaimed()) {
                        scanDataCount = scanner.getDataCount();
                    } else {
                        scanDataCount = 0;
                    }
                } catch (JposException e) {
                }
                scanDataCountField.setText(Integer.toString(scanDataCount));
            }
        });

        return mainPanel;
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

                scanner.open(logicalName);
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on open " + e);
            }
        } else if (ae.getActionCommand().equals("claim")) {
            try {
                scanner.claim(0);
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to claim \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on claim " + e);
            }
        } else if (ae.getActionCommand().equals("release")) {
            try {
                scanner.release();
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to release \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on release " + e);
            }
        } else if (ae.getActionCommand().equals("close")) {
            try {
                scanner.close();
                updateDevice = false;
                claimedCB.setSelected(false);
                autoDisableCB.setSelected(false);
                deviceEnabledCB.setSelected(false);
                dataEventEnabledCB.setSelected(false);
                freezeEventsCB.setSelected(false);
                decodeDataCB.setSelected(false);
                updateDevice = true;
                if (scn2Open) {
                    scanner2.close();
                    scn2Open = false;
                }
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to close \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception on close" + e);
            }
        } else if (ae.getActionCommand().equals("info")) {
            try {
                if (scanner.getState() != JposConst.JPOS_S_CLOSED) {
                    String ver;
                    ver = Integer.toString(scanner.getDeviceServiceVersion());
                    String msg = "Service Description: " + scanner.getDeviceServiceDescription();
                    msg = msg + "\nService Version: v" + new Integer(ver.substring(0, 1)) + "." + new Integer(ver.substring(1, 4)) + "." + new Integer(ver.substring(4, 7));
                    ver = Integer.toString(scanner.getDeviceControlVersion());
                    msg += "\n\nControl Description: " + scanner.getDeviceControlDescription();
                    msg += "\nControl Version: v" + new Integer(ver.substring(0, 1)) + "." + new Integer(ver.substring(1, 4)) + "." + new Integer(ver.substring(4, 7));
                    msg += "\n\nPhysical Device Name: " + scanner.getPhysicalDeviceName();
                    msg += "\nPhysical Device Description: " + scanner.getPhysicalDeviceDescription();

                    msg += "\n\nProperties:\n------------------------";
                    msg += "\nCapPowerReporting: " + (scanner.getCapPowerReporting() == JposConst.JPOS_PR_ADVANCED ? "Advanced" : (scanner.getCapPowerReporting() == JposConst.JPOS_PR_STANDARD ? "Standard" : "None"));
                    JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Exception in Info\nException: " + e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if (ae.getActionCommand().equals("oce")) {
            try {
                if (logicalName.equals("")) {
                    logicalName = defaultLogicalName;
                }
                if (logicalName.equals("testopen")) {
                    int i;
                    for (i = 0; i < 5000; ++i) {
                        System.out.println("Test cycle: " + Integer.toString(i));
                        logicalName = "SymbolScannerUSB";
                        scanner.open(logicalName);
                        scanner.claim(1000);
                        scanner.setDeviceEnabled(true);
                        scanner.setDataEventEnabled(true);
                        scanner.close();
                    }
                } else {
                    if (logicalName.equals("test2")) {
                        logicalName = "SymbolUSBHandHeld";
                        try {
                            scanner2.open("SymbolUSBTableTop");
                            scn2Open = true;
                            scanner2.claim(1000);
                            scanner2.setDeviceEnabled(true);
                            scanner2.setDataEventEnabled(true);
                        } catch (JposException e) {
                            System.out.println("Jpos exception " + e);
                        }
                    }
                    scanner.open(logicalName);
                    scanner.claim(1000);
                    scanner.setDeviceEnabled(true);
                    scanner.setDataEventEnabled(true);
                }
            } catch (JposException e) {
                JOptionPane.showMessageDialog(null, "Failed to claim \"" + logicalName + "\"\nException: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                System.err.println("Jpos exception " + e);
            }
        } else if(ae.getActionCommand().equals("DIO")){
            StringBuffer inOutXml = new StringBuffer();
            String inxml = jtxtDIOInXml.getText();
            int dioCommand=0;
            if("GET_SCANNERS".equals((String)comboDIOOpcodes.getSelectedItem()))
            {
                dioCommand = DirectIOCommand.GET_SCANNERS;
            }else 
            {
                if ("RSM_ATTR_GETALL".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_GETALL;
                } else if ("RSM_ATTR_GET".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_GET;
                } else if ("RSM_ATTR_SET".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_SET;
                } else if ("RSM_ATTR_STORE".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.RSM_ATTR_STORE;
                } else if ("DIO_NCR_SCANNER_NOF".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.DIO_NCR_SCANNER_NOF;
                } else if ("DIO_NCR_SCANNER_TONE".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.DIO_NCR_SCAN_TONE;
                } else if ("DIO_SCANNER_NOT_ON_FILE".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.DIO_SCANNER_NOT_ON_FILE;
                } else if ("DIO_SCANNER_DIO_NOF".equals((String) comboDIOOpcodes.getSelectedItem())) {
                    dioCommand = DirectIOCommand.DIO_SCANNER_DIO_NOF;
                }
                inOutXml.append(jtxtDIOInXml.getText());
            }
            
            int[] directIOStatus = new int[1];
            directIOStatus[0] = -1;

            try {
                scanner.directIO(dioCommand, directIOStatus, inOutXml);
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
        }else if(ae.getActionCommand().equals("InternalHealthCheck"))
        {
            String healthText = "";
            try {
                scanner.checkHealth(JposConst.JPOS_CH_INTERNAL);
                healthText = scanner.getCheckHealthText();
            } catch (JposException je) {
                healthText = je.getMessage();
                System.err.println("Jpos exception: " + je.getMessage());
            }
            scanHealthField.setText(healthText);
        }else if(ae.getActionCommand().equals("ExternalHealthCheck"))
        {
            String healthText = "";
            try {
                scanner.checkHealth(JposConst.JPOS_CH_EXTERNAL);
                healthText = scanner.getCheckHealthText();
            } catch (JposException je) {
                healthText = je.getMessage();
                System.err.println("Jpos exception: " + je.getMessage());
            }
            scanHealthField.setText(healthText);
        }else if(ae.getActionCommand().equals("InteractiveHealthCheck"))
        {
            String healthText = "";
            try {
                scanner.checkHealth(JposConst.JPOS_CH_INTERACTIVE);
                healthText = scanner.getCheckHealthText();
            } catch (JposException je) {
                healthText = je.getMessage();
                System.err.println("Jpos exception: " + je.getMessage());
            }
            scanHealthField.setText(healthText);
        }else if(ae.getActionCommand().equals("ClearInputProperties"))
        {
            try {
                scanner.clearInputProperties();
                
                // Fetch data from the service layer after performing clearInputProperties
                scanData = scanner.getScanData();
                scanDataLabel = scanner.getScanDataLabel();
                scanDataType = scanner.getScanDataType();
            } catch (JposException je) {
                System.err.println("Jpos exception: " + je.getMessage());
            }
        }else if(ae.getActionCommand().equals("GetStatistics"))
        {
            try {
                String[] astrStatsBuffer = new String[1];
                astrStatsBuffer[0] = statusParamField.getText();
                scanner.retrieveStatistics(astrStatsBuffer);
                JOptionPane.showMessageDialog(null, formatXml(astrStatsBuffer[0].toString()));
                
            } catch (JposException je) {
                System.err.println("Jpos exception: " + je.getMessage());
            }
        }else if(ae.getActionCommand().equals("ResetStatistics"))
        {
            try {
                String[] astrStatsBuffer = new String[1];
                astrStatsBuffer[0] = statusParamField.getText();
                scanner.resetStatistics(astrStatsBuffer[0].toString());
                
            } catch (JposException je) {
                System.err.println("Jpos exception: " + je.getMessage());
            }
        }else if(ae.getActionCommand().equals("PowerState"))
        {
            String powerState = "";
            int ipowerState;
            try {
                    ipowerState = scanner.getPowerState();
                    if (ipowerState == JposConst.JPOS_PS_UNKNOWN) {
                        powerState = "JPOS_PS_UNKNOWN";
                    } else if (ipowerState == JposConst.JPOS_PS_ONLINE) {
                        powerState = "JPOS_PS_ONLINE";
                    } else if (ipowerState == JposConst.JPOS_PS_OFF) {
                        powerState = "JPOS_PS_OFF";
                    }
                    jpowerState.setText(powerState);
            } catch (JposException je) {
                System.err.println("Jpos Exception: " + je.getMessage());
            }
        }
        if (scanner.getState() != JposConst.JPOS_S_CLOSED) {
            updateGUI();
        }

    }

    public static String getBarcodeTypeName(int code) {
        String val = "Unknown";
        switch (code) {
            case ScannerConst.SCAN_SDT_UPCA:
                val = "UPC-A";
                break;
            case ScannerConst.SCAN_SDT_UPCE:
                val = " UPC-E";
                break;
            case ScannerConst.SCAN_SDT_JAN8:
                val = "JAN 8 / EAN 8";
                break;
            case ScannerConst.SCAN_SDT_JAN13:
                val = "JAN 13 / EAN 13";
                break;
            case ScannerConst.SCAN_SDT_TF:
                val = "2 of 5";
                break;
            case ScannerConst.SCAN_SDT_ITF:
                val = "Interleaved 2 of 5";
                break;
            case ScannerConst.SCAN_SDT_Codabar:
                val = "Codabar";
                break;
            case ScannerConst.SCAN_SDT_Code39:
                val = "Code 39";
                break;
            case ScannerConst.SCAN_SDT_Code93:
                val = "Code 93";
                break;
            case ScannerConst.SCAN_SDT_Code128:
                val = "Code 128";
                break;
            case ScannerConst.SCAN_SDT_UPCA_S:
                val = " UPC-A with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_UPCE_S:
                val = "UPC-E with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_UPCD1:
                val = "UPC-D1";
                break;
            case ScannerConst.SCAN_SDT_UPCD2:
                val = "UPC-D2";
                break;
            case ScannerConst.SCAN_SDT_UPCD3:
                val = "UPC-D3";
                break;
            case ScannerConst.SCAN_SDT_UPCD4:
                val = "UPC-D4";
                break;
            case ScannerConst.SCAN_SDT_UPCD5:
                val = "UPC-D5";
                break;
            case ScannerConst.SCAN_SDT_EAN8_S:
                val = "EAN-8 with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_EAN13_S:
                val = "EAN-13 with Supplemental";
                break;
            case ScannerConst.SCAN_SDT_EAN128:
                val = "EAN-128";
                break;
            case ScannerConst.SCAN_SDT_OCRA:
                val = "OCR \"A\"";
                break;
            case ScannerConst.SCAN_SDT_OCRB:
                val = "OCR \"B\"";
                break;
            case ScannerConst.SCAN_SDT_PDF417:
                val = "PDF 417";
                break;
            case ScannerConst.SCAN_SDT_MAXICODE:
                val = "MAXICODE";
                break;
            case ScannerConst.SCAN_SDT_RSS_EXPANDED:
                val = "GS1 Databar Expanded";
                break;
            case ScannerConst.SCAN_SDT_AZTEC:
                val = "AZTEC";
                break;
            case ScannerConst.SCAN_SDT_DATAMATRIX:
                val = "Data Matrix";
                break;

            case ScannerConst.SCAN_SDT_QRCODE:
                val = "QR Code";
                break;

            case ScannerConst.SCAN_SDT_UQRCODE:
                val = "Micro QR Code";
                break;

            case ScannerConst.SCAN_SDT_UPDF417:
                val = "Micro PDF417";
                break;
                
            case ScannerConst.SCAN_SDT_TFMAT:
                val = "Matrix 2 of 5";
                break;
                
            case ScannerConst.SCAN_SDT_UsPlanet:
                val = "US Planet";
                break;
                
            case ScannerConst.SCAN_SDT_ISBT128:
                val = "ISBT 128";
                break;
                
            case ScannerConst.SCAN_SDT_MSI:
                val = "MSI";
                break;
                
            case ScannerConst.SCAN_SDT_GS1DATAMATRIX:
                val = "GS1 DataMatrix";
                break;

            case ScannerConst.SCAN_SDT_GS1QRCODE:
                val = "GS1 QR Code";
                break;
                
            case ScannerConst.SCAN_SDT_DutchKix:
                val = "Dutch Postal";
                break;
                
            case ScannerConst.SCAN_SDT_JapanPost:
                val = "Japan Postal";
                break;
                
            case ScannerConst.SCAN_SDT_AusPost:
                val = "Australian Postal";
                break;
                
            case ScannerConst.SCAN_SDT_UkPost:
                val = "UK Postal";
                break;
                
            case ScannerConst.SCAN_SDT_PLESSEY:
                val = "Plessey Code";
                break;
                
            case ScannerConst.SCAN_SDT_GS1DATABAR:
                val = "GS1 Databar";
                break;
                
            case ScannerConst.SCAN_SDT_UNKNOWN:
                val = "Unknown";
                break;

            case ScannerConst.SCAN_SDT_OTHER:
                val = "Other";
                break;
        }
        return val;
    }

    public void dataOccurred(DataEvent de) {
        try {
            Scanner scn = (Scanner) de.getSource();
            if (scn.equals(scanner)) {
                if (autoDataEventEnableCB.isSelected()) {
                    scanner.setDataEventEnabled(true);
                }
                scanData = scanner.getScanData();
                scanDataLabel = scanner.getScanDataLabel();
                scanDataType = scanner.getScanDataType();
                autoDisable = scanner.getAutoDisable();
                dataEventEnabled = scanner.getDataEventEnabled();
                deviceEnabled = scanner.getDeviceEnabled();
                freezeEvents = scanner.getFreezeEvents();
                decodeData = scanner.getDecodeData();
                scanDataCount = scanner.getDataCount();
                if(deviceAutoEnable == true){
                    deviceEnabled = true;
                    scanner.setDeviceEnabled(deviceEnabled);
                }

            } else {
                scn.setDataEventEnabled(true);
                scanData = scn.getScanData();
                scanDataLabel = scn.getScanDataLabel();
                scanDataType = scn.getScanDataType();
                decodeData = scn.getDecodeData();
                scanDataCount = scn.getDataCount();
            }
        } catch (JposException je) {
            System.err.println("Scanner: dataOccurred: Jpos Exception" + je);
        }

        updateDevice = false;

        try {
            SwingUtilities.invokeLater(doUpdateGUI);
        } catch (Exception e) {
            System.err.println("InvokeLater exception.");
        }
        updateDevice = true;

    }

    public void errorOccurred(ErrorEvent ee) {
        System.out.println("Error Occurred");
    }

   /**
    * Get a hex encoded string from a byte array
    * @param bytes Byte array
    * @return Hex encoded string in 0x01 0xAA ... format
    */
    private String getHexEncodedDataLabel(byte[] bytes)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(byte b : bytes)
        {
            stringBuilder.append(String.format("0x%02X ", (b & 0xff)));
        }
        return stringBuilder.toString();
    }
    
    public void updateGUI() {
        scanDataTextField.setText(new String(scanData));
        scanDataLabelHexTextField.setText(getHexEncodedDataLabel(scanDataLabel));
        scanDataLabelTextField.setText(new String(scanDataLabel));
        Integer i = new Integer(scanDataType);
        scanDataTypeTextField.setText(Integer.toString(scanDataType) + " (" + getBarcodeTypeName(scanDataType) + ")");
        scanDataCountField.setText("0");

        try {
            if (scanner.getState() != JposConst.JPOS_S_CLOSED) {
                updateDevice = false;
                autoDisableCB.setSelected(scanner.getAutoDisable());
                dataEventEnabledCB.setSelected(scanner.getDataEventEnabled());
                freezeEventsCB.setSelected(scanner.getFreezeEvents());
                decodeDataCB.setSelected(scanner.getDecodeData());
                if (scanner.getClaimed()) {
                    claimedCB.setSelected(true);
                    deviceEnabledCB.setSelected(scanner.getDeviceEnabled());
                    scanDataCount = scanner.getDataCount();
                    scanDataCountField.setText(Integer.toString(scanDataCount));
                } else {
                    claimedCB.setSelected(false);
                    deviceEnabledCB.setSelected(false);
                }
                updateDevice = true;
            }
        } catch (JposException je) {
            System.err.println("ScannerPanel: updateGUI method received JposException: " + je);
        }
    }
    
    public String formatXml(String xml) {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StringWriter stringWriter = new StringWriter();
            try {
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
                    transformer.transform(xmlInput, new StreamResult(stringWriter));

                    return stringWriter.toString().trim();
            } catch (Exception e) {
                System.err.println("Scanner: Failed to format the xml" + e);
                return xml;
            }
	}

    class CheckBoxListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (updateDevice) {
                Object source = e.getItemSelectable();
                if (source == autoDisableCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            scanner.setAutoDisable(false);
                        } else {
                            scanner.setAutoDisable(true);
                        }
                    } catch (JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == dataEventEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            scanner.setDataEventEnabled(false);

                        } else {
                            scanner.setDataEventEnabled(true);

                        }
                    } catch (JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }

                } else if (source == deviceEnabledCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            scanner.setDeviceEnabled(false);
                            powerNotifyCB.setEnabled(true);
                        } else {
                            scanner.setDeviceEnabled(true);
                             powerNotifyCB.setEnabled(false);
}
                    } catch (JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == freezeEventsCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            scanner.setFreezeEvents(false);
                        } else {
                            scanner.setFreezeEvents(true);
                        }
                    } catch (JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == decodeDataCB) {
                    try {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            scanner.setDecodeData(false);
                        } else {
                            scanner.setDecodeData(true);
                        }
                    } catch (JposException je) {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                } else if (source == checkAutoEnableDeviceCB) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        deviceAutoEnable = false;
                    } else {
                        deviceAutoEnable = true;
                    }
                }else if(source == powerNotifyCB)
                {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        try {
                            scanner.setPowerNotify(JposConst.JPOS_PN_DISABLED);
                        } catch (JposException ex) {
                            System.err.println("Jpos Exception: " + ex.getMessage());
                            JOptionPane.showMessageDialog(null, "\nException: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        try {
                            scanner.setPowerNotify(JposConst.JPOS_PN_ENABLED);
                        } catch (JposException ex) {
                            System.err.println("Jpos Exception: " + ex.getMessage());
                            JOptionPane.showMessageDialog(null, "\nException: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                updateGUI();
            }

        }
    }
}
