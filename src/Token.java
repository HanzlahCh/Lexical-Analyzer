/**
 * Token.java
 * Represents a lexical token with its type, lexeme, and position information.
 * Compatible with both ManualScanner and JFlexScanner implementations.
 */
public class Token {
    private TokenType type;
    private String lexeme;
    private int line;
    private int column;

    /**
     * Creates a new Token.
     * @param type   The TokenType category
     * @param lexeme The matched text
     * @param line   1-based line number
     * @param column 1-based column number
     */
    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Checks equality based on type and lexeme (ignores position).
     * Useful for comparing tokens produced by ManualScanner vs JFlexScanner.
     */
    public boolean equalsToken(Token other) {
        if (other == null) return false;
        return this.type == other.type && this.lexeme.equals(other.lexeme);
    }

    @Override
    public String toString() {
        return String.format("<%s, \"%s\", Line: %d, Col: %d>",
                type, lexeme, line, column);
    }
}
