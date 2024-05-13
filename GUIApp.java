public class GUIApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
                GUIApp::createAndShowGUI
        );
    }

    public static void createAndShowGUI() {
        IModel model = new Model();
        Controller controller = new Controller(model); // Create Controller instance
        new View(model, controller); // Pass Controller instance to View constructor
    }
}
