# UndefinedLang - Lexical Analyzer Project

## Language Name and File Extension

- **Language Name**: UndefinedLang
- **File Extension**: `.lang`

---

## Complete Keyword List with Meanings

UndefinedLang has **12 reserved keywords**:

| Keyword | Meaning |
|---------|---------|
| `start` | Marks the beginning of program execution |
| `finish` | Marks the end of a block or program |
| `loop` | Begins a loop construct |
| `condition` | Begins a conditional branching statement |
| `declare` | Declares a new variable |
| `output` | Prints/displays a value to the screen |
| `input` | Reads a value from the user |
| `function` | Declares a function |
| `return` | Returns a value from a function |
| `break` | Exits from a loop early |
| `continue` | Skips the rest of the current loop iteration |
| `else` | Alternative branch in a condition statement |

---

## Identifier Rules and Examples

- Must **start with an uppercase letter** (`A-Z`)
- Followed by any combination of **lowercase letters** (`a-z`), **digits** (`0-9`), **underscores** (`_`), or **spaces**
- **Maximum length**: 31 characters
- Identifiers starting with a lowercase letter are flagged as errors

**Formal Rule:**
```
IDENTIFIER := [A-Z] ([a-z] | [0-9] | '_' | ' ')* 
```

**Valid Examples:**
```
Count
With_underscore
Variable with spaces
Pi
Name
```

**Invalid Examples:**
```
invalid_name       ## Error: starts with lowercase
lowercase          ## Error: starts with lowercase
This is a very very very very long identifier name   ## Error: exceeds 31 characters
```

---

## Literal Formats with Examples

### Integer Literals
```
INTEGER_LITERAL := [0-9]+
```
**Examples:**
```
42
0
7
100
```

### Float Literals
Digits followed by a decimal point and 1–6 decimal digits, with optional scientific notation.
```
FLOAT_LITERAL := [0-9]+ '.' [0-9]{1,6} ([eE] [+-]? [0-9]+)?
```
**Examples:**
```
3.14
0.000001
1.5e10
2.0E-3
```

### String Literals
Double-quoted text with support for escape sequences.
```
STRING_LITERAL := '"' (CHAR | ESCAPE_SEQUENCE)* '"'
```
**Examples:**
```
"Hello World"
"Line1\nLine2"
"Col1\tCol2"
"Path\\to\\file"
"She said \"hello\""
""
```

### Character Literals
Single-quoted single character or escape sequence.
```
CHAR_LITERAL := ''' (CHAR | ESCAPE_SEQUENCE) '''
```
**Examples:**
```
'A'
'z'
'0'
'\n'
'\t'
'\\'
'\''
```

### Boolean Literals
```
BOOLEAN_LITERAL := 'true' | 'false'
```
**Examples:**
```
true
false
```

### Escape Sequences (for strings and characters)
| Sequence | Meaning |
|----------|---------|
| `\n` | Newline |
| `\t` | Tab |
| `\r` | Carriage return |
| `\\` | Backslash |
| `\"` | Double quote |
| `\'` | Single quote |

---

## Operator List with Precedence

Operators are listed from **highest** to **lowest** precedence:

| Precedence | Operator(s) | Type | Associativity |
|------------|-------------|------|---------------|
| 1 (highest) | `!`, unary `+`, unary `-` | Unary | Right-to-left |
| 2 | `**` | Exponentiation | Left-to-right |
| 3 | `*`, `/`, `%` | Multiplicative | Left-to-right |
| 4 | `+`, `-` | Additive | Left-to-right |
| 5 | `<`, `>`, `<=`, `>=` | Relational | Left-to-right |
| 6 | `==`, `!=` | Equality | Left-to-right |
| 7 | `&&` | Logical AND | Left-to-right |
| 8 (lowest) | `\|\|` | Logical OR | Left-to-right |

### Assignment Operators
| Operator | Meaning |
|----------|---------|
| `=` | Simple assignment |
| `+=` | Add and assign |
| `-=` | Subtract and assign |
| `*=` | Multiply and assign |
| `/=` | Divide and assign |

### Increment/Decrement Operators
| Operator | Meaning |
|----------|---------|
| `++` | Increment by 1 |
| `--` | Decrement by 1 |

### Punctuators
| Symbol | Usage |
|--------|-------|
| `(` `)` | Function parameters, grouping expressions |
| `{` `}` | Expression lists |
| `[` `]` | Array indexing |
| `,` | Parameter/argument separator |
| `;` | Statement separator / expression list separator |
| `:` | Function body delimiter |

---

## Comment Syntax

### Single-Line Comments
Start with `##` and continue to the end of the line.
```
## This is a single-line comment
declare Count = 0  ## Inline comment after code
```

