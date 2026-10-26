/**B. DeviceSetupRequest
Represents setup or configuration of one or more devices.
Required additional data:
        • int deviceCount (must be positive)
Resolution rule:
        • awarded service points = basePoints * deviceCount*/

package itsupport;

public class DeviceSetupRequest {
    public synchronized int resolveFor(Technician technician) {
        if (resolved) {
            return 0;
        }

        // ...
    }

    @Override
    public String toString() {

    }
}