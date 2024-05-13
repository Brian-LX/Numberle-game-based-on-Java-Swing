import java.util.List;

public interface IModel {
    int MAX_ATTEMPTS = 6;
    void initialize();
    void selectRandomEquations();
    void processInput(String input, String target);
    void startNewGame();
    boolean isGameOver();
    boolean isGameWon();
    List<String> getTargetEquations();
    List<String> getFixedTargetEquations();
    String provideFeedback(String guess, String target);
    int getRemainingAttempts();
    void decreaseAttemptsRemaining();
    void resetRemainingAttempts();
    String validateEquation(String rowInput, String targetEquation);
    void notifyObservers(Object arg);
}
