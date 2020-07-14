package JposTest.src;

import jpos.JposException;

@FunctionalInterface
interface CmdOpen {

    void Set(String value) throws JposException;
}

@FunctionalInterface
interface CmdClaim {

    void Set(int value) throws JposException;
}

@FunctionalInterface
interface CmdRelease {

    void Set() throws JposException;
}

@FunctionalInterface
interface CmdClose {

    void Set() throws JposException;
}

@FunctionalInterface
interface CmdDeviceEnable {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface CmdDataEventEnable {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface CmdAutoDisable {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface CmdFreezeEvents {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface CmdHealthCheck {

    void Set(int value) throws JposException;
}

@FunctionalInterface
interface CmdHCText {

    String Get() throws JposException;
}

@FunctionalInterface
interface CmdClearInput {

    void Set() throws JposException;
}

@FunctionalInterface
interface CmdInputOutputC {

    void Set(int value, int[] data, Object object) throws JposException;
}

@FunctionalInterface
interface CmdControlVersion {

    int Get() throws JposException;
}

@FunctionalInterface
interface CmdServiceVersion {

    int Get() throws JposException;
}

@FunctionalInterface
interface CmdControlDescription {

    String Get() throws JposException;
}

@FunctionalInterface
interface CmdServiceDescription {

    String Get() throws JposException;
}

@FunctionalInterface
interface CmdPhysicalDeviceName {

    String Get() throws JposException;
}

@FunctionalInterface
interface CmdPhysicalDeviceDescription {

    String Get() throws JposException;
}

/**
 * This class binds common methods in scanner and scale devices.
 *
 * @author CS7291
 */
public class DeviceTypeBinder {

    public String deviceCategory;
    public CmdOpen openCommand;
    public CmdClaim claimCommand;
    public CmdRelease releaseCommand;
    public CmdClose closeCommand;
    public CmdDeviceEnable dEnableCommand;
    public CmdDataEventEnable dataEventCommand;
    public CmdFreezeEvents freezeEventsComand;
    public CmdHealthCheck healthCheckCommand;
    public CmdHCText healthCheckTextCommand;
    public CmdClearInput clearInputCommand;
    public CmdInputOutputC directIOCommand;
    public CmdAutoDisable autoDisableCommand;

    public CmdControlVersion controlVersionCommand;
    public CmdServiceVersion serviceVersionCommand;
    public CmdControlDescription controlDescriptionCommand;
    public CmdServiceDescription serviceDescriptionCommand;
    public CmdPhysicalDeviceDescription physicalDeviceDescriptionCommand;
    public CmdPhysicalDeviceName physicalDeviceNameCommand;

    /**
     * Return a String value (Scanner or Scale)
     *
     * @return device type selected
     */
    public String getDevice() {
        return deviceCategory;
    }

    /**
     * This method is used to open the control on the selected logical device
     *
     * @param value : name of the logical device selected
     * @throws JposException
     */
    public void setOpen(String value) throws JposException {
        openCommand.Set(value);
    }

    /**
     * This method is used to claim the opened logical device
     *
     * @param value : (Integer)Timeout value
     * @throws JposException
     */
    public void setClaim(int value) throws JposException {
        claimCommand.Set(value);
    }

    /**
     * This method is used to release the logical device which has opened and
     * claimed.
     *
     * @throws JposException
     */
    public void setRelease() throws JposException {
        releaseCommand.Set();
    }

    /**
     * This method is used to close the service opened on the selected logical
     * device.
     *
     * @throws JposException
     */
    public void setClose() throws JposException {
        closeCommand.Set();
    }

    /**
     * This method is used to enable or disable the logical device which has to
     * be opened and claimed. Requires a boolean value.
     *
     * @param value : enable the device if true, disable if false
     * @throws JposException
     */
    public void setDeviceEnable(boolean value) throws JposException {
        dEnableCommand.Set(value);
    }

    /**
     * This method is used to enable or disable data event. Requires a boolean
     * value.
     *
     * @param value : enable data event if true, disable if false
     * @throws JposException
     */
    public void setDataEventEnable(boolean value) throws JposException {
        dataEventCommand.Set(value);
    }

    public void setAutoDisable(boolean value) throws JposException {
        autoDisableCommand.Set(value);
    }

    /**
     * This method is used to enable or disable freeze events.
     *
     * @param value : Boolean value. set freeze events true if true, false
     * otherwise
     * @throws JposException
     */
    public void setFreezeEvents(boolean value) throws JposException {
        freezeEventsComand.Set(value);
    }

    /**
     * This method is used to enable the health check. Requires an Integer.
     *
     * @param value : health check type
     * @throws JposException
     */
    public void setHealthCheck(int value) throws JposException {
        healthCheckCommand.Set(value);
    }

    /**
     * Returns the health check text after performing the health check method.
     *
     * @return : health check text
     * @throws JposException
     */
    public String getHealthCheckText() throws JposException {
        return healthCheckTextCommand.Get();
    }

    /**
     * This method is used to clear all input data.
     *
     * @throws JposException
     */
    public void setClearInput() throws JposException {
        clearInputCommand.Set();
    }

    public int getControlVersion() throws JposException {
        return controlVersionCommand.Get();
    }

    public int getServiceVersion() throws JposException {
        return serviceVersionCommand.Get();
    }

    public String getControlDescription() throws JposException {
        return controlDescriptionCommand.Get();
    }

    public String getServiceDescription() throws JposException {
        return serviceDescriptionCommand.Get();
    }

    public String getPhysicalDeviceName() throws JposException {
        return physicalDeviceNameCommand.Get();
    }

    public String getPhysicalDeviceDescription() throws JposException {
        return physicalDeviceDescriptionCommand.Get();
    }

    /**
     * This method is used to perform direct input output commands
     *
     * @param value : opCode of the command
     * @param data : status value
     * @param object : contains InXml and OutXml values
     * @throws JposException
     */
    public void setDirectIO(int value, int[] data, Object object) throws JposException {
        directIOCommand.Set(value, data, object);
    }

}
