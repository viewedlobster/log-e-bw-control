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
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class MPK261ExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("a92b98c2-1b4e-4606-913c-fabec101b9ad");
   
   public MPK261ExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "MPK 261 log-e";
   }
   
   @Override
   public String getAuthor()
   {
      return "lnsol";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Akai";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "MPK 261";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 19;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 2;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 2;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
      if (platformType == PlatformType.WINDOWS)
      {
         list.add(new String[]{"MPK261", "MIDIIN4 (MPK261)"}, new String[]{"MPK261","MIDIOUT4 (MPK261)"});
      }
      else if (platformType == PlatformType.MAC)
      {
         list.add(new String[]{"MPK261 Port A", "MPK261 Remote"}, new String[]{"MPK261 Port A", "MPK261 Remote"});
      }
      else if (platformType == PlatformType.LINUX)
      {
         list.add(new String[]{"MPK261 MIDI 1", "MPK261 MIDI 4"}, new String[]{"MPK261 MIDI 1", "MPK261 MIDI 4"});
      }
   }

   @Override
   public MPK261Extension createInstance(final ControllerHost host)
   {
      return new MPK261Extension(this, host);
   }
}
