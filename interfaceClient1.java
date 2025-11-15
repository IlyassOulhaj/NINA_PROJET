import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class interfaceClient1 extends JFrame {
    
    // --- Network Fields ---
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private PrintWriter serverOut;
    private BufferedReader serverIn;

    // --- GUI Components ---
    private JTextArea outputArea;
    private JTextField loginField, targetField, messageField;
    private JTextField nameField, telField, emailField;
    private JButton loginButton, listButton, addButton, sendButton, quitButton;
    private JPanel leftPanel, rightPanel, controlPanel;

    public interfaceClient1() {
        super("Annuaire & Messagerie Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        
        initializeGUI();
        applyCoolColors();
        connectToServer();
        
        setVisible(true);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setText("Client started. Connecting to server...\n");
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Control Panel
        controlPanel = new JPanel(new GridLayout(1, 2));
        
        // Left Panel
        leftPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Connection & Directory"));
        
        loginField = new JTextField(10);
        nameField = new JTextField(10);
        telField = new JTextField(10);
        emailField = new JTextField(10);
        
        loginButton = new JButton("LOGIN");
        listButton = new JButton("LIST Contacts");
        addButton = new JButton("ADD Contact");
        quitButton = new JButton("QUIT");

        leftPanel.add(new JLabel("Login Name:"));
        leftPanel.add(loginField);
        leftPanel.add(loginButton);
        leftPanel.add(listButton); 
        leftPanel.add(new JLabel("Name (ADD):"));
        leftPanel.add(nameField);
        leftPanel.add(new JLabel("Phone (ADD):"));
        leftPanel.add(telField);
        leftPanel.add(new JLabel("Email (ADD):"));
        leftPanel.add(emailField);
        leftPanel.add(addButton);
        leftPanel.add(quitButton);
        
        // Right Panel
        rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Messaging"));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        targetField = new JTextField();
        messageField = new JTextField();
        sendButton = new JButton("SEND_MSG");

        inputPanel.add(new JLabel("Recipient:"));
        inputPanel.add(targetField);
        inputPanel.add(new JLabel("Message:"));
        inputPanel.add(messageField);
        
        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(sendButton, BorderLayout.SOUTH);
        
        controlPanel.add(leftPanel);
        controlPanel.add(rightPanel);
        add(controlPanel, BorderLayout.SOUTH);
        
        setupButtonActions();
    }
    
    private void applyCoolColors() {
        // Main background
        getContentPane().setBackground(new Color(45, 45, 65));
        
        // Output area
        outputArea.setBackground(new Color(30, 30, 40));
        outputArea.setForeground(Color.WHITE);
        outputArea.setCaretColor(Color.WHITE);
        
        // Panels
        leftPanel.setBackground(new Color(55, 55, 75));
        rightPanel.setBackground(new Color(55, 55, 75));
        controlPanel.setBackground(new Color(55, 55, 75));
        JPanel inputPanel = (JPanel) rightPanel.getComponent(0);
        inputPanel.setBackground(new Color(55, 55, 75));
        // Make ALL labels white
        setAllLabelsWhite(leftPanel);
        setAllLabelsWhite(rightPanel);
        
        // Buttons
        Color[] buttonColors = {
            new Color(86, 98, 246),    // login - blue
            new Color(139, 69, 19),    // list - brown  
            new Color(34, 139, 34),    // add - green
            new Color(220, 20, 60),    // send - red
            new Color(128, 0, 0)       // quit - dark red
        };
        
        JButton[] buttons = {loginButton, listButton, addButton, sendButton, quitButton};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(buttonColors[i]);
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFocusPainted(false);
        }
        
        // Text fields
        JTextField[] textFields = {loginField, nameField, telField, emailField, targetField, messageField};
        for (JTextField field : textFields) {
            field.setBackground(new Color(70, 70, 90));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
        }
        
        // Border colors
        setTitledBorderColor(leftPanel, new Color(100, 149, 237));
        setTitledBorderColor(rightPanel, new Color(100, 149, 237));
    }
    
    private void setAllLabelsWhite(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(Color.WHITE);
            } else if (comp instanceof Container) {
                setAllLabelsWhite((Container) comp);
            }
        }
    }
    
    private void setTitledBorderColor(JPanel panel, Color color) {
        javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) panel.getBorder();
        if (border != null) {
            border.setTitleColor(color);
        }
    }
    
    private void setupButtonActions() {
        loginButton.addActionListener(e -> sendCommand("LOGIN " + loginField.getText()));
        listButton.addActionListener(e -> sendCommand("LIST"));
        quitButton.addActionListener(e -> { sendCommand("QUIT"); dispose(); System.exit(0); });
        
        addButton.addActionListener(e -> {
            String name = nameField.getText(), tel = telField.getText(), email = emailField.getText();
            if (!name.isEmpty() && !tel.isEmpty() && !email.isEmpty()) {
                sendCommand("ADD " + name + " " + tel + " " + email);
                nameField.setText(""); telField.setText(""); emailField.setText("");
            } else outputArea.append("Error: All fields required for ADD\n");
        });
        
        sendButton.addActionListener(e -> {
            String target = targetField.getText(), msg = messageField.getText();
            if (!target.isEmpty() && !msg.isEmpty()) {
                sendCommand("SEND_MSG " + target + " " + msg);
                messageField.setText("");
            } else outputArea.append("Error: Recipient and message required\n");
        });
    }
    
    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverOut = new PrintWriter(socket.getOutputStream(), true);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputArea.append("✅ Connected to server.\n");
            new ServerListenerWorker(serverIn, outputArea).execute();
        } catch (IOException e) {
            outputArea.append("❌ Connection Error\n");
            JOptionPane.showMessageDialog(this, "Cannot connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendCommand(String command) {
        if (serverOut != null) {
            serverOut.println(command);
            outputArea.append("You: " + command + "\n");
        } else outputArea.append("ERROR: Not connected\n");
    }
    
    private static class ServerListenerWorker extends SwingWorker<Void, String> {
        private BufferedReader reader;
        private JTextArea outputArea;
        
        public ServerListenerWorker(BufferedReader reader, JTextArea outputArea) {
            this.reader = reader;
            this.outputArea = outputArea;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            try {
                String response;
                while ((response = reader.readLine()) != null) publish(response);
            } catch (IOException e) {
                publish("❌ Connection lost");
            }
            return null;
        }
        
        @Override
        protected void process(List<String> chunks) {
            for (String response : chunks) {
                if (response.startsWith("MESSAGE_FROM ")) {
                    // Convert "MESSAGE_FROM John: Hello" to "John: Hello"
                    String cleanMessage = response.replace("MESSAGE_FROM ", "");
                    outputArea.append(cleanMessage + "\n");
                } else {
                    // Show other messages as is
                    outputArea.append(response + "\n");
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new interfaceClient1()); // FIXED: interfaceClient1
    }
}
