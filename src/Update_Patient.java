import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Update_Patient extends JFrame implements ActionListener {
    JButton b1, b2;
    JTextField patientIdField, nameField, contactField;

    public Update_Patient() {
        // Frame setup
        setTitle("Update Patient Information");
        setSize(900, 600);
        setLocationRelativeTo(null); // Centers the window

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("UPDATE PATIENT INFORMATION");
        titleLabel.setBounds(300, 20, 400, 30);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Patient ID Label & Field
        JLabel labelPatientID = new JLabel("Patient ID Number:");
        labelPatientID.setBounds(250, 80, 200, 25);
        labelPatientID.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelPatientID.setForeground(Color.WHITE);
        panel.add(labelPatientID);

        patientIdField = new JTextField();
        patientIdField.setBounds(450, 80, 200, 25);
        panel.add(patientIdField);

        // Name Label & Field
        JLabel labelName = new JLabel("New Name:");
        labelName.setBounds(250, 130, 200, 25);
        labelName.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelName.setForeground(Color.WHITE);
        panel.add(labelName);

        nameField = new JTextField();
        nameField.setBounds(450, 130, 200, 25);
        panel.add(nameField);

        // Contact Label & Field
        JLabel labelContact = new JLabel("New Contact:");
        labelContact.setBounds(250, 180, 200, 25);
        labelContact.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelContact.setForeground(Color.WHITE);
        panel.add(labelContact);

        contactField = new JTextField();
        contactField.setBounds(450, 180, 200, 25);
        panel.add(contactField);

        // Update Button
        b1 = new JButton("Update");
        b1.setBounds(350, 240, 120, 35);
        b1.setFont(new Font("Tahoma", Font.BOLD, 14));
        b1.setForeground(Color.WHITE);
        b1.setBackground(new Color(59, 89, 182));
        b1.addActionListener(this);
        panel.add(b1);

        // Back Button
        b2 = new JButton("Back");
        b2.setBounds(480, 240, 120, 35);
        b2.setFont(new Font("Tahoma", Font.BOLD, 14));
        b2.setForeground(Color.WHITE);
        b2.setBackground(new Color(200, 50, 50));
        b2.addActionListener(this);
        panel.add(b2);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Update_Patient::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            // Update button action
            String patientId = patientIdField.getText();
            String newName = nameField.getText();
            String newContact = contactField.getText();

            if (patientId.isEmpty() || newName.isEmpty() || newContact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Conn c = new Conn();
                String query = "UPDATE Patient_Info SET name = ?, contact = ? WHERE ID_Number = ?";
                PreparedStatement pst = c.getConnection().prepareStatement(query);
                pst.setString(1, newName);
                pst.setString(2, newContact);
                pst.setString(3, patientId);

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Patient information updated successfully!");
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No patient found with ID: " + patientId, "Error", JOptionPane.ERROR_MESSAGE);
                }

                pst.close();
                c.getConnection().close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else if (e.getSource() == b2) {
            // Back button action
            this.dispose(); // Close this window
            // You might want to open a previous window here
        }
    }
}