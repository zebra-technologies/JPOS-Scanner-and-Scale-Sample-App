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
     * This method is used to open the service on the selected logical device
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] openAction(DeviceTypeBinder device) {

        String[] status = new String[2];
        try {
            device.setOpen(logicalName);
            status[0] = "Device Opened";
        } catch (JposException je) {
            status[0] = "Device Not Opened";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to open \"" + logicalName + "\"\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to claim the opened logical device
     *
     * @param device : object created from DeviceTypeBinder class
     * @param claimTimeOut : timeout form claim
     * @return status
     */
    public String[] claimAction(DeviceTypeBinder device, int claimTimeOut) {
        String[] status = new String[2];
        try {
            device.setClaim(claimTimeOut);
            status[0] = "Device Claimed";
        } catch (JposException je) {
            status[0] = "Device Not Claimed";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to claim \"" + logicalName + "\"\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to release the logical device
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] releaseAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setRelease();
            status[0] = "Device Released";
        } catch (JposException je) {
            status[0] = "Device Not Released";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to release \"" + logicalName + "\"\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to close the service from logical device.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] closeAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setClose();
            status[0] = "Device Closed";
        } catch (JposException je) {
            status[0] = "Device Unable to Close";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to close \"" + logicalName + "\"\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform device enable and disable action. Requires
     * a boolean value
     *
     * @param device : object created from DeviceTypeBinder class
     * @param deviceEnabled : enables if true, disables if false
     * @return status
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
            deviceEnableC = false;
            status[0] = "Device Disabled";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable device " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to enable or disable the data event on the logical
     * device. Requires a boolean value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param dataEventEnabled : enable data event is true
     * @return status
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
            dataEventEnableC = false;
            status[0] = "Data Event Disabled";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable data event " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to enable or disable the data event on the logical
     * device. Requires a boolean value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param autoDisable : enable device if true
     * @return status
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
            autoDisableC = false;
            status[0] = "Auto Disable is False";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed in auto device enable " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform the freeze events action. Requires a
     * boolean value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param freezeEventsEnabled : freeze events if true
     * @return status
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
            freezeEventsC = false;
            status[0] = "Freeze Events Disabled";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable freeze events " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform the decode data action. Requires a boolean
     * value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param decodeDataEnabled : enable decode data event if true
     * @return status
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
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable decode data " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform check health action. Requires an Integer
     * value.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param healthCheckType : type of the health check to be performed
     * @return status
     */
    public String[] checkHealthAction(DeviceTypeBinder device, int healthCheckType) {
        String[] status = new String[2];
        try {
            device.setHealthCheck(healthCheckType);
            status[0] = "Health Check Enabled";
        } catch (JposException je) {
            status[0] = "Health Check Disabled";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable health check " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to get the health check text on the health check type
     * performed.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] checkHealthTextAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            healthCheckText = device.getHealthCheckText();
            status[0] = "Health Check Text Enabled";
        } catch (JposException je) {
            status[0] = "Health Check Text Disabled";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable health check text " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to enable fast mode for Scanners.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
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
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable fast mode " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform fast mode in Scale.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
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
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable fast mode " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to set property values into default.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] clearInputPropertiesAction(ScannerDeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setClearInputProperties();
            status[0] = "Set clear Input Properties true";
        } catch (JposException je) {
            status[0] = "Set clear Input Properties false";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to clear input properties " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to enable asynchronous mode in scale.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param value : enable if true, disable if false
     * @return status
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
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Jpos exception in AsyncMode " + je, "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform status notify action.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param value : indicates whether to enable or disable the property.
     * (1-enable, 2- disable)
     * @param autoDeviceEnable : indicates whether the autoDeviceEnable property
     * is true or false
     * @return status
     */
    public String[] statusNotifyAction(ScaleDeviceTypeBinder device, int value, boolean autoDeviceEnable) {
        String[] status = new String[2];
        try {
            device.setStatusNotify(value);
            if (value == 2) {
                status[0] = "Live Weight Enabled";
            } else {
                status[0] = "Live Weight Disabled";
            }
        } catch (JposException je) {
            status[0] = "Live Weight Disabled";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to enable live weight " + "\nException: " + je.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform read weight action.
     *
     * @param device : object created from DeviceTypeBinder class
     * @param timeOut : timeout value
     * @return status
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
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to read weight \"" + logicalName + "\"\nException: " + je, "Fails", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to perform the zero scale method.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] zeroScaleAction(ScaleDeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setZeroScale();
            status[0] = "Zero Scale Performed";
        } catch (JposException je) {
            status[0] = "Zero Scale Unsuccessful";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Failed to zero \"" + logicalName + "\"\nException" + je, "Fails", JOptionPane.ERROR_MESSAGE);
        }
        return status;
    }

    /**
     * This method is used to clear input data.
     *
     * @param device : object created from DeviceTypeBinder class
     * @return status
     */
    public String[] clearInputAction(DeviceTypeBinder device) {
        String[] status = new String[2];
        try {
            device.setClearInput();
            status[0] = "Clear Input Data";
        } catch (JposException je) {
            status[0] = "No Input Data Cleared";
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Exception " + je, "Failed", JOptionPane.ERROR_MESSAGE);
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
     * @return status
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
            status[1] = "Exception: " + je.getMessage() + "  Error: " + je.getErrorCode();
            JOptionPane.showMessageDialog(null, "Exception :" + je, "Failed", JOptionPane.ERROR_MESSAGE);
        }
        return status;
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
