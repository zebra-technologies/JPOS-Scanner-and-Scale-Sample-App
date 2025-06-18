package JposTest.src;

import com.zebra.jpos.serviceonscanner.directio.DirectIOCommand;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import jpos.JposException;

/**
 * This class is used to create the arrayLists of device categories, scanner
 * properties, scale properties and direct Io commands. Bind the ArrayLists with
 * respective drop-down lists in the User Interface.
 *
 * @author CS7291
 */
public class DeviceCategorySelection {

    //InXml - for scanner and scale
    private final String attribGetAllInXml = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + "</inArgs>";

    private final String attribGetInXml = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + " <cmdArgs>\n"
            + "  <arg-xml>\n"
            + "   <attrib_list>1</attrib_list>\n"
            + "  </arg-xml>\n"
            + " </cmdArgs>\n"
            + "</inArgs>";

    public final String attribSetInXml = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + " <cmdArgs>\n"
            + "  <arg-xml>\n"
            + "   <attrib_list>\n"
            + "    <attribute>\n"
            + "      <id>1</id>\n"
            + "      <datatype>F</datatype>\n"
            + "      <value>True</value>\n"
            + "    </attribute>\n"
            + "   </attrib_list>\n"
            + "  </arg-xml>\n"
            + " </cmdArgs>\n"
            + "</inArgs>";
    
    private final String attribGetNcrDirAccessInXml = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + " <opcode>5000</opcode>\n"
            + " <cmdArgs>\n"
            + "  <arg-xml>\n"
            + "   <attrib_list>1</attrib_list>\n"
            + "  </arg-xml>\n"
            + " </cmdArgs>\n"
            + "</inArgs>";
    
    private final String getPowerCycleStats = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + "</inArgs>";
    
    private final String retriveCradleContactState = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + "</inArgs>";
    
    private final String rtaSupportedGetInXml  = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + "</inArgs>";
    
    private final String rtaRegisterInXml  = "<inArgs>\n" 
            +"  <scannerID>1</scannerID>\n" 
            +"  <cmdArgs>\n" 
            +"        <arg-xml>\n" 
            +"            <rtaevent_list>\n" 
            +"                <rtaevent>\n" 
            +"                    <id>38004</id>\n" 
            +"                    <stat>7</stat>\n" 
            +"                    <onlimit>Not set</onlimit>\n" 
            +"                    <offlimit>Not applicable</offlimit>\n" 
            +"                </rtaevent>\n" 
            +"                 <rtaevent>\n" 
            +"                    <id>38001</id>\n" 
            +"                    <stat>7</stat>\n" 
            +"                    <onlimit>Not set</onlimit>\n" 
            +"                    <offlimit>Not applicable</offlimit>\n" 
            +"                </rtaevent>\n" 
            +"            </rtaevent_list>\n" 
            +"        </arg-xml>\n" 
            +"    </cmdArgs>\n" 
            +"</inArgs>";
    
    private final String rtaUnregisterInXml ="<inArgs>\n" 
            +"    <scannerID>1</scannerID>\n" 
            +"    <cmdArgs>\n" 
            +"        <arg-xml>\n" 
            +"            <rtaevent_list>\n" 
            +"                <rtaevent>\n" 
            +"                    <id>30012</id>\n" 
            +"                    <stat>7</stat>\n" 
            +"                </rtaevent>\n" 
            +"                <rtaevent>\n" 
            +"                    <id>616</id>\n" 
            +"                    <stat>2</stat>\n" 
            +"                </rtaevent>\n" 
            +"            </rtaevent_list>\n" 
            +"        </arg-xml>\n" 
            +"    </cmdArgs>\n" 
            +"</inArgs>";
    
    private final String rtaAlertStatusGetInXml = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + "</inArgs>";
    
