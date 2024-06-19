package org.example;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import javax.sound.sampled.*;
import javax.swing.*;

// class for pitch detection using mic input
public abstract class PitchDetection {
    private TargetDataLine microphone; // mic input line
    private AudioDispatcher dispatcher; // for processing audio streams
    private Thread dispatchThread; // to run dispatcher
    private int currentNoteIndex = 0; // index tracking current note detected
    private JLabel statusLabel; // label to show status message
    public JLabel currentPitchLabel; // Label for displaying current pitch

    // constructor to initialise pitch detection w status and pitch labels
    public PitchDetection(JLabel statusLabel, JLabel currentPitchLabel) {
        this.statusLabel = statusLabel;
        this.currentPitchLabel = currentPitchLabel;
    }

    // start mic input for pitch detection
    public void startMic() throws LineUnavailableException {
        // configure audio format
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        // obtain and open mic line
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        if (microphone == null) {
            throw new LineUnavailableException("Microphone not available");
        }
        microphone.open(format);
        microphone.start(); // start capture audio

        // Wrap the microphone input stream with JVM Audio Input Stream
        AudioInputStream stream = new AudioInputStream(microphone);
        JVMAudioInputStream jvmAudioStream = new JVMAudioInputStream(stream);
        // Create an AudioDispatcher with the JVM audio stream, buffer size 1024, and overlap 512
        dispatcher = new AudioDispatcher(jvmAudioStream, 1024, 512);

        // Create a PitchDetectionHandler using the abstract method implemented by subclasses
        PitchDetectionHandler handler = createPitchDetectionHandler();
        // Add a PitchProcessor with YIN pitch estimation algorithm to the dispatcher
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 44100, 1024, handler));

        // Start a new thread for running the audio dispatcher
        dispatchThread = new Thread(dispatcher, "Audio Dispatcher");
        dispatchThread.start();
    }

    // stop the mic
    public void stopMic() {
        // stops dispatcher if running
        if (dispatcher != null) {
            dispatcher.stop();
        }
        // stop mic and close line
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        // Interrupt the dispatcher thread if it's alive
        if (dispatchThread != null && dispatchThread.isAlive()) {
            dispatchThread.interrupt();
        }
    }

    // create pitchdetectionhandler implemented by subclasses and return pitchdetectionhandler specific mode scale or song
    protected abstract PitchDetectionHandler createPitchDetectionHandler();

    // get pitch frequencies
    protected abstract float[] getNotePitches();

    // get name of notes
    protected abstract String[] getNoteNames();

    // invoked when song / scale is complete
    protected abstract void onCompletion();

    // handle pitch detection results
    public void pitchDetection(PitchDetectionResult result) {
        float detectedPitch = result.getPitch(); // get pitch

        if (detectedPitch > 0) { // make sure valid pitch detected
            // update pitch label
            SwingUtilities.invokeLater(() -> currentPitchLabel.setText("Current Pitch: " + detectedPitch + " Hz"));

            float[] notePitches = getNotePitches(); // get pitch frequencies
            String[] noteNames = getNoteNames(); // get name of notes

            float minDifference = Float.MAX_VALUE;
            int closestNoteIndex = -1;
            // Find the closest note pitch to the detected pitch
            for (int i = 0; i < notePitches.length; i++) {
                float difference = Math.abs(notePitches[i] - detectedPitch);
                if (difference < minDifference) {
                    minDifference = difference;
                    closestNoteIndex = i;
                }
            }

            // Check if the detected note matches the expected note in the sequence
            if (closestNoteIndex != -1 && noteNames[closestNoteIndex].equals(noteNames[currentNoteIndex])) {
                currentNoteIndex++; // move to next note
                // Check if the scale or song is completed
                if (currentNoteIndex >= noteNames.length) {
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Congratulations!"));
                    stopMic(); // Stop microphone input
                    onCompletion(); // Notify completion
                } else {
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Correct! Sing the next note: " + noteNames[currentNoteIndex]));
                }
            }
        }
    }
}
