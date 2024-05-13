import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Model extends Observable implements IModel {
    private static final String FILE_PATH = "equations.txt";
    private List<String> equations;
    private List<String> targetEquation;
    private int remainingAttempts;
    private boolean gameWon;
    private Set<Character> excludedSet = new HashSet<>(); // A collection of used characters
    private final Set<Character> greenSet = new HashSet<>();
    // Collection to store characters marked as orange
    private final Set<Character> orangeSet = new HashSet<>();
    // Collection to store characters marked as dark gray
    private final Set<Character> darkGraySet = new HashSet<>();
    private final Set<Character> colourlessSet = new HashSet<>();

    // Class Invariant: remainingAttempts is always non-negative.
    private boolean invariant1() {
        return remainingAttempts >= 0 && remainingAttempts <= MAX_ATTEMPTS;
    }
    // Class Invariant: equations list is never null.
    private boolean invariant2() {
        return equations != null && !equations.isEmpty();
    }

    // Class Invariant: targetEquation list is never null.
    private boolean invariant3() {
        return !targetEquation.isEmpty();
    }

    public Model() {
        initialize();
    }

    /**
     * @invariant invariant2()   // Class invariant: Ensure equations list is not empty
     * @ensures invariant2()     // Post-condition: Ensure equations list is not empty
     */
    @Override
    public void initialize() {
        equations = loadEquationsFromFile();
        assert invariant2();
        startNewGame();
    }

    // flag2
    /**
     * @invariant invariant3()   // Class invariant: Ensure targetEquation list is not empty after selection
     * @ensures invariant3()     // Post-condition: Ensure targetEquation list is not empty after selection
     */
    @Override
    public List<String> getTargetEquations() {
        selectRandomEquations();
        // Post-condition: Ensure that the returned list contains at least one equation
        assert invariant3();
        return targetEquation;
    }

    // flag3
    /**
     * @invariant invariant3()   // Class invariant: Ensure targetEquation list is not empty after selection
     * @ensures invariant3()     // Post-condition: Ensure targetEquation list is not empty after selection
     */
    @Override
    public List<String> getFixedTargetEquations() {
        targetEquation = new ArrayList<>();
        // Add a fixed equation to the list
        targetEquation.add("5+2-3=4");
        // Post-condition: Returns a list containing a fixed target equation.
        assert invariant3();
        return targetEquation;
    }

    // flag3
    /**
     * @invariant invariant2()   // Class invariant: Ensure equations list is not empty
     * @requires invariant2()    // Pre-condition: Ensure equations list is not empty
     * @ensures invariant3()     // Post-condition: Ensure targetEquation list is not empty after selection
     */
    // Post-condition: Selects a random equation from the equations list as the target equation.
    @Override
    public void selectRandomEquations() {
        // Pre-condition: Ensure that the equations list is not empty
        assert invariant2();
        Random random = new Random();
        int index = random.nextInt(equations.size());
        targetEquation = new ArrayList<>();
        targetEquation.add(equations.get(index));
        // Post-condition: Ensure that the targetEquation list is not empty after selection
        assert invariant3();
    }

    // Import equations file
    private List<String> loadEquationsFromFile() {
        List<String> equations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                equations.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return equations;

    }

    // Reduce the number of attempts by one
    /**
     * @requires invariant1()  // Pre-condition: remainingAttempts is always non-negative.
     * @ensures invariant1()  // Post-condition: remainingAttempts is always non-negative.
     */
    @Override
    public void decreaseAttemptsRemaining() {
        assert invariant1();
        remainingAttempts--;
        assert invariant1();
    }

    // Pre-condition: None
    // Post-condition: Returns the remaining attempts.
    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    // Reset the remaining attempts to 6
    // Pre-condition: None
    // Post-condition: Resets the remaining attempts to the maximum allowed attempts.
    @Override
    public void resetRemainingAttempts(){
        remainingAttempts = MAX_ATTEMPTS;
    }


    /**
     * @invariant invariant2()   // Class invariant: Ensure that the equations list is not empty
     * @requires invariant2()    // Pre-condition: Ensure that the equations list is not empty
     */
    @Override
    public void startNewGame() {
        // Pre-condition: Equations list is not empty.
        assert invariant2();
        targetEquation = getTargetEquations();
        // remainingAttempts = MAX_ATTEMPTS;
        resetRemainingAttempts();
        gameWon = false;
        excludedSet = new HashSet<>(); // Refreshes the used character collection

        setChanged();
        notifyObservers();
    }

    // Pre-condition: None
    // Post-condition: Returns true if the game is over (remaining attempts are zero or game is won), otherwise false.
    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    // Pre-condition: None
    // Post-condition: Returns true if the game is won, otherwise false.
    @Override
    public boolean isGameWon() {
        return gameWon;
    }


    // Process the input equations
    @Override
    public void processInput(String input, String target) {
        // Pre-condition: Ensure that input and target equations are not null
        assert input != null && target != null : "Input and target equations cannot be null";
        String feedback = provideFeedback(input, target);
        System.out.println("\nFeedback of the current equation: " + feedback);
        decreaseAttemptsRemaining();

        // Post-condition: Processes the input equations, provides feedback, decreases attempts, and notifies observers.
        setChanged();
        notifyObservers();
    }

    // Pre-condition: None
    // Post-condition: Notifies the observer object and passes a parameter.
    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }


    // Compare feedback against the target equation and the input equation
    @Override
    public String provideFeedback(String guess, String target) {
        assert guess != null && target != null : "Guess and target equations cannot be null";
        StringBuilder feedback = new StringBuilder();
        // Characters to include in coloring
        String includedChars = "0123456789+-*/";

        // Define colors
        String green = "(green: ";
        String orange = "(orange: ";
        String darkGray = "(darkgray: ";
        String colourless = "(colourless: ";

        // Check for characters not in the allowed set and not excluded
        feedback.append("\n").append(colourless);
        for (char c : includedChars.toCharArray()) {
            if (!excludedSet.contains(c) && !guess.contains(String.valueOf(c))) {
                feedback.append(c);
                colourlessSet.add(c); // Add to colourless set
            }
        }

        feedback.append(" )\n");

        // Process feedback for each character in guess and target
        for (int i = 0; i < Math.min(guess.length(), target.length()); i++) {
            char guessChar = guess.charAt(i);
            char targetChar = target.charAt(i);

            // If guess and target have the same character in the same position, use green for this character
            if (guessChar == targetChar) {
                feedback.append(green).append(guessChar).append(")");
                excludedSet.add(guessChar); // Mark as excluded
                greenSet.add(guessChar); // Add to green set
            }
            // If the target string contains the character guess in the current position, but in a different position, this character is colored orange
            else if (target.contains(String.valueOf(guessChar))) {
                feedback.append(orange).append(guessChar).append(")");
                excludedSet.add(guessChar); // Mark as excluded
                orangeSet.add(guessChar); // Add to orange set
            }
            // If the character is not an included character, use dark gray
            else if (includedChars.contains(String.valueOf(guessChar))) {
                feedback.append(darkGray).append(guessChar).append(")");
                excludedSet.add(guessChar); // Mark as excluded
                darkGraySet.add(guessChar); // Add to darkGray set
            }
            // If the character is not included, use colourless
            else {
                feedback.append(colourless).append(guessChar).append(")");
            }
        }

        orangeSet.removeAll(greenSet);
        // Remove characters present in greenSet, orangeSet, and darkGraySet from colourlessSet
        colourlessSet.removeAll(greenSet);
        colourlessSet.removeAll(orangeSet);
        colourlessSet.removeAll(darkGraySet);

        // Print list of colors
        System.out.println("\nColor list of all characters:");
        System.out.println("Characters in the correct position(Green): " + greenSet);
        System.out.println("Characters in the wrong position(Orange): " + orangeSet);
        System.out.println("Nonexistent characters(Dark Gray): " + darkGraySet);
        System.out.println("Unused characters(Colourless): " + colourlessSet);
        // Post-condition: Provides feedback based on the input guess and the target equation.
        return feedback.toString();
    }

    // flag1: Verify that the entered equation is a valid one
    // Pre-condition: rowInput and targetEquation cannot be null.
    // Post-condition: Returns a message indicating the validity of the input equation.
    @Override
    public String validateEquation(String rowInput, String targetEquation) {
        // Pre-condition: Ensure that rowInput and targetEquation are not null
        assert rowInput != null && targetEquation != null : "Input and target equations cannot be null";

        if (rowInput.length() < 7) {
            return "Too short";
        }

        if (rowInput.length() > 7){
            return "Too long";
        }

        if (!rowInput.contains("=")) {
            return "No equal sign in the equation";
        }

        if (rowInput.matches(".*[+\\-*/]{2,}.*")) {
            return "Multiple math symbols in a row";
        }

        if (!rowInput.matches(".*[+\\-*/].*")) {
            return "There is at least one sign +-*/";
        }

        String[] sides = rowInput.split("=");
        String leftSide = sides[0].trim();
        String rightSide = sides[1].trim();

        // Check if the equation is logically correct
        double leftResult = evaluateExpression(leftSide);
        double rightResult = evaluateExpression(rightSide);

        if (leftResult != rightResult) {
            return "Right side is not equal to left side";
        }

        if (!rowInput.equals(targetEquation)) {
            return "Incorrect! Please try again.";
        }

        return "Correct! You've guessed the equation correctly.";

    }

    // Helper method to evaluate arithmetic expressions
    private double evaluateExpression(String expression) {
        try {
            return new Object() {
                double eval(String expression) {
                    return new Object() {
                        int pos = -1, ch;
                        void nextChar() {
                            ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                        }
                        boolean eat(int charToEat) {
                            while (ch == ' ') nextChar();
                            if (ch == charToEat) {
                                nextChar();
                                return true;
                            }
                            return false;
                        }

                        double parse() {
                            nextChar();
                            double x = parseExpression();
                            // Post-condition: If there are more characters in the expression after parsing, throw an exception.
                            if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                            return x;
                        }

                        double parseExpression() {
                            double x = parseTerm();
                            for (; ; ) {
                                if (eat('+')) x += parseTerm(); // addition
                                else if (eat('-')) x -= parseTerm(); // subtraction
                                else return x;
                            }
                        }

                        double parseTerm() {
                            double x = parseFactor();
                            for (; ; ) {
                                if (eat('*')) x *= parseFactor(); // multiplication
                                else if (eat('/')) x /= parseFactor(); // division
                                else return x;
                            }
                        }

                        double parseFactor() {
                            if (eat('+')) return parseFactor(); // unary plus
                            if (eat('-')) return -parseFactor(); // unary minus

                            double x;
                            int startPos = this.pos;
                            if (eat('(')) { // parentheses
                                x = parseExpression();
                                eat(')');
                            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                                x = Double.parseDouble(expression.substring(startPos, this.pos));
                            } else {
                                throw new RuntimeException("Unexpected: " + (char) ch);
                            }

                            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                            return x;
                        }
                    }.parse();
                }
            }.eval(expression);
        } catch (Exception e) {
            // If an exception occurs during evaluation, return NaN.
            return Double.NaN; // Invalid expression
        }
    }
}
