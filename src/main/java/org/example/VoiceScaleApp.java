package org.example;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VoiceScaleApp extends JFrame {
    private JButton learnButton;
    private JButton playButton;
    private JButton startButton;
    private JLabel instructionLabel;
    private JLabel currentLabel;
    private JLabel pitchLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private String currentMode;
    private PitchDetector pitchDetector;
    private TargetDataLine microphone;

    public VoiceScaleApp() {
        setTitle("Voice Scale and Song Player");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        learnButton = new JButton("Learn the Voice Scale");
        learnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCanvas();
                currentMode = "Learn the Voice Scale";
                addComponents();
            }
        });
        add(learnButton);

        playButton = new JButton("Play a Song");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetCanvas();
                currentMode = "Play a Song";
                addComponents();
            }
        });
        add(playButton);

        setVisible(true);
    }

    private void resetCanvas() {
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    private void addComponents() {
        if (controlPanel != null) {
            remove(controlPanel);
        }
        controlPanel = new JPanel(new FlowLayout());

        if (currentMode.equals("Learn the Voice Scale")) {
            startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    instructionLabel.setText("Instructions: Sing the scale " +
                            "            261.63f, // C4 (Do)\n" +
                            "            293.66f, // D4 (Re)\n" +
                            "            329.63f, // E4 (Mi)\n" +
                            "            349.23f, // F4 (Fa)\n" +
                            "            392.00f, // G4 (Sol)\n" +
                            "            440.00f, // A4 (La)\n" +
                            "            493.88f, // B4 (Ti)\n" +
                            "            523.25f  // C5 (Do)");
                    currentLabel.setText("Start with DO");
                    pitchLabel.setText("Current Pitch:");
                    statusLabel.setText("Microphone on");
                    addPauseButton();
                    addResetButton();

                    controlPanel.remove(startButton);
                    controlPanel.revalidate();
                    controlPanel.repaint();

                    startPitchDetection();
                }
            });
            controlPanel.add(startButton);

            add(controlPanel);
        } else if (currentMode.equals("Play a Song")) {
            startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    instructionLabel.setText("Instructions: Play the song 'Silent Night' - GAGE GAGE DB CG ACBA GAGE ACBAGAGE DFDBCE CGEGFDC");
                    currentLabel.setText("Start with G");
                    pitchLabel.setText("Current Pitch:");
                    statusLabel.setText("Microphone on");
                    addPauseButton();
                    addResetButton();

                    controlPanel.remove(startButton);
                    controlPanel.revalidate();
                    controlPanel.repaint();

                    startPitchDetection();
                }
            });
            controlPanel.add(startButton);

            add(controlPanel);
        }

        instructionLabel = new JLabel();
        add(instructionLabel);

        currentLabel = new JLabel();
        add(currentLabel);

        pitchLabel = new JLabel();
        add(pitchLabel);

        statusLabel = new JLabel();
        add(statusLabel);

        revalidate();
        repaint();
    }

    private void addPauseButton() {
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pauseButton.getText().equals("Pause")) {
                    // Pause logic
                    pauseButton.setText("Unpause");
                    statusLabel.setText("Microphone off");
                    pitchDetector.stop();
                } else {
                    // Unpause logic
                    pauseButton.setText("Pause");
                    statusLabel.setText("Microphone on");
                    pitchDetector.start();
                }
            }
        });
        controlPanel.add(pauseButton);
    }

    private void addResetButton() {
        JButton resetButton = new JButton("Back");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logic for resetting the application
                resetCanvas();
                if (currentMode.equals("Learn the Voice Scale") || currentMode.equals("Play a Song")) {
                    getContentPane().removeAll(); // Remove all components
                    getContentPane().revalidate(); // Revalidate the container
                    getContentPane().repaint(); // Repaint the container
                    add(learnButton); // Add the learn button
                    add(playButton); // Add the play button
                }
                if (microphone != null && microphone.isOpen()) {
                    microphone.close();
                }
            }
        });
        controlPanel.add(resetButton);
    }

    private void startPitchDetection() {
        try {
            if (microphone == null || !microphone.isOpen()) {
                microphone = AudioSystem.getTargetDataLine(new AudioFormat(44100, 16, 1, true, false));
                microphone.open();
            }
            microphone.start();
            pitchDetector = new PitchDetector(microphone, pitchLabel, statusLabel, currentLabel);
            pitchDetector.setMode(currentMode); // Ensure currentMode is set before starting
            pitchDetector.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VoiceScaleApp();
            }
        });
    }
}