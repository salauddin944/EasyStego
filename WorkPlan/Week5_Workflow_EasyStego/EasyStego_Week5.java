import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * EasyStego - A Drag-and-Drop Image Steganography Tool
 *
 * Week 5: Drag-and-Drop Integration
 * This week adds:
 *  - ImageTransferHandler inner class (drag-and-drop from OS)
 *  - updateDropZonePreview() — image thumbnail inside drop zone
 *  - calculateCapacity()    — real-time capacity label update
 * LSB embedding logic will be added in Week 6.
 * LSB extraction logic will be added in Week 7.
 *
 * Course: Software Development I (CSE 2216)
 * Author: Md. Salauddin | ID: 11240321728
 * Submitted to: Vashkar Kar, Lecturer, Dept. of CSE, NUBT Khulna
 */
public class EasyStego extends JFrame {

    // --- Global Styles ---
    private final Font  MAIN_FONT       = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font  TITLE_FONT      = new Font("Segoe UI", Font.BOLD, 16);
    private final Color ACCENT_COLOR    = new Color(0, 120, 215);   // Windows Blue
    private final Color DROP_ZONE_COLOR = new Color(240, 240, 240);

    // --- Hide Tab Components ---
    private JLabel    hideDropZone;
    private JTextArea inputMsgArea;
    private BufferedImage sourceImage;
    private JLabel    capacityLabel;

    // --- Reveal Tab Components ---
    private JLabel    revealDropZone;
    private JTextArea outputMsgArea;

    public EasyStego() {
        setTitle("EasyStego - Drag & Drop Steganography");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TITLE_FONT);
        tabs.addTab("  1. Hide Message  ",   createHidePanel());
        tabs.addTab("  2. Reveal Message  ", createRevealPanel());

        add(tabs);
    }

    // ==========================================
    //           PANEL 1: HIDE MESSAGE
    // ==========================================
    private JPanel createHidePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Drop zone with drag-and-drop handler
        hideDropZone = createDropZone("Step 1: Drag & Drop an Image Here (JPG/PNG)");
        hideDropZone.setTransferHandler(new ImageTransferHandler(img -> {
            sourceImage = img;
            updateDropZonePreview(hideDropZone, img);
            calculateCapacity(img);
        }));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setPreferredSize(new Dimension(0, 200));
        topContainer.add(hideDropZone, BorderLayout.CENTER);

        // Message input area
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        JLabel msgLabel = new JLabel("Step 2: Type your secret message:");
        msgLabel.setFont(TITLE_FONT);

        inputMsgArea = new JTextArea();
        inputMsgArea.setFont(MAIN_FONT);
        inputMsgArea.setLineWrap(true);
        inputMsgArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(inputMsgArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        capacityLabel = new JLabel("Capacity: Waiting for image...");
        capacityLabel.setForeground(Color.GRAY);
        capacityLabel.setFont(MAIN_FONT);

        centerPanel.add(msgLabel,      BorderLayout.NORTH);
        centerPanel.add(scroll,        BorderLayout.CENTER);
        centerPanel.add(capacityLabel, BorderLayout.SOUTH);

        // Action button (listener wired in Week 6)
        JButton saveBtn = createStyledButton("Step 3: Encrypt & Save Image", ACCENT_COLOR);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(centerPanel,  BorderLayout.CENTER);
        panel.add(saveBtn,      BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    //           PANEL 2: REVEAL MESSAGE
    // ==========================================
    private JPanel createRevealPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Drop zone with drag-and-drop handler
        revealDropZone = createDropZone("Step 1: Drag & Drop the Secret Image Here");
        revealDropZone.setTransferHandler(new ImageTransferHandler(img -> {
            updateDropZonePreview(revealDropZone, img);
            // extractMessage() will be fully implemented in Week 7
            String msg = extractMessage(img);
            outputMsgArea.setText(msg);
        }));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setPreferredSize(new Dimension(0, 200));
        topContainer.add(revealDropZone, BorderLayout.CENTER);

        // Message output area
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        JLabel msgLabel = new JLabel("Hidden Message:");
        msgLabel.setFont(TITLE_FONT);

        outputMsgArea = new JTextArea();
        outputMsgArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputMsgArea.setEditable(false);
        outputMsgArea.setLineWrap(true);
        outputMsgArea.setBackground(new Color(250, 250, 250));

        centerPanel.add(msgLabel,                       BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(outputMsgArea), BorderLayout.CENTER);

        // Action button (listener wired in Week 7)
        JButton copyBtn = createStyledButton("Copy Text to Clipboard", new Color(46, 139, 87));

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(centerPanel,  BorderLayout.CENTER);
        panel.add(copyBtn,      BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    //        STUB — will be replaced Week 6
    // ==========================================
    private BufferedImage embedMessage(BufferedImage img, String message) {
        // Full LSB embedding logic coming in Week 6
        return img;
    }

    // ==========================================
    //        STUB — will be replaced Week 7
    // ==========================================
    private String extractMessage(BufferedImage img) {
        // Full LSB extraction logic coming in Week 7
        return "Extraction logic coming in Week 7...";
    }

    // ==========================================
    //           HELPER UI METHODS
    // ==========================================

    private JLabel createDropZone(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setOpaque(true);
        label.setBackground(DROP_ZONE_COLOR);
        label.setForeground(Color.GRAY);
        label.setBorder(new CompoundBorder(
            BorderFactory.createDashedBorder(Color.GRAY, 2, 5, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return label;
    }

    /**
     * Renders a scaled image thumbnail inside the drop zone after a successful drop.
     * Replaces the dashed border with a solid accent-color border to show loaded state.
     */
    private void updateDropZonePreview(JLabel dropZone, BufferedImage img) {
        dropZone.setText("");
        dropZone.setIcon(new ImageIcon(
            img.getScaledInstance(Math.min(img.getWidth(), 300), 180, Image.SCALE_SMOOTH)
        ));
        dropZone.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
    }

    /**
     * Computes and displays the maximum embeddable message size.
     * Formula: capacity = (width x height) / 8 characters
     */
    private void calculateCapacity(BufferedImage img) {
        long capacity = ((long) img.getWidth() * img.getHeight()) / 8;
        capacityLabel.setText(
            "Image Capacity: You can hide approx " + capacity + " characters here."
        );
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
    //        DRAG-AND-DROP HANDLER
    // ==========================================

    /**
     * Custom TransferHandler that accepts image files dropped from the OS.
     * Uses a Consumer<BufferedImage> callback so the same class works
     * for both the Hide tab and the Reveal tab with different actions.
     */
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
                List<?> files = (List<?>) support.getTransferable()
                                    .getTransferData(DataFlavor.javaFileListFlavor);
                if (!files.isEmpty() && files.get(0) instanceof File) {
                    BufferedImage img = ImageIO.read((File) files.get(0));
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

    // ==========================================
    //                ENTRY POINT
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EasyStego().setVisible(true));
    }
}
