package GUICRUD;

// RekamMedisFrame.java - CRUD GUI untuk Tabel Rekam Medis

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RekamMedisFrame extends JFrame {
    private JTextField txtID, txtVisitID, txtDiagnosa;
    private JTextArea txtTindakan, txtCatatan;
    private JTable table;
    private DefaultTableModel model;

    public RekamMedisFrame() {
        setTitle("Manajemen Rekam Medis");
        setSize(800, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("ID Rekam Medis:"));
        txtID = new JTextField(); formPanel.add(txtID);
        formPanel.add(new JLabel("ID Kunjungan:"));
        txtVisitID = new JTextField(); formPanel.add(txtVisitID);
        formPanel.add(new JLabel("Diagnosa:"));
        txtDiagnosa = new JTextField(); formPanel.add(txtDiagnosa);

        txtTindakan = new JTextArea(3, 20);
        txtCatatan = new JTextArea(3, 20);

        JPanel textAreaPanel = new JPanel(new GridLayout(2, 2));
        textAreaPanel.add(new JLabel("Tindakan:"));
        textAreaPanel.add(new JScrollPane(txtTindakan));
        textAreaPanel.add(new JLabel("Catatan:"));
        textAreaPanel.add(new JScrollPane(txtCatatan));

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnUpdate = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear = new JButton("Bersihkan");
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(textAreaPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID Rekam Medis", "ID Kunjungan", "Diagnosa", "Tindakan", "Catatan"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        btnAdd.addActionListener(e -> tambahRekam());
        btnUpdate.addActionListener(e -> ubahRekam());
        btnDelete.addActionListener(e -> hapusRekam());
        btnClear.addActionListener(e -> bersihForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtID.setText(model.getValueAt(row, 0).toString());
                txtVisitID.setText(model.getValueAt(row, 1).toString());
                txtDiagnosa.setText(model.getValueAt(row, 2).toString());
                txtTindakan.setText(model.getValueAt(row, 3).toString());
                txtCatatan.setText(model.getValueAt(row, 4).toString());
                txtID.setEditable(false);
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM RekamMedis")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("rekmed_id"),
                        rs.getString("visit_id"),
                        rs.getString("diagnosa"),
                        rs.getString("tindakan"),
                        rs.getString("catatan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }

    private void tambahRekam() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO RekamMedis VALUES (?, ?, ?, ?, ?);")) {
            stmt.setString(1, txtID.getText());
            stmt.setString(2, txtVisitID.getText());
            stmt.setString(3, txtDiagnosa.getText());
            stmt.setString(4, txtTindakan.getText());
            stmt.setString(5, txtCatatan.getText());
            stmt.executeUpdate();
            loadData();
            bersihForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah: " + e.getMessage());
        }
    }

    private void ubahRekam() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RekamMedis SET visit_id=?, diagnosa=?, tindakan=?, catatan=? WHERE rekmed_id=?;")) {
            stmt.setString(1, txtVisitID.getText());
            stmt.setString(2, txtDiagnosa.getText());
            stmt.setString(3, txtTindakan.getText());
            stmt.setString(4, txtCatatan.getText());
            stmt.setString(5, txtID.getText());
            stmt.executeUpdate();
            loadData();
            bersihForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }

    private void hapusRekam() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM RekamMedis WHERE rekmed_id=?;")) {
                stmt.setString(1, txtID.getText());
                stmt.executeUpdate();
                loadData();
                bersihForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
            }
        }
    }

    private void bersihForm() {
        txtID.setText(""); txtID.setEditable(true);
        txtVisitID.setText(""); txtDiagnosa.setText("");
        txtTindakan.setText(""); txtCatatan.setText("");
    }
}

