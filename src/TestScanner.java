import java.io.*;
import java.nio.file.*;

/**
 * TestScanner.java
 * Test driver for the ManualScanner - writes output to both console and output.txt
 */
public class TestScanner {

    public static void main(String[] args) {
        try (PrintWriter fileWriter = new PrintWriter(new FileWriter("../tests/TestResults.txt"))) {
            // Create a custom output stream that writes to both console and file
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            
            // Custom PrintStream that writes to both console and file
            PrintStream dualOut = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    originalOut.write(b);
                    fileWriter.write(b);
                    fileWriter.flush();
                }
            });
            
            PrintStream dualErr = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    originalErr.write(b);
                    fileWriter.write(b);
                    fileWriter.flush();
                }
            });
            
            // Redirect System.out and System.err
            System.setOut(dualOut);
            System.setErr(dualErr);
            
            // Test case 1: All valid tokens
            System.out.println("Running Test 1: All Valid Tokens...\n");
            testFromFile("../tests/test1.lang");

            // Test case 2: Complex expressions
            System.out.println("\n\nRunning Test 2: Complex Expressions...\n");
            testFromFile("../tests/test2.lang");

            // Test case 3: String/char with escapes
            System.out.println("\n\nRunning Test 3: String/Char Escapes...\n");
            testFromFile("../tests/test3.lang");

            // Test case 4: Lexical errors
            System.out.println("\n\nRunning Test 4: Lexical Errors...\n");
            testFromFile("../tests/test4.lang");

            // Test case 5: Comments
            System.out.println("\n\nRunning Test 5: Comments...\n");
            testFromFile("../tests/test5.lang");

            System.out.println("\n========================================");
            System.out.println("    ALL TESTS COMPLETED SUCCESSFULLY");
            System.out.println("========================================");
            
            // Restore original streams
            System.setOut(originalOut);
            System.setErr(originalErr);
            
        } catch (IOException e) {
            System.err.println("Error creating output file: " + e.getMessage());
        }
    }

    /**
     * Tests the scanner with input from a file
     */
    private static void testFromFile(String filename) {
        try {
            String content = Files.readString(Paths.get(filename));

            System.out.println("========== INPUT FROM: " + filename + " ==========");
            System.out.println(content);
            System.out.println("=" + "=".repeat(filename.length() + 28) + "\n");

            ManualScanner scanner = new ManualScanner(content);
            scanner.scan();

            scanner.printTokens();
            scanner.printStatistics();
            scanner.getSymbolTable().print();

            System.out.println("\n========== TEST COMPLETED ==========");

        } catch (IOException e) {
            System.err.println("Error reading file '" + filename + "': " + e.getMessage());
            System.err.println("Make sure the file exists in the current directory.");
        }
    }
}
