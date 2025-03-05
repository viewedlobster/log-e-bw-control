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
   private HWControlBank[] controlBanks;
   private HWPianoKeys pianoKeys;
   private HWPads pads;


   static final String[] MPK_CONTROL_BANK_NAMES = {
      "A",
      "B",
      "C",
   };

   static final int MPK_NUM_MIDI_IN             = 2;
   static final int MPK_NUM_MIDI_OUT            = 2;

   public HWController(HardwareSurface surface) {
      hwsurface = surface;

      transport = new HWTransport(hwsurface);
      dawControl = new HWDawControl(hwsurface);
      pianoKeys = new HWPianoKeys(hwsurface);
      pads = new HWPads(hwsurface);

      controlBanks = new HWControlBank[MPK_CONTROL_BANK_NAMES.length];
      for (int i = 0; i < MPK_CONTROL_BANK_NAMES.length; i++) {
         controlBanks[i] = new HWControlBank(hwsurface, MPK_CONTROL_BANK_NAMES[i]);
      }
   }

   public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
      assert(midiIns.length == MPK_NUM_MIDI_IN - 1);

      transport.connectMidiIn(midiIn);
      dawControl.connectMidiIn(midiIn);
      pianoKeys.connectMidiIn(midiIn);
      pads.connectMidiIn(midiIn);

      for (int i = 0; i < controlBanks.length; i++) {
         controlBanks[i].connectMidiIn(midiIn);
      }
   }

   public void bindNoteInput() {
      pianoKeys.bindNoteInput();
      pads.bindNoteInput();
   }

   public void bindCCActions() {
      transport.bindCCActions();
      dawControl.bindCCActions();
      for (HWControlBank bank : controlBanks) {
         bank.bindCCActions();
      }
   }

   public void connectMidiOut(MidiOut out, MidiOut... midiOuts) {
      assert(midiOuts.length == MPK_NUM_MIDI_OUT - 1);
      
   }

}
