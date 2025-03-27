import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.*;

// Login class extending JFrame and implementing ActionListener for button actions
public class Login extends JFrame implements ActionListener {
    // Declaring UI components
    JTextField textField;        // Text field for username input
    JPasswordField jPasswordField;  // Password field for password input
    JButton b1, b2;             // Buttons for login and cancel actions

    // Constructor to initialize the login window
    Login() {
        // Creating and setting properties for username label
        JLabel username = new JLabel("Username");
        username.setBounds(40, 20, 100, 30);
        username.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(username);

        // Creating and setting properties for password label
        JLabel password = new JLabel("Password");
        password.setBounds(40, 70, 100, 30);
        password.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(password);

        // Creating and setting properties for username input field
        textField = new JTextField();
        textField.setBounds(150, 20, 150, 30);
        textField.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(textField);

        // Creating and setting properties for password input field
        jPasswordField = new JPasswordField();
        jPasswordField.setBounds(150, 70, 150, 30);
        jPasswordField.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(jPasswordField);

        // Creating and setting properties for Login button
        b1 = new JButton("Login");
        b1.setBounds(150, 120, 100, 30);
        b1.setFont(new Font("Tahoma", Font.BOLD, 20));
        b1.addActionListener(this);  // Adding action listener
        add(b1);

        // Creating and setting properties for Cancel button
        b2 = new JButton("Cancel");
        b2.setBounds(150, 160, 120, 30);
        b2.setFont(new Font("Tahoma", Font.BOLD, 20));
        b2.addActionListener(this);  // Adding action listener
        add(b2);

        // Setting JFrame properties
        setSize(750, 300);                 // Setting window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Closing operation
        setLocation(750, 270);              // Setting window location on screen
        setLayout(null);                    // Using absolute positioning
        setVisible(true);                    // Making the frame visible
    }

    // Main method to run the application
    public static void main(String[] args) {
        new Login();  // Creating an instance of Login class
    }

    // Action handler for button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        // If Login button is clicked
        if (e.getSource() == b1) {
            try {
                Conn c = new Conn();  // Establish database connection
                String userName = textField.getText();  // Get username input
                String pass = jPasswordField.getText(); // Get password input

                // SQL query to check if the user exists in the database
                String query = "SELECT * FROM login WHERE ID = '" + userName + "' AND PW = '" + pass + "'";
                ResultSet resultSet = c.statement.executeQuery(query);

                // If user is found, open the next window (test class)
                if (resultSet.next()) {
                    new Reception();
                    setVisible(false); // Hide login window
                } else {
                    // Show error message if credentials are invalid
                    JOptionPane.showMessageDialog(null, "INVALID");
                }
            } catch (Exception E) {
                E.printStackTrace(); // Print exception details in case of an error
            }
        } else {
            // If Cancel button is clicked, exit the application
            System.exit(10);
        }
    }
}
