package org.example;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.LineUnavailableException;

public class VoiceHub extends JFrame {
    // UI components
    private JButton scaleButton;
    private JButton songButton;
    private JButton micButton;
    private boolean micOn = false;
    private PitchDetection pitchDetection;
    private Scale scale;
    private Song song;
    private JLabel statusLabel;
    private JTextArea instructionsArea;
    private JLabel currentPitchLabel;

    // constructor to initialize UI
    public VoiceHub() {
        initializeUI();
    }

    // method to set up the UI components
    private void initializeUI() {
        setTitle("Voice Hub");
        setLayout(new BorderLayout());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // panel to hold the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

        // button labels
        scaleButton = new JButton("Learn Scale");
        songButton = new JButton("Sing Song");
        micButton = new JButton("Start Mic");

        //action listener scale button to show first instruction
        scaleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scale = new Scale();
                showInstructions(scale, "Start with Do");
            }
        });

        // action listener for song to show first instruction
        songButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                song = new Song();
                showInstructions(song, "Start with G");
            }
        });

        // action listener to toggle mic
        micButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMic();
            }
        });

        // add buttons to panel
        buttonPanel.add(scaleButton);
        buttonPanel.add(songButton);
        buttonPanel.add(micButton);

        // add button panel to frame
        add(buttonPanel, BorderLayout.NORTH);

        // status label at the bottom
        statusLabel = new JLabel("VoiceHub");
        add(statusLabel, BorderLayout.SOUTH);

        // text area to show instructions w scroll
        instructionsArea = new JTextArea();
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(instructionsArea);
        add(scrollPane, BorderLayout.CENTER);

        // labeling the current pitch section + current pitch place
        currentPitchLabel = new JLabel("Current Pitch: ");
        add(currentPitchLabel, BorderLayout.EAST);

        // make frame visible
        setVisible(true);
    }

    // method to show instructions for the selected mode scale or song
    private void showInstructions(Instructions mode, String startMessage) {
        String instructions = mode.instructions(); // Get detailed instructions
        instructionsArea.setText(instructions); // set instruction text
        statusLabel.setText(startMessage); // Set the start message

        // Create a new PitchDetection instance with anonymous inner class for handling pitch detection
        pitchDetection = new PitchDetection(statusLabel, currentPitchLabel) {
            @Override
            protected PitchDetectionHandler createPitchDetectionHandler() {
                return new PitchDetectionHandler() {
                    @Override
                    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                        pitchDetection(pitchDetectionResult);
                    }
                };
            }

            @Override
            protected float[] getNotePitches() {
                return mode.getNotePitches();
            }

            @Override
            protected String[] getNoteNames() {
                return mode.getNoteNames();
            }

            // Override method to handle completion of the scale or song
            @Override
            protected void onCompletion() {
                SwingUtilities.invokeLater(() -> {
                    micButton.setText("Start Mic");
                    micOn = false;
                });
            }
        };
    }

    // Method to toggle the microphone on or off
    private void toggleMic() {
        if (micOn) {
            stopMicrophone();
        } else {
            startMicrophone();
        }
    }

    // Method to start the microphone
    private void startMicrophone() {
        try {
            pitchDetection.startMic();
            micButton.setText("Stop Mic");
            micOn = true;
        } catch (LineUnavailableException e) {
            showError("Microphone not available. Please check your audio device and try again.");
        } catch (Exception e) {
            showError("An unexpected error occurred while starting the microphone. " + e.getMessage());
        }
    }

    // Method to stop the microphone
    private void stopMicrophone() {
        try {
            pitchDetection.stopMic();
            micButton.setText("Start Mic");
            micOn = false;
        } catch (Exception e) {
            showError("An unexpected error occurred while stopping the microphone. " + e.getMessage());
        }
    }

    // Method to show error messages
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VoiceHub::new);
    }
}
