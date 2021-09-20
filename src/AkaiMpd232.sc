AkaiMpd232 : DzMidiDevice {
	classvar < deviceName = "MPD232";
	classvar < hasInput = true;
	classvar < hasOutput = false;


	/**
	 Respond to pad noteOn events.

	 @param Function func is a function to evaluate that takes the following args:
	   @args noteDef, note, vel, channel
	   noteDef will respond with a SymbolDictionary with helpful parameters. The positional parameters x, y, pan, and zoom assume that the numbers start in the lower left hand corner and rise up going to the right, and then up a row etc.
	     note: the midi note
	     vel: the velocity of the button
	     index: the id from 0..15 of the button
	     x: the x value from 0..3 of the button
	     y: the y value from 0..3 of the button
	     pan: the x value from -1..1 of the button
	     zoom: the y value from -1..1 of the button

	 @param mixed padNumbers: Either an array of MIDI note numbers to respond to, or a single starting number that will be extended to a 16-length array.
	 **/
	padsOn {
		arg func, padNumbers = 36, channel;
		var innerFunc;
		padNumbers = this.extendNumbersToArray(padNumbers, 16);
		innerFunc = this.pr_padInnerFunction(func, padNumbers);
		^ responderBuilder.noteOn(innerFunc, padNumbers, channel, this.srcId(0))
	}

	/**
	Respond to note off events. Otherwise the same as @see noteOn()
	**/
	padsOff {
		arg func, padNumbers = 36, channel;
		var innerFunc;
		padNumbers = this.extendNumbersToArray(padNumbers, 16);
		innerFunc = this.pr_padInnerFunction(func, padNumbers);
		^ responderBuilder.noteOff(innerFunc, padNumbers, channel, this.srcId(0));
	}

	/**
	Respond to polytouch events. Otherwise the same as @see noteOn()
	**/
	polytouch {
		arg func, padNumbers = 36, channel;
		var innerFunc;
		padNumbers = this.extendNumbersToArray(padNumbers, 16);
		innerFunc = this.pr_padInnerFunction(func, padNumbers);
		^ responderBuilder.polytouch(innerFunc, padNumbers, channel, this.srcId(0));
	}

	/**
	Creates the inner function used by @see noteOn, noteOff, and polytouch.
	**/
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
			func.value(noteDef, note, vel, channel);
		};
	}

	/**
	Create a responder for the sliders.
	@param Mixed ccNum can either be a single number that will be extended, or an array of numbers.
	**/
	sliders {
		arg func, ccNums = 20, chan;
		ccNums = this.extendNumbersToArray(ccNums, 8);
		^ responderBuilder.ccArray(func, ccNums, 8, chan, this.srcId(0), "Slider %");
	}

	knobs {
		arg func, ccNums = 12, chan;
		ccNums = this.extendNumbersToArray(ccNums, 8);
		^ responderBuilder.ccArray(func, ccNums, 8, chan, this.srcId(0), "Knob %");
	}


	buttons {
		arg onFunc, offFunc, ccNums = 28, chan;
		ccNums = this.extendNumbersToArray(ccNums, 8);
		^ responderBuilder.ccButtonArray(onFunc, offFunc, ccNums, 8, chan, this.srcId(0), "Butty %");
	}

	stop {
		arg func;
		^ responderBuilder.cc(func, 117, 0, this.srcId(0));
	}

	play {
		arg func;
		^ responderBuilder.cc(func, 118, 0, this.srcId(0));
	}

	record {
		arg func;
		^ responderBuilder.cc(func, 119, 0, this.srcId(0));
	}


}
