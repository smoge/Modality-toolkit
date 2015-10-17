/*

General QuNexus info:
The QuNexus has 3 input ports, which require three separate desc files.
Its general info in is "keith-mcmillen-qunexus.parentDesc.scd".

This file is for Port 2 = portIndex 1.
This port is for the MIDI Expander in/output,
so one can connect any external MIDI hardware device;
the elements would then represent this connected device.

*/

(
///////// GENERIC INFORMATION COPIED TO ALL QUNEXUS DESC FILES: ///////
parentDesc: "keith-mcmillen-qunexus",

///////// SPECIFIC INFORMATION for this port and config: ///////

elementTypes: [],
deviceType: \externalMidiPort,

status: (
	linux: "unknown",
	osx: "unknown",
	win: "unknown"),

idInfo: (deviceName: "QuNexus", sourcePortIndex: 1, destPortIndex: 1),
portName: "Port 2",

// port is for connecting a MIDI device by midi plugs,
// so you can put the info of your device in copies of this file,
// or plug it in from the desc file of a MIDI-only device.
description: (

)

);