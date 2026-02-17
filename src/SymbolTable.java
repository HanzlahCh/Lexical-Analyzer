import java.util.*;

/**
 * SymbolTable.java
 * Stores information about identifiers encountered during scanning
 */
public class SymbolTable {
    
    public static class SymbolInfo {
        private String name;
        private String type;
        private int firstLine;
        private int firstColumn;
        private int frequency;
        
        public SymbolInfo(String name, String type, int firstLine, int firstColumn) {
            this.name = name;
            this.type = type;
            this.firstLine = firstLine;
            this.firstColumn = firstColumn;
            this.frequency = 1;
        }
        
        public void incrementFrequency() {
            this.frequency++;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public int getFirstLine() {
            return firstLine;
        }
        
        public int getFirstColumn() {
            return firstColumn;
        }
        
        public int getFrequency() {
            return frequency;
        }
        
        @Override
        public String toString() {
            return String.format("%-20s %-15s Line: %-4d Col: %-4d Frequency: %d", 
                               name, type, firstLine, firstColumn, frequency);
        }
    }
    
    private Map<String, SymbolInfo> table;
    
    public SymbolTable() {
        this.table = new LinkedHashMap<>();
    }
    
    /**
     * Adds an identifier to the symbol table or increments its frequency if it already exists
     */
    public void addSymbol(String name, String type, int line, int column) {
        if (table.containsKey(name)) {
            table.get(name).incrementFrequency();
        } else {
            table.put(name, new SymbolInfo(name, type, line, column));
        }
    }
    
    /**
     * Checks if an identifier exists in the symbol table
     */
    public boolean contains(String name) {
        return table.containsKey(name);
    }
    
    /**
     * Gets symbol information for a given identifier
     */
    public SymbolInfo getSymbol(String name) {
        return table.get(name);
    }
    
    /**
     * Returns all symbols in the table
     */
    public Collection<SymbolInfo> getAllSymbols() {
        return table.values();
    }
    
    /**
     * Prints the symbol table in a formatted manner
     */
    public void print() {
        System.out.println("\n========== SYMBOL TABLE ==========");
        System.out.println(String.format("%-20s %-15s %-10s %-10s %s", 
                                        "Name", "Type", "First Line", "First Col", "Frequency"));
        System.out.println("=".repeat(80));
        
        if (table.isEmpty()) {
            System.out.println("No identifiers found.");
        } else {
            for (SymbolInfo symbol : table.values()) {
                System.out.println(symbol);
            }
        }
        
        System.out.println("=".repeat(80));
        System.out.println("Total unique identifiers: " + table.size());
    }
}
