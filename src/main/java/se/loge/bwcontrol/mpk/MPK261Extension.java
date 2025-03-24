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
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.MidiOut;

import se.loge.bwcontrol.common.BWHost;
import se.loge.bwcontrol.common.BWHost.LogLevel;
import se.loge.bwcontrol.mpk.hardware.HWController;

import com.bitwig.extension.controller.ControllerExtension;



public class MPK261Extension extends ControllerExtension
{
   private HWController hwController;

   protected MPK261Extension(final MPK261ExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();

      MPKHost.setup(host, LogLevel.INFO);

      hwController = new HWController();

      // TODO move this into separate method
      MidiIn midiIn0 = host.getMidiInPort(0);
      MidiIn midiIn1 = host.getMidiInPort(1);
      MidiOut midiOut0 = host.getMidiOutPort(0);
      MidiOut midiOut1 = host.getMidiOutPort(1);

      hwController.connectMidiIn(midiIn0, midiIn1);
      hwController.connectMidiOut(midiOut0, midiOut1);
      hwController.bindMidiIn();

      // TODO move this into separate method
      midiIn0.setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      midiIn0.setSysexCallback((String data) -> onSysex0(data));
      midiIn1.setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi1(msg));
      midiIn1.setSysexCallback((String data) -> onSysex1(data));

      MPKHost.init();
      MPKHost.updateHardware();

      BWHost.logln(LogLevel.INFO, "log-e-bw-control: Akai MPK261 extension initialized");
   }

   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer running.
      BWHost.logln(LogLevel.INFO, "log-e-bw-control: Akai MPK261 extension exited");
   }

   @Override
   public void flush()
   {
      MPKHost.updateHardware();
   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
      BWHost.debugln("Midi0: %s", msg);
   }

   /** Called when we receive sysex MIDI message on port 0. */
   private void onSysex0(final String data) 
   {
      BWHost.debugln("Sysex0: %s", data);
   }

   /** Called when we receive short MIDI message on port 1. */
   private void onMidi1(ShortMidiMessage msg) 
   {
      BWHost.debugln("Midi1: %s", msg);
   }

   /** Called when we receive sysex MIDI message on port 1. */
   private void onSysex1(final String data) 
   {
      BWHost.debugln("Sysex1: %s", data);
   }

}
