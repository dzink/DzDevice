KeyStation61MK3 : DzMidiAbstractDevice {
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

	bend {
		arg func;
		^ super.bend(func, 0, keySrcId, 8192, 0, 61383);
	}

	volume {
		arg func;
		^ this.cc(func, 7, 0, keySrcId);
	}

	mod {
		arg func;
		^ this.cc(func, 1, 0, keySrcId);
	}

  //16383 8192 0
	directionalPadOn {
		arg func;
		var buttons = this.directionalPadButtonSymbols();
		^ this.pr_buttonSetOn(func, (96..100), nil, buttons, controlSrcId);
	}

	directionalPadButtonSymbols {
		^ cache.at(\directionalPadButtonSymbols, {
			var symbols = IdentityDictionary[
				96 -> SymbolDictionary[\noteNum -> 96, \name -> \up, \x -> 0, \y -> -1, \isEnter -> false],
				97 -> SymbolDictionary[\noteNum -> 97, \name -> \down, \x -> 0, \y -> 1, \isEnter -> false],
				98 -> SymbolDictionary[\noteNum -> 98, \name -> \left, \x -> -1, \y -> 0, \isEnter -> false],
				99 -> SymbolDictionary[\noteNum -> 99, \name -> \right, \x -> 1, \y -> 0, \isEnter -> false],
				100 -> SymbolDictionary[\noteNum -> 100, \name -> \enter, \x -> 0, \y -> -1, \isEnter -> true],
			];
			symbols.do {
				arg symbol;
				symbol.lock();
			};
			symbols;
		})
	}

	transportOn {
		arg func;
		var buttons = this.transportButtonSymbols();
		^ this.pr_buttonSetOn(func, (93..95), nil, buttons, controlSrcId);
	}

	transportButtonSymbols {
		^ cache.at(\transportButtonSymbols, {
			var symbols = IdentityDictionary[
				93 -> SymbolDictionary[\noteNum -> 93, \name -> \stop],
				94 -> SymbolDictionary[\noteNum -> 94, \name -> \play],
				95 -> SymbolDictionary[\noteNum -> 95, \name -> \record],
			];
			symbols.do {
				arg symbol;
				symbol.lock();
			};
			symbols;
		})
	}

}
