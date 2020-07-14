package JposTest.src;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jpos.JposConst;
import jpos.JposException;
import jpos.Scale;
import static jpos.ScaleConst.*;
import jpos.Scanner;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class JposSampleApp extends javax.swing.JFrame implements DataListener, StatusUpdateListener, ErrorListener {

    //common to scanner and scale
    private String deviceType;
    private String[] status = new String[2];
    public static String logicalName;
    private int claimTimeout;
    private boolean deviceEnabled = false;
    private boolean dataEventEnabled = false;
    private boolean autoDisable = false;
    private boolean autoDeviceEnable = false;
    private boolean freezeEventsEnabled = false;
    private int healthCheckType = -1;
    public static String healthCheckText;
    private String info;
    private String[] errorStatus = new String[2];
    private String[] setPropertyStatus = new String[2];

    private int opCode;
    private StringBuffer deviceParams;                          //InOutXml 
    private int[] statusScanner;

    //scanner variables
    static Scanner scanner = new Scanner();

    private boolean decodeDataEnabled = false;
    private boolean autoDataEventEnableScanner = false;
    private String scannerPropertyValue;
    public static String scanDataLabelText;
    public static String scanDataLabelHex;
    public static String scanData;
    public static int scanDataType;
    public static int scanDataCount = 0;

    //scale variables
    static Scale scale = new Scale();

    private boolean asyncModeEnabled = false;
    private boolean autoDataEventEnableScale = false;
    public static int liveWeight;
    public static int[] readWeight;
    public static float fWeight;
    public static String units = "";
    private String scalePropertyValue;
    private int scaleRWTimeout;
    private int statusNotifyEnabled;
    private float weightAM;
    private String[] weightInAsyncM = new String[2];
            
    //service object status
    public static boolean fastModeScannerC = true;
    public static boolean fastModeScaleC = true;
    public static boolean deviceEnableC = true;
    public static boolean dataEventEnableC = true;
    public static boolean decodeDataEnableC = true;
    public static boolean freezeEventsC = true;
    public static boolean autoDisableC = true;
    public static boolean asyncModeC = true;
    public static boolean directIOC = true;

    //set properties
    private boolean setValue;

    Runnable doUpdateGUI;
    Runnable doUpdateScaleUI;
    IntermediateLayer intermediateLayer = new IntermediateLayer();

    public JposSampleApp() {

        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            initComponents();

            DeviceCategorySelection deviceC = new DeviceCategorySelection();

            //populate the device category drop-down list
            this.cmbDeviceCategory.setModel(deviceC.deviceCategory());

            //populate property drop-down lists
            this.cmbScnProperties.setModel(deviceC.scannerProperty());
            this.cmbSclProperties.setModel(deviceC.scaleProperty());

            //populate direct IO command drop-down lists
            this.cmbScnCommand.setModel(deviceC.scnDirectIOCommand());
            this.cmbSclCommand.setModel(deviceC.sclDirectIOCommand());

            AutoCompletion.enable(cmbScnProperties);
            AutoCompletion.enable(cmbSclProperties);

            scnInternalCH.setSelected(true);
            scanDataText.setSelected(true);

            scanner.addDataListener(this);

            scale.addStatusUpdateListener(this);
            scale.addErrorListener(this);
            scale.addDataListener(new DataListener() {

                @Override
                public void dataOccurred(DataEvent de) {
                    try {
                        weightAM = de.getStatus();
                        weightInAsyncM[0] = "Read Weight in Asynchronous Mode. Weight : " + ((float) weightAM) / 1000 + " " + units;
                        SwingUtilities.invokeLater(doUpdateScaleUI);
                    } catch (Exception e) {
                        txtLogView.setText("InvokeLater exception." + e);
                        Logger
                                .getLogger(JposSampleApp.class
                                        .getName()).log(Level.SEVERE, null, e);
                    }
                }
            });
            //update Scanner UI after each data event
            doUpdateGUI = new Runnable() {
                @Override
                public void run() {
                    try {
                        updateScannerGUI();
                    } catch (JposException ex) {
                        Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            //update Scale UI after each data event
            doUpdateScaleUI = new Runnable() {
                @Override
                public void run() {
                    try {
                        updateScaleGUI();
                    } catch (JposException je) {
                        Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, je);
                    }
                }
            };
            //remove the two tab heads from the JTabbedPane
            final boolean showTabsHeader = false;
            jTabbedPane.setUI(new javax.swing.plaf.metal.MetalTabbedPaneUI() {
                @Override
                protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                    if (showTabsHeader) {
                        return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
                    } else {
                        return 0;
                    }
                }
            });
        } catch (UnsupportedLookAndFeelException e) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, e);
        } catch (InstantiationException e) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, e);
        } catch (IllegalAccessException e) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, e);
        } catch (JposException e) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkHealthBtnGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        scnPanelCommonMethods = new javax.swing.JPanel();
        lblDeviceType = new javax.swing.JLabel();
        cmbDeviceCategory = new javax.swing.JComboBox();
        lblLogicalName = new javax.swing.JLabel();
        cmbLogicalDevice = new javax.swing.JComboBox();
        btnOpen = new javax.swing.JButton();
        lblClaimTimeout = new javax.swing.JLabel();
        txtClaimTimeout = new javax.swing.JTextField();
        btnClaim = new javax.swing.JButton();
        btnRelease = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnFastMode = new javax.swing.JButton();
        lblDeviceInfo = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtDeviceInfo = new javax.swing.JTextArea();
        jTabbedPane = new javax.swing.JTabbedPane();
        scannerPanel = new javax.swing.JPanel();
        scnPanelDataEvent = new javax.swing.JPanel();
        chkScnDeviceEnable = new javax.swing.JCheckBox();
        chkScnDataEventEnable = new javax.swing.JCheckBox();
        chkScnAutoDataEventEnable = new javax.swing.JCheckBox();
        chkScnFreezeEvents = new javax.swing.JCheckBox();
        chkScnAutoDisable = new javax.swing.JCheckBox();
        chkScnAutoDeviceEnable = new javax.swing.JCheckBox();
        scnPanelRecData = new javax.swing.JPanel();
        lblScanDataLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtScanDataLabel = new javax.swing.JTextArea();
        scanDataText = new javax.swing.JRadioButton();
        lblScanData = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtScanData = new javax.swing.JTextArea();
        lblScanDataType = new javax.swing.JLabel();
        txtScanDataType = new javax.swing.JTextField();
        lblDataCount = new javax.swing.JLabel();
        txtDataCount = new javax.swing.JTextField();
        btnClearInput = new javax.swing.JButton();
        chkScnDecodeData = new javax.swing.JCheckBox();
        scanDataHex = new javax.swing.JRadioButton();
        scnPanelProperties = new javax.swing.JPanel();
        cmbScnProperties = new javax.swing.JComboBox();
        lblScnPropertyValue = new javax.swing.JLabel();
        txtScnPropertyValue = new javax.swing.JTextField();
        btnScnProperties = new javax.swing.JButton();
        btnScnClearInputProperties = new javax.swing.JButton();
        scnPanelDirectIO = new javax.swing.JPanel();
        lblScnCommand = new javax.swing.JLabel();
        cmbScnCommand = new javax.swing.JComboBox();
        btnScnExecute = new javax.swing.JButton();
        lblScnStatus = new javax.swing.JLabel();
        txtScnStatus = new javax.swing.JTextField();
        btnScnClear = new javax.swing.JButton();
        lblScnInXml = new javax.swing.JLabel();
        lblScnOutXml = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtScnOutXml = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtScnInxml = new javax.swing.JTextArea();
        btnScnCopy = new javax.swing.JButton();
        scnPanelCheckHealth = new javax.swing.JPanel();
        scnInternalCH = new javax.swing.JRadioButton();
        scnExternalCH = new javax.swing.JRadioButton();
        scnInteractiveCH = new javax.swing.JRadioButton();
        btnScnCheckHealth = new javax.swing.JButton();
        lblScnCheckHealthText = new javax.swing.JLabel();
        txtScnHealthCheckText = new javax.swing.JTextField();
        btnScnCheckHealthText = new javax.swing.JButton();
        scalePanel = new javax.swing.JPanel();
        sclPanelDataEvent = new javax.swing.JPanel();
        chkSclDeviceEnable = new javax.swing.JCheckBox();
        chkSclDataEventEnable = new javax.swing.JCheckBox();
        chkSclFreezeEvents = new javax.swing.JCheckBox();
        chkSclAutoDeviceEnable = new javax.swing.JCheckBox();
        chkSclAutoDisable = new javax.swing.JCheckBox();
        chkSclAutoDataEventEnable = new javax.swing.JCheckBox();
        sclPanelCheckHealth = new javax.swing.JPanel();
        sclInternal = new javax.swing.JRadioButton();
        sclExternal = new javax.swing.JRadioButton();
        sclInteractive = new javax.swing.JRadioButton();
        btnSclCheckHealth = new javax.swing.JButton();
        lblSclCHText = new javax.swing.JLabel();
        txtSclCheckHealthText = new javax.swing.JTextField();
        btnSclCHText = new javax.swing.JButton();
        sclPanelScaleWeight = new javax.swing.JPanel();
        lblSclReadWeght = new javax.swing.JLabel();
        txtSclRWTimeout = new javax.swing.JTextField();
        txtSclDisplayWeight = new javax.swing.JTextField();
        btnSclReadWeight = new javax.swing.JButton();
        btnSclZeroScale = new javax.swing.JButton();
        btnSclClearInput = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        chkSclAsyncMode = new javax.swing.JCheckBox();
        sclPanelLiveWeight = new javax.swing.JPanel();
        txtSclLiveWeight = new javax.swing.JTextField();
        chkSclEnableLiveWeight = new javax.swing.JCheckBox();
        sclPanelDirectIO = new javax.swing.JPanel();
        lblSclCommand = new javax.swing.JLabel();
        cmbSclCommand = new javax.swing.JComboBox();
        btnSclExecute = new javax.swing.JButton();
        lblSclInXml = new javax.swing.JLabel();
        lblSclOutXml = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtSclInXml = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtSclOutXml = new javax.swing.JTextArea();
        lblSclStatus = new javax.swing.JLabel();
        txtSclStatus = new javax.swing.JTextField();
        btnSclClear = new javax.swing.JButton();
        btnSclCopy = new javax.swing.JButton();
        sclPanelProperties = new javax.swing.JPanel();
        cmbSclProperties = new javax.swing.JComboBox();
        lblSclPropertyValue = new javax.swing.JLabel();
        txtSclPropertyValue = new javax.swing.JTextField();
        btnSclProperties = new javax.swing.JButton();
        panelLogView = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtLogView = new javax.swing.JTextArea();
        btnLogClear = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JPOS Sample Application");
        setBackground(new java.awt.Color(204, 204, 204));
        setResizable(false);

        scnPanelCommonMethods.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        scnPanelCommonMethods.setPreferredSize(new java.awt.Dimension(100, 30));

        lblDeviceType.setText("Device Type :");

        cmbDeviceCategory.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Scanner", "Scale" }));
        cmbDeviceCategory.setPreferredSize(new java.awt.Dimension(153, 28));
        cmbDeviceCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDeviceCategoryActionPerformed(evt);
            }
        });

        lblLogicalName.setText("Logical Device Name :");

        cmbLogicalDevice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ZebraScannerSerial", "ZebraScannerUSB", "ZebraUSBTableTop", "ZebraUSBHandHeld", "ZebraUSBOPOS", "ZebraScannerSNAPI", "ZebraAllScanners114", "ZebraAllScanners" }));
        cmbLogicalDevice.setFocusable(false);
        cmbLogicalDevice.setPreferredSize(new java.awt.Dimension(153, 28));

        btnOpen.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        btnOpen.setText("Open");
        btnOpen.setActionCommand("open");
        btnOpen.setAlignmentX(0.5F);
        btnOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpen.setMaximumSize(new java.awt.Dimension(81, 25));
        btnOpen.setMinimumSize(new java.awt.Dimension(81, 25));
        btnOpen.setPreferredSize(new java.awt.Dimension(120, 30));
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        lblClaimTimeout.setText("Claim Timeout (ms)");
        lblClaimTimeout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtClaimTimeout.setText("1000");

        btnClaim.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        btnClaim.setText("Claim");
        btnClaim.setActionCommand("");
        btnClaim.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClaim.setMaximumSize(new java.awt.Dimension(81, 25));
        btnClaim.setMinimumSize(new java.awt.Dimension(81, 25));
        btnClaim.setPreferredSize(new java.awt.Dimension(120, 30));
        btnClaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClaimActionPerformed(evt);
            }
        });

        btnRelease.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        btnRelease.setText("Release");
        btnRelease.setActionCommand("");
        btnRelease.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRelease.setMaximumSize(new java.awt.Dimension(81, 25));
        btnRelease.setMinimumSize(new java.awt.Dimension(81, 25));
        btnRelease.setPreferredSize(new java.awt.Dimension(120, 30));
        btnRelease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReleaseActionPerformed(evt);
            }
        });

        btnClose.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        btnClose.setText("Close");
        btnClose.setActionCommand("");
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMaximumSize(new java.awt.Dimension(81, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(81, 25));
        btnClose.setPreferredSize(new java.awt.Dimension(120, 30));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnFastMode.setText("Fast Mode");
        btnFastMode.setPreferredSize(new java.awt.Dimension(120, 30));
        btnFastMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFastModeActionPerformed(evt);
            }
        });

        lblDeviceInfo.setText("Device Information :");

        txtDeviceInfo.setColumns(20);
        txtDeviceInfo.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtDeviceInfo.setLineWrap(true);
        txtDeviceInfo.setTabSize(0);
        txtDeviceInfo.setWrapStyleWord(true);
        jScrollPane5.setViewportView(txtDeviceInfo);

        javax.swing.GroupLayout scnPanelCommonMethodsLayout = new javax.swing.GroupLayout(scnPanelCommonMethods);
        scnPanelCommonMethods.setLayout(scnPanelCommonMethodsLayout);
        scnPanelCommonMethodsLayout.setHorizontalGroup(
            scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelCommonMethodsLayout.createSequentialGroup()
                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, scnPanelCommonMethodsLayout.createSequentialGroup()
                        .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(scnPanelCommonMethodsLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDeviceInfo)
                                    .addComponent(lblLogicalName)
                                    .addComponent(lblDeviceType)
                                    .addComponent(btnClaim, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                                    .addGroup(scnPanelCommonMethodsLayout.createSequentialGroup()
                                        .addComponent(lblClaimTimeout)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtClaimTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(scnPanelCommonMethodsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(btnFastMode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(btnClose, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnRelease, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(scnPanelCommonMethodsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, scnPanelCommonMethodsLayout.createSequentialGroup()
                                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cmbDeviceCategory, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbLogicalDevice, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnOpen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addGap(0, 4, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        scnPanelCommonMethodsLayout.setVerticalGroup(
            scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelCommonMethodsLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(lblDeviceType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDeviceCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLogicalName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLogicalDevice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClaimTimeout)
                    .addComponent(txtClaimTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClaim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFastMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(lblDeviceInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jTabbedPane.setBackground(new java.awt.Color(204, 204, 204));
        jTabbedPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        scnPanelDataEvent.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Event :"));
        scnPanelDataEvent.setPreferredSize(new java.awt.Dimension(480, 99));

        chkScnDeviceEnable.setText("Device Enable");
        chkScnDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnDeviceEnableActionPerformed(evt);
            }
        });

        chkScnDataEventEnable.setText("Data Event Enable");
        chkScnDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnDataEventEnableActionPerformed(evt);
            }
        });

        chkScnAutoDataEventEnable.setText("Auto Data Event Enable");
        chkScnAutoDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnAutoDataEventEnableActionPerformed(evt);
            }
        });

        chkScnFreezeEvents.setText("Freeze Events");
        chkScnFreezeEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnFreezeEventsActionPerformed(evt);
            }
        });

        chkScnAutoDisable.setText("Auto Disable");
        chkScnAutoDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnAutoDisableActionPerformed(evt);
            }
        });

        chkScnAutoDeviceEnable.setText("Auto Device Enable");
        chkScnAutoDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnAutoDeviceEnableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scnPanelDataEventLayout = new javax.swing.GroupLayout(scnPanelDataEvent);
        scnPanelDataEvent.setLayout(scnPanelDataEventLayout);
        scnPanelDataEventLayout.setHorizontalGroup(
            scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelDataEventLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkScnDeviceEnable)
                    .addComponent(chkScnFreezeEvents))
                .addGap(50, 50, 50)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkScnAutoDisable)
                    .addComponent(chkScnDataEventEnable))
                .addGap(45, 45, 45)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkScnAutoDataEventEnable)
                    .addComponent(chkScnAutoDeviceEnable))
                .addGap(10, 10, 10))
        );
        scnPanelDataEventLayout.setVerticalGroup(
            scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelDataEventLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkScnDeviceEnable)
                    .addComponent(chkScnDataEventEnable)
                    .addComponent(chkScnAutoDataEventEnable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkScnFreezeEvents)
                    .addComponent(chkScnAutoDisable)
                    .addComponent(chkScnAutoDeviceEnable))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        scnPanelRecData.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Barcode Scanning :"));
        scnPanelRecData.setAlignmentX(1.0F);
        scnPanelRecData.setPreferredSize(new java.awt.Dimension(480, 263));

        lblScanDataLabel.setText("Scan Data Label");
        lblScanDataLabel.setAlignmentX(0.5F);

        txtScanDataLabel.setEditable(false);
        txtScanDataLabel.setColumns(20);
        txtScanDataLabel.setLineWrap(true);
        txtScanDataLabel.setRows(5);
        txtScanDataLabel.setWrapStyleWord(true);
        txtScanDataLabel.setPreferredSize(new java.awt.Dimension(0, 30));
        jScrollPane3.setViewportView(txtScanDataLabel);

        buttonGroup1.add(scanDataText);
        scanDataText.setText("Text View");
        scanDataText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanDataTextActionPerformed(evt);
            }
        });

        lblScanData.setText("Scan Data");

        txtScanData.setEditable(false);
        txtScanData.setColumns(20);
        txtScanData.setRows(5);
        txtScanData.setPreferredSize(new java.awt.Dimension(0, 30));
        jScrollPane6.setViewportView(txtScanData);

        lblScanDataType.setText("Scan Data Type");

        txtScanDataType.setEditable(false);
        txtScanDataType.setPreferredSize(new java.awt.Dimension(0, 30));

        lblDataCount.setText("Data Count");

        txtDataCount.setEditable(false);
        txtDataCount.setPreferredSize(new java.awt.Dimension(0, 30));

        btnClearInput.setText("Clear Input");
        btnClearInput.setPreferredSize(new java.awt.Dimension(100, 30));
        btnClearInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearInputActionPerformed(evt);
            }
        });

        chkScnDecodeData.setText("Decode Data");
        chkScnDecodeData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnDecodeDataActionPerformed(evt);
            }
        });

        buttonGroup1.add(scanDataHex);
        scanDataHex.setText("Hex View");
        scanDataHex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanDataHexActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scnPanelRecDataLayout = new javax.swing.GroupLayout(scnPanelRecData);
        scnPanelRecData.setLayout(scnPanelRecDataLayout);
        scnPanelRecDataLayout.setHorizontalGroup(
            scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                        .addComponent(chkScnDecodeData, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(scanDataText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scanDataHex))
                    .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                            .addComponent(lblDataCount)
                            .addGap(40, 40, 40)
                            .addComponent(txtDataCount, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClearInput, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                                .addComponent(lblScanDataLabel)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblScanData)
                                    .addComponent(lblScanDataType))
                                .addGap(18, 18, 18)
                                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtScanDataType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        scnPanelRecDataLayout.setVerticalGroup(
            scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scanDataHex)
                    .addComponent(scanDataText)
                    .addComponent(chkScnDecodeData))
                .addGap(8, 8, 8)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScanDataLabel)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblScanDataType)
                            .addComponent(txtScanDataType, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDataCount)
                            .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDataCount, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnClearInput, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblScanData))
                .addGap(4, 5, Short.MAX_VALUE))
        );

        scnPanelProperties.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Properties :"));
        scnPanelProperties.setPreferredSize(new java.awt.Dimension(480, 147));

        cmbScnProperties.setPreferredSize(new java.awt.Dimension(153, 30));
        cmbScnProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbScnPropertiesActionPerformed(evt);
            }
        });

        lblScnPropertyValue.setText("Property Value ");

        txtScnPropertyValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtScnPropertyValue.setPreferredSize(new java.awt.Dimension(20, 30));

        btnScnProperties.setText("Set Properties");
        btnScnProperties.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnPropertiesActionPerformed(evt);
            }
        });

        btnScnClearInputProperties.setText("Clear Input Properties");
        btnScnClearInputProperties.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnClearInputProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnClearInputPropertiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scnPanelPropertiesLayout = new javax.swing.GroupLayout(scnPanelProperties);
        scnPanelProperties.setLayout(scnPanelPropertiesLayout);
        scnPanelPropertiesLayout.setHorizontalGroup(
            scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelPropertiesLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(scnPanelPropertiesLayout.createSequentialGroup()
                        .addComponent(lblScnPropertyValue)
                        .addGap(18, 18, 18)
                        .addComponent(txtScnPropertyValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cmbScnProperties, 0, 249, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnScnClearInputProperties, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(btnScnProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        scnPanelPropertiesLayout.setVerticalGroup(
            scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelPropertiesLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbScnProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnClearInputProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtScnPropertyValue, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblScnPropertyValue))
                .addGap(5, 5, 5))
        );

        scnPanelDirectIO.setBorder(javax.swing.BorderFactory.createTitledBorder("Direct IO :"));
        scnPanelDirectIO.setPreferredSize(new java.awt.Dimension(485, 359));

        lblScnCommand.setText("Command :");

        cmbScnCommand.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_GETNEXT", "RSM_ATTR_SET", "RSM_ATTR_STORE" }));
        cmbScnCommand.setPreferredSize(new java.awt.Dimension(153, 30));
        cmbScnCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbScnCommandActionPerformed(evt);
            }
        });

        btnScnExecute.setText("Execute");
        btnScnExecute.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnExecuteActionPerformed(evt);
            }
        });

        lblScnStatus.setText("Status :");

        txtScnStatus.setEditable(false);
        txtScnStatus.setPreferredSize(new java.awt.Dimension(100, 30));

        btnScnClear.setText("Clear");
        btnScnClear.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnClearActionPerformed(evt);
            }
        });

        lblScnInXml.setText("InXml :");

        lblScnOutXml.setText("OutXml :");

        txtScnOutXml.setEditable(false);
        txtScnOutXml.setColumns(20);
        txtScnOutXml.setRows(5);
        txtScnOutXml.setPreferredSize(new java.awt.Dimension(160, 94));
        txtScnOutXml.setSelectionColor(new java.awt.Color(153, 204, 255));
        jScrollPane2.setViewportView(txtScnOutXml);

        txtScnInxml.setColumns(20);
        txtScnInxml.setRows(5);
        txtScnInxml.setPreferredSize(new java.awt.Dimension(160, 94));
        jScrollPane4.setViewportView(txtScnInxml);

        btnScnCopy.setText("Copy");
        btnScnCopy.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnCopyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scnPanelDirectIOLayout = new javax.swing.GroupLayout(scnPanelDirectIO);
        scnPanelDirectIO.setLayout(scnPanelDirectIOLayout);
        scnPanelDirectIOLayout.setHorizontalGroup(
            scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelDirectIOLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(lblScnStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtScnStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnScnCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnScnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                        .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblScnInXml)
                            .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                                .addComponent(lblScnCommand)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbScnCommand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnScnExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblScnOutXml)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(22, 22, 22))
        );
        scnPanelDirectIOLayout.setVerticalGroup(
            scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                        .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblScnCommand)
                            .addComponent(cmbScnCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblScnInXml)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(btnScnExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblScnOutXml)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 6, Short.MAX_VALUE)
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnScnCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnScnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtScnStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblScnStatus)))
                .addGap(15, 15, 15))
        );

        scnPanelCheckHealth.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Check Health :")));
        scnPanelCheckHealth.setPreferredSize(new java.awt.Dimension(504, 89));

        checkHealthBtnGroup.add(scnInternalCH);
        scnInternalCH.setText("Internal");
        scnInternalCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scnInternalCHActionPerformed(evt);
            }
        });

        checkHealthBtnGroup.add(scnExternalCH);
        scnExternalCH.setText("External");
        scnExternalCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scnExternalCHActionPerformed(evt);
            }
        });

        checkHealthBtnGroup.add(scnInteractiveCH);
        scnInteractiveCH.setText("Interactive");
        scnInteractiveCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scnInteractiveCHActionPerformed(evt);
            }
        });

        btnScnCheckHealth.setText("Check Health");
        btnScnCheckHealth.setAlignmentX(0.5F);
        btnScnCheckHealth.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnCheckHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnCheckHealthActionPerformed(evt);
            }
        });

        lblScnCheckHealthText.setText("Health Check Text :");

        txtScnHealthCheckText.setPreferredSize(new java.awt.Dimension(100, 30));

        btnScnCheckHealthText.setText("Get Health Check Text");
        btnScnCheckHealthText.setAlignmentX(0.5F);
        btnScnCheckHealthText.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnCheckHealthText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnCheckHealthTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scnPanelCheckHealthLayout = new javax.swing.GroupLayout(scnPanelCheckHealth);
        scnPanelCheckHealth.setLayout(scnPanelCheckHealthLayout);
        scnPanelCheckHealthLayout.setHorizontalGroup(
            scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(scnInternalCH)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scnExternalCH)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scnInteractiveCH))
                    .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(btnScnCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(lblScnCheckHealthText)))
                .addGap(2, 2, 2)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtScnHealthCheckText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnScnCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        scnPanelCheckHealthLayout.setVerticalGroup(
            scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scnInternalCH)
                    .addComponent(scnExternalCH)
                    .addComponent(scnInteractiveCH)
                    .addComponent(btnScnCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnScnCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtScnHealthCheckText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblScnCheckHealthText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout scannerPanelLayout = new javax.swing.GroupLayout(scannerPanel);
        scannerPanel.setLayout(scannerPanelLayout);
        scannerPanelLayout.setHorizontalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scnPanelDataEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelRecData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scnPanelCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        scannerPanelLayout.setVerticalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createSequentialGroup()
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scnPanelCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelDataEvent, 102, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(scannerPanelLayout.createSequentialGroup()
                        .addComponent(scnPanelRecData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scnPanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scnPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        scnPanelCheckHealth.getAccessibleContext().setAccessibleDescription("");

        jTabbedPane.addTab("", scannerPanel);

        sclPanelDataEvent.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Event :"));
        sclPanelDataEvent.setPreferredSize(new java.awt.Dimension(480, 99));

        chkSclDeviceEnable.setText("Device Enable");
        chkSclDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclDeviceEnableActionPerformed(evt);
            }
        });

        chkSclDataEventEnable.setText("Data Event Enable");
        chkSclDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclDataEventEnableActionPerformed(evt);
            }
        });

        chkSclFreezeEvents.setText("Freeze Events");
        chkSclFreezeEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclFreezeEventsActionPerformed(evt);
            }
        });

        chkSclAutoDeviceEnable.setText("Auto Device Enable");
        chkSclAutoDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAutoDeviceEnableActionPerformed(evt);
            }
        });

        chkSclAutoDisable.setText("Auto Disable");
        chkSclAutoDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAutoDisableActionPerformed(evt);
            }
        });

        chkSclAutoDataEventEnable.setText("Auto Data Event Enable");
        chkSclAutoDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAutoDataEventEnableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sclPanelDataEventLayout = new javax.swing.GroupLayout(sclPanelDataEvent);
        sclPanelDataEvent.setLayout(sclPanelDataEventLayout);
        sclPanelDataEventLayout.setHorizontalGroup(
            sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelDataEventLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSclDeviceEnable)
                    .addComponent(chkSclFreezeEvents))
                .addGap(50, 50, 50)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSclDataEventEnable)
                    .addComponent(chkSclAutoDisable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSclAutoDataEventEnable)
                    .addComponent(chkSclAutoDeviceEnable))
                .addGap(16, 16, 16))
        );
        sclPanelDataEventLayout.setVerticalGroup(
            sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelDataEventLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSclDeviceEnable)
                    .addComponent(chkSclDataEventEnable)
                    .addComponent(chkSclAutoDataEventEnable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSclFreezeEvents)
                    .addComponent(chkSclAutoDisable)
                    .addComponent(chkSclAutoDeviceEnable))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        sclPanelCheckHealth.setBorder(javax.swing.BorderFactory.createTitledBorder("Check Health :"));

        checkHealthBtnGroup.add(sclInternal);
        sclInternal.setText("Internal");
        sclInternal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sclInternalActionPerformed(evt);
            }
        });

        checkHealthBtnGroup.add(sclExternal);
        sclExternal.setText("External");
        sclExternal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sclExternalActionPerformed(evt);
            }
        });

        checkHealthBtnGroup.add(sclInteractive);
        sclInteractive.setText("Interactive");
        sclInteractive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sclInteractiveActionPerformed(evt);
            }
        });

        btnSclCheckHealth.setText("Check Health");
        btnSclCheckHealth.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclCheckHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclCheckHealthActionPerformed(evt);
            }
        });

        lblSclCHText.setText("Health Check Text :");

        txtSclCheckHealthText.setPreferredSize(new java.awt.Dimension(100, 30));

        btnSclCHText.setText("Get Health Check Text");
        btnSclCHText.setAlignmentX(0.5F);
        btnSclCHText.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclCHText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclCHTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sclPanelCheckHealthLayout = new javax.swing.GroupLayout(sclPanelCheckHealth);
        sclPanelCheckHealth.setLayout(sclPanelCheckHealthLayout);
        sclPanelCheckHealthLayout.setHorizontalGroup(
            sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelCheckHealthLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(btnSclCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(lblSclCHText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(sclPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(sclInternal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sclExternal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sclInteractive)
                        .addGap(63, 63, 63)))
                .addGap(0, 0, 0)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSclCHText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSclCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        sclPanelCheckHealthLayout.setVerticalGroup(
            sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelCheckHealthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSclCHText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclInternal)
                    .addComponent(sclExternal)
                    .addComponent(sclInteractive))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSclCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSclCHText)
                    .addComponent(btnSclCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sclPanelScaleWeight.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Scale Weight :"));
        sclPanelScaleWeight.setPreferredSize(new java.awt.Dimension(480, 391));

        lblSclReadWeght.setText("Timeout (ms) :");

        txtSclRWTimeout.setText("1000");

        txtSclDisplayWeight.setEditable(false);
        txtSclDisplayWeight.setPreferredSize(new java.awt.Dimension(100, 28));

        btnSclReadWeight.setText("Read Weight");
        btnSclReadWeight.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclReadWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclReadWeightActionPerformed(evt);
            }
        });

        btnSclZeroScale.setText("Zero Scale");
        btnSclZeroScale.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclZeroScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclZeroScaleActionPerformed(evt);
            }
        });

        btnSclClearInput.setText("Clear Input");
        btnSclClearInput.setActionCommand("btnClearInput");
        btnSclClearInput.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclClearInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclClearInputActionPerformed(evt);
            }
        });

        jLabel2.setText("Weigth :");

        chkSclAsyncMode.setText("Async Mode");
        chkSclAsyncMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAsyncModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sclPanelScaleWeightLayout = new javax.swing.GroupLayout(sclPanelScaleWeight);
        sclPanelScaleWeight.setLayout(sclPanelScaleWeightLayout);
        sclPanelScaleWeightLayout.setHorizontalGroup(
            sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSclReadWeght)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                        .addComponent(txtSclRWTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 118, Short.MAX_VALUE))
                    .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                        .addComponent(txtSclDisplayWeight, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnSclZeroScale, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                        .addComponent(btnSclReadWeight, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                        .addComponent(btnSclClearInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(chkSclAsyncMode)))
                .addContainerGap())
        );
        sclPanelScaleWeightLayout.setVerticalGroup(
            sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                        .addComponent(chkSclAsyncMode)
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelScaleWeightLayout.createSequentialGroup()
                        .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSclRWTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSclReadWeght))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSclDisplayWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(btnSclReadWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(btnSclZeroScale, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSclClearInput, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        sclPanelLiveWeight.setBorder(javax.swing.BorderFactory.createTitledBorder("Live Weight :"));

        txtSclLiveWeight.setPreferredSize(new java.awt.Dimension(100, 28));

        chkSclEnableLiveWeight.setText("Enable Live Weight");
        chkSclEnableLiveWeight.setToolTipText("Enable Live Weight (before Device Enables) . This enables Status Notify Events.");
        chkSclEnableLiveWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclEnableLiveWeightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sclPanelLiveWeightLayout = new javax.swing.GroupLayout(sclPanelLiveWeight);
        sclPanelLiveWeight.setLayout(sclPanelLiveWeightLayout);
        sclPanelLiveWeightLayout.setHorizontalGroup(
            sclPanelLiveWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelLiveWeightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSclLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(chkSclEnableLiveWeight)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        sclPanelLiveWeightLayout.setVerticalGroup(
            sclPanelLiveWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelLiveWeightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelLiveWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSclLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSclEnableLiveWeight))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        sclPanelDirectIO.setBorder(javax.swing.BorderFactory.createTitledBorder("Direct IO :"));
        sclPanelDirectIO.setPreferredSize(new java.awt.Dimension(485, 359));

        lblSclCommand.setText("Command :");

        cmbSclCommand.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_GETNEXT", "RSM_ATTR_SET", "RSM_ATTR_STORE" }));
        cmbSclCommand.setPreferredSize(new java.awt.Dimension(153, 30));
        cmbSclCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSclCommandActionPerformed(evt);
            }
        });

        btnSclExecute.setText("Execute");
        btnSclExecute.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclExecuteActionPerformed(evt);
            }
        });

        lblSclInXml.setText("InXml :");

        lblSclOutXml.setText("OutXml :");

        txtSclInXml.setColumns(20);
        txtSclInXml.setRows(5);
        txtSclInXml.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSclInXml.setPreferredSize(new java.awt.Dimension(160, 94));
        txtSclInXml.setSelectionColor(new java.awt.Color(153, 204, 255));
        jScrollPane8.setViewportView(txtSclInXml);

        txtSclOutXml.setEditable(false);
        txtSclOutXml.setColumns(20);
        txtSclOutXml.setRows(5);
        txtSclOutXml.setSelectionColor(new java.awt.Color(153, 204, 255));
        jScrollPane9.setViewportView(txtSclOutXml);

        lblSclStatus.setText("Status :");

        txtSclStatus.setEditable(false);
        txtSclStatus.setPreferredSize(new java.awt.Dimension(100, 30));

        btnSclClear.setText("Clear");
        btnSclClear.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclClearActionPerformed(evt);
            }
        });

        btnSclCopy.setText("Copy");
        btnSclCopy.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclCopyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sclPanelDirectIOLayout = new javax.swing.GroupLayout(sclPanelDirectIO);
        sclPanelDirectIO.setLayout(sclPanelDirectIOLayout);
        sclPanelDirectIOLayout.setHorizontalGroup(
            sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSclInXml)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(lblSclCommand)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSclCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(lblSclStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSclStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSclOutXml)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelDirectIOLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelDirectIOLayout.createSequentialGroup()
                                .addComponent(btnSclCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSclClear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSclExecute, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(22, 22, 22))
        );
        sclPanelDirectIOLayout.setVerticalGroup(
            sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(btnSclExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSclOutXml)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSclCommand)
                            .addComponent(cmbSclCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSclInXml)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSclClear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSclStatus)
                    .addComponent(txtSclStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        sclPanelProperties.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Properties :"));
        sclPanelProperties.setPreferredSize(new java.awt.Dimension(480, 112));

        cmbSclProperties.setPreferredSize(new java.awt.Dimension(100, 28));
        cmbSclProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSclPropertiesActionPerformed(evt);
            }
        });

        lblSclPropertyValue.setText("Property Value ");

        txtSclPropertyValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSclPropertyValue.setPreferredSize(new java.awt.Dimension(100, 28));

        btnSclProperties.setText("Set Properties");
        btnSclProperties.setPreferredSize(new java.awt.Dimension(100, 28));
        btnSclProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclPropertiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sclPanelPropertiesLayout = new javax.swing.GroupLayout(sclPanelProperties);
        sclPanelProperties.setLayout(sclPanelPropertiesLayout);
        sclPanelPropertiesLayout.setHorizontalGroup(
            sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelPropertiesLayout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sclPanelPropertiesLayout.createSequentialGroup()
                        .addComponent(lblSclPropertyValue)
                        .addGap(18, 18, 18)
                        .addComponent(txtSclPropertyValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cmbSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        sclPanelPropertiesLayout.setVerticalGroup(
            sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSclPropertyValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSclPropertyValue))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout scalePanelLayout = new javax.swing.GroupLayout(scalePanel);
        scalePanel.setLayout(scalePanelLayout);
        scalePanelLayout.setHorizontalGroup(
            scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sclPanelDataEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sclPanelScaleWeight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(sclPanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(sclPanelLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sclPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclPanelCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        scalePanelLayout.setVerticalGroup(
            scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalePanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sclPanelDataEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclPanelCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scalePanelLayout.createSequentialGroup()
                        .addComponent(sclPanelScaleWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sclPanelLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sclPanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(sclPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jTabbedPane.addTab("", scalePanel);

        panelLogView.setBorder(javax.swing.BorderFactory.createTitledBorder("Log View :"));

        txtLogView.setColumns(20);
        txtLogView.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtLogView.setRows(5);
        jScrollPane7.setViewportView(txtLogView);

        btnLogClear.setText("Clear");
        btnLogClear.setPreferredSize(new java.awt.Dimension(100, 30));
        btnLogClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLogViewLayout = new javax.swing.GroupLayout(panelLogView);
        panelLogView.setLayout(panelLogViewLayout);
        panelLogViewLayout.setHorizontalGroup(
            panelLogViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLogViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLogViewLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLogClear, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelLogViewLayout.setVerticalGroup(
            panelLogViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogViewLayout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("JPOS Sample Application");

        jLabel3.setText("v1.0");
        jLabel3.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scnPanelCommonMethods, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1020, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelLogView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(panelLogView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scnPanelCommonMethods, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //methods that are common to scanner and scale
    public void commonMethods(String actionCommand) {

        logicalName = (String) cmbLogicalDevice.getSelectedItem();
        DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) cmbDeviceCategory.getSelectedItem();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
        Date today = new Date();
        String date = DATE_FORMAT.format(today);

        //common Methods
        switch (actionCommand) {
            case "open":
                status = intermediateLayer.openAction(deviceTypeBinder);

                info = intermediateLayer.getDeviceInfo(deviceTypeBinder);
                txtDeviceInfo.setText(info);
                break;
            case "claim":
                status = intermediateLayer.claimAction(deviceTypeBinder, claimTimeout);
                break;
            case "release":
                status = intermediateLayer.releaseAction(deviceTypeBinder);
                break;
            case "close":
                status = intermediateLayer.closeAction(deviceTypeBinder);
                break;
            case "enableDevice":
                status = intermediateLayer.deviceEnableAction(deviceTypeBinder, deviceEnabled);
                break;
            case "enableDataEvent":
                status = intermediateLayer.dataEventEnableAction(deviceTypeBinder, dataEventEnabled);
                break;
            case "autoDisable":
                status = intermediateLayer.autoDisable(deviceTypeBinder, autoDisable);
                break;
            case "freezeEvents":
                status = intermediateLayer.freezeEventsAction(deviceTypeBinder, freezeEventsEnabled);
                break;
            case "healthCheck":
                status = intermediateLayer.checkHealthAction(deviceTypeBinder, healthCheckType);
                break;
            case "healthCheckText":
                status = intermediateLayer.checkHealthTextAction(deviceTypeBinder);
                break;
            case "directInputOutput":
                status = intermediateLayer.directIOAction(deviceTypeBinder, opCode, statusScanner, deviceParams);
                break;
            case "clearInput":
                status = intermediateLayer.clearInputAction(deviceTypeBinder);
                break;
            case "errorOccured":
                status = errorStatus;
            case "setProperty":
                status = setPropertyStatus;
        }
        txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[0]);
        if (status[1] != null) {
            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[1]);
        }
    }

    private void scannerMethods(String actionCommand) {
        logicalName = (String) cmbLogicalDevice.getSelectedItem();
        ScannerDeviceTypeBinder deviceTypeBinder = (ScannerDeviceTypeBinder) cmbDeviceCategory.getSelectedItem();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
        Date today = new Date();
        String date = DATE_FORMAT.format(today);

        switch (actionCommand) {
            //Scanner : Fast Mode Action    
            case "fastModeScanner":
                status = intermediateLayer.fastModeScannerAction(deviceTypeBinder);
                info = intermediateLayer.getDeviceInfo(deviceTypeBinder);
                txtDeviceInfo.setText(info);
                break;
            //Scanner : Decode Data Action
            case "decodeData":
                status = intermediateLayer.decodeDataAction(deviceTypeBinder, decodeDataEnabled);
                break;
            //Scanner : Clear Input Properties
            case "clearInputProperties":
                status = intermediateLayer.clearInputPropertiesAction(deviceTypeBinder);
                break;
        }
        txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[0]);
        if (status[1] != null) {
            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[1]);
        }
    }

    private void scaleMethods(String actionCM) {
        logicalName = (String) cmbLogicalDevice.getSelectedItem();
        ScaleDeviceTypeBinder deviceTypeBinder = (ScaleDeviceTypeBinder) cmbDeviceCategory.getSelectedItem();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
        Date today = new Date();
        String date = DATE_FORMAT.format(today);

        switch (actionCM) {
            //Scale : Fast Mode Action    
            case "fastModeScale":
                status = intermediateLayer.fastModeScaleAction(deviceTypeBinder);
                info = intermediateLayer.getDeviceInfo(deviceTypeBinder);
                txtDeviceInfo.setText(info);
                break;
            //Scale : Status Notify Action
            case "statusNotify":
                status = intermediateLayer.statusNotifyAction(deviceTypeBinder, statusNotifyEnabled, autoDeviceEnable);
                break;
            //Scale : Async Mode
            case "AsyncMode":
                status = intermediateLayer.asyncModeAction(deviceTypeBinder, asyncModeEnabled);
                break;
            //Scale : Read Weight
            case "readWeight":
                status = intermediateLayer.readWeightAction(deviceTypeBinder, scaleRWTimeout);
                break;
            //Scale : Zero Scale
            case "zeroScale":
                status = intermediateLayer.zeroScaleAction(deviceTypeBinder);
                break;
            //Scale : Read weight while on Asynchronous Mode
            case "weightOnAsyncMode":
                status = weightInAsyncM;
        }
        txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[0]);
        if (status[1] != null) {
            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[1]);
        }
    }

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        commonMethods("close");
        chkScnDeviceEnable.setSelected(false);
        chkScnDataEventEnable.setSelected(false);
        chkScnDecodeData.setSelected(false);
        chkScnAutoDataEventEnable.setSelected(false);
        chkScnAutoDeviceEnable.setSelected(false);
        chkScnFreezeEvents.setSelected(false);
        chkScnAutoDisable.setSelected(false);

        chkSclDeviceEnable.setSelected(false);
        chkSclDataEventEnable.setSelected(false);
        chkSclAsyncMode.setSelected(false);
        chkSclAutoDeviceEnable.setSelected(false);
        chkSclAutoDisable.setSelected(false);
        chkSclEnableLiveWeight.setSelected(false);
        chkSclFreezeEvents.setSelected(false);
        chkSclEnableLiveWeight.setEnabled(true);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnClaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClaimActionPerformed

        claimTimeout = Integer.valueOf(txtClaimTimeout.getText());
        commonMethods("claim");
    }//GEN-LAST:event_btnClaimActionPerformed

    private void btnReleaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReleaseActionPerformed
        commonMethods("release");
    }//GEN-LAST:event_btnReleaseActionPerformed


    private void cmbScnCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbScnCommandActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbScnCommand.getSelectedItem();
        txtScnInxml.setText(dIOCommand.getInXml());                        //display inXml in the textField        

    }//GEN-LAST:event_cmbScnCommandActionPerformed

    private void btnScnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnExecuteActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbScnCommand.getSelectedItem();
        deviceParams = new StringBuffer();
        deviceParams.append(txtScnInxml.getText());
        statusScanner = new int[]{-1};
        opCode = dIOCommand.getOpCode();

        commonMethods("directInputOutput");
        if (directIOC) {
            if (deviceParams.length() <= 5) {
                deviceParams = new StringBuffer(" ");
            }
            txtScnOutXml.setText(deviceParams.toString());
        }
        txtScnStatus.setText(Arrays.toString(statusScanner).replace("[", " ").replace("]", " "));
    }//GEN-LAST:event_btnScnExecuteActionPerformed

    private void btnScnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnClearActionPerformed
        txtScnOutXml.setText("");
        txtScnStatus.setText("");
    }//GEN-LAST:event_btnScnClearActionPerformed

    private void chkSclDataEventEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclDataEventEnableActionPerformed
        if (chkSclDataEventEnable.isSelected()) {
            dataEventEnabled = true;
        } else if (!chkSclDataEventEnable.isSelected()) {
            dataEventEnabled = false;
        }
        commonMethods("enableDataEvent");
        if (!dataEventEnableC) {
            chkSclDataEventEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkSclDataEventEnableActionPerformed

    private void chkSclEnableLiveWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclEnableLiveWeightActionPerformed
        if (chkSclEnableLiveWeight.isSelected()) {
            statusNotifyEnabled = SCAL_SN_ENABLED;
            scaleMethods("statusNotify");

            if (autoDeviceEnable && !deviceEnabled) {
                deviceEnabled = true;
                commonMethods("enableDevice");
                chkSclDeviceEnable.setSelected(true);
                chkSclEnableLiveWeight.setEnabled(false);
            }
        } else {
            statusNotifyEnabled = SCAL_SN_DISABLED;
            scaleMethods("statusNotify");
        }

    }//GEN-LAST:event_chkSclEnableLiveWeightActionPerformed

    private void chkSclAsyncModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclAsyncModeActionPerformed
        if (chkSclAsyncMode.isSelected()) {
            asyncModeEnabled = true;
        } else if (!chkSclAsyncMode.isSelected()) {
            asyncModeEnabled = false;
        }
        scaleMethods("AsyncMode");
        if (!asyncModeC) {
            chkSclAsyncMode.setSelected(false);
        }
    }//GEN-LAST:event_chkSclAsyncModeActionPerformed

    private void chkSclFreezeEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclFreezeEventsActionPerformed
        if (chkSclFreezeEvents.isSelected()) {
            freezeEventsEnabled = true;
        } else if (!chkSclFreezeEvents.isSelected()) {
            freezeEventsEnabled = false;
        }
        commonMethods("freezeEvents");
        if (!freezeEventsC) {
            chkSclFreezeEvents.setSelected(false);
        }
    }//GEN-LAST:event_chkSclFreezeEventsActionPerformed

    private void btnSclReadWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclReadWeightActionPerformed
        scaleRWTimeout = Integer.valueOf(txtSclRWTimeout.getText());
        scaleMethods("readWeight");
        if (!asyncModeEnabled) {
            txtSclDisplayWeight.setText(Float.toString(fWeight) + " " + units);
        }
    }//GEN-LAST:event_btnSclReadWeightActionPerformed

    private void btnSclZeroScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclZeroScaleActionPerformed
        scaleMethods("zeroScale");
    }//GEN-LAST:event_btnSclZeroScaleActionPerformed

    private void scnInternalCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scnInternalCHActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERNAL;
    }//GEN-LAST:event_scnInternalCHActionPerformed

    private void scnExternalCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scnExternalCHActionPerformed
        healthCheckType = JposConst.JPOS_CH_EXTERNAL;
    }//GEN-LAST:event_scnExternalCHActionPerformed

    private void scnInteractiveCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scnInteractiveCHActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERACTIVE;
    }//GEN-LAST:event_scnInteractiveCHActionPerformed

    private void btnScnCheckHealthTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnCheckHealthTextActionPerformed

        commonMethods("healthCheckText");
        txtScnHealthCheckText.setText(healthCheckText);
    }//GEN-LAST:event_btnScnCheckHealthTextActionPerformed

    private void chkSclDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclDeviceEnableActionPerformed
        if (chkSclDeviceEnable.isSelected()) {
            deviceEnabled = true;
        } else {
            deviceEnabled = false;
            chkSclEnableLiveWeight.setEnabled(true);
        }
        commonMethods("enableDevice");
        chkSclEnableLiveWeight.setEnabled(false);
        if (!deviceEnableC) {
            chkSclDeviceEnable.setSelected(false);
            chkSclEnableLiveWeight.setEnabled(true);
        }
    }//GEN-LAST:event_chkSclDeviceEnableActionPerformed

    private void btnSclClearInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclClearInputActionPerformed
        scaleMethods("clearInput");
        txtSclDisplayWeight.setText("");
    }//GEN-LAST:event_btnSclClearInputActionPerformed

    private void sclInternalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sclInternalActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERNAL;
    }//GEN-LAST:event_sclInternalActionPerformed

    private void sclExternalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sclExternalActionPerformed
        healthCheckType = JposConst.JPOS_CH_EXTERNAL;
    }//GEN-LAST:event_sclExternalActionPerformed

    private void sclInteractiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sclInteractiveActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERACTIVE;
    }//GEN-LAST:event_sclInteractiveActionPerformed

    private void btnSclCheckHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclCheckHealthActionPerformed
        if (healthCheckType == -1) {
            healthCheckType = JposConst.JPOS_CH_INTERNAL;
            sclInternal.setSelected(true);
        }
        commonMethods("healthCheck");
        healthCheckType = -1;
    }//GEN-LAST:event_btnSclCheckHealthActionPerformed

    private void btnScnClearInputPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnClearInputPropertiesActionPerformed
        scannerMethods("clearInputProperties");

    }//GEN-LAST:event_btnScnClearInputPropertiesActionPerformed

    private void updateChkScanner(String propertyName, boolean value) {
        switch (propertyName) {
            case "PIDX_AutoDisable":
                chkScnAutoDisable.setSelected(value);
                break;
            case "PIDX_DataEventEnabled":
                chkScnDataEventEnable.setSelected(value);
                break;
            case "PIDX_DeviceEnabled":
                chkScnDeviceEnable.setSelected(value);
                break;
            case "PIDX_FreezeEvents":
                chkScnFreezeEvents.setSelected(value);
                break;
            case "PIDXScan_DecodeData":
                chkScnDecodeData.setSelected(value);
                break;
        }
    }

    private void btnScnPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnPropertiesActionPerformed
        scannerPropertyValue = txtScnPropertyValue.getText();
        PropertyBinder propertyBinder = (PropertyBinder) cmbScnProperties.getSelectedItem();
        String type = propertyBinder.getType();
        String propertyName = propertyBinder.getPropertyName();

        if ("false".equals(scannerPropertyValue)) {
            setValue = false;
        } else if ("true".equals(scannerPropertyValue)) {
            setValue = true;
        }

        try {
            switch (type) {
                case "boolean":
                    propertyBinder.setBoolean(Boolean.valueOf(scannerPropertyValue));
                    updateChkScanner(propertyName, setValue);
                    break;
                case "int":
                    propertyBinder.setInt(Integer.valueOf(scannerPropertyValue));
                    break;
            }
            setPropertyStatus[0] = "Property Changed     :" + propertyName;
            commonMethods("setProperty");
        } catch (JposException ex) {
            JOptionPane.showMessageDialog(null, "Exception in set Properties : " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnScnPropertiesActionPerformed


    private void cmbScnPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbScnPropertiesActionPerformed
        PropertyBinder propertyBinder = (PropertyBinder) cmbScnProperties.getSelectedItem();
        String type = propertyBinder.getType();
        boolean editable = propertyBinder.isEditable();

        //enable the SetProperties button only if the selected property has a setProperty method
        if (!editable) {
            btnScnProperties.setEnabled(false);
        } else {
            btnScnProperties.setEnabled(true);
        }

        try {
            switch (type) {
                case "boolean":
                    boolean boolValue = propertyBinder.getBoolean();
                    txtScnPropertyValue.setText(String.valueOf(boolValue));
                    break;
                case "int":
                    int intValue = propertyBinder.getInt();
                    txtScnPropertyValue.setText(String.valueOf(intValue));
                    break;
                case "string":
                    String str = propertyBinder.getString();
                    txtScnPropertyValue.setText(str);
                    break;
                case "byte":
                    byte[] byteValue = propertyBinder.getByte();
                    txtScnPropertyValue.setText(Arrays.toString(byteValue));
                    break;
                case "long":
                    long longVAlue = propertyBinder.getLong();
                    txtScnPropertyValue.setText(String.valueOf(longVAlue));
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Exception in Properties section: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cmbScnPropertiesActionPerformed

    private void cmbSclCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSclCommandActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbSclCommand.getSelectedItem();
        txtSclInXml.setText(dIOCommand.getInXml());                        //display inXml in the textField

    }//GEN-LAST:event_cmbSclCommandActionPerformed

    private void btnSclExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclExecuteActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbSclCommand.getSelectedItem();
        deviceParams = new StringBuffer();
        deviceParams.append(txtSclInXml.getText());
        statusScanner = new int[]{-1};
        opCode = dIOCommand.getOpCode();

        commonMethods("directInputOutput");
        if (directIOC) {
            if (deviceParams.length() <= 5) {
                deviceParams = new StringBuffer(" ");
            }
            txtSclOutXml.setText(deviceParams.toString());
        }
        txtSclStatus.setText(Arrays.toString(statusScanner).replace("[", " ").replace("]", " "));
    }//GEN-LAST:event_btnSclExecuteActionPerformed

    private void btnSclClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclClearActionPerformed
        txtSclOutXml.setText("");
        txtSclStatus.setText("");
    }//GEN-LAST:event_btnSclClearActionPerformed

    private void cmbSclPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSclPropertiesActionPerformed
        PropertyBinder propertyBinder = (PropertyBinder) cmbSclProperties.getSelectedItem();
        String type = propertyBinder.getType();
        boolean editable = propertyBinder.isEditable();

        //enable the SetProperties button only if the selected property has a setProperty method
        if (!editable) {
            btnSclProperties.setEnabled(false);
        } else {
            btnSclProperties.setEnabled(true);
        }

        try {
            switch (type) {
                case "boolean":
                    boolean boolValue = propertyBinder.getBoolean();
                    txtSclPropertyValue.setText(String.valueOf(boolValue));
                    break;
                case "int":
                    long intValue = propertyBinder.getInt();
                    txtSclPropertyValue.setText(String.valueOf(intValue));
                    break;
                case "string":
                    String str = propertyBinder.getString();
                    txtSclPropertyValue.setText(str);
                    break;
                case "byte":
                    byte[] byteValue = propertyBinder.getByte();
                    txtSclPropertyValue.setText(Arrays.toString(byteValue));
                    break;
                case "long":
                    long longValue = propertyBinder.getLong();
                    txtSclPropertyValue.setText(String.valueOf(longValue));
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Exception in Properties section: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_cmbSclPropertiesActionPerformed

    private void updateChkScale(String propertyName, boolean value) {
        switch (propertyName) {
            case "PIDX_AutoDisable":
                chkSclAutoDisable.setSelected(value);
                break;
            case "PIDX_DataEventEnabled":
                chkSclDataEventEnable.setSelected(value);
                break;
            case "PIDX_DeviceEnabled":
                chkSclDeviceEnable.setSelected(value);
                break;
            case "PIDX_FreezeEvents":
                chkSclFreezeEvents.setSelected(value);
                break;
            case "PIDXScal_AsyncMode":
                chkSclAsyncMode.setSelected(value);
                break;
            case "PIDXScal_EnableLiveWeight":
                if (!chkSclDeviceEnable.isSelected()) {
                    chkSclEnableLiveWeight.setSelected(value);
                }
                break;
        }
    }

    private void btnSclPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclPropertiesActionPerformed
        scalePropertyValue = txtSclPropertyValue.getText();
        PropertyBinder propertyBinder = (PropertyBinder) cmbSclProperties.getSelectedItem();
        String type = propertyBinder.getType();
        String propertyName = propertyBinder.getPropertyName();

        if ("false".equals(scalePropertyValue)) {
            setValue = false;
        } else if ("true".equals(scalePropertyValue)) {
            setValue = true;
        } else if (Integer.valueOf(scalePropertyValue) == 2) {
            setValue = true;
        } else if (Integer.valueOf(scalePropertyValue) != 2) {
            setValue = false;
        }
        updateChkScale(propertyName, setValue);

        try {
            switch (type) {
                case "boolean":
                    propertyBinder.setBoolean(Boolean.valueOf(scalePropertyValue));
                    break;
                case "int":
                    propertyBinder.setInt(Integer.valueOf(scalePropertyValue));
                    break;
                case "long":
                    propertyBinder.setLong(Long.valueOf(scalePropertyValue));
                    break;
            }

            setPropertyStatus[0] = "Property Changed     :" + propertyName;
            commonMethods("setProperty");
        } catch (JposException je) {
            JOptionPane.showMessageDialog(null, "Exception in set Properties : " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSclPropertiesActionPerformed

    private void chkScnDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnDeviceEnableActionPerformed
        if (chkScnDeviceEnable.isSelected()) {
            deviceEnabled = true;
        } else if (!chkScnDeviceEnable.isSelected()) {
            deviceEnabled = false;
        }
        commonMethods("enableDevice");
        if (!deviceEnableC) {
            chkScnDeviceEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkScnDeviceEnableActionPerformed

    private void chkScnDataEventEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnDataEventEnableActionPerformed
        if (chkScnDataEventEnable.isSelected()) {
            dataEventEnabled = true;
        } else if (!chkScnDataEventEnable.isSelected()) {
            dataEventEnabled = false;
        }
        commonMethods("enableDataEvent");
        if (!dataEventEnableC) {
            chkScnDataEventEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkScnDataEventEnableActionPerformed

    private void btnFastModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFastModeActionPerformed
        DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) cmbDeviceCategory.getSelectedItem();
        deviceType = deviceTypeBinder.getDevice();

        if ("Scanner".equals(deviceType)) {
            scannerMethods("fastModeScanner");
            if (fastModeScannerC) {
                chkScnDeviceEnable.setSelected(true);
                chkScnDataEventEnable.setSelected(true);
                chkScnDecodeData.setSelected(true);
            } else if (!fastModeScannerC) {
                chkScnDeviceEnable.setSelected(false);
                chkScnDataEventEnable.setSelected(false);
                chkScnDecodeData.setSelected(false);
            }
        } else if ("Scale  ".equals(deviceType)) {
            scaleMethods("fastModeScale");
            if (fastModeScaleC) {
                chkSclDeviceEnable.setSelected(true);
                chkSclEnableLiveWeight.setEnabled(false);
            } else if (!fastModeScaleC) {
                chkSclDeviceEnable.setSelected(false);
                chkSclEnableLiveWeight.setEnabled(true);
            }
        }
    }//GEN-LAST:event_btnFastModeActionPerformed

    private void chkScnDecodeDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnDecodeDataActionPerformed
        if (chkScnDecodeData.isSelected()) {
            decodeDataEnabled = true;
        } else if (!chkScnDecodeData.isSelected()) {
            decodeDataEnabled = false;
        }
        scannerMethods("decodeData");
        if (!decodeDataEnableC) {
            chkScnDecodeData.setSelected(false);
        }
    }//GEN-LAST:event_chkScnDecodeDataActionPerformed

    private void chkScnAutoDataEventEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnAutoDataEventEnableActionPerformed
        if (chkScnAutoDataEventEnable.isSelected()) {
            dataEventEnabled = true;
            autoDataEventEnableScanner = true;
            chkScnDataEventEnable.setSelected(true);
        } else {
            autoDataEventEnableScanner = false;
        }
        commonMethods("enableDataEvent");
        if (!dataEventEnableC) {
            chkScnAutoDataEventEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkScnAutoDataEventEnableActionPerformed

    private void scanDataHexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanDataHexActionPerformed
        txtScanDataLabel.setText(scanDataLabelHex);
    }//GEN-LAST:event_scanDataHexActionPerformed

    private void scanDataTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanDataTextActionPerformed
        txtScanDataLabel.setText(scanDataLabelText);
    }//GEN-LAST:event_scanDataTextActionPerformed

    private void btnClearInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearInputActionPerformed
        txtScanData.setText("");
        txtScanDataLabel.setText("");
        txtScanDataType.setText("");
        txtDataCount.setText("");
        scanData = "";
        scanDataLabelText = "";
        scanDataLabelHex = "";
    }//GEN-LAST:event_btnClearInputActionPerformed

    private void btnScnCheckHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnCheckHealthActionPerformed

        if (healthCheckType == -1) {
            healthCheckType = JposConst.JPOS_CH_INTERNAL;
            scnInternalCH.setSelected(true);
        }
        commonMethods("healthCheck");
        healthCheckType = -1;
    }//GEN-LAST:event_btnScnCheckHealthActionPerformed

    private void btnLogClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogClearActionPerformed
        txtLogView.setText("");
    }//GEN-LAST:event_btnLogClearActionPerformed

    private void btnSclCHTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclCHTextActionPerformed
        commonMethods("healthCheckText");
        txtSclCheckHealthText.setText(healthCheckText);
    }//GEN-LAST:event_btnSclCHTextActionPerformed

    private void chkScnFreezeEventsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnFreezeEventsActionPerformed
        if (chkScnFreezeEvents.isSelected()) {
            freezeEventsEnabled = true;
        } else if (!chkScnFreezeEvents.isSelected()) {
            freezeEventsEnabled = false;
        }
        commonMethods("freezeEvents");
        if (!freezeEventsC) {
            chkScnFreezeEvents.setSelected(false);
        }
    }//GEN-LAST:event_chkScnFreezeEventsActionPerformed

    private void cmbDeviceCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDeviceCategoryActionPerformed
        DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) cmbDeviceCategory.getSelectedItem();
        deviceType = deviceTypeBinder.getDevice();

        String[] scannerTypes = new String[]{"ZebraScannerSerial", "ZebraScannerUSB", "ZebraUSBTableTop", "ZebraUSBHandHeld", "ZebraUSBOPOS", "ZebraScannerSNAPI", "ZebraAllScanners114", "ZebraAllScanners"};
        String[] scaleTypes = new String[]{"ZebraScale", "ZebraScale114"};

        DefaultComboBoxModel modelScanner = new DefaultComboBoxModel(scannerTypes);
        DefaultComboBoxModel modelScale = new DefaultComboBoxModel(scaleTypes);

        switch (deviceType) {
            case "Scanner":
                cmbLogicalDevice.setModel(modelScanner);
                jTabbedPane.setSelectedIndex(0);
                scnInternalCH.setSelected(true);
                scanDataText.setSelected(true);
                break;
            case "Scale  ":
                cmbLogicalDevice.setModel(modelScale);
                jTabbedPane.setSelectedIndex(1);
                sclInternal.setSelected(true);
                break;
        }
    }//GEN-LAST:event_cmbDeviceCategoryActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        commonMethods("open");
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnScnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnCopyActionPerformed
        String outXml;
        outXml = txtScnOutXml.getText();
        StringSelection selection = new StringSelection(outXml);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }//GEN-LAST:event_btnScnCopyActionPerformed

    private void btnSclCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclCopyActionPerformed
        String outXml;
        outXml = txtSclOutXml.getText();
        StringSelection selection = new StringSelection(outXml);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }//GEN-LAST:event_btnSclCopyActionPerformed

    private void chkScnAutoDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnAutoDisableActionPerformed
        if (chkScnAutoDisable.isSelected()) {
            autoDisable = true;
        } else if (!chkScnAutoDisable.isSelected()) {
            autoDisable = false;
        }
        commonMethods("autoDisable");
        if (!autoDisableC) {
            chkScnAutoDisable.setSelected(false);
        }
    }//GEN-LAST:event_chkScnAutoDisableActionPerformed

    private void chkSclAutoDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclAutoDisableActionPerformed
        if (chkSclAutoDisable.isSelected()) {
            autoDisable = true;
        } else if (!chkSclAutoDisable.isSelected()) {
            autoDisable = false;
        }
        commonMethods("autoDisable");
        if (!autoDisableC) {
            chkSclAutoDisable.setSelected(false);
        }
    }//GEN-LAST:event_chkSclAutoDisableActionPerformed

    private void chkSclAutoDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclAutoDeviceEnableActionPerformed
        if (chkSclAutoDeviceEnable.isSelected()) {
            autoDeviceEnable = true;
        } else if (!chkSclAutoDeviceEnable.isSelected()) {
            autoDeviceEnable = false;
        }
    }//GEN-LAST:event_chkSclAutoDeviceEnableActionPerformed

    private void chkScnAutoDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnAutoDeviceEnableActionPerformed
        if (chkScnAutoDeviceEnable.isSelected()) {
            deviceEnabled = true;
            autoDeviceEnable = true;
            commonMethods("enableDevice");
            chkScnDeviceEnable.setSelected(true);
        } else {
            autoDeviceEnable = false;
        }
        if (!deviceEnableC) {
            chkScnAutoDeviceEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkScnAutoDeviceEnableActionPerformed

    private void chkSclAutoDataEventEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclAutoDataEventEnableActionPerformed
        if (chkSclAutoDataEventEnable.isSelected()) {
            dataEventEnabled = true;
            autoDataEventEnableScale = true;
            chkSclDataEventEnable.setSelected(true);
        } else {
            autoDataEventEnableScale = false;
        }
        commonMethods("enableDataEvent");
        if (!dataEventEnableC) {
            chkSclAutoDataEventEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkSclAutoDataEventEnableActionPerformed

    //populate the properties combo-box
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JposSampleApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JposSampleApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClaim;
    private javax.swing.JButton btnClearInput;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFastMode;
    private javax.swing.JButton btnLogClear;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRelease;
    private javax.swing.JButton btnSclCHText;
    private javax.swing.JButton btnSclCheckHealth;
    private javax.swing.JButton btnSclClear;
    private javax.swing.JButton btnSclClearInput;
    private javax.swing.JButton btnSclCopy;
    private javax.swing.JButton btnSclExecute;
    private javax.swing.JButton btnSclProperties;
    private javax.swing.JButton btnSclReadWeight;
    private javax.swing.JButton btnSclZeroScale;
    private javax.swing.JButton btnScnCheckHealth;
    private javax.swing.JButton btnScnCheckHealthText;
    private javax.swing.JButton btnScnClear;
    private javax.swing.JButton btnScnClearInputProperties;
    private javax.swing.JButton btnScnCopy;
    private javax.swing.JButton btnScnExecute;
    private javax.swing.JButton btnScnProperties;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup checkHealthBtnGroup;
    private javax.swing.JCheckBox chkSclAsyncMode;
    private javax.swing.JCheckBox chkSclAutoDataEventEnable;
    private javax.swing.JCheckBox chkSclAutoDeviceEnable;
    private javax.swing.JCheckBox chkSclAutoDisable;
    private javax.swing.JCheckBox chkSclDataEventEnable;
    private javax.swing.JCheckBox chkSclDeviceEnable;
    private javax.swing.JCheckBox chkSclEnableLiveWeight;
    private javax.swing.JCheckBox chkSclFreezeEvents;
    private javax.swing.JCheckBox chkScnAutoDataEventEnable;
    private javax.swing.JCheckBox chkScnAutoDeviceEnable;
    private javax.swing.JCheckBox chkScnAutoDisable;
    private javax.swing.JCheckBox chkScnDataEventEnable;
    private javax.swing.JCheckBox chkScnDecodeData;
    private javax.swing.JCheckBox chkScnDeviceEnable;
    private javax.swing.JCheckBox chkScnFreezeEvents;
    private javax.swing.JComboBox cmbDeviceCategory;
    private javax.swing.JComboBox cmbLogicalDevice;
    private javax.swing.JComboBox cmbSclCommand;
    private javax.swing.JComboBox cmbSclProperties;
    private javax.swing.JComboBox cmbScnCommand;
    private javax.swing.JComboBox cmbScnProperties;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel lblClaimTimeout;
    private javax.swing.JLabel lblDataCount;
    private javax.swing.JLabel lblDeviceInfo;
    private javax.swing.JLabel lblDeviceType;
    private javax.swing.JLabel lblLogicalName;
    private javax.swing.JLabel lblScanData;
    private javax.swing.JLabel lblScanDataLabel;
    private javax.swing.JLabel lblScanDataType;
    private javax.swing.JLabel lblSclCHText;
    private javax.swing.JLabel lblSclCommand;
    private javax.swing.JLabel lblSclInXml;
    private javax.swing.JLabel lblSclOutXml;
    private javax.swing.JLabel lblSclPropertyValue;
    private javax.swing.JLabel lblSclReadWeght;
    private javax.swing.JLabel lblSclStatus;
    private javax.swing.JLabel lblScnCheckHealthText;
    private javax.swing.JLabel lblScnCommand;
    private javax.swing.JLabel lblScnInXml;
    private javax.swing.JLabel lblScnOutXml;
    private javax.swing.JLabel lblScnPropertyValue;
    private javax.swing.JLabel lblScnStatus;
    private javax.swing.JPanel panelLogView;
    private javax.swing.JPanel scalePanel;
    private javax.swing.JRadioButton scanDataHex;
    private javax.swing.JRadioButton scanDataText;
    private javax.swing.JPanel scannerPanel;
    private javax.swing.JRadioButton sclExternal;
    private javax.swing.JRadioButton sclInteractive;
    private javax.swing.JRadioButton sclInternal;
    private javax.swing.JPanel sclPanelCheckHealth;
    private javax.swing.JPanel sclPanelDataEvent;
    private javax.swing.JPanel sclPanelDirectIO;
    private javax.swing.JPanel sclPanelLiveWeight;
    private javax.swing.JPanel sclPanelProperties;
    private javax.swing.JPanel sclPanelScaleWeight;
    private javax.swing.JRadioButton scnExternalCH;
    private javax.swing.JRadioButton scnInteractiveCH;
    private javax.swing.JRadioButton scnInternalCH;
    private javax.swing.JPanel scnPanelCheckHealth;
    private javax.swing.JPanel scnPanelCommonMethods;
    private javax.swing.JPanel scnPanelDataEvent;
    private javax.swing.JPanel scnPanelDirectIO;
    private javax.swing.JPanel scnPanelProperties;
    private javax.swing.JPanel scnPanelRecData;
    private javax.swing.JTextField txtClaimTimeout;
    private javax.swing.JTextField txtDataCount;
    private javax.swing.JTextArea txtDeviceInfo;
    private javax.swing.JTextArea txtLogView;
    private javax.swing.JTextArea txtScanData;
    private javax.swing.JTextArea txtScanDataLabel;
    private javax.swing.JTextField txtScanDataType;
    private javax.swing.JTextField txtSclCheckHealthText;
    private javax.swing.JTextField txtSclDisplayWeight;
    private javax.swing.JTextArea txtSclInXml;
    private javax.swing.JTextField txtSclLiveWeight;
    private javax.swing.JTextArea txtSclOutXml;
    private javax.swing.JTextField txtSclPropertyValue;
    private javax.swing.JTextField txtSclRWTimeout;
    private javax.swing.JTextField txtSclStatus;
    private javax.swing.JTextField txtScnHealthCheckText;
    private javax.swing.JTextArea txtScnInxml;
    private javax.swing.JTextArea txtScnOutXml;
    private javax.swing.JTextField txtScnPropertyValue;
    private javax.swing.JTextField txtScnStatus;
    // End of variables declaration//GEN-END:variables

    public void updateScannerGUI() throws JposException {

        if (autoDataEventEnableScanner) {
            dataEventEnabled = true;
            commonMethods("enableDataEvent");
        } else {
            chkScnDataEventEnable.setSelected(false);
        }

        if (chkScnAutoDisable.isSelected()) {
            if (chkScnAutoDeviceEnable.isSelected()) {
                chkScnDeviceEnable.setSelected(true);
                commonMethods("enableDevice");
            } else {
                chkScnDeviceEnable.setSelected(false);
                chkScnDataEventEnable.setSelected(false);
                chkScnAutoDataEventEnable.setSelected(false);
            }
        }

        if (scanDataHex.isSelected()) {
            txtScanDataLabel.setText(scanDataLabelHex);
        } else {
            txtScanDataLabel.setText(scanDataLabelText);
            scanDataText.setSelected(true);
        }
        txtScanData.setText(scanData);
        txtScanDataType.setText(Integer.toString(scanDataType) + " (" + BarcodeType.getBarcodeTypeName(scanDataType) + ")");
        txtDataCount.setText(String.valueOf(scanDataCount));

    }

    public void updateScaleGUI() throws JposException {
        scaleMethods("weightOnAsyncMode");

        float dWeight = ((float) weightAM) / 1000;
        txtSclDisplayWeight.setText(String.valueOf(dWeight) + "  " + units);
        if (autoDataEventEnableScale) {
            dataEventEnabled = true;
            commonMethods("enableDataEvent");
        } else {
            chkSclDataEventEnable.setSelected(false);
        }

        if (chkSclAutoDisable.isSelected()) {
            if (chkSclAutoDeviceEnable.isSelected()) {
                chkSclDeviceEnable.setSelected(true);
                commonMethods("enableDevice");
            } else {
                chkSclDeviceEnable.setSelected(false);
                chkSclAutoDataEventEnable.setSelected(false);
            }
        }
    }

    @Override
    public void dataOccurred(DataEvent de) {

        ScannerDeviceTypeBinder deviceTypeBinder = (ScannerDeviceTypeBinder) cmbDeviceCategory.getSelectedItem();
        try {
            intermediateLayer.dataListenerEvent(deviceTypeBinder);
            SwingUtilities.invokeLater(this.doUpdateGUI);
        } catch (Exception e) {
            txtLogView.setText("InvokeLater exception." + e);
            Logger
                    .getLogger(JposSampleApp.class
                            .getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void statusUpdateOccurred(StatusUpdateEvent e) {
        ScaleDeviceTypeBinder deviceTypeBinder = (ScaleDeviceTypeBinder) cmbDeviceCategory.getSelectedItem();
        String response = intermediateLayer.statusUpdateListenerEvent(deviceTypeBinder, e);
        txtSclLiveWeight.setText(String.valueOf(response));
    }

    @Override
    public void errorOccurred(ErrorEvent ee) {
        System.out.println("Scale error event recieved. Error code : " + ee.getErrorCode());
        txtSclDisplayWeight.setText("Error Occoured. Error Code: " + ee.getErrorCode());

        errorStatus[0] = "Scale error event recieved. Error code : " + " " + ee.getErrorCode();
        commonMethods("errorOccured");
    }
}
