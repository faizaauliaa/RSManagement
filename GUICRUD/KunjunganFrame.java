package GUICRUD;

// KunjunganFrame.java - CRUD GUI untuk Tabel Kunjungan

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class KunjunganFrame extends JFrame {
    private JTextField txtVisitID, txtPatientID, txtDoctorID, txtTanggal, txtKeluhan, txtStatus, txtCari;
    private JTable table;
    private DefaultTableModel model;

    public KunjunganFrame() {
        setTitle("Manajemen Kunjungan");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        txtVisitID = new JTextField(); txtPatientID = new JTextField();
        txtDoctorID = new JTextField(); txtTanggal = new JTextField();
        txtKeluhan = new JTextField(); txtStatus = new JTextField();

        formPanel.add(new JLabel("ID Kunjungan:")); formPanel.add(txtVisitID);
        formPanel.add(new JLabel("ID Pasien:")); formPanel.add(txtPatientID);
        formPanel.add(new JLabel("ID Dokter:")); formPanel.add(txtDoctorID);
        formPanel.add(new JLabel("Tanggal Kunjungan (YYYY-MM-DD):")); formPanel.add(txtTanggal);
        formPanel.add(new JLabel("Keluhan:")); formPanel.add(txtKeluhan);
        formPanel.add(new JLabel("Status:")); formPanel.add(txtStatus);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnUpdate = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear = new JButton("Bersih");
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Pasien", "Dokter", "Tanggal", "Keluhan", "Status"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        txtCari = new JTextField();
        JButton btnCari = new JButton("Cari");
        searchPanel.add(new JLabel("Cari ID/Nama Pasien: "), BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);
        add(searchPanel, BorderLayout.SOUTH);

        loadData("");

        btnAdd.addActionListener(e -> tambahKunjungan());
        btnUpdate.addActionListener(e -> ubahKunjungan());
        btnDelete.addActionListener(e -> hapusKunjungan());
        btnClear.addActionListener(e -> bersihForm());
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtVisitID.setText(model.getValueAt(row, 0).toString());
                txtPatientID.setText(model.getValueAt(row, 1).toString());
                txtDoctorID.setText(model.getValueAt(row, 2).toString());
                txtTanggal.setText(model.getValueAt(row, 3).toString());
                txtKeluhan.setText(model.getValueAt(row, 4).toString());
                txtStatus.setText(model.getValueAt(row, 5).toString());
                txtVisitID.setEditable(false);
            }
        });
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String query = "SELECT * FROM Kunjungan WHERE visit_id LIKE ? OR patient_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("visit_id"), rs.getString("patient_id"), rs.getString("doctor_id"),
                    rs.getDate("tgl_kunjungan"), rs.getString("keluhan"), rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    private void tambahKunjungan() {
        String sql = "INSERT INTO Kunjungan VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtVisitID.getText());
            stmt.setString(2, txtPatientID.getText());
            stmt.setString(3, txtDoctorID.getText());
            stmt.setDate(4, Date.valueOf(txtTanggal.getText()));
            stmt.setString(5, txtKeluhan.getText());
            stmt.setString(6, txtStatus.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah: " + e.getMessage());
        }
    }

    private void ubahKunjungan() {
        String sql = "UPDATE Kunjungan SET patient_id=?, doctor_id=?, tgl_kunjungan=?, keluhan=?, status=? WHERE visit_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtPatientID.getText());
            stmt.setString(2, txtDoctorID.getText());
            stmt.setDate(3, Date.valueOf(txtTanggal.getText()));
            stmt.setString(4, txtKeluhan.getText());
            stmt.setString(5, txtStatus.getText());
            stmt.setString(6, txtVisitID.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }

    private void hapusKunjungan() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kunjungan WHERE visit_id=?")) {
                stmt.setString(1, txtVisitID.getText());
                stmt.executeUpdate();
                loadData("");
                bersihForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
            }
        }
    }

    private void bersihForm() {
        txtVisitID.setText(""); txtVisitID.setEditable(true);
        txtPatientID.setText(""); txtDoctorID.setText("");
        txtTanggal.setText(""); txtKeluhan.setText(""); txtStatus.setText("");
    }
}
