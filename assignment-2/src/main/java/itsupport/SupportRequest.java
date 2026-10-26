/**1) SupportRequest (interface)
Define a behavior contract for all support requests.
Required methods:
        • int getId()
• String getTitle()
• int getBasePoints()
3
        • boolean isResolved()
• int resolveFor(Technician technician)
Critical rule: resolveFor must:
        • update the request’s resolved state,
        • award service points to the technician,
• and return the number of service points awarded.
Design note: The help desk should call request.resolveFor(technician) without needing
to know the concrete request type. This is the point of the interface and polymorphic dispatch.*/

package itsupport;

public interface SupportRequest {
    // Note: Should be thread safe
    int resolveFor(Technician technician);

    int getId();
    String getTitle();
    int getBasePoints();
    boolean isResolved();
}