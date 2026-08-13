import javax.swing.*;

/**
 * EasyStego - A Drag-and-Drop Image Steganography Tool
 *
 * Week 3: Environment Setup & Project Initialization
 * This file establishes the main application window (JFrame).
 * GUI panels, LSB logic, and drag-and-drop will be added in upcoming weeks.
 *
 * Course: Software Development I (CSE 2216)
 * Author: Md. Salauddin | ID: 11240321728
 * Submitted to: Vashkar Kar, Lecturer, Dept. of CSE, NUBT Khulna
 */
public class EasyStego extends JFrame {

    public EasyStego() {
        setTitle("EasyStego - Drag & Drop Steganography");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        // GUI panels will be added in Week 4 (Hide Message tab)
        // and Week 5 (Reveal Message tab + drag-and-drop)
    }

    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread (EDT) as required by Java Swing
        SwingUtilities.invokeLater(() -> new EasyStego().setVisible(true));
    }
}
