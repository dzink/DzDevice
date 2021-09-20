# DzDevice

A library of shortcuts to interface with MIDI devices.

## Supported MIDI Devices

### AkaiMpd232 Akai MPD 232 Drum pad

#### Pad Responders
`.padsOn()`, `.padsOff()`, and `.polytouch()`.
These will respond to pad events. The noteDef returned will be a SymbolDictionary with the following parameters:
- note: the midi note
- vel: the velocity of the button
- index: the id from 0..15 of the button
- x: the x value from 0..3 of the button
- y: the y value from 0..3 of the button
- pan: the x value from -1..1 of the button
- zoom: the y value from -1..1 of the button

The positional parameters x, y, pan, and zoom assume that the numbers start in the lower left hand corner and rise up going to the right, and then up a row etc.

#### Control Change Array Responders
`.sliders()`, `.buttons()`, `.knobs()`

These will take a function and an array of cc numbers and create the given responder. `.buttons()` also takes a second function for when the button is off. The SymbolDictionary passed to each function will have the following properties:

- ccNum: the control change number from 0..127
- index: the index from 0..7
- value: the value of the control
- on: whether the control is considered on or off. "Off" is 0, "on" is all other numbers.
- name: the name of the knob

#### Other Responders
`.stop()`, `play()`, `record()`

These work precisely how you'd expect them to. The run whatever function when the given button is pressed.

### M-Audio Keystation 61 MK3 keyboard

#### Keyboard responders
`.keyOn()`, `.keyOff()`

Respond to key presses and releases.

#### Control Change Responders
`.bend()`, `.mod()`, `.volume()`

#### Other responders
`.directionalPad()`, `.directionalPadOff`, `.enter()`, `.enterOff()`

Directional pad responders provide a SymbolDictionary with the following properties:

- noteNum: The number of the button 96..99
- noteName: The name of the button.
- x: The change in x
- y: The change in y
