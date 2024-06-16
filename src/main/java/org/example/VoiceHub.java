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
    private JButton scaleButton;
    private JButton songButton;
    private JButton micButton;
    private boolean micOn = false;
    private PitchDetection pitchDetection;
    private Scale scale;
    private Song song;
    private JLabel statusLabel;
    private JTextArea instructionsArea;
    private JLabel currentPitchLabel; // New label for current pitch

    public VoiceHub() {
        setTitle("Voice Hub");
        setLayout(new BorderLayout());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        scaleButton = new JButton("Learn Scale");
        songButton = new JButton("Sing Song");
        micButton = new JButton("Start Mic");

        scaleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scale = new Scale(statusLabel);
                showInstructions(scale);
            }
        });

        songButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                song = new Song();
                showInstructions(song);
            }
        });

        micButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMic();
            }
        });

        buttonPanel.add(scaleButton);
        buttonPanel.add(songButton);
        buttonPanel.add(micButton);

        add(buttonPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status");
        add(statusLabel, BorderLayout.SOUTH);

        instructionsArea = new JTextArea();
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(instructionsArea);
        add(scrollPane, BorderLayout.CENTER);

        currentPitchLabel = new JLabel("Current Pitch: "); // Initialize current pitch label
        add(currentPitchLabel, BorderLayout.EAST); // Add current pitch label to the east section

        setVisible(true);
    }

    private void showInstructions(Instructions mode) {
        String instructions = mode.instructions(); // Get detailed instructions
        instructionsArea.setText(instructions);

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
        };
    }

    private void toggleMic() {
        if (micOn) {
            pitchDetection.stopMic();
            micButton.setText("Start Mic");
            micOn = false;
        } else {
            try {
                pitchDetection.startMic();
                micButton.setText("Stop Mic");
                micOn = true;
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Microphone not available", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new VoiceHub();
    }
}
