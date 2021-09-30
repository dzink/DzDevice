AkaiMpc40Test : DzDeviceTest {

	test_midi {
		var out;
		var d = AkaiApc40();
		d.setAbletonMode2();
		d.pr_addSingleButtonMethods();
		// 10.wait;
		// 2.wait;
		// [\out1, d.outs[0].class].postln;
		// MIDIClient.destinations[1].uid.postln;
		//
		[\clipLaunchOn, \clipLaunchOff, \clipStopOn, \clipStopOff, \trackSelectOn, \trackSelectOff, \activatorOn, \activatorOff, \soloCueOn, \soloCueOff, \recordArmOn, \recordArmOff, \trackVolume, \trackControl, \deviceControl, \panOn, \panOff, \sendAOn, \sendAOff, \sendBOn, \sendBOff, \sendCOn, \sendCOff, \shiftOn, \shiftOff, \tapTempoOn, \tapTempoOff, \nudgeUpOn, \nudgeUpOff, \nudgeDownOn, \nudgeDownOff, \clipTrackOn, \clipTrackOff, \deviceActivateOn, \deviceActivateOff, \backButtonOn, \backButtonOff, \forwardButtonOn, \forwardButtonOff, \detailViewOn, \detailViewOff, \recQuantizationOn, \recQuantizationOff, \midiOverdubOn, \midiOverdubOff, \metronomeOn, \metronomeOff, \playOn, \playOff, \recordOn, \recordOff, \stopOn, \stopOff, \masterVolume, \crossFade, \directionalPadOn, \directionalPadOff, \masterOn, \masterOff, \stopAllClipsOn, \stopAllClipsOff, \sceneLaunchOn, \sceneLaunchOff, \cueLevel].do {
			arg method;
			d.perform(method, {
				arg ... args;
				[method, args].postcs;
			});
		};
		// MIDIFunc.trace(true);
		d.sendCLight(1);
		d.trackControlLight(2, 127);
		d.trackControlRingType(4, \pan);
		d.deviceControlLight(2, 127);
		d.deviceControlRingType(4, \pan);
		d.masterLight(\flash);
		d.sceneLaunchLight(2, \flash);

		\clipt.postln;

		// (0..127).do {
		// 	arg i;
		// 	d.midiOut.noteOn(0, i, 0);
		// };
		// {
		// 	[0, 1, 2, 3].do {
		// 		arg color;
		// 		0.5.wait;
		// 		(0..7).do {
		// 			arg x;
		// 			(0..4).do {
		// 				arg y;
		// 				d.clipLaunchLight(x, y, color);
		// 			};
		// 			d.activatorLight(x, color);
		// 			d.soloCueLight(x, color);
		// 			d.recordArmLight(x, color);
		// 			d.trackSelectLight(x, color);
		// 			d.clipStopLight(x, color);
		// 		};
		// 	};
		// }.loop;
		d.clipLaunchLightBundle(
			[[1, 0, 1],
			[3, 1, 3],
			[5, 3, 5]], 2, 1
			);
		1000.wait;
	}



		// d.sources.postln;

}
