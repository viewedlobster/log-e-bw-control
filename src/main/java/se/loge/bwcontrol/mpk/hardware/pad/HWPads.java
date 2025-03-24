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
import com.bitwig.extension.controller.api.HardwareActionBindable;
import com.bitwig.extension.controller.api.HardwareActionMatcher;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;
import com.bitwig.extension.controller.api.NoteInput;

import se.loge.bwcontrol.common.ExtensionStore;
import se.loge.bwcontrol.common.SysexBuilder;
import se.loge.bwcontrol.mpk.hardware.NoteRange;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasHost;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasOutputState;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;
import se.loge.bwcontrol.mpk.hardware.ifc.HWINoteInput;
import se.loge.bwcontrol.mpk.state.MPKStateAccess;
import se.loge.bwcontrol.mpk.MPKConst;

public class HWPads implements HWIHasHost, HWIMidiIn, HWIMidiOut, HWINoteInput, HWIHasOutputState {
  static final String MPK_PADS_NOTE_INPUT_ID = "mpk_pads";
  // TODO remove duplication between this and padbank class
  static final int MPK_PAD_BANK_SIZE = 16;
  static final int MPK_NUM_PAD_BANKS = 4;

  static final int MPK_PADS_MIDI_CHANNEL = 9;

  static final int MPK_NUM_MIDI_NOTES = 128;

  private final Integer[] allNotesOff;
  private final Integer[] allNotesOn;

  private MidiIn midi0In;
  private final HWPadBank[] padBanks;

  @SuppressWarnings("unused")
  private NoteInput noteIn;
  private MidiOut midiRemoteOut;

  private ClipLauncherSlotBank primaryClips;
  private HardwareActionBindable[] recActions;
  private HardwareActionMatcher[] noteOns;

  public enum PadMode {
    MPK_PAD_NOTES_PLAY,
    MPK_PAD_CLIP_START_RECORD,
    MPK_PAD_CLIP_TRIGGER,
    MPK_PAD_NOPE;
  }

  public interface UsingPadMode {
    public default PadMode getPadMode() {
      MPKStateAccess s = (MPKStateAccess)ExtensionStore.getStore().extra();
      return s.getPadMode();
    }

    public default void setPadMode(PadMode m) {
      MPKStateAccess s = (MPKStateAccess)ExtensionStore.getStore().extra();
      s.setPadMode(m);
    }

    public default void revertPadMode(PadMode m) {
      MPKStateAccess s = (MPKStateAccess)ExtensionStore.getStore().extra();
      s.revertPadMode(m);
    }
  }

  private PadMode mode;
  private PadMode prevMode;


  public HWPads(HardwareSurface hwsurface) {
    // for global access to mode
    ((MPKStateAccess)ExtensionStore.getStore().extra()).setHWPads(this);

    // set constants for mode switching
    allNotesOff = new Integer[MPK_NUM_MIDI_NOTES];
    allNotesOn = new Integer[MPK_NUM_MIDI_NOTES];
    for (int i = 0; i < MPK_NUM_MIDI_NOTES; i++) {
      allNotesOff[i] = -1;
      allNotesOn[i] = i;
    }

    // init mode
    mode = PadMode.MPK_PAD_NOPE;

    // primary track clips shared between pad banks
    primaryClips = primaryTrack().clipLauncherSlotBank();

    // init pad banks
    padBanks = new HWPadBank[MPK_NUM_PAD_BANKS];
    for (int bankIdx = 0; bankIdx < MPK_NUM_PAD_BANKS; bankIdx++) {
      padBanks[bankIdx] =
        new HWPadBank(hwsurface,
          bankIdx,
          new NoteRange(bankIdx*MPK_PAD_BANK_SIZE, MPK_PAD_BANK_SIZE),
          MPK_PAD_BANK_SIZE * bankIdx, primaryClips);
    }

    primaryTrack().color().addValueObserver(
      (r, g, b) -> { updateLeds(); }
    );
  }

  public void setMode(PadMode mode) {
    if (mode != this.mode) {
      modeRemap(this.mode, mode);
      this.prevMode = this.mode;
      this.mode = mode;
      updateLeds();
    }
  }

  public PadMode getMode() {
    return this.mode;
  }

  private void updateLeds() {
    switch (this.mode) {
      case MPK_PAD_NOTES_PLAY:
        ledPlayMode(true);
        break;
      case MPK_PAD_CLIP_START_RECORD:
        ledClipRecordMode(true);
        break;
      case MPK_PAD_CLIP_TRIGGER:
        // TODO
        break;
      case MPK_PAD_NOPE:
        // TODO
        break;
      default:
        println("unrecognized mode: " + this.mode.toString());
    }
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

  private void modeRemap(PadMode ol, PadMode nw) {
    // note input unmapping/consume
    switch (ol) {
      case MPK_PAD_NOTES_PLAY:
        noteIn.setKeyTranslationTable(allNotesOff);
      case MPK_PAD_CLIP_START_RECORD:
      case MPK_PAD_CLIP_TRIGGER:
        noteIn.setShouldConsumeEvents(true);
        break;
      case MPK_PAD_NOPE:
        break;
      default:
        println("Unrecognized mode: " + mode.toString());
    }

    for (int bank = 0; bank < MPK_NUM_PAD_BANKS; bank++) {
      for (int pad = 0; pad < MPK_PAD_BANK_SIZE; pad++) {
        padBanks[bank].pads[pad].modeRemap(ol, nw);
      }
    }

    // note input mapping/unconsume
    switch (nw) {
    case MPK_PAD_NOTES_PLAY:
      noteIn.setKeyTranslationTable(allNotesOn);
      break;
    case MPK_PAD_CLIP_START_RECORD:
    case MPK_PAD_CLIP_TRIGGER:
      noteIn.setShouldConsumeEvents(false);
      break;
    case MPK_PAD_NOPE:
      break;
    }
  }

  public void revertMode() {
    setMode(this.prevMode);
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
    println("sending sysex1: " + SysexBuilder.bytesToHexString(sysex));
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
      println("sending sysex1: " + SysexBuilder.bytesToHexString(sysex));
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
  public void bindNoteInput() {
    noteIn = midi0In.createNoteInput(MPK_PADS_NOTE_INPUT_ID,
      String.format("8%x????", MPK_PADS_MIDI_CHANNEL), // note off
      String.format("9%x????", MPK_PADS_MIDI_CHANNEL), // note on
      String.format("a%x????", MPK_PADS_MIDI_CHANNEL), // poly aftertouch
      String.format("d%x????", MPK_PADS_MIDI_CHANNEL) // mono aftertouch
      );
    noteIn.setKeyTranslationTable(allNotesOff);
  }

  @Override
  public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
    for (int i = 0; i < padBanks.length; i++) {
      padBanks[i].connectMidiOut(midiOut, midiOuts);
    }
    midiRemoteOut = midiOuts[0];
  }

  public void initFinalize() {
    setMode(PadMode.MPK_PAD_NOTES_PLAY);
  }
}
