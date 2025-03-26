import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Reception extends JFrame {
    Reception(){
        JPanel panel= new JPanel();
        panel.setBounds(5,160,1525,670);
        panel.setBackground(new Color(109,164,170));
        panel.setLayout(null);
        add(panel);

        JPanel panel1= new JPanel();
        panel1.setBounds(5,5,1525,150);
        panel1.setBackground(new Color(109,164,170));
        panel1.setLayout(null);
        add(panel1);

        JButton btn=new JButton("Add new Patient");
        btn.setBounds(30, 15, 200, 30);
        btn.setBackground(new Color(246,215,118));
        panel1.add(btn);
        btn.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e){
                  new New_Patient();

            }
        });

        JButton btn2=new JButton("Update Patient details");
        btn2.setBounds(30, 58, 200, 30);
        btn2.setBackground(new Color(246,215,118));
        panel1.add(btn2);
        btn2.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e1){

            }
        });

        JButton btn3=new JButton(" patients info");
        btn3.setBounds(30, 100, 200, 30);
        btn3.setBackground(new Color(246,215,118));
        panel1.add(btn3);
        btn3.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });
        
        JButton btn4=new JButton(" Generate Bill ");
        btn4.setBounds(270,15, 200, 30);
        btn4.setBackground(new Color(246,215,118));
        panel1.add(btn4);
        btn4.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });

        JButton btn5=new JButton("Book Appointment ");
        btn5.setBounds(270,58 , 200, 30);
        btn5.setBackground(new Color(246,215,118));
        panel1.add(btn5);
        btn5.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });

        JButton btn6=new JButton("Reschedule ");
        btn6.setBounds(270,100, 200, 30);
        btn6.setBackground(new Color(246,215,118));
        panel1.add(btn6);
        btn6.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });

        JButton btn7=new JButton("Cancel Appointments");
        btn7.setBounds(510,15, 200, 30);
        btn7.setBackground(new Color(246,215,118));
        panel1.add(btn7);
        btn7.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });

        JButton btn8=new JButton(" View doctor details");
        btn8.setBounds(510, 58, 200, 30);
        btn8.setBackground(new Color(246,215,118));
        panel1.add(btn8);
        btn8.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });

        JButton btn9=new JButton("view  treatment log");
        btn9.setBounds(510, 100, 200, 30);
        btn9.setBackground(new Color(246,215,118));
        panel1.add(btn9);
        btn9.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){

            }
        });

        JButton btn10=new JButton("Logout");
        btn10.setBounds(750,15, 200, 30);
        btn10.setBackground(new Color(246,215,118));
        panel1.add(btn10);
        btn10.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e2){
               System.exit(10);
            }
        });




        getContentPane().setBackground(Color.WHITE);
        setSize(1950,1090);
        setVisible(true);
        setLayout(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Reception();
    }
}
