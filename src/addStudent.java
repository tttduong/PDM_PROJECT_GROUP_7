import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class addStudent extends JFrame {
    public JPanel Main;
    private JTextField id;
    private JTextField name;
    private JTextField dob;
    private JTextField tuition;
    private JTextField department;
    private JButton cancelButton;
    private JButton saveButton;
    private Lecturer lecturer;
    private Connection con;


    public addStudent(Lecturer lecturer) {
        this.lecturer = lecturer;

        setTitle("Add New Student");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewStudent();
            }
        });
    }

    private void addNewStudent() {
        String student_id = id.getText();
        String studentName = name.getText();
        String doB = dob.getText();
        String tuitionFee = tuition.getText();
        String studentDepartment = department.getText();

        if (student_id.isEmpty() || studentName.isEmpty() || doB.isEmpty() || studentDepartment.isEmpty() || tuitionFee.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all the fields");
            return;
        }

        StudentInfo info = addNewStudentToDatabase(student_id, studentName, doB, tuitionFee, studentDepartment);

        if (info != null) {
            JOptionPane.showMessageDialog(null, "Student added successfully");
            lecturer.loadSelectedTable("student");
            dispose();
        }
    }

    public void connect() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser("sa");
        ds.setPassword("Quenroi6212@");
        ds.setServerName("DESKTOP-O34QFU6\\SQLEXPRESS");
        ds.setPortNumber(1433);
        ds.setDatabaseName("Final_Project_PDM");
        ds.setEncrypt(false);
        try {
            con = ds.getConnection();
            System.out.println("Connection successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private StudentInfo addNewStudentToDatabase(String student_id, String studentName, String doB, String tuitionFee, String studentDepartment) {
        StudentInfo info = null;
        Connection con = null;
        PreparedStatement pst = null;
//        connect();
        try {
            con = DriverManager.getConnection(
                    "jdbc:sqlserver://DESKTOP-O34QFU6\\SQLEXPRESS; databaseName=Final_Project_PDM; integratedSecurity=true"
            );
            String sql = "INSERT INTO student (student_id, studentName, doB, tuitionFee, studentDepartment) VALUES (?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, student_id);
            pst.setString(2, studentName);
            pst.setString(3, doB);
            pst.setString(4, tuitionFee);
            pst.setString(5, studentDepartment);

            int addedRows = pst.executeUpdate();
            if (addedRows > 0) {
                info = new StudentInfo();
                info.student_id = student_id;
                info.studentName = studentName;
                info.doB = doB;
                info.tuitionFee = tuitionFee;
                info.studentDepartment = studentDepartment;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage());
        } finally {
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return info;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                addStudent form = new addStudent(null);
                form.setContentPane(form.Main);
                form.setVisible(true);
            }
        });
    }
}
