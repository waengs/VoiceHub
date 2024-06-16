package org.example;

public class Song implements Instructions {

    private String[] noteNames = {
            "G", "A", "G", "E", "G", "A", "G", "E",
            "D", "B", "C", "G", "A", "C", "B", "A",
            "G", "A", "G", "E", "D", "F", "D", "B",
            "C", "E", "C", "G", "E", "G", "F", "D", "C"
    };

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

    @Override
    public String instructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Let's sing 'Silent Night':\n");
        for (int i = 0; i < noteNames.length; i++) {
            sb.append(noteNames[i]).append(" - ").append(notePitches[i]).append("Hz\n");
        }
        return sb.toString();
    }

    @Override
    public String[] getNoteNames() {
        return noteNames;
    }

    @Override
    public float[] getNotePitches() {
        return notePitches;
    }
}
