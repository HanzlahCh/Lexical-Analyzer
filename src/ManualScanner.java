import java.util.*;

/**
 * ManualScanner.java
 * A DFA-based lexical analyzer that recognizes tokens according to the
 * specification
 */
public class ManualScanner {

    private String input;
    private int position;
    private int line;
    private int column;
    private List<Token> tokens;
    private SymbolTable symbolTable;
    private ErrorHandler errorHandler;

    // Statistics
    private Map<TokenType, Integer> tokenCounts;
    private int totalTokens;
    private int linesProcessed;
    private int commentsRemoved;

    // Keywords set for quick lookup
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "start", "finish", "loop", "condition", "declare", "output",
            "input", "function", "return", "break", "continue", "else"));

    // Boolean literals
    private static final Set<String> BOOLEAN_LITERALS = new HashSet<>(Arrays.asList(
            "true", "false"));

    public ManualScanner(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.symbolTable = new SymbolTable();
        this.errorHandler = new ErrorHandler();
        this.tokenCounts = new HashMap<>();
        this.totalTokens = 0;
        this.linesProcessed = 1;
        this.commentsRemoved = 0;
    }

    /**
     * Main scanning method - tokenizes the entire input
     */
    public List<Token> scan() {
        while (position < input.length()) {
            // Try to match tokens in priority order
            if (matchMultiLineComment())
                continue;
            if (matchSingleLineComment())
                continue;
            if (matchWhitespace())
                continue;
            if (matchMultiCharOperator())
                continue;
            if (matchKeywordOrBooleanOrIdentifier())
                continue;
            if (matchFloatLiteral())
                continue;
            if (matchIntegerLiteral())
                continue;
            if (matchStringLiteral())
                continue;
            if (matchCharLiteral())
                continue;
            if (matchSingleCharOperator())
                continue;
            if (matchPunctuator())
                continue;

            // If nothing matches, it's an error
            handleError();
        }

        return tokens;
    }

    /**
     * Matches multi-line comments: #* ... *#
     */
    private boolean matchMultiLineComment() {
        if (peek(2).equals("#*")) {
            int startLine = line;
            int startCol = column;
            consume(2); // consume #*

            while (position < input.length()) {
                if (peek(2).equals("*#")) {
                    consume(2);
                    commentsRemoved++;
                    return true;
                }
                
                consume(1); // Let consume handle all line/column tracking
            }

            // Unclosed comment - treat as error but continue
            errorHandler.reportUnclosedComment(startLine, startCol);
            commentsRemoved++;
            return true;
        }
        return false;
    }

    /**
     * Matches single-line comments: ## ...
     */
    private boolean matchSingleLineComment() {
        if (peek(2).equals("##")) {
            while (position < input.length() && peek() != '\n') {
                consume(1);
            }
            commentsRemoved++;
            return true;
        }
        return false;
    }

    /**
     * Matches whitespace and updates line/column tracking
     */
    private boolean matchWhitespace() {
        char ch = peek();
        if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
            consume(1); // Let consume handle all position and line tracking
            return true;
        }
        return false;
    }

    /**
     * Matches multi-character operators: **, ==, !=, <=, >=, &&, ||, ++, --, +=,
     * -=, *=, /=
     */
    private boolean matchMultiCharOperator() {
        String twoChar = peek(2);
        int startLine = line;
        int startCol = column;

        // Arithmetic operators
        if (twoChar.equals("**")) {
            addToken(TokenType.ARITHMETIC_OP, "**", startLine, startCol);
            consume(2);
            return true;
        }

        // Relational operators
        if (twoChar.equals("==") || twoChar.equals("!=") ||
                twoChar.equals("<=") || twoChar.equals(">=")) {
            addToken(TokenType.RELATIONAL_OP, twoChar, startLine, startCol);
            consume(2);
            return true;
        }

        // Logical operators
        if (twoChar.equals("&&") || twoChar.equals("||")) {
            addToken(TokenType.LOGICAL_OP, twoChar, startLine, startCol);
            consume(2);
            return true;
        }

        // Increment/Decrement
        if (twoChar.equals("++")) {
            addToken(TokenType.INCREMENT_OP, "++", startLine, startCol);
            consume(2);
            return true;
        }

        if (twoChar.equals("--")) {
            addToken(TokenType.DECREMENT_OP, "--", startLine, startCol);
            consume(2);
            return true;
        }

        // Compound assignment
        if (twoChar.equals("+=") || twoChar.equals("-=") ||
                twoChar.equals("*=") || twoChar.equals("/=")) {
            addToken(TokenType.ASSIGNMENT_OP, twoChar, startLine, startCol);
            consume(2);
            return true;
        }

        return false;
    }

    /**
     * Matches keywords, boolean literals, or identifiers
     * Identifiers: [A-Z][a-z0-9_]{0,30}
     */
    private boolean matchKeywordOrBooleanOrIdentifier() {
        char first = peek();

        // Must start with a letter
        if (!Character.isLetter(first)) {
            return false;
        }

        int startLine = line;
        int startCol = column;

        // Build the lexeme
        StringBuilder lexeme = new StringBuilder();
        lexeme.append(first);
        consume(1);

        // Continue reading lowercase letters, digits, underscores
        // Spaces are only allowed for identifiers (which start with uppercase)
        while (position < input.length()) {
            char ch = peek();
            if (Character.isLowerCase(ch) || Character.isDigit(ch) || ch == '_') {
                lexeme.append(ch);
                consume(1);
            } else if (ch == ' ' && Character.isUpperCase(first)) {
                // Spaces only allowed in identifiers (which start with uppercase)
                lexeme.append(ch);
                consume(1);
            } else {
                break;
            }
        }

        // Trim trailing spaces from the word
        String word = lexeme.toString().stripTrailing();

        // Check length constraint (max 31 characters)
        if (word.length() > 31) {
            System.out.println("Warning: Identifier too long (max 31 chars) at Line: " +
                    startLine + ", Col: " + startCol + " - truncating");
            word = word.substring(0, 31);
        }

        // Priority: Keywords > Boolean > Identifiers
        if (KEYWORDS.contains(word)) {
            addToken(TokenType.KEYWORD, word, startLine, startCol);
            return true;
        }

        if (BOOLEAN_LITERALS.contains(word)) {
            addToken(TokenType.BOOLEAN_LITERAL, word, startLine, startCol);
            return true;
        }

        // Identifier must start with uppercase
        if (Character.isUpperCase(first)) {
            addToken(TokenType.IDENTIFIER, word, startLine, startCol);
            symbolTable.addSymbol(word, "IDENTIFIER", startLine, startCol);
            return true;
        }

        // If starts with lowercase but not keyword/boolean, report as invalid identifier
        errorHandler.reportInvalidIdentifier(word, startLine, startCol, 
                "Identifier '" + word + "' must start with an uppercase letter");
        return true; // Return true to consume the token and continue
    }

    /**
     * Matches floating-point literals: [+-]?[0-9]+\.[0-9]{1,6}([eE][+-]?[0-9]+)?
     */
    private boolean matchFloatLiteral() {
        int startPos = position;
        int startLine = line;
        int startCol = column;

        // Optional sign
        if (peek() == '+' || peek() == '-') {
            consume(1);
        }

        // Must have at least one digit before decimal point
        if (!Character.isDigit(peek())) {
            position = startPos;
            column = startCol;
            return false;
        }

        // Read digits before decimal
        while (position < input.length() && Character.isDigit(peek())) {
            consume(1);
        }

        // Must have decimal point
        if (peek() != '.') {
            position = startPos;
            column = startCol;
            return false;
        }
        consume(1); // consume '.'

        // Must have 1-6 digits after decimal
        int decimalDigits = 0;
        while (position < input.length() && Character.isDigit(peek()) && decimalDigits < 6) {
            consume(1);
            decimalDigits++;
        }

        if (decimalDigits < 1) {
            position = startPos;
            column = startCol;
            return false;
        }

        // Optional exponent
        if (peek() == 'e' || peek() == 'E') {
            int expStart = position;
            consume(1);

            // Optional sign
            if (peek() == '+' || peek() == '-') {
                consume(1);
            }

            // Must have at least one digit in exponent
            if (!Character.isDigit(peek())) {
                position = expStart;
                column = startCol + (expStart - startPos);
            } else {
                while (position < input.length() && Character.isDigit(peek())) {
                    consume(1);
                }
            }
        }

        String lexeme = input.substring(startPos, position);
        addToken(TokenType.FLOAT_LITERAL, lexeme, startLine, startCol);
        return true;
    }

    /**
     * Matches integer literals: [+-]?[0-9]+
     */
    private boolean matchIntegerLiteral() {
        int startPos = position;
        int startLine = line;
        int startCol = column;

        // Optional sign
        if (peek() == '+' || peek() == '-') {
            consume(1);
        }

        // Must have at least one digit
        if (!Character.isDigit(peek())) {
            position = startPos;
            column = startCol;
            return false;
        }

        // Read all digits
        while (position < input.length() && Character.isDigit(peek())) {
            consume(1);
        }

        String lexeme = input.substring(startPos, position);
        addToken(TokenType.INTEGER_LITERAL, lexeme, startLine, startCol);
        return true;
    }

    /**
     * Matches string literals: "([^"\\\n]|\\["\\ntr])*"
     */
    private boolean matchStringLiteral() {
        if (peek() != '"') {
            return false;
        }

        int startLine = line;
        int startCol = column;
        StringBuilder lexeme = new StringBuilder();
        lexeme.append('"');
        consume(1);

        while (position < input.length()) {
            char ch = peek();

            if (ch == '"') {
                lexeme.append('"');
                consume(1);
                addToken(TokenType.STRING_LITERAL, lexeme.toString(), startLine, startCol);
                return true;
            }

            if (ch == '\n') {
                errorHandler.reportUnterminatedString(lexeme.toString(), startLine, startCol);
                return false;
            }

            if (ch == '\\') {
                consume(1);
                lexeme.append('\\');

                if (position < input.length()) {
                    char escaped = peek();
                    if (escaped == '"' || escaped == '\\' || escaped == 'n' ||
                            escaped == 't' || escaped == 'r') {
                        lexeme.append(escaped);
                        consume(1);
                    } else {
                        System.out.println("Warning: Invalid escape sequence at Line: " +
                                line + ", Col: " + column + " - treating as literal characters");
                    }
                }
            } else {
                lexeme.append(ch);
                consume(1);
            }
        }

        errorHandler.reportUnterminatedString(lexeme.toString(), startLine, startCol);
        return false;
    }

    /**
     * Matches character literals: '([^'\\\n]|\\['\\ntr])'
     */
    private boolean matchCharLiteral() {
        if (peek() != '\'') {
            return false;
        }

        int startLine = line;
        int startCol = column;
        StringBuilder lexeme = new StringBuilder();
        lexeme.append('\'');
        consume(1);

        if (position >= input.length()) {
            errorHandler.reportUnterminatedChar(lexeme.toString(), startLine, startCol);
            return false;
        }

        char ch = peek();

        // Handle escape sequences
        if (ch == '\\') {
            lexeme.append('\\');
            consume(1);

            if (position < input.length()) {
                char escaped = peek();
                if (escaped == '\'' || escaped == '\\' || escaped == 'n' ||
                        escaped == 't' || escaped == 'r') {
                    lexeme.append(escaped);
                    consume(1);
                } else {
                    errorHandler.reportInvalidEscapeSequence("\\" + escaped, line, column);
                    return false;
                }
            }
        } else if (ch != '\'' && ch != '\n') {
            lexeme.append(ch);
            consume(1);
        } else {
            errorHandler.reportInvalidChar(lexeme.toString(), startLine, startCol, "invalid character in character literal");
            return false;
        }

        // Must have closing quote
        if (peek() == '\'') {
            lexeme.append('\'');
            consume(1);
            addToken(TokenType.CHAR_LITERAL, lexeme.toString(), startLine, startCol);
            return true;
        }

        errorHandler.reportUnterminatedChar(lexeme.toString(), startLine, startCol);
        return false;
    }

    /**
     * Matches single-character operators: +, -, *, /, %, <, >, !, =
     */
    private boolean matchSingleCharOperator() {
        char ch = peek();
        int startLine = line;
        int startCol = column;

        // Arithmetic
        if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%') {
            addToken(TokenType.ARITHMETIC_OP, String.valueOf(ch), startLine, startCol);
            consume(1);
            return true;
        }

        // Relational
        if (ch == '<' || ch == '>') {
            addToken(TokenType.RELATIONAL_OP, String.valueOf(ch), startLine, startCol);
            consume(1);
            return true;
        }

        // Logical NOT
        if (ch == '!') {
            addToken(TokenType.LOGICAL_OP, String.valueOf(ch), startLine, startCol);
            consume(1);
            return true;
        }

        // Assignment
        if (ch == '=') {
            addToken(TokenType.ASSIGNMENT_OP, String.valueOf(ch), startLine, startCol);
            consume(1);
            return true;
        }

        return false;
    }

    /**
     * Matches punctuators: ( ) { } [ ] , ; :
     */
    private boolean matchPunctuator() {
        char ch = peek();
        if (ch == '(' || ch == ')' || ch == '{' || ch == '}' ||
                ch == '[' || ch == ']' || ch == ',' || ch == ';' || ch == ':') {
            addToken(TokenType.PUNCTUATOR, String.valueOf(ch), line, column);
            consume(1);
            return true;
        }
        return false;
    }

    /**
     * Handles unrecognized characters
     */
    private void handleError() {
        char ch = peek();
        errorHandler.reportInvalidCharacter(ch, line, column);
        consume(1);
    }

    /**
     * Peeks at the current character without consuming it
     */
    private char peek() {
        if (position >= input.length()) {
            return '\0';
        }
        return input.charAt(position);
    }

    /**
     * Peeks at the next n characters
     */
    private String peek(int n) {
        if (position + n > input.length()) {
            return input.substring(position);
        }
        return input.substring(position, position + n);
    }

    /**
     * Consumes n characters and updates position and column
     */
    private void consume(int n) {
        for (int i = 0; i < n && position < input.length(); i++) {
            char ch = input.charAt(position);
            position++;
            if (ch == '\n') {
                line++;
                linesProcessed++;
                column = 1;
            } else {
                column++;
            }
        }
    }

    /**
     * Adds a token to the list and updates statistics
     */
    private void addToken(TokenType type, String lexeme, int line, int column) {
        tokens.add(new Token(type, lexeme, line, column));
        tokenCounts.put(type, tokenCounts.getOrDefault(type, 0) + 1);
        totalTokens++;
    }

    /**
     * Prints all tokens
     */
    public void printTokens() {
        System.out.println("\n========== TOKENS ==========");
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("============================\n");
    }

    /**
     * Prints statistics
     */
    public void printStatistics() {
        System.out.println("\n========== STATISTICS ==========");
        System.out.println("Total Tokens: " + totalTokens);
        System.out.println("Lines Processed: " + linesProcessed);
        System.out.println("Comments Removed: " + commentsRemoved);
        System.out.println("Errors Detected: " + errorHandler.getErrorCount());
        System.out.println("\nToken Count by Type:");
        System.out.println("-".repeat(40));

        // Sort by token type name for consistent output
        tokenCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Enum::name)))
                .forEach(entry -> {
                    System.out.printf("%-25s: %d\n", entry.getKey(), entry.getValue());
                });

        System.out.println("=".repeat(40));

        // Print error summary if there are errors
        if (errorHandler.hasErrors()) {
            errorHandler.printErrorSummary();
        }
    }

    /**
     * Gets the symbol table
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     * Gets all tokens
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Gets the error handler
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
}
