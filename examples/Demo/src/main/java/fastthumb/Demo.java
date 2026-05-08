package fastthumb;

import fasttheme.FastTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Demo {
    private static final Color COLOR_BG = new Color(15, 15, 15);
    private static final Color COLOR_PANEL = new Color(22, 22, 22);
    private static final Color COLOR_ACCENT = new Color(32, 255, 128);
    private static JPanel gallery;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0f");

        JFrame frame = new JFrame("FastThumb - High-Performance Shell Image Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1440, 1080);
        frame.getContentPane().setBackground(COLOR_BG);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        header.setBackground(COLOR_BG);
        JLabel title = new JLabel("FastThumb Native Demo");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);

        JButton btnOpen = new JButton("Extract Custom File...");
        btnOpen.setBackground(COLOR_PANEL);
        btnOpen.setForeground(COLOR_ACCENT);
        btnOpen.setFocusPainted(false);
        btnOpen.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1));
        btnOpen.setPreferredSize(new Dimension(200, 40));
        btnOpen.addActionListener(e -> pickAndExtract());
        header.add(btnOpen);

        frame.add(header, BorderLayout.NORTH);

        // Gallery
        gallery = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        gallery.setBackground(COLOR_BG);
        JScrollPane scroll = new JScrollPane(gallery);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        frame.add(scroll, BorderLayout.CENTER);

        // Add some default items (Force ICON for EXEs)
        addThumbnailCard("C:\\Windows\\explorer.exe", "System Icon", true);
        addThumbnailCard("C:\\Windows\\regedit.exe", "Registry Editor", true);
        addThumbnailCard("C:\\Windows", "Folder Preview", false);
        
        String wallpaper = "C:\\Windows\\Web\\Wallpaper\\Windows\\img0.jpg";
        if (new File(wallpaper).exists()) {
            addThumbnailCard(wallpaper, "Image Thumbnail", false);
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarDarkMode(hwnd, true);
            FastTheme.setTitleBarColor(hwnd, 15, 15, 15);
            FastTheme.setTitleBarTextColor(hwnd, 220, 220, 220);
        }
    }

    private static void addThumbnailCard(String path, String label, boolean forceIcon) {
        new Thread(() -> {
            BufferedImage img = forceIcon ? FastThumb.extractIcon(path, 256) : FastThumb.extract(path, 256);
            
            final BufferedImage finalImg = img;
            SwingUtilities.invokeLater(() -> {
                JPanel card = new JPanel(new BorderLayout(5, 5));
                card.setBackground(COLOR_PANEL);
                card.setPreferredSize(new Dimension(280, 320));
                card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                if (finalImg != null) {
                    JLabel imgLabel = new JLabel(new ImageIcon(finalImg));
                    imgLabel.setHorizontalAlignment(JLabel.CENTER);
                    card.add(imgLabel, BorderLayout.CENTER);
                    
                    String resLabel = label + " (" + finalImg.getWidth() + "x" + finalImg.getHeight() + ")";
                    JLabel textLabel = new JLabel(resLabel);
                    textLabel.setForeground(Color.GRAY);
                    textLabel.setHorizontalAlignment(JLabel.CENTER);
                    card.add(textLabel, BorderLayout.SOUTH);
                } else {
                    JLabel errorLabel = new JLabel("Failed: " + label);
                    errorLabel.setForeground(new Color(200, 50, 50));
                    errorLabel.setHorizontalAlignment(JLabel.CENTER);
                    card.add(errorLabel, BorderLayout.CENTER);
                }

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
            addThumbnailCard(selected.getAbsolutePath(), selected.getName(), false);
        }
    }
}
