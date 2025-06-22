package GUICRUD;

// File: ObatFrame.java
import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ObatFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfId, tfNama, tfDosis, tfHarga, tfSearch;

    public ObatFrame() {
        setTitle("Manajemen Obat");
        setSize(800, 400);
        setLocationRelativeTo(null);

        JPanel panelInput = new JPanel(new GridLayout(5, 2));
        panelInput.add(new JLabel("ID Obat:"));
        tfId = new JTextField();
        panelInput.add(tfId);
        panelInput.add(new JLabel("Nama Obat:"));
        tfNama = new JTextField();
        panelInput.add(tfNama);
        panelInput.add(new JLabel("Dosis:"));
        tfDosis = new JTextField();
        panelInput.add(tfDosis);
        panelInput.add(new JLabel("Harga:"));
        tfHarga = new JTextField();
        panelInput.add(tfHarga);
        panelInput.add(new JLabel("Cari Nama:"));
        tfSearch = new JTextField();
        panelInput.add(tfSearch);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Nama", "Dosis", "Harga"});
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panelBtn = new JPanel();
        JButton btnAdd = new JButton("Tambah"), btnUpdate = new JButton("Ubah"),
                btnDelete = new JButton("Hapus"), btnRefresh = new JButton("Refresh");
        panelBtn.add(btnAdd); panelBtn.add(btnUpdate);
        panelBtn.add(btnDelete); panelBtn.add(btnRefresh);

        setLayout(new BorderLayout());
        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBtn, BorderLayout.SOUTH);

        loadData();

        btnAdd.addActionListener(e -> tambahObat());
        btnUpdate.addActionListener(e -> ubahObat());
        btnDelete.addActionListener(e -> hapusObat());
        btnRefresh.addActionListener(e -> loadData());
        tfSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchObat(tfSearch.getText());
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                tfId.setText(model.getValueAt(row, 0).toString());
                tfNama.setText(model.getValueAt(row, 1).toString());
                tfDosis.setText(model.getValueAt(row, 2).toString());
                tfHarga.setText(model.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Obat")) {
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tambahObat() {
        String sql = "INSERT INTO Obat VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfId.getText());
            ps.setString(2, tfNama.getText());
            ps.setString(3, tfDosis.getText());
            ps.setDouble(4, Double.parseDouble(tfHarga.getText()));
            ps.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah data: " + e.getMessage());
        }
    }

    private void ubahObat() {
        String sql = "UPDATE Obat SET nama_obat=?, dosis=?, harga=? WHERE obat_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfNama.getText());
            ps.setString(2, tfDosis.getText());
            ps.setDouble(3, Double.parseDouble(tfHarga.getText()));
            ps.setString(4, tfId.getText());
            ps.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        }
    }

    private void hapusObat() {
        String sql = "DELETE FROM Obat WHERE obat_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tfId.getText());
            ps.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
        }
    }

    private void searchObat(String keyword) {
        String sql = "SELECT * FROM Obat WHERE nama_obat LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
