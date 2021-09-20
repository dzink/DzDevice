KeyStation61MK3Test : DzDeviceTest {
	var d;

	test_midi {
		var s = Server.default;
		d = KeyStation61MK3.allocate();
		if (d.deviceExists()) {
			d.connect();

			d.play({
				\play.postln;
			});
			d.stop({
				\stop.postln;
			});
			d.record {
				\record.postln;
			};
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
			
			20.wait;
		}
	}
}
