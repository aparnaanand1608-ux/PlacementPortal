
import java.util.*;
import java.io.*;

// Student class
class Student {
    String id, name;
    double cgpa;

    Student(String id, String name, double cgpa) {
        this.id = id;
        this.name = name;
        this.cgpa = cgpa;
    }
}

// BST Node
class Node {
    Student student;
    Node left, right;

    Node(Student student) {
        this.student = student;
    }
}

// Binary Search Tree
class StudentBST {
    Node root;

    void insert(Student s) {
        root = insertRec(root, s);
    }

    Node insertRec(Node root, Student s) {
        if (root == null)
            return new Node(s);

        if (s.id.compareTo(root.student.id) < 0)
            root.left = insertRec(root.left, s);
        else if (s.id.compareTo(root.student.id) > 0)
            root.right = insertRec(root.right, s);

        return root;
    }

    Student search(String id) {
        Node current = root;

        while (current != null) {
            int result = id.compareTo(current.student.id);

            if (result == 0)
                return current.student;
            else if (result < 0)
                current = current.left;
            else
                current = current.right;
        }
        return null;
    }

    void display(Node root) {
        if (root != null) {
            display(root.left);
            System.out.println(root.student.id + " | "
                    + root.student.name + " | CGPA "
                    + root.student.cgpa);
            display(root.right);
        }
    }

    void displayAll() {
        if (root == null)
            System.out.println("No students found.");
        else
            display(root);
    }

    void collect(Node root, List<Student> list) {
        if (root != null) {
            collect(root.left, list);
            list.add(root.student);
            collect(root.right, list);
        }
    }

    List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        collect(root, list);
        return list;
    }
}

// Job class
class Job {
    String id, company, role;
    double minCGPA;

    Set<String> applicantIds = new HashSet<>();

    PriorityQueue<Student> applicants = new PriorityQueue<>(
        Comparator.comparingDouble((Student s) -> s.cgpa)
                .reversed()
                .thenComparing(s -> s.name)
    );

    Job(String id, String company, String role, double minCGPA) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.minCGPA = minCGPA;
    }

    void addApplicant(Student s) {
        if (applicantIds.add(s.id))
            applicants.offer(s);
    }
}

// Main class
public class PlacementPortal {

    static Scanner sc = new Scanner(System.in);

    static StudentBST students = new StudentBST();

    static HashMap<String, Job> jobs = new HashMap<>();

    static int nextId = 101;

    static final String FILE = "placements.txt";

    // Add Student
    static void addStudent() {
        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        double cgpa = readDouble("Enter CGPA: ");

        if (cgpa < 0 || cgpa > 10) {
            System.out.println("Invalid CGPA. Enter between 0 and 10.");
            return;
        }

        String id = "ST" + nextId++;

        Student s = new Student(id, name, cgpa);
        students.insert(s);

        System.out.println("Student added: " + id + " | "
                + name + " | CGPA " + cgpa);
    }

    // Add Job
    static void addJob() {
        System.out.print("Enter Job ID: ");
        String id = sc.nextLine();

        if (jobs.containsKey(id)) {
            System.out.println("Job ID already exists.");
            return;
        }

        System.out.print("Enter company name: ");
        String company = sc.nextLine();

        System.out.print("Enter job role: ");
        String role = sc.nextLine();

        double cgpa = readDouble("Enter minimum CGPA: ");

        if (cgpa < 0 || cgpa > 10) {
            System.out.println("Invalid minimum CGPA.");
            return;
        }

        jobs.put(id, new Job(id, company, role, cgpa));

        System.out.println("Job added successfully.");
    }

    // Search Job
    static void searchJob() {
        System.out.print("Enter Job ID: ");
        String id = sc.nextLine();

        Job j = jobs.get(id);

        if (j == null) {
            System.out.println("Job " + id + " not found.");
        } else {
            System.out.println("Job ID: " + j.id);
            System.out.println("Company: " + j.company);
            System.out.println("Role: " + j.role);
            System.out.println("Minimum CGPA: " + j.minCGPA);
        }
    }

