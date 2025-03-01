# MPK 261 bitwig control script

## structure of MPK261 hardware

So far i know the following structure

- keyboard
  * global settings
- 4 banks of pads
  * individual settings for each pad
- transport controls
  * includes:
    - rwd
    - ffwd
    - stop
    - play
    - rec
  * section-global settings for all transport buttons
    - type of message sent: mmc, midi rt, midi cc, ptex, or combo
- 3 banks of controls with each bank containing
  * 8 faders
  * 8 knobs
    - different behaviuor according to setting
  * 8 buttons
    - toggle or push/release according to setting
  * each control/bank combination has individual settings

## TODO:

- Is there a way to send control messages to light buttons in control banks?
  * Yes there is! See function in garbage/MPK2_common.js: setSwitchLED



- Sketch out software control structure that maps well to the layout of control surface.


## Software control sketches

