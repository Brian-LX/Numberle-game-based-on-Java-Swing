import java.util.List;
import java.util.Scanner;

public class CLIApp {
    private final IModel model;
    private final Scanner scanner;

    public CLIApp() {
        model = new Model();
        scanner = new Scanner(System.in);
    }

    public void start() {
        // Print the game rules
        System.out.println("\nWelcome to the Equation Guessing Game!");
        System.out.println("The Rules: Please use the 1, 2, 3, +, 4, 5, 6, -, 7, 8 and 9, *, 0, /, = character to express the equation. If the number or symbol does not appear in the mathematical equation, it is shown in dark gray; If the correct position of the number or symbol is found, it is shown in green; If the correct position of the number or symbol is guessed, it is shown in orange");
        System.out.println("You have " + model.getRemainingAttempts() + " attempts to guess the right equation.");

        while (true) {
            playGame();
            // Decide whether to play again
            System.out.print("Do you want to play again? (yes/no): ");
            String playAgain = scanner.nextLine().trim().toLowerCase();

            if (!playAgain.equals("yes")) {
                System.out.println("Thanks for playing!");
                break;
            }
        }

        scanner.close();
    }

    private void playGame() {
        model.startNewGame();
        // Set flag3
        System.out.print("flag3 → Do you want to randomly select a target equation in the list? (yes/no): ");
        String flag3 = scanner.nextLine().trim().toLowerCase();
        System.out.print("flag2 → Whether you want to display the target equation? (yes/no): ");
        // Set flag2
        String flag2 = scanner.nextLine().trim().toLowerCase();
        // Two kinds of target equations of random selection and fixed selection
        List<String> targetEquation = model.getTargetEquations();
        List<String> FixedTargetEquation = model.getFixedTargetEquations();

        if (flag2.equals("yes")) {
            if (flag3.equals("yes")){
                // Print out the target equation
                System.out.println("The target equation is randomly chosen as: " + targetEquation.get(0));
            }else{
                System.out.println("This target equation is fixed as: " + FixedTargetEquation.get(0));
            }
        }

        // Set flag1
        System.out.print("flag1 → Do you want to set invalid equations to display error messages? (yes/no): ");
        String flag1 = scanner.nextLine().trim().toLowerCase();

        while (!model.isGameOver()) {
            // Displays the remaining number of attempts
            System.out.println("Attempts remaining: " + model.getRemainingAttempts());

            // Enter the guessed equation
            System.out.print("Enter your guess equation: ");
            String guess = scanner.nextLine().trim();
            // Check the input equation feedback on the premise of a random target equation
            if (flag3.equals("yes")) {
                String validationMessage = model.validateEquation(guess, targetEquation.get(0));
                // The equation is guessed correctly.
                if (validationMessage.equals("Correct! You've guessed the equation correctly.")) {
                    model.processInput(guess,targetEquation.get(0));
                    System.out.println("Congratulations! You've guessed the equation correctly.");
                    break;
                } else if (flag1.equals("yes") && !validationMessage.equals("Incorrect! Please try again.")) {
                    // If the validation message is not "Incorrect! Please try again.",
                    // then the equation is invalid, so we don't reduce attempts
                    System.out.println(validationMessage);
                } else {
                    // If the validation message is "Incorrect guess",
                    // then the equation is incorrect, and we reduce attempts
                    System.out.println("Incorrect guess.");
                    model.processInput(guess,targetEquation.get(0));
                }
            }else { // Check the input equation feedback on the premise of a fixed target equation
                String validationMessage = model.validateEquation(guess, FixedTargetEquation.get(0));
                if (validationMessage.equals("Correct! You've guessed the equation correctly.")) {
                    model.processInput(guess,FixedTargetEquation.get(0));
                    System.out.println("Congratulations! You've guessed the equation correctly.");
                    break;
                } else if (flag1.equals("yes") && !validationMessage.equals("Incorrect! Please try again.")) {
                    // If the validation message is not "Incorrect! Please try again.",
                    // then the equation is invalid, so we don't reduce attempts
                    System.out.println(validationMessage);
                } else {
                    // If the validation message is "Incorrect guess",
                    // then the equation is incorrect, and we reduce attempts
                    System.out.println("Incorrect guess.");
                    model.processInput(guess,FixedTargetEquation.get(0));
                }
            }
            // Failed to guess within the specified number of attempts
            if (!model.isGameWon() && model.getRemainingAttempts() < 1) {
                if (flag3.equals("yes")) {
                    System.out.println("Sorry, you've run out of attempts. The correct equation was: " + targetEquation.get(0));
                }else{
                    System.out.println("Sorry, you've run out of attempts. The correct equation was: " + FixedTargetEquation.get(0));
                }
            }
        }
    }

    public static void main(String[] args) {
        CLIApp cli = new CLIApp();
        cli.start();
    }
}
