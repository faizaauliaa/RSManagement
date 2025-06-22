
package GUICRUD;

// ResepFrame.java - CRUD GUI untuk Tabel Resep

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ResepFrame extends JFrame {
    private JTextField txtID, txtVisitID, txtObatID, txtTanggal, txtJumlah, txtCari;
    private JTable table;
    private DefaultTableModel model;

    public ResepFrame() {
        setTitle("Manajemen Resep");
        setSize(750, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(6, 2));
        formPanel.add(new JLabel("ID Resep:"));
        txtID = new JTextField(); formPanel.add(txtID);
        formPanel.add(new JLabel("ID Kunjungan:"));
        txtVisitID = new JTextField(); formPanel.add(txtVisitID);
        formPanel.add(new JLabel("ID Obat:"));
        txtObatID = new JTextField(); formPanel.add(txtObatID);
        formPanel.add(new JLabel("Tanggal Resep (YYYY-MM-DD):"));
        txtTanggal = new JTextField(); formPanel.add(txtTanggal);
        formPanel.add(new JLabel("Jumlah:"));
        txtJumlah = new JTextField(); formPanel.add(txtJumlah);

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

        model = new DefaultTableModel(new String[]{"ID", "Kunjungan", "Obat", "Tanggal", "Jumlah"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        txtCari = new JTextField();
        JButton btnCari = new JButton("Cari");
        searchPanel.add(new JLabel("Cari ID/Obat:"), BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);
        add(searchPanel, BorderLayout.SOUTH);

        loadData("");

        btnAdd.addActionListener(e -> tambahResep());
        btnUpdate.addActionListener(e -> ubahResep());
        btnDelete.addActionListener(e -> hapusResep());
        btnClear.addActionListener(e -> bersihForm());
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtID.setText(model.getValueAt(row, 0).toString());
                txtVisitID.setText(model.getValueAt(row, 1).toString());
                txtObatID.setText(model.getValueAt(row, 2).toString());
                txtTanggal.setText(model.getValueAt(row, 3).toString());
                txtJumlah.setText(model.getValueAt(row, 4).toString());
                txtID.setEditable(false);
            }
        });
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Resep WHERE resep_id LIKE ? OR obat_id LIKE ?")) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("resep_id"),
                    rs.getString("visit_id"),
                    rs.getString("obat_id"),
                    rs.getDate("tgl_resep"),
                    rs.getInt("jumlah")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    private void tambahResep() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Resep VALUES (?, ?, ?, ?, ?);")) {
            stmt.setString(1, txtID.getText());
            stmt.setString(2, txtVisitID.getText());
            stmt.setString(3, txtObatID.getText());
            stmt.setDate(4, Date.valueOf(txtTanggal.getText()));
            stmt.setInt(5, Integer.parseInt(txtJumlah.getText()));
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah: " + e.getMessage());
        }
    }

    private void ubahResep() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Resep SET visit_id=?, obat_id=?, tgl_resep=?, jumlah=? WHERE resep_id=?")) {
            stmt.setString(1, txtVisitID.getText());
            stmt.setString(2, txtObatID.getText());
            stmt.setDate(3, Date.valueOf(txtTanggal.getText()));
            stmt.setInt(4, Integer.parseInt(txtJumlah.getText()));
            stmt.setString(5, txtID.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }

    private void hapusResep() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Resep WHERE resep_id=?")) {
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
        txtVisitID.setText("");
        txtObatID.setText("");
        txtTanggal.setText("");
        txtJumlah.setText("");
    }
}

