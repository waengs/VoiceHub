package org.example;

// interface used for song and scale
public interface Instructions {
    String instructions();
    String[] getNoteNames();
    float[] getNotePitches();
}
