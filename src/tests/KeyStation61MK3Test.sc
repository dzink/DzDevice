KeyStation61MK3Test : DzMidiTest {
	var d;

	test_midi {
		var s = Server.default;
		d = KeyStation61MK3();
		if (d.deviceExists()) {
			s.boot;
			d.connect();
			// d.verbose = true;
			// d.connect();
			// // MIDIFunc.trace(true);
			//
			d.transportOn({
				arg button;
				[\transport, button, button.name].postln;
			});
			d.directionalPad({
				arg button;
				[\dpad, button, button.x].postln;
			});
			d.directionalPadOff({
				arg button;
				[\dpadOff, button, button.x].postln;
			});
			d.volume({
				arg ... args;
				[\volume, args].postln;
			});
			d.mod({
				arg ... args;
				[\mod, args].postln;
			});
			d.enter({
				arg ... args;
				[\enter, args].postln;
			});
			d.bend({
				arg ... args;
				[\bend, args].postln;
			});
			d.keyOn({
				arg ... args;
				args.postln;
			});
			s.sync;

			// [\sources, d.sources].postln;
			20.wait;
		}
	}
}
