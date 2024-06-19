package org.example;
// Implementation of the Instructions interface for a scale
public class Scale implements Instructions {

    // Array of note names for the scale
    private final String[] noteNames = {
            "Do", "Re", "Mi", "Fa", "Sol", "La", "Ti", "Do"
    };

    // Array of corresponding note pitches in Hz
    private final float[] notePitches = {
            261.63f, // C4 (Do)
            293.66f, // D4 (Re)
            329.63f, // E4 (Mi)
            349.23f, // F4 (Fa)
            392.00f, // G4 (Sol)
            440.00f, // A4 (La)
            493.88f, // B4 (Ti)
            523.25f  // C5 (Do)
    };


    @Override
    public String instructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Let's learn the scale doremifasolatido:\n");
        // Append each note name and its corresponding pitch to the instructions
        sb.append("C4 (Do) - 261.63Hz\n");
        sb.append("D4 (Re) - 293.66Hz\n");
        sb.append("E4 (Mi) - 329.63Hz\n");
        sb.append("F4 (Fa) - 349.23Hz\n");
        sb.append("G4 (Sol) - 392.00Hz\n");
        sb.append("A4 (La) - 440.00Hz\n");
        sb.append("B4 (Ti) - 493.88Hz\n");
        sb.append("C5 (Do) - 523.25Hz\n");
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
