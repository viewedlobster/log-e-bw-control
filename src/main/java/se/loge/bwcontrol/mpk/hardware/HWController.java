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

 // Some notes for future implementation?

// registerMidiCallback(midi0In, (msg) -> onMidi0Action(msg));
/* S1-S4 are bound to knob and fader remote controls page navigation */ 
/*S[0].pressedAction().addBinding(controlsK.selectPreviousAction());
S[1].pressedAction().addBinding(controlsK.selectNextAction());
S[2].pressedAction().addBinding(controlsF.selectPreviousAction());
S[3].pressedAction().addBinding(controlsF.selectNextAction());
*/
/* S5-S8 remain unbound */
/* Ideas for use
 * - setting pad mode: 
 *   * play instrument
 *   * play clips current track,
 *   * rec clips current track, 
 *   * global queues/actions (i.e. bound manually)
 */
// S[4].pressedAction().addBinding(null);
// S[5].pressedAction().addBinding(null);
// S[6].pressedAction().addBinding(null);
// S[7].pressedAction().addBinding(null);
package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIControlCC;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiIn;
import se.loge.bwcontrol.mpk.hardware.ifc.HWIMidiOut;
import se.loge.bwcontrol.mpk.hardware.ifc.HWINoteInput;

public class HWController implements HWIMidiIn, HWIMidiOut, HWINoteInput, HWIControlCC {

   ControllerExtension ext;

   private HardwareSurface hwsurface;

   private HWTransport transport;
   private HWDawControl dawControl;
   private HWControlBank bankA, bankB, bankC;
   private HWPianoKeys pianoKeys;
   private HWPads pads;

   static final int MPK_NUM_MIDI_IN             = 2;
   static final int MPK_NUM_MIDI_OUT            = 2;

   public HWController(HardwareSurface surface) {
      hwsurface = surface;

      transport = new HWTransport(hwsurface);
      dawControl = new HWDawControl(hwsurface);
      pianoKeys = new HWPianoKeys(hwsurface);
      pads = new HWPads(hwsurface);

      bankA = new HWControlBankA(hwsurface);
      bankB = new HWControlBankB(hwsurface);
      bankC = new HWControlBankC(hwsurface);
   }

   public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
      assert(midiIns.length == MPK_NUM_MIDI_IN - 1);

      transport.connectMidiIn(midiIn);
      dawControl.connectMidiIn(midiIn);
      pianoKeys.connectMidiIn(midiIn);
      pads.connectMidiIn(midiIn);

      for (HWControlBank bank : new HWControlBank[] { bankA, bankB, bankC }) {
         bank.connectMidiIn(midiIn);
      }
   }

   public void bindNoteInput() {
      pianoKeys.bindNoteInput();
      pads.bindNoteInput();
   }

   public void bindCCActions() {
      transport.bindCCActions();
      dawControl.bindCCActions();

      for (HWControlBank bank : new HWControlBank[] { bankA, bankB, bankC }) {
         bank.bindCCActions();
      }
   }

   public void connectMidiOut(MidiOut midiOut, MidiOut... midiOuts) {
      assert(midiOuts.length == MPK_NUM_MIDI_OUT - 1);
      bankA.connectMidiOut(midiOut);
   }

}
