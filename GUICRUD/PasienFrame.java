
package GUICRUD;

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PasienFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfId, tfNama, tfNIK, tfCari;
    private JComboBox<String> cbJK, cbGol;
    private JFormattedTextField tfTglLahir;

    public PasienFrame() {
        setTitle("Data Pasien");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Nama", "NIK", "Tgl Lahir", "JK", "Gol Darah"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        tfId = new JTextField();
        tfNama = new JTextField();
        tfNIK = new JTextField();
        tfTglLahir = new JFormattedTextField(java.text.DateFormat.getDateInstance());
        cbJK = new JComboBox<>(new String[]{"L", "P"});
        cbGol = new JComboBox<>(new String[]{"A", "B", "AB", "O"});

        form.add(new JLabel("ID Pasien:")); form.add(tfId);
        form.add(new JLabel("Nama:")); form.add(tfNama);
        form.add(new JLabel("NIK:")); form.add(tfNIK);
        form.add(new JLabel("Tanggal Lahir:")); form.add(tfTglLahir);
        form.add(new JLabel("Jenis Kelamin:")); form.add(cbJK);
        form.add(new JLabel("Gol Darah:")); form.add(cbGol);

        JPanel tombol = new JPanel();
        JButton simpan = new JButton("Simpan");
        JButton ubah = new JButton("Ubah");
        JButton hapus = new JButton("Hapus");
        JButton clear = new JButton("Clear");
        tombol.add(simpan); tombol.add(ubah); tombol.add(hapus); tombol.add(clear);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.add(form, BorderLayout.CENTER);
        bawah.add(tombol, BorderLayout.SOUTH);
        add(bawah, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout());
        tfCari = new JTextField();
        top.add(new JLabel("Cari Nama/NIK:"), BorderLayout.WEST);
        top.add(tfCari, BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh");
        top.add(refresh, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        loadData("");

        tfCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadData(tfCari.getText());
            }
        });

        refresh.addActionListener(e -> loadData(""));

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                tfId.setText(model.getValueAt(i, 0).toString());
                tfNama.setText(model.getValueAt(i, 1).toString());
                tfNIK.setText(model.getValueAt(i, 2).toString());
                tfTglLahir.setText(model.getValueAt(i, 3).toString());
                cbJK.setSelectedItem(model.getValueAt(i, 4).toString());
                cbGol.setSelectedItem(model.getValueAt(i, 5).toString());
            }
        });

        clear.addActionListener(e -> clearForm());

        simpan.addActionListener(e -> {
            try (Connection c = DatabaseConnection.getConnection()) {
                PreparedStatement ps = c.prepareStatement("INSERT INTO Pasien VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, tfId.getText());
                ps.setString(2, tfNama.getText());
                ps.setString(3, tfNIK.getText());
                ps.setDate(4, java.sql.Date.valueOf(tfTglLahir.getText()));
                ps.setString(5, cbJK.getSelectedItem().toString());
                ps.setString(6, cbGol.getSelectedItem().toString());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data disimpan.");
                loadData("");
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        ubah.addActionListener(e -> {
            try (Connection c = DatabaseConnection.getConnection()) {
                PreparedStatement ps = c.prepareStatement("UPDATE Pasien SET nama=?, NIK=?, tgl_lahir=?, jenis_kelamin=?, gol_darah=? WHERE patient_id=?");
                ps.setString(1, tfNama.getText());
                ps.setString(2, tfNIK.getText());
                ps.setDate(3, java.sql.Date.valueOf(tfTglLahir.getText()));
                ps.setString(4, cbJK.getSelectedItem().toString());
                ps.setString(5, cbGol.getSelectedItem().toString());
                ps.setString(6, tfId.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data diubah.");
                loadData("");
                clearForm();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        hapus.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?");
            if (confirm == 0) {
                try (Connection c = DatabaseConnection.getConnection()) {
                    PreparedStatement ps = c.prepareStatement("DELETE FROM Pasien WHERE patient_id=?");
                    ps.setString(1, tfId.getText());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data dihapus.");
                    loadData("");
                    clearForm();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });
    }

    private void loadData(String keyword) {
        try (Connection c = DatabaseConnection.getConnection()) {
            model.setRowCount(0);
            String sql = "SELECT * FROM Pasien WHERE nama LIKE ? OR NIK LIKE ?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("patient_id"),
                    rs.getString("nama"),
                    rs.getString("NIK"),
                    rs.getDate("tgl_lahir"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("gol_darah")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        tfId.setText("");
        tfNama.setText("");
        tfNIK.setText("");
        tfTglLahir.setText("");
        cbJK.setSelectedIndex(0);
        cbGol.setSelectedIndex(0);
    }
}

