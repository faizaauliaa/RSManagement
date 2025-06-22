package GUICRUD;

// PembayaranFrame.java - CRUD GUI untuk Tabel Pembayaran

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PembayaranFrame extends JFrame {
    private JTextField txtID, txtVisitID, txtTotal, txtMetode, txtTanggal, txtCari;
    private JTable table;
    private DefaultTableModel model;

    public PembayaranFrame() {
        setTitle("Manajemen Pembayaran");
        setSize(750, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(new JLabel("ID Pembayaran:"));
        txtID = new JTextField(); formPanel.add(txtID);
        formPanel.add(new JLabel("ID Kunjungan:"));
        txtVisitID = new JTextField(); formPanel.add(txtVisitID);
        formPanel.add(new JLabel("Total Biaya:"));
        txtTotal = new JTextField(); formPanel.add(txtTotal);
        formPanel.add(new JLabel("Metode Bayar:"));
        txtMetode = new JTextField(); formPanel.add(txtMetode);
        formPanel.add(new JLabel("Tanggal Bayar (YYYY-MM-DD):"));
        txtTanggal = new JTextField(); formPanel.add(txtTanggal);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnUpdate = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear = new JButton("Bersihkan");
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Kunjungan", "Total", "Metode", "Tanggal"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        txtCari = new JTextField();
        JButton btnCari = new JButton("Cari");
        searchPanel.add(new JLabel("Cari ID Pembayaran/Kunjungan: "), BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);
        add(searchPanel, BorderLayout.SOUTH);

        loadData("");

        btnAdd.addActionListener(e -> tambahPembayaran());
        btnUpdate.addActionListener(e -> ubahPembayaran());
        btnDelete.addActionListener(e -> hapusPembayaran());
        btnClear.addActionListener(e -> bersihForm());
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtID.setText(model.getValueAt(row, 0).toString());
                txtVisitID.setText(model.getValueAt(row, 1).toString());
                txtTotal.setText(model.getValueAt(row, 2).toString());
                txtMetode.setText(model.getValueAt(row, 3).toString());
                txtTanggal.setText(model.getValueAt(row, 4).toString());
                txtID.setEditable(false);
            }
        });
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pembayaran WHERE bayar_id LIKE ? OR visit_id LIKE ?")) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getString(4), rs.getDate(5)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    private void tambahPembayaran() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Pembayaran VALUES (?, ?, ?, ?, ?);")) {
            stmt.setString(1, txtID.getText());
            stmt.setString(2, txtVisitID.getText());
            stmt.setDouble(3, Double.parseDouble(txtTotal.getText()));
            stmt.setString(4, txtMetode.getText());
            stmt.setDate(5, Date.valueOf(txtTanggal.getText()));
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah: " + e.getMessage());
        }
    }

    private void ubahPembayaran() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Pembayaran SET visit_id=?, total_biaya=?, metode_bayar=?, tgl_bayar=? WHERE bayar_id=?;")) {
            stmt.setString(1, txtVisitID.getText());
            stmt.setDouble(2, Double.parseDouble(txtTotal.getText()));
            stmt.setString(3, txtMetode.getText());
            stmt.setDate(4, Date.valueOf(txtTanggal.getText()));
            stmt.setString(5, txtID.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }

    private void hapusPembayaran() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Pembayaran WHERE bayar_id=?;")) {
                stmt.setString(1, txtID.getText());
                stmt.executeUpdate();
                loadData("");
                bersihForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
            }
        }
    }

    private void bersihForm() {
        txtID.setText(""); txtID.setEditable(true);
        txtVisitID.setText(""); txtTotal.setText("");
        txtMetode.setText(""); txtTanggal.setText("");
    }
}

