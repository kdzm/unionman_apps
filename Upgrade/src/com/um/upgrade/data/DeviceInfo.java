package com.um.upgrade.data;

/**
 * Created by hjian on 2015/3/16.
 */
public class DeviceInfo {
    private String produceModel;
    private String vendor;
    private String hardwareVersion;
    private String softwareVersion;
    private String serial;
    private String mac;
    private String displaySoftVersion;
    private String displaySerial;
    private String machineModel;

    public String getMachineModel() {
        return machineModel;
    }

    public void setMachineModel(String machineModel) {
        this.machineModel = machineModel;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDisplaySoftVersion() {
        return displaySoftVersion;
    }

    public void setDisplaySoftVersion(String displaySoftVersion) {
        this.displaySoftVersion = displaySoftVersion;
    }

    public String getDisplaySerial() {
        return displaySerial;
    }

    public void setDisplaySerial(String displaySerial) {
        this.displaySerial = displaySerial;
    }

    public String getProduceModel() {
        return produceModel;
    }

    public void setProduceModel(String produceModel) {
        this.produceModel = produceModel;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
