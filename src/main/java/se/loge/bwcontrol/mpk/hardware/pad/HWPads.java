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

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.NoteInput;

import se.loge.bwcontrol.common.CStateField;
import se.loge.bwcontrol.common.SysexBuilder;
import se.loge.bwcontrol.common.ifc.HasOutputState;
import se.loge.bwcontrol.common.ifc.CMidiIn;
import se.loge.bwcontrol.common.ifc.CMidiOut;
import se.loge.bwcontrol.common.ifc.HasBWHost;
import se.loge.bwcontrol.mpk.hardware.NoteRange;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMPKStateAccess;
import se.loge.bwcontrol.mpk.state.MPKCState.PadEvt;
import se.loge.bwcontrol.mpk.state.MPKCState.PadMode;
import se.loge.bwcontrol.mpk.MPKConst;

public class HWPads implements HasBWHost, CMidiIn, CMidiOut, HasOutputState, HWIMPKStateAccess {
  public static final String MPK_PADS_NOTE_INPUT_ID = "mpk_pads";
  // TODO remove duplication between this and MPKConst
  public static final int MPK_PAD_BANK_SIZE = 16;
  public static final int MPK_NUM_PAD_BANKS = 4;

  public static final int MPK_PADS_MIDI_CHANNEL = 9;

  public static final int MPK_NUM_MIDI_NOTES = 128;

  public static final int MPK_PADS_NOTE_OFFSET = 36;

  private final Integer[] allNotesOff;
  private final Integer[] allNotesOn;

  private MidiIn midi0In;
  private final HWPadBank[] padBanks;

  @SuppressWarnings("unused")
  private NoteInput noteIn;
  private MidiOut midiRemoteOut;

  private ClipLauncherSlotBank primaryClips;

  CStateField<PadMode, PadEvt>.CStateConn<PadMode, PadEvt> padMode;

  public HWPads() {
    // for global access to mode
    padMode = state().padMode().connect((mode) -> this.onPadModeUpdate(mode));

    // set constants for mode switching
    allNotesOff = new Integer[MPK_NUM_MIDI_NOTES];
    allNotesOn = new Integer[MPK_NUM_MIDI_NOTES];
    for (int i = 0; i < MPK_NUM_MIDI_NOTES; i++) {
      allNotesOff[i] = -1;
      allNotesOn[i] = i;
    }

    // primary track clips shared between pad banks
    primaryClips = primaryTrack().clipLauncherSlotBank();

    // init pad banks
    padBanks = new HWPadBank[MPK_NUM_PAD_BANKS];
    for (int bankIdx = 0; bankIdx < MPK_NUM_PAD_BANKS; bankIdx++) {
      padBanks[bankIdx] =
        new HWPadBank(bankIdx,
          new NoteRange(MPK_PADS_NOTE_OFFSET + bankIdx*MPK_PAD_BANK_SIZE,
                        MPK_PAD_BANK_SIZE),
          MPK_PAD_BANK_SIZE * bankIdx, primaryClips);
    }

    primaryTrack().color().addValueObserver(
      (r, g, b) -> { updateLeds(); }
    );
  }

  private void onPadModeUpdate(PadMode mode) {
    modeRemap(mode);
    updateLeds(mode);
  }

  private void updateLeds(PadMode mode) {
    switch (mode.rec) {
    case RECMODE_ON:
      ledClipRecordMode(true);
      break;
    case RECMODE_OFF:
      switch (mode.play) {
      case NOTE_PLAY:
        ledPlayMode(true);
        break;
      case CLIP_PLAY:
        errorln("Clip play mode not implemented yet, you shouldn't see this message.");
        break;
      }
      break;
    }
  }

  private void updateLeds() {
    updateLeds(padMode.get());
  }

  private void ledPlayMode(boolean signal) {
    for (int bank = 0; bank < MPK_NUM_PAD_BANKS; bank++) {
      padBanks[bank].ledPlayMode(false);
    }
    if (signal) {
      signalHardwareUpdate();
    }
  }

  private void ledClipRecordMode(boolean signal) {
    for (int bank = 0; bank < MPK_NUM_PAD_BANKS; bank++) {
      padBanks[bank].ledClipRecordMode(false);
    }
    if (signal) {
      signalHardwareUpdate();
    }
  }

