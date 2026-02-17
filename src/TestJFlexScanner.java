import java.io.*;
import java.nio.file.*;

/**
 * TestJFlexScanner.java
 * Test driver for the JFlexScanner - compares output with ManualScanner
 */
public class TestJFlexScanner {

    public static void main(String[] args) {
        try (PrintWriter fileWriter = new PrintWriter(new FileWriter("../tests/JFlexTestResults.txt"))) {
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            
            // Dual output to console and file
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
            
            System.setOut(dualOut);
            System.setErr(dualErr);
            
            System.out.println("========================================");
            System.out.println("    JFLEX SCANNER TEST");
            System.out.println("========================================\n");

            // Test JFlex Scanner
            testJFlexScanner("../tests/test1.lang");
            //testJFlexScanner("../tests/test2.lang");
            //testJFlexScanner("../tests/test3.lang");
            //testJFlexScanner("../tests/test4.lang");
            //testJFlexScanner("../tests/test5.lang");

            System.out.println("\n========================================");
            System.out.println("    JFLEX SCANNER TEST COMPLETED");
            System.out.println("========================================");
            
            System.setOut(originalOut);
            System.setErr(originalErr);
            
        } catch (IOException e) {
            System.err.println("Error creating output file: " + e.getMessage());
        }
    }

    private static void testJFlexScanner(String filename) {
        try {
            String content = Files.readString(Paths.get(filename));

            System.out.println("========== JFLEX INPUT FROM: " + filename + " ==========");
            System.out.println(content);
            System.out.println("=".repeat(filename.length() + 35) + "\n");

            // Create JFlex scanner
            JFlexScanner scanner = new JFlexScanner(new StringReader(content));
            
            // Scan all tokens
            scanner.scanAll();

            // Print results using same methods as ManualScanner
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