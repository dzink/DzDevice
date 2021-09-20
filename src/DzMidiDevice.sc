DzMidiDevice {
	var < connected = false;
	var <> verbose = false;
	var < responderBuilder;
	var < midiResponders;
	var cache;
	var < outs;

	classvar < deviceName = "";
	classvar < hasInput = false;
	classvar < hasOutput = false;

	*new {
		^ this.allocate.connect();
	}

	/**
	Initialize but do not connect yet.
	**/
	*allocate {
		^ super.new.initializeAbstractMidiDevice();
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
		cache = DataCache[];
		outs = List[];
		responderBuilder = DzMidiResponderBuilder(this);
	}

	ensureMidiClientIsRunning {
		if (MIDIClient.initialized.not) {
			MIDIClient.init(verbose: verbose);
		};
		^ this;
	}

	/**
	Reports whether the device exists.
	**/
	deviceExists {
		try {
			this.ensureMidiClientIsRunning();
			if (this.pr_sourcesForDevice.size > 0) {
				^ true;
			};
			if (this.pr_destinationsForDevice.size > 0) {
				^ true;
			};
		} {
			^ false;
		};
		^ false;
	}

	/**
	Connect this device to the CPU's MIDI system.
	**/
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
			MIDIIn.connect(port, source.asMIDIInPortUID);
		};
		^ this;
	}

	connectOut {
		arg port = 0;
		this.ensureMidiClientIsRunning();
		this.destinations.do {
			arg destinations;
			MIDIOut.connect(port, destinations.asMIDIInPortUID);
		};
		^ this;
	}

	/**
	Retrieve endpoints for this device as a source.
	Each device SHOULD implement this in its own way.
	**/
	sources {
		^ cache.at(\sources, {
			this.pr_sourcesForDevice
		});
	}

	/**
	Retrieve endpoints for this device as a destination.
	Each device SHOULD implement this in its own way.
	**/
	destinations {
		^ cache.at(\destinations, {
			this.pr_destinationsForDevice
		});
	}

	findSourceEndpoint {
		arg name;
		^ this.pr_findEndpointByName(this.pr_sourcesForDevice.postln, name);
	}

	findDestinationEndpoint {
		arg name;
		^ this.pr_findEndpointByName(this.pr_destinationsForDevice, name);
	}

	pr_endpointsForDevice {
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

	pr_sourcesForDevice {
		^ this.pr_endpointsForDevice(MIDIClient.sources);
	}

	pr_destinationsForDevice {
		^ this.pr_endpointsForDevice(MIDIClient.destinations);
	}

	sourceIds {
		^ cache.at(\sourceIds, {
			this.sources.collect {
				arg device;
				device.uid;
			}
		});
	}

	destinationIds {
		^ cache.at(\destinationIds, {
			this.destinations.collect {
				arg device;
				device.uid;
			}
		});
	}

	srcId {
		arg sourceSelect;
		if (sourceSelect.isNil) {
			sourceSelect = 0;
		};
		if (sourceSelect.isKindOf(Symbol)) {
			sourceSelect = this.sourceNames.indexOf(sourceSelect);
		};
		^ this.sourceIds.at(sourceSelect);
	}

	destinationId {
		arg destinationId;
		if (destinationId.isNil) {
			destinationId = 0;
		};
		if (destinationId.isKindOf(Symbol)) {
			destinationId = this.destinationNames.indexOf(destinationId);
		};
		^ this.destinationIds.at(destinationId);
	}

	extendNumbersToArray {
		arg first = 0, length = 8;
		if (first.isKindOf(Number)) {
			first = first + (0..(length - 1));
		};
		^ first;
	}
}
