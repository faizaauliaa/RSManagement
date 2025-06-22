package GUICRUD;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class LoginPage extends JFrame {
    private JTextField txtNama;

    public LoginPage() {
        setTitle("Login Sistem Rumah Sakit");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/GUICRUD/Assets/loginbg.jpg");
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJudul = new JLabel("Login Sistem Rumah Sakit", JLabel.CENTER);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblJudul.setForeground(Color.BLACK);

        JPanel namaPanel = new JPanel(new FlowLayout());
        namaPanel.setOpaque(false);
        JLabel lblNama = new JLabel("Masukkan Nama:");
        lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtNama = new JTextField(20);
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        namaPanel.add(lblNama);
        namaPanel.add(txtNama);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnLogin.setPreferredSize(new Dimension(150, 40));
        btnLogin.addActionListener(e -> doLogin());

        gbc.gridy = 0;
        backgroundPanel.add(lblJudul, gbc);

        gbc.gridy = 1;
        backgroundPanel.add(namaPanel, gbc);

        gbc.gridy = 3;
        backgroundPanel.add(btnLogin, gbc);

        setContentPane(backgroundPanel);
    }

    private void doLogin() {
        String nama = txtNama.getText().trim().toLowerCase();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> bebasPassword = Arrays.asList("faiz", "ninda", "shafa", "zarin");

        if (!bebasPassword.contains(nama)) {
            // Jika nama tidak termasuk yang dibebaskan → minta password
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(this, pf, "Masukkan Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                String password = new String(pf.getPassword());
                if (!password.equals("basisdata6")) {
                    JOptionPane.showMessageDialog(this, "Password salah!", "Gagal Login", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Selamat datang, " + nama + "!");
        Main main = new Main();
        main.setVisible(true);
        this.dispose();
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String path) {
            try {
                backgroundImage = new ImageIcon(getClass().getResource(path)).getImage();
            } catch (Exception e) {
                System.out.println("Gagal load gambar: " + path);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
