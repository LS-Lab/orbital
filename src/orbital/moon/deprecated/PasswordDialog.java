/*
 * @(#)PasswordDialog.java 0.9 1998/02/15 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;

import java.io.IOException;
import java.io.NotSerializableException;

import orbital.io.cryptix.Cipher;
import orbital.io.cryptix.CipherOutputStream;
import orbital.io.encoding.Base64Writer;
import java.security.Key;
import java.security.GeneralSecurityException;
import orbital.util.InnerCheckedException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.io.StringWriter;

import javax.crypto.spec.SecretKeySpec;


/**
 * A PasswordDialog displays a prompt waiting modally for input.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @dependency orbital.io.cryptix.* Uses orbital service "security provider Orbital".
 * @dependency orbital.io.encoding.* Uses orbital service "encoding".
 */
public
class PasswordDialog extends InputDialog {
	private static transient final String transformation = "Direct";
	private static transient final Key	  key = new javax.crypto.spec.SecretKeySpec("1b84nH80cT2".getBytes(), transformation);

	private static class Debug {
		private Debug() {}
		public static void main(String arg[]) throws Exception {
			UserDialog dlg = new PasswordDialog(new Frame(), "PasswordDialog", "Type any password here");
			dlg.start();
			System.out.println(dlg.getResult());
		} 
	}


	public PasswordDialog(Frame parent, String title) {
		super(parent, title);
		input.setEchoChar('*');
	}

	public PasswordDialog(Frame parent, String title, String message) {
		super(parent, title, message);
		input.setEchoChar('*');
	}

	/**
	 * For security reasons, encrypts the result of a PasswordDialog.
	 */
	protected void setResult(String prev_res) {
		String res = encrypt(input.getText());
		input.setText("");
		result = res;	 // InputDialog.super.setResult(res);
		res = null;
		prev_res = null;
	} 

	private static String encrypt(String s) {
		try {
			Cipher c = Cipher.getInstance(transformation);
			c.init(Cipher.ENCRYPT_MODE, key);
			ByteArrayOutputStream bos;
			OutputStream		  os = new CipherOutputStream(bos = new ByteArrayOutputStream(), c);
			os.write(s.getBytes());
			os.close();
			os = null;
			StringWriter swr;
			Writer		 wr = new Base64Writer(swr = new StringWriter());
			wr.write(bos.toString());
			bos = null;
			wr.close();
			return swr.toString();
		} catch (IOException e) {
			throw new InnerCheckedException(e);
		} catch (GeneralSecurityException e) {
			throw (SecurityException) new SecurityException().initCause(e);
		} 
	} 

	/**
	 * Prevents serialization of fields to stream for security reasons.
	 * @serialData None
	 * @throws NotSerializableException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		throw new NotSerializableException("PasswordDialog cannot be serialized for security reasons");
	} 

	/*
	 * Prevents reading from an ObjectInputStream for security reasons.
	 * @serial None
	 * @throws NotSerializableException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		throw new NotSerializableException("PasswordDialog cannot be serialized for security reasons");
	} 
}
