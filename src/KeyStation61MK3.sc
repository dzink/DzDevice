KeyStation61MK3 : DzMidiDevice {
	var midiResponders;
	var cache;
	classvar < deviceName = "Keystation 61 MK3";
	classvar < keyName = "Keystation 61 MK3 MIDI 1";
	classvar < controlName = "Keystation 61 MK3 MIDI 2";
	classvar < keySrcId = 0;
	classvar < controlSrcId = 1;
	classvar < hasInput = true;
	classvar < hasOutput = false;

	keyName {
		^ this.class.keyName;
	}

	controlName {
		^ this.class.controlName;
	}

	sources {
		^ cache.at(\sources, {
			[
				this.findSource(this.keyName),
				this.findSource(this.controlName),
			]
		});
	}

	sourceNames {
		^ [\keys, \control];
	}

	keyOn {
		arg func, noteNum, chan;
		^ responderBuilder.noteOn(func, noteNum, chan, this.srcId(\keys));
	}

	keyOff {
		arg func, noteNum, chan;
		^ responderBuilder.noteOn(func, noteNum, chan, this.srcId(\keys));
	}

	bend {
		arg func;
		^ responderBuilder.bend(func, 0, this.srcId(\keys), 8192, 0, 61383);
	}

	volume {
		arg func;
		^ responderBuilder.cc(func, 7, 0, this.srcId(\keys));
	}

	mod {
		arg func;
		^ responderBuilder.cc(func, 1, 0, this.srcId(\keys));
	}

	enter {
		arg func;
		^ responderBuilder.noteOn(func, 100, nil, this.srcId(\control));
	}

	enterOff {
		arg func;
		^ responderBuilder.noteOff(func, 100, nil, this.srcId(\control));
	}

	directionalPad {
		arg func;
		var buttons = this.directionalPadButtonSymbols();
		^ responderBuilder.buttonSetOn(func, buttons.keys, nil, buttons, this.srcId(\control));
	}

	directionalPadOff {
		arg func;
		var buttons = this.directionalPadButtonSymbols();
		^ responderBuilder.buttonSetOff(func, buttons.keys, nil, buttons, this.srcId(\control));
	}

	directionalPadButtonSymbols {
		^ cache.at(\directionalPadButtonSymbols, {
			IdentityDictionary[
				96 -> SymbolDictionary[\noteNum -> 96, \name -> \up, \x -> 0, \y -> -1].lock,
				97 -> SymbolDictionary[\noteNum -> 97, \name -> \down, \x -> 0, \y -> 1].lock,
				98 -> SymbolDictionary[\noteNum -> 98, \name -> \left, \x -> -1, \y -> 0].lock,
				99 -> SymbolDictionary[\noteNum -> 99, \name -> \right, \x -> 1, \y -> 0].lock,
			];
		})
	}

	stop {
		arg func;
		^ responderBuilder.noteOn(func, 93, 0, this.srcId(\control));
	}

	play {
		arg func;
		^ responderBuilder.noteOn(func, 94, 0, this.srcId(\control));
	}

	record {
		arg func;
		^ responderBuilder.noteOn(func, 95, 0, this.srcId(\control));
	}

}
