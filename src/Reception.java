import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Reception extends JFrame{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Reception::new);
    }

    public Reception() {
        JFrame frame = new JFrame("Reception");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Header Label
        JLabel header = new JLabel("Reception Panel", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setOpaque(true);
        header.setBackground(new Color(30, 144, 255)); // Blue Header
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(600, 40));
        frame.add(header, BorderLayout.NORTH);

        // Panel for Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 3, 10, 10)); // 4 rows, 3 columns
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(Color.WHITE);

        // Buttons with consistent style
        String[] buttonLabels = {
                "Add Patient", "Update Patient Details", "View Patients Info",
                "Generate Bill", "Appointments", "View Doctor Details", "Manage Treatment Log"
        };
        for (String text : buttonLabels) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setFocusPainted(false);
            button.setBackground(new Color(173, 216, 230)); // Light Blue Buttons
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLUE, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            button.addActionListener(new ButtonHandler(text));
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 20, 60)); // Red Logout Button
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(600, 40));
        logoutButton.addActionListener(new ButtonHandler("Logout"));

        frame.add(logoutButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static class ButtonHandler implements ActionListener {
        private final String action;

        public ButtonHandler(String action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (action.equals("Add Patient")) {
                new Add_Patient(); // Open New_Patient window
            } else if (action.equals("Logout")) {
                new Login();  // Open the Login window
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                topFrame.dispose();  // Close the Reception window
            } else if (action.equals("Update Patient Details")){
                new Update_Patient();
            } else if (action.equals("View Patients Info")){
                new View_Patient_Info();
            } else if (action.equals("View Doctor Details")){
                new View_Doc_Details();
            } else if (action.equals("Appointments")){
                new AppointmentSystem();
            } else if (action.equals("Manage Treatment Log")){
                new ManageTreatmentLog();
            } else if (action.equals("Generate Bill")){
                new HospitalBillingSystem();
            }
            else {
                JOptionPane.showMessageDialog(null, "Functionality for: " + action);
            }
        }
    }
}
