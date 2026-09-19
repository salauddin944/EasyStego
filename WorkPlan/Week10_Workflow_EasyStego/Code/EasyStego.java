
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class EasyStego extends JFrame {

    // --- Global Styles ---
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Color ACCENT_COLOR = new Color(0, 120, 215); // Windows Blue
    private final Color DROP_ZONE_COLOR = new Color(240, 240, 240);

    // --- Hide (Encrypt) Components ---
    private JLabel hideDropZone;
    private JTextArea inputMsgArea;
    private BufferedImage sourceImage;
    private JLabel capacityLabel;

    // --- Reveal (Decrypt) Components ---
    private JLabel revealDropZone;
    private JTextArea outputMsgArea;

    public EasyStego() {
        setTitle("EasyStego - Drag & Drop Steganography");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TITLE_FONT);
        tabs.addTab("  1. Hide Message  ", createHidePanel());
        tabs.addTab("  2. Reveal Message  ", createRevealPanel());

        add(tabs);
    }

    // ==========================================
    //           PANEL 1: HIDE MESSAGE
    // ==========================================
    private JPanel createHidePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Top - Drop Zone
        hideDropZone = createDropZone("Step 1: Drag & Drop an Image Here (JPG/PNG)");
        hideDropZone.setTransferHandler(new ImageTransferHandler(img -> {
            sourceImage = img;
            updateDropZonePreview(hideDropZone, img);
            calculateCapacity(img);
        }));

        // 2. Center - Input Area
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lbl = new JLabel("Step 2: Type your secret message:");
        lbl.setFont(TITLE_FONT);

        inputMsgArea = new JTextArea();
        inputMsgArea.setFont(MAIN_FONT);
        inputMsgArea.setLineWrap(true);
        inputMsgArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(inputMsgArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        capacityLabel = new JLabel("Capacity: Waiting for image...");
        capacityLabel.setForeground(Color.GRAY);

        centerPanel.add(lbl, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.add(capacityLabel, BorderLayout.SOUTH);

        // 3. Bottom - Save Button
        JButton saveBtn = createStyledButton("Step 3: Encrypt & Save Image", ACCENT_COLOR);
        saveBtn.addActionListener(e -> {
            if (sourceImage == null) {
                JOptionPane.showMessageDialog(this, "Please drop an image first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (inputMsgArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please type a message!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            saveEncryptedImage();
        });

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setPreferredSize(new Dimension(0, 200));
        topContainer.add(hideDropZone, BorderLayout.CENTER);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(saveBtn, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    //           PANEL 2: REVEAL MESSAGE
    // ==========================================
    private JPanel createRevealPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Top - Drop Zone
        revealDropZone = createDropZone("Step 1: Drag & Drop the Secret Image Here");
        revealDropZone.setTransferHandler(new ImageTransferHandler(img -> {
            updateDropZonePreview(revealDropZone, img);
            String msg = extractMessage(img);
            outputMsgArea.setText(msg);
        }));

        // 2. Center - Output Area
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel lbl = new JLabel("Hidden Message:");
        lbl.setFont(TITLE_FONT);

        outputMsgArea = new JTextArea();
        outputMsgArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputMsgArea.setEditable(false);
        outputMsgArea.setLineWrap(true);
        outputMsgArea.setBackground(new Color(250, 250, 250));

        centerPanel.add(lbl, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(outputMsgArea), BorderLayout.CENTER);

        // 3. Bottom - Copy Button
        JButton copyBtn = createStyledButton("Copy Text to Clipboard", new Color(46, 139, 87));
        copyBtn.addActionListener(e -> {
            outputMsgArea.selectAll();
            outputMsgArea.copy();
            JOptionPane.showMessageDialog(this, "Copied to clipboard!");
        });

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setPreferredSize(new Dimension(0, 200));
        topContainer.add(revealDropZone, BorderLayout.CENTER);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(copyBtn, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    //           CORE STEGANOGRAPHY LOGIC
    // ==========================================

    private BufferedImage embedMessage(BufferedImage img, String message) {
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImg.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        message += "###END###";
        byte[] msgBytes = message.getBytes();
        int bitIndex = 0;
        int byteIndex = 0;

        for (int y = 0; y < newImg.getHeight(); y++) {
            for (int x = 0; x < newImg.getWidth(); x++) {
                if (byteIndex >= msgBytes.length) return newImg;

                int rgb = newImg.getRGB(x, y);
                int bit = (msgBytes[byteIndex] >> (7 - bitIndex)) & 1;

                int newRgb = (rgb & 0xFFFFFFFE) | bit;
                newImg.setRGB(x, y, newRgb);

                bitIndex++;
                if (bitIndex >= 8) {
                    bitIndex = 0;
                    byteIndex++;
                }
            }
        }
        return newImg;
    }

    private String extractMessage(BufferedImage img) {
        StringBuilder sb = new StringBuilder();
        int currentByte = 0;
        int bitIndex = 0;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int lastBit = rgb & 1;

                currentByte = (currentByte << 1) | lastBit;
                bitIndex++;

                if (bitIndex >= 8) {
                    char c = (char) currentByte;
                    sb.append(c);
                    currentByte = 0;
                    bitIndex = 0;

                    if (sb.toString().endsWith("###END###")) {
                        return sb.toString().substring(0, sb.length() - 9);
                    }
                }
            }
        }
        return "No hidden message found (or file is not a PNG).";
    }

    // ==========================================
    //              HELPER UI METHODS
    // ==========================================

    private void saveEncryptedImage() {
        BufferedImage finalImg = embedMessage(sourceImage, inputMsgArea.getText());
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("secret_image.png"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fileChooser.getSelectedFile();
                if (!f.getName().endsWith(".png")) f = new File(f.getAbsolutePath() + ".png");
                ImageIO.write(finalImg, "png", f);
                JOptionPane.showMessageDialog(this, "Success! Image saved.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        }
    }

    private JLabel createDropZone(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setOpaque(true);
        label.setBackground(DROP_ZONE_COLOR);
        label.setForeground(Color.GRAY);

        Border dashed = BorderFactory.createDashedBorder(Color.GRAY, 2, 5, 2, true);
        label.setBorder(new CompoundBorder(dashed, new EmptyBorder(20, 20, 20, 20)));
        return label;
    }

    private void updateDropZonePreview(JLabel dropZone, BufferedImage img) {
        dropZone.setText("");
        dropZone.setIcon(new ImageIcon(img.getScaledInstance(Math.min(img.getWidth(), 300), 180, Image.SCALE_SMOOTH)));
        dropZone.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
    }

    private void calculateCapacity(BufferedImage img) {
        long pixels = (long) img.getWidth() * img.getHeight();
        long bytes = pixels / 8;
        capacityLabel.setText("Image Capacity: You can hide approx " + bytes + " characters here.");
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(TITLE_FONT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 50));
        return btn;
    }

    // ==========================================
    //           DRAG AND DROP HANDLER
    // ==========================================

    class ImageTransferHandler extends TransferHandler {
        private final java.util.function.Consumer<BufferedImage> onImageLoaded;

        public ImageTransferHandler(java.util.function.Consumer<BufferedImage> onImageLoaded) {
            this.onImageLoaded = onImageLoaded;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            try {
                List<?> files = (List<?>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (!files.isEmpty() && files.get(0) instanceof File) {
                    File file = (File) files.get(0);
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        onImageLoaded.accept(img);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EasyStego().setVisible(true));
    }
}