    // Apply for Job
    static void applyJob() {
        System.out.print("Enter Student ID: ");
        String sid = sc.nextLine();

        Student s = students.search(sid);

        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter Job ID: ");
        String jid = sc.nextLine();

        Job j = jobs.get(jid);

        if (j == null) {
            System.out.println("Job " + jid + " not found.");
            return;
        }

        if (s.cgpa < j.minCGPA) {
            System.out.println("Not eligible. Minimum CGPA required: "
                    + j.minCGPA);
            return;
        }

        if (j.applicantIds.contains(sid)) {
            System.out.println("Duplicate application not allowed.");
            return;
        }

        j.addApplicant(s);

        System.out.println("Application submitted successfully!");
    }

    // Shortlist Students
    static void shortlist() {
        System.out.print("Enter Job ID: ");
        String id = sc.nextLine();

        Job j = jobs.get(id);

        if (j == null) {
            System.out.println("Job not found.");
            return;
        }

        if (j.applicants.isEmpty()) {
            System.out.println("No eligible applicants found.");
            return;
        }

        List<Student> list = new ArrayList<>(j.applicants);

        list.sort(
            Comparator.comparingDouble((Student s) -> s.cgpa)
                    .reversed()
                    .thenComparing(s -> s.name)
        );

        System.out.println("\nShortlisted Students:");

        int rank = 1;

        for (Student s : list) {
            System.out.println(rank++ + ". " + s.id + " | "
                    + s.name + " | " + s.cgpa);
        }
    }

    // Display All Students
    static void displayStudents() {
        System.out.println("\nStudent Records:");
        students.displayAll();
    }

    // Display All Jobs
    static void displayJobs() {
        if (jobs.isEmpty()) {
            System.out.println("No jobs available.");
            return;
        }

        System.out.println("\nAvailable Jobs:");

        for (Job j : jobs.values()) {
            System.out.println(j.id + " | " + j.company
                    + " | " + j.role + " | Minimum CGPA: "
                    + j.minCGPA);
        }
    }

    // Save data to file
    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {

            for (Student s : students.getAll()) {
                pw.println("S|" + s.id + "|" + s.name + "|" + s.cgpa);
            }

            for (Job j : jobs.values()) {
                pw.println("J|" + j.id + "|" + j.company
                        + "|" + j.role + "|" + j.minCGPA);
            }

            for (Job j : jobs.values()) {
                for (String sid : j.applicantIds) {
                    pw.println("A|" + j.id + "|" + sid);
                }
            }

            System.out.println("Data saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load data from file
    static void loadData() {
        File file = new File(FILE);

        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data[0].equals("S") && data.length == 4) {
                    Student s = new Student(
                            data[1], data[2],
                            Double.parseDouble(data[3]));

                    students.insert(s);

                    try {
                        nextId = Math.max(nextId,
                                Integer.parseInt(data[1].substring(2)) + 1);
                    } catch (NumberFormatException e) {
                        // Ignore non-standard student IDs.
                    }
                }

                else if (data[0].equals("J") && data.length == 5) {
                    Job j = new Job(data[1], data[2],
                            data[3], Double.parseDouble(data[4]));

                    jobs.put(j.id, j);
                }

                else if (data[0].equals("A") && data.length == 3) {
                    Job j = jobs.get(data[1]);
                    Student s = students.search(data[2]);

                    if (j != null && s != null
                            && s.cgpa >= j.minCGPA) {
                        j.addApplicant(s);
                    }
                }
            }

            System.out.println("Previous data loaded successfully.");

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    // Read valid double input
    static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                double value = Double.parseDouble(sc.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Main Method
    public static void main(String[] args) {

        loadData();

        while (true) {

            System.out.println("\n===== PLACEMENT PORTAL =====");
            System.out.println("1. Add Student");
            System.out.println("2. Add Job");
            System.out.println("3. Search Job");
            System.out.println("4. Apply for Job");
            System.out.println("5. Shortlist Students");
            System.out.println("6. Display Students");
            System.out.println("7. Display Jobs");
            System.out.println("8. Exit");

            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {

                case "1":
                    addStudent();
                    break;

                case "2":
                    addJob();
                    break;

                case "3":
                    searchJob();
                    break;

                case "4":
                    applyJob();
                    break;

                case "5":
                    shortlist();
                    break;

                case "6":
                    displayStudents();
                    break;

                case "7":
                    displayJobs();
                    break;

                case "8":
                    saveData();
                    System.out.println("Thank you for using Placement Portal!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