    private final String rtaAlertStatusSetInXml  =  "<inArgs>\n" 
            +"    <scannerID>1</scannerID>\n" 
            +"    <cmdArgs>\n" 
            +"        <arg-xml>\n" 
            +"            <rtaevent_list>\n" 
            +"                <rtaevent>\n" 
            +"                    <id>30012</id>\n" 
            +"                    <stat>7</stat>\n" 
            +"                    <reported>0</reported>\n"
            +"                </rtaevent>\n" 
            +"                <rtaevent>\n" 
            +"                    <id>616</id>\n" 
            +"                    <stat>2</stat>\n" 
            +"                    <reported>0</reported>\n"
            +"                </rtaevent>\n" 
            +"            </rtaevent_list>\n" 
            +"        </arg-xml>\n" 
            +"    </cmdArgs>\n" 
            +"</inArgs>";
    
    private final String rtaSuspendInXml  =  "<inArgs>\n" 
            +"  <scannerID>1</scannerID>\n" 
            +"  <cmdArgs>\n" 
            +"    <arg-bool>false</arg-bool>\n" 
            +"  </cmdArgs>\n" 
            +"</inArgs>";
    
    private final String rtaStateInXml  = "<inArgs>\n"
            + " <scannerID>1</scannerID>\n"
            + "</inArgs>";
    
    private final String configurationPushInXml = "<inArgs>\n"
            +" <scannerID>1</scannerID>\n"
            +" <cmdArgs>\n"
            +"  <arg-string></arg-string>\n"
            +" </cmdArgs>\n"
            +"</inArgs>";
    
    private final String firmwarePushInXml = "<inArgs>\n"
            +" <scannerID>1</scannerID>\n"
            +" <cmdArgs>\n"
            +"  <arg-string></arg-string>\n"
            +"  <arg-int>2</arg-int>"
            +" </cmdArgs>\n"
            +"</inArgs>";
    
    //populate the device Category drop-down list 
    public DefaultComboBoxModel deviceCategory() throws JposException {
        List<Object> deviceList = new ArrayList<>();

        deviceList.add(new ScannerDeviceTypeBinder(JposSampleApp.scanner));
        deviceList.add(new ScaleDeviceTypeBinder(JposSampleApp.scale));

        DefaultComboBoxModel model = new DefaultComboBoxModel(deviceList.toArray());
        return model;
    }

