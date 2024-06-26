package org.example;
// Implementation of the Instructions interface for a specific song
public class Song implements Instructions {

    // Array of note names for the song
    private String[] noteNames = {
            "G", "A", "G", "E", "G", "A", "G", "E",
            "D", "B", "C5", "G", "A", "C5", "B", "A",
            "G", "A", "G", "E", "D", "F", "D", "B",
            "C5", "E", "C5", "G", "E", "G", "F", "D", "C5"
    };

    // Array of corresponding note pitches in Hz
    private float[] notePitches = {
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
            523.25f  // C5
    };

    // Method from the Instructions interface to provide instructions for the song
    @Override
    public String instructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Let's Play 'Silent Night':\n");
        for (int i = 0; i < noteNames.length; i++) {
            sb.append(noteNames[i]).append(" - ").append(notePitches[i]).append("Hz\n");
        }
        return sb.toString(); // Return the complete instructions as a string
    }

    // Method from the Instructions interface to get the note names
    @Override
    public String[] getNoteNames() {
        return noteNames;
    }

    // Method from the Instructions interface to get the note pitches
    @Override
    public float[] getNotePitches() {
        return notePitches;
    }
}
