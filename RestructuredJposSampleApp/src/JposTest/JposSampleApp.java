package JposTest.src;

import com.zebra.jpos.serviceonscale.directio.DirectIOStatus;
import com.zebra.jpos.serviceonscanner.directio.DirectIOCommand;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jpos.ScaleConst;
import jpos.JposConst;
import jpos.JposException;
import jpos.Scale;
import static jpos.ScaleConst.*;
import jpos.Scanner;
import jpos.config.JposEntry;
import jpos.config.simple.SimpleEntryRegistry;
import jpos.config.simple.xml.SimpleXmlRegPopulator;
import jpos.events.DataEvent;
import jpos.events.DataListener;
import jpos.events.DirectIOEvent;
import jpos.events.DirectIOListener;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JposSampleApp extends javax.swing.JFrame implements DataListener, StatusUpdateListener, ErrorListener {

    //common to scanner and scale
    private String deviceType;
    private String[] status = new String[2];
    public static String logicalName;
    private int claimTimeout;
    private boolean deviceEnabled = false;
    private boolean dataEventEnabled = false;
    private boolean autoDisable = false;
    public static boolean autoDeviceEnable = false;
    private boolean freezeEventsEnabled = false;
    private int healthCheckType = -1;
    public static String healthCheckText;
    public static int powerState = -1;
    private String info;
    private String[] errorStatus = new String[2];
    private String[] setPropertyStatus = new String[2];
    private int powerNotifyEnabled = -1;

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

    private String resetStatValue;
    private String[] retrieveStatValue = new String[1];

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
    public final String JPOS_XML_SCANNER_MODEL = "ScannerModel";
    public final String JPOS_XML_SCANNER_SERIAL = "SerialNumber";
    public HashMap<String , String[]> scannerDeviceInfoList = new HashMap<>();
    private String logicalDeviceInfo;
    
    //service object status
    public static boolean fastModeScannerC = false;
    public static boolean fastModeScaleC = false;
    public static boolean deviceEnableC = true;
    public static boolean dataEventEnableC = true;
    public static boolean decodeDataEnableC = true;
    public static boolean freezeEventsC = true;
    public static boolean autoDisableC = true;
    public static boolean asyncModeC = true;
    public static boolean directIOC = true;
    public static boolean statusNotifyC = true;
    public static boolean powerNotifyC = true;
    public static boolean error = false;

     //----- Firmware download events ------//
    private static final int SCANNER_UF_SESS_START = 11; // Triggered when flash download session starts 
    private static final int SCANNER_UF_DL_START = 12; // Triggered when component download starts
    private static final int SCANNER_UF_DL_PROGRESS = 13; // Triggered when block(s) of flash completed 
    private static final int SCANNER_UF_DL_END = 14; // Triggered when component download ends
    private static final int SCANNER_UF_SESS_END = 15; // Triggered when flash download session ends 
    private static final int SCANNER_UF_STATUS = 16; // Triggered when update error or status       

    //Load configuration events
    private static final int CONFIG_LOAD_START = 17;
    private static final int CONFIG_LOAD_PROGRESS = 18;
    private static final int CONFIG_LOAD_END = 19;
    
    //Pnp events
    private static final int STATUS_ATTACH = 0;
    private static final int STATUS_DETACH = 1;
    
    //set properties
    private boolean setValue;

    Runnable doUpdateGUI;
    Runnable doUpdateScaleUI;
    Runnable doUpdateScaleUIAfterErrorEvent;
    IntermediateLayer intermediateLayer = new IntermediateLayer();

    public static HashMap<String, Scanner> scannersList = new HashMap<>();
    private static Scanner unUsedScannerTab; //if scan from a tab which not belogs to particuler scanner
    private boolean isScannerAvailable = false;
    private boolean isScaleAvailable = false;
    public static String scannerTabName;
    public static ArrayList<String> openedScanners = new ArrayList<>();
    private int selectedIndex = 0;
    
    public JposSampleApp(String deviceName , String scannerTab, String scaleTab){
        if(deviceName.equalsIgnoreCase(MultiJposSampleApp.JPOS_SCANNER)){
            isScannerAvailable = true; 
            scannerTabName = scannerTab;
            scannersList.put(scannerTabName, new jpos.Scanner());
        }
        else if(deviceName.equalsIgnoreCase(MultiJposSampleApp.JPOS_SCALE)){
            isScaleAvailable = true;
        }
        
        init();     
    }
    
    public JposSampleApp() {
        init();
    }

    private void init() {

        try {
            // Set System L&F
            String os = System.getProperty("os.name").toLowerCase();

            if (isWindows(os)) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            initComponents();
            addRadioButtonKeyListeners();
            /**
             * hide power notification components in scale pane since power
             * notification feature is not implemented in scale
             */
            chkSclPowerNotify.setVisible(true);
            btnSclPowerState.setVisible(false);
            txtSclPowerState.setVisible(false);
             
            DeviceCategorySelection deviceC = new DeviceCategorySelection();
                    
            if(isScannerAvailable){
                //populate logical devices drop-down list
                logicalDeviceName("Scanner");

                jTabbedPane.setSelectedIndex(0);
                scnInternalCH.setSelected(true);
                scanDataText.setSelected(true);
            }
            else if(isScaleAvailable){ 
                //populate logical devices drop-down list
                logicalDeviceName("Scale  ");            
            
                jTabbedPane.setSelectedIndex(1);
                sclInternal.setSelected(true); 
            }

            //populate property drop-down lists
            if(isScannerAvailable){
                this.cmbScnProperties.setModel(deviceC.scannerProperty());
            }
            else if(isScaleAvailable){
                this.cmbSclProperties.setModel(deviceC.scaleProperty());
            }
            
            //populate direct IO command drop-down lists
            this.cmbScnCommand.setModel(deviceC.scnDirectIOCommand());
            this.cmbSclCommand.setModel(deviceC.sclDirectIOCommand());

            AutoCompletion.enable(cmbScnProperties);
            AutoCompletion.enable(cmbSclProperties);

            if(isScaleAvailable){
                scale.addStatusUpdateListener(new StatusUpdateListener() {

                    @Override
                    public void statusUpdateOccurred(StatusUpdateEvent sue) {

                        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
                        Date today = new Date();
                        String date = DATE_FORMAT.format(today);

                        // Power notification events
                        if (sue.getStatus() == JposConst.JPOS_PS_ONLINE || sue.getStatus() == JposConst.JPOS_PS_OFF_OFFLINE || sue.getStatus() == JposConst.JPOS_PS_UNKNOWN) {
                            DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) new ScaleDeviceTypeBinder(JposSampleApp.scale);

                            String statusText = "";
                            switch (sue.getStatus()) {
                                case JposConst.JPOS_SUE_POWER_ONLINE:
                                    statusText = " JPOS_SUE_POWER_ONLINE";
                                    break;

                                case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                                    statusText = " JPOS_SUE_POWER_OFF_OFFLINE";
                                    break;

                            }
                            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + "Status Update Event: " + sue.getStatus() + statusText);
                            txtLogView.setCaretPosition(txtLogView.getDocument().getLength());
                        } // Live weight events
                        else {
                            ScaleDeviceTypeBinder deviceTypeBinder = new ScaleDeviceTypeBinder(JposSampleApp.scale);
                            String response = intermediateLayer.statusUpdateListenerEvent(deviceTypeBinder, sue);
                            txtSclLiveWeight.setText(String.valueOf(response));

                            String statusText = "";
                            switch (sue.getStatus()) {

                                case ScaleConst.SCAL_SUE_STABLE_WEIGHT:
                                    statusText = " SCAL_SUE_STABLE_WEIGHT";
                                    break;

                                case ScaleConst.SCAL_SUE_NOT_READY:
                                    statusText = " SCAL_SUE_NOT_READY";
                                    break;

                                case ScaleConst.SCAL_SUE_WEIGHT_UNSTABLE:
                                    statusText = " SCAL_SUE_WEIGHT_UNSTABLE";
                                    break;

                                case ScaleConst.SCAL_SUE_WEIGHT_ZERO:
                                    statusText = " SCAL_SUE_WEIGHT_ZERO";
                                    break;

                                case ScaleConst.SCAL_SUE_WEIGHT_UNDER_ZERO:
                                    statusText = " SCAL_SUE_WEIGHT_UNDER_ZERO";
                                    break;

                                case ScaleConst.SCAL_SUE_WEIGHT_OVERWEIGHT:
                                    statusText = " SCAL_SUE_WEIGHT_OVERWEIGHT";
                                    break;
                            }

                            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + "Status Update Event: " + sue.getStatus() + statusText);
                            txtLogView.setCaretPosition(txtLogView.getDocument().getLength());

                        }
                    }
                });
                scale.addErrorListener(new ErrorListener() {
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
                    Date today = new Date();
                    String date = DATE_FORMAT.format(today);

                    @Override
                    public void errorOccurred(ErrorEvent ee) {
                        try {
                            DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) new ScaleDeviceTypeBinder(JposSampleApp.scale);;
                            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + "Error Event - ErrorCode: " + ee.getErrorCode() + " ExtErrorCode: " + ee.getErrorCodeExtended() + " ErrorLocus: " + ee.getErrorLocus() +" ErrorResponse: " + ee.getErrorResponse());
                            SwingUtilities.invokeLater(doUpdateScaleUIAfterErrorEvent); //update scale UI afetr each error event
                        } catch (Exception e) {
                            txtLogView.setText("InvokeLater exception." + e);
                            Logger
                                    .getLogger(JposSampleApp.class
                                            .getName()).log(Level.SEVERE, null, e);
                        }
                    }

                });
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
                scale.addDirectIOListener(new DirectIOListener() {
                    ScaleDeviceTypeBinder deviceTypeBinder = new ScaleDeviceTypeBinder(JposSampleApp.scale);
                    @Override
                    public void directIOOccurred(DirectIOEvent de) {
                        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
                        Date today = new Date();
                        String date = DATE_FORMAT.format(today);

                        String directIOEventText = "";
                        switch (de.getEventNumber()) {
                            case CONFIG_LOAD_START:
                                directIOEventText = " start";
                                break;
                            case CONFIG_LOAD_PROGRESS:
                                directIOEventText = " progress";
                                break;
                            case CONFIG_LOAD_END:
                                directIOEventText = " end";
                                break;    
                        }
                        txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + "DirectIO Event scanner/s " + directIOEventText + "; JSON : " + ((String) de.getObject()));
                        txtLogView.setCaretPosition(txtLogView.getDocument().getLength());
                    }
                });
            }
            
            //update Scanner UI after each data event
            if(isScannerAvailable){
                updateScannerStatusAndUI();
            }
          
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
            //update ScaleUI after each error event
            doUpdateScaleUIAfterErrorEvent = new Runnable() {
                @Override
                public void run() {
                    try {
                        updateScaleUIAfterErrorEvent();
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
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException | JposException e) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //check for platform
    private boolean isWindows(String os) {
        return (os.contains("win"));
    }

    private void updateScannerStatusAndUI(){
        
        scannersList.get(JposSampleApp.scannerTabName).addDataListener(this);
        scannersList.get(JposSampleApp.scannerTabName).addStatusUpdateListener(new StatusUpdateListener() {

            @Override
            public void statusUpdateOccurred(StatusUpdateEvent sue) {

                //DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName)); 
                ScannerDeviceTypeBinder deviceTypeBinder = null;
                if (scannersList.get(JposSampleApp.scannerTabName) != sue.getSource()) {
                    deviceTypeBinder = new ScannerDeviceTypeBinder((Scanner) sue.getSource());
                } else {
                    deviceTypeBinder = new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));
                }

                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
                Date today = new Date();
                String date = DATE_FORMAT.format(today);

                String statusText = "";
                switch (sue.getStatus()) {
                    case JposConst.JPOS_SUE_POWER_ONLINE:
                        statusText = " JPOS_SUE_POWER_ONLINE";
                        break;

                    case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                        statusText = " JPOS_SUE_POWER_OFF_OFFLINE";
                        break;
                    }
                txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + "Status Update Event: " + sue.getStatus() + statusText);
                txtLogView.setCaretPosition(txtLogView.getDocument().getLength());
            }
        });

        scannersList.get(JposSampleApp.scannerTabName).addDirectIOListener(new DirectIOListener() {

            @Override
            public void directIOOccurred(DirectIOEvent de) {
                //DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName)); 
                ScannerDeviceTypeBinder deviceTypeBinder = null;
                if (scannersList.get(JposSampleApp.scannerTabName) != de.getSource()) {
                    deviceTypeBinder = new ScannerDeviceTypeBinder((Scanner) de.getSource());
                } else {
                    deviceTypeBinder = new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));
                }
                
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
                Date today = new Date();
                String date = DATE_FORMAT.format(today);

                String directIOEventText = "";
                switch (de.getEventNumber()) {
                    case STATUS_ATTACH:
                        directIOEventText = " attached";
                        break;
                    case STATUS_DETACH:
                        directIOEventText = " detached";
                        break;
                    case CONFIG_LOAD_START:
                        directIOEventText = " start";
                        break;
                    case CONFIG_LOAD_PROGRESS:
                        directIOEventText = " progress";
                        break;
                    case CONFIG_LOAD_END:
                        directIOEventText = " end";
                        break;
                    case SCANNER_UF_SESS_START:
                        directIOEventText = " session start";
                        break;    
                    case SCANNER_UF_DL_START:
                        directIOEventText = " download start";
                        break;    
                    case SCANNER_UF_DL_PROGRESS:
                        directIOEventText = " download progress";
                        break;    
                    case SCANNER_UF_DL_END:
                        directIOEventText = " download end";
                        break;    
                    case SCANNER_UF_SESS_END:
                        directIOEventText = " session end";
                        break; 
                    case SCANNER_UF_STATUS:
                        directIOEventText = " status";
                        break;      
                }
                txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + "DirectIO Event scanner/s " + directIOEventText + "; JSON : " + ((String) de.getObject()));
                txtLogView.setCaretPosition(txtLogView.getDocument().getLength());
            }
        });

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
        sclCheckHealthBtnGrp = new javax.swing.ButtonGroup();
        jScrollPane11 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        scannerPanel = new javax.swing.JPanel();
        scnPanelDataEvent = new javax.swing.JPanel();
        chkScnDeviceEnable = new javax.swing.JCheckBox();
        chkScnDataEventEnable = new javax.swing.JCheckBox();
        chkScnAutoDataEventEnable = new javax.swing.JCheckBox();
        chkScnFreezeEvents = new javax.swing.JCheckBox();
        chkScnAutoDisable = new javax.swing.JCheckBox();
        chkScnAutoDeviceEnable = new javax.swing.JCheckBox();
        chkScnPowerNotify = new javax.swing.JCheckBox();
        txtScnPowerState = new javax.swing.JTextField();
        btnScnPowerState = new javax.swing.JButton();
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
        btnClearInput = new javax.swing.JButton();
        chkScnDecodeData = new javax.swing.JCheckBox();
        scanDataHex = new javax.swing.JRadioButton();
        scnPanelCheckHealth = new javax.swing.JPanel();
        scnInternalCH = new javax.swing.JRadioButton();
        scnExternalCH = new javax.swing.JRadioButton();
        scnInteractiveCH = new javax.swing.JRadioButton();
        btnScnCheckHealth = new javax.swing.JButton();
        lblScnCheckHealthText = new javax.swing.JLabel();
        txtScnHealthCheckText = new javax.swing.JTextField();
        btnScnCheckHealthText = new javax.swing.JButton();
        scnPanelStat = new javax.swing.JPanel();
        lblScnStatistic = new javax.swing.JTextField();
        btnScnRetreiveStat = new javax.swing.JButton();
        btnScnResetStat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtScnStatOutput = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        scnPanelDirectIO = new javax.swing.JPanel();
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
        lblScnCommand = new javax.swing.JLabel();
        scnPanelProperties = new javax.swing.JPanel();
        cmbScnProperties = new javax.swing.JComboBox();
        lblScnPropertyValue = new javax.swing.JLabel();
        txtScnPropertyValue = new javax.swing.JTextField();
        btnScnProperties = new javax.swing.JButton();
        btnScnClearInputProperties = new javax.swing.JButton();
        scalePanel = new javax.swing.JPanel();
        sclPanelDataEvent = new javax.swing.JPanel();
        chkSclDeviceEnable = new javax.swing.JCheckBox();
        chkSclDataEventEnable = new javax.swing.JCheckBox();
        chkSclFreezeEvents = new javax.swing.JCheckBox();
        chkSclAutoDeviceEnable = new javax.swing.JCheckBox();
        chkSclAutoDisable = new javax.swing.JCheckBox();
        chkSclAutoDataEventEnable = new javax.swing.JCheckBox();
        chkSclPowerNotify = new javax.swing.JCheckBox();
        txtSclPowerState = new javax.swing.JTextField();
        btnSclPowerState = new javax.swing.JButton();
        sclPanelScaleWeight = new javax.swing.JPanel();
        lblSclReadWeght = new javax.swing.JLabel();
        txtSclRWTimeout = new javax.swing.JTextField();
        txtSclDisplayWeight = new javax.swing.JTextField();
        btnSclReadWeight = new javax.swing.JButton();
        btnSclZeroScale = new javax.swing.JButton();
        btnSclClearInput = new javax.swing.JButton();
        lblSclWeight = new javax.swing.JLabel();
        chkSclAsyncMode = new javax.swing.JCheckBox();
        sclPanelLiveWeight = new javax.swing.JPanel();
        txtSclLiveWeight = new javax.swing.JTextField();
        chkSclEnableLiveWeight = new javax.swing.JCheckBox();
        sclPanelCheckHealth = new javax.swing.JPanel();
        sclInternal = new javax.swing.JRadioButton();
        sclExternal = new javax.swing.JRadioButton();
        sclInteractive = new javax.swing.JRadioButton();
        btnSclCheckHealth = new javax.swing.JButton();
        txtSclCheckHealthText = new javax.swing.JTextField();
        btnSclCHText = new javax.swing.JButton();
        lblSclCHText = new javax.swing.JLabel();
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
        sclPanelStat = new javax.swing.JPanel();
        lblSclStatistic = new javax.swing.JTextField();
        btnSclResetStat = new javax.swing.JButton();
        btnSclRetreiveStat = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        txtSclStatOutput = new javax.swing.JTextArea();
        sclPanelProperties = new javax.swing.JPanel();
        cmbSclProperties = new javax.swing.JComboBox();
        lblSclPropertyValue = new javax.swing.JLabel();
        txtSclPropertyValue = new javax.swing.JTextField();
        btnSclProperties = new javax.swing.JButton();
        scnPanelCommonMethods = new javax.swing.JPanel();
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
        txtScannerInfoScroll = new javax.swing.JScrollPane();
        txtLogicalInfo = new javax.swing.JTextArea();
        panelLogView = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtLogView = new javax.swing.JTextArea();
        btnLogClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JPOS Sample Application");
        setBackground(new java.awt.Color(204, 204, 204));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("JPOS Sample Application");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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

        jTabbedPane.setBackground(new java.awt.Color(204, 204, 204));
        jTabbedPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane.setForeground(new java.awt.Color(204, 204, 204));

        scnPanelDataEvent.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Data Event ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scnPanelDataEvent.setPreferredSize(new java.awt.Dimension(489, 99));
        scnPanelDataEvent.setVerifyInputWhenFocusTarget(false);

        chkScnDeviceEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnDeviceEnable.setText("Device Enable");
        chkScnDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnDeviceEnableActionPerformed(evt);
            }
        });

        chkScnDataEventEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnDataEventEnable.setText("Data Event Enable");
        chkScnDataEventEnable.setMargin(new java.awt.Insets(2, -1, 2, 2));
        chkScnDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnDataEventEnableActionPerformed(evt);
            }
        });

        chkScnAutoDataEventEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnAutoDataEventEnable.setText("Auto Data Event Enable");
        chkScnAutoDataEventEnable.setMargin(new java.awt.Insets(2, -2, 2, 2));
        chkScnAutoDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnAutoDataEventEnableActionPerformed(evt);
            }
        });

        chkScnFreezeEvents.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnFreezeEvents.setText("Freeze Events");
        chkScnFreezeEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnFreezeEventsActionPerformed(evt);
            }
        });

        chkScnAutoDisable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnAutoDisable.setText("Auto Disable");
        chkScnAutoDisable.setMargin(new java.awt.Insets(2, -1, 2, 2));
        chkScnAutoDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnAutoDisableActionPerformed(evt);
            }
        });

        chkScnAutoDeviceEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnAutoDeviceEnable.setText("Auto Device Enable");
        chkScnAutoDeviceEnable.setMargin(new java.awt.Insets(2, -2, 2, 2));
        chkScnAutoDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnAutoDeviceEnableActionPerformed(evt);
            }
        });

        chkScnPowerNotify.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnPowerNotify.setText("Power Notify");
        chkScnPowerNotify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnPowerNotifyActionPerformed(evt);
            }
        });

        txtScnPowerState.setEditable(false);
        txtScnPowerState.setFocusable(false);

        btnScnPowerState.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnPowerState.setText("Power State");
        btnScnPowerState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnPowerStateActionPerformed(evt);
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
                    .addComponent(chkScnFreezeEvents)
                    .addComponent(chkScnPowerNotify))
                .addGap(25, 25, 25)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chkScnAutoDisable)
                    .addComponent(chkScnDataEventEnable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnScnPowerState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkScnAutoDeviceEnable)
                    .addComponent(chkScnAutoDataEventEnable)
                    .addComponent(txtScnPowerState, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        scnPanelDataEventLayout.setVerticalGroup(
            scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelDataEventLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkScnAutoDataEventEnable, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkScnDeviceEnable)
                        .addComponent(chkScnDataEventEnable)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkScnFreezeEvents)
                    .addComponent(chkScnAutoDisable)
                    .addComponent(chkScnAutoDeviceEnable))
                .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtScnPowerState, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnScnPowerState, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scnPanelDataEventLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkScnPowerNotify))))
        );

        scnPanelRecData.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Barcode Scanning ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scnPanelRecData.setAlignmentX(1.0F);
        scnPanelRecData.setPreferredSize(new java.awt.Dimension(488, 262));

        lblScanDataLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScanDataLabel.setText("Scan Data Label");
        lblScanDataLabel.setAlignmentX(0.5F);

        jScrollPane3.setAutoscrolls(true);

        txtScanDataLabel.setEditable(false);
        txtScanDataLabel.setColumns(20);
        txtScanDataLabel.setLineWrap(true);
        txtScanDataLabel.setRows(5);
        txtScanDataLabel.setWrapStyleWord(true);
        txtScanDataLabel.setPreferredSize(new java.awt.Dimension(0, 30));
        jScrollPane3.setViewportView(txtScanDataLabel);

        buttonGroup1.add(scanDataText);
        scanDataText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scanDataText.setText("Text View");
        scanDataText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanDataTextActionPerformed(evt);
            }
        });

        lblScanData.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScanData.setText("Scan Data");

        txtScanData.setEditable(false);
        txtScanData.setColumns(20);
        txtScanData.setRows(5);
        txtScanData.setPreferredSize(new java.awt.Dimension(0, 30));
        jScrollPane6.setViewportView(txtScanData);

        lblScanDataType.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScanDataType.setText("Scan Data Type");

        txtScanDataType.setEditable(false);
        txtScanDataType.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtScanDataType.setPreferredSize(new java.awt.Dimension(0, 30));

        btnClearInput.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnClearInput.setText("Clear Input");
        btnClearInput.setPreferredSize(new java.awt.Dimension(100, 30));
        btnClearInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearInputActionPerformed(evt);
            }
        });

        chkScnDecodeData.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkScnDecodeData.setText("Decode Data");
        chkScnDecodeData.setMargin(new java.awt.Insets(2, -2, 2, 2));
        chkScnDecodeData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScnDecodeDataActionPerformed(evt);
            }
        });

        buttonGroup1.add(scanDataHex);
        scanDataHex.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                .addGap(12, 12, 12)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScanDataType)
                    .addComponent(lblScanDataLabel)
                    .addComponent(lblScanData))
                .addGap(12, 12, 12)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClearInput, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtScanDataType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane6)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelRecDataLayout.createSequentialGroup()
                        .addComponent(chkScnDecodeData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                        .addComponent(scanDataText)
                        .addGap(3, 3, 3)
                        .addComponent(scanDataHex)))
                .addGap(10, 10, 10))
        );
        scnPanelRecDataLayout.setVerticalGroup(
            scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelRecDataLayout.createSequentialGroup()
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scanDataHex)
                    .addComponent(scanDataText)
                    .addComponent(chkScnDecodeData))
                .addGap(4, 4, 4)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScanDataLabel)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScanData)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelRecDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScanDataType)
                    .addComponent(txtScanDataType, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearInput, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scnPanelCheckHealth.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Check Health ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scnPanelCheckHealth.setPreferredSize(new java.awt.Dimension(504, 89));

        checkHealthBtnGroup.add(scnInternalCH);
        scnInternalCH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scnInternalCH.setText("Internal");
        scnInternalCH.setMargin(new java.awt.Insets(2, -1, 2, 2));
        scnInternalCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scnInternalCHActionPerformed(evt);
            }
        });

        checkHealthBtnGroup.add(scnExternalCH);
        scnExternalCH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scnExternalCH.setText("External");
        scnExternalCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scnExternalCHActionPerformed(evt);
            }
        });

        checkHealthBtnGroup.add(scnInteractiveCH);
        scnInteractiveCH.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scnInteractiveCH.setText("Interactive");
        scnInteractiveCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scnInteractiveCHActionPerformed(evt);
            }
        });

        btnScnCheckHealth.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnCheckHealth.setText("Check Health");
        btnScnCheckHealth.setAlignmentX(0.5F);
        btnScnCheckHealth.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnCheckHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnCheckHealthActionPerformed(evt);
            }
        });

        lblScnCheckHealthText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScnCheckHealthText.setText("HealthCheck Text  ");

        txtScnHealthCheckText.setPreferredSize(new java.awt.Dimension(100, 30));
        txtScnHealthCheckText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtScnHealthCheckTextActionPerformed(evt);
            }
        });

        btnScnCheckHealthText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnCheckHealthText.setText("Health Check Text");
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
                .addGap(10, 10, 10)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(scnInternalCH)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scnExternalCH)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scnInteractiveCH))
                    .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(btnScnCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(lblScnCheckHealthText)))
                .addGap(0, 0, 0)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnScnCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtScnHealthCheckText, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        scnPanelCheckHealthLayout.setVerticalGroup(
            scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelCheckHealthLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scnInternalCH)
                    .addComponent(scnExternalCH)
                    .addComponent(scnInteractiveCH)
                    .addComponent(btnScnCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(scnPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnScnCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtScnHealthCheckText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblScnCheckHealthText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scnPanelStat.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Statistics ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scnPanelStat.setPreferredSize(new java.awt.Dimension(503, 130));

        lblScnStatistic.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btnScnRetreiveStat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnRetreiveStat.setText("Retrieve Statistics");
        btnScnRetreiveStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnRetreiveStatActionPerformed(evt);
            }
        });

        btnScnResetStat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnResetStat.setText("Reset Statistics");
        btnScnResetStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnResetStatActionPerformed(evt);
            }
        });

        txtScnStatOutput.setColumns(4);
        txtScnStatOutput.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtScnStatOutput.setLineWrap(true);
        txtScnStatOutput.setRows(5);
        txtScnStatOutput.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtScnStatOutput);

        jLabel2.setText("Filter By");

        javax.swing.GroupLayout scnPanelStatLayout = new javax.swing.GroupLayout(scnPanelStat);
        scnPanelStat.setLayout(scnPanelStatLayout);
        scnPanelStatLayout.setHorizontalGroup(
            scnPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelStatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scnPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(scnPanelStatLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblScnStatistic, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnScnRetreiveStat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnScnResetStat, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        scnPanelStatLayout.setVerticalGroup(
            scnPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelStatLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(scnPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScnStatistic, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnRetreiveStat, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnResetStat, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scnPanelDirectIO.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Direct IO ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scnPanelDirectIO.setPreferredSize(new java.awt.Dimension(485, 359));

        cmbScnCommand.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbScnCommand.setAutoscrolls(true);
        cmbScnCommand.setPreferredSize(new java.awt.Dimension(153, 30));
        cmbScnCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbScnCommandActionPerformed(evt);
            }
        });

        btnScnExecute.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnExecute.setText("Execute");
        btnScnExecute.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnExecuteActionPerformed(evt);
            }
        });

        lblScnStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScnStatus.setText("Status ");

        txtScnStatus.setEditable(false);
        txtScnStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtScnStatus.setPreferredSize(new java.awt.Dimension(100, 30));
        txtScnStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtScnStatusActionPerformed(evt);
            }
        });

        btnScnClear.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnClear.setText("Clear");
        btnScnClear.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnClearActionPerformed(evt);
            }
        });

        lblScnInXml.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScnInXml.setText("InXml :");

        lblScnOutXml.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScnOutXml.setText("OutXml :");

        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        txtScnOutXml.setEditable(false);
        txtScnOutXml.setColumns(100);
        txtScnOutXml.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtScnOutXml.setLineWrap(true);
        txtScnOutXml.setRows(50);
        txtScnOutXml.setWrapStyleWord(true);
        txtScnOutXml.setSelectionColor(new java.awt.Color(153, 204, 255));
        jScrollPane2.setViewportView(txtScnOutXml);

        jScrollPane4.setAutoscrolls(true);

        txtScnInxml.setColumns(100);
        txtScnInxml.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtScnInxml.setRows(50);
        txtScnInxml.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane4.setViewportView(txtScnInxml);

        btnScnCopy.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnCopy.setText("Copy");
        btnScnCopy.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnCopyActionPerformed(evt);
            }
        });

        lblScnCommand.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScnCommand.setText("Command ");

        javax.swing.GroupLayout scnPanelDirectIOLayout = new javax.swing.GroupLayout(scnPanelDirectIO);
        scnPanelDirectIO.setLayout(scnPanelDirectIOLayout);
        scnPanelDirectIOLayout.setHorizontalGroup(
            scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelDirectIOLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                        .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, scnPanelDirectIOLayout.createSequentialGroup()
                                .addComponent(lblScnCommand)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbScnCommand, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblScnInXml)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)
                        .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblScnOutXml)
                            .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnScnExecute, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(lblScnStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtScnStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnScnCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnScnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        scnPanelDirectIOLayout.setVerticalGroup(
            scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelDirectIOLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbScnCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblScnCommand)
                    .addComponent(btnScnExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScnInXml)
                    .addComponent(lblScnOutXml))
                .addGap(0, 0, 0)
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnScnCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnScnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scnPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtScnStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblScnStatus))))
        );

        scnPanelProperties.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Properties ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        scnPanelProperties.setPreferredSize(new java.awt.Dimension(480, 147));

        cmbScnProperties.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbScnProperties.setPreferredSize(new java.awt.Dimension(153, 30));
        cmbScnProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbScnPropertiesActionPerformed(evt);
            }
        });

        lblScnPropertyValue.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblScnPropertyValue.setText("Property Value ");

        txtScnPropertyValue.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtScnPropertyValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtScnPropertyValue.setPreferredSize(new java.awt.Dimension(20, 30));

        btnScnProperties.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnScnProperties.setText("Set Properties");
        btnScnProperties.setPreferredSize(new java.awt.Dimension(100, 30));
        btnScnProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScnPropertiesActionPerformed(evt);
            }
        });

        btnScnClearInputProperties.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                .addGap(10, 10, 10)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(scnPanelPropertiesLayout.createSequentialGroup()
                        .addComponent(lblScnPropertyValue)
                        .addGap(4, 4, 4)
                        .addComponent(txtScnPropertyValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cmbScnProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnScnClearInputProperties, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnProperties, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        scnPanelPropertiesLayout.setVerticalGroup(
            scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelPropertiesLayout.createSequentialGroup()
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbScnProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnClearInputProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(scnPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtScnPropertyValue, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnScnProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblScnPropertyValue))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout scannerPanelLayout = new javax.swing.GroupLayout(scannerPanel);
        scannerPanel.setLayout(scannerPanelLayout);
        scannerPanelLayout.setHorizontalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(scnPanelRecData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelDataEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelStat, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scnPanelProperties, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(scnPanelCheckHealth, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(scnPanelDirectIO, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );
        scannerPanelLayout.setVerticalGroup(
            scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scannerPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scnPanelDataEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scnPanelCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scannerPanelLayout.createSequentialGroup()
                        .addComponent(scnPanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scnPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scannerPanelLayout.createSequentialGroup()
                        .addComponent(scnPanelRecData, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(scnPanelStat, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );

        scnPanelCheckHealth.getAccessibleContext().setAccessibleDescription("");

        jTabbedPane.addTab("", scannerPanel);

        sclPanelDataEvent.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Data Event ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelDataEvent.setPreferredSize(new java.awt.Dimension(480, 99));

        chkSclDeviceEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclDeviceEnable.setText("Device Enable");
        chkSclDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclDeviceEnableActionPerformed(evt);
            }
        });

        chkSclDataEventEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclDataEventEnable.setText("Data Event Enable");
        chkSclDataEventEnable.setMargin(new java.awt.Insets(2, -1, 2, 2));
        chkSclDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclDataEventEnableActionPerformed(evt);
            }
        });

        chkSclFreezeEvents.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclFreezeEvents.setText("Freeze Events");
        chkSclFreezeEvents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclFreezeEventsActionPerformed(evt);
            }
        });

        chkSclAutoDeviceEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclAutoDeviceEnable.setText("Auto Device Enable");
        chkSclAutoDeviceEnable.setMargin(new java.awt.Insets(2, -2, 2, 2));
        chkSclAutoDeviceEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAutoDeviceEnableActionPerformed(evt);
            }
        });

        chkSclAutoDisable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclAutoDisable.setText("Auto Disable");
        chkSclAutoDisable.setMargin(new java.awt.Insets(2, -1, 2, 2));
        chkSclAutoDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAutoDisableActionPerformed(evt);
            }
        });

        chkSclAutoDataEventEnable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclAutoDataEventEnable.setText("Auto Data Event Enable");
        chkSclAutoDataEventEnable.setMargin(new java.awt.Insets(2, -2, 2, 2));
        chkSclAutoDataEventEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclAutoDataEventEnableActionPerformed(evt);
            }
        });

        chkSclPowerNotify.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclPowerNotify.setText("Power Notify");
        chkSclPowerNotify.setMargin(new java.awt.Insets(1, 2, 2, 2));
        chkSclPowerNotify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSclPowerNotifyActionPerformed(evt);
            }
        });

        txtSclPowerState.setEditable(false);
        txtSclPowerState.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSclPowerState.setRequestFocusEnabled(false);

        btnSclPowerState.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclPowerState.setText("Power State");
        btnSclPowerState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclPowerStateActionPerformed(evt);
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
                    .addComponent(chkSclFreezeEvents)
                    .addComponent(chkSclPowerNotify))
                .addGap(25, 25, 25)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(chkSclAutoDisable)
                    .addComponent(chkSclDataEventEnable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSclPowerState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtSclPowerState, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkSclAutoDeviceEnable, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(chkSclAutoDataEventEnable))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        sclPanelDataEventLayout.setVerticalGroup(
            sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelDataEventLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSclAutoDataEventEnable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkSclDeviceEnable)
                        .addComponent(chkSclDataEventEnable)))
                .addGap(0, 0, 0)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSclFreezeEvents)
                    .addComponent(chkSclAutoDisable)
                    .addComponent(chkSclAutoDeviceEnable))
                .addGap(0, 0, 0)
                .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSclPowerNotify, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sclPanelDataEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSclPowerState, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSclPowerState, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sclPanelScaleWeight.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Scale Weight ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelScaleWeight.setPreferredSize(new java.awt.Dimension(480, 391));

        lblSclReadWeght.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclReadWeght.setText("Timeout (ms) :");

        txtSclRWTimeout.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        txtSclRWTimeout.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSclRWTimeout.setText("1000");

        txtSclDisplayWeight.setEditable(false);
        txtSclDisplayWeight.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSclDisplayWeight.setPreferredSize(new java.awt.Dimension(100, 28));

        btnSclReadWeight.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclReadWeight.setText("Read Weight");
        btnSclReadWeight.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclReadWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclReadWeightActionPerformed(evt);
            }
        });

        btnSclZeroScale.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclZeroScale.setText("Zero Scale");
        btnSclZeroScale.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclZeroScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclZeroScaleActionPerformed(evt);
            }
        });

        btnSclClearInput.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclClearInput.setText("Clear Input");
        btnSclClearInput.setActionCommand("btnClearInput");
        btnSclClearInput.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclClearInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclClearInputActionPerformed(evt);
            }
        });

        lblSclWeight.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclWeight.setText("Weight :");

        chkSclAsyncMode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        chkSclAsyncMode.setText("Async Mode");
        chkSclAsyncMode.setMargin(new java.awt.Insets(2, -1, 2, 2));
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
                    .addComponent(lblSclWeight))
                .addGap(16, 16, 16)
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSclRWTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSclDisplayWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSclClearInput, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclZeroScale, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclReadWeight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSclAsyncMode, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        sclPanelScaleWeightLayout.setVerticalGroup(
            sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(btnSclReadWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addComponent(btnSclZeroScale, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSclClearInput, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sclPanelScaleWeightLayout.createSequentialGroup()
                        .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSclRWTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSclReadWeght)
                            .addComponent(chkSclAsyncMode))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sclPanelScaleWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSclDisplayWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSclWeight))
                        .addGap(78, 78, 78)))
                .addContainerGap())
        );

        sclPanelLiveWeight.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Live Weight ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelLiveWeight.setPreferredSize(new java.awt.Dimension(481, 73));

        txtSclLiveWeight.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSclLiveWeight.setPreferredSize(new java.awt.Dimension(100, 28));

        chkSclEnableLiveWeight.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                .addComponent(txtSclLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(chkSclEnableLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        sclPanelLiveWeightLayout.setVerticalGroup(
            sclPanelLiveWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelLiveWeightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelLiveWeightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSclLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSclEnableLiveWeight))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sclPanelCheckHealth.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Check Health ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelCheckHealth.setPreferredSize(new java.awt.Dimension(480, 98));

        sclCheckHealthBtnGrp.add(sclInternal);
        sclInternal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sclInternal.setText("Internal");
        sclInternal.setMargin(new java.awt.Insets(2, -1, 2, 2));
        sclInternal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sclInternalActionPerformed(evt);
            }
        });

        sclCheckHealthBtnGrp.add(sclExternal);
        sclExternal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sclExternal.setText("External");
        sclExternal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sclExternalActionPerformed(evt);
            }
        });

        sclCheckHealthBtnGrp.add(sclInteractive);
        sclInteractive.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sclInteractive.setText("Interactive");
        sclInteractive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sclInteractiveActionPerformed(evt);
            }
        });

        btnSclCheckHealth.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclCheckHealth.setText("Check Health");
        btnSclCheckHealth.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclCheckHealth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclCheckHealthActionPerformed(evt);
            }
        });

        txtSclCheckHealthText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSclCheckHealthText.setPreferredSize(new java.awt.Dimension(100, 30));

        btnSclCHText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclCHText.setText("Health Check Text");
        btnSclCHText.setAlignmentX(0.5F);
        btnSclCHText.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclCHText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclCHTextActionPerformed(evt);
            }
        });

        lblSclCHText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclCHText.setText("HealthCheck Text  ");

        javax.swing.GroupLayout sclPanelCheckHealthLayout = new javax.swing.GroupLayout(sclPanelCheckHealth);
        sclPanelCheckHealth.setLayout(sclPanelCheckHealthLayout);
        sclPanelCheckHealthLayout.setHorizontalGroup(
            sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelCheckHealthLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(sclInternal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sclExternal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sclInteractive))
                    .addGroup(sclPanelCheckHealthLayout.createSequentialGroup()
                        .addComponent(btnSclCHText, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(lblSclCHText)))
                .addGap(0, 0, 0)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSclCheckHealth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSclCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        sclPanelCheckHealthLayout.setVerticalGroup(
            sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelCheckHealthLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sclInternal)
                    .addComponent(sclExternal)
                    .addComponent(sclInteractive)
                    .addComponent(btnSclCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(sclPanelCheckHealthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSclCheckHealthText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclCHText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSclCHText))
                .addGap(6, 6, 6))
        );

        sclPanelDirectIO.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Direct IO ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelDirectIO.setPreferredSize(new java.awt.Dimension(485, 359));

        lblSclCommand.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclCommand.setText("Command ");

        cmbSclCommand.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSclCommand.setAutoscrolls(true);
        cmbSclCommand.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmbSclCommand.setPreferredSize(new java.awt.Dimension(153, 28));
        cmbSclCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSclCommandActionPerformed(evt);
            }
        });

        btnSclExecute.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclExecute.setText("Execute");
        btnSclExecute.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclExecuteActionPerformed(evt);
            }
        });

        lblSclInXml.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclInXml.setText("InXml :");

        lblSclOutXml.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclOutXml.setText("OutXml :");

        jScrollPane8.setAutoscrolls(true);

        txtSclInXml.setColumns(100);
        txtSclInXml.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtSclInXml.setRows(50);
        txtSclInXml.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtSclInXml.setSelectionColor(new java.awt.Color(153, 204, 255));
        jScrollPane8.setViewportView(txtSclInXml);

        jScrollPane9.setAutoscrolls(true);

        txtSclOutXml.setEditable(false);
        txtSclOutXml.setColumns(100);
        txtSclOutXml.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtSclOutXml.setLineWrap(true);
        txtSclOutXml.setRows(50);
        txtSclOutXml.setWrapStyleWord(true);
        txtSclOutXml.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtSclOutXml.setSelectionColor(new java.awt.Color(153, 204, 255));
        jScrollPane9.setViewportView(txtSclOutXml);

        lblSclStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclStatus.setText("Status ");

        txtSclStatus.setEditable(false);
        txtSclStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSclStatus.setPreferredSize(new java.awt.Dimension(100, 30));

        btnSclClear.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclClear.setText("Clear");
        btnSclClear.setPreferredSize(new java.awt.Dimension(100, 30));
        btnSclClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclClearActionPerformed(evt);
            }
        });

        btnSclCopy.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(lblSclStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSclStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelDirectIOLayout.createSequentialGroup()
                            .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblSclInXml)
                                .addComponent(lblSclCommand))
                            .addGap(4, 4, 4)
                            .addComponent(cmbSclCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                                .addComponent(lblSclOutXml)
                                .addGap(187, 187, 187))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelDirectIOLayout.createSequentialGroup()
                                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelDirectIOLayout.createSequentialGroup()
                                        .addComponent(btnSclCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnSclClear, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(10, 10, 10))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelDirectIOLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSclExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );
        sclPanelDirectIOLayout.setVerticalGroup(
            sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbSclCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSclCommand))
                        .addGap(6, 6, 6)
                        .addComponent(lblSclInXml))
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(btnSclExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblSclOutXml)))
                .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSclStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSclStatus)))
                    .addGroup(sclPanelDirectIOLayout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sclPanelDirectIOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSclClear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSclCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );

        sclPanelStat.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Statistics ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelStat.setPreferredSize(new java.awt.Dimension(503, 69));

        lblSclStatistic.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btnSclResetStat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclResetStat.setText("Reset Statistics");
        btnSclResetStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclResetStatActionPerformed(evt);
            }
        });

        btnSclRetreiveStat.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSclRetreiveStat.setText("Retrieve Statistics");
        btnSclRetreiveStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSclRetreiveStatActionPerformed(evt);
            }
        });

        txtSclStatOutput.setColumns(4);
        txtSclStatOutput.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtSclStatOutput.setRows(5);
        jScrollPane10.setViewportView(txtSclStatOutput);

        javax.swing.GroupLayout sclPanelStatLayout = new javax.swing.GroupLayout(sclPanelStat);
        sclPanelStat.setLayout(sclPanelStatLayout);
        sclPanelStatLayout.setHorizontalGroup(
            sclPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelStatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sclPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sclPanelStatLayout.createSequentialGroup()
                        .addComponent(lblSclStatistic, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSclRetreiveStat, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(btnSclResetStat, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sclPanelStatLayout.setVerticalGroup(
            sclPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sclPanelStatLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(sclPanelStatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSclStatistic, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclResetStat, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSclRetreiveStat, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        sclPanelProperties.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Properties ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        sclPanelProperties.setPreferredSize(new java.awt.Dimension(480, 112));

        cmbSclProperties.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSclProperties.setPreferredSize(new java.awt.Dimension(100, 28));
        cmbSclProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSclPropertiesActionPerformed(evt);
            }
        });

        lblSclPropertyValue.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSclPropertyValue.setText("Property Value ");

        txtSclPropertyValue.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSclPropertyValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSclPropertyValue.setPreferredSize(new java.awt.Dimension(100, 28));
        txtSclPropertyValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSclPropertyValueActionPerformed(evt);
            }
        });

        btnSclProperties.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                .addGap(10, 10, 10)
                .addGroup(sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sclPanelPropertiesLayout.createSequentialGroup()
                        .addComponent(lblSclPropertyValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSclPropertyValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(cmbSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        sclPanelPropertiesLayout.setVerticalGroup(
            sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sclPanelPropertiesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(cmbSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(sclPanelPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSclPropertyValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSclPropertyValue)
                    .addComponent(btnSclProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout scalePanelLayout = new javax.swing.GroupLayout(scalePanel);
        scalePanel.setLayout(scalePanelLayout);
        scalePanelLayout.setHorizontalGroup(
            scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sclPanelLiveWeight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclPanelDataEvent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclPanelScaleWeight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclPanelStat, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sclPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(sclPanelCheckHealth, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(sclPanelProperties, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addGap(46, 46, 46))
        );
        scalePanelLayout.setVerticalGroup(
            scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scalePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sclPanelDataEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sclPanelCheckHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scalePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(scalePanelLayout.createSequentialGroup()
                        .addComponent(sclPanelProperties, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sclPanelDirectIO, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(scalePanelLayout.createSequentialGroup()
                        .addComponent(sclPanelScaleWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(sclPanelLiveWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(sclPanelStat, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );

        jTabbedPane.addTab("", scalePanel);

        scnPanelCommonMethods.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        scnPanelCommonMethods.setPreferredSize(new java.awt.Dimension(100, 30));

        lblLogicalName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblLogicalName.setText("Logical Device Name :");

        cmbLogicalDevice.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbLogicalDevice.setPreferredSize(new java.awt.Dimension(153, 28));
        cmbLogicalDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLogicalDeviceActionPerformed(evt);
            }
        });

        btnOpen.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        btnOpen.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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

        lblClaimTimeout.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblClaimTimeout.setText("Claim Timeout (ms) :");
        lblClaimTimeout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtClaimTimeout.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtClaimTimeout.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtClaimTimeout.setText("1000");
        txtClaimTimeout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClaimTimeoutActionPerformed(evt);
            }
        });

        btnClaim.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.light"));
        btnClaim.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
        btnRelease.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
        btnClose.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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

        btnFastMode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnFastMode.setText("Fast Mode");
        btnFastMode.setPreferredSize(new java.awt.Dimension(120, 30));
        btnFastMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFastModeActionPerformed(evt);
            }
        });

        lblDeviceInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDeviceInfo.setText("Device Information :");

        txtDeviceInfo.setColumns(20);
        txtDeviceInfo.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtDeviceInfo.setLineWrap(true);
        txtDeviceInfo.setTabSize(0);
        txtDeviceInfo.setWrapStyleWord(true);
        jScrollPane5.setViewportView(txtDeviceInfo);

        txtLogicalInfo.setColumns(20);
        txtLogicalInfo.setRows(5);
        txtScannerInfoScroll.setViewportView(txtLogicalInfo);

        javax.swing.GroupLayout scnPanelCommonMethodsLayout = new javax.swing.GroupLayout(scnPanelCommonMethods);
        scnPanelCommonMethods.setLayout(scnPanelCommonMethodsLayout);
        scnPanelCommonMethodsLayout.setHorizontalGroup(
            scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scnPanelCommonMethodsLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scnPanelCommonMethodsLayout.createSequentialGroup()
                        .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDeviceInfo)
                            .addComponent(lblLogicalName))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelCommonMethodsLayout.createSequentialGroup()
                        .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, scnPanelCommonMethodsLayout.createSequentialGroup()
                                .addComponent(lblClaimTimeout, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClaimTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnClaim, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFastMode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbLogicalDevice, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnOpen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRelease, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClose, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtScannerInfoScroll, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(7, 7, 7))))
        );
        scnPanelCommonMethodsLayout.setVerticalGroup(
            scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scnPanelCommonMethodsLayout.createSequentialGroup()
                .addComponent(lblLogicalName)
                .addGap(3, 3, 3)
                .addComponent(cmbLogicalDevice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScannerInfoScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scnPanelCommonMethodsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClaimTimeout)
                    .addComponent(txtClaimTimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClaim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRelease, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFastMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblDeviceInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelLogView.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Log View :", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        jScrollPane7.setAutoscrolls(true);

        txtLogView.setColumns(20);
        txtLogView.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtLogView.setRows(5);
        jScrollPane7.setViewportView(txtLogView);

        btnLogClear.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
                .addGroup(panelLogViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLogViewLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 961, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLogViewLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(btnLogClear, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelLogViewLayout.setVerticalGroup(
            panelLogViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLogViewLayout.createSequentialGroup()
                .addComponent(jScrollPane7)
                .addGap(3, 3, 3)
                .addComponent(btnLogClear, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(scnPanelCommonMethods, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 997, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelLogView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelLogView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(scnPanelCommonMethods, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)))
        );

        jScrollPane11.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //methods that are common to scanner and scale
    public void commonMethods(String actionCommand) {

        logicalName = (String) cmbLogicalDevice.getSelectedItem();
        DeviceTypeBinder deviceTypeBinder = null;
        if(isScannerAvailable){
            if(null != unUsedScannerTab){
                deviceTypeBinder = (DeviceTypeBinder) new ScannerDeviceTypeBinder(unUsedScannerTab);
            }
            else{
                deviceTypeBinder = (DeviceTypeBinder) new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));
            }
        }
        else if(isScaleAvailable){
            deviceTypeBinder = (DeviceTypeBinder) new ScaleDeviceTypeBinder(JposSampleApp.scale);
        }

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy: HH:mm:ss");
        Date today = new Date();
        String date = DATE_FORMAT.format(today);

        //common Methods
        switch (actionCommand) {
            case "open":
                status = intermediateLayer.openAction(deviceTypeBinder);

                // Get the device info if OpenAction is successful.
                if (status[1] == null) {
                    info = intermediateLayer.getDeviceInfo(deviceTypeBinder);
                    if(isScannerAvailable){
                        openedScanners.add(scannerTabName);
                        logicalDeviceInfo = intermediateLayer.getLogicalDeviceInfo(scannerDeviceInfoList, logicalName);
                    } 
                    else if (isScaleAvailable) logicalDeviceInfo = "No Device Model or \nDevice Serial available \nfor Scale";
                }
                txtDeviceInfo.setText(info);
                txtLogicalInfo.setText(logicalDeviceInfo);
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
                if (opCode == DirectIOCommand.NCRDIO_SCAN_DIRECT || opCode == DirectIOCommand.DIO_NCR_SCANNER_NOF
                        || opCode == com.zebra.jpos.serviceonscale.directio.DirectIOCommand.NCRDIO_SCAL_DIRECT) {
                    status = intermediateLayer.ncrDirectIOAction(deviceTypeBinder, opCode, statusScanner, deviceParams);
                } else if (opCode == DirectIOCommand.DIO_NCR_SCAN_TONE) {
                    try {
                        // Get the NCR tone type from the UI
                        statusScanner[0] = Integer.parseInt(txtScnInxml.getText());
                        status = intermediateLayer.ncrDirectIOAction(deviceTypeBinder, opCode, statusScanner, deviceParams);
                        statusScanner[0] = DirectIOStatus.STATUS_SUCCESS;
                    } catch (Exception e) {
                        statusScanner[0] = -1;
                        status[0] = "Invalid NCR Tone Type";
                        status[1] = intermediateLayer.exceptionDialog(new JposException(0), "Invalid NCR Tone Type ");
                    }
                } else if (opCode == com.zebra.jpos.serviceonscale.directio.DirectIOCommand.NCR_DIO_SCAL_LIVE_WEIGHT) {

                    status = intermediateLayer.ncrDirectIOLiveWeightAction(deviceTypeBinder, opCode, statusScanner, deviceParams);
                    // Set the weight value to deviceParams. Weight value comes in the statusScanner[0]. deviceParams is used to show the result in the UI
                    deviceParams.append(statusScanner[0]);

                    if (status[0].equals("Direct IO Successful")) // Set the statusScanner[0] to success
                    {
                        statusScanner[0] = DirectIOStatus.STATUS_SUCCESS;
                    } else {
                        statusScanner[0] = -1;
                    }
                } else {
                    status = intermediateLayer.directIOAction(deviceTypeBinder, opCode, statusScanner, deviceParams);
                }
                break;
            case "clearInput":
                status = intermediateLayer.clearInputAction(deviceTypeBinder);
                break;
            case "errorOccured":
                status = errorStatus;
                break;
            case "setProperty":
                status = setPropertyStatus;
                break;
            case "retrieveStatistics":
                status = intermediateLayer.retrieveStatistics(deviceTypeBinder, retrieveStatValue);
                break;
            case "resetStatistics":
                status = intermediateLayer.resetStatistics(deviceTypeBinder, resetStatValue);
                break;
            case "powerNotify":
                status = intermediateLayer.PowerNotify(deviceTypeBinder, powerNotifyEnabled);
                break;
            case "powerState":
                status = intermediateLayer.PowerState(deviceTypeBinder);
                break;
        }
        txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[0]);
        if (status[1] != null) {
            txtLogView.setText(txtLogView.getText() + "\n" + date + " : " + deviceTypeBinder.getDevice() + "      :" + logicalName + " " + status[1]);
        }
    }

    private void scannerMethods(String actionCommand) {
        logicalName = (String) cmbLogicalDevice.getSelectedItem();
        ScannerDeviceTypeBinder deviceTypeBinder = new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));

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
        ScaleDeviceTypeBinder deviceTypeBinder = new ScaleDeviceTypeBinder(JposSampleApp.scale);

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
                status = intermediateLayer.statusNotifyAction(deviceTypeBinder, statusNotifyEnabled);
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
            case "clearInput":
                status = intermediateLayer.clearInputAction(deviceTypeBinder);
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
        if(isScannerAvailable){
            chkScnDeviceEnable.setSelected(false);
            if(!chkScnDeviceEnable.isEnabled()) chkScnDeviceEnable.setEnabled(true);
            chkScnDataEventEnable.setSelected(false);
            chkScnDecodeData.setSelected(false);
            chkScnAutoDataEventEnable.setSelected(false);
            chkScnAutoDeviceEnable.setSelected(false);
            chkScnFreezeEvents.setSelected(false);
            chkScnAutoDisable.setSelected(false);
            chkScnPowerNotify.setSelected(false);
            txtScnOutXml.setText("");
            txtScnHealthCheckText.setText("");
            txtScanDataLabel.setText("");               
            txtScanData.setText("");      
            txtScanDataType.setText("");        
            txtScnPowerState.setText("");
            txtScnPropertyValue.setText("");
            txtScnStatOutput.setText("");
            autoDataEventEnableScanner = false;
        }
        else if(isScaleAvailable){
            asyncModeEnabled = false;
            asyncModeC = false;
            chkSclDeviceEnable.setSelected(false);
            if(!chkSclDeviceEnable.isEnabled()) chkSclDeviceEnable.setEnabled(true);
            chkSclDataEventEnable.setSelected(false);
            chkSclAsyncMode.setSelected(false);
            chkSclAutoDeviceEnable.setSelected(false);
            chkSclAutoDisable.setSelected(false);
            chkSclEnableLiveWeight.setSelected(false);
            chkSclFreezeEvents.setSelected(false);
            if(!chkSclEnableLiveWeight.isEnabled()) chkSclEnableLiveWeight.setEnabled(true);
            chkSclAutoDataEventEnable.setSelected(false);
            txtSclDisplayWeight.setText("");
            txtSclLiveWeight.setText("");
            txtSclOutXml.setText("");
            txtSclCheckHealthText.setText("");
            txtSclPropertyValue.setText("");
            txtSclStatOutput.setText("");
            autoDataEventEnableScale = false;
            chkSclPowerNotify.setSelected(false);
        }     
        selectedIndex = 0;
        commonMethods("close");
    }//GEN-LAST:event_btnCloseActionPerformed
    
     /**
     * Only execute when a tab close forcefully from the tab close icon.
     * This method is used to close the service from logical device.
     *
     * @param tabName : particular tab name
     */
    public static void closeScanner(String tabName){            
      DeviceTypeBinder deviceTypeBinder = (DeviceTypeBinder) new ScannerDeviceTypeBinder(scannersList.get(tabName));
      if(openedScanners.contains(tabName)){
        try {
            deviceTypeBinder.setClose();
        } catch (JposException ex) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        openedScanners.remove(tabName);
      }
    }
    
    private void btnClaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClaimActionPerformed

        try {
            claimTimeout = Integer.valueOf(txtClaimTimeout.getText());
            commonMethods("claim");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Timeout", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnClaimActionPerformed

    private void btnReleaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReleaseActionPerformed
          chkScnDeviceEnable.setSelected(false);
          if(!chkScnDeviceEnable.isEnabled()) chkScnDeviceEnable.setEnabled(true);
          chkScnAutoDeviceEnable.setSelected(false);
          
          chkSclDeviceEnable.setSelected(false);
          if(!chkSclDeviceEnable.isEnabled()) chkSclDeviceEnable.setEnabled(true);
          chkSclAutoDeviceEnable.setSelected(false);
          if(!chkSclEnableLiveWeight.isEnabled()) chkSclEnableLiveWeight.setEnabled(true);
          if(isScaleAvailable) txtSclLiveWeight.setText("");
          commonMethods("release");
    }//GEN-LAST:event_btnReleaseActionPerformed


    private void chkSclDataEventEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclDataEventEnableActionPerformed
        if (chkSclDataEventEnable.isSelected()) {
            dataEventEnabled = true;
        } else if (!chkSclDataEventEnable.isSelected()) {
            dataEventEnabled = false;
        }
        commonMethods("enableDataEvent");
        chkSclDataEventEnable.setSelected(dataEventEnableC);
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
        if (!statusNotifyC) {
            chkSclEnableLiveWeight.setSelected(false);
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
        chkSclFreezeEvents.setSelected(freezeEventsC);
    }//GEN-LAST:event_chkSclFreezeEventsActionPerformed

    private void btnSclReadWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclReadWeightActionPerformed
        try {
            scaleRWTimeout = Integer.valueOf(txtSclRWTimeout.getText());
            scaleMethods("readWeight");
            if (asyncModeEnabled) {
                weightAM = 0;
                if(!freezeEventsEnabled) txtSclDisplayWeight.setText("0.000 " + units);
                if (chkSclAutoDisable.isSelected()) {
                    chkSclDeviceEnable.setSelected(false);
                    if(chkSclAutoDeviceEnable.isSelected())chkSclAutoDeviceEnable.setSelected(false);
                    if(!chkSclDeviceEnable.isEnabled()) chkSclDeviceEnable.setEnabled(true);
                }
            } else {
                float weight = readWeight[0] / 1000f;
                
                // Format the number to x.xxx form
                NumberFormat formatter = new DecimalFormat("#0.000");
                txtSclDisplayWeight.setText(formatter.format(weight) + " " + units);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Timeout", "Failed", JOptionPane.ERROR_MESSAGE);
            txtSclRWTimeout.setText("1000");
        }

    }//GEN-LAST:event_btnSclReadWeightActionPerformed

    private void btnSclZeroScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclZeroScaleActionPerformed
        scaleMethods("zeroScale");
    }//GEN-LAST:event_btnSclZeroScaleActionPerformed

    private void scnInternalCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scnInternalCHActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERNAL;
        selectedIndex = 0;
    }//GEN-LAST:event_scnInternalCHActionPerformed

    private void scnExternalCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scnExternalCHActionPerformed
        healthCheckType = JposConst.JPOS_CH_EXTERNAL;
        selectedIndex = 1;
    }//GEN-LAST:event_scnExternalCHActionPerformed

    private void scnInteractiveCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scnInteractiveCHActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERACTIVE;
        selectedIndex = 2;
    }//GEN-LAST:event_scnInteractiveCHActionPerformed

    private void btnScnCheckHealthTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnCheckHealthTextActionPerformed

        commonMethods("healthCheckText");
        txtScnHealthCheckText.setText(healthCheckText);
    }//GEN-LAST:event_btnScnCheckHealthTextActionPerformed

    private void chkSclDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclDeviceEnableActionPerformed
        if (chkSclDeviceEnable.isSelected()) {
            deviceEnabled = true;
            commonMethods("enableDevice");
        } else {
            deviceEnabled = false;
            chkSclEnableLiveWeight.setEnabled(true);
            txtSclLiveWeight.setText("");
            commonMethods("enableDevice");
        }

        chkSclEnableLiveWeight.setEnabled(false);
        chkSclDeviceEnable.setSelected(deviceEnableC);
        chkSclEnableLiveWeight.setEnabled(!deviceEnableC);
    }//GEN-LAST:event_chkSclDeviceEnableActionPerformed

    private void btnSclClearInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclClearInputActionPerformed
        scaleMethods("clearInput");
        txtSclDisplayWeight.setText("");
    }//GEN-LAST:event_btnSclClearInputActionPerformed

    private void sclInternalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sclInternalActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERNAL;
        selectedIndex = 0;
    }//GEN-LAST:event_sclInternalActionPerformed

    private void sclExternalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sclExternalActionPerformed
        healthCheckType = JposConst.JPOS_CH_EXTERNAL;
        selectedIndex = 1;
    }//GEN-LAST:event_sclExternalActionPerformed

    private void sclInteractiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sclInteractiveActionPerformed
        healthCheckType = JposConst.JPOS_CH_INTERACTIVE;
        selectedIndex = 2;
    }//GEN-LAST:event_sclInteractiveActionPerformed

    private void btnSclCheckHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclCheckHealthActionPerformed
        if (healthCheckType == -1) {
            healthCheckType = JposConst.JPOS_CH_INTERNAL;
            sclInternal.setSelected(true);
        }
        commonMethods("healthCheck");
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
    
    private void updateScannerIntProperties(String propertyName, int value) {
        switch (propertyName) {
            case "PIDX_PowerNotify":
                chkScnPowerNotify.setSelected(value == JposConst.JPOS_PN_ENABLED);
                break;
            default:
                //none
        }
    }

    private void btnScnPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnPropertiesActionPerformed
        scannerPropertyValue = txtScnPropertyValue.getText();
        PropertyBinder propertyBinder = (PropertyBinder) cmbScnProperties.getSelectedItem();
        String type = propertyBinder.getType();
        String propertyName = propertyBinder.getPropertyName();
        try {
            if ((type.equals("boolean")) && ((scannerPropertyValue.equalsIgnoreCase("true")) || (scannerPropertyValue.equalsIgnoreCase("false")))) {
                propertyBinder.setBoolean(Boolean.valueOf(scannerPropertyValue));
                updateChkScanner(propertyName, Boolean.valueOf(scannerPropertyValue));
                setPropertyStatus[0] = "Property Changed     :" + propertyName;
            } else if (type.equals("int")) {
                propertyBinder.setInt(Integer.valueOf(scannerPropertyValue));
                updateScannerIntProperties(propertyName, Integer.valueOf(scannerPropertyValue));
                setPropertyStatus[0] = "Property Changed     :" + propertyName;
            } else {
                setPropertyStatus[0] = "Incorrect Parameter Value     :" + propertyName;
            }
        } catch (NumberFormatException ex) {
            setPropertyStatus[0] = "Incorrect Parameter Value     :" + propertyName;
        } catch (JposException ex) {
            setPropertyStatus[0] = "Exception in set Properties     :" + ex.getMessage();
            JOptionPane.showMessageDialog(null, "Exception in set Properties : " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        commonMethods("setProperty");
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
                    if(propertyBinder.getPropertyName().equals("PIDX_DeviceEnabled")){
                        updateChkScanner("PIDX_DeviceEnabled", boolValue);
                    }
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
                    if(propertyBinder.getPropertyName().equals("PIDXScan_ScanData") || propertyBinder.getPropertyName().equals("PIDXScan_ScanDataLabel")){
                        changeScanDataForHexOrText(byteValue);
                    }
                    else{
                        txtScnPropertyValue.setText(Arrays.toString(byteValue));
                    }
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

    private void changeScanDataForHexOrText(byte[] bytes){
        if (scanDataHex.isSelected()) {
            txtScnPropertyValue.setText(intermediateLayer.getHexEncodedDataLabel(bytes));
        } else {
            txtScnPropertyValue.setText(new String(bytes));
        }
    }
    
    private void cmbSclCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSclCommandActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbSclCommand.getSelectedItem();
        txtSclInXml.setText(dIOCommand.getInXml());                        //display inXml in the textField

    }//GEN-LAST:event_cmbSclCommandActionPerformed

    private void btnSclExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclExecuteActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbSclCommand.getSelectedItem();
        ScaleDeviceTypeBinder deviceTypeBinder = new ScaleDeviceTypeBinder(JposSampleApp.scale);
        deviceParams = new StringBuffer();
        deviceParams.append(txtSclInXml.getText());
        statusScanner = new int[]{-1};
        opCode = dIOCommand.getOpCode();

        commonMethods("directInputOutput");
        if (directIOC) {
            txtSclOutXml.setText(deviceParams.toString());
        } else {
            txtSclOutXml.setText("");
        }

        boolean sclDeviceEnabled = intermediateLayer.checkDeviceEnable(deviceTypeBinder);
        chkSclDeviceEnable.setSelected(sclDeviceEnabled);

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
                if (chkSclDeviceEnable.isSelected()) {
                    chkSclEnableLiveWeight.setEnabled(false);
                }else{
                    chkSclEnableLiveWeight.setEnabled(true);
                }
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

    private void updateScaleIntProperties(String propertyName, int value) {
        switch (propertyName) {
            case "PIDX_PowerNotify":
                chkSclPowerNotify.setSelected(value == JposConst.JPOS_PN_ENABLED);
                break;
            case "PIDXScal_EnableLiveWeight":            
                if (!chkSclDeviceEnable.isSelected()) {
                    chkSclEnableLiveWeight.setSelected(value == ScaleConst.SCAL_SN_ENABLED);
                }
            default:
                //none
        }
    }
    
    private void btnSclPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclPropertiesActionPerformed
        scalePropertyValue = txtSclPropertyValue.getText();
        PropertyBinder propertyBinder = (PropertyBinder) cmbSclProperties.getSelectedItem();
        String type = propertyBinder.getType();
        String propertyName = propertyBinder.getPropertyName();
        try {

            if ((type.equals("boolean")) && ((scalePropertyValue.equals("true")) || (scalePropertyValue.equals("false")))) { 
                propertyBinder.setBoolean(Boolean.valueOf(scalePropertyValue));
                updateChkScale(propertyName, Boolean.valueOf(scalePropertyValue)); 
                setPropertyStatus[0] = "Property Changed     :" + propertyName;
            } else if (type.equals("int")) {
                propertyBinder.setInt(Integer.valueOf(scalePropertyValue));
                updateScaleIntProperties(propertyName, Integer.valueOf(scalePropertyValue));
                setPropertyStatus[0] = "Property Changed     :" + propertyName;
            } else if (type.equals("long")) {
                propertyBinder.setLong(Long.valueOf(scalePropertyValue));
            } else {
                setPropertyStatus[0] = "Incorrect Parameter Value     :" + propertyName;
            }
        } catch (NumberFormatException ex) {
            setPropertyStatus[0] = "Incorrect Parameter Value     :" + propertyName;
        } catch (JposException ex) {
            setPropertyStatus[0] = "Exception in set Properties     :" + ex.getMessage();
            JOptionPane.showMessageDialog(null, "Exception in set Properties : " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        commonMethods("setProperty");
    }//GEN-LAST:event_btnSclPropertiesActionPerformed

    private void chkScnDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnDeviceEnableActionPerformed
        if (chkScnDeviceEnable.isSelected()) {
            deviceEnabled = true;
            commonMethods("enableDevice");
        } else if (!chkScnDeviceEnable.isSelected()) {
            deviceEnabled = false;
            commonMethods("enableDevice");
        }
        chkScnDeviceEnable.setSelected(deviceEnableC);
    }//GEN-LAST:event_chkScnDeviceEnableActionPerformed

    private String powerStateText(int value) {
        String text = "";
        switch (value) {
            case 2000:
                text = "JPOS_PS_UNKNOWN";
                break;
            case 2001:
                text = "JPOS_PS_ONLINE";
                break;
            case 2002:
                text = "JPOS_PS_OFF";
                break;
            case 2003:
                text = "JPOS_PS_OFFLINE";
                break;
            case 2004:
                text = "JPOS_PS_OFF_OFFLINE";
                break;
        }
        return text;
    }

    private void chkScnDataEventEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnDataEventEnableActionPerformed
        if (chkScnDataEventEnable.isSelected()) {
            dataEventEnabled = true;
        } else if (!chkScnDataEventEnable.isSelected()) {
            dataEventEnabled = false;
        }
        commonMethods("enableDataEvent");
        chkScnDataEventEnable.setSelected(dataEventEnableC);
    }//GEN-LAST:event_chkScnDataEventEnableActionPerformed

    private void btnFastModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFastModeActionPerformed
        DeviceTypeBinder deviceTypeBinder = null;
        if(isScannerAvailable){
           deviceTypeBinder = (DeviceTypeBinder) new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));
        }
        else if(isScaleAvailable){
            deviceTypeBinder = (DeviceTypeBinder) new ScaleDeviceTypeBinder(JposSampleApp.scale);
        }
        deviceType = deviceTypeBinder != null ? deviceTypeBinder.getDevice() : null;

        if (null != deviceType) {
            switch (deviceType) {
                case "Scanner":
                    scannerMethods("fastModeScanner");
                    if (fastModeScannerC) {
                        chkScnDeviceEnable.setSelected(true);
                        chkScnDataEventEnable.setSelected(true); 
                        chkScnDecodeData.setSelected(true);
                    }
                    break;
                case "Scale  ":
                    scaleMethods("fastModeScale");
                    if (fastModeScaleC) {
                        chkSclDeviceEnable.setSelected(true);
                        chkSclEnableLiveWeight.setEnabled(false);
                    }
                    break;
            }
        }
    }//GEN-LAST:event_btnFastModeActionPerformed

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
            chkScnDataEventEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkScnAutoDataEventEnableActionPerformed

    private void btnScnCheckHealthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnCheckHealthActionPerformed

        if (healthCheckType == -1) {
            healthCheckType = JposConst.JPOS_CH_INTERNAL;
            scnInternalCH.setSelected(true);
        }
        commonMethods("healthCheck");
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
        chkScnFreezeEvents.setSelected(freezeEventsC);
    }//GEN-LAST:event_chkScnFreezeEventsActionPerformed

    private void logicalDeviceName(String deviceCategory) {
        SimpleEntryRegistry reg = new SimpleEntryRegistry(new SimpleXmlRegPopulator());
        reg.load();

        int scn = 0;
        int scl = 0;

        Enumeration entryCountEnum = reg.getEntries();

        while (entryCountEnum.hasMoreElements()) {
            JposEntry entry = (JposEntry) entryCountEnum.nextElement();
            String[] deviceCount = {entry.getProp(JposEntry.DEVICE_CATEGORY_PROP_NAME).getValueAsString()};

            switch (deviceCount[0]) {
                case "Scanner":
                    scn++;
                    break;
                case "Scale":
                    scl++;
                    break;
            }
        }

        int i = 0;
        int j = 0;

        Object[] scnLogicalNames = new Object[scn];
        Object[] sclLogicalNames = new Object[scl];

        Enumeration entriesEnum = reg.getEntries();
        while (entriesEnum.hasMoreElements()) {
            JposEntry entry = (JposEntry) entriesEnum.nextElement();

            String[] row = {entry.getProp(JposEntry.DEVICE_CATEGORY_PROP_NAME).getValueAsString(), entry.getLogicalName()};

            String[] scannerInfoArray = {entry.getProp(JPOS_XML_SCANNER_MODEL) != null ? entry.getProp(JPOS_XML_SCANNER_MODEL).getValueAsString() : "",
                                entry.getProp(JPOS_XML_SCANNER_SERIAL) != null ? entry.getProp(JPOS_XML_SCANNER_SERIAL).getValueAsString() : ""};
            
            switch (row[0]) {
                case "Scanner":
                    scnLogicalNames[i] = row[1];
                    scannerDeviceInfoList.put(row[1], scannerInfoArray);
                    i++;
                    break;
                case "Scale":
                    sclLogicalNames[j] = row[1];
                    j++;
                    break;
            }
        }

        // Sort logical names in alphabatical order
        Arrays.sort(scnLogicalNames);
        Arrays.sort(sclLogicalNames);

        switch (deviceCategory) {
            case "Scanner":
                DefaultComboBoxModel model1 = new DefaultComboBoxModel(scnLogicalNames);
                cmbLogicalDevice.setModel(model1);
                break;
            case "Scale  ":
                DefaultComboBoxModel model2 = new DefaultComboBoxModel(sclLogicalNames);
                cmbLogicalDevice.setModel(model2);
                break;
        }
    }

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        commonMethods("open");       
    }//GEN-LAST:event_btnOpenActionPerformed

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
        chkScnAutoDisable.setSelected(autoDisableC);
    }//GEN-LAST:event_chkScnAutoDisableActionPerformed

    private void chkSclAutoDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclAutoDisableActionPerformed
        if (chkSclAutoDisable.isSelected()) {
            autoDisable = true;
        } else if (!chkSclAutoDisable.isSelected()) {
            autoDisable = false;
        }
        commonMethods("autoDisable");
        chkSclAutoDisable.setSelected(autoDisableC);
    }//GEN-LAST:event_chkSclAutoDisableActionPerformed

    private void chkSclAutoDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclAutoDeviceEnableActionPerformed
        autoDeviceEnable = chkSclAutoDeviceEnable.isSelected();
        
        if (autoDeviceEnable) {
            deviceEnabled = true;
            commonMethods("enableDevice");
        }
        else{
            deviceEnabled = false;
            commonMethods("enableDevice");
        }
        
        chkSclDeviceEnable.setSelected(deviceEnableC);
        chkSclAutoDeviceEnable.setSelected(deviceEnableC);
        chkSclDeviceEnable.setEnabled(!deviceEnableC);
        chkSclEnableLiveWeight.setEnabled(!deviceEnableC);
    }//GEN-LAST:event_chkSclAutoDeviceEnableActionPerformed

    private void chkScnAutoDeviceEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnAutoDeviceEnableActionPerformed
        autoDeviceEnable = chkScnAutoDeviceEnable.isSelected();
        
        if (autoDeviceEnable) {
            deviceEnabled = true;
            commonMethods("enableDevice");
        }
        else{
            deviceEnabled = false;
            commonMethods("enableDevice");
        }
       
        chkScnDeviceEnable.setSelected(deviceEnableC);
        chkScnAutoDeviceEnable.setSelected(deviceEnableC);
        chkScnDeviceEnable.setEnabled(!deviceEnableC);
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
            chkSclDataEventEnable.setSelected(false);
        }
    }//GEN-LAST:event_chkSclAutoDataEventEnableActionPerformed

    private void btnScnResetStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnResetStatActionPerformed
        resetStatValue = lblScnStatistic.getText();
        commonMethods("resetStatistics");
    }//GEN-LAST:event_btnScnResetStatActionPerformed

    private void btnScnRetreiveStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnRetreiveStatActionPerformed
        retrieveStatValue[0] = lblScnStatistic.getText();
        commonMethods("retrieveStatistics");
        txtScnStatOutput.setText(formatXml(retrieveStatValue[0]));
    }//GEN-LAST:event_btnScnRetreiveStatActionPerformed

    public String formatXml(String xml) {
        try {
            StringWriter stringWriter = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            if(!xml.isEmpty()){
                transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(stringWriter));
            }
            return stringWriter.toString().trim();
        } catch (IllegalArgumentException | TransformerException e) {
            return xml;
        }
    }

    private void btnSclRetreiveStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclRetreiveStatActionPerformed
        retrieveStatValue[0] = lblSclStatistic.getText();
        commonMethods("retrieveStatistics");
        txtSclStatOutput.setText(formatXml(retrieveStatValue[0]));
    }//GEN-LAST:event_btnSclRetreiveStatActionPerformed

    private void btnSclResetStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclResetStatActionPerformed
        resetStatValue = lblSclStatistic.getText();
        commonMethods("resetStatistics");
    }//GEN-LAST:event_btnSclResetStatActionPerformed

    private void chkScnPowerNotifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScnPowerNotifyActionPerformed
        if (chkScnPowerNotify.isSelected()) {
            powerNotifyEnabled = JposConst.JPOS_PN_ENABLED;
        } else {
            powerNotifyEnabled = JposConst.JPOS_PN_DISABLED;
        }
        commonMethods("powerNotify");

        chkScnPowerNotify.setSelected(powerNotifyC);
    }//GEN-LAST:event_chkScnPowerNotifyActionPerformed

    private void chkSclPowerNotifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSclPowerNotifyActionPerformed
        if (chkSclPowerNotify.isSelected()) {
            powerNotifyEnabled = JposConst.JPOS_PN_ENABLED;
        } else {
            powerNotifyEnabled = JposConst.JPOS_PN_DISABLED;
        }
        commonMethods("powerNotify");
        chkSclPowerNotify.setSelected(powerNotifyC);  
    }//GEN-LAST:event_chkSclPowerNotifyActionPerformed

    private void btnScnPowerStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnPowerStateActionPerformed
        commonMethods("powerState");
        txtScnPowerState.setText(powerStateText(powerState));
    }//GEN-LAST:event_btnScnPowerStateActionPerformed

    private void btnSclPowerStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSclPowerStateActionPerformed
        commonMethods("powerState");
        txtSclPowerState.setText(Integer.toString(powerState));
    }//GEN-LAST:event_btnSclPowerStateActionPerformed

    private void btnScnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnCopyActionPerformed
        String outXml;
        outXml = txtScnOutXml.getText();
        StringSelection selection = new StringSelection(outXml);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }//GEN-LAST:event_btnScnCopyActionPerformed

    private void btnScnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnClearActionPerformed
        txtScnOutXml.setText("");
        txtScnStatus.setText("");
    }//GEN-LAST:event_btnScnClearActionPerformed

    private void btnScnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScnExecuteActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbScnCommand.getSelectedItem();
        ScannerDeviceTypeBinder deviceTypeBinder = new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));
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
        boolean scnDeviceEnabled = intermediateLayer.checkDeviceEnable(deviceTypeBinder);
        chkScnDeviceEnable.setSelected(scnDeviceEnabled);

        txtScnStatus.setText(Arrays.toString(statusScanner).replace("[", " ").replace("]", " "));
    }//GEN-LAST:event_btnScnExecuteActionPerformed

    private void cmbScnCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbScnCommandActionPerformed
        DirectIOBinding dIOCommand = (DirectIOBinding) cmbScnCommand.getSelectedItem();
        txtScnInxml.setText(dIOCommand.getInXml());                        //display inXml in the textField
    }//GEN-LAST:event_cmbScnCommandActionPerformed

    private void cmbLogicalDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLogicalDeviceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbLogicalDeviceActionPerformed

    private void txtClaimTimeoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClaimTimeoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClaimTimeoutActionPerformed

    private void scanDataHexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanDataHexActionPerformed
        txtScanDataLabel.setText(scanDataLabelHex);
    }//GEN-LAST:event_scanDataHexActionPerformed

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

    private void btnClearInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearInputActionPerformed
        txtScanData.setText("");
        txtScanDataLabel.setText("");
        txtScanDataType.setText("");      
        scanData = "";
        scanDataLabelText = "";
        scanDataLabelHex = "";

        commonMethods("clearInput");
    }//GEN-LAST:event_btnClearInputActionPerformed

    private void scanDataTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanDataTextActionPerformed
        txtScanDataLabel.setText(scanDataLabelText);
    }//GEN-LAST:event_scanDataTextActionPerformed

    private void txtScnHealthCheckTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtScnHealthCheckTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtScnHealthCheckTextActionPerformed

    private void txtSclPropertyValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSclPropertyValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSclPropertyValueActionPerformed

    private void txtScnStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtScnStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtScnStatusActionPerformed

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
            @Override
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
    private javax.swing.JButton btnSclPowerState;
    private javax.swing.JButton btnSclProperties;
    private javax.swing.JButton btnSclReadWeight;
    private javax.swing.JButton btnSclResetStat;
    private javax.swing.JButton btnSclRetreiveStat;
    private javax.swing.JButton btnSclZeroScale;
    private javax.swing.JButton btnScnCheckHealth;
    private javax.swing.JButton btnScnCheckHealthText;
    private javax.swing.JButton btnScnClear;
    private javax.swing.JButton btnScnClearInputProperties;
    private javax.swing.JButton btnScnCopy;
    private javax.swing.JButton btnScnExecute;
    private javax.swing.JButton btnScnPowerState;
    private javax.swing.JButton btnScnProperties;
    private javax.swing.JButton btnScnResetStat;
    private javax.swing.JButton btnScnRetreiveStat;
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
    private javax.swing.JCheckBox chkSclPowerNotify;
    private javax.swing.JCheckBox chkScnAutoDataEventEnable;
    private javax.swing.JCheckBox chkScnAutoDeviceEnable;
    private javax.swing.JCheckBox chkScnAutoDisable;
    private javax.swing.JCheckBox chkScnDataEventEnable;
    private javax.swing.JCheckBox chkScnDecodeData;
    private javax.swing.JCheckBox chkScnDeviceEnable;
    private javax.swing.JCheckBox chkScnFreezeEvents;
    private javax.swing.JCheckBox chkScnPowerNotify;
    private javax.swing.JComboBox cmbLogicalDevice;
    private javax.swing.JComboBox cmbSclCommand;
    private javax.swing.JComboBox cmbSclProperties;
    private javax.swing.JComboBox cmbScnCommand;
    private javax.swing.JComboBox cmbScnProperties;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
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
    private javax.swing.JLabel lblDeviceInfo;
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
    private javax.swing.JTextField lblSclStatistic;
    private javax.swing.JLabel lblSclStatus;
    private javax.swing.JLabel lblSclWeight;
    private javax.swing.JLabel lblScnCheckHealthText;
    private javax.swing.JLabel lblScnCommand;
    private javax.swing.JLabel lblScnInXml;
    private javax.swing.JLabel lblScnOutXml;
    private javax.swing.JLabel lblScnPropertyValue;
    private javax.swing.JTextField lblScnStatistic;
    private javax.swing.JLabel lblScnStatus;
    private javax.swing.JPanel panelLogView;
    private javax.swing.JPanel scalePanel;
    private javax.swing.JRadioButton scanDataHex;
    private javax.swing.JRadioButton scanDataText;
    private javax.swing.JPanel scannerPanel;
    private javax.swing.ButtonGroup sclCheckHealthBtnGrp;
    private javax.swing.JRadioButton sclExternal;
    private javax.swing.JRadioButton sclInteractive;
    private javax.swing.JRadioButton sclInternal;
    private javax.swing.JPanel sclPanelCheckHealth;
    private javax.swing.JPanel sclPanelDataEvent;
    private javax.swing.JPanel sclPanelDirectIO;
    private javax.swing.JPanel sclPanelLiveWeight;
    private javax.swing.JPanel sclPanelProperties;
    private javax.swing.JPanel sclPanelScaleWeight;
    private javax.swing.JPanel sclPanelStat;
    private javax.swing.JRadioButton scnExternalCH;
    private javax.swing.JRadioButton scnInteractiveCH;
    private javax.swing.JRadioButton scnInternalCH;
    private javax.swing.JPanel scnPanelCheckHealth;
    private javax.swing.JPanel scnPanelCommonMethods;
    private javax.swing.JPanel scnPanelDataEvent;
    private javax.swing.JPanel scnPanelDirectIO;
    private javax.swing.JPanel scnPanelProperties;
    private javax.swing.JPanel scnPanelRecData;
    private javax.swing.JPanel scnPanelStat;
    private javax.swing.JTextField txtClaimTimeout;
    private javax.swing.JTextArea txtDeviceInfo;
    private javax.swing.JTextArea txtLogView;
    private javax.swing.JTextArea txtLogicalInfo;
    private javax.swing.JTextArea txtScanData;
    private javax.swing.JTextArea txtScanDataLabel;
    private javax.swing.JTextField txtScanDataType;
    private javax.swing.JScrollPane txtScannerInfoScroll;
    private javax.swing.JTextField txtSclCheckHealthText;
    private javax.swing.JTextField txtSclDisplayWeight;
    private javax.swing.JTextArea txtSclInXml;
    private javax.swing.JTextField txtSclLiveWeight;
    private javax.swing.JTextArea txtSclOutXml;
    private javax.swing.JTextField txtSclPowerState;
    private javax.swing.JTextField txtSclPropertyValue;
    private javax.swing.JTextField txtSclRWTimeout;
    private javax.swing.JTextArea txtSclStatOutput;
    private javax.swing.JTextField txtSclStatus;
    private javax.swing.JTextField txtScnHealthCheckText;
    private javax.swing.JTextArea txtScnInxml;
    private javax.swing.JTextArea txtScnOutXml;
    private javax.swing.JTextField txtScnPowerState;
    private javax.swing.JTextField txtScnPropertyValue;
    private javax.swing.JTextArea txtScnStatOutput;
    private javax.swing.JTextField txtScnStatus;
    // End of variables declaration//GEN-END:variables

    public void updateScannerGUI() throws JposException {

        if (autoDataEventEnableScanner) {
            dataEventEnabled = true;
            commonMethods("enableDataEvent");
            if(null != unUsedScannerTab) unUsedScannerTab = null;   
        } else {
            chkScnDataEventEnable.setSelected(false);
        }

        if (chkScnAutoDisable.isSelected()) {
            if (chkScnAutoDeviceEnable.isSelected()) {
                chkScnDeviceEnable.setSelected(true);
                commonMethods("enableDevice");
            } else {
                ScannerDeviceTypeBinder deviceTypeBinder = new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));
                boolean scnDeviceEnabled = intermediateLayer.checkDeviceEnable(deviceTypeBinder);
                chkScnDeviceEnable.setSelected(scnDeviceEnabled);
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

    }

    public void updateScaleGUI() throws JposException {
        scaleMethods("weightOnAsyncMode");

        float dWeight = ((float) weightAM) / 1000;
        NumberFormat formatter = new DecimalFormat("#0.000");
        txtSclDisplayWeight.setText(formatter.format(dWeight) + "  " + units);
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
            } 
        }
    }

    public void updateScaleUIAfterErrorEvent() throws JposException {
        txtSclDisplayWeight.setText("0.000 " + units);
        if (autoDataEventEnableScale) {
            dataEventEnabled = true;
            commonMethods("enableDataEvent");
        } else {
            chkSclDataEventEnable.setSelected(true);
        }
    }

    @Override
    public void dataOccurred(DataEvent de) { 
        ScannerDeviceTypeBinder deviceTypeBinder = null;
        if(scannersList.get(JposSampleApp.scannerTabName) != de.getSource()){
            if (chkScnAutoDataEventEnable.isSelected()) unUsedScannerTab = (Scanner) de.getSource();   
            deviceTypeBinder = new ScannerDeviceTypeBinder((Scanner) de.getSource());         
        }
        else{
            deviceTypeBinder = new ScannerDeviceTypeBinder(scannersList.get(JposSampleApp.scannerTabName));  
        }
        
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
        ScaleDeviceTypeBinder deviceTypeBinder = new ScaleDeviceTypeBinder(JposSampleApp.scale);
        String response = intermediateLayer.statusUpdateListenerEvent(deviceTypeBinder, e);
        txtSclLiveWeight.setText(String.valueOf(response));
    }

    @Override
    public void errorOccurred(ErrorEvent ee) {
        System.out.println("Scale error event recieved. Error code : " + ee.getErrorCode());
        txtSclDisplayWeight.setText("Error Occurred. Error Code: " + ee.getErrorCode());

        errorStatus[0] = "Scale error event recieved. Error code : " + " " + ee.getErrorCode();
        commonMethods("errorOccured");
    }
        
    private void addRadioButtonKeyListeners() {
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                handleRadioButtonKeyPressed(evt);
            }
        };
        if (isScannerAvailable) {
            scnInternalCH.addKeyListener(keyAdapter);
            scnExternalCH.addKeyListener(keyAdapter);
            scnInteractiveCH.addKeyListener(keyAdapter);
        } else if (isScaleAvailable) {
            sclInternal.addKeyListener(keyAdapter);
            sclExternal.addKeyListener(keyAdapter);
            sclInteractive.addKeyListener(keyAdapter);
        }
    }

    private void handleRadioButtonKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            ++selectedIndex;
            if (selectedIndex > 2) {
                selectedIndex = 0;
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            --selectedIndex;
            if (selectedIndex < 0) {
                selectedIndex = 2;
            }
        }
        switch (selectedIndex) {
            case 0:
                healthCheckType = JposConst.JPOS_CH_INTERNAL;
                break;
            case 1:
                healthCheckType = JposConst.JPOS_CH_EXTERNAL;
                break;
            case 2:
                healthCheckType = JposConst.JPOS_CH_INTERACTIVE;
                break;
        }
    }
   }
