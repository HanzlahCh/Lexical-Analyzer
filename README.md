# Lexical Analyzer Project

## Overview
This project implements two lexical analyzers for a custom programming language: a hand-coded manual scanner and an automated JFlex-generated scanner. Both scanners produce identical token streams and are performance-compared.

## Implementation

### Manual Scanner (ManualScanner.java)
- **Approach**: Hand-coded DFA-based lexical analysis
- **Features**: Custom state machine with lookahead support, integrated error handling
- **Lines of Code**: 641 lines
- **Key Components**:
  - State-driven tokenization with manual transitions
  - Keyword/identifier disambiguation 
  - Numeric literal parsing (integers, floats with scientific notation)
  - String/character literal handling with escape sequences
  - Comment processing (single-line `##`, multi-line `#* *#`)

### JFlex Scanner (JFlexScanner.java)
- **Approach**: Generated from JFlex specification file
- **Features**: Table-driven DFA with 97 minimized states
- **Generation**: Created from Scanner.flex (240 lines) using JFlex 1.9.1
- **Key Components**:
  - Macro-based pattern definitions
  - Regular expression rules for all token types
  - Automatic state optimization and conflict resolution

### Supporting Classes
- **Token.java**: Token representation with type, lexeme, line/column info
- **TokenType.java**: Enumeration of all token categories
- **ErrorHandler.java**: Centralized error reporting and tracking
- **SymbolTable.java**: Identifier storage and lookup

## Language Grammar

### Lexical Elements
- **Keywords**: `start`, `finish`, `loop`, `condition`, `declare`, `output`, `input`, `function`, `return`, `break`, `continue`, `else`
- **Identifiers**: Uppercase letter followed by lowercase letters, digits, underscores, or spaces (max 31 chars)
- **Literals**: Integers, floats (6 decimal places), strings, characters, booleans
- **Operators**: Arithmetic (`+`, `-`, `*`, `/`, `%`, `**`), relational, logical, assignment
- **Comments**: Single-line (`##`) and multi-line (`#* *#`)

### Program Structure
```
program := 'start' statement_list 'finish'
statement := declaration | assignment | control_flow | function | io

declaration := 'declare' IDENTIFIER '=' expression
control_flow := 'loop'/'condition' expression statement_list 'finish'
function := 'function' IDENTIFIER '(' params ')' ':' body 'finish'
```

## Testing and Validation

### Test Files
- **test1.lang**: All valid tokens (keywords, literals, operators, punctuators)
- **test2.lang**: Complex expressions with nested operations and precedence
- **test3.lang**: String and character literals with escape sequences
- **test4.lang**: Lexical errors (invalid characters, unterminated literals)
- **test5.lang**: Comment handling (single-line and multi-line)

### Results
- **Token Generation**: Both scanners produce identical token streams across all test cases
- **Error Detection**: Consistent error reporting by both implementations
- **Symbol Table**: Accurate identifier tracking and frequency counting
- **Performance**: JFlex scanner provides ~8% performance improvement over manual implementation
- **Test Coverage**: 5 comprehensive test files covering all language features

## Project Structure
```
src/                    - Source code directory
├── ManualScanner.java      - Hand-coded lexical analyzer
├── JFlexScanner.java       - Generated JFlex scanner
├── Scanner.flex           - JFlex specification file
├── Token.java            - Token representation class
├── TokenType.java        - Token type enumeration
├── ErrorHandler.java     - Centralized error management
├── SymbolTable.java      - Symbol table implementation
├── TestScanner.java      - Manual scanner test driver
├── TestJFlexScanner.java - JFlex scanner test driver
└── TestErrorHandler.java - Error handler validation

docs/                   - Documentation directory
├── LanguageGrammar.txt    - Complete grammar specification
├── Comparison.md         - Detailed scanner comparison
└── Comparison.pdf        - PDF version of comparison

tests/                  - Test files and results
├── test1.lang           - All valid tokens
├── test2.lang           - Complex expressions
├── test3.lang           - String/char with escapes
├── test4.lang           - Lexical errors
├── test5.lang           - Comments
├── TestResults.txt      - Manual scanner output
└── JFlexTestResults.txt - JFlex scanner output
```

## Key Features
- **Identical Output**: Both scanners generate equivalent token streams
- **Error Recovery**: Robust error handling with continued parsing
- **Performance**: Automated generation provides speed advantage
- **Flexibility**: Manual implementation allows custom optimizations
- **Validation**: Comprehensive testing ensures correctness

## Compilation and Execution
```bash
# Navigate to source directory
cd src

# Compile all Java files
javac *.java

# Run manual scanner tests
java TestScanner

# Run JFlex scanner tests
java TestJFlexScanner

# Regenerate JFlex scanner 
jflex Scanner.flex
javac *.java
```

**Output Files:**
- Manual scanner results: `../tests/TestResults.txt`
- JFlex scanner results: `../tests/JFlexTestResults.txt`

This project demonstrates both manual and automated approaches to lexical analysis, providing insights into compiler construction techniques and performance trade-offs.
