package itsupport;
import java.util.ArrayList;
import java.util.HashSet;

/**Main Demo Requirements
 Your Main must be a hardcoded demonstration that shows the system working.
 Minimum demo:
 • Create at least 3 technicians, including two technicians with the same name to demonstrate
 HashSet<Technician> behavior.
 • Create at least 6 support requests total, including at least 2 of each required request type.
 • Add requests to the help desk.
 • Assign at least 4 requests across at least 2 technicians.
 • Resolve at least 3 requests across different request types.
 • Print all requests.
 • Print assignments for each technician.
 • Print final technician service point totals.
 • Use HelpDeskUtil.sumPoints to print the total service points across technicians.
 Fail-fast requirement: Demonstrate at least one invalid action and show that your program
 throws an exception. Examples include adding a duplicate request ID or resolving the same request
 twice. Do not silently ignore invalid operations.
 Open Design Choices (You Must Decide and Document)
 You must choose rules and enforce them consistently. Add a short comment where the rule is
 enforced.
 1) Can a support request be resolved more than once?
 • Recommended: no; resolving twice should throw IllegalStateException.
 7
 2) Can the same support request be assigned to the same technician more than once?
 • If no, enforce the rule in HelpDesk.assignRequest.
 • If yes, clearly document what duplicate assignment means.
 3) Can one support request be assigned to multiple technicians?
 • Choose and document a rule, then enforce it consistently.
 4) For NetworkIssueRequest, define a clear service-point rule and validate its data.
 Deliverables
 Submit your exported project as a .zip or .rar file through your IDE. Your project must include
 the files below at minimum. You may include additional classes if your design is clean and justified.
 • SupportRequest.java
 • AbstractSupportRequest.java
 • PasswordResetRequest.java
 • DeviceSetupRequest.java
 • NetworkIssueRequest.java
 • Technician.java
 • HelpDesk.java
 • HelpDeskUtil.java
 • Main.java
 • README.txt
 • A screenshots folder outside your source directory
 8
 Grading Rubric (40 points total)
 1. Correctness and Required Features (12 points)
 • Program compiles and runs; required files and demo are complete (3 points)
 • Request types resolve correctly and award the correct service points (4 points)
 • HelpDesk supports add, find, assign, resolve, and printing as specified (3 points)
 • Fail-fast behavior is demonstrated correctly for invalid operations (2 points)
 2. Polymorphism and Object-Oriented Design (10 points)
 • Uses the SupportRequest interface and polymorphic calls properly; no request-type condi-
 tional chain in HelpDesk (4 points)
 • Appropriate use of AbstractSupportRequest for shared state and behavior (2 points)
 • Responsibilities are well placed; avoids god classes and anemic objects (2 points)
 • Avoids long message chains and knowledge leaks (2 points)
 3. Collections, equals/hashCode, and Data Integrity (7 points)
 • Correct use of generic collections (Map, List, Set) with appropriate type parameters (3 points)
 • Technician.equals and Technician.hashCode are consistent and support correct HashSet
 behavior (2 points)
 • Duplicate request ID detection and other integrity rules are enforced reliably (2 points)
 4. Generics Utilities (5 points)
 • HelpDeskUtil.printAll(List<T>) is implemented correctly and used in the demo (2 points)
 • HelpDeskUtil.sumPoints(List<? extends Technician>) is implemented correctly and used
 in the demo (3 points)
 5. Git Collaboration and Commit Quality (3 points)
 • All group members make meaningful contributions through Git commits (1 point)
 • Commits are reasonably frequent and describe logical units of work rather than one large
 final upload (1 point)
 • Repository history demonstrates appropriate collaboration, including pulling/pushing respon-
 sibly and avoiding destructive overwrites (1 point)
 6. Readability, Documentation, and Submission Quality (3 points)
 • Clear naming, consistent formatting, and meaningful toString() output (2 points)
 • README, screenshots, GitHub link, and exported Canvas submission are correct (1 point)
 9
 Notes to Students
 There are multiple valid designs. You will be graded on:
 • how well your design uses polymorphism to avoid type-based conditionals,
 • how well your classes protect their invariants and fail fast,
 • how correctly you use collections and generics,
 • how consistently you enforce your documented rules,
 • and how effectively your group collaborates through Git.
 Before submitting, ask yourself:
 • Can an object be created in an invalid state?
 • Does the help desk know too much about concrete request types?
 • Are request-specific rules located inside the request classes?
 • Are internal collections protected from outside modification?
 • Are equals and hashCode consistent?
 • Are invalid operations rejected clearly rather than silently ignored?
 • Did every group member contribute meaningful commits?
 • Can your exported project be imported and run successfully on another computer?*/

public class Main {
    public static void main(String[] args) {

        //HelpDesk creation
        HelpDesk help = new HelpDesk();

        //Tech creation and adding to ArrayList
        Technician unique = new Technician("Jacob");
        Technician copy1 = new Technician("Randy");
        Technician copy2 = new Technician("Randy");

        HashSet<Technician> techSet = new HashSet<Technician>();
        techSet.add(unique);
        techSet.add(copy1);
        techSet.add(copy2);

        //DeviceSetUpRequest objects
        DeviceSetupRequest device1 = new DeviceSetupRequest(1, "Classroom setup", 5,24 );
        DeviceSetupRequest device2 = new DeviceSetupRequest(2, "Office setup", 10,6 );

        //NetworkIssueRequest objects
        NetworkIssueRequest network1 = new NetworkIssueRequest(3, "wifi no internet", 2, 52);
        NetworkIssueRequest network2 = new NetworkIssueRequest(4, "Database not connecting to API", 15, 2);

        //PasswordResetRequest objects
        PasswordResetRequest password1 = new PasswordResetRequest(5, "Forgot Password", 5, "JohnSmith1");
        PasswordResetRequest password2 = new PasswordResetRequest(6, "Forgot Password", 5, "JakeSully1948");

        //Adding request to HelpDesk object
        help.addRequest(device1);
        help.addRequest(device2);
        help.addRequest(network1);
        help.addRequest(network2);
        help.addRequest(password1);
        help.addRequest(password2);

        //Assigning requests to technicians
        help.assignRequest(unique, 1);
        help.assignRequest(copy1, 2);
        help.assignRequest(copy1, 3);
        help.assignRequest(unique, 4);
        help.assignRequest(copy1, 5);
        help.assignRequest(unique, 6);

        //Resolve requests
        help.resolveRequest(copy1, 2);
        help.resolveRequest(copy1, 5);
        help.resolveRequest(unique, 4);
        System.out.println("Error for resolving already resolved issue from another technician:\n--------------------------------------------------------");
        help.resolveRequest(unique, 5); //Should throw error
        System.out.println("--------------------------------------------------------");
        //Printing section
        System.out.println("Printing all requests:");
        help.printAllRequests();

        System.out.println("\n\nPrinting device setup requests:");
        help.printAssignmentsFor(unique);
        help.printAssignmentsFor(copy1);

        System.out.println("\n\nPrinting individual points:");
        for(Technician tech : techSet){
            System.out.println(tech);
        }

        System.out.println("\n\nTotal points in system:");
        //Translating HashSet to ArrayList for sumPoints method to work
        ArrayList<Technician> techList = new ArrayList<>(techSet);
        int totalPoints = HelpDeskUtil.sumPoints(techList);
        System.out.println(totalPoints);




    }
}
