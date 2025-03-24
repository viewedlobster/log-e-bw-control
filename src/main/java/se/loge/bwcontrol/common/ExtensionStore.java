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

package se.loge.bwcontrol.common;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MidiIn;
import com.bitwig.extension.controller.api.Transport;

import se.loge.bwcontrol.mpk.hardware.ifc.HWIHasOutputState;

public abstract class ExtensionStore {
  protected static ExtensionStore store;

  public static ExtensionStore getStore() {
    assert(store != null);
    return store;
  }

  public abstract ControllerHost getHost();
  public abstract Transport getTransport();
  public abstract CursorTrack getPrimaryTrackCursor();
  public abstract CursorDevice getPrimaryDeviceCursor();
  public abstract CursorDevice getPrimaryInstrumentCursor();
  public abstract CallbackPair<CallbackRegistry<ShortMidiMessage>, CallbackRegistry<String>> registerMidiIn(MidiIn midiIn);
  public abstract void registerMidiCallback(MidiIn midiIn, CallbackRegistry.MatchingCallback<ShortMidiMessage> cb);
  public abstract void registerSysexCallback(MidiIn midiIn, CallbackRegistry.MatchingCallback<String> cb);

  public abstract void signalHardwareUpdate(HWIHasOutputState elm);
  public abstract boolean shouldHardwareUpdate();
  public abstract void updateHardware();
  
  public abstract Object extra();
}
