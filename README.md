# VoiceHub

VoiceHub is a desktop vocal training app that listens to your microphone and gives real-time feedback on pitch. Practice a C major scale (Do–Re–Mi–Fa–Sol–La–Ti–Do) or sing through *Silent Night* note by note.

## Features

- **Learn Scale** — Sing an octave of the C major scale in solfège; the app tracks each note in sequence.
- **Sing Song** — Follow the melody of *Silent Night* with on-screen note names and target frequencies.
- **Live pitch detection** — Uses the [YIN algorithm](https://en.wikipedia.org/wiki/YIN_algorithm) via [TarsosDSP](https://github.com/JorenSix/TarsosDSP) to show your current pitch in Hz.
- **Step-by-step guidance** — Prompts you for the next note after each correct match and congratulates you when you finish.

## Requirements

- **Java 21** or later
- **Maven 3.6+** (for building from the command line)
- A working **microphone**

## Getting Started

### Build

```bash
mvn compile
```

### Run

**From an IDE** (IntelliJ IDEA is recommended): open the project and run `org.example.VoiceHub`.

**From the command line** with the Exec Maven Plugin:

```bash
mvn exec:java -Dexec.mainClass="org.example.VoiceHub"
```

If the Exec plugin is not configured in your environment, you can run it once without adding it to `pom.xml`:

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass="org.example.VoiceHub"
```

## How to Use

1. Launch VoiceHub.
2. Choose a mode:
   - **Learn Scale** — practice the C major scale starting on Do (C4).
   - **Sing Song** — sing *Silent Night* starting on G.
3. Read the instructions in the center panel (note names and target Hz values).
4. Click **Start Mic** to begin pitch detection.
5. Sing each note in order. When you hit the correct pitch, the status bar advances to the next note.
6. Click **Stop Mic** to pause, or finish the sequence to see a congratulations message.

The right panel shows your **current pitch** in Hz while the mic is active.

## Project Structure

```
src/main/java/org/example/
├── VoiceHub.java        # Main Swing UI and application entry point
├── PitchDetection.java  # Microphone capture and pitch-matching logic
├── Instructions.java      # Interface for scale/song modes
├── Scale.java             # C major scale (Do–Re–Mi–Fa–Sol–La–Ti–Do)
└── Song.java              # Silent Night melody
```

## Dependencies

| Library | Purpose |
|---------|---------|
| [TarsosDSP Core](https://mvn.0110.be/releases/be/tarsos/dsp/core/) | Audio processing and pitch estimation |
| [TarsosDSP JVM](https://mvn.0110.be/releases/be/tarsos/dsp/jvm/) | JVM audio input stream support |

Dependencies are resolved from the [0110.be Maven repository](https://mvn.0110.be/releases).

## How It Works

1. Audio is captured from the default microphone at 44.1 kHz.
2. TarsosDSP's `PitchProcessor` estimates the fundamental frequency using YIN.
3. The detected pitch is compared to the closest target note for the active mode.
4. When the closest match equals the expected note in the sequence, the app advances to the next step.

## Troubleshooting

- **"Microphone not available"** — Check that a mic is connected, not in use by another app, and allowed in your OS privacy settings.
- **Notes not registering** — Sing clearly and steadily near the target frequency shown in the instructions. Background noise can affect detection.
- **Build errors** — Confirm Java 21 is installed (`java -version`) and Maven can reach the 0110.be repository.

## License

No license file is included in this repository. Contact the repository owner for usage terms.
