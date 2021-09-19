KeyStation61MK3Test : DzMidiTest {
	var d;

	test_midi {
		d = KeyStation61MK3.connect();

		// d.verbose = true;
		// d.connect();
		// // MIDIFunc.trace(true);
		//
		d.transportOn({
			arg button;
			[\transport, button].postln;
		});
		d.directionalPadOn({
			arg button;
			[\dpad, button].postln;
		});
		d.volume({
			arg ... args;
			[\volume, args].postln;
		});
		d.mod({
			arg ... args;
			[\mod, args].postln;
		});
		d.bend({
			arg ... args;
			[\bend, args].postln;
		});
		// [\sources, d.sources].postln;
		20.wait;
	}
}
