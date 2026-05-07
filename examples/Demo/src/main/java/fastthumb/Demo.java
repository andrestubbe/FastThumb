package fastthumb;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Demo {
    private static JPanel gallery;

    public static void main(String[] args) {
        JFrame frame = new JFrame("FastThumb - High-Performance Shell Image Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        header.setBackground(new Color(30, 30, 30));
        JLabel title = new JLabel("FastThumb Native Demo");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title);

        JButton btnOpen = new JButton("Extract Custom File...");
        btnOpen.setFocusPainted(false);
        btnOpen.addActionListener(e -> pickAndExtract());
        header.add(btnOpen);

        frame.add(header, BorderLayout.NORTH);

        // Gallery
        gallery = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        gallery.setBackground(new Color(45, 45, 45));
        JScrollPane scroll = new JScrollPane(gallery);
        scroll.setBorder(null);
        frame.add(scroll, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            addThumbnailCard("C:\\Windows\\explorer.exe", "System Icon (256x)");
            addThumbnailCard("C:\\Windows\\System32\\notepad.exe", "App Icon");
            addThumbnailCard("C:\\Windows", "Folder Preview");
            
            String wallpaper = "C:\\Windows\\Web\\Wallpaper\\Windows\\img0.jpg";
            if (new File(wallpaper).exists()) {
                addThumbnailCard(wallpaper, "Image Thumbnail");
            }
        });
    }

    private static void addThumbnailCard(String path, String label) {
        new Thread(() -> {
            BufferedImage img = FastThumb.extract(path, 256);
            if (img == null) {
                img = FastThumb.extractIcon(path, 256);
            }

            final BufferedImage finalImg = img;
            SwingUtilities.invokeLater(() -> {
                JPanel card = new JPanel(new BorderLayout(5, 5));
                card.setBackground(new Color(60, 60, 60));
                card.setPreferredSize(new Dimension(280, 320));
                card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                if (finalImg != null) {
                    JLabel imgLabel = new JLabel(new ImageIcon(finalImg));
                    imgLabel.setHorizontalAlignment(JLabel.CENTER);
                    card.add(imgLabel, BorderLayout.CENTER);
                } else {
                    JLabel errorLabel = new JLabel("Failed: " + label);
                    errorLabel.setForeground(Color.RED);
                    errorLabel.setHorizontalAlignment(JLabel.CENTER);
                    card.add(errorLabel, BorderLayout.CENTER);
                }

                JLabel textLabel = new JLabel(label);
                textLabel.setForeground(Color.WHITE);
                textLabel.setHorizontalAlignment(JLabel.CENTER);
                card.add(textLabel, BorderLayout.SOUTH);

                gallery.add(card);
                gallery.revalidate();
                gallery.repaint();
            });
        }).start();
    }

    private static void pickAndExtract() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            addThumbnailCard(selected.getAbsolutePath(), selected.getName());
        }
    }
}
