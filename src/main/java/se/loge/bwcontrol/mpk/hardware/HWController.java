package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

public class HWController implements HWIMidiIn, HWIMidiOut{

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

   static final int MPK_KEYS_MIDI_IDX           = 0;
   static final int MPK_PADS_MIDI_IDX           = 0;
   static final int MPK_CONTROL_BANKS_MIDI_IDX  = 0;
   static final int MPK_DAW_CONTROL_MIDI_IDX    = 1;
   static final int MPK_TRANSPORT_MIDI_IDX      = 1;

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

      pianoKeys.connectMidiIn(midiIn);
      pads.connectMidiIn(midiIn);
   }

   public void connectMidiOut(MidiOut out, MidiOut... midiOuts) {
      assert(midiOuts.length == MPK_NUM_MIDI_OUT - 1);
      
   }

}
