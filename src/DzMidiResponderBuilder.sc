DzMidiResponderBuilder {
	var < responders;
	var < device;

	*new {
		arg device;
		^ super.new().initDzMidiResponderBuilder(device);
	}

	initDzMidiResponderBuilder {
		arg a_device;

		// @TODO make sure it's a real device and it's connected.
		device = a_device;

		responders = List[];
	}

	noteOn  {
		arg func, noteNum, chan, sourceSelect;
		var responder = MIDIFunc.noteOn(func, noteNum, chan, srcID: sourceSelect);
		responders.add(responder);
		^ responder;
	}

	noteOff  {
		arg func, noteNum, chan, sourceSelect;
		var responder = MIDIFunc.noteOff(func, noteNum, chan, srcID: sourceSelect);
		responders.add(responder);
		^ responder;
	}

	polytouch  {
		arg func, noteNum, chan, sourceSelect;
		var responder = MIDIFunc.polytouch(func, noteNum, chan, srcID: sourceSelect);
		responders.add(responder);
		^ responder;
	}


	/**
		I HATE the way that bend is scaled in MIDI
		The math is always slightly off.
		This makes it a simple and accurate -1..0..1 (and also allows for some wonkier wheels that have sloppy centers).
	**/
	bend  {
		arg func, chan, sourceSelect, center = 8192, min = 0, max = 16383, centerRange = 0;
		var responder;
		var innerFunc = {
			arg amount, channel;
			var bend;
			if (amount > (center + centerRange)) {
				bend = amount.linlin(center + centerRange, max, 0.0, 1.0);
			} {
				bend = amount.linlin(min, center - centerRange, -1.0, 0.0);
			};
			func.value(bend, channel);
		};
		responder = MIDIFunc.bend(innerFunc, chan, srcID: sourceSelect);
		responders.add(responder);
		^ responder;
	}

	cc  {
		arg func, ccNum, chan, sourceSelect;
		var responder = MIDIFunc.cc(func, ccNum, chan, srcID: sourceSelect);
		responders.add(responder);
		^ responder;
	}

	/**
		Responds to a group of buttons mapped to NoteOn midi controls.
		Instead of giving a weird array of notes and vels, gives a more meaningful set of arguments.
	**/
	pr_buttonSetNote {
		arg method = \noteOn, func, noteRange, chan, buttonDefs, sourceSelect;
		var innerFunc = {
			arg vel, noteNum;
			func.value(buttonDefs[noteNum], noteNum, vel);
		};
		^ this.perform(method, innerFunc, noteRange, chan, sourceSelect: sourceSelect);
	}

	buttonSetOn {
		arg func, noteRange, chan, buttonDefs, sourceSelect;
		^ this.pr_buttonSetNote(\noteOn, func, noteRange, chan, buttonDefs, sourceSelect);
	}

	buttonSetOff {
		arg func, noteRange, chan, buttonDefs, sourceSelect;
		^ this.pr_buttonSetNote(\noteOff, func, noteRange, chan, buttonDefs, sourceSelect);
	}

	ccSet {
		arg func, ccRange, ccDefs, chan, sourceSelect;
		var innerFunc = {
			arg value, ccNum, channel;
			var cc = ccDefs[ccNum].copy;
			cc.unlock.put(\value, value).put(\on, value != 0).lock;
			func.value(cc, value, ccNum, channel);
		};
		^ this.cc(innerFunc, ccRange, chan, sourceSelect);
	}

	ccArray {
		arg func, ccNums, chan, srcId, nameFormat = "Control %";
		var ccDefs = IdentityDictionary[];
		ccNums.do {
			arg ccNum, i;
			ccDefs[ccNum] = SymbolDictionary[
				\ccNum -> ccNum,
				\index -> i,
				\name -> nameFormat.format(i + 1),
			];
		};
		^ this.ccSet(func, ccNums, ccDefs, chan, srcId);
	}

	ccButtonArray {
		arg onFunc, offFunc, firstCc = 28, length = 8, chan, srcId, nameFormat = "Button %";
		var func = {
			arg ccDef, value, ccNum, channel;
			if (value == 0) {
				offFunc.value(ccDef, value, ccNum, channel);
			} {
				onFunc.value(ccDef, value, ccNum, channel);
			};
		};
		^ this.ccArray(func, firstCc, length, chan, srcId, nameFormat);

	}
}
