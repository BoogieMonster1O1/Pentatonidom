package io.github.boogiemonster1o1.pentatonidom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Main {
	public static final int[] PENTATONIC_SCALE = {76, 79, 81, 84, 86, 88, 91, 93, 96};

	public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException, IOException {
		Sequencer sequencer = MidiSystem.getSequencer();
		SecureRandom random = new SecureRandom();
		sequencer.open();
		Sequence seq = new Sequence(Sequence.PPQ, 4);
		Track track = seq.createTrack();
		int index = (int) (random.nextDouble() * PENTATONIC_SCALE.length);
		for (int i = 4; i < (2 * 150 * 13) + 5; i += 4) {
			boolean eighth = random.nextDouble() < 0.8;
			if (eighth && random.nextDouble() < 0.2) {
				track.add(makeEvent(144, 1, PENTATONIC_SCALE[index], 116, i));
				track.add(makeEvent(128, 1, PENTATONIC_SCALE[index], 116, i + 1));
				index = (int) (random.nextDouble() * (Math.min(index + 2, PENTATONIC_SCALE.length - 1) - Math.max(index - 2, 0))) + Math.max(index - 2, 0);
				track.add(makeEvent(144, 1, PENTATONIC_SCALE[index], 116, i + 1));
				track.add(makeEvent(128, 1, PENTATONIC_SCALE[index], 116, i + 2));
			} else {
				track.add(makeEvent(144, 1, PENTATONIC_SCALE[index], 116, i));
				track.add(makeEvent(128, 1, PENTATONIC_SCALE[index], 116, i + (eighth ? 2 : 3)));
			}
			if (eighth) {
				i -= 2;
			}
			index = (int) (random.nextDouble() * (Math.min(index + 2, PENTATONIC_SCALE.length - 1) - Math.max(index - 2, 0))) + Math.max(index - 2, 0);
		}
		sequencer.setSequence(seq);
		sequencer.setTempoInBPM(145);
//		sequencer.start();
		Path path = Path.of(".", "out", "midi", System.currentTimeMillis() + ".mid");
		Files.createDirectories(path.getParent());
		Files.createFile(path);
		MidiSystem.write(seq, 0, Files.newOutputStream(path));
		while (true) {
			if (!sequencer.isRunning()) {
				sequencer.close();
				System.exit(0);
			}
		}
	}

	public static MidiEvent makeEvent(int command, int channel, int note, int velocity, int tick) throws InvalidMidiDataException {
		ShortMessage message = new ShortMessage();
		message.setMessage(command, channel, note, velocity);
		return new MidiEvent(message, tick);
	}
}
