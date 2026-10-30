/**5) HelpDesk
 This class coordinates request storage and assignment.
 Required fields (minimum):
 • Map<Integer, SupportRequest> requestsById
 • Map<Technician, List<SupportRequest>> assignments
 Required behavior:
 • addRequest(SupportRequest request)
 – rejects duplicate request IDs by throwing IllegalArgumentException
 • SupportRequest findRequest(int id) returns the matching request or null if not found
 • assignRequest(Technician technician, int requestId)
 – assigns an existing request to a technician
 – decide whether the same request can be assigned more than once to the same technician
 and enforce your rule consistently
 • int resolveRequest(Technician technician, int requestId)
 – finds the request in that technician’s assignments
 – calls request.resolveFor(technician) polymorphically
 – returns the service points awarded
 • void printAllRequests()
 • void printAssignmentsFor(Technician technician)
 Design requirements:
 • Keep fields private.
 • Do not expose internal collections directly.
 • Avoid long message chains and knowledge leaks.
 • The help desk should not perform type checking such as if (request instanceof DeviceSetupRequest).
 • Request-specific behavior belongs in the request classes, not in HelpDesk.
 • Keep HelpDesk focused on coordination rather than turning it into a god class.*/

package itsupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HelpDesk {
    private final Map<Integer, SupportRequest> requestsById = new LinkedHashMap<>();
    private final Map<Technician, List<SupportRequest>> assignments = new LinkedHashMap<>();

    public HelpDesk() {

    }

    public int addRequest(SupportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Support request cannot be null");
        }

        int id = request.getId();

        if (requestsById.containsKey(id)) {
            throw new IllegalArgumentException("Request with id '" + id + "' already exists");
        }

        requestsById.put(id, request);
        return id;
    }

    // No Optional
    public SupportRequest findRequest(int id) {
        return requestsById.get(id);
    }

    // For now, I am allowing a request to be assigned to multiple technicians, but only allowing it to be resolved against one technician
    // I think that this aligns with the assignment's requirements, and it can be seen as maybe two technicians are competing for the request's points
    public boolean assignRequest(Technician technician, int requestId) {
        if (technician == null) {
            throw new IllegalArgumentException("Technician cannot be null");
        }

        if (!(requestsById.containsKey(requestId))) {
            throw new IllegalArgumentException("Support request with id '" + requestId + "' does not exist");
        }

        List<SupportRequest> requestsByTechnician = assignments.putIfAbsent(technician, new ArrayList<>());

        // Avoids using lambda with computeIfAbsent()
        if (requestsByTechnician == null) {
            requestsByTechnician = assignments.get(technician);
        }

        SupportRequest request = requestsById.get(requestId);

        if (requestsByTechnician.contains(request)) {
            return false;
        }

        requestsByTechnician.add(requestsById.get(requestId));
        return true;
    }

    public int resolveRequest(Technician technician, int requestId) {
        if (technician == null) {
            throw new IllegalArgumentException("Technician cannot be null");
        }

        List<SupportRequest> requestsByTechnician = assignments.get(technician);
        SupportRequest request = requestsById.get(requestId);

        if (requestsByTechnician == null || !(requestsByTechnician.contains(request))) {
            throw new IllegalArgumentException("Support request '" + requestId + "' does not belong to technician '" + technician.getName());
        }

        int pointsAwarded = request.resolveFor(technician);
        // Should we remove it from the technician's assignments? I decided not to remove it
        // Basically assignments represents anything they have ever been assigned,
        // even if it has since been resolved against them or if another technician had it resolved against them
        return pointsAwarded;
    }

    public void printAllRequests() {
        System.out.println("Support Requests by ID");

        for (Map.Entry<Integer, SupportRequest> entry : requestsById.entrySet()) {
            // toString() for clarity - Java automatically calls toString when concatenating with a String
            System.out.println("(ID) " + entry.getKey() + " - (Request) " + entry.getValue().toString());
        }
    }

    public void printAssignmentsFor(Technician technician) {
        if (technician == null) {
            throw new IllegalArgumentException("Technician cannot be null");
        }

        System.out.println("Technician " + technician.getName() + "'s Assignments");

        List<SupportRequest> assignmentsForTechnician = assignments.get(technician);

        HelpDeskUtil.printAll(assignmentsForTechnician);
    }
}