import java.util.Arrays; // Import Arrays class
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;
import javax.swing.border.*;

public class View implements Observer {
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 7;

    private final Controller controller;
    private final IModel model;
    private JFrame frame;
    private JTextField[][] inputFields;
    private JLabel attemptsLabel;
    private JLabel targetEquationLabel;
    private JButton[] operatorButtons;
    private boolean[] rowLocks;
    private boolean[] rowSubmitted;
    private JPanel inputButtonsPanel;
    private JPanel backspaceSubmitPanel;
    private String targetEquation;

    public View(IModel model, Controller controller) {
        this.model = model;
        this.controller = controller;
        this.controller.setView(this);
        ((Model)this.model).addObserver(this);
        createAndShowGUI();
        update((Model)this.model,null);
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (arg instanceof String) {
            String updateType = (String) arg;

            switch (updateType) {
                case "newgame":
                    if (rowSubmitted[0]) {
                        resetInputFields(); // reset input fields
                        resetColors(); // Reset input field colors
                        resetInputButtonColors(); // Reset input button colors

                        controller.resetRemainingAttempts(); // Reset remaining attempts to 6
                        model.notifyObservers("updateAttempts");
                        model.notifyObservers("updateEquation");
                        // Set all elements in the rowSubmitted array to false. Used to mark that all rows have not been committed.
                        Arrays.fill(rowSubmitted, false);
                    }else {
                        // If the first row is not locked or committed, a hint is given
                        String message = "<html><body><p style='font-family:SansSerif; font-size:15pt; font-weight:bold;'>Please enter the characters on the first line!</p></body></html>";
                        JOptionPane.showMessageDialog(null, message);
                    }
                    break;

                case "submit":
                    handleInput();
                    break;

                case "updateAttempts":
                    attemptsLabel.setText("Remaining attempts: " + controller.getRemainingAttempts());
                    break;

                case "updateEquation":
                    // Display a popup dialog with flag options
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                    // Create checkbox for the flags
                    JCheckBox checkBox_1 = new JCheckBox("flag1: Display the feedback of invalid equation.");
                    JCheckBox checkBox_2 = new JCheckBox("flag2: Display the target equation.");
                    JCheckBox checkBox_3 = new JCheckBox("flag3: Randomly select the target equation.");
                    checkBox_1.setFont(getCustomFont());
                    checkBox_2.setFont(getCustomFont());
                    checkBox_3.setFont(getCustomFont());
                    panel.add(checkBox_1);
                    panel.add(checkBox_2);
                    panel.add(checkBox_3);
                    // Modify the default button text
                    UIManager.put("OptionPane.okButtonText", "OK");
                    UIManager.put("OptionPane.cancelButtonText", "Cancel");

                    // Display the option dialog
                    int result = JOptionPane.showConfirmDialog(frame, panel, "Flag Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    // Check if the "OK" button is clicked
                    if (result == JOptionPane.OK_OPTION) {
                        if (checkBox_3.isSelected()) {
                            // Execute updateEquation functionality for randomly selecting the target equation
                            targetEquation = controller.getTargetEquations();
                        } else {
                            // Execute updateEquation functionality for a fixed target equation
                            targetEquation = controller.getFixedTargetEquations();
                        }

                        if (checkBox_2.isSelected()) {
                            // Update targetEquationLabel to display the target equation
                            targetEquationLabel.setText("Target Equation: " + targetEquation);
                        } else {
                            // Don't display the target equation
                            targetEquationLabel.setText("Target Equation: ");
                        }
                    }
                    System.out.println("The target equation is: " + targetEquation);
                    break;

                case "back":
                    for (int i = NUM_ROWS - 1; i >= 0; i--) {
                        if (!rowSubmitted[i]) {
                            for (int j = NUM_COLUMNS - 1; j >= 0; j--) {
                                JTextField inputField = inputFields[i][j];
                                if (!inputField.getText().isEmpty()) {
                                    inputField.setText("");
                                    return;
                                }
                            }
                        }
                    }
                    break;
                default:
                    System.out.println("Unknown update type");
                    break;
            }
        }
    }

    // Process input field
    private void handleInput() {
        // Find the index of the currently submitted row
        int currentRowIndex = -1;
        // Find the uncommitted row, set the currentRowIndex to its index, and exit the loop.
        for (int i = 0; i < 6; i++) {
            if (!rowSubmitted[i]) {
                currentRowIndex = i;
                break;
            }
        }
        // Ensure that a valid row index was found
        if (currentRowIndex != -1) {
            // Get the equation from the currently submitted row
            StringBuilder rowInput = new StringBuilder();
            // Iterate over all input fields of the current row (JTextField object)
            for (int j = 0; j < 7; j++) {
                JTextField inputField = inputFields[currentRowIndex][j];
                // Text content is appended to rowInput
                rowInput.append(inputField.getText());
            }
            // Validate the equation and obtain the validation message
            String validationMessage = controller.validateEquation(rowInput.toString(), targetEquation);
            // Display the validation message only for the currently submitted row
            JLabel messageLabel1 = new JLabel(validationMessage);
            // Display the validation message only for the currently submitted row if it's not "Incorrect! Please try again."
            if (!"Incorrect! Please try again.".equals(validationMessage)) {
                messageLabel1.setFont(getCustomFont());
                JOptionPane.showMessageDialog(frame, messageLabel1, "Reminder", JOptionPane.INFORMATION_MESSAGE);
            }
            // If the equation is valid, update colors for the currently submitted row
            if (validationMessage.equals("Correct! You've guessed the equation correctly.") || validationMessage.equals("Incorrect! Please try again.")) {
                updateColors(currentRowIndex, rowInput.toString(), targetEquation);
                // Mark the currently submitted row as submitted after processing
                rowSubmitted[currentRowIndex] = true;
                // If the equation is correct, reset the number of attempts remaining
                if(validationMessage.equals("Correct! You've guessed the equation correctly.")){
                    // Reset remaining attempts to 6
                    controller.resetRemainingAttempts();
                } else {
                    // Decrease remaining attempts when a row is submitted
                    controller.decreaseAttemptsRemaining();
                }
                // Update attempts label after decrementing attempts
                model.notifyObservers("updateAttempts");

                // If the target equation is entered, game over with "You won!" message
                if (validationMessage.equals("Correct! You've guessed the equation correctly.")) {
                    controller.startNewGame();
                    JLabel messageLabel = new JLabel("You Won!");
                    messageLabel.setFont(getCustomFont());
                    // The messageLabel information is displayed
                    JOptionPane.showMessageDialog(frame, messageLabel1, "Reminder", JOptionPane.INFORMATION_MESSAGE);
                    resetInputFields();
                    resetColors(); // Reset input field color
                    resetInputButtonColors(); // Reset input button colors
                    // Update the target equation for the next game
                    model.notifyObservers("updateEquation");
                    Arrays.fill(rowSubmitted, false);
                }
            } else {
                // If the equation is invalid, keep the current row editable
                // Reset colors for the current row
                for (int j = 0; j < 7; j++) {
                    JTextField inputField = inputFields[currentRowIndex][j];
                    inputField.setBackground(null); // Reset background color
                }
            }
            // Check if all rows have been submitted
            boolean allRowsSubmitted = true;
            for (boolean submitted : rowSubmitted) {
                if (!submitted) {
                    allRowsSubmitted = false;
                    break;
                }
            }
            // If all rows have been submitted, trigger game over
            if (allRowsSubmitted || controller.isGameOver()) {
                // Display the game over message for the currently submitted row
                JLabel messageLabel = new JLabel("You Lost! Game Over. The target equation is: "+ targetEquation);
                messageLabel.setFont(getCustomFont());
                JOptionPane.showMessageDialog(frame, messageLabel);
                controller.startNewGame();
                resetInputFields();
                resetColors();
                resetInputButtonColors(); // Reset input button colors
                controller.resetRemainingAttempts();
                model.notifyObservers("updateAttempts");
                model.notifyObservers("updateEquation");
                Arrays.fill(rowSubmitted, false);
            }
        }
    }


    private void createAndShowGUI() {
        initializeFrame();
        initializeInputFields();
        initializeKeyboardPanel();
        initializeTopPanel();
        frame.setVisible(true);
        model.notifyObservers("updateEquation");
        model.notifyObservers("updateAttempts");
    }

    private void initializeFrame() {
        frame = new JFrame("Numberle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 900);
        frame.setLayout(new BorderLayout());
    }

    // Initialize Input Fields
    private void initializeInputFields() {
        inputFields = new JTextField[NUM_ROWS][NUM_COLUMNS];
        rowLocks = new boolean[NUM_ROWS];
        rowSubmitted = new boolean[NUM_ROWS];

        JPanel inputFieldsPanel = new JPanel(new GridLayout(NUM_ROWS, NUM_COLUMNS, 5, 5));
        inputFieldsPanel.setBackground(Color.WHITE);

        int arcRadius = 10;

        Border roundedBorder = createRoundedBorder(Color.decode("#dce1ed"), arcRadius);

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                JTextField inputField = createInputField(roundedBorder);
                inputFields[i][j] = inputField;
                inputFieldsPanel.add(inputField);
            }
        }

        inputFieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(60, 60, 60, 60),
                BorderFactory.createCompoundBorder(
                        new LineBorder(Color.decode("#ffffff"), 5),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        ));

