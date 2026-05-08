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
    // Stolen from FastTheme "DARK" Theme
    private static final Color COLOR_BG = new Color(44, 50, 46);
    private static final Color COLOR_ACCENT = new Color(223, 145, 70);
    private static final Color COLOR_PANEL = new Color(54, 62, 57); // Slightly lighter for cards
    private static final Color COLOR_TEXT_DIM = new Color(125, 138, 118);
    
    private static JPanel gallery;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0f");

        JFrame frame = new JFrame("FastThumb - Professional Dark Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1173, 610); // Exact FastTheme Demo size
        frame.getContentPane().setBackground(COLOR_BG);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 35, 30));
        header.setBackground(COLOR_BG);
        
        JLabel title = new JLabel("FastThumb // Native");
        title.setForeground(COLOR_ACCENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.add(title);

        JButton btnOpen = new JButton("Load Media...");
        btnOpen.setBackground(COLOR_PANEL);
        btnOpen.setForeground(COLOR_ACCENT);
        btnOpen.setFocusPainted(false);
        btnOpen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOpen.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1));
        btnOpen.setPreferredSize(new Dimension(140, 35));
        btnOpen.addActionListener(e -> pickAndExtract());
        header.add(btnOpen);

        frame.add(header, BorderLayout.NORTH);

        // Gallery
        gallery = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        gallery.setBackground(COLOR_BG);
        JScrollPane scroll = new JScrollPane(gallery);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getViewport().setBackground(COLOR_BG);
        frame.add(scroll, BorderLayout.CENTER);

        // Default test items
        addThumbnailCard("C:\\Windows\\explorer.exe", "Explorer", true);
        addThumbnailCard("C:\\Windows", "System Folder", false);
        
        String wallpaper = "C:\\Windows\\Web\\Wallpaper\\Windows\\img0.jpg";
        if (new File(wallpaper).exists()) {
            addThumbnailCard(wallpaper, "Windows Background", false);
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Apply FastTheme DARK styles
        long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarDarkMode(hwnd, true);
            FastTheme.setTitleBarColor(hwnd, 44, 50, 46);
            FastTheme.setTitleBarTextColor(hwnd, 125, 138, 118);
            FastTheme.setWindowTransparency(hwnd, 230);
        }
    }

    private static void addThumbnailCard(String path, String label, boolean forceIcon) {
        new Thread(() -> {
            Path p = Paths.get(path);
            FastImage fastImg = FastThumb.get(p, 256);
            
            if (fastImg != null) {
                BufferedImage swingImg = fastImg.toBufferedImage();
                fastImg.dispose();
                SwingUtilities.invokeLater(() -> updateUI(swingImg, label));
            } else {
                SwingUtilities.invokeLater(() -> updateUI(null, label));
            }
        }).start();
    }

    private static void updateUI(BufferedImage img, String label) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_PANEL);
        card.setPreferredSize(new Dimension(240, 280));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (img != null) {
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(imgLabel, BorderLayout.CENTER);
            
            JLabel textLabel = new JLabel(label);
            textLabel.setForeground(COLOR_TEXT_DIM);
            textLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
            textLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(textLabel, BorderLayout.SOUTH);
        } else {
            JLabel errorLabel = new JLabel("Load Error");
            errorLabel.setForeground(new Color(200, 80, 80));
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
            addThumbnailCard(selected.getAbsolutePath(), selected.getName(), false);
        }
    }
}
