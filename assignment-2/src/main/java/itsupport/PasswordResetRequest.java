/**A. PasswordResetRequest
Represents a password or account reset request.
Required additional data:
        • String accountName (not null or blank)
Resolution rule:
        • awards exactly basePoints*/

package itsupport;

public class PasswordResetRequest {
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