### Multi-Line Comments
Start with `#*` and end with `*#`. Can span multiple lines.
```
#* This is a
   multi-line comment *#

#* Comment with code inside:
   declare X = 10
   loop condition X > 0
       X--
   finish
*#
```

---

## Sample Programs

### Sample Program 1: Basic Program Structure
Demonstrates keywords, declarations, loops, conditions, functions, and I/O.
```
start
    declare Count = 0
    declare Flag = true

    loop condition Count < 10
        Count++
        condition Count == 5
            break
        else
            continue
        finish
    finish

    function Add(Param1, Param2):
        return Param1 + Param2
    finish

    input Name
    output "Done"
finish
```

### Sample Program 2: Complex Expressions and Operator Precedence
Demonstrates arithmetic precedence, compound assignments, nested conditions, and array usage.
```
start
    declare Result = 2 + 3 * 4
    declare Nested = (2 + 3) * (4 - 1)
    declare Power expr = 2 ** 3 ** 2
    declare Mixed = 10 % 3 + 2 ** 4 - 1

    Result += 10
    Result -= 3
    Result *= 2
    Result /= 4
    Result++
    Result--

    condition Result > 0 && Result < 100 || Result == 0
        condition !false && true
            output "Nested condition"
        finish
    finish

    declare Index = 0
    loop condition Index < 10 && !false
        declare Temp = Index * 2 + 1
        declare Square = Index ** 2
        condition Temp % 2 == 0
            output Temp
        else
            condition Square > 50
                break
            finish
        finish
        Index++
    finish

    function Calculate(X, Y):
        declare Sum = X + Y
        declare Diff = X - Y
        declare Product = X * Y
        return Sum + Diff * Product
    finish

    declare Data[100]
    declare Matrix[10][10]
    declare A = 10
    declare B = 20
    declare C = 30
    declare Val = Data[A + B]
    declare Cell = Matrix[A * 2][B / 4]

    output A + B * C - 1
    output (A + B) * (C - A)
finish
```

### Sample Program 3: String and Character Literals with Escape Sequences
Demonstrates string/character handling and escape sequences.
```
start
    declare Empty = ""
    declare Hello = "Hello World"
    declare Digits = "12345"

    declare Newline str = "Line1\nLine2"
    declare Tab str = "Col1\tCol2"
    declare Backslash str = "Path\\to\\file"
    declare Quote str = "She said \"hello\""
    declare All escapes = "New\nTab\tReturn\rSlash\\Quote\""

    declare Path = "C:\\Users\\Documents\\file.txt"
    declare Dialog = "He said \"Hi\" and she said \"Bye\""

    declare Ch a = 'A'
    declare Ch z = 'z'
    declare Ch newline = '\n'
    declare Ch tab = '\t'
    declare Ch backslash = '\\'
    declare Ch quote = '\''

    condition Hello == "Hello World"
        output "Match found\n"
    finish

    output "Result:\t"
    output Hello
    output "\nDone\n"
finish
```

---

## Compilation and Execution Instructions

### Prerequisites
- **Java JDK** (for compiling and running)
- **JFlex 1.9.1** (only if regenerating the JFlex scanner)

### Steps

```bash
# Navigate to source directory
cd src

# Compile all Java files
javac *.java

# Run manual scanner tests
java TestScanner

# Run JFlex scanner tests
java TestJFlexScanner
```

### Regenerating JFlex Scanner (optional)
```bash
# Generate JFlexScanner.java from the specification file
jflex Scanner.flex

# Recompile
javac *.java
```

### Output Files
- Manual scanner results: `tests/TestResults.txt`
- JFlex scanner results: `tests/JFlexTestResults.txt`

---

## Team Members

|        Name        | Roll Number |
|--------------------|-------------|
| Hanzlah Mehmood Ch |   22I-0001  |
| Zain Imran         |   22I-0002  |

---

## Project Structure

```
src/                        Source code
├── ManualScanner.java          Hand-coded DFA-based lexical analyzer (641 lines)
├── JFlexScanner.java           JFlex-generated scanner (table-driven DFA, 97 states)
├── Scanner.flex                JFlex specification file (240 lines)
├── Token.java                  Token representation (type, lexeme, line, column)
├── TokenType.java              Token type enumeration
├── ErrorHandler.java           Centralized error detection and reporting
├── SymbolTable.java            Identifier storage and frequency tracking
├── TestScanner.java            Manual scanner test driver
└── TestJFlexScanner.java       JFlex scanner test driver

docs/                       Documentation
├── LanguageGrammar.txt         Complete grammar specification
└── Comparison.md               Detailed scanner comparison report

tests/                      Test files and results
├── test1.lang                  All valid tokens
├── test2.lang                  Complex expressions
├── test3.lang                  String/char with escapes
├── test4.lang                  Lexical errors
├── test5.lang                  Comments
├── TestResults.txt             Manual scanner output
└── JFlexTestResults.txt        JFlex scanner output
```
