package mainapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MyApp {

    // Method to check if the branches feature-a and feature-b-renamed exist
    public static String areBranchesPresent() {
        try {
            System.out.println("Checking if branches 'feature-a' and 'feature-b-renamed' exist...");
            String branches = executeCommand("git branch --list").trim();

            if (branches.contains("feature-a") && branches.contains("feature-b-renamed")) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            System.out.println("Error in areBranchesPresent method: " + e.getMessage());
            return "";
        }
    }

    // Method to check if the "Created from main" entry exists in the reflog for feature-a
    public static String checkReflogForFeatureA() {
        try {
            System.out.println("Checking for 'feature-a' branch origin");
            String reflog = executeCommand("git reflog show feature-a").trim();

            if (reflog.contains("Created from main")) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            System.out.println("Error in checkReflogForFeatureA method: " + e.getMessage());
            return "";
        }
    }

    // Method to check if the "Created from feature-a" entry exists in the reflog for feature-b-renamed
    public static String checkReflogForFeatureBRenamed() {
        try {
            System.out.println("Checking for 'feature-b-renamed' branch origin");
            String reflog = executeCommand("git reflog show feature-b-renamed").trim();

            if (reflog.contains("Created from feature-a")) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            System.out.println("Error in checkReflogForFeatureBRenamed method: " + e.getMessage());
            return "";
        }
    }

    // Method to check for the merge commit and validate branch switching
    public static String checkMergeCommitAndBranchMove(String mergedBranchName) {
        try {
            System.out.println("Checking for merge commit and branch movement for " + mergedBranchName + "...");

            // Get the reflog for the repository
            String reflog = executeCommand("git reflog").trim();
            // System.out.println(reflog);

            // Split the reflog into individual lines
            String[] reflogLines = reflog.split("\n");

            // Check for the merge commit and get the merged branch name
            String mergedBranch = "";
            String movedToBranch = "";

            // Check for the merge commit for the passed mergedBranchName
            for (String line : reflogLines) {
                if (line.toLowerCase().contains("merge")) {
                    // Extract the branch name after "merge"
                    mergedBranch = line.split("merge")[1].split(":")[0].trim();
                    if (mergedBranch.equals(mergedBranchName)) {
                        System.out.println("Merged branch: " + mergedBranch);
                        // Now check for the "moving from" line
                        for (String moveLine : reflogLines) {
                            if (moveLine.toLowerCase().contains("moving from " + mergedBranch)) {
                                // Extract the branch name to which the movement has occurred
                                String[] parts = moveLine.split("moving from " + mergedBranch + " to ");
                                if (parts.length > 1) {
                                    movedToBranch = parts[1].split(" ")[0].trim(); // Get the branch name after 'to'
                                    System.out.println("Found branch merged to " + movedToBranch);
                                    return "true";  // Branch move confirmed for mergedBranchName
                                }
                            }
                        }
                    }
                }
            }

            // If no matching "moving from" line is found, return false
            return "false";

        } catch (Exception e) {
            System.out.println("Error in checkMergeCommitAndBranchMove method: " + e.getMessage());
            return "";
        }
    }

    // Method to check for branch renaming
    public static String checkBranchRenaming(String originalBranchName, String renamedBranchName) {
        try {
            System.out.println("Checking for branch renaming from " + originalBranchName + " to " + renamedBranchName + "...");

            // Get the reflog for the renamed branch
            String reflog = executeCommand("git reflog show " + renamedBranchName).trim();

            // Split the reflog into individual lines
            String[] reflogLines = reflog.split("\n");

            // Check for the "Branch: renamed" entry that confirms the renaming
            for (String line : reflogLines) {
                if (line.toLowerCase().contains("branch: renamed")) {
                    System.out.println(line);
                    // Extract and print the original and renamed branch names from the reflog entry
                    String[] parts = line.split(" to ");
                    if (parts.length > 1) {
                        // Split the 0th and 1st parts by "/"
                        String renamedFrom = parts[0].split("/")[2].trim();  // Extract the branch name after "refs/heads/"
                        String renamedTo = parts[1].split("/")[2].trim();  // Extract the branch name after "refs/heads/"
                        
                        // Check if the renamed branches match
                        if (renamedFrom.equals(originalBranchName) && renamedTo.equals(renamedBranchName)) {
                            System.out.println("Branch renamed from " + originalBranchName + " to " + renamedBranchName);
                            return "true";  // Renaming confirmed
                        }
                    }
                }
            }

            // If no matching "Branch: renamed" entry is found, return false
            return "false";

        } catch (Exception e) {
            System.out.println("Error in checkBranchRenaming method: " + e.getMessage());
            return "";
        }
    }

    // Helper method to execute git commands
    private static String executeCommand(String command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(".")); // Ensure this is the correct directory where Git repo is located
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            return output.toString();
        } else {
            System.out.println("Command failed with exit code: " + exitVal);
            throw new RuntimeException("Failed to execute command: " + command);
        }
    }

    // Main method to run the checks manually
    public static void main(String[] args) {
        try {
            // Check if 'feature-a' and 'feature-b-renamed' branches exist
            String branchesExist = areBranchesPresent();
            if (branchesExist.equals("true")) {
                System.out.println("Both branches 'feature-a' and 'feature-b-renamed' exist.");
            } else {
                System.out.println("One or both branches do not exist.");
            }

            // Check reflog for 'feature-a'
            String featureAReflog = checkReflogForFeatureA();
            if (featureAReflog.equals("true")) {
                System.out.println("Reflog for 'feature-a' contains 'Created from main'.");
            } else {
                System.out.println("Reflog for 'feature-a' does not contain 'Created from main'.");
            }

            // Check reflog for 'feature-b-renamed'
            String featureBReflog = checkReflogForFeatureBRenamed();
            if (featureBReflog.equals("true")) {
                System.out.println("Reflog for 'feature-b-renamed' contains 'Created from feature-a'.");
            } else {
                System.out.println("Reflog for 'feature-b-renamed' does not contain 'Created from feature-a'.");
            }

        } catch (Exception e) {
            System.out.println("Error in main method: " + e.getMessage());
        }
    }
}
