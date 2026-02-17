/* Scanner.flex
 * JFlex specification for the lexical analyzer
 * Recognizes the same tokens as the ManualScanner with identical priority
 */

/* ==================== USER CODE SECTION ==================== */
%%

%class JFlexScanner
%unicode
%line
%column
%public
%type Token

%{
    /* ---- Imports and helper fields ---- */
    private java.util.List<Token> tokens = new java.util.ArrayList<>();
    private SymbolTable symbolTable = new SymbolTable();
    private ErrorHandler errorHandler = new ErrorHandler();

    /* Statistics */
    private java.util.Map<TokenType, Integer> tokenCounts = new java.util.HashMap<>();
    private int totalTokens = 0;
    private int commentsRemoved = 0;

    /* ---- Helper methods ---- */

    /** Adds a token and updates statistics */
    private Token addToken(TokenType type, String lexeme) {
        Token t = new Token(type, lexeme, yyline + 1, yycolumn + 1);
        tokens.add(t);
        tokenCounts.put(type, tokenCounts.getOrDefault(type, 0) + 1);
        totalTokens++;
        return t;
    }

    /** Scans the entire input and returns all tokens */
    public java.util.List<Token> scanAll() throws java.io.IOException {
        Token t;
        while ((t = yylex()) != null) {
            // tokens are already added in addToken()
        }
        return tokens;
    }

    /** Returns all tokens after scanning */
    public java.util.List<Token> getTokens() { return tokens; }

    /** Returns the symbol table */
    public SymbolTable getSymbolTable() { return symbolTable; }

    /** Returns the error handler */
    public ErrorHandler getErrorHandler() { return errorHandler; }

    /** Prints all tokens */
    public void printTokens() {
        System.out.println("\n========== TOKENS ==========");
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("============================\n");
    }

    /** Prints statistics */
    public void printStatistics() {
        System.out.println("\n========== STATISTICS ==========");
        System.out.println("Total Tokens: " + totalTokens);
        System.out.println("Lines Processed: " + (yyline + 1));
        System.out.println("Comments Removed: " + commentsRemoved);
        System.out.println("Errors Detected: " + errorHandler.getErrorCount());
        System.out.println("\nToken Count by Type:");
        System.out.println("-".repeat(40));

        tokenCounts.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey(java.util.Comparator.comparing(Enum::name)))
                .forEach(entry -> {
                    System.out.printf("%-25s: %d\n", entry.getKey(), entry.getValue());
                });

        System.out.println("=".repeat(40));

        if (errorHandler.hasErrors()) {
            errorHandler.printErrorSummary();
        }
    }
%}

/* ==================== MACRO DEFINITIONS ==================== */

/* Basic character classes */
DIGIT       = [0-9]
UPPER       = [A-Z]
LOWER       = [a-z]
LETTER      = [a-zA-Z]
UNDERSCORE  = _

/* Whitespace */
LINETERM    = \r|\n|\r\n
WHITESPACE  = [ \t\r\n]

/* Keywords */
KEYWORD     = "start"|"finish"|"loop"|"condition"|"declare"|"output"|"input"|"function"|"return"|"break"|"continue"|"else"

/* Boolean literals */
BOOLEAN     = "true"|"false"

/* Identifiers: starts with uppercase, followed by lowercase/digits/underscores/spaces */
IDENT_START = {UPPER}
IDENT_BODY  = ({LOWER}|{DIGIT}|{UNDERSCORE}|" ")*

/* Integer literal */
INTEGER     = {DIGIT}+

/* Float literal: digits.digits(1-6)(optional exponent) */
FLOAT       = {DIGIT}+\.{DIGIT}{1,6}([eE][+-]?{DIGIT}+)?

