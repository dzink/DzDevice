AkaiMpd232Test : DzMidiTest {

	test_midi {
		var out;
		var d = AkaiMpd232();
		if (d.deviceExists) {

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
		};
		// out = MIDIOut.connect(0, d.destinationId(0).postln);
		2000.wait;
	}



		// d.sources.postln;

}
