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

import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;

public class HWPadBank implements HWIMidiIn, HWIMidiOut {

  final static int MPK261_NUM_PADS = 16;

  final HWPad[] pads;

  final String id;
  final NoteRange range;

  public HWPadBank(HardwareSurface hwsurface, String bankId, NoteRange r, int padIdxOffset) {
    pads = new HWPad[MPK261_NUM_PADS];
    id = bankId;
    range = r;

    for ( int i = 0; i < MPK261_NUM_PADS; i++ ) {
      pads[i] = new HWPad(hwsurface, padIdxOffset + i, bankId, range.getNote(i));
    }
  }

  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < pads.length; i++) {
      pads[i].connectMidiIn(midiIn, midiIns);
    }
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    for (int i = 0; i < MPK261_NUM_PADS; i++) {
      pads[i].connectMidiOut(midiOut, midiOuts);
    }
  }
}