/* String literal: double-quoted with escape sequences */
STRING_CHAR = [^\"\\\n]
STRING_ESC  = \\[\"\\ntr]
STRING      = \"({STRING_CHAR}|{STRING_ESC})*\"

/* Character literal: single-quoted with escape sequences */
CHAR_BODY   = [^'\\\n]
CHAR_ESC    = \\['\\ntr]
CHAR        = '({CHAR_BODY}|{CHAR_ESC})'

/* Comments */
SINGLE_COMMENT = "##"[^\n]*
MULTI_COMMENT  = "#*"([^*]|\*+[^#*])*\*+"#"

/* ==================== LEXICAL RULES ==================== */
%%

/* --- Comments (highest priority) --- */
{MULTI_COMMENT}         { commentsRemoved++; /* skip - return null to continue */ }
"#*"([^*]|\*+[^#*])*   { 
                            commentsRemoved++; 
                            errorHandler.reportUnclosedComment(yyline + 1, yycolumn + 1); 
                            /* skip - return null to continue */
                        }
{SINGLE_COMMENT}        { commentsRemoved++; /* skip - return null to continue */ }

/* --- Whitespace --- */
{WHITESPACE}            { /* skip - return null to continue */ }

/* --- Multi-character operators (before single-char to ensure longest match) --- */
"**"                    { return addToken(TokenType.ARITHMETIC_OP, yytext()); }
"=="                    { return addToken(TokenType.RELATIONAL_OP, yytext()); }
"!="                    { return addToken(TokenType.RELATIONAL_OP, yytext()); }
"<="                    { return addToken(TokenType.RELATIONAL_OP, yytext()); }
">="                    { return addToken(TokenType.RELATIONAL_OP, yytext()); }
"&&"                    { return addToken(TokenType.LOGICAL_OP, yytext()); }
"||"                    { return addToken(TokenType.LOGICAL_OP, yytext()); }
"++"                    { return addToken(TokenType.INCREMENT_OP, yytext()); }
"--"                    { return addToken(TokenType.DECREMENT_OP, yytext()); }
"+="                    { return addToken(TokenType.ASSIGNMENT_OP, yytext()); }
"-="                    { return addToken(TokenType.ASSIGNMENT_OP, yytext()); }
"*="                    { return addToken(TokenType.ASSIGNMENT_OP, yytext()); }
"/="                    { return addToken(TokenType.ASSIGNMENT_OP, yytext()); }

/* --- Keywords and Boolean literals (before identifiers) --- */
{KEYWORD}               { 
                            /* Check if it's followed by a delimiter to avoid partial matches */
                            String word = yytext();
                            return addToken(TokenType.KEYWORD, word); 
                        }
{BOOLEAN}               { 
                            String word = yytext(); 
                            return addToken(TokenType.BOOLEAN_LITERAL, word); 
                        }

/* --- Identifiers (uppercase start, may contain spaces) --- */
{IDENT_START}{IDENT_BODY}  {
                                String word = yytext().stripTrailing();
                                if (word.length() > 31) {
                                    System.out.println("Warning: Identifier too long (max 31 chars) at Line: " +
                                            (yyline + 1) + ", Col: " + (yycolumn + 1) + " - truncating");
                                    word = word.substring(0, 31);
                                }
                                symbolTable.addSymbol(word, "IDENTIFIER", yyline + 1, yycolumn + 1);
                                return addToken(TokenType.IDENTIFIER, word);
                            }

/* --- Invalid identifier (starts with lowercase, not keyword/boolean) --- */
{LOWER}({LOWER}|{DIGIT}|{UNDERSCORE})*  { 
                                String word = yytext();
                                /* Check if it is a keyword or boolean that wasn't caught above */
                                java.util.Set<String> kw = new java.util.HashSet<>(java.util.Arrays.asList(
                                    "start","finish","loop","condition","declare","output",
                                    "input","function","return","break","continue","else"));
                                java.util.Set<String> bl = new java.util.HashSet<>(java.util.Arrays.asList("true","false"));
                                if (kw.contains(word)) {
                                    return addToken(TokenType.KEYWORD, word);
                                } else if (bl.contains(word)) {
                                    return addToken(TokenType.BOOLEAN_LITERAL, word);
                                } else {
                                    errorHandler.reportInvalidIdentifier(word, yyline + 1, yycolumn + 1,
                                        "Identifier '" + word + "' must start with an uppercase letter");
                                    /* continue scanning after error */
                                }
                            }

/* --- Float literals (before integer to ensure longest match) --- */
{FLOAT}                 { return addToken(TokenType.FLOAT_LITERAL, yytext()); }

/* --- Integer literals --- */
{DIGIT}+                { return addToken(TokenType.INTEGER_LITERAL, yytext()); }

/* --- String literals --- */
{STRING}                { return addToken(TokenType.STRING_LITERAL, yytext()); }
\"([^\"\\\n]|{STRING_ESC})*  {
                            errorHandler.reportUnterminatedString(yytext(), yyline + 1, yycolumn + 1);
                            /* continue scanning after error */
                        }

/* --- Character literals --- */
{CHAR}                  { return addToken(TokenType.CHAR_LITERAL, yytext()); }
'([^'\\\n]|{CHAR_ESC})  {
                            errorHandler.reportUnterminatedChar(yytext(), yyline + 1, yycolumn + 1);
                            /* continue scanning after error */
                        }
'                       {
                            errorHandler.reportUnterminatedChar("'", yyline + 1, yycolumn + 1);
                            /* continue scanning after error */
                        }

/* --- Single-character operators --- */
"+"                     { return addToken(TokenType.ARITHMETIC_OP, yytext()); }
"-"                     { return addToken(TokenType.ARITHMETIC_OP, yytext()); }
"*"                     { return addToken(TokenType.ARITHMETIC_OP, yytext()); }
"/"                     { return addToken(TokenType.ARITHMETIC_OP, yytext()); }
"%"                     { return addToken(TokenType.ARITHMETIC_OP, yytext()); }
"<"                     { return addToken(TokenType.RELATIONAL_OP, yytext()); }
">"                     { return addToken(TokenType.RELATIONAL_OP, yytext()); }
"!"                     { return addToken(TokenType.LOGICAL_OP, yytext()); }
"="                     { return addToken(TokenType.ASSIGNMENT_OP, yytext()); }

/* --- Punctuators --- */
"("                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
")"                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
"{"                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
"}"                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
"["                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
"]"                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
","                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
";"                     { return addToken(TokenType.PUNCTUATOR, yytext()); }
":"                     { return addToken(TokenType.PUNCTUATOR, yytext()); }

/* --- Catch-all for unrecognized characters (error handling) --- */
[^]                     { 
                            errorHandler.reportInvalidCharacter(yytext().charAt(0), yyline + 1, yycolumn + 1); 
                            /* continue scanning after error */
                        }
