package GUICRUD;

import javax.swing.*;
import java.awt.*;

public class WelcomePage extends JFrame {

    public WelcomePage() {
        setTitle("Welcome to Sistem Rumah Sakit");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/GUICRUD/Assets/hospital.png");
        backgroundPanel.setLayout(new BorderLayout(10, 10));

        JLabel labelWelcome = new JLabel("Welcome to Sistem Rumah Sakit", JLabel.CENTER);
        labelWelcome.setFont(new Font("Arial", Font.BOLD, 40));
        labelWelcome.setForeground(Color.WHITE); 
        backgroundPanel.add(labelWelcome, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel();
        panelBawah.setOpaque(false); // Supaya transparan
        panelBawah.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));

        JButton btnNext = new JButton("Login");
        panelBawah.add(btnNext);
        backgroundPanel.add(panelBawah, BorderLayout.SOUTH);

        btnNext.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        setContentPane(backgroundPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomePage().setVisible(true);
        });
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            } catch (Exception e) {
                System.out.println("Gambar tidak ditemukan: " + imagePath);
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
