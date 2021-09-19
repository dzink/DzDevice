DzMidiAbstractDevice {
	var < connected = false;
	var <> verbose = false;
	var < midiResponders;
	var cache;

	classvar < deviceName = "";
	classvar < hasInput = false;
	classvar < hasOutput = false;

	*new {
		^ super.new.initializeAbstractMidiDevice();
	}

	*connect {
		^ this.new.connect();
	}

	deviceName {
		^ this.class.deviceName();
	}

	hasInput {
		^ this.class.hasInput();
	}

	hasOutput {
		^ this.class.hasOutput();
	}

	initializeAbstractMidiDevice {
		midiResponders = List[];
		cache = DataCache[];
	}

	ensureMidiClientIsRunning {
		if (MIDIClient.initialized.not) {
			MIDIClient.init(verbose: verbose);
		};
		^ this;
	}

	deviceExists {
		this.ensureMidiClientIsRunning();
		if (this.pr_sourcesByDevice.size > 0) {
			^ true;
		};
		if (this.pr_destinationsByDevice.size > 0) {
			^ true;
		};
		^ false;
	}

	connect {
		arg inPort = 0, outPort = 0;
		if (this.hasInput) {
			this.connectIn(inPort);
		};
		if (this.hasOutput) {
			this.connectOut(outPort);
		};
		connected = true;
		^ this;
	}

	connectIn {
		arg port = 0;
		this.ensureMidiClientIsRunning();
		this.sources.do {
			arg source;
			[\ss, source].postln;
			MIDIIn.connect(port, source.asMIDIInPortUID);
		};
		^ this;
	}

	connectOut {
		arg port = 0;
		this.ensureMidiClientIsRunning();
		this.sources.do {
			arg source;
			MIDIIn.connect(port, source.asMIDIInPortUID);
		};
		^ this;
	}

	sources {
		^ cache.at(\sources, {
			this.pr_sourcesByDevice
		});
	}

	destinations {
		^ cache.at(\destinations, {
			this.pr_destinationsByDevice
		});
	}

	findSource {
		arg name;
		^ this.pr_findEndpointByName(this.pr_sourcesByDevice, name);
	}

	findDestination {
		arg name;
		^ this.pr_findEndpointByName(this.pr_destinationsByDevice, name);
	}

	pr_endpointsByDevice {
		arg endpoints;
		var name = this.deviceName();
		^ endpoints.select {
			arg endpoint;
			endpoint.device == name;
		};
	}

	pr_findEndpointByName {
		arg endpoints, name;
		endpoints.do {
			arg endpoint;
			if (endpoint.name == name) {
				^ endpoint;
			};
		};
		Exception("% could not find an endpoint with the name %".format(this.class, name)).throw();
	}

	pr_sourcesByDevice {
		^ this.pr_endpointsByDevice(MIDIClient.sources);
	}

	pr_destinationsByDevice {
		^ this.pr_endpointsByDevice(MIDIClient.destinations);
	}

	sourceIds {
		^ cache.at(\sourceIds, {
			this.sources.collect {
				arg device;
				device.uid;
			}
		});
	}

	srcId {
		arg sourceSelect = 0;
		if (sourceSelect.isKindOf(Symbol)) {
			sourceSelect = this.sourceNames.indexOf(sourceSelect);
		};
		^ this.sourceIds.at(sourceSelect);
	}

	// MIDIFunc shortcuts

	noteOn {
		arg func, noteNum, chan, sourceSelect = 0;
		var responder = MIDIFunc.noteOn(func, noteNum, chan, srcID: this.srcId(sourceSelect));
		midiResponders.add(responder);
		^ responder;
	}

	noteOff {
		arg func, noteNum, chan, sourceSelect = 0;
		var responder = MIDIFunc.noteOff(func, noteNum, chan, srcID: this.srcId(sourceSelect));
		midiResponders.add(responder);
		^ responder;
	}

	/**
	 * I HATE the way that bend is scaled in MIDI
	 * The math is always slightly off.
	 * This makes it a simple and accurate -1..0..1 (and also allows for some wonkier wheels that have sloppy centers).
	 */
	bend {
		arg func, chan, sourceSelect = 0, center = 8192, min = 0, max = 16383, centerRange = 0;
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
		responder = MIDIFunc.bend(innerFunc, chan, srcId: this.srcId(sourceSelect));
		midiResponders.add(responder);
		^ responder;
	}

	cc {
		arg func, ccNum, chan, sourceSelect = 0;
		var responder = MIDIFunc.cc(func, ccNum, chan, srcID: this.srcId(sourceSelect));
		midiResponders.add(responder);
		^ responder;
	}

	/**
	 * Responds to a group of buttons mapped to NoteOn midi controls.
	 * Instead of giving a weird array of notes and vels, gives a more meaningful set of arguments.
	 */
	pr_buttonSetNote {
		arg method = \noteOn, func, noteRange, channel, buttonDefs, sourceSelect = 0;
		var innerFunc = {
			arg vel, noteNum;
			func.value(buttonDefs[noteNum]);
		};
		^ this.perform(method, innerFunc, noteRange, sourceSelect: sourceSelect);
	}

	pr_buttonSetOn {
		arg func, noteRange, channel, buttonDefs, sourceSelect = 0;
		^ this.pr_buttonSetNote(\noteOn, func, noteRange, channel, buttonDefs, sourceSelect);
	}

	pr_buttonSetOff {
		arg func, noteRange, channel, buttonDefs, sourceSelect = 0;
		^ this.pr_buttonSetNote(\noteOff, func, noteRange, channel, buttonDefs, sourceSelect);
	}

}
