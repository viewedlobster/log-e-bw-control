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

package se.loge.bwcontrol.mpk.hardware.pad;

import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

import se.loge.bwcontrol.common.ifc.HasOutputState;
import se.loge.bwcontrol.common.ifc.CMidiIn;
import se.loge.bwcontrol.common.ifc.CMidiOut;
import se.loge.bwcontrol.common.ifc.HasBWHost;
import se.loge.bwcontrol.mpk.hardware.NoteRange;

public class HWPadBank implements HasBWHost, CMidiIn, CMidiOut, HasOutputState {

  final static int MPK261_NUM_PADS = 16;
  static final String[] MPK_PAD_BANK_NAMES = {
    "A",
    "B",
    "C",
    "D",
  };

  final HWPad[] pads;

  private final String id;
  private final NoteRange range;
  private final DrumPadBank drumPads;
  private final ClipLauncherSlotBank primaryClips;

  public HWPadBank(int bankIdx, NoteRange r, int padIdxOffset,
      ClipLauncherSlotBank clips) {
    this.id = MPK_PAD_BANK_NAMES[bankIdx];
    this.pads = new HWPad[MPK261_NUM_PADS];
    this.range = r;
    this.primaryClips = clips;

    // create drum pad bank and set it to correct index
    this.drumPads = primaryInstrument().createDrumPadBank(MPK261_NUM_PADS);
    this.drumPads.scrollPosition().set(padIdxOffset);
    this.drumPads.exists().markInterested();

    for ( int i = 0; i < MPK261_NUM_PADS; i++ ) {
      ClipLauncherSlot clip = i < primaryClips.getSizeOfBank() ? primaryClips.getItemAt(i) : null;
      debugln(Integer.toString(range.getNote(i)));
      pads[i] = new HWPad(padIdxOffset + i, id, range.getNote(i),
        drumPads.getItemAt(i), clip);
    }



    //drumPads.exists().addValueObserver(
    //  (exsts) -> {
    //    if (exsts) {
    //      drumPads.scrollPosition().set(bankIdx * MPK261_NUM_PADS);
    //    }
    //  }
    //);

  }

  public void ledPlayMode(boolean signal) {
    if (drumPads.exists().get()) {
      for (int pad = 0; pad < MPK261_NUM_PADS; pad++) {
        pads[pad].ledPlayModeDrumPad(false);
      }
    } else {
      for (int pad = 0; pad < MPK261_NUM_PADS; pad++) {
        pads[pad].ledPlayMode(false);
      }
    }

    if (signal) {
      signalHardwareUpdate();
    }
  }

  public void ledClipRecordMode(boolean signal) {
    for (int pad = 0; pad < MPK261_NUM_PADS; pad++) {
      pads[pad].ledClipRecordMode(false);
    }
    if (signal) {
      signalHardwareUpdate();
    }
  }

  @Override
  public void onHardwareUpdate() {
    // TODO check if this can be batched, but atm this is rarely/never called
    for (int pad = 0; pad < MPK261_NUM_PADS; pad++) {
      pads[pad].onHardwareUpdate();
    }
  }

  @Override
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

  @Override
  public void bindMidiIn() {
    for (int pad = 0; pad < MPK261_NUM_PADS; pad++) {
      pads[pad].bindMidiIn();
    }
  }
}
