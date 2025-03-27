import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

// Login class extending JFrame and implementing ActionListener
public class Login extends JFrame implements ActionListener {
    private JTextField textField;
    private JPasswordField jPasswordField;
    private JButton b1, b2;

    // Constructor to initialize the login window
    Login() {
        // Setting background color
        getContentPane().setBackground(new Color(230, 240, 255));
        setLayout(null);

        // Username Label
        JLabel username = new JLabel("Username:");
        username.setBounds(40, 20, 100, 30);
        username.setFont(new Font("Tahoma", Font.BOLD, 18));
        username.setForeground(Color.DARK_GRAY);
        add(username);

        // Password Label
        JLabel password = new JLabel("Password:");
        password.setBounds(40, 70, 100, 30);
        password.setFont(new Font("Tahoma", Font.BOLD, 18));
        password.setForeground(Color.DARK_GRAY);
        add(password);

        // Username Input Field
        textField = new JTextField();
        textField.setBounds(150, 20, 200, 30);
        textField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(textField);

        // Password Input Field
        jPasswordField = new JPasswordField();
        jPasswordField.setBounds(150, 70, 200, 30);
        jPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        add(jPasswordField);

        // Login Button
        b1 = new JButton("Login");
        b1.setBounds(150, 120, 100, 30);
        b1.setFont(new Font("Tahoma", Font.BOLD, 18));
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(30, 144, 255));
        b1.setBorderPainted(false);
        b1.addActionListener(this);
        add(b1);

        // Cancel Button
        b2 = new JButton("Cancel");
        b2.setBounds(270, 120, 100, 30);
        b2.setFont(new Font("Tahoma", Font.BOLD, 18));
        b2.setForeground(Color.WHITE);
        b2.setBackground(Color.RED);
        b2.setBorderPainted(false);
        b2.addActionListener(this);
        add(b2);

        // Frame Properties
        setSize(450, 250);
        setLocationRelativeTo(null);  // Centers the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Action handler for button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            try {
                Conn c = new Conn();  // Establish database connection
                String userName = textField.getText();
                String pass = new String(jPasswordField.getPassword()); // Secure password handling

                // SQL query to check if user exists
                String query = "SELECT * FROM login WHERE ID = ? AND PW = ?";
                PreparedStatement pstmt = c.getConnection().prepareStatement(query);
                pstmt.setString(1, userName);
                pstmt.setString(2, pass);

                ResultSet resultSet = pstmt.executeQuery();

                // If user is found, open the next window
                if (resultSet.next()) {
                    new Reception();
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

                resultSet.close();
                pstmt.close();
                c.closeConnection(); // Close DB connection

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == b2) {
            System.exit(0);
        }
    }

    // Main method to launch login window
    public static void main(String[] args) {
        new Login();
    }
}
