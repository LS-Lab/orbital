package orbital.moon.awt.virtual;

import java.io.*;

/**
 * File Datastructure representation of *.ani files
 */
public class VectorGraphicsAnimationHeader implements Externalizable {
	public int	 reserved;
	public byte  id;			  // ==0x3A
	public short version;		  // in hex notation z.B. 0x0100
	public short HeaderSize;	  // inkl. feld "HeaderSize"
	public short CommandSize;	  // groesse command chunk
	public int	 CommandOffset;	  // offset zum ersten befehl (rel. zum dateianfang)
	
	public VectorGraphicsAnimationHeader() {
		reserved = 0x12345678;
		id = 0x3A;
		version = 0x0109;
		HeaderSize = 0xF;
		CommandSize = 0xD;
		CommandOffset = 0x11;
	}

	public void readExternal(ObjectInput is) throws IOException {
		reserved = is.readInt();
		id = is.readByte();
		if (id != 0x3A)
			throw new IOException("wrong file format for 3DAnimation in *.ani file");
		version = is.readShort();
		HeaderSize = is.readShort();
		CommandSize = is.readShort();
		CommandOffset = is.readInt();

		if (is instanceof DataInputStream)
			((DataInputStream) is).skipBytes(CommandOffset - 15);
		else if (is instanceof InputStream)
			((InputStream) is).skip(CommandOffset - 15);
		else
			for (int i = 0; i < CommandOffset - 15; i++)
				is.readByte();
	} 

	public void writeExternal(ObjectOutput os) throws IOException {
		os.writeInt(reserved);
		os.writeByte(id);
		os.writeShort(version);
		os.writeShort(HeaderSize);
		os.writeShort(CommandSize);
		os.writeInt(CommandOffset);

		for (int i = 0; i < CommandOffset - 15; i++)
			os.writeByte(0xCC);
	} 
}
