/**B. DeviceSetupRequest
Represents setup or configuration of one or more devices.
Required additional data:
        • int deviceCount (must be positive)
Resolution rule:
        • awarded service points = basePoints * deviceCount*/

package itsupport;

public class DeviceSetupRequest extends AbstractSupportRequest {
    int deviceCount;

    public DeviceSetupRequest(int deviceCount, String title, int id) {
        super(id, title, deviceCount);

        if(deviceCount < 0){
            throw new IllegalArgumentException("Device count must be a positive integer");
        }
        this.deviceCount = deviceCount;
        this.awardedPoints = this.getBasePoints()*deviceCount;
    }

    @Override
    public String toString() {
        return "DeviceSetupRequest [device count=" + deviceCount + "] " + super.toString();

    }
}