    //bind setProperty, getProperty methods of each scanner property
    public DefaultComboBoxModel scannerProperty() throws JposException {

        List<Object> propertiesList = new ArrayList<>();

        propertiesList.add(new PropertyBinder("PIDX_AutoDisable", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getAutoDisable, JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::setAutoDisable, true));
        propertiesList.add(new PropertyBinder("PIDX_CapCompareFirmwareVersion", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getCapCompareFirmwareVersion, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapPowerReporting", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getCapPowerReporting, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapStatisticsReporting", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getCapStatisticsReporting, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapUpdateFirmware", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getCapUpdateFirmware, null, false));

        propertiesList.add(new PropertyBinder("PIDX_CapUpdateStatistics", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getCapUpdateStatistics, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CheckHealthText", "string", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getCheckHealthText, null, false));
        propertiesList.add(new PropertyBinder("PIDX_Claimed", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getClaimed, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DataCount", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDataCount, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DataEventEnabled", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDataEventEnabled, JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::setDataEventEnabled, true));

        propertiesList.add(new PropertyBinder("PIDX_DeviceControlDescription", "string", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDeviceControlDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceControlVersion", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDeviceControlVersion, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceEnabled", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDeviceEnabled, JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::setDeviceEnabled, true));
        propertiesList.add(new PropertyBinder("PIDX_DeviceServiceDescription", "string", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDeviceServiceDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceServiceVersion", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDeviceServiceVersion, null, false));

        propertiesList.add(new PropertyBinder("PIDX_FreezeEvents", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getFreezeEvents, JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::setFreezeEvents, true));
        propertiesList.add(new PropertyBinder("PIDX_PowerNotify", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getPowerNotify, JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::setPowerNotify, true));              
        propertiesList.add(new PropertyBinder("PIDX_PowerState", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getPowerState, null, false));
        propertiesList.add(new PropertyBinder("PIDX_PhysicalDeviceDescription", "string", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getPhysicalDeviceDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_PhysicalDeviceName", "string", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getPhysicalDeviceName, null, false));

        propertiesList.add(new PropertyBinder("PIDX_State", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getState, null, false));
        propertiesList.add(new PropertyBinder("PIDXScan_DecodeData", "boolean", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getDecodeData, JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::setDecodeData, true));
        propertiesList.add(new PropertyBinder("PIDXScan_ScanData", "byte", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getScanData, null, false));
        propertiesList.add(new PropertyBinder("PIDXScan_ScanDataLabel", "byte", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getScanDataLabel, null, false));
        propertiesList.add(new PropertyBinder("PIDXScan_ScanDataType", "int", JposSampleApp.scannersList.get(JposSampleApp.scannerTabName)::getScanDataType, null, false));

        DefaultComboBoxModel model = new DefaultComboBoxModel(propertiesList.toArray());
        return model;

    }

    //bind setProperty, getProperty methods of each scale property
    public DefaultComboBoxModel scaleProperty() throws JposException {

        List<Object> propertiesList = new ArrayList<>();

        propertiesList.add(new PropertyBinder("PIDX_AutoDisable", "boolean", JposSampleApp.scale::getAutoDisable, JposSampleApp.scale::setAutoDisable, true));
        propertiesList.add(new PropertyBinder("PIDX_CapCompareFirmwareVersion", "boolean", JposSampleApp.scale::getCapCompareFirmwareVersion, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapPowerReporting", "int", JposSampleApp.scale::getCapPowerReporting, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapStatisticsReporting", "boolean", JposSampleApp.scale::getCapStatisticsReporting, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapUpdateFirmware", "boolean", JposSampleApp.scale::getCapUpdateFirmware, null, false));

        propertiesList.add(new PropertyBinder("PIDX_CapUpdateStatistics", "boolean", JposSampleApp.scale::getCapUpdateStatistics, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CheckHealthText", "string", JposSampleApp.scale::getCheckHealthText, null, false));
        propertiesList.add(new PropertyBinder("PIDX_Claimed", "boolean", JposSampleApp.scale::getClaimed, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DataCount", "int", JposSampleApp.scale::getDataCount, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DataEventEnabled", "boolean", JposSampleApp.scale::getDataEventEnabled, JposSampleApp.scale::setDataEventEnabled, true));

        propertiesList.add(new PropertyBinder("PIDX_DeviceControlDescription", "string", JposSampleApp.scale::getDeviceControlDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceControlVersion", "int", JposSampleApp.scale::getDeviceControlVersion, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceEnabled", "boolean", JposSampleApp.scale::getDeviceEnabled, JposSampleApp.scale::setDeviceEnabled, true));
        propertiesList.add(new PropertyBinder("PIDX_DeviceServiceDescription", "string", JposSampleApp.scale::getDeviceServiceDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceServiceVersion", "int", JposSampleApp.scale::getDeviceServiceVersion, null, false));

        propertiesList.add(new PropertyBinder("PIDX_FreezeEvents", "boolean", JposSampleApp.scale::getFreezeEvents, JposSampleApp.scale::setFreezeEvents, true));
        propertiesList.add(new PropertyBinder("PIDX_PowerNotify", "int", JposSampleApp.scale::getPowerNotify, JposSampleApp.scale::setPowerNotify, true));          
        propertiesList.add(new PropertyBinder("PIDX_PowerState", "int", JposSampleApp.scale::getPowerState, null, false));
        propertiesList.add(new PropertyBinder("PIDX_PhysicalDeviceDescription", "string", JposSampleApp.scale::getPhysicalDeviceDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_PhysicalDeviceName", "string", JposSampleApp.scale::getPhysicalDeviceName, null, false));

        propertiesList.add(new PropertyBinder("PIDX_State", "int", JposSampleApp.scale::getState, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_MaxDisplayTextChars", "int", JposSampleApp.scale::getMaxDisplayTextChars, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_AsyncMode", "boolean", JposSampleApp.scale::getAsyncMode, JposSampleApp.scale::setAsyncMode, true));
        propertiesList.add(new PropertyBinder("PIDXScal_MaximumWeight", "int", JposSampleApp.scale::getMaximumWeight, null, false));

        propertiesList.add(new PropertyBinder("PIDXScal_ScaleLiveWeight", "int", JposSampleApp.scale::getScaleLiveWeight, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_EnableLiveWeight", "int", JposSampleApp.scale::getStatusNotify, JposSampleApp.scale::setStatusNotify, true));   
        propertiesList.add(new PropertyBinder("PIDXScal_TarWeight", "int", JposSampleApp.scale::getTareWeight, JposSampleApp.scale::setTareWeight, true));              
        propertiesList.add(new PropertyBinder("PIDXScal_UnitPrice",  JposSampleApp.scale::getUnitPrice, JposSampleApp.scale::setUnitPrice,"long", true));
        propertiesList.add(new PropertyBinder("PIDXScal_SalesPrice",  JposSampleApp.scale::getSalesPrice, null,"long", false));

        propertiesList.add(new PropertyBinder("PIDXScal_WeightUnit", "int", JposSampleApp.scale::getWeightUnit, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_ZeroValid", "boolean", JposSampleApp.scale::getZeroValid, JposSampleApp.scale::setZeroValid, true));
        propertiesList.add(new PropertyBinder("PIDXScal_CapDisplay", "boolean", JposSampleApp.scale::getCapDisplay, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_CapDisplayText", "boolean", JposSampleApp.scale::getCapDisplayText, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_CapPriceCalculating", "boolean", JposSampleApp.scale::getCapPriceCalculating, null, false));
        
        propertiesList.add(new PropertyBinder("PIDXScal_CapTareWeight", "boolean", JposSampleApp.scale::getCapTareWeight, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_CapZeroScale", "boolean", JposSampleApp.scale::getCapZeroScale, null, false));
        propertiesList.add(new PropertyBinder("PIDXScal_CapStatusUpdate", "boolean", JposSampleApp.scale::getCapStatusUpdate, null, false));

        DefaultComboBoxModel model = new DefaultComboBoxModel(propertiesList.toArray());
        return model;

    }

    //binds relevant InXml and opcode with each DirectIO Command
    //Scanner
    public DefaultComboBoxModel scnDirectIOCommand() {

        List<DirectIOBinding> cmd = new ArrayList<>();

        String[] command = new String[]{"GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_GETNEXT", "RSM_ATTR_SET", "RSM_ATTR_STORE", "DIO_NCR_SCANNER_NOF", "DIO_NCR_SCANNER_TONE", "DIO_SCANNER_NOT_ON_FILE", "DIO_SCANNER_DIO_NOF", "NCRDIO_SCAN_RESET", "NCRDIO_SCAN_STATUS", "NCRDIO_SCAN_DIRECT","RETRIEVE_CRADLE_CONTACT_STATE_OF_HEALTH","RTA_SUPPORTED_GET","RTA_REGISTER","RTA_UNREGISTER","RTA_ALERT_STATUS_GET","RTA_ALERT_STATUS_SET","RTA_SUSPEND","RTA_STATE","LOAD_CONFIGURATION","UPD_FW_PLUGIN", "UPD_FW_DAT"};
        String[] inXml = new String[]{"", attribGetAllInXml, attribGetInXml, attribGetInXml, attribSetInXml, attribSetInXml, "", "1001", "", "", "", "", attribGetNcrDirAccessInXml,retriveCradleContactState,rtaSupportedGetInXml,rtaRegisterInXml,rtaUnregisterInXml,rtaAlertStatusGetInXml,rtaAlertStatusSetInXml,rtaSuspendInXml,rtaStateInXml,configurationPushInXml, firmwarePushInXml, firmwarePushInXml};
        int[] opCode = new int[]{DirectIOCommand.GET_SCANNERS, DirectIOCommand.RSM_ATTR_GETALL, DirectIOCommand.RSM_ATTR_GET, DirectIOCommand.RSM_ATTR_GETNEXT, DirectIOCommand.RSM_ATTR_SET, DirectIOCommand.RSM_ATTR_STORE, DirectIOCommand.DIO_NCR_SCANNER_NOF, DirectIOCommand.DIO_NCR_SCAN_TONE, DirectIOCommand.DIO_SCANNER_NOT_ON_FILE, DirectIOCommand.DIO_SCANNER_DIO_NOF, DirectIOCommand.NCRDIO_SCAN_RESET, DirectIOCommand.NCRDIO_SCAN_STATUS, DirectIOCommand.NCRDIO_SCAN_DIRECT,DirectIOCommand.RETRIEVE_CRADLE_CONTACT_STATE_OF_HEALTH,DirectIOCommand.CMD_RTA_SUPPORTED_GET,DirectIOCommand.CMD_RTA_REGISTER,DirectIOCommand.CMD_RTA_UNREGISTER,DirectIOCommand.CMD_RTA_ALERT_STATUS_GET,DirectIOCommand.CMD_RTA_ALERT_STATUS_SET,DirectIOCommand.CMD_RTA_SUSPEND,DirectIOCommand.CMD_RTA_STATE, DirectIOCommand.CMD_LOAD_CONFIGURATION, DirectIOCommand.CMD_DEVICE_UPDATE_FIRMWARE_FROM_PLUGIN, DirectIOCommand.CMD_DEVICE_UPDATE_FIRMWARE_DAT};

        for (int i = 0; i < command.length; i++) {
            DirectIOBinding data = new DirectIOBinding(command[i], inXml[i], opCode[i]);
            cmd.add(data);
        }

        DefaultComboBoxModel model = new DefaultComboBoxModel(cmd.toArray());
        return model;

    }
    
    //binds relevant InXml and opcode with each DirectIO Command
    //Scale
    public DefaultComboBoxModel sclDirectIOCommand() {

        List<DirectIOBinding> cmd = new ArrayList<>();

        String[] command = new String[]{"GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_GETNEXT", "RSM_ATTR_SET", "RSM_ATTR_STORE","NCR_DIO_SCAL_LIVE_WEIGHT","NCRDIO_SCAL_STATUS", "NCRDIO_SCAL_DIRECT", "RTA_SUPPORTED_GET","RTA_REGISTER","RTA_UNREGISTER","RTA_ALERT_STATUS_GET","RTA_ALERT_STATUS_SET","RTA_SUSPEND","RTA_STATE", "LOAD_CONFIGURATION"};
        String[] inXml = new String[]{"", attribGetAllInXml, attribGetInXml, attribGetInXml, attribSetInXml, attribSetInXml,"","", attribGetNcrDirAccessInXml, rtaSupportedGetInXml, rtaRegisterInXml, rtaUnregisterInXml, rtaAlertStatusGetInXml, rtaAlertStatusSetInXml, rtaSuspendInXml, rtaStateInXml, configurationPushInXml};
        int[] opCode = new int[]{DirectIOCommand.GET_SCANNERS, DirectIOCommand.RSM_ATTR_GETALL, DirectIOCommand.RSM_ATTR_GET, DirectIOCommand.RSM_ATTR_GETNEXT, DirectIOCommand.RSM_ATTR_SET, DirectIOCommand.RSM_ATTR_STORE, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.NCR_DIO_SCAL_LIVE_WEIGHT, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.NCRDIO_SCAL_STATUS, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.NCRDIO_SCAL_DIRECT, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_SUPPORTED_GET, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_REGISTER, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_UNREGISTER, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_ALERT_STATUS_GET, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_ALERT_STATUS_SET, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_SUSPEND, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_RTA_STATE, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.CMD_LOAD_CONFIGURATION};

        for (int i = 0; i < command.length; i++) {
            DirectIOBinding data = new DirectIOBinding(command[i], inXml[i], opCode[i]);
            cmd.add(data);
        }

        DefaultComboBoxModel model = new DefaultComboBoxModel(cmd.toArray());
        return model;

    }
}
