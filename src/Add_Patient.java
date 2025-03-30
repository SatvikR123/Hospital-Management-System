import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Add_Patient extends JFrame implements ActionListener {
    JComboBox<String> combobox;
    JTextField txtNumber, txtName, txtAge, txtDeposit, txtContact;
    JRadioButton r1, r2;
    JButton b1, b2;
    JLabel imageLabel;

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
        b1.addActionListener(this);
        panel.add(b1);

        // Back Button
        b2 = new JButton("Back");
        b2.setBounds(260, 430, 120, 30);
        b2.addActionListener(this);
        panel.add(b2);

        // Image Label (Right Corner)
        ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\Desktop\\hospital management system\\Hospital-Management-System\\patient image.png");
        Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Adjust size if needed
        imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setBounds(530, 80, 250, 250); // Adjust position and size
        panel.add(imageLabel);

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
            JOptionPane.showMessageDialog(null, "Patient added successfully!");
        }
        if (e.getSource() == b2) {
            setVisible(false);
        }
    }
}
