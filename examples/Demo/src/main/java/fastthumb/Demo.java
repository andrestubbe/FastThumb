package fastthumb;

import fasttheme.FastTheme;
import fastimage.FastImage;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Demo {
    // Stolen from FastTheme "ANTIGRAVITY" Theme
    private static final Color COLOR_BG = new Color(20, 20, 35);
    private static final Color COLOR_ACCENT = new Color(0, 255, 200);
    private static final Color COLOR_PANEL = new Color(30, 30, 50);
    
    private static JPanel gallery;

    public static void main(String[] args) {
        // Fix scaling for high-DPI displays
        System.setProperty("sun.java2d.uiScale", "1.0f");

        JFrame frame = new JFrame("FastThumb Antigravity Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1173, 700); // Matches FastTheme resolution width
        frame.getContentPane().setBackground(COLOR_BG);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        header.setBackground(COLOR_BG);
        
        JLabel title = new JLabel("FASTTHUMB // NATIVE_ENGINE");
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        header.add(title);

        JButton btnOpen = new JButton("LOAD_CUSTOM");
        btnOpen.setBackground(COLOR_PANEL);
        btnOpen.setForeground(COLOR_ACCENT);
        btnOpen.setFocusPainted(false);
        btnOpen.setFont(new Font("Consolas", Font.BOLD, 12));
        btnOpen.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1));
        btnOpen.setPreferredSize(new Dimension(150, 35));
        btnOpen.addActionListener(e -> pickAndExtract());
        header.add(btnOpen);

        frame.add(header, BorderLayout.NORTH);

        // Gallery
        gallery = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        gallery.setBackground(COLOR_BG);
        JScrollPane scroll = new JScrollPane(gallery);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBackground(COLOR_BG);
        scroll.getViewport().setBackground(COLOR_BG);
        frame.add(scroll, BorderLayout.CENTER);

        // Default test items
        addThumbnailCard("C:\\Windows\\explorer.exe", "EXPLORER.EXE", true);
        addThumbnailCard("C:\\Windows", "SYSTEM_FOLDER", false);
        
        String wallpaper = "C:\\Windows\\Web\\Wallpaper\\Windows\\img0.jpg";
        if (new File(wallpaper).exists()) {
            addThumbnailCard(wallpaper, "WIN_WALLPAPER", false);
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Apply FastTheme "Magic"
        long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarDarkMode(hwnd, true);
            FastTheme.setTitleBarColor(hwnd, 30, 30, 50); // Match COLOR_PANEL
            FastTheme.setTitleBarTextColor(hwnd, 0, 255, 200); // Match COLOR_ACCENT
            FastTheme.setWindowTransparency(hwnd, 230); // Slight transparency
        }
    }

    private static void addThumbnailCard(String path, String label, boolean forceIcon) {
        new Thread(() -> {
            System.out.println("[Demo] Attempting to load: " + path);
            Path p = Paths.get(path);
            FastImage fastImg = FastThumb.get(p, 256);
            
            if (fastImg != null) {
                System.out.println("[Demo] Successfully loaded FastImage for: " + label);
                BufferedImage swingImg = fastImg.toBufferedImage();
                fastImg.dispose(); // Important: free native memory
                
                SwingUtilities.invokeLater(() -> updateUI(swingImg, label));
            } else {
                System.err.println("[Demo] FAILED to load FastImage for: " + label);
                SwingUtilities.invokeLater(() -> updateUI(null, "LOAD_FAILED: " + label));
            }
        }).start();
    }

    private static void updateUI(BufferedImage img, String label) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_PANEL);
        card.setPreferredSize(new Dimension(250, 300));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (img != null) {
            // High-quality icon display
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(imgLabel, BorderLayout.CENTER);
            
            JLabel textLabel = new JLabel(label);
            textLabel.setForeground(COLOR_ACCENT);
            textLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
            textLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(textLabel, BorderLayout.SOUTH);
        } else {
            JLabel errorLabel = new JLabel("ERROR: " + label);
            errorLabel.setForeground(Color.RED);
            errorLabel.setFont(new Font("Consolas", Font.BOLD, 12));
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(errorLabel, BorderLayout.CENTER);
        }

        gallery.add(card);
        gallery.revalidate();
        gallery.repaint();
    }

    private static void pickAndExtract() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            addThumbnailCard(selected.getAbsolutePath(), selected.getName().toUpperCase(), false);
        }
    }
}
