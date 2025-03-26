/*
 * Copyright (C) 2025 Ellen Arvidsson
 *
 * This file is part of log-e-bw-control.
 *
 * log-e-bw-control is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * log-e-bw-control is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with log-e-bw-control. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.HardwareSlider;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.NoteInput;
import com.bitwig.extension.controller.api.PianoKeyboard;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;

public class HWPianoKeys implements HWIMidiIn {
  final static int MPK261_NUM_KEYS      = 61;
  final static int MPK261_OCT           = 0;
  final static int MPK261_OCT_START_KEY = 36;

  final static String MPK_NOTE_INPUT_ID = "mpk_keyboard";

  final static int MPK_KEYS_MIDI_CHANNEL = 0;

  static final int MPK_MOD_WHEEL_MIDI_CC = 1;
  
  final PianoKeyboard keys;
  private MidiIn in;

  @SuppressWarnings("unused")
  private NoteInput noteIn;

  HardwareSlider modWheel;

  public HWPianoKeys(HardwareSurface hwsurface) {
    keys = hwsurface.createPianoKeyboard("piano_keys", MPK261_NUM_KEYS,
      MPK261_OCT, MPK261_OCT_START_KEY);
    modWheel = hwsurface.createHardwareSlider("mod_wheel");
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... rest) {
    keys.setMidiIn(midiIn);
    modWheel.setAdjustValueMatcher(
      midiIn.createAbsoluteCCValueMatcher(
        MPK_KEYS_MIDI_CHANNEL, MPK_MOD_WHEEL_MIDI_CC));

    in = midiIn;
  }

  public void bindNoteInput() {
    noteIn = in.createNoteInput(MPK_NOTE_INPUT_ID,
      String.format("8%x????", MPK_KEYS_MIDI_CHANNEL), // note off
      String.format("9%x????", MPK_KEYS_MIDI_CHANNEL), // note on
      //String.format("a%x????", MPK_KEYS_MIDI_CHANNEL), // poly aftertouch
      String.format("b%x01??", MPK_KEYS_MIDI_CHANNEL), // mod wheel
      String.format("b%x02??", MPK_KEYS_MIDI_CHANNEL), // breath
      String.format("b%x40??", MPK_KEYS_MIDI_CHANNEL), // sustain
      String.format("b%x47??", MPK_KEYS_MIDI_CHANNEL), // timbre
      String.format("d%x????", MPK_KEYS_MIDI_CHANNEL), // mono aftertouch
      String.format("e%x????", MPK_KEYS_MIDI_CHANNEL)  // pitch bend
      );
    noteIn.setShouldConsumeEvents(false);
  }

}
