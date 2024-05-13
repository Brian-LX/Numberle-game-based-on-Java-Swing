import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class ModelTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new Model();
    }

    @AfterEach
    void tearDown() {
        model = null;
    }

    // Test scenario for decreasing remaining attempts
    @Test
    public void testReduceAttempt() {
        int initialAttempts = model.MAX_ATTEMPTS;
        model.decreaseAttemptsRemaining();
        assertEquals(initialAttempts-1, model.getRemainingAttempts());
    }

    // Test scenario for processing input fields
    @Test
    public void testProcessInput(){
        int initialAttempts = model.MAX_ATTEMPTS;
        List<String> targetEquation = model.getTargetEquations();
        String input = "1+2+3=6";
        assertNotNull(model.provideFeedback(input, targetEquation.get(0)));
        model.processInput(input, targetEquation.get(0));
        assertEquals(initialAttempts-1, model.getRemainingAttempts());
    }

    // Test scenario for starting new game
    @Test
    public void testStartNewGame() {
        model.startNewGame();
        boolean gameWon = model.isGameWon();
        assertNotNull(model.getTargetEquations());
        assertFalse(model.getTargetEquations().isEmpty());
        assertEquals(6, model.MAX_ATTEMPTS);
        assertFalse(gameWon);
    }
}