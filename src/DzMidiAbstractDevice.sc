DzMidiAbstractDevice {
	var < connected = false;
	var <> verbose = false;
	var < responderBuilder;
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
}
