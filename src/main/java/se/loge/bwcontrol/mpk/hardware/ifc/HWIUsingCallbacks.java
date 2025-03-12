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

package se.loge.bwcontrol.mpk.hardware.ifc;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.MidiIn;

import se.loge.bwcontrol.common.CallbackRegistry;
import se.loge.bwcontrol.common.ExtensionStore;

public interface HWIUsingCallbacks {
  public default void registerSysexCallback(MidiIn midi, CallbackRegistry.MatchingCallback<String> cb) {
    ExtensionStore.getStore().registerSysexCallback(midi, cb);
  }
  public default void registerMidiCallback(MidiIn midi, CallbackRegistry.MatchingCallback<ShortMidiMessage> cb) {
    ExtensionStore.getStore().registerMidiCallback(midi, cb);
  }
}
