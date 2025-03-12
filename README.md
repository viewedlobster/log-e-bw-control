# MPK-261

A custom bitwig controller script for the Akai Professional MPK261 keyboard. The
original implementation from Akai is outdated and was pretty unusable. This
rewrite is written in java, with functionality that differs from the original,
mostly with a focus on a quick workflow when it comes to setting up instruments.
Some features as of now includes:

* Basic keyboard support, including pitch bend, mod wheel, and aftertouch.
* Basic pad support, including polyphonic aftertouch.
* Basic transport support.
* Daw control buttons left alone (i.e. they are mapped to directional buttons + enter).
* Auto-mapping of controller bank A to instrument remote control pages. 
  - Independent knob and fader bank mappings
  - Mapping only for pages with tags 'mpk-bank-a-knobs' and 'mpk-bank-a-faders'.
* Switching between remote control pages for bank A using solo buttons.
  - Chosen page is indicated by solo lights, 
  - Independent chooser modes for knobs and faders. Active chooser mode
    indicated by S8 light.

Features continuously added, but there is a bias towards a certain workflow so
don't expect anything in particular. That being said, if you have ideas on
features or ways to improve already existing ones, feel free to file an issue
here on github.

Some features i want to add include:
* Mode for playing clips using pads
* Pad LEDs supporting drum machine and clip playing
* Controls for easy on-the-fly clip recording

