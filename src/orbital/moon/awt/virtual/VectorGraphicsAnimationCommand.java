package orbital.moon.awt.virtual;

import java.io.*;
import orbital.math.*;

public class VectorGraphicsAnimationCommand implements Externalizable {
	public byte   cmd;		  // command#:
	// 1 - objekt positionieren/bewegen
	// 2 -   "    skalieren
	// 3 -   "    rotieren
	// 4 - execute transformation now    nSteps
	// 5 - set delaytime
	// 0xF - end of initial movement (all commands up to F will be skipped in loops)
	
    // 3*j_int   command-specific data:
	public int	   i_data;	  // delaytime in ms  /  nSteps  /  unused for 1,2,3,F
	public double x_data;	  // unused for 4,5,F
	public double y_data;	  // unused for 4,5,F
	public double z_data;	  // unused for 4,5,F
	
	public VectorGraphicsAnimationCommand() {}
	public VectorGraphicsAnimationCommand(byte cmd, int data) {
		this.cmd = cmd;
		this.i_data = data;
		this.x_data = Double.NaN;
		this.y_data = 0;
		this.z_data = 0;
	}
	public VectorGraphicsAnimationCommand(byte cmd, double x, double y, double z) {
		this.cmd = cmd;
		this.x_data = x;
		this.y_data = y;
		this.z_data = z;
		this.i_data = -1;
	}

	public void readExternal(ObjectInput is) throws IOException {
		cmd = is.readByte();
		if (cmd < 4)
			x_data = readDouble(is);
		else
			i_data = is.readInt();
		y_data = readDouble(is);
		z_data = readDouble(is);

		//XXX: is.skipBytes( VectorGraphicsAnimationHeader.header.CommandSize - 13 );
	} 

	public void writeExternal(ObjectOutput os) throws IOException {
		os.writeByte(cmd);
		if (cmd < 4) {
			writeDouble(os, x_data);
			writeDouble(os, y_data);
			writeDouble(os, z_data);
		} else {
			os.writeInt(i_data);
			writeDouble(os, 0);
			writeDouble(os, 0);
		}

		//XXX: is.skipBytes( VectorGraphicsAnimationHeader.header.CommandSize - 13 );
	} 

	/**
	 * read fixedpoint double formatted as 22bit.10bit
	 */
	private double readDouble(DataInput is) throws IOException {
		double v = (double) is.readInt();
		return v / 1024;
	} 
	/**
	 * write fixedpoint double formatted as 22bit.10bit
	 */
	private void writeDouble(DataOutput os, double v) throws IOException {
		os.writeInt((int) (v * 1024));
	} 

	public String toString() {
		switch (cmd) {
			case 1: return "\"move\" (" + MathUtilities.format(x_data, 8) + "|" + MathUtilities.format(y_data, 8) + "|" + MathUtilities.format(z_data, 8) + ")";
			case 2: return "\"scale\" (" + MathUtilities.format(x_data, 8) + "|" + MathUtilities.format(y_data, 8) + "|" + MathUtilities.format(z_data, 8) + ")";
			case 3: return "\"rotate\" (" + MathUtilities.format(x_data, 8) + "|" + MathUtilities.format(y_data, 8) + "|" + MathUtilities.format(z_data, 8) + ")";
			case 4: return "\"exec\" " + i_data;
			case 5: return "\"set_delay\" " + i_data;
			case 0xF: return "\"start\"";
			default:
				throw new IllegalStateException("wrong file format for 3DAnimation in *.ani file. unknown command " + cmd);
		}
	}
}