        frame.add(inputFieldsPanel, BorderLayout.CENTER);
    }

    // create Input Field
    private JTextField createInputField(Border roundedBorder) {
        JTextField inputField = new JTextField(3);
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setFont(new Font("SansSerif", Font.BOLD, 28));
        inputField.setBorder(roundedBorder);
        return inputField;
    }

    // Initialize keyboard
    private void initializeKeyboardPanel() {
        JPanel keyboardPanel = new JPanel(new BorderLayout());
        inputButtonsPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        inputButtonsPanel.setBackground(Color.WHITE);

        Border roundedBorder = createRoundedBorder(Color.WHITE, 10);

        initializeNumberButtons(roundedBorder);
        arithmeticSignPanel(roundedBorder);

        keyboardPanel.add(inputButtonsPanel, BorderLayout.NORTH);
        keyboardPanel.add(backspaceSubmitPanel, BorderLayout.SOUTH);
        frame.add(keyboardPanel, BorderLayout.SOUTH);
    }

    private void initializeNumberButtons(Border roundedBorder) {
        for (int i = 1; i <= 9; i++) {
            JButton numberButton = createButton(Integer.toString(i), roundedBorder);
            numberButton.addActionListener(new NumberButtonListener());
            inputButtonsPanel.add(numberButton);
        }

        JButton zeroButton = createButton("0", roundedBorder);
        zeroButton.addActionListener(new NumberButtonListener());
        inputButtonsPanel.add(zeroButton);

        setButtonSizes(inputButtonsPanel, new Dimension(56, 56));
    }

    private JButton createButton(String label, Border roundedBorder) {
        JButton button = new JButton(label);
        button.setBackground(Color.decode("#dce1ed"));
        button.setFont(new Font("SansSerif", Font.BOLD, 25));
        button.setBorder(roundedBorder);
        return button;
    }

    // Operator button label
    private void arithmeticSignPanel(Border roundedBorder) {
        backspaceSubmitPanel = new JPanel(new GridLayout(1, 4, 5, 5)); // Reduced to 4 columns
        JButton backspaceButton = createButton("Back", roundedBorder);
        backspaceButton.addActionListener(new BackspaceButtonListener());
        backspaceSubmitPanel.add(backspaceButton);

        String[] operators = {"+", "-", "*", "/", "="};
        operatorButtons = new JButton[operators.length];

        for (int i = 0; i < operators.length; i++) {
            JButton button = createButton(operators[i], roundedBorder);
            button.addActionListener(new OperatorButtonListener());
            backspaceSubmitPanel.add(button);
            backspaceSubmitPanel.setBackground(Color.WHITE);
            operatorButtons[i] = button;
        }

        JButton enterButton = createButton("Enter", roundedBorder);
        enterButton.addActionListener(new SubmitButtonListener());
        backspaceSubmitPanel.add(enterButton);

        setButtonSizes(backspaceSubmitPanel, new Dimension(200, 50));
    }

    private void setButtonSizes(Container container, Dimension dimension) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setPreferredSize(dimension);
            }
        }
    }

    private void initializeTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        Font font = new Font("SansSerif", Font.BOLD, 17);
        // Remaining number of attempts label
        attemptsLabel = new JLabel("Attempts remaining: " + controller.getRemainingAttempts());
        attemptsLabel.setFont(font);
        attemptsLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        topPanel.add(attemptsLabel, BorderLayout.WEST);

        targetEquationLabel = new JLabel("Target Equation: ");
        targetEquationLabel.setFont(font);
        targetEquationLabel.setBorder(new EmptyBorder(0, 130, 0, 10));
        topPanel.add(targetEquationLabel, BorderLayout.CENTER);
        // New game label
        JPanel newGamePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        newGamePanel.setBackground(Color.WHITE);


        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new NewGameButtonListener());
        newGameButton.setPreferredSize(new Dimension(120, 40));
        newGameButton.setBackground(Color.decode("#dce1ed"));
        newGameButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        newGameButton.setBorder(createRoundedBorder(Color.WHITE, 10));
        newGamePanel.add(newGameButton);
        topPanel.add(newGamePanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.PAGE_START);
    }

    // Set the rounded border
    private Border createRoundedBorder(Color color, int arcRadius) {
        return new LineBorder(color, 4, true) {
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(1, 1, 1, 1);
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }

            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(getLineColor());
                g2d.setStroke(new BasicStroke(getThickness()));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawRoundRect(x, y, width - 1, height - 1, arcRadius, arcRadius);
                g2d.dispose();
            }
        };
    }

    private static Font getCustomFont() {
        return new Font("SansSerif", Font.BOLD, 15);
    }

    // Gets the number button appended to the input field
    private class NumberButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String number = button.getText();
            appendToInputField(number);
        }
    }

    // The method for adding characters to an input field
    private void appendToInputField(String text) {
        for (int i = 0; i < NUM_ROWS; i++) {
            if (!rowLocks[i] && !rowSubmitted[i]) {
                for (int j = 0; j < NUM_COLUMNS; j++) {
                    JTextField inputField = inputFields[i][j];
                    if (inputField.getText().isEmpty()) {
                        inputField.setText(text);
                        return;
                    }
                }
            }
        }
    }

    // Refresh game
    private class NewGameButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent newgame) {
            model.notifyObservers("newgame");
        }
    }

    // Submit the click event for the input field
    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.notifyObservers("submit");
        }
    }

    // Gets the action button appended to the input field
    private class OperatorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            Arrays.asList(operatorButtons).indexOf(button);
            String operator = button.getText();
            appendToInputField(operator);
        }
    }

    // Delete input field
    private class BackspaceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.notifyObservers("back");
        }
    }

    // This method updates the color of the input field according to the target equation
    private void updateColors(int rowIndex, String rowInput, String targetEquation) {
        System.out.println("Updating colors for row " + (rowIndex + 1)); // Debug row index print

        // Validate the equation
        String validationMessage = controller.validateEquation(rowInput, targetEquation);
        if (!validationMessage.equals("Correct! You've guessed the equation correctly.") && !validationMessage.equals("Incorrect! Please try again.") ) {
            // If the equation is not correct, do not update colors
            System.out.println("Equation is invalid, colors will not be updated.");
            return;
        }

        // Debug print for target equation
        System.out.println("Target Equation updating color: " + rowInput);

        if (rowSubmitted[rowIndex])
            // Disable input fields for submitted row
            for (int j = 0; j < 7; j++) {
                JTextField inputField = inputFields[rowIndex][j];
                inputField.setEditable(false);
            }

        // Update input fields colors
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                JTextField inputField = inputFields[rowIndex][j];
                if (!rowInput.isEmpty()) {
                    // Get the rowInput and targetEquation characters at position j, respectively.
                    char userChar = rowInput.charAt(j);
                    char targetChar = targetEquation.charAt(j);
                    // Set the input field colors
                    if (userChar == targetChar) {
                        inputField.setBackground(Color.decode("#2fc1a5")); // Correct position and correct digit
                    } else if (targetEquation.contains(Character.toString(userChar))) {
                        inputField.setBackground(Color.decode("#f79a67")); // Correct digit but wrong position
                    } else {
                        inputField.setBackground(Color.decode("#a4aec4")); // Not in target equation
                    }
                }
            }
        }

        // Update operator buttons colors for the current row
        if (!rowInput.isEmpty()) {
            // Iterate over each JButton object in the operatorButtons list
            for (JButton button : operatorButtons) {
                String buttonText = button.getText(); // Gets the text of the current button and stores it in the buttonText variable
                // Check that the button text exists in rowInput and targetEquation
                boolean isButtonPressed = rowInput.contains(buttonText);
                boolean isButtonInTargetEquation = targetEquation.contains(buttonText);
                int targetIndex = targetEquation.indexOf(buttonText); // Find the location where buttonText first appears in targetEquation

                setKeyboardColors(rowInput, buttonText, button, isButtonPressed, isButtonInTargetEquation, targetIndex);
            }

            // Debug print for updating operator buttons
            System.out.println("Operator buttons updated.");

            // Update number buttons colors for the current row
            for (Component component : inputButtonsPanel.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    String buttonText = button.getText();
                    // Check if the button represents a digit
                    if (Character.isDigit(buttonText.charAt(0))) {
                        boolean isButtonPressed = rowInput.contains(buttonText);
                        boolean isButtonInTargetEquation = targetEquation.contains(buttonText);
                        int targetIndex = targetEquation.indexOf(buttonText);

                        setKeyboardColors(rowInput, buttonText, button, isButtonPressed, isButtonInTargetEquation, targetIndex);
                    }
                }
            }
            // Debug print for updating number buttons
            System.out.println("Number buttons updated.");
        }
    }

    // Set the color of the number button and the operator button
    private void setKeyboardColors(String rowInput, String buttonText, JButton button, boolean isButtonPressed, boolean isButtonInTargetEquation, int targetIndex){
        // Check if the character appears multiple times in rowInput
        if (rowInput.indexOf(buttonText) != rowInput.lastIndexOf(buttonText)) {
            // Check if any occurrence of buttonText is in the correct position
            for (int i = 0; i < rowInput.length(); i++) {
                if (rowInput.charAt(i) == buttonText.charAt(0) && targetEquation.charAt(i) == buttonText.charAt(0)) {
                    button.setBackground(Color.decode("#2fc1a5"));
                }
            }
        }

        if (isButtonPressed && isButtonInTargetEquation) {
            if (rowInput.indexOf(buttonText) == targetIndex) {
                // Button pressed and in correct position of target equation
                button.setBackground(Color.decode("#2fc1a5")); // Button previously in wrong position but now in correct position
            } else {
                if (button.getBackground() != null && button.getBackground().equals(Color.decode("#2fc1a5"))) {
                    button.setBackground(button.getBackground()); // Preserve dominant color
                } else {
                    button.setBackground(Color.decode("#f79a67")); // Button pressed but in wrong position of target equation
                }
            }
        } else if (isButtonPressed) {
            button.setBackground(Color.decode("#a4aec4")); // Button not pressed but in target equation
        } else {
            // Add condition to preserve the color
            if (button.getBackground() != null && (button.getBackground().equals(Color.decode("#a4aec4")) || button.getBackground().equals(Color.decode("#2fc1a5")))) {
                button.setBackground(button.getBackground());
            }
        }
    }

    // Refresh the color of the input field
    private void resetInputFields() {
        for (int rowIndex = 0; rowIndex < NUM_ROWS; rowIndex++) {
            rowLocks[rowIndex] = false;
            for (int columnIndex = 0; columnIndex < NUM_COLUMNS; columnIndex++) {
                JTextField inputField = inputFields[rowIndex][columnIndex];
                inputField.setText("");
            }
        }

        resetInputButtonColors();
    }

    // Refresh the color of the action button
    private void resetColors() {
        for (JButton button : operatorButtons) {
            button.setBackground(Color.decode("#dce1ed"));
        }

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                JTextField inputField = inputFields[i][j];
                inputField.setBackground(null);
            }
        }
    }

    // Refresh button color
    private void resetInputButtonColors() {
        for (Component component : inputButtonsPanel.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                String buttonText = button.getText();
                if (Character.isDigit(buttonText.charAt(0))) {
                    button.setBackground(Color.decode("#dce1ed"));
                }
            }
        }
    }
}