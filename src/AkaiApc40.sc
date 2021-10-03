AkaiApc40 : DzMidiDevice {
	var < midiOut;
	var < currentCueLevel = 64;
	var < modifiers;

	classvar < deviceName = "Akai APC40";
	classvar < hasInput = true;
	classvar < hasOutput = true;
	classvar < midiInName = "Akai APC40 MIDI 1";
	classvar < midiOutName = "Akai APC40 MIDI 1";
	classvar < sysexIntroduction = "";

	classvar < genericMode = 0x40;
	classvar < abletonMode1 = 0x41;
	classvar < abletonMode2 = 0x42;

	classvar < colorOff = 0;
	classvar < colorOn = 1;
	classvar < colorFlash = 2;
	classvar < colorGreen = 1;
	classvar < colorGreenFlash = 2;
	classvar < colorRed = 3;
	classvar < colorRedFlash = 4;
	classvar < colorAmber = 5;
	classvar < colorAmberFlash = 6;

	classvar < ringStyleOff = 0;
	classvar < ringStyleSingle = 1;
	classvar < ringStyleVolume = 2;
	classvar < ringStylePan = 3;

	*new {
		^ super.new.initAkaiApc40();
	}

	initAkaiApc40 {
		modifiers = IdentityDictionary();
		this.setAbletonMode2();
		this.pr_addSingleButtonMethods();
	}

	pr_buttonNotes {
		^ cache.at(\buttonNotes, {
			IdentityDictionary[
				\pan -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 87,
					\hasLight -> true,
				],
				\sendA -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 88,
					\hasLight -> true,
				],
				\sendB -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 89,
					\hasLight -> true,
				],
				\sendC -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 90,
					\hasLight -> true,
				],
				\shift -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 98,
				],
				\tapTempo -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 99,
				],
				\nudgeUp -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 100,
				],
				\nudgeDown -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 101,
				],
				\clipTrack -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 58,
					\hasLight -> true,
				],
				\deviceActivate -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 59,
					\hasLight -> true,
				],
				\backButton -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 60,
					\hasLight -> true,
				],
				\forwardButton -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 61,
					\hasLight -> true,
				],
				\detailView -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 62,
					\hasLight -> true,
				],
				\recQuantization -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 63,
					\hasLight -> true,
				],
				\midiOverdub -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 64,
					\hasLight -> true,
				],
				\metronome -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 65,
					\hasLight -> true,
				],
				\play -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 91,
				],
				\record -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 93,
				],
				\stop -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 92,
				],
				\master -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 80,
					\hasLight -> true,
				],
				\stopAllClips -> SymbolDictionary[
					\channel -> 0,
					\noteNum -> 81,
				],
			];
		});
	}

	pr_addSingleButtonMethods {
		this.pr_buttonNotes.keysValuesDo {
			arg buttonId, buttonNote;
			this.addUniqueMethod((buttonId ++ 'On').asSymbol, {
				arg o, func;
				responderBuilder.noteOn(func, buttonNote.noteNum, buttonNote.channel, this.srcId(0));
			});
			this.addUniqueMethod((buttonId ++ 'Off').asSymbol, {
				arg o, func;
				responderBuilder.noteOff(func, buttonNote.noteNum, buttonNote.channel, this.srcId(0));
			});
			if (buttonNote[\hasLight].notNil()) {

				this.addUniqueMethod((buttonId ++ 'Light').asSymbol, {
					arg o, color;
					midiOut.noteOn(buttonNote.channel, buttonNote.noteNum, color);
				});
			};
		};
	}

	pr_respondToSingleButtons {
		arg func, buttonId, method = \noteOn;
		var buttonNotes = this.pr_buttonNotes();
		var buttonNote = buttonNotes[buttonId];
		^ responderBuilder.perform(method, func, buttonNote.noteNum, buttonNote.channel, this.srcId(0));
	}

	sources {
		^ cache.at(\sources, {
			[
				this.findSourceEndpoint(midiInName),
			]
		});
	}

	destinations {
		^ cache.at(\destinations, {
			[
				MIDIOut.newByName("Akai APC40", "Akai APC40 MIDI 1"),
			]
		});
	}

	connectOut {
		super.connectOut();
		midiOut = outs[0];
	}

	setGenericMode {
		^ this.pr_sysexIntroduction(genericMode);
	}

	setAbletonMode1 {
		^ this.pr_sysexIntroduction(abletonMode1);
	}

	setAbletonMode2 {
		^ this.pr_sysexIntroduction(abletonMode2);
	}

	pr_sysexIntroduction {
		arg modeKey = 0x40;
		var bundle = List[0xf0, 0x47, 0x00, 0x73, 0x60, 0x00, 0x04] ++ List[modeKey] ++ List[0x08, 0x02, 0x01, 0xf7];
		// bundle.insert(7, modeKey);
		bundle = Int8Array.newFrom(bundle);
		Int8Array[0xf0, 0x00, 0x20, 0x3c, 0x02, 0x00, 0x61, 21, 54, 0xf7].postln;
		[\bun, bundle].postln;
		outs.postln;
		midiOut.postln.sysex(bundle);
		\success.postln;
	}

	desintations {
		^ cache.at(\desintations, {
			[
				this.findDestinationEndpoint(midiOutName),
			]
		});
	}

	pr_buttonSet {
		arg func, buttonDefMethod, responderBuilderMethod, notes, channels, srcId;
		var innerFunc;
		innerFunc = {
			arg velocity, note, channel, srcId;
			var buttonDef = this.perform(buttonDefMethod, velocity, note, channel, srcId);
			func.value(buttonDef, note, velocity, channel, srcId);
		};
		^ responderBuilder.perform(responderBuilderMethod, innerFunc, notes, channels, srcId);
	}

	clipLaunchOn {
		arg func;
		^ this.pr_buttonSet(func, \pr_clipLaunchButtonDef, \noteOn, (53..57), (0..7), this.srcId(0));
	}

	clipLaunchOff {
		arg func;
		^ this.pr_buttonSet(func, \pr_clipLaunchButtonDef, \noteOff, (53..57), (0..7), this.srcId(0));
	}

	sceneLaunchOn {
		arg func;
		var innerFunc = {
			arg velocity, note, channel;
			var buttonDef = this.pr_sceneLaunchButtonDef(velocity, note, channel);
			func.value(buttonDef, note, velocity, channel);
		};
		^ responderBuilder.noteOn(innerFunc, (0x52..0x56), 0, this.srcId(0));
	}

	sceneLaunchOff {
		arg func;
		var innerFunc = {
			arg velocity, note, channel;
			var buttonDef = this.pr_sceneLaunchButtonDef(velocity, note, channel);
			func.value(buttonDef, note, velocity, channel);
		};
		^ responderBuilder.noteOff(innerFunc, (0x52..0x56), 0, this.srcId(0));
	}

	clipStopOn {
		arg func, channels, notes;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOn, 52, (0..7), this.srcId(0));
	}

	clipStopOff {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOff, 52, (0..7), this.srcId(0));
	}

	trackSelectOn {
		arg func, channels, notes;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOn, 51, (0..7), this.srcId(0));
	}

	trackSelectOff {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOff, 51, (0..7), this.srcId(0));
	}

	activatorOn {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOn, 50, (0..7), this.srcId(0));
	}

	activatorOff {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOff, 50, (0..7), this.srcId(0));
	}

	soloCueOn {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOn, 49, (0..7), this.srcId(0));
	}

	soloCueOff {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOff, 49, (0..7), this.srcId(0));
	}

	recordArmOn {
		arg func, channels, notes;
		notes = notes ? 48;
		channels = channels ? (0..7);
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOn, 48, (0..7), this.srcId(0));
	}

	recordArmOff {
		arg func, channels, notes;
		notes = notes ? 48;
		channels = channels ? (0..7);
		^ this.pr_buttonSet(func, \pr_trackBasedButtonDef, \noteOff, 48, (0..7), this.srcId(0));
	}

	trackVolume {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackVolumeSliderDef, \cc, 7, (0..7), this.srcId(0));
	}

	masterVolume {
		arg func;
		var innerFunc = {
			arg value, ccNum, chan;
			var controlDef = this.pr_masterVolumeDef(value, ccNum, chan);
			func.value(controlDef, value, ccNum, chan);
		};
		^ this.responderBuilder.cc(innerFunc, 14, 0, this.srcId(0));
	}

	crossFade {
		arg func;
		var innerFunc = {
			arg value, ccNum, chan;
			var controlDef = this.pr_crossFadeDef(value, ccNum, chan);
			func.value(controlDef, value, ccNum, chan);
		};
		^ this.responderBuilder.cc(innerFunc, 15, 0, this.srcId(0));
	}

	trackControl {
		arg func;
		^ this.pr_buttonSet(func, \pr_trackControlDef, \cc, (48..55), 0, this.srcId(0));
	}

	deviceControl {
		arg func;
		^ this.pr_buttonSet(func, \pr_deviceControlDef, \cc, (16..23), 0, this.srcId(0));
	}

	directionalPadOn {
		arg func;
		var innerFunc = {
			arg velocity, note, chan;
			func.value(this.pr_directionalPadDef(velocity, note, chan), velocity, note, chan);
		};
		^ this.responderBuilder.noteOn(innerFunc, (94..97), 0, this.srcId(0));
	}

	directionalPadOff {
		arg func;
		var innerFunc = {
			arg velocity, note, chan;
			func.value(this.pr_directionalPadDef(velocity, note, chan), velocity, note, chan);
		};
		^ this.responderBuilder.noteOff(innerFunc, (94..97), 0, this.srcId(0));
	}

	cueLevel {
		arg func;
		var innerFunc = {
			arg value, ccNum, channel, srcId;
			var controlDef;
			var difference = if (value > 63) {
				value - 128;
			} {
				value
			};
			controlDef = this.pr_cueLevelDef(value, difference, ccNum, channel, srcId);
			func.value(controlDef, value, ccNum, channel, srcId);
		};
		this.responderBuilder.cc(innerFunc, 47, 0, this.srcId(0));
	}

	trackCueLevel {
		^ this.cueLevel {
			arg controlDef;
			currentCueLevel = (currentCueLevel + controlDef[\difference]).clip(0, 127);
		};
	}

	// BUTTON/SLIDER DEFS

	pr_directionalPadDef {
		arg velocity, note, channel, srcId;
		var directionalPadDefinitions = cache.at(\directionalPadDefinitions, {
			IdentityDictionary[
				94 -> SymbolDictionary[
					\x -> 0,
					\y -> -1,
					\name -> \up,
				],
				95 -> SymbolDictionary[
					\x -> 0,
					\y -> 1,
					\name -> \down,
				],
				97 -> SymbolDictionary[
					\x -> -1,
					\y -> 0,
					\name -> \left,
				],
				96 -> SymbolDictionary[
					\x -> 1,
					\y -> 0,
					\name -> \right,
				],
			]
		});
		var def = directionalPadDefinitions.at(note);
		def = def ++ SymbolDictionary[
			\velocity -> velocity,
			\note -> note,
			\channel -> channel,
			\srcId -> srcId,
		];
		def.lock;
		^ def;
	}

	pr_clipLaunchButtonDef {
		arg velocity, note, channel, srcId;
		var y = note - 53;
		^ SymbolDictionary[
			\note -> note,
			\velocity -> currentCueLevel,
			\channel -> channel,
			\srcId -> srcId,
			\track -> channel,
			\x -> channel,
			\y -> y,
			\clipId -> (channel * 5 + y),
		].lock;
	}

	pr_sceneLaunchButtonDef {
		arg velocity, note, channel, srcId;
		var y = note - 0x52;
		^ SymbolDictionary[
			\note -> note,
			\velocity -> velocity,
			\channel -> channel,
			\srcId -> srcId,
			\scene -> y,
			\y -> y,
		].lock;
	}

	pr_trackBasedButtonDef {
		arg velocity, note, channel, srcId;
		^ SymbolDictionary[
			\note -> note,
			\velocity -> velocity,
			\channel -> channel,
			\srcId -> srcId,
			\track -> channel,
			\x -> channel,
		].lock;
	}

	pr_trackVolumeSliderDef {
		arg velocity, ccNum, channel, srcId;
		^ SymbolDictionary[
			\ccNum -> ccNum,
			\value -> velocity,
			\volume -> velocity,
			\channel -> channel,
			\srcId -> srcId,
			\track -> channel,
			\x -> channel,
		].lock;
	}

	pr_trackControlDef {
		arg velocity, ccNum, channel, srcId;
		^ SymbolDictionary[
			\knob -> (ccNum - 48),
			\value -> velocity,
			\channel -> channel,
			\srcId -> srcId,
			\track -> channel,
		].lock;
	}

	pr_deviceControlDef {
		arg velocity, ccNum, channel, srcId;
		^ SymbolDictionary[
			\knob -> (ccNum - 16),
			\value -> velocity,
			\channel -> channel,
			\srcId -> srcId,
			\track -> channel,
		].lock;
	}


	pr_crossFadeDef {
		arg value, ccNum, channel, srcId;
		^ SymbolDictionary[
			\value -> value,
			\fade -> value.linlin(0, 127, -1, 1),
			\ccNum -> ccNum,
			\channel -> channel,
			\srcId -> srcId,
		].lock
	}

	pr_masterVolumeDef {
		arg value, ccNum, channel, srcId;
		^ SymbolDictionary[
			\value -> value,
			\volume -> value,
			\db -> (value / 127).ampdb,
			\ccNum -> ccNum,
			\channel -> channel,
			\srcId -> srcId,
		].lock
	}

	pr_cueLevelDef {
		arg value, difference, ccNum, channel, srcId;
		^ SymbolDictionary[
			\value -> value,
			\difference -> difference,
			\cueLevel -> currentCueLevel,
			\ccNum -> ccNum,
			\channel -> channel,
			\srcId -> srcId,
		].lock
	}

	// LIGHT CONTROL

	clipLaunchLightBundle {
		arg bundle, xOffset = 0, yOffset = 0;
		bundle.do {
			arg row, y;
			y = y + yOffset;
			row.do {
				arg color, x;
				x = x + xOffset;
				this.clipLaunchLight(x, y, this.pr_getColorFromSymbol(color));
			};
		};
	}

	clipLaunchLightById {
		arg id, color = 1;
		var x = id % 8;
		var y = (id / 5).floor;
		^ this.clipLaunchLight(x, y, this.pr_getColorFromSymbol(color));
	}

	clipLaunchLight {
		arg x, y, color = 1;
		midiOut.noteOn(x, y + 53, this.pr_getColorFromSymbol(color));
	}

	sceneLaunchLight {
		arg y, color;
		midiOut.noteOn(0, y + 0x52, this.pr_getColorFromSymbol(color));
	}

	activatorLight {
		arg x, color = 1;
		midiOut.noteOn(x, 50, this.pr_getColorFromSymbol(color));
	}

	trackSelectLight {
		arg x, color = 1;
		midiOut.noteOn(x, 51, this.pr_getColorFromSymbol(color));
	}

	clipStopLight {
		arg x, color = 1;
		midiOut.noteOn(x, 52, this.pr_getColorFromSymbol(color));
	}

	soloCueLight {
		arg x, color = 1;
		midiOut.noteOn(x, 49, this.pr_getColorFromSymbol(color));
	}

	recordArmLight {
		arg x, color = 1;
		midiOut.noteOn(x, 48, this.pr_getColorFromSymbol(color));
	}


	deviceControlLight {
		arg track = 0, value = 127;
		midiOut.control(0, 0x10 + track, value);
	}

	deviceControlRingType {
		arg track = 0, type = 1;
		type = this.pr_getRingTypeFromSymbol(type);
		midiOut.control(0, 0x18 + track, type);
	}

	trackControlLight {
		arg track = 0, value = 127;
		midiOut.control(0, 0x30 + track, value);
	}

	trackControlRingType {
		arg track = 0, type = 1;
		type = this.pr_getRingTypeFromSymbol(type);
		midiOut.control(0, 0x38 + track, type);
	}

	pr_getRingTypeFromSymbol {
		arg type;
		if (type.isKindOf(Symbol)) {
			type = IdentityDictionary[
				\off -> ringStyleOff,
				\single -> ringStyleSingle,
				\volume -> ringStyleVolume,
				\pan -> ringStylePan,
			].at(type);
		};
		^ type;
	}

	pr_getColorFromSymbol {
		arg color;
		if (color.isKindOf(Symbol)) {
			color = IdentityDictionary[
				\off -> colorOff,
				\on -> colorOn,
				\flash -> colorFlash,
				\green -> colorGreen,
				\red -> colorRed,
				\amber -> colorAmber,
				\greenFlash -> colorGreenFlash,
				\redFlash -> colorRedFlash,
				\amberFlash -> colorAmberFlash,
			].at(color);
		};
		^ color;
	}

	getModifierCombo {
		arg ... modifierIds;
		modifierIds.do {
			arg modifierId;
			if (modifiers[modifierId] != true) {
				^ false;
			};
		}
		^ true;
	}

	setModifier {
		arg modifierId, state = true;
		modifiers[modifierId] = state;
		^ this;
	}

	initializeModifier {
		arg modifierId, initialState = false;
		this.setModifier(modifierId, initialState);
		this.perform((modifierId ++ 'On').asSymbol, {
			this.setModifier(modifierId, true);
		});
		this.perform((modifierId ++ 'Off').asSymbol, {
			this.setModifier(modifierId, false);
		});
		^ this;
	}

}
