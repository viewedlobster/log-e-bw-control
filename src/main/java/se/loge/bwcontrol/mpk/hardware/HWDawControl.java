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

package se.loge.bwcontrol.mpk.hardware;

import com.bitwig.extension.controller.api.MidiIn;

import se.loge.bwcontrol.common.ifc.CMidiIn;
import se.loge.bwcontrol.common.ifc.HasBWHost;

import com.bitwig.extension.controller.api.HardwareButton;

public class HWDawControl implements HasBWHost, CMidiIn {
  final HardwareButton enter;
  final HardwareButton up;
  final HardwareButton down;
  final HardwareButton left;
  final HardwareButton right;

  // TODO replace values with proper ones from hardware
  final static int MIDI_ENTER = 0x1;
  final static int MIDI_UP    = 0x2;
  final static int MIDI_DOWN  = 0x3;
  final static int MIDI_LEFT  = 0x4;
  final static int MIDI_RIGHT = 0x5;

  public HWDawControl() {
    enter = surface().createHardwareButton("dawc_enter");
    up = surface().createHardwareButton("dawc_up");
    down = surface().createHardwareButton("dawc_down");
    left = surface().createHardwareButton("dawc_left");
    right = surface().createHardwareButton("dawc_right");

  }


  public void connectMidiIn(MidiIn midiIn, MidiIn... midiIns) {
    // TODO: implement
  }

  public void bindMidiIn() {
    // TODO implement
  }

}
