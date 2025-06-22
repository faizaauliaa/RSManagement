package GUICRUD;

// DokterFrame.java - CRUD GUI untuk tabel Dokter

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DokterFrame extends JFrame {
    private JTextField txtID, txtNama, txtSpesialisasi, txtJadwal, txtCari;
    private JTable table;
    private DefaultTableModel model;

    public DokterFrame() {
        setTitle("Manajemen Dokter");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Dokter"));

        formPanel.add(new JLabel("ID Dokter:"));
        txtID = new JTextField();
        formPanel.add(txtID);

        formPanel.add(new JLabel("Nama Dokter:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);

        formPanel.add(new JLabel("Spesialisasi:"));
        txtSpesialisasi = new JTextField();
        formPanel.add(txtSpesialisasi);

        formPanel.add(new JLabel("Jadwal Praktik:"));
        txtJadwal = new JTextField();
        formPanel.add(txtJadwal);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnUpdate = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear = new JButton("Bersihkan");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID Dokter", "Nama", "Spesialisasi", "Jadwal Praktik"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        txtCari = new JTextField();
        JButton btnCari = new JButton("Cari");
        searchPanel.add(new JLabel("Cari ID/Nama: "), BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);

        add(searchPanel, BorderLayout.SOUTH);

        loadData("");

        btnAdd.addActionListener(e -> tambahDokter());
        btnUpdate.addActionListener(e -> ubahDokter());
        btnDelete.addActionListener(e -> hapusDokter());
        btnClear.addActionListener(e -> bersihForm());
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row >= 0) {
                    txtID.setText(model.getValueAt(row, 0).toString());
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtSpesialisasi.setText(model.getValueAt(row, 2).toString());
                    txtJadwal.setText(model.getValueAt(row, 3).toString());
                    txtID.setEditable(false);
                }
            }
        });
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM Dokter WHERE doctor_id LIKE ? OR nama LIKE ?")) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("doctor_id"),
                        rs.getString("nama"),
                        rs.getString("spesialisasi"),
                        rs.getString("jadwal_praktik")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void tambahDokter() {
        if (txtID.getText().isEmpty() || txtNama.getText().isEmpty() || txtSpesialisasi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, Nama, dan Spesialisasi wajib diisi!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Dokter (doctor_id, nama, spesialisasi, jadwal_praktik) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, txtID.getText());
            stmt.setString(2, txtNama.getText());
            stmt.setString(3, txtSpesialisasi.getText());
            stmt.setString(4, txtJadwal.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal tambah data: " + ex.getMessage());
        }
    }

    private void ubahDokter() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diubah terlebih dahulu!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Dokter SET nama=?, spesialisasi=?, jadwal_praktik=? WHERE doctor_id=?")) {
            stmt.setString(1, txtNama.getText());
            stmt.setString(2, txtSpesialisasi.getText());
            stmt.setString(3, txtJadwal.getText());
            stmt.setString(4, txtID.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal ubah data: " + ex.getMessage());
        }
    }

    private void hapusDokter() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus dokter ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Dokter WHERE doctor_id=?")) {
                stmt.setString(1, txtID.getText());
                stmt.executeUpdate();
                loadData("");
                bersihForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal hapus data: " + ex.getMessage());
            }
        }
    }

    private void bersihForm() {
        txtID.setText("");
        txtNama.setText("");
        txtSpesialisasi.setText("");
        txtJadwal.setText("");
        txtID.setEditable(true);
    }
}

