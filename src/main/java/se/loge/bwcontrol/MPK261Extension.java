package se.loge.bwcontrol;

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;

import com.bitwig.extension.controller.api.HardwareButton;
import com.bitwig.extension.controller.api.HardwareSurface;

public class MPK261Extension extends ControllerExtension
{
   protected MPK261Extension(final MPK261ExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }

   @Override
   public void init()
   {
      final ControllerHost host = getHost();

      mTransport = host.createTransport();
      host.getMidiInPort(0).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi0(msg));
      host.getMidiInPort(0).setSysexCallback((String data) -> onSysex0(data));
      host.getMidiInPort(1).setMidiCallback((ShortMidiMessageReceivedCallback)msg -> onMidi1(msg));
      host.getMidiInPort(1).setSysexCallback((String data) -> onSysex1(data));

      HardwareSurface hwsurface = host.createHardwareSurface();
      HardwareButton playButton = hwsurface.createHardwareButton("play button");
      HardwareButton stopButton = hwsurface.createHardwareButton("stop button");
      HardwareButton recordButton = hwsurface.createHardwareButton("rec button");

      // playButton.pressedAction().port

      // TODO: Perform your driver initialization here.
      // For now just show a popup notification for verification that it is running.
      host.showPopupNotification("MPK 261 Initializedd");
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
      // TODO Send any updates you need here.
   }

   /** Called when we receive short MIDI message on port 0. */
   private void onMidi0(ShortMidiMessage msg) 
   {
      getHost().println("Midi0: " + msg.toString());
      // TODO: Implement your MIDI input handling code here.
   }

   /** Called when we receive sysex MIDI message on port 0. */
   private void onSysex0(final String data) 
   {
      // MMC Transport Controls:
      if (data.equals("f07f7f0605f7"))
            mTransport.rewind();
      else if (data.equals("f07f7f0604f7"))
            mTransport.fastForward();
      else if (data.equals("f07f7f0601f7"))
            mTransport.stop();
      else if (data.equals("f07f7f0602f7"))
            mTransport.play();
      else if (data.equals("f07f7f0606f7"))
            mTransport.record();

      getHost().println("Sysex0: " + data);
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
      getHost().println("Sysex0: " + data);
   }

   private Transport mTransport;
}
