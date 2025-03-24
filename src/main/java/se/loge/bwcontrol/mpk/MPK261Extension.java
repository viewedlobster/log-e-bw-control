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

package se.loge.bwcontrol.mpk;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareSurface;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

import se.loge.bwcontrol.common.CallbackRegistry;
import se.loge.bwcontrol.common.CallbackPair;
import se.loge.bwcontrol.mpk.hardware.HWController;

import com.bitwig.extension.controller.ControllerExtension;



public class MPK261Extension extends ControllerExtension
{
   private HWController hwController;
   private CallbackPair<CallbackRegistry<ShortMidiMessage>, CallbackRegistry<String>> cbp;

   protected MPK261Extension(final MPK261ExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();

      MPKStore.initStore(host);

      final HardwareSurface hwsurface = host.createHardwareSurface();
      hwController = new HWController(hwsurface);

      MidiIn midiIn0 = host.getMidiInPort(0);
      MidiIn midiIn1 = host.getMidiInPort(1);
      MidiOut midiOut0 = host.getMidiOutPort(0);
      MidiOut midiOut1 = host.getMidiOutPort(1);

      cbp = MPKStore.getStore().registerMidiIn(midiIn0);

      hwController.connectMidiIn(midiIn0, midiIn1);
      host.println("Midi In connected");
      hwController.connectMidiOut(midiOut0, midiOut1);
      host.println("Midi Out connected");

      hwController.bindNoteInput();
      hwController.bindCCActions();

      // bwTransport = host.createTransport();
      midiIn0.setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      midiIn0.setSysexCallback((String data) -> onSysex0(data));
      midiIn1.setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi1(msg));
      midiIn1.setSysexCallback((String data) -> onSysex1(data));


      // set initial state
      hwController.initFinalize();

      // send update to hardware
      MPKStore.getStore().updateHardware();

      host.showPopupNotification("MPK 261 Initialized");
   }

   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer running.
      getHost().showPopupNotification("MPK 261 Exited");
   }

   @Override
   public void flush()
   {
      MPKStore.getStore().updateHardware();
   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
      if (!cbp.midi.invoke(msg))
         getHost().println("Midi0: " + msg.toString());
      // TODO: Implement your MIDI input handling code here.
   }

   /** Called when we receive sysex MIDI message on port 0. */
   private void onSysex0(final String data) 
   {
      if (!cbp.sysex.invoke(data))
         getHost().println("Sysex0: " + data);
      // MMC Transport Controls:
      //if (data.equals("f07f7f0605f7"))
      //      bwTransport.rewind();
      //else if (data.equals("f07f7f0604f7"))
      //      bwTransport.fastForward();
      //else if (data.equals("f07f7f0601f7"))
      //      bwTransport.stop();
      //else if (data.equals("f07f7f0602f7"))
      //      bwTransport.play();
      //else if (data.equals("f07f7f0606f7"))
      //      bwTransport.record();

   }
   /** Called when we receive short MIDI message on port 1. */
   private void onMidi1(ShortMidiMessage msg) 
   {
      // TODO: Implement your MIDI input handling code here.
      getHost().println("Midi1: " + msg.toString());
   }

   /** Called when we receive sysex MIDI message on port 1. */
   private void onSysex1(final String data) 
   {
      getHost().println("Sysex1: " + data);
   }

}
