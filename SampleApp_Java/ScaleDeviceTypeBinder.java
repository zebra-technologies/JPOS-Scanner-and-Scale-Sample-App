package JposTest.src;

import jpos.JposException;
import jpos.Scale;

@FunctionalInterface
interface CmdLiveWeight {

    int Get() throws JposException;
}

@FunctionalInterface
interface CmdStatusNotify {

    void Set(int value) throws JposException;
}

@FunctionalInterface
interface CmdAsyncMode {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface CmdWeightUnit {

    int Get() throws JposException;
}

@FunctionalInterface
interface CmdReadWeight {

    void Get(int[] readValue, int value) throws JposException;
}

@FunctionalInterface
interface CmdZeroScale {

    void Set() throws JposException;
}

/**
 * This class binds scale related methods.
 *
 * @author CS7291
 */
public class ScaleDeviceTypeBinder extends DeviceTypeBinder {

    private final CmdLiveWeight liveWeightCommand;
    private final CmdStatusNotify statusNotifyCommand;
    private final CmdAsyncMode asyncModeCommand;
    private final CmdWeightUnit weightUnitCommand;
    private final CmdReadWeight readWeightCommand;
    private final CmdZeroScale zeroScaleCommand;

    /**
     * Constructor creates instances of device categories bind with their
     * methods
     *
     * @param scale : selected control object
     */
    public ScaleDeviceTypeBinder(Scale scale) {

        super.deviceCategory = "Scale  ";
        super.openCommand = scale::open;
        super.claimCommand = scale::claim;
        super.releaseCommand = scale::release;
        super.closeCommand = scale::close;
        super.dEnableCommand = scale::setDeviceEnabled;
        super.getDeviceEnableCommand = scale::getDeviceEnabled;
        super.dataEventCommand = scale::setDataEventEnabled;
        super.autoDisableCommand = scale::setAutoDisable;
        super.freezeEventsComand = scale::setFreezeEvents;
        super.healthCheckCommand = scale::checkHealth;
        super.healthCheckTextCommand = scale::getCheckHealthText;
        this.liveWeightCommand = scale::getScaleLiveWeight;
        this.statusNotifyCommand = scale::setStatusNotify;
        this.asyncModeCommand = scale::setAsyncMode;
        this.weightUnitCommand = scale::getWeightUnit;
        this.readWeightCommand = scale::readWeight;
        this.zeroScaleCommand = scale::zeroScale;
        super.directIOCommand = scale::directIO;
        super.clearInputCommand = scale::clearInput;

        super.controlVersionCommand = scale::getDeviceControlVersion;
        super.serviceVersionCommand = scale::getDeviceServiceVersion;
        super.controlDescriptionCommand = scale::getDeviceControlDescription;
        super.serviceDescriptionCommand = scale::getDeviceServiceDescription;
        super.physicalDeviceDescriptionCommand = scale::getPhysicalDeviceDescription;
        super.physicalDeviceNameCommand = scale::getPhysicalDeviceName;
        
        super.retrieveStatCommand = scale::retrieveStatistics;
        super.resetStatisticsCommand = scale::resetStatistics;
        super.powerNotifyCommand = scale::setPowerNotify;
        super.powerStateCommand = scale::getPowerState;
    }

    /**
     * This method is used to get the live weight measured.
     *
     * @return : live weight
     * @throws JposException
     */
    public int getLiveWeight() throws JposException {
        return liveWeightCommand.Get();
    }

    /**
     * This method is used to enable or disable status notify property.
     *
     * @param value : enable statusNotify if true, disable if false
     * @throws JposException
     */
    public void setStatusNotify(int value) throws JposException {
        statusNotifyCommand.Set(value);
    }

    /**
     * This method is used to set Asynchronous mode enabled or disabled.
     *
     * @param value : enables if true, disables if false
     * @throws JposException
     */
    public void setAsyncMode(boolean value) throws JposException {
        asyncModeCommand.Set(value);
    }

    /**
     * This method is used to get the units of the read weight. Returns an
     * Integer.
     *
     * @return : Integer value. Indicates a specific unit type.
     * @throws JposException
     */
    public int getWeightUnits() throws JposException {
        return weightUnitCommand.Get();
    }

    /**
     * This method is used to get the weight measured by the device.
     *
     * @param readValue : read weight
     * @param value : timeout value
     * @throws JposException
     */
    public void getReadWeight(int[] readValue, int value) throws JposException {
        readWeightCommand.Get(readValue, value);
    }

    /**
     * This method is used to set the scale to zero.
     *
     * @throws JposException
     */
    public void setZeroScale() throws JposException {
        zeroScaleCommand.Set();
    }

    @Override
    public String toString() {
        return "Scale";
    }
}
