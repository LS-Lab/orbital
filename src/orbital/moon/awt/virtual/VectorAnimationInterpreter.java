/*
 * @(#)VectorAnimationInterpreter.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt.virtual;


import java.io.*;
import java.net.URL;
import orbital.math.*;

import orbital.moon.io.ObjectDataInputStream;

/**
 * interpretes a *.ani Animation file.
 */
public
class VectorAnimationInterpreter {
	protected ObjectDataInputStream	   animationInstructions;
	protected URL				   vectorAnimation;
	private VectorGraphicsAnimationHeader header;
	public VectorAnimationInterpreter(URL vectorAnimation) {
		this.vectorAnimation = vectorAnimation;
	}
	
	public boolean ready() throws IOException {
		return animationInstructions.available() > 0;
	}

	public void start() {
		try {
			InputStream ifs = new BufferedInputStream(vectorAnimation.openStream());
			animationInstructions = new ObjectDataInputStream(ifs);
			header = new VectorGraphicsAnimationHeader();
			header.readExternal(animationInstructions);
		} catch (IOException x) {
			x.printStackTrace();
		} 
	} 

	public void skipInitialMove() throws IOException {
		VectorGraphicsAnimationCommand command = new VectorGraphicsAnimationCommand();
		do {
			command.readExternal(animationInstructions);
		} while (command.cmd != 0xF);
	} 

	public int interpretCommand(Matrix3D move) throws IOException {
		VectorGraphicsAnimationCommand command = readCommand();
		switch (command.cmd) {
			case 1:
				move.translate(command.x_data, command.y_data, command.z_data);
				break;
			case 2:
				move.scale(command.x_data, command.y_data, command.z_data);
				break;
			case 3:
				move.rotate(command.x_data, command.y_data, command.z_data);
				break;
			case 4:
				return command.i_data;	   // to be stepped up to zero
			case 5:
				return -command.i_data;	   // to be set as delaytime
			case 0xF:
				return 1;	//XXX: was 0, is 1 better?
			default:
				throw new IOException("wrong file format for 3DAnimation in *.ani file. unknown command " + command.cmd);
		}
		return 0;
	} 

	protected VectorGraphicsAnimationCommand readCommand() throws IOException {
		VectorGraphicsAnimationCommand command = new VectorGraphicsAnimationCommand();
		command.readExternal(animationInstructions);
		return command;
	} 
}