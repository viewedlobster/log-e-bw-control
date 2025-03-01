package se.loge.bwcontrol.mpk.hardware;

public class NoteRange {
  private final int firstNote;
  private final int numNotes;

  static final int MIDI_MAX_NOTE = 128;
  public NoteRange(int low, int size) {
    assert(low + size < MIDI_MAX_NOTE);
    firstNote = low;
    numNotes = size;
  }

  public int getNote(int idx) {
    assert(0 <= idx && idx < numNotes);
    return firstNote + idx;
  }
}
