package orbital.moon.awt.virtual;

import java.io.*;
import java.util.Vector;

/**
 * Structure representing vector animation data.
 */
public class VectorAnimationData implements Externalizable {
	public VectorGraphicsAnimationHeader  header;
	public Vector						  initial;
	public Vector						  commands;
	public VectorAnimationData() {
		header = new VectorGraphicsAnimationHeader();
		initial = new Vector();
		commands = new Vector();
	}
	
	public VectorGraphicsAnimationCommand[] getInitial() {
		return (VectorGraphicsAnimationCommand[]) initial.toArray(new VectorGraphicsAnimationCommand[0]);
	}
	public VectorGraphicsAnimationCommand[] getCommands() {
		return (VectorGraphicsAnimationCommand[]) commands.toArray(new VectorGraphicsAnimationCommand[0]);
	}

	public void readExternal(ObjectInput is) throws IOException {
		header.readExternal(is);
		initial = new Vector();
		VectorGraphicsAnimationCommand command;
		do {
			initial.add(command = readCommand(is));
		} while (command.cmd != 0xF);
		commands = new Vector();
		while (((DataInputStream) is).available() > 0)
			commands.add(readCommand(is));
	} 

	public void writeExternal(ObjectOutput os) throws IOException {
		header.writeExternal(os);
		Externalizable[] c = getInitial();
		for (int i = 0; i < c.length; i++)
			c[i].writeExternal(os);
		c = getCommands();
		for (int i = 0; i < c.length; i++)
			c[i].writeExternal(os);
	} 
	
	protected VectorGraphicsAnimationCommand readCommand(ObjectInput is) throws IOException {
		VectorGraphicsAnimationCommand command = new VectorGraphicsAnimationCommand();
		command.readExternal(is);
		return command;
	} 
}