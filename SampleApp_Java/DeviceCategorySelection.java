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

        propertiesList.add(new PropertyBinder("PIDX_AutoDisable", "boolean", JposSampleApp.scanner::getAutoDisable, JposSampleApp.scanner::setAutoDisable, true));
        propertiesList.add(new PropertyBinder("PIDX_CapCompareFirmwareVersion", "boolean", JposSampleApp.scanner::getCapCompareFirmwareVersion, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapPowerReporting", "int", JposSampleApp.scanner::getCapPowerReporting, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapStatisticsReporting", "boolean", JposSampleApp.scanner::getCapStatisticsReporting, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CapUpdateFirmware", "boolean", JposSampleApp.scanner::getCapUpdateFirmware, null, false));

        propertiesList.add(new PropertyBinder("PIDX_CapUpdateStatistics", "boolean", JposSampleApp.scanner::getCapUpdateStatistics, null, false));
        propertiesList.add(new PropertyBinder("PIDX_CheckHealthText", "string", JposSampleApp.scanner::getCheckHealthText, null, false));
        propertiesList.add(new PropertyBinder("PIDX_Claimed", "boolean", JposSampleApp.scanner::getClaimed, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DataCount", "int", JposSampleApp.scanner::getDataCount, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DataEventEnabled", "boolean", JposSampleApp.scanner::getDataEventEnabled, JposSampleApp.scanner::setDataEventEnabled, true));

        propertiesList.add(new PropertyBinder("PIDX_DeviceControlDescription", "string", JposSampleApp.scanner::getDeviceControlDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceControlVersion", "int", JposSampleApp.scanner::getDeviceControlVersion, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceEnabled", "boolean", JposSampleApp.scanner::getDeviceEnabled, JposSampleApp.scanner::setDeviceEnabled, true));
        propertiesList.add(new PropertyBinder("PIDX_DeviceServiceDescription", "string", JposSampleApp.scanner::getDeviceServiceDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_DeviceServiceVersion", "int", JposSampleApp.scanner::getDeviceServiceVersion, null, false));

        propertiesList.add(new PropertyBinder("PIDX_FreezeEvents", "boolean", JposSampleApp.scanner::getFreezeEvents, JposSampleApp.scanner::setFreezeEvents, true));
        propertiesList.add(new PropertyBinder("PIDX_PowerNotify", "int", JposSampleApp.scanner::getPowerNotify, JposSampleApp.scanner::setPowerNotify, true));              
        propertiesList.add(new PropertyBinder("PIDX_PowerState", "int", JposSampleApp.scanner::getPowerState, null, false));
        propertiesList.add(new PropertyBinder("PIDX_PhysicalDeviceDescription", "string", JposSampleApp.scanner::getPhysicalDeviceDescription, null, false));
        propertiesList.add(new PropertyBinder("PIDX_PhysicalDeviceName", "string", JposSampleApp.scanner::getPhysicalDeviceName, null, false));

        propertiesList.add(new PropertyBinder("PIDX_State", "int", JposSampleApp.scanner::getState, null, false));
        propertiesList.add(new PropertyBinder("PIDXScan_DecodeData", "boolean", JposSampleApp.scanner::getDecodeData, JposSampleApp.scanner::setDecodeData, true));
        propertiesList.add(new PropertyBinder("PIDXScan_ScanData", "byte", JposSampleApp.scanner::getScanData, null, false));
        propertiesList.add(new PropertyBinder("PIDXScan_ScanDataLabel", "byte", JposSampleApp.scanner::getScanDataLabel, null, false));
        propertiesList.add(new PropertyBinder("PIDXScan_ScanDataType", "int", JposSampleApp.scanner::getScanDataType, null, false));

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

        String[] command = new String[]{"GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_GETNEXT", "RSM_ATTR_SET", "RSM_ATTR_STORE", "DIO_NCR_SCANNER_NOF", "DIO_NCR_SCANNER_TONE", "DIO_SCANNER_NOT_ON_FILE", "DIO_SCANNER_DIO_NOF"};
        String[] inXml = new String[]{"", attribGetAllInXml, attribGetInXml, attribGetInXml, attribSetInXml, attribSetInXml, "", "", "", ""};
        int[] opCode = new int[]{DirectIOCommand.GET_SCANNERS, DirectIOCommand.RSM_ATTR_GETALL, DirectIOCommand.RSM_ATTR_GET, DirectIOCommand.RSM_ATTR_GETNEXT, DirectIOCommand.RSM_ATTR_SET, DirectIOCommand.RSM_ATTR_STORE, DirectIOCommand.DIO_NCR_SCANNER_NOF, DirectIOCommand.DIO_NCR_SCAN_TONE, DirectIOCommand.DIO_SCANNER_NOT_ON_FILE, DirectIOCommand.DIO_SCANNER_DIO_NOF};

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

        String[] command = new String[]{"GET_SCANNERS", "RSM_ATTR_GETALL", "RSM_ATTR_GET", "RSM_ATTR_GETNEXT", "RSM_ATTR_SET", "RSM_ATTR_STORE","NCR_DIO_SCAL_LIVE_WEIGHT"};
        String[] inXml = new String[]{"", attribGetAllInXml, attribGetInXml, attribGetInXml, attribSetInXml, attribSetInXml,""};
        int[] opCode = new int[]{DirectIOCommand.GET_SCANNERS, DirectIOCommand.RSM_ATTR_GETALL, DirectIOCommand.RSM_ATTR_GET, DirectIOCommand.RSM_ATTR_GETNEXT, DirectIOCommand.RSM_ATTR_SET, DirectIOCommand.RSM_ATTR_STORE, com.zebra.jpos.serviceonscale.directio.DirectIOCommand.DIO_NCR_READ_WEIGHT_TIMEOUT};

        for (int i = 0; i < command.length; i++) {
            DirectIOBinding data = new DirectIOBinding(command[i], inXml[i], opCode[i]);
            cmd.add(data);
        }

        DefaultComboBoxModel model = new DefaultComboBoxModel(cmd.toArray());
        return model;

    }
}
