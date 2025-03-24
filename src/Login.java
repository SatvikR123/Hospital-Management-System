
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login  extends JFrame{
    JTextField text;
    JTextField text2;
    Login(){
        JLabel username= new JLabel("username");
        username.setBounds(40,20,100,30);
        username.setFont(new Font("Tahoma",Font.BOLD,20));
        add(username);

        JLabel password= new JLabel("password");
        password.setBounds(40,70,100,30);
        password.setFont(new Font("Tahoma",Font.BOLD,20));
        add(password);

        text=new JTextField();
        text.setBounds(150,20,150,30);
        text.setFont(new Font("Tahoma",Font.BOLD,20));
        add(text);

        text2=new JPasswordField();
        text2.setBounds(150,70,150,30);
        text2.setFont(new Font("Tahoma",Font.BOLD,20));
        add(text2);
        
       JButton b1=new JButton("Login");
       b1.setBounds(150,120,100,30);
       b1.setFont(new Font("Tahoma",Font.BOLD,20));
       add(b1);
      
       JButton b2=new JButton("Cancel");
       b2.setBounds(150,160,120,30);
       b2.setFont(new Font("Tahoma",Font.BOLD,20));
       add(b2);


        setSize(750,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(750,270);
        setLayout(null);
        setVisible(true);


    }
    public static void main(String[] args) {
        new Login();
        
    }
}
