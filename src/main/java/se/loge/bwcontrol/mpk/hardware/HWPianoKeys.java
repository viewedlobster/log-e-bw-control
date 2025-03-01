package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.bitwig.extension.controller.api.PianoKeyboard;

import se.loge.bwcontrol.HostDebug;

public class HWPianoKeys implements HWIMidiIn {
  final static int MPK261_NUM_KEYS      = 61;
  final static int MPK261_OCT           = 0;
  final static int MPK261_OCT_START_KEY = 36;

  final static String MPK_NOTE_INPUT_ID = "mpk_keyboard";

  final static int MPK_KEYS_MIDI_CHANNEL = 0;

  static final int MPK_MOD_WHEEL_MIDI_CHANNEL = 0;
  static final int MPK_MOD_WHEEL_MIDI_CC = 1;
  
  final PianoKeyboard keys;
  private NoteInput noteIn;

  HardwareSlider modWheel;

  public HWPianoKeys(HardwareSurface hwsurface) {
    keys = hwsurface.createPianoKeyboard("piano_keys", MPK261_NUM_KEYS,
      MPK261_OCT, MPK261_OCT_START_KEY);
    modWheel = hwsurface.createHardwareSlider("mod_wheel");
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... rest) {
    keys.setMidiIn(midiIn);

    noteIn = midiIn.createNoteInput(MPK_NOTE_INPUT_ID,
      String.format("8%x????", MPK_KEYS_MIDI_CHANNEL),
      String.format("9%x????", MPK_KEYS_MIDI_CHANNEL),
      String.format("a%x????", MPK_KEYS_MIDI_CHANNEL),
      String.format("b%x01??", MPK_KEYS_MIDI_CHANNEL), // mod wheel
      String.format("b%x02??", MPK_KEYS_MIDI_CHANNEL), // breath
      String.format("b%x40??", MPK_KEYS_MIDI_CHANNEL), // sustain
      String.format("b%x47??", MPK_KEYS_MIDI_CHANNEL), // timbre
      String.format("d%x????", MPK_KEYS_MIDI_CHANNEL),
      String.format("e%x????", MPK_KEYS_MIDI_CHANNEL)
      );
  }

}
