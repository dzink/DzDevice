AkaiMpd232Test : DzMidiTest {

	test_midi {
		var d = AkaiMpd232();
		d.deviceName.postln();
		d.connect;
		// MIDIFunc.trace(true);
		d.padsOn({
			arg ... args;
			[\padon, args].postln;
		});

		d.padsOff({
			arg ... args;
			[\padoff, args].postln;
		});

		d.polytouch({
			arg ... args;
			[\padpoly, args].postln;
		});

		d.sources.do {
			arg source;
			[source, source.uid].postln;
		};
		d.play({
			\play.postln;
		});
		d.stop({
			\stop.postln;
		});
		d.record {
			\record.postln;
		};

		d.sliders({
			arg ... args;
			args.postln;
		}, 20);
		d.knobs({
			arg ... args;
			args.postln;
		}, 12);

		d.buttons({
			arg d;
			[\bon, d].postln;
		}, {
			arg d;
			[\boff, d].postln;
		});
		2000.wait;

		// d.sources.postln;
	}

}
