MAbstractElement {
	
	var <source; // the Ktl it belongs to
	var <name; // its name in Ktl.elements
	var <type; // its type. 

	var <funcChain;
	
	// keep value and previous value here
	var <value;
	var <prevValue;
	
	// server support, currently only one server per element supported.
	var <bus;

	*new { |source, name|
		^super.newCopyArgs( source, name).init;
	}

	init { 
		this.reset;
	}

		// remove all functionalities from the funcChains
	reset {
		funcChain = FuncChain.new;
	}

	// funcChain interface //
	
	// by default, just do add = addLast, no flag needed. 
	// (indirection with perform is much slower than method calls.)
	addFunc { |funcName, function, addAction, otherName|
		// by default adds the action to the end of the list
		// if otherName is set, the valid addActions are: 
		// \addLast, \addFirst, \addBefore, \addAfter, \replaceAt, are valid
		funcChain.add(funcName, function, addAction, otherName);
	}

	addFuncFirst { |funcName, function|
		funcChain.addFirst(funcName, function);
	}

	addFuncLast { |funcName, function|
		funcChain.addLast(funcName, function);
	}

	addFuncAfter { |funcName, function, otherName|
		funcChain.addAfter(funcName, function, otherName);
	}
	
	addFuncBefore { |funcName, function, otherName|
		funcChain.addBefore(funcName, function, otherName);
	}
	
	replaceFunc { |funcName, function, otherName| 
		funcChain.replaceAt(funcName, function, otherName);
	}
	
	removeFunc {|funcName| 
		funcChain.removeAt(funcName) 
	}
	
	send { |val|
		value = val;
		source.send(name,val)
	}

	value_ { | newval |
		// copies the current state to:
		prevValue = value;
		// updates the state with the latest value
		value = newval;
		this.updateValueOnServer;
	}
	
	valueAction_ { |newval|
		this.value_( newval );
		this.doAction;
	}
	
	rawValue_{|newval|
		this.value_(newval);
	}
	
	rawValueAction_{|newval|
		this.rawValue_( newval );
		this.doAction;
	}
	
	doAction {
		source.recordRawValue( name, value );
		funcChain.value( this );
	}

	// UGen support
	updateValueOnServer {
		//this.initBus;
		// set bus values
		bus !? {bus.setn(this.value.asArray)};
	}
	
	initBus {|server|
		server = server ?? {Server.default};
		server.serverRunning.not.if{^this};
		bus.isNil.if({
			bus = Bus.control(server, (value ? 1).asArray.size);
		});	
	}
	
	freeBus {
		bus.notNil.if({
			Bus.free;
		});	
	}
	
	kr {|server| 
		// server is an optional argument that you only have to set once 
		// and only if the server for your bus is not the defualt one. 
		this.initBus(server);
		^In.kr(bus.index, bus.numChannels)
	}
	
}

MKtlElement : MAbstractElement{
	classvar <types;

	var <deviceDescription;	 //its particular device description
	                         //of type: ( 'chan':Int, 'midiType':symbol, 'spec':ControlSpec,
	                         //           'ccNum': Int, 'specName':symbol, 'type':Symbol )
	                         // i.e.   ( 'chan':0, 'midiType':'cc', 'spec': ControlSpec,
	                         //          'ccNum': 24, 'specName':'midiCC', 'type':'midiBut' )
	var <spec; // ControlSpec -> its spec

	*initClass {
		types = (
			\slider: \x,
			\button: \x,
			\thumbStick: [\joyAxis, \joyAxis, \button],
			\joyStick: [\joyAxis, \joyAxis, \button]
		)
	}

	*new { |source, name|
		^super.newCopyArgs( source, name).init;
	}

	init { 
		super.init;
		deviceDescription = source.deviceDescriptionFor(name);
		spec = deviceDescription[\spec];
		if (spec.isNil) { 
			warn("spec for '%' is missing!".format(spec));
		} { 
			value = prevValue = spec.default ? 0;
		};
		type = deviceDescription[\type];

		spec = deviceDescription[\spec];
		if (spec.isNil) { 
			warn("spec for '%' is missing!".format(spec));
		} { 
			value = prevValue = spec.default ? 0;
		};
	}

	defaultValue {
		^spec.default;
	}

	value { ^spec.unmap(value) }

	// usually, you do not call this but rawValue_ instead.
	value_ {|newval|
		^super.value(spec.map(newval))
	}

	rawValue { ^value }

	rawValue_ {|newVal|
		super.value_(newVal)
	}
}



















