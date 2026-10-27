/**B. DeviceSetupRequest
Represents setup or configuration of one or more devices.
Required additional data:
        • int deviceCount (must be positive)
Resolution rule:
        • awarded service points = basePoints * deviceCount*/

package itsupport;

public class DeviceSetupRequest extends AbstractSupportRequest {
    private final int deviceCount;

    public DeviceSetupRequest(int id, String title, int basePoints, int deviceCount) {
        super(id, title, basePoints);

        if(deviceCount <= 0){
            throw new IllegalArgumentException("Device count must be a positive integer");
        }
        this.deviceCount = deviceCount;
    }

    @Override
    protected int calculatePoints() {
        return getBasePoints() * deviceCount;
    }

    @Override
    public String toString() {
        return "DeviceSetupRequest [device count=" + deviceCount + "] " + super.toString();

    }
}