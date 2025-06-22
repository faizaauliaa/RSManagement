package GUICRUD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {

    public Main() {
        setTitle("Sistem Manajemen Rumah Sakit");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 235, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ImageIcon icon = new ImageIcon(getClass().getResource("/GUICRUD/Assets/setting.png"));
        Image scaledImage = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton btnSetting = new JButton(new ImageIcon(scaledImage));
        btnSetting.setContentAreaFilled(false);
        btnSetting.setBorderPainted(false);
        btnSetting.setFocusPainted(false);
        btnSetting.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemProfile = new JMenuItem("Info Profil");
        JMenuItem itemAddAccess = new JMenuItem("Tambahkan Akses Tanpa Password");
        JMenuItem itemChangePassword = new JMenuItem("Ganti Password");
        JMenuItem itemLogout = new JMenuItem("Logout");
        popupMenu.add(itemProfile);
        popupMenu.add(itemAddAccess);
        popupMenu.add(itemChangePassword);
        popupMenu.addSeparator();
        popupMenu.add(itemLogout);

        btnSetting.addActionListener(e -> popupMenu.show(btnSetting, 0, btnSetting.getHeight()));
        headerPanel.add(btnSetting, BorderLayout.WEST);

        JLabel title = new JLabel("MANAJEMEN RUMAH SAKIT", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.DARK_GRAY);
        headerPanel.add(title, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        String[] labels = {
            "Manajemen Pasien", "Manajemen Dokter",
            "Manajemen Obat", "Manajemen Kunjungan",
            "Manajemen Rekam Medis", "Manajemen Pembayaran",
            "Manajemen Resep", "Manajemen Detail Resep"
        };

        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(225, 235, 255));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            menuPanel.add(btn);

            btn.addActionListener(e -> {
                JFrame frame;
                switch (label) {
                    case "Manajemen Pasien": frame = new PasienFrame(); break;
                    case "Manajemen Dokter": frame = new DokterFrame(); break;
                    case "Manajemen Obat": frame = new ObatFrame(); break;
                    case "Manajemen Kunjungan": frame = new KunjunganFrame(); break;
                    case "Manajemen Rekam Medis": frame = new RekamMedisFrame(); break;
                    case "Manajemen Pembayaran": frame = new PembayaranFrame(); break;
                    case "Manajemen Resep": frame = new ResepFrame(); break;
                    default: frame = new DetailResepFrame(); break;
                }
                frame.setVisible(true);
            });
        }

        add(menuPanel, BorderLayout.CENTER);

        itemProfile.addActionListener(e -> JOptionPane.showMessageDialog(this, "Nama: [Nama Anda]\nRole: Admin"));
        itemAddAccess.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur penambahan akses tanpa password belum tersedia."));
        itemChangePassword.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur ganti password belum tersedia."));
        itemLogout.addActionListener(e -> {
            new WelcomePage().setVisible(true);
            dispose();
        });
    }
}
