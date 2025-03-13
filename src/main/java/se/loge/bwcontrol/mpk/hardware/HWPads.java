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

import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.NoteInput;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;
import se.loge.bwcontrol.mpk.hardware.ifc.HWINoteInput;

public class HWPads implements HWIMidiIn, HWIMidiOut, HWINoteInput {
  static final String MPK_PADS_NOTE_INPUT_ID = "mpk_pads";
  static final String[] MPK_PAD_BANK_NAMES = {
    "A",
    "B",
    "C",
    "D",
  };
  static final int MPK_PAD_BANK_SIZE = 16;

  static final int MPK_PADS_MIDI_CHANNEL = 9;

  private MidiIn in;
  private final HWPadBank[] padBanks;

  @SuppressWarnings("unused")
  private NoteInput noteIn;

  public HWPads(HardwareSurface hwsurface) {

    padBanks = new HWPadBank[MPK_PAD_BANK_NAMES.length];
    for (int i = 0; i < MPK_PAD_BANK_NAMES.length; i++) {
      padBanks[i] = new HWPadBank(hwsurface, MPK_PAD_BANK_NAMES[i],
        new NoteRange(i*MPK_PAD_BANK_SIZE, MPK_PAD_BANK_SIZE));
    }
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < padBanks.length; i++) {
      padBanks[i].connectMidiIn(midiIn, midiIns);
    }
    in = midiIn;
  }

  public void bindNoteInput() {
    noteIn = in.createNoteInput(MPK_PADS_NOTE_INPUT_ID,
      String.format("8%x????", MPK_PADS_MIDI_CHANNEL), // note off
      String.format("9%x????", MPK_PADS_MIDI_CHANNEL), // note on
      String.format("a%x????", MPK_PADS_MIDI_CHANNEL), // poly aftertouch
      String.format("d%x????", MPK_PADS_MIDI_CHANNEL) // mono aftertouch
      );
  }

  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    // TODO: implement
  }



}
