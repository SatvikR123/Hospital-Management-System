import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillManager {
    private Conn connection;

    public BillManager() {
        this.connection = new Conn();
    }

    public boolean createBill(Bill bill) {
        String query = "INSERT INTO Bills (patient_id, appointment_id, treatment_id, " +
                "issue_date, due_date, total_amount, paid_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, bill.getPatientId());
            setNullableInt(pstmt, 2, bill.getAppointmentId());
            setNullableInt(pstmt, 3, bill.getTreatmentId());
            pstmt.setDate(4, bill.getIssueDate());
            pstmt.setDate(5, bill.getDueDate());
            pstmt.setDouble(6, bill.getTotalAmount());
            pstmt.setDouble(7, bill.getPaidAmount());
            pstmt.setString(8, bill.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int billId = rs.getInt(1);
                        // Save all bill items
                        for (BillItem item : bill.getItems()) {
                            addBillItem(billId, item);
                        }
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating bill: " + e.getMessage());
            return false;
        }
    }

    private void setNullableInt(PreparedStatement pstmt, int index, Integer value)
            throws SQLException {
        if (value != null) {
            pstmt.setInt(index, value);
        } else {
            pstmt.setNull(index, Types.INTEGER);
        }
    }

    private boolean addBillItem(int billId, BillItem item) {
        String query = "INSERT INTO Bill_Items (bill_id, description, quantity, unit_price) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, billId);
            pstmt.setString(2, item.getDescription());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding bill item: " + e.getMessage());
            return false;
        }
    }

    public List<Bill> getBillsByPatient(int patientId) {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM Bills WHERE patient_id = ? ORDER BY issue_date DESC";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Bill bill = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("appointment_id"),
                        rs.getInt("treatment_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("paid_amount"),
                        rs.getString("status")
                );

                // Load bill items
                loadBillItems(bill);
                bills.add(bill);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bills: " + e.getMessage());
        }
        return bills;
    }

    private void loadBillItems(Bill bill) {
        String query = "SELECT * FROM Bill_Items WHERE bill_id = ?";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, bill.getBillId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bill.addItem(new BillItem(
                        rs.getInt("item_id"),
                        rs.getInt("bill_id"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading bill items: " + e.getMessage());
        }
    }
}


class Bill {
    private int billId;
    private int patientId;
    private Integer appointmentId;
    private Integer treatmentId;
    private Date issueDate;
    private Date dueDate;
    private double totalAmount;
    private double paidAmount;
    private String status;
    private List<BillItem> items;

    public Bill(int billId, int patientId, Integer appointmentId, Integer treatmentId,
                Date issueDate, Date dueDate, double totalAmount,
                double paidAmount, String status) {
        this.billId = billId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.treatmentId = treatmentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public void addItem(BillItem item) {
        this.items.add(item);
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Integer treatmentId) {
        this.treatmentId = treatmentId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }
}

// BillItem.java
class BillItem {
    private int itemId;
    private int billId;
    private String description;
    private int quantity;
    private double unitPrice;

    public BillItem(int itemId, int billId, String description,
                    int quantity, double unitPrice) {
        this.itemId = itemId;
        this.billId = billId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotal() {
        return quantity * unitPrice;
    }
}
