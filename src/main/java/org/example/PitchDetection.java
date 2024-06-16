package org.example;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import javax.sound.sampled.*;
import javax.swing.*;

public abstract class PitchDetection {
    private TargetDataLine microphone;
    private AudioDispatcher dispatcher;
    private Thread dispatchThread;
    private int currentNoteIndex = 0;
    private JLabel statusLabel;
    public JLabel currentPitchLabel; // Label for displaying current pitch

    public PitchDetection(JLabel statusLabel, JLabel currentPitchLabel) {
        this.statusLabel = statusLabel;
        this.currentPitchLabel = currentPitchLabel; // Initialize current pitch label
    }

    public PitchDetection(JLabel statusLabel) {
    }


    public void startMic() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        if (microphone == null) {
            throw new LineUnavailableException("Microphone not available");
        }

        microphone.open(format);
        microphone.start();

        AudioInputStream stream = new AudioInputStream(microphone);
        JVMAudioInputStream jvmAudioStream = new JVMAudioInputStream(stream);
        dispatcher = new AudioDispatcher(jvmAudioStream, 1024, 512);

        PitchDetectionHandler handler = createPitchDetectionHandler();
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 44100, 1024, handler));

        dispatchThread = new Thread(dispatcher, "Audio Dispatcher");
        dispatchThread.start();
    }

    public void stopMic() {
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

    protected abstract PitchDetectionHandler createPitchDetectionHandler();

    protected abstract float[] getNotePitches();

    protected abstract String[] getNoteNames();

    public void pitchDetection(PitchDetectionResult result) {
        float detectedPitch = result.getPitch();

        if (detectedPitch > 0) {
            SwingUtilities.invokeLater(() -> currentPitchLabel.setText("Current Pitch: " + detectedPitch + " Hz")); // Update current pitch label

            float[] notePitches = getNotePitches();
            String[] noteNames = getNoteNames();

            float minDifference = Float.MAX_VALUE;
            int closestNoteIndex = -1;

            for (int i = 0; i < notePitches.length; i++) {
                float difference = Math.abs(notePitches[i] - detectedPitch);
                if (difference < minDifference) {
                    minDifference = difference;
                    closestNoteIndex = i;
                }
            }

            if (closestNoteIndex != -1 && noteNames[closestNoteIndex].equals(noteNames[currentNoteIndex])) {
                currentNoteIndex++;
                if (currentNoteIndex >= noteNames.length) {
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Congratulations! You've completed the scale."));
                    stopMic();
                } else {
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Correct! Sing the next note: " + noteNames[currentNoteIndex]));
                }
            }
        }
    }
}
