package org.example;

import javax.sound.sampled.*;
import javax.swing.*;
import be.tarsos.dsp.*;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.*;

public class PitchDetector {

    private String currentMode;

    private static final float[] NOTE_PITCHES = {
            261.63f, // C4 (Do)
            293.66f, // D4 (Re)
            329.63f, // E4 (Mi)
            349.23f, // F4 (Fa)
            392.00f, // G4 (Sol)
            440.00f, // A4 (La)
            493.88f, // B4 (Ti)
            523.25f  // C5 (Do)
    };

    private static final String[] NOTE_NAMES = {
            "Do", "Re", "Mi", "Fa", "Sol", "La", "Ti", "Do"
    };
    private static final float[] SONG_PITCHES = {
            392.00f, // G4
            440.00f, // A4
            392.00f, // G4
            329.63f, // E4
            392.00f, // G4
            440.00f, // A4
            392.00f, // G4
            329.63f, // E4
            293.66f, // D4
            493.88f, // B4
            523.25f, // C5
            392.00f, // G4
            440.00f, // A4
            523.25f, // C5
            493.88f, // B4
            440.00f, // A4
            392.00f, // G4
            440.00f, // A4
            392.00f, // G4
            329.63f, // E4
            587.33f, // D5
            349.23f, // F4
            293.66f, // D4
            493.88f, // B4
            523.25f, // C5
            329.63f, // E4
            523.25f, // C5
            392.00f, // G4
            329.63f, // E4
            392.00f, // G4
            349.23f, // F4
            293.66f, // D4
            523.25f, // C5
    };

    private static final String[] SONG_NAMES = {
            "G", "A", "G", "E",
            "G", "A", "G", "E",
            "D", "B",
            "C5", "G",
            "A", "C5", "B", "A",
            "G", "A", "G", "E",
            "D5", "F", "D", "B", "C5", "E",
            "C5", "G", "E", "G", "F", "D", "C5"
    };

    private TargetDataLine microphone;
    private JLabel pitchLabel;
    private JLabel statusLabel;
    private JLabel currentLabel;
    private AudioDispatcher dispatcher;
    private Thread dispatchThread;
    private int currentNoteIndex;
    private int currentSongIndex;

    public PitchDetector(TargetDataLine microphone, JLabel pitchLabel, JLabel statusLabel, JLabel currentLabel) {
        this.microphone = microphone;
        this.pitchLabel = pitchLabel;
        this.statusLabel = statusLabel;
        this.currentLabel = currentLabel;
        this.currentNoteIndex = 0;
        this.currentSongIndex = 0;
    }

    public void start() {
        try {
            if (microphone == null) {
                return;
            }

            microphone.open();
            microphone.start();

            AudioInputStream stream = new AudioInputStream(microphone);
            JVMAudioInputStream jvmAudioStream = new JVMAudioInputStream(stream);
            dispatcher = new AudioDispatcher(jvmAudioStream, 1024, 512);

            PitchDetectionHandler handler = createPitchDetectionHandler(pitchLabel, statusLabel);
            dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 44100, 1024, handler));

            dispatchThread = new Thread(dispatcher, "Audio Dispatcher");
            dispatchThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (dispatcher != null) {
            dispatcher.stop();
        }
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        if (dispatchThread != null && dispatchThread.isAlive()) {
            dispatchThread.interrupt();
        }
    }

    public void setMode(String mode) {
        this.currentMode = mode;
    }
    private PitchDetectionHandler createPitchDetectionHandler(JLabel detectedPitchLabel, JLabel statusLabel) {
        return new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent event) {
                float detectedPitch = result.getPitch();
                updatePitchLabel(detectedPitchLabel, detectedPitch);

                if (detectedPitch > 0) {
                    switch (currentMode) {
                        case "Learn the Voice Scale":
                            String detectedNote = getClosestNoteName(detectedPitch);
                            if (detectedNote.equals(NOTE_NAMES[currentNoteIndex])) {
                                currentNoteIndex++;
                                if (currentNoteIndex >= NOTE_NAMES.length) {
                                    SwingUtilities.invokeLater(() -> statusLabel.setText("Congratulations! You've completed the scale."));
                                    stop();
                                } else {
                                    SwingUtilities.invokeLater(() -> statusLabel.setText("Correct! Sing the next note: " + NOTE_NAMES[currentNoteIndex]));
                                }
                            }
                            break;
                        case "Play a Song":
                            String detectedSong = getClosestSongName(detectedPitch);
                            if (detectedSong.equals(SONG_NAMES[currentSongIndex])) {
                                currentSongIndex++;
                                if (currentSongIndex >= SONG_NAMES.length) {
                                    SwingUtilities.invokeLater(() -> currentLabel.setText("Congratulations! You've completed the song."));
                                    stop();
                                } else {
                                    SwingUtilities.invokeLater(() -> currentLabel.setText("Correct! Sing the next phrase: " + SONG_NAMES[currentSongIndex]));
                                }
                            }
                            break;
                        default:
                            // Handle default behavior or unknown mode
                            break;
                    }
                }
            }
        };
    }

    private void updatePitchLabel(JLabel detectedPitchLabel, float detectedPitch) {
        SwingUtilities.invokeLater(() -> {
            detectedPitchLabel.setText("Current Pitch: " + detectedPitch);
        });
    }

    private String getClosestSongName(float pitch) {
        float miniDifference = Float.MAX_VALUE;
        int closestSongIndex = 0;

        for (int i = 0; i < SONG_PITCHES.length; i++) {
            float difference = Math.abs(SONG_PITCHES[i] - pitch);
            if (difference < miniDifference) {
                miniDifference = difference;
                closestSongIndex = i;
            }
        }

        return SONG_NAMES[closestSongIndex];
    }
    private String getClosestNoteName(float pitch) {
        float minDifference = Float.MAX_VALUE;
        int closestNoteIndex = 0;

        for (int i = 0; i < NOTE_PITCHES.length; i++) {
            float difference = Math.abs(NOTE_PITCHES[i] - pitch);
            if (difference < minDifference) {
                minDifference = difference;
                closestNoteIndex = i;
            }
        }

        return NOTE_NAMES[closestNoteIndex];
    }
}