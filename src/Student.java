import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Student extends JFrame{
    private JPanel Main;
    private JTable table1;
    private JButton signOutButton;
    private JComboBox<String> comboBox1;
    private JButton searchButton;
    private JTextField textField1;

    private Connection con;
    private PreparedStatement pst;

    // Map to hold display names and corresponding table names
    private final Map<String, String> tableMap = new HashMap<>();

    public Student() {
        connect();
        table_load();

        // Define the fields to populate in the comboBox1
        String[] displayFields = {"Examination", "Student Grade", "View"};
        String[] tableNames = {"Examination", "grade_student", "View_Examination"};

        // Populate the map
        for (int i = 0; i < displayFields.length; i++) {
            tableMap.put(displayFields[i], tableNames[i]);
        }

        comboBox1.setModel(new JComboBox<>(displayFields).getModel());

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String displayValue = (String) comboBox1.getSelectedItem();
                String tableName = tableMap.get(displayValue);
                loadSelectedTable(tableName);
            }
        });

        // Add action listener for the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = textField1.getText();
                String displayValue = (String) comboBox1.getSelectedItem();
                String tableName = tableMap.get(displayValue);
                searchInTable(tableName, searchTerm);
            }
        }

        );
        // Add action listener for the sign_out button
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sign_out();
            }
        });
    }

    public JPanel getMainPanel() {
        return Main;
    }

    public static void main(String[] args) {
        initComponents();
    }
    public static void initComponents() {
        JFrame frame = new JFrame("Student");
        frame.setContentPane(new Student().getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
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
    void sign_out(){
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(Main);
        topFrame.dispose();

        Login lg = new Login();
        lg.setVisible(true);
    }

    void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM student");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void loadSelectedTable(String tableName) {
        try {
            pst = con.prepareStatement("SELECT * FROM " + tableName);
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void searchInTable(String tableName, String searchTerm) {
        try {
            // Get metadata to retrieve column names
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            // Construct the query dynamically
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ");
            boolean first = true;
            while (columns.next()) {
                if (!first) {
                    queryBuilder.append(" OR ");
                }
                String columnName = columns.getString("COLUMN_NAME");
                queryBuilder.append(columnName).append(" LIKE ?");
                first = false;
            }

            pst = con.prepareStatement(queryBuilder.toString());

            // Set the search term for all columns
            columns.beforeFirst();
            int index = 1;
            while (columns.next()) {
                pst.setString(index++, "%" + searchTerm + "%");
            }

            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
