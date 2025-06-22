package GUICRUD;

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DetailResepFrame extends JFrame {
    private JTextField txtDetailID, txtResepID, txtObatID, txtDosis, txtFrekuensi, txtDurasi, txtJumlah, txtInstruksi, txtCari;
    private JTable table;
    private DefaultTableModel model;

    public DetailResepFrame() {
        setTitle("Manajemen Detail Resep");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.add(new JLabel("Detail ID:"));
        txtDetailID = new JTextField(); formPanel.add(txtDetailID);
        formPanel.add(new JLabel("Resep ID:"));
        txtResepID = new JTextField(); formPanel.add(txtResepID);
        formPanel.add(new JLabel("Obat ID:"));
        txtObatID = new JTextField(); formPanel.add(txtObatID);
        formPanel.add(new JLabel("Dosis:"));
        txtDosis = new JTextField(); formPanel.add(txtDosis);
        formPanel.add(new JLabel("Frekuensi:"));
        txtFrekuensi = new JTextField(); formPanel.add(txtFrekuensi);
        formPanel.add(new JLabel("Durasi (hari):"));
        txtDurasi = new JTextField(); formPanel.add(txtDurasi);
        formPanel.add(new JLabel("Jumlah:"));
        txtJumlah = new JTextField(); formPanel.add(txtJumlah);
        formPanel.add(new JLabel("Instruksi Khusus:"));
        txtInstruksi = new JTextField(); formPanel.add(txtInstruksi);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnUpdate = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear = new JButton("Bersihkan");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Detail ID", "Resep ID", "Obat ID", "Dosis", "Frekuensi", "Durasi", "Jumlah", "Instruksi"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        txtCari = new JTextField();
        JButton btnCari = new JButton("Cari");
        searchPanel.add(new JLabel("Cari Detail ID / Resep ID / Obat ID: "), BorderLayout.WEST);
        searchPanel.add(txtCari, BorderLayout.CENTER);
        searchPanel.add(btnCari, BorderLayout.EAST);
        add(searchPanel, BorderLayout.SOUTH);

        loadData("");

        btnAdd.addActionListener(e -> tambahDetailResep());
        btnUpdate.addActionListener(e -> ubahDetailResep());
        btnDelete.addActionListener(e -> hapusDetailResep());
        btnClear.addActionListener(e -> bersihForm());
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtDetailID.setText(model.getValueAt(row, 0).toString());
                    txtResepID.setText(model.getValueAt(row, 1).toString());
                    txtObatID.setText(model.getValueAt(row, 2).toString());
                    txtDosis.setText(model.getValueAt(row, 3).toString());
                    txtFrekuensi.setText(model.getValueAt(row, 4).toString());
                    txtDurasi.setText(model.getValueAt(row, 5).toString());
                    txtJumlah.setText(model.getValueAt(row, 6).toString());
                    txtInstruksi.setText(model.getValueAt(row, 7).toString());
                    txtDetailID.setEditable(false);
                }
            }
        });
    }

    private void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT * FROM DetailResep WHERE detail_id LIKE ? OR resep_id LIKE ? OR obat_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
            stmt.setString(3, kw);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("detail_id"),
                        rs.getString("resep_id"),
                        rs.getString("obat_id"),
                        rs.getString("dosis"),
                        rs.getString("frekuensi"),
                        rs.getInt("durasi_hari"),
                        rs.getInt("jumlah"),
                        rs.getString("instruksi_khusus")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    private void tambahDetailResep() {
        if (!validasiForm()) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO DetailResep VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, txtDetailID.getText());
            stmt.setString(2, txtResepID.getText());
            stmt.setString(3, txtObatID.getText());
            stmt.setString(4, txtDosis.getText());
            stmt.setString(5, txtFrekuensi.getText());
            stmt.setInt(6, Integer.parseInt(txtDurasi.getText()));
            stmt.setInt(7, Integer.parseInt(txtJumlah.getText()));
            stmt.setString(8, txtInstruksi.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambah.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah data: " + e.getMessage());
        }
    }

    private void ubahDetailResep() {
        if (!validasiForm()) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE DetailResep SET resep_id=?, obat_id=?, dosis=?, frekuensi=?, durasi_hari=?, jumlah=?, instruksi_khusus=? WHERE detail_id=?")) {
            stmt.setString(1, txtResepID.getText());
            stmt.setString(2, txtObatID.getText());
            stmt.setString(3, txtDosis.getText());
            stmt.setString(4, txtFrekuensi.getText());
            stmt.setInt(5, Integer.parseInt(txtDurasi.getText()));
            stmt.setInt(6, Integer.parseInt(txtJumlah.getText()));
            stmt.setString(7, txtInstruksi.getText());
            stmt.setString(8, txtDetailID.getText());
            stmt.executeUpdate();
            loadData("");
            bersihForm();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah data: " + e.getMessage());
        }
    }

    private void hapusDetailResep() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM DetailResep WHERE detail_id=?")) {
                stmt.setString(1, txtDetailID.getText());
                stmt.executeUpdate();
                loadData("");
                bersihForm();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus data: " + e.getMessage());
            }
        }
    }

    private boolean validasiForm() {
        if (txtDetailID.getText().isEmpty() || txtResepID.getText().isEmpty() || txtObatID.getText().isEmpty() ||
            txtDosis.getText().isEmpty() || txtFrekuensi.getText().isEmpty() || txtDurasi.getText().isEmpty() ||
            txtJumlah.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi kecuali instruksi khusus.");
            return false;
        }
        try {
            Integer.parseInt(txtDurasi.getText());
            Integer.parseInt(txtJumlah.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Durasi dan Jumlah harus berupa angka.");
            return false;
        }
        return true;
    }

    private void bersihForm() {
        txtDetailID.setText(""); txtDetailID.setEditable(true);
        txtResepID.setText(""); txtObatID.setText(""); txtDosis.setText("");
        txtFrekuensi.setText(""); txtDurasi.setText(""); txtJumlah.setText(""); txtInstruksi.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DetailResepFrame().setVisible(true);
        });
    }
}

