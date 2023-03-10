import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Name:       Jarrad Self
 * Instructor:   Sharon Perry
 * Project:     Deliverable 2 Parser
 **/

public class Main {
    static boolean errorFound = false;

    public static void main(String[] args) throws IOException {
        //runFile("src\\juliaFiles\\Test1.jl");
        //runFile("src\\juliaFiles\\Test2.jl");
        runFile("src\\juliaFiles\\Test3.jl");
    }


    private static void runFile(String filePath) throws IOException {
        // Read the source code from the file
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        // Send the code to the lexical analyzer
        run(new String(bytes, Charset.defaultCharset()));
        if (errorFound) System.exit(39);
    }

    private static void run(String sourceCode) {
        // Create an instance of the scanner based on the source code
        LexicalAnalyzer scanner = new LexicalAnalyzer(sourceCode);
        // Scan the source code and generate tokens
        List<Token> tokens = scanner.scanTokens();

         /*Create an instance of the parser based on the list of tokens
         generated by the scanner*/
        SyntaxAnalyzer parser = new SyntaxAnalyzer(tokens);

        /* Initialize the first element of the parser's list of keywords with
         the first token from the scanner's list of tokens*/
        parser.getListOfKeywords().add(tokens.get(0));

         /*Invoke the parser's method for parsing and store the list of statements
         generated from the parsing process*/
        List<Statement> statements = parser.parse();

        // Stop if there was a syntax error.
        if (errorFound) return;


        // Print the table containing lexemes, tokens, and their associated opCode
        System.out.printf("%-25s %-30s %-30s", "Lexeme", "Token", "OpCode");
        System.out.println("\n----------------------------------------------------------------");
        for (Token token : tokens) {
            System.out.printf("%-25s %-30s %-30s\n", token.getLexeme(), token.getType(), token.getType().ordinal());
        }
        System.out.println("\n------------------------ End of Test File -----------------------");

        System.out.println("\n");
        System.out.println("Parse Tree");
        System.out.println("function " + parser.getFunctionName() + "()");

        // print the parse tree for the different statements
        for (Statement statement : statements
        ) {
            System.out.println(new ParseTreePrinter().print(statement));
        }

        System.out.println("end");

    }

    // Generate and print an error encountered during the scanning process
    static void generateError(int line, String errorMessage) {
        printError(line, "", errorMessage);
    }

    private static void printError(int line, String where, String errorMessage) {
        System.err.println("(line number " + line + ") Error" + where + ": " + errorMessage);
        errorFound = true;
    }

    // Generate and print a syntax error encountered during the parsing process
    static void generateSyntaxError(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            printError(token.getLine(), " at end", message);
        } else {
            printError(token.getLine(), " at '" + token.getLexeme() + "'", message);
        }
    }
}