import java.util.List;
import java.util.Random;

public class Controller{
    private final IModel model;
    private View view;

    public Controller(IModel model) {
        this.model = model;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public void decreaseAttemptsRemaining() {
        model.decreaseAttemptsRemaining();
    }

    public void resetRemainingAttempts(){
        model.resetRemainingAttempts();
    }

    // Example method where you want to call validateExpression
    public String validateEquation(String rowInput, String targetEquation) {
        // Now you can use the validationMessage as needed
        return model.validateEquation(rowInput, targetEquation);
    }

    public void startNewGame() {
        model.startNewGame();
    }

    public boolean isGameOver(){
        return model.isGameOver();
    }

    // Method to get the random target equation from the model
    public String getTargetEquations() {
        List<String> equations = model.getTargetEquations();
        Random random = new Random();
        int index = random.nextInt(equations.size());
        return equations.get(index);
    }

    public String getFixedTargetEquations() {
        List<String> equations = model.getFixedTargetEquations();
        return equations.get(0);
    }

}
