AkaiMpd232 : DzMidiAbstractDevice {
	classvar < deviceName = "MPD232";
	classvar < hasInput = true;
	classvar < hasOutput = false;

	padsOn {
		arg func, padNumbers = 36, channel;
		var innerFunc;
		if (padNumbers.isKindOf(Number)) {
			padNumbers = padNumbers + (0..15);
		};
		innerFunc = this.pr_padInnerFunction(func, padNumbers);
		^ this.pr_noteOn(innerFunc, padNumbers, channel, this.srcId(0))
	}

	padsOff {
		arg func, padNumbers = 36, channel;
		var innerFunc;
		if (padNumbers.isKindOf(Number)) {
			padNumbers = padNumbers + (0..15);
		};
		innerFunc = this.pr_padInnerFunction(func, padNumbers);
		^ this.pr_noteOff(innerFunc, padNumbers, channel, this.srcId(0));
	}

	polytouch {
		arg func, padNumbers = 36, channel;
		var innerFunc;
		if (padNumbers.isKindOf(Number)) {
			padNumbers = padNumbers + (0..15);
		};
		innerFunc = this.pr_padInnerFunction(func, padNumbers);
		^ this.pr_polytouch(innerFunc, padNumbers, channel, this.srcId(0));
	}

	pr_padInnerFunction {
		arg func, padNumbers;
		^ {
			arg vel, note, channel;
			var index = padNumbers.indexOf(note);
			var x = (index % 4);
			var y = (index / 4).asInteger;
			var noteDef = SymbolDictionary[
				\note -> note,
				\vel -> vel,
				\index -> index,
				\x -> x,
				\y -> y,
				\pan -> x.linlin(0, 3, -1, 1),
				\zoom -> y.linlin(0, 3, -1, 1),
			].lock;
			func.value(noteDef, vel, note, channel);
		};
	}

	transport {
		arg func;
		var buttons = this.transportCcSymbols();
		^ this.pr_ccSet(func, buttons.keys.postln, 0, buttons, this.srcId(0).postln);
	}

	sliders {
		arg func, firstCc = 20, chan;
		^ this.pr_ccArray(func, firstCc, 8, chan, this.srcId(0), "Slider %");
	}

	knobs {
		arg func, firstCc = 12, chan;
		^ this.pr_ccArray(func, firstCc, 8, chan, this.srcId(0), "Knob %");
	}

	buttons {
		arg onFunc, offFunc, firstCc = 28, chan;
		^ this.pr_ccButtonArray(onFunc, offFunc, firstCc, 8, chan, this.srcId(0), "Butty %");
	}

	stop {
		arg func;
		^ this.pr_cc(func, 117, 0, this.srcId(0));
	}

	play {
		arg func;
		^ this.pr_cc(func, 118, 0, this.srcId(0));
	}

	record {
		arg func;
		^ this.pr_cc(func, 119, 0, this.srcId(0));
	}

	transportCcSymbols {
		^ cache.at(\transportCcSymbols, {
			IdentityDictionary[
				117 -> this.stopCcSymbol(),
				118 -> this.playCcSymbol(),
				119 -> this.recordCcSymbol(),
			];
		});
	}

	stopCcSymbol {
		^ cache.at(\playCcSymbol, {
			SymbolDictionary[\noteNum -> 117, \name -> \stop].lock;
		})
	}

	playCcSymbol {
		^ cache.at(\playCcSymbol, {
			SymbolDictionary[\noteNum -> 118, \name -> \play].lock;
		})
	}

	recordCcSymbol {
		^ cache.at(\recordCcSymbol, {
			SymbolDictionary[\noteNum -> 119, \name -> \record].lock;
		})
	}

}
