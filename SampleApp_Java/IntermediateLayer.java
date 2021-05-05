package JposTest.src;

import static JposTest.src.JposSampleApp.*;
import com.zebra.jpos.serviceonscale.directio.DirectIOStatus;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jpos.JposException;
import jpos.ScaleConst;
import jpos.events.StatusUpdateEvent;

/**
 * This class contains all the methods performed by scanner scale service
 * objects.
 *
 * @author CS7291
 */
public class IntermediateLayer {

    /**
     * This method is called when a JposException triggers.
     *
     * @param je : JposException
     * @param errorMsg : Error message to be added in the dialog box
     * @return exception details: error message and error code
     */
    public String exceptionDialog(JposException je, String errorMsg) {
        String exceptionMsg = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode() + " ExtError: " + je.getErrorCodeExtended();
        JOptionPane.showMessageDialog(null, errorMsg + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        return exceptionMsg;
    }

    /**
     * This method is used to open the service on the selected logical device
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] openAction(DeviceTypeBinder device) {

        String[] status = new String[2];
        try {
            device.setOpen(logicalName);
            status[0] = "Device Opened";
        } catch (JposException je) {
            status[0] = "Device Not Opened";
            status[1] = exceptionDialog(je, "Failed to open \"" + logicalName + "\"\nException: ");
        }
        return status;
    }

    /**
     * This method is used to claim the opened logical device
     *
     * @param device : object created from DeviceTypeBinder class
     * @param claimTimeOut : timeout form claim
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] claimAction(DeviceTypeBinder device, int claimTimeOut) {
        String[] status = new String[2];
        try {
            device.setClaim(claimTimeOut);
            status[0] = "Device Claimed";
        } catch (JposException je) {
            status[0] = "Device Not Claimed";
            status[1] = exceptionDialog(je, "Failed to claim \"" + logicalName + "\"\nException: ");
        }
        return status;
    }

    /**
     * This method is used to release the logical device
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] releaseAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setRelease();
            status[0] = "Device Released";
        } catch (JposException je) {
            status[0] = "Device Not Released";
            status[1] = exceptionDialog(je, "Failed to release \"" + logicalName + "\"\nException: ");
        }
        return status;
    }

    /**
     * This method is used to close the service from logical device.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] closeAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setClose();
            status[0] = "Device Closed";
        } catch (JposException je) {
            status[0] = "Device Unable to Close";
            status[1] = exceptionDialog(je, "Failed to close \"" + logicalName + "\"\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform device enable and disable action. Requires
     * a boolean value
     *
     * @param device : object created from DeviceTypeBinder class
     * @param deviceEnabled : enables if true, disables if false
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] deviceEnableAction(DeviceTypeBinder device, boolean deviceEnabled) {
        String[] status = new String[2];
        try {
            device.setDeviceEnable(deviceEnabled);
            if (deviceEnabled) {
                status[0] = "Device Enabled";
                deviceEnableC = true;
            } else {
                status[0] = "Device Disabled";
                deviceEnableC = false;
            }
        } catch (JposException je) {
            deviceEnableC = !deviceEnabled;
            status[0] = "Device Disabled";
            status[1] = exceptionDialog(je, "Failed to enable device " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to enable or disable the data event on the logical
     * device. Requires a boolean value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param dataEventEnabled : enable data event is true
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] dataEventEnableAction(DeviceTypeBinder device, boolean dataEventEnabled) {
        String[] status = new String[2];
        try {
            device.setDataEventEnable(dataEventEnabled);
            if (dataEventEnabled) {
                status[0] = "Data Event Enabled";
                dataEventEnableC = true;
            } else {
                status[0] = "Data Event Disabled";
                dataEventEnableC = false;
            }
        } catch (JposException je) {
            dataEventEnableC = !dataEventEnabled;
            status[0] = "Data Event Disabled";
            status[1] = exceptionDialog(je, "Failed to enable data event " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to enable or disable the data event on the logical
     * device. Requires a boolean value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param autoDisable : enable device if true
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] autoDisable(DeviceTypeBinder device, boolean autoDisable) {
        String[] status = new String[2];
        try {
            device.setAutoDisable(autoDisable);
            if (autoDisable) {
                status[0] = "Auto Disable is true";
                autoDisableC = true;
            } else {
                status[0] = "Auto Disable is False";
                autoDisableC = false;
            }
        } catch (JposException je) {
            autoDisableC = !autoDisable;
            status[0] = "Auto Disable is False";
            status[1] = exceptionDialog(je, "Failed in auto device enable " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform the freeze events action. Requires a
     * boolean value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param freezeEventsEnabled : freeze events if true
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] freezeEventsAction(DeviceTypeBinder device, boolean freezeEventsEnabled) {
        String[] status = new String[2];
        try {
            device.setFreezeEvents(freezeEventsEnabled);
            if (freezeEventsEnabled) {
                status[0] = "Freeze Events Enabled";
                freezeEventsC = true;
            } else {
                status[0] = "Freeze Events Disabled";
                freezeEventsC = false;
            }
        } catch (JposException je) {
            freezeEventsC = !freezeEventsEnabled;
            status[0] = "Freeze Events Disabled";
            status[1] = exceptionDialog(je, "Failed to enable freeze events " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform the decode data action. Requires a boolean
     * value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param decodeDataEnabled : enable decode data event if true
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] decodeDataAction(ScannerDeviceTypeBinder device, boolean decodeDataEnabled) {
        String[] status = new String[2];
        try {
            device.setDecodeData(decodeDataEnabled);
            if (decodeDataEnabled) {
                status[0] = "Decode Data Enabled";
                decodeDataEnableC = true;
            } else {
                status[0] = "Decode Data Disabled";
                decodeDataEnableC = false;
            }
        } catch (JposException je) {
            decodeDataEnableC = false;
            status[0] = "Decode Data Disabled";
            status[1] = exceptionDialog(je, "Failed to enable decode data " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform check health action. Requires an Integer
     * value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param healthCheckType : type of the health check to be performed
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] checkHealthAction(DeviceTypeBinder device, int healthCheckType) {
        String[] status = new String[2];
        try {
            device.setHealthCheck(healthCheckType);
            status[0] = "Health Check Enabled";
        } catch (JposException je) {
            status[0] = "Health Check Disabled";
            status[1] = exceptionDialog(je, "Failed to enable health check " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to get the health check text on the health check type
     * performed.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] checkHealthTextAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            healthCheckText = device.getHealthCheckText();
            status[0] = "Health Check Text Enabled";
        } catch (JposException je) {
            status[0] = "Health Check Text Disabled";
            status[1] = exceptionDialog(je, "Failed to enable health check text " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to enable fast mode for Scanners.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] fastModeScannerAction(ScannerDeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setOpen(logicalName);
            device.setClaim(5000);
            device.setDeviceEnable(true);
            device.setDataEventEnable(true);
            device.setDecodeData(true);
            status[0] = "Fast Mode Enabled";
        } catch (JposException je) {
            fastModeScannerC = false;
            status[0] = "Fast Mode Disable";
            status[1] = exceptionDialog(je, "Failed to enable fast mode " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform fast mode in Scale.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] fastModeScaleAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setOpen(logicalName);
            device.setClaim(5000);
            device.setDeviceEnable(true);

            status[0] = "Fast Mode Enabled";
        } catch (JposException je) {
            fastModeScaleC = false;
            status[0] = "Fast Mode Disabled";
            status[1] = exceptionDialog(je, "Failed to enable fast mode " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to set property values into default.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] clearInputPropertiesAction(ScannerDeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setClearInputProperties();
            status[0] = "Set clear Input Properties true";
        } catch (JposException je) {
            status[0] = "Set clear Input Properties false";
            status[1] = exceptionDialog(je, "Failed to clear input properties " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to enable asynchronous mode in scale.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param value : enable if true, disable if false
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] asyncModeAction(ScaleDeviceTypeBinder device, boolean value) {
        String[] status = new String[2];
        try {
            device.setAsyncMode(value);
            if (value) {
                status[0] = "Async Mode Enabled";
                asyncModeC = true;
            } else {
                status[0] = "Async Mode Disabled";
                asyncModeC = false;
            }
        } catch (JposException je) {
            asyncModeC = false;
            status[0] = "Async Mode Disabled";
            status[1] = exceptionDialog(je, "Jpos exception in AsyncMode ");
        }
        return status;
    }

    /**
     * This method is used to perform status notify action.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param value : indicates whether to enable or disable the property.
     * (1-enable, 2- disable)
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] statusNotifyAction(ScaleDeviceTypeBinder device, int value) {
        String[] status = new String[2];
        try {
            device.setStatusNotify(value);
            if (value == 2) {
                status[0] = "Live Weight Enabled";
                statusNotifyC = true;
            } else {
                status[0] = "Live Weight Disabled";
                statusNotifyC = false;
            }
        } catch (JposException je) {
            status[0] = "Live Weight Disabled";
            statusNotifyC = false;
            status[1] = exceptionDialog(je, "Failed to enable live weight " + "\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform read weight action.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param timeOut : timeout value
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] readWeightAction(ScaleDeviceTypeBinder device, int timeOut) {
        String[] status = new String[2];
        try {
            readWeight = new int[1];
            device.getReadWeight(readWeight, timeOut);
            fWeight = ((float) readWeight[0]) / 1000;
            units = getUnit(device);

            status[0] = "Read Weight Performed";
        } catch (JposException je) {
            status[0] = "Read Weight Unsuccessful";
            status[1] = exceptionDialog(je, "Failed to read weight \"" + logicalName + "\"\nException: ");
        }
        return status;
    }

    /**
     * This method is used to perform the zero scale method.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] zeroScaleAction(ScaleDeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setZeroScale();
            status[0] = "Zero Scale Performed";
        } catch (JposException je) {
            status[0] = "Zero Scale Unsuccessful";
            status[1] = exceptionDialog(je, "Failed to zero \"" + logicalName + "\"\nException");
        }
        return status;
    }

    /**
     * This method is used to clear input data.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] clearInputAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setClearInput();
            status[0] = "Clear Input Data";
        } catch (JposException je) {
            status[0] = "No Input Data Cleared";
            status[1] = exceptionDialog(je, "Exception: ");
        }
        return status;
    }

    /**
     * This method is used to retrieve statistics.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param retrieveStatValue : String array defining the statistics to be
     * retrieved
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] retrieveStatistics(DeviceTypeBinder device, String[] retrieveStatValue) {
        String[] status = new String[2];
        try {
            device.retrieveStatistics(retrieveStatValue);
            status[0] = "Statistics Retrieved";

        } catch (JposException je) {
            status[0] = "Statistics Retrieval failed";
            status[1] = exceptionDialog(je, "Failed to Retrieve Statistics. Exception: ");
        }
        return status;
    }

    /**
     * This method is used to reset statistics
     *
     * @param device : object created from DeviceTypeBinder class
     * @param resetValue : String array defining statistics that are to be reset
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] resetStatistics(DeviceTypeBinder device, String resetValue) {
        String[] status = new String[2];
        try {
            device.resetStatistics(resetValue);
            status[0] = "Statistics Reset";
        } catch (JposException je) {
            status[0] = "Statistics Reset failed";
            status[1] = exceptionDialog(je, "Failed to Reset Statistics. Exception: ");
        }
        return status;
    }

    /**
     * This method is used to enable and disable power notifications
     *
     * @param device : object created from DeviceTypeBinder class
     * @param powerNotifyValue : indicates whether to enable or disable the
     * property. (1-enable, 2- disable)
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] PowerNotify(DeviceTypeBinder device, int powerNotifyValue) {
        String[] status = new String[2];
        try {
            device.setPowerNotify(powerNotifyValue);
            if (powerNotifyValue == 1) {
                status[0] = "Power Notifications Enabled";
                powerNotifyC = true;
            } else {
                status[0] = "Power Notifications Disabled";
                powerNotifyC = false;
            }
        } catch (JposException je) {
            status[0] = "Command Unsuccessful";
            powerNotifyC = (powerNotifyValue != 1);
            status[1] = exceptionDialog(je, "Exception: ");
        }
        return status;
    }

    /**
     * This method is used to get the power state of the device
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] PowerState(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            powerState = device.getPowerState();
            status[0] = "Power State Enabled";
        } catch (JposException je) {
            status[0] = "Unable to get Power State";
            status[1] = exceptionDialog(je, "Exception: ");
        }
        return status;
    }

    /**
     * This method returns a String. It contains the versions of the control
     * object and the service object, control and service descriptions, physical
     * device name and description
     *
     * @param device : object created from DeviceTypeBinder class
     * @return device information
     */
    public String getDeviceInfo(DeviceTypeBinder device) {
        String controlVersion;
        String serviceVersion;
        String controlDescription;
        String serviceDescription;
        String physicalDeviceName;
        String physicalDeviceDescription;
        String info;
        try {
            controlVersion = Integer.toString(device.getControlVersion() / 1000);
            serviceVersion = Integer.toString(device.getServiceVersion() / 1000);
            controlDescription = device.getControlDescription();
            serviceDescription = device.getServiceDescription();
            physicalDeviceName = device.getPhysicalDeviceName();
            physicalDeviceDescription = device.getPhysicalDeviceDescription();

        } catch (JposException je) {
            info = "" + je.getMessage();
            JOptionPane.showMessageDialog(null, "Exception " + je, "Failed", JOptionPane.ERROR_MESSAGE);
            return info;
        }
        String version = "Version :\n" + "Control Object : v" + controlVersion.replace("0", ".") + ".0" + "\nService Object : v" + serviceVersion.replace("0", ".") + ".0" + "\n\n";
        String deviceName = "Physical Device Name :\n" + physicalDeviceName + "\n\n" + "Physical Device Description :\n" + physicalDeviceDescription + "\n\n\n";
        String description = "Device Description :\n\n" + "Control Object Description:\n" + controlDescription + "\n\nService Object Description:\n" + serviceDescription;

        info = version.concat(deviceName);
        info = info.concat(description);
        return info;
    }

    /**
     * This method is used to perform Direct Input Output action.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param opCode : opCode of the Direct IO command
     * @param csStatus : status of the command performed
     * @param deviceParams : InXml and OutXml values
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] directIOAction(DeviceTypeBinder device, int opCode, int[] csStatus, Object deviceParams) {
        String[] status = new String[2];
        try {
            device.setDirectIO(opCode, csStatus, (Object) deviceParams);
            status[0] = "Direct IO Successful";
            directIOC = true;
            if (csStatus[0] != DirectIOStatus.STATUS_SUCCESS) {
                JOptionPane.showMessageDialog(null, "Error", "Error performing DirectIO", JOptionPane.ERROR_MESSAGE);
                status[0] = "Error in performing Direct IO";
                directIOC = false;
            }
        } catch (JposException je) {
            directIOC = false;
            status[0] = "Error in performing Direct IO";
            status[1] = exceptionDialog(je, "Exception ");
        }
        return status;
    }
    
    /**
     * This method is used to perform NCR Direct Input Output action.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param opCode : opCode of the Direct IO command
     * @param csStatus : status of the command performed
     * @param deviceParams : InXml and OutXml values
     * @return status(String []) status[0]:indicates the status of the method
     * called status[1]:exception details
     */
    public String[] ncrDirectIOAction(DeviceTypeBinder device, int opCode, int[] csStatus, Object deviceParams) {
        String[] status = new String[2];
        try {
            device.setDirectIO(opCode, csStatus, (Object) deviceParams);
            status[0] = "Direct IO Successful";
            csStatus[0] = DirectIOStatus.STATUS_SUCCESS;
            directIOC = true;
            
        } catch (JposException je) {
            directIOC = false;
            csStatus[0] = -1;
            status[0] = "Error in performing Direct IO";
            status[1] = exceptionDialog(je, "Exception ");
        }
        return status;
    }
    
    public String[] ncrDirectIOLiveWeightAction(DeviceTypeBinder device, int opCode, int[] csStatus, Object deviceParams) {
        String[] status = new String[2];
        try {
            device.setDirectIO(opCode, csStatus, (Object) deviceParams);
            status[0] = "Direct IO Successful";
            directIOC = true;
            
        } catch (JposException je) {
            directIOC = false;
            status[0] = "Error in performing Direct IO";
            status[1] = exceptionDialog(je, "Exception ");
        }
        return status;
    }

    /**
     * This method is used to check the deviceEnable status.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return deviceEnabled(boolean) true if device is enabled and false
     * otherwise
     */
    public boolean checkDeviceEnable(DeviceTypeBinder device) {
        boolean deviceEnabled = true;
        try {
            deviceEnabled = device.getDeviceEnable();
        } catch (JposException ex) {
            Logger.getLogger(IntermediateLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return deviceEnabled;
    }

    /**
     * This method is used to capture the bar codes read by the device.
     *
     * @param device : object created from DeviceTypeBinder class
     */
    public void dataListenerEvent(ScannerDeviceTypeBinder device) {
        try {
            scanDataLabelText = new String(device.getScanDataLabel());
            scanDataLabelHex = getHexEncodedDataLabel(device.getScanDataLabel());
            scanData = new String(device.getScanData());
            scanDataType = device.getScanDataType();
            scanDataCount = device.getDataCount();
        } catch (JposException je) {
            JOptionPane.showMessageDialog(null, "Failed to Scan Data " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is used to convert the scanned data into hexadecimal form.
     *
     * @param bytes : scanned data in text form
     * @return status
     */
    private String getHexEncodedDataLabel(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("0x%02X ", (b & 0xff)));
        }
        return stringBuilder.toString();
    }

    /**
     * This method is used to receive the live weight notifications.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param e : status update event
     * @return live weight value along with a description
     */
    public String statusUpdateListenerEvent(ScaleDeviceTypeBinder device, StatusUpdateEvent e) {
        String response = "";

        try {
            liveWeight = device.getLiveWeight();

            switch (e.getStatus()) {
                case ScaleConst.SCL_SUE_STABLE_WEIGHT:
                    response = (((float) liveWeight) / 1000 + " " + getUnit(device) + "\t- SCL_SUE_STABLE_WEIGHT");
                    break;
                case ScaleConst.SCL_SUE_NOT_READY:
                    response = (((float) liveWeight) / 1000 + " " + getUnit(device) + "\t- SCL_SUE_NOT_READY");
                    break;
                case ScaleConst.SCL_SUE_WEIGHT_UNSTABLE:
                    response = (((float) liveWeight) / 1000 + " " + getUnit(device) + "\t- SCL_SUE_WEIGHT_UNSTABLE");
                    break;
                case ScaleConst.SCL_SUE_WEIGHT_ZERO:
                    response = (((float) liveWeight) / 1000 + " " + getUnit(device) + "\t- SCL_SUE_WEIGHT_ZERO");
                    break;
                case ScaleConst.SCL_SUE_WEIGHT_UNDER_ZERO:
                    response = (((float) liveWeight) / 1000 + " " + getUnit(device) + "\t- SCL_SUE_WEIGHT_UNDER_ZERO");
                    break;
            }
        } catch (JposException je) {
            JOptionPane.showMessageDialog(null, "Failed to perform Live Weight " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return response;
    }

    /**
     * This method returns unit types corresponding to Integer constants.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    private String getUnit(ScaleDeviceTypeBinder device) {
        int unit;
        String dUnits = "";
        try {
            unit = device.getWeightUnits();

            switch (unit) {
                case ScaleConst.SCAL_WU_GRAM:
                    dUnits = "g";
                    break;
                case ScaleConst.SCAL_WU_KILOGRAM:
                    dUnits = "kg";
                    break;
                case ScaleConst.SCAL_WU_OUNCE:
                    dUnits = "oz";
                    break;
                case ScaleConst.SCAL_WU_POUND:
                    dUnits = "lb";
                    break;
            }
        } catch (JposException ex) {
            Logger.getLogger(JposSampleApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dUnits;
    }
}