  private void modeRemap(PadMode mode) {
    // note input on/off
    // actions are mapped in pad class
    debugln("remapping pad mode");
    switch (mode.rec) {
    case RECMODE_ON:
      noteIn.setKeyTranslationTable(allNotesOff);
      noteIn.setShouldConsumeEvents(false);
      break;
    case RECMODE_OFF:
      switch (mode.play) {
        case NOTE_PLAY:
          noteIn.setKeyTranslationTable(allNotesOn);
          noteIn.setShouldConsumeEvents(true);
          break;
        case CLIP_PLAY:
          noteIn.setKeyTranslationTable(allNotesOff);
          noteIn.setShouldConsumeEvents(false);
          break;
      }
      break;
    }
  }

  public HWPad getPad(int bankIdx, int bankOffset) {
    return padBanks[bankIdx].pads[bankOffset];
  }

  public HWPad getPad(int i) {
    int bankIdx = i / MPK_PAD_BANK_SIZE;
    int bankOffset = i % MPK_PAD_BANK_SIZE;

    return getPad(bankIdx, bankOffset);
  }

  public void updatePressedColors() {
    SysexBuilder b = new SysexBuilder();

    b.add("F0 47 00");
    b.add(MPKConst.MPK261_PRODUCT_ID);
    b.add("31 00 43 40");
    b.add(SysexBuilder.msb7(MPKConst.MPK_PAD_LIGHT_PRESSED_COLOR_MIN));
    b.add(SysexBuilder.lsb7(MPKConst.MPK_PAD_LIGHT_PRESSED_COLOR_MIN));
    for (int i = 0; i < MPK_NUM_PAD_BANKS * MPK_PAD_BANK_SIZE; i++) {
      b.add(getPad(i).pressedColorByte());
    }
    b.add("F7");

    byte[] sysex = b.build();
    debugln("sending sysex1: " + SysexBuilder.bytesToHexString(sysex));
    if (midiRemoteOut != null) {
      midiRemoteOut.sendSysex(sysex);
    }
  }

  public void updateColors() {
    SysexBuilder b = new SysexBuilder();

    b.add("F0 47 00");
    b.add(MPKConst.MPK261_PRODUCT_ID);
    b.add("31 00 43 40");
    b.add(SysexBuilder.msb7(MPKConst.MPK_PAD_LIGHT_COLOR_MIN));
    b.add(SysexBuilder.lsb7(MPKConst.MPK_PAD_LIGHT_COLOR_MIN));
    for (int i = 0; i < MPK_NUM_PAD_BANKS * MPK_PAD_BANK_SIZE; i++) {
      b.add(getPad(i).colorByte());
    }
    b.add("F7");

    byte[] sysex = b.build();
    if (midiRemoteOut != null) {
      debugln("sending sysex1: " + SysexBuilder.bytesToHexString(sysex));
      midiRemoteOut.sendSysex(sysex);
    }
  }

  @Override
  public void onHardwareUpdate() {
    updateColors();
  }

  @Override
  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    for (int i = 0; i < padBanks.length; i++) {
      padBanks[i].connectMidiIn(midiIn, midiIns);
    }
    midi0In = midiIn;
  }

  @Override
  public void bindMidiIn() {
    for (int i = 0; i < MPK_NUM_PAD_BANKS; i++) {
      padBanks[i].bindMidiIn();
    }
    noteIn = midi0In.createNoteInput(MPK_PADS_NOTE_INPUT_ID,
      String.format("8%x????", MPK_PADS_MIDI_CHANNEL), // note off
      String.format("9%x????", MPK_PADS_MIDI_CHANNEL), // note on
      String.format("a%x????", MPK_PADS_MIDI_CHANNEL), // poly aftertouch
      String.format("d%x????", MPK_PADS_MIDI_CHANNEL) // mono aftertouch
      );
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    for (int i = 0; i < padBanks.length; i++) {
      padBanks[i].connectMidiOut(midiOut, midiOuts);
    }
    midiRemoteOut = midiOuts[0];
  }
}
