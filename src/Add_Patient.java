import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Add_Patient extends JFrame implements ActionListener {
    JComboBox<String> combobox;
    JTextField txtNumber, txtName, txtAge, txtDeposit, txtContact;
    JRadioButton r1, r2;
    JButton b1, b2;

    public Add_Patient() {
        // Panel Setup
        JPanel panel = new JPanel();
        panel.setBounds(5, 5, 840, 540);
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Form Header
        JLabel label = new JLabel("NEW PATIENT FORM");
        label.setBounds(118, 11, 260, 53);
        label.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel.add(label);

        // ID Label and ComboBox
        JLabel labelID = new JLabel("ID_Type: ");
        labelID.setBounds(35, 76, 200, 14);
        labelID.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelID.setForeground(Color.WHITE);
        panel.add(labelID);

        combobox = new JComboBox<>(new String[]{"Aadhar_Card", "Driving_License", "Voter_ID"});
        combobox.setBounds(271, 73, 150, 20);
        combobox.setBackground(new Color(3, 45, 48));
        combobox.setForeground(Color.WHITE);
        combobox.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(combobox);

        // Number
        JLabel labelNumber = new JLabel("ID_Number: ");
        labelNumber.setBounds(35, 111, 200, 14);
        labelNumber.setFont(new Font("Tahoma", Font.BOLD, 14));
        labelNumber.setForeground(Color.WHITE);
        panel.add(labelNumber);

        txtNumber = new JTextField();
        txtNumber.setBounds(271, 111, 150, 20);
        panel.add(txtNumber);

        // Name
        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setBounds(35, 151, 200, 14);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel);

        txtName = new JTextField();
        txtName.setBounds(271, 151, 150, 20);
        panel.add(txtName);

        // Gender
        JLabel Gender = new JLabel("Gender: ");
        Gender.setBounds(35, 191, 200, 14);
        Gender.setFont(new Font("Tahoma", Font.BOLD, 14));
        Gender.setForeground(Color.WHITE);
        panel.add(Gender);

        r1 = new JRadioButton("Male");
        r1.setFont(new Font("Tahoma", Font.BOLD, 14));
        r1.setForeground(Color.WHITE);
        r1.setBackground(new Color(109, 164, 170));
        r1.setBounds(271, 191, 80, 20);
        panel.add(r1);

        r2 = new JRadioButton("Female");
        r2.setFont(new Font("Tahoma", Font.BOLD, 14));
        r2.setForeground(Color.WHITE);
        r2.setBackground(new Color(109, 164, 170));
        r2.setBounds(350, 191, 80, 20);
        panel.add(r2);

        ButtonGroup bg = new ButtonGroup();
        bg.add(r1);
        bg.add(r2);

        // Age
        JLabel lblAge = new JLabel("Age: ");
        lblAge.setBounds(35, 231, 200, 14);
        lblAge.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblAge.setForeground(Color.WHITE);
        panel.add(lblAge);

        txtAge = new JTextField();
        txtAge.setBounds(271, 231, 150, 20);
        panel.add(txtAge);

        // Deposit
        JLabel lblDeposit = new JLabel("Deposit: ");
        lblDeposit.setBounds(35, 274, 200, 14);
        lblDeposit.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblDeposit.setForeground(Color.WHITE);
        panel.add(lblDeposit);

        txtDeposit = new JTextField();
        txtDeposit.setBounds(271, 274, 150, 20);
        panel.add(txtDeposit);

        // Contact
        JLabel lblContact = new JLabel("Contact: ");
        lblContact.setBounds(35, 320, 200, 14);
        lblContact.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblContact.setForeground(Color.WHITE);
        panel.add(lblContact);

        txtContact = new JTextField();
        txtContact.setBounds(271, 320, 150, 20);
        panel.add(txtContact);

        // Submit Button
        b1 = new JButton("Submit");
        b1.setBounds(100, 430, 120, 30);
        b1.setFont(new Font("Tahoma", Font.BOLD, 14));
        b1.setForeground(Color.WHITE);
        b1.setBackground(Color.BLACK);
        b1.addActionListener(this);
        panel.add(b1);

        // Back Button
        b2 = new JButton("Back");
        b2.setBounds(260, 430, 120, 30);
        b2.setFont(new Font("Tahoma", Font.BOLD, 14));
        b2.setForeground(Color.WHITE);
        b2.setBackground(Color.BLACK);
        b2.addActionListener(this);
        panel.add(b2);

        add(panel);

        setSize(850, 550);
        setLayout(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Add_Patient();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            try {
                Conn c = new Conn();
                String gender = r1.isSelected() ? "Male" : "Female";

                String idType = (String) combobox.getSelectedItem();
                String ID_Number = txtNumber.getText();
                String name = txtName.getText();
                String ageText = txtAge.getText();
                String depositText = txtDeposit.getText();
                String contact = txtContact.getText();

                // Validate inputs
                if (ID_Number.isEmpty() || name.isEmpty() || ageText.isEmpty() || depositText.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all required fields!");
                    return;
                }

                int age = Integer.parseInt(ageText);
                double deposit = Double.parseDouble(depositText);

                String query = "INSERT INTO Patient_Info (ID_Type, ID_Number, Name, Gender, Age, Deposit, Contact) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = c.getConnection().prepareStatement(query);
                pstmt.setString(1, idType);
                pstmt.setString(2, ID_Number);
                pstmt.setString(3, name);
                pstmt.setString(4, gender);
                pstmt.setInt(5, age);
                pstmt.setDouble(6, deposit);
                pstmt.setString(7, contact);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Added Successfully");
                setVisible(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        if (e.getSource() == b2) {
            setVisible(false);
        }
    }
}
