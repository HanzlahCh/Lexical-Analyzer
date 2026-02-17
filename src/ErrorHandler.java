import java.util.*;

/**
 * ErrorHandler.java
 * Centralized error detection, reporting, and recovery for the lexical analyzer
 */
public class ErrorHandler {

    /**
     * Error types that can be detected during lexical analysis
     */
    public enum ErrorType {
        INVALID_CHARACTER, // @, $, etc.
        MALFORMED_FLOAT, // Multiple decimals, invalid format
        MALFORMED_STRING, // Unterminated string
        MALFORMED_CHAR, // Unterminated or invalid character literal
        INVALID_IDENTIFIER, // Wrong starting character or exceeding length
        UNCLOSED_COMMENT, // Unclosed multi-line comment
        INVALID_ESCAPE_SEQUENCE // Invalid escape sequence in string/char
    }

    /**
     * Represents a single lexical error
     */
    public static class LexicalError {
        private ErrorType type;
        private int line;
        private int column;
        private String lexeme;
        private String reason;

        public LexicalError(ErrorType type, int line, int column, String lexeme, String reason) {
            this.type = type;
            this.line = line;
            this.column = column;
            this.lexeme = lexeme;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return String.format("[%s] Line: %d, Col: %d, Lexeme: \"%s\" - %s",
                    type, line, column, lexeme, reason);
        }

        public ErrorType getType() {
            return type;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public String getLexeme() {
            return lexeme;
        }

        public String getReason() {
            return reason;
        }
    }

    private List<LexicalError> errors;
    private int errorCount;

    public ErrorHandler() {
        this.errors = new ArrayList<>();
        this.errorCount = 0;
    }

    /**
     * Reports an error and adds it to the error list
     */
    public void reportError(ErrorType type, int line, int column, String lexeme, String reason) {
        LexicalError error = new LexicalError(type, line, column, lexeme, reason);
        errors.add(error);
        errorCount++;

        // Print error immediately for user feedback
        System.err.println("Error: " + error.toString());
    }

    /**
     * Reports an invalid character error
     */
    public void reportInvalidCharacter(char ch, int line, int column) {
        reportError(ErrorType.INVALID_CHARACTER, line, column,
                String.valueOf(ch),
                "Character '" + ch + "' is not recognized by the scanner");
    }

    /**
     * Reports a malformed float literal error
     */
    public void reportMalformedFloat(String lexeme, int line, int column, String reason) {
        reportError(ErrorType.MALFORMED_FLOAT, line, column, lexeme, reason);
    }

    /**
     * Reports an unterminated string literal error
     */
    public void reportUnterminatedString(String lexeme, int line, int column) {
        reportError(ErrorType.MALFORMED_STRING, line, column, lexeme,
                "String literal is not properly terminated with closing quote");
    }

    /**
     * Reports an unterminated character literal error
     */
    public void reportUnterminatedChar(String lexeme, int line, int column) {
        reportError(ErrorType.MALFORMED_CHAR, line, column, lexeme,
                "Character literal is not properly terminated with closing quote");
    }

    /**
     * Reports an invalid character literal error
     */
    public void reportInvalidChar(String lexeme, int line, int column, String reason) {
        reportError(ErrorType.MALFORMED_CHAR, line, column, lexeme, reason);
    }

    /**
     * Reports an invalid identifier error
     */
    public void reportInvalidIdentifier(String lexeme, int line, int column, String reason) {
        reportError(ErrorType.INVALID_IDENTIFIER, line, column, lexeme, reason);
    }

    /**
     * Reports an unclosed multi-line comment error
     */
    public void reportUnclosedComment(int line, int column) {
        reportError(ErrorType.UNCLOSED_COMMENT, line, column, "#*...",
                "Multi-line comment started but never closed with *#");
    }

    /**
     * Reports an invalid escape sequence error
     */
    public void reportInvalidEscapeSequence(String sequence, int line, int column) {
        reportError(ErrorType.INVALID_ESCAPE_SEQUENCE, line, column, sequence,
                "Invalid escape sequence '" + sequence + "' - only \\n, \\t, \\r, \\\\, \\\", \\' are allowed");
    }

    /**
     * Returns all errors encountered
     */
    public List<LexicalError> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Returns the total number of errors
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Checks if any errors were encountered
     */
    public boolean hasErrors() {
        return errorCount > 0;
    }

    /**
     * Prints a summary of all errors
     */
    public void printErrorSummary() {
        if (!hasErrors()) {
            System.out.println("\n✓ No lexical errors detected.");
            return;
        }

        System.out.println("\n========== ERROR SUMMARY ==========");
        System.out.println("Total Errors: " + errorCount);
        System.out.println("===================================");

        // Group errors by type
        Map<ErrorType, Integer> errorsByType = new HashMap<>();
        for (LexicalError error : errors) {
            errorsByType.put(error.getType(),
                    errorsByType.getOrDefault(error.getType(), 0) + 1);
        }

        System.out.println("\nErrors by Type:");
        for (Map.Entry<ErrorType, Integer> entry : errorsByType.entrySet()) {
            System.out.printf("  %-25s: %d\n", entry.getKey(), entry.getValue());
        }
        System.out.println("=".repeat(40));
    }

    /**
     * Clears all errors (useful for testing)
     */
    public void clearErrors() {
        errors.clear();
        errorCount = 0;
    }
}
