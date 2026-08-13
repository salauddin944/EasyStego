import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * EasyStego - A Drag-and-Drop Image Steganography Tool
 *
 * Week 4: GUI Layout - Tabs, Drop Zones & Theming
 * This week adds the full two-tab interface with styled components.
 * Drag-and-drop logic will be wired in Week 5.
 * LSB embedding and extraction logic will be added in Week 6 and 7.
 *
 * Course: Software Development I (CSE 2216)
 * Author: Md. Salauddin | ID: 11240321728
 * Submitted to: Vashkar Kar, Lecturer, Dept. of CSE, NUBT Khulna
 */
public class EasyStego extends JFrame {

    // --- Global Styles ---
    private final Font MAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Color ACCENT_COLOR    = new Color(0, 120, 215);  // Windows Blue
    private final Color DROP_ZONE_COLOR = new Color(240, 240, 240);

    // --- Hide Tab Components ---
    private JLabel   hideDropZone;
    private JTextArea inputMsgArea;
    private JLabel   capacityLabel;

    // --- Reveal Tab Components ---
    private JLabel   revealDropZone;
    private JTextArea outputMsgArea;

    public EasyStego() {
        setTitle("EasyStego - Drag & Drop Steganography");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Apply native OS look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Build tabbed interface
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

        // --- Top: Drop Zone ---
        hideDropZone = createDropZone("Step 1: Drag & Drop an Image Here (JPG/PNG)");
        // Note: TransferHandler (drag-and-drop) will be added in Week 5

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setPreferredSize(new Dimension(0, 200));
        topContainer.add(hideDropZone, BorderLayout.CENTER);

        // --- Center: Message Input ---
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

        // --- Bottom: Action Button ---
        JButton saveBtn = createStyledButton("Step 3: Encrypt & Save Image", ACCENT_COLOR);
        // Note: ActionListener will be wired in Week 6 (LSB embedding logic)

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

        // --- Top: Drop Zone ---
        revealDropZone = createDropZone("Step 1: Drag & Drop the Secret Image Here");
        // Note: TransferHandler (drag-and-drop) will be added in Week 5

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setPreferredSize(new Dimension(0, 200));
        topContainer.add(revealDropZone, BorderLayout.CENTER);

        // --- Center: Message Output ---
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

        // --- Bottom: Action Button ---
        JButton copyBtn = createStyledButton("Copy Text to Clipboard", new Color(46, 139, 87));
        // Note: ActionListener will be wired in Week 7 (extraction + clipboard logic)

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(centerPanel,  BorderLayout.CENTER);
        panel.add(copyBtn,      BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    //           HELPER UI METHODS
    // ==========================================

    /**
     * Creates a styled dashed-border drop zone label.
     * Drag-and-drop TransferHandler will be attached in Week 5.
     */
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
     * Creates a styled action button with custom background color.
     */
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
    //                 ENTRY POINT
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EasyStego().setVisible(true));
    }
}
