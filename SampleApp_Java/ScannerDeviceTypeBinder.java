package JposTest.src;

import jpos.JposException;
import jpos.Scanner;

@FunctionalInterface
interface CmdDecodeData {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface CmdClearInputProperties {

    void Set() throws JposException;
}

@FunctionalInterface
interface CmdScanData {

    byte[] Get() throws JposException;
}

@FunctionalInterface
interface CmdScanDataLabel {

    byte[] Get() throws JposException;
}

@FunctionalInterface
interface CmdScanDataType {

    int Get() throws JposException;
}

@FunctionalInterface
interface CmdDataCount {

    int Get() throws JposException;
}

/**
 * This class binds scanner related methods.
 *
 * @author CS7291
 */
public class ScannerDeviceTypeBinder extends DeviceTypeBinder {

    private final CmdDecodeData decodeDataCommand;
    private final CmdClearInputProperties clearInputPropertiesCommand;
    private final CmdScanData scanDataCommand;
    private final CmdScanDataLabel scanDataLabelCommand;
    private final CmdScanDataType scanDataTypeCommand;
    private final CmdDataCount dataCountCommand;

    /**
     * Constructor creates instances of device categories bind with their
     * methods
     *
     * @param scanner : selected control object
     */
    public ScannerDeviceTypeBinder( Scanner scanner) {

        super.deviceCategory = "Scanner";
        super.openCommand = scanner::open;
        super.claimCommand = scanner::claim;
        super.releaseCommand = scanner::release;
        super.closeCommand = scanner::close;
        super.dEnableCommand = scanner::setDeviceEnabled;
        super.dataEventCommand = scanner::setDataEventEnabled;
        super.autoDisableCommand = scanner::setAutoDisable;
        super.freezeEventsComand = scanner::setFreezeEvents;
        super.healthCheckCommand = scanner::checkHealth;
        super.healthCheckTextCommand = scanner::getCheckHealthText;
        this.decodeDataCommand = scanner::setDecodeData;
        this.clearInputPropertiesCommand = scanner::clearInputProperties;
        this.scanDataCommand = scanner::getScanData;
        this.scanDataLabelCommand = scanner::getScanDataLabel;
        this.scanDataTypeCommand = scanner::getScanDataType;
        this.dataCountCommand = scanner::getDataCount;
        super.directIOCommand = scanner::directIO;
        super.clearInputCommand = scanner::clearInput;
        
        super.controlVersionCommand = scanner::getDeviceControlVersion;
        super.serviceVersionCommand=scanner::getDeviceServiceVersion;
        super.controlDescriptionCommand = scanner::getDeviceControlDescription;
        super.serviceDescriptionCommand = scanner::getDeviceServiceDescription;
        super.physicalDeviceDescriptionCommand = scanner::getPhysicalDeviceDescription;
        super.physicalDeviceNameCommand = scanner::getPhysicalDeviceName;
    }

    /**
     * This method is used to enable or disable Decode Data Event. Requires a
     * boolean value.
     *
     * @param value : enables decode data event if true, disabled decode data
     * event if false
     * @throws JposException
     */
    public void setDecodeData(boolean value) throws JposException {
        decodeDataCommand.Set(value);

    }

    /**
     * This method is used to set all property values to their default values.
     *
     * @throws JposException
     */
    public void setClearInputProperties() throws JposException {
        clearInputPropertiesCommand.Set();

    }

    /**
     * Returns a byte array. This method is used to get the scanned raw data
     *
     * @return : scanned data
     * @throws JposException
     */
    public byte[] getScanData() throws JposException {
        return scanDataCommand.Get();
    }

    /**
     * Returns the scanned data in text form. Returns a byte array.
     *
     * @return : scan data label
     * @throws JposException
     */
    public byte[] getScanDataLabel() throws JposException {
        return scanDataLabelCommand.Get();
    }

    /**
     * This method is used to get the scanned data type. Returns an Integer
     * which can be mapped into a bar code type.
     *
     * @return : Integer value which indicates the scanned data type
     * @throws JposException
     */
    public int getScanDataType() throws JposException {
        return scanDataTypeCommand.Get();
    }

    /**
     * This method is used to get the number of scanned elements in the queue.
     *
     * @return : count in the queue
     * @throws JposException
     */
    public int getDataCount() throws JposException {
        return dataCountCommand.Get();
    }

    @Override
    public String toString() {
        return "Scanner";
    }
}
