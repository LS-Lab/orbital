/*
 * @(#)PersistentValue.java 0.9 2000/03/31 Andre Platzer
 * 
 * Copyright (c) 1994 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.io;

import java.io.Serializable;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import orbital.util.InnerCheckedException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This derived class will auto use persistent storage for a value
 * at creation/finalization.
 * <p>
 * Auto loads/stores persistent data on disk.</p>
 * <p>
 * <b>Note:</b> if you need resource streams instead of storage files, you cannot use this class,
 * since ordinary {@link Class#getResourceAsStream(String) resource streams} cannot be written to.</p>
 * 
 * @version 0.9, 2000/03/31
 * @author  Andr&eacute; Platzer
 */
public
class PersistentValue extends Object /*implements Serializable*/ {
	/**
	 * The storage file where the value is stored.
	 * @serial
	 */
	protected String file;
	/**
	 * The value stored.
	 * @serial
	 */
	protected Object value;

	/**
	 * tries to auto load persistent data from stream
	 */
	private PersistentValue() {}

	/**
	 * tries to auto load persistent data from stream
	 * @param file the storage file where the value is stored.
	 */
	public PersistentValue(String file) throws IOException {
		this.file = file;
		load();
	}

	/**
	 * Create new persistent data.
	 */
	public PersistentValue(String file, Object value) {
		this.file = file;
		setValue(value);
	}

	/**
	 * Tries to auto load persistent data from stream using a default value if not existent.
	 * If the specified file contains data as expected, then its content will be the persistent
	 * value, the default value otherwise.
	 * @return a persistent value from the file or the default.
	 */
	public static PersistentValue getPersistentValue(String file, Object defaultValue) {
		try {

			// if p had a file, then it could overwrite our file on finalization which
			// can be just anytime, even after an exception in the constructor
			PersistentValue p = new PersistentValue();
			p.setFile(null);	// (!) for finalization purpose
			p.loadImpl(file);
			p.setFile(file);
			return p;
		}
		catch (IOException fall_through) { /* fall-through */}
		catch (SecurityException fall_through) { /* fall-through */} 
		catch (InnerCheckedException fall_through) { /* fall-through */} 
		return new PersistentValue(file, defaultValue);
	} 

	/**
	 * auto stores persistent data in stream.
	 */
	protected void finalize() throws Throwable {
		store();
		super.finalize();
	} 

	// get/set methods
	public void setValue(Object v) {
		this.value = v;
		try {
			store();
		}
		catch (IOException trial) {Logger.global.log(Level.WARNING, "exception", trial);} 
		catch (SecurityException trial) {Logger.global.log(Level.WARNING, "SecurityException", trial);} 
	} 
	public Object getValue() {
		return value;
	} 
	public void setFile(String file) {
		this.file = file;
	} 
	public String getFile() {
		return file;
	} 

	/**
	 * load Object data from stream.
	 */
	public void load() throws IOException {
		loadImpl(file);
	} 
	private void loadImpl(String f) throws IOException {
		if (f != null)
			try {
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));
				value = is.readObject();
				is.close();
			} catch (ClassNotFoundException e) {
				throw new InnerCheckedException("found no class", e);
			} 
	} 

	/**
	 * store Object data in stream.
	 */
	public void store() throws IOException {
		if (file == null)
			return;
		ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
		os.writeObject(value);
		os.close();
	} 
}
