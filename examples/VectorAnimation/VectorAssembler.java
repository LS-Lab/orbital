

import orbital.awt.virtual.*;
import orbital.moon.awt.virtual.*;
import java.io.*;
import orbital.io.*;
import orbital.io.encoding.*;
import java.util.*;
import orbital.math.MathUtilities;
import orbital.math.Values;
import orbital.math.Real;
import orbital.util.*;
import java.awt.Color;

import orbital.moon.io.*;

/**
 * VectorAssembler is an assembler and disassembler for
 * vector graphic files and animation files.
 * VectorAssembler recognizes the following files:<ul>
 * <li>vector graphic binary files (.vec)</li>
 * <li>animation binary files (.ani)</li>
 * <li>vector graphic source files (.vecsrc)</li>
 * <li>animation source files (.anisrc)</li>
 * </ul>
 * 
 * @version 0.8,, 2000/07/26
 * @author  Andr&eacute; Platzer
 */
public class VectorAssembler {

    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	if (arg.length < 2 || "-?".equals(arg[0])) {
	    System.out.println(usage);
	    return;
	} 
	VectorAssembler asm = new VectorAssembler();
	String			action = arg[0];
	File			file = new File(arg[1]);
	String			extension = IOUtilities.getExtension(file);
	for (int i = 0; i < action.length(); i++) {
	    switch (action.charAt(i)) {
	    case 'a':
		if ("vecsrc".equalsIgnoreCase(extension)) {
		    ObjectDataOutputStream os;
		    File			 dst = IOUtilities.changeExtension(file, "vec");
		    asm.load(DataReader.getInstance(new FileReader(file), "strict"));
		    asm.store(os = new ObjectDataOutputStream(new FileOutputStream(dst)));
		    os.close();
		    System.out.println("assembled " + dst);
		} else if ("anisrc".equalsIgnoreCase(extension)) {
		    ObjectDataOutputStream os;
		    File				   dst = IOUtilities.changeExtension(file, "ani");
		    asm.loadAnimation(DataReader.getInstance(new FileReader(file), "strict"));
		    asm.storeAnimation(os = new ObjectDataOutputStream(new FileOutputStream(dst)));
		    os.close();
		    System.out.println("assembled " + dst);
		} else
		    System.err.println("wrong file extension: " + extension);
		break;
	    case 'd':
		if ("vec".equalsIgnoreCase(extension)) {
		    DataWriter os;
		    File	   dst = IOUtilities.changeExtension(file, "vecsrc");
		    asm.load(new ObjectDataInputStream(new FileInputStream(file)));
		    asm.store(os = new DataWriter(new FileOutputStream(dst)));
		    os.close();
		    System.out.println("disassembled " + dst);
		} else if ("ani".equalsIgnoreCase(extension)) {
		    DataWriter os;
		    File	   dst = IOUtilities.changeExtension(file, "anisrc");
		    asm.loadAnimation(new ObjectDataInputStream(new FileInputStream(file)));
		    asm.storeAnimation(os = new DataWriter(new FileOutputStream(dst)));
		    os.close();
		    System.out.println("disassembled " + dst);
		} else
		    System.err.println("wrong file extension: " + extension);
		break;
	    default:
		System.err.println("no valid action '" + action.charAt(i) + "'");
	    }
	} 
    } 
    public static final String  usage = "usage: " + VectorAssembler.class + " action [file.ani|file.vec|file.anisrc|file.vecsrc]" + System.getProperty("line.separator") + "Where action is one of:\n\ta - assemble binary file from source code (file.vecsrc or file.anisrc)" + "\n\td - disassemble binary file (file.vec or file.ani)";


    private v_View				world;
    private VectorAnimationData animation;

    /**
     * Read binary code of the world.
     */
    protected void load(ObjectInput is) throws ClassNotFoundException, IOException {
	world = new v_View();
	world.readExternal(is);
    } 

    /**
     * Write binary code of the world.
     */
    protected void store(ObjectOutput os) throws IOException {
	world.writeExternal(os);
    } 

    /**
     * Parse in sourcecode of the world.
     */
    protected void load(DataReader in) throws ParseException, IOException {
	world = (v_View) loadComp(in);
    } 

    /**
     * Write sourcecode of the world.
     */
    protected void store(DataWriter os) throws IOException {
	storeComp(os, world);
    } 

    /**
     * Read binary code of the animation.
     */
    protected void loadAnimation(ObjectInput is) throws IOException {
	animation = new VectorAnimationData();
	animation.readExternal(is);
    } 

    /**
     * Parse in sourcecode of the animation.
     */
    protected void loadAnimation(DataReader in) throws ParseException, IOException {
	try {
	    animation = (VectorAnimationData) ((Class) classIdentifier.get(in.readUTF())).newInstance();
	    if ('[' == in.readChar())
		while (true) {
		    Object tok = in.readObject();
		    if (new Character(']').equals(tok))
			break;
		    if (new Character(',').equals(tok))
			continue;
		    animation.initial.add(loadCommand((String) tok, in));
		} 
	    if (animation.initial.isEmpty() || ((VectorGraphicsAnimationCommand) animation.initial.lastElement()).cmd != 0xF)
		animation.initial.add(new VectorGraphicsAnimationCommand((byte) 0xF, 0));
	    if ('{' != in.readChar())
		throw new ParseException(in, "{");
	    while (true) {
		Object tok = in.readObject();
		if (new Character('}').equals(tok))
		    break;
		if (new Character(',').equals(tok))
		    continue;
		animation.commands.add(loadCommand((String) tok, in));
	    } 
	} catch (InstantiationException x) {
	    throw new IOException("exception resolving class specified in input");
	} catch (IllegalAccessException x) {
	    throw new IOException("exception resolving class specified in input");
	} 
    } 

    /**
     * Write binary code of the animation.
     */
    protected void storeAnimation(ObjectOutput os) throws IOException {
	animation.writeExternal(os);
    } 

    /**
     * Write sourcecode of the animation.
     */
    protected void storeAnimation(DataWriter out) throws IOException {
	out.writeUTF(typeIdentifier.get(animation.getClass()) + "");	// .writeBytes
	Object[] c = animation.getInitial();
	out.writeChars("[");
	for (int i = 0; i < c.length; i++)
	    out.writeChars("\t" + c[i]);
	out.writeChars("]");
	c = animation.getCommands();
	out.writeChars("{");
	for (int i = 0; i < c.length; i++)
	    out.writeChars("\t" + c[i]);
	out.writeChars("}");
    } 


    private void storeComp(DataWriter out, v_Component comp) throws IOException {
	if (!(comp instanceof v_Vertex))
	    out.writeUTF(typeIdentifier.get(comp.getClass()) + "");	   // .writeBytes
	storeComponentsDeclaredFields(out, comp);
	if (!(comp instanceof v_Container))
	    return;
	if (comp instanceof v_Polygon)
	    return;
	out.writeChars("{");
	v_Container c = (v_Container) comp;
	for (int i = 0; i < c.getComponentCount(); i++) {
	    v_Component n = c.getComponent(i);
	    storeComp(out, n);
	} 
	out.writeChars("}");
    } 

    private v_Component loadComp(DataReader in) throws ParseException, IOException {
	try {
	    if (Character.TYPE.equals(in.nextType()))
		if ('}' == in.readChar())
		    return null;
	    v_Component comp = (v_Component) ((Class) classIdentifier.get(in.readUTF())).newInstance();
	    System.err.println("  at " + comp.getClass());
	    loadComponentsDeclaredFields(in, comp);
	    if (!(comp instanceof v_Container))
		return comp;
	    if (comp instanceof v_Polygon)
		return comp;
	    if ('{' != in.readChar())
		throw new ParseException(in, "{");
	    v_Container c = (v_Container) comp;
	    while (true) {
		v_Component n = loadComp(in);
		if (n == null)
		    break;
		c.add(n);
	    } 
	    return comp;
	} catch (ClassNotFoundException x) {
	    throw new IOException("exception resolving class specified in input");
	} catch (InstantiationException x) {
	    throw new IOException("exception resolving class specified in input");
	} catch (IllegalAccessException x) {
	    throw new IOException("exception resolving class specified in input");
	} 
    } 

    /**
     * External storage of the declaredFields of v.
     * Must keep conform with orbital.awt.virtual.*
     */
    private void storeComponentsDeclaredFields(DataWriter out, v_Component v) throws IOException {
	if (v instanceof v_Vertex)
	    out.writeChars(v.toString() + ", ");
	else if (v instanceof v_Polygon) {
	    v_Polygon o = (v_Polygon) v;
	    out.writeUTF(Integer.toHexString(o.getColor().getRGB()));
	    out.writeChars("[" + MathUtilities.format(o.getVertexIndices(lastObject)) + "]");
	} else if (v instanceof v_Object) {
	    v_Object o = (v_Object) v;
	    out.writeChars("[");
	    for (int i = 0; i < o.getVertexCount(); i++)
		out.writeChars("\t" + o.getVertex(i) + ", ");
	    out.writeChars("]");
	    lastObject = o;
	} else if (v instanceof v_View) {
	    v_View o = (v_View) v;
	    out.writeChars("(");
	    out.writeUTF("polygonSorting");
	    out.writeBoolean(o.polysorted);
	    out.writeUTF("zBuffer");
	    out.writeBoolean(o.z_buffered);
	    out.writeChars(")");
	} else if (v instanceof v_World) {}
	else
	    throw new IllegalArgumentException("unhandled type " + v);
    } 
    static v_Object lastObject = null;

    /**
     * External load of the declaredFields of v.
     * Must keep conform with orbital.awt.virtual.*
     */
    private void loadComponentsDeclaredFields(DataReader in, v_Component v) throws ParseException, IOException, ClassNotFoundException {
	if (v instanceof v_Vertex)
	    throw new GeneralComplexionException();
	else if (v instanceof v_Polygon) {
	    v_Polygon o = (v_Polygon) v;
	    if (String.class.equals(in.nextType())) {
		String s = in.readUTF();
		o.setColor(new Color((int) Long.parseLong(s, 16)));
	    } 
	    if ("[".equals(in.readWord())) {
		while (true) {
		    Object tok = in.readObject();
		    if (new Character(']').equals(tok))
			break;
		    if (new Character(',').equals(tok))
			continue;
		    o.add(lastObject.getVertex(((Number) tok).intValue()));
		} 
	    } else
		throw new ParseException(in, "[");

	    // TODO: check for vertices to be arranged counter-clockwise or reorder (@see v_Polygon#isBackface())
	} else if (v instanceof v_Object) {
	    v_Object o = (v_Object) v;
	    if ('[' == in.readChar()) {
		addVertexes:
		while (true) {
		    Object tok = in.readObject();
		    if (new Character(']').equals(tok))
			break;
		    String s = "";
		    while (!new Character(',').equals(tok)) {
			if (new Character(']').equals(tok)) {
			    o.getVertexes().add(v_Vertex.valueOf(s));
			    break addVertexes;
			} 
			s += tok;
			tok = in.readObject();
		    } 
		    o.getVertexes().add(v_Vertex.valueOf(s));
		} 
	    } else
		throw new ParseException(in, "[");
	    lastObject = o;
	} else if (v instanceof v_View) {
	    v_View o = (v_View) v;
	    if (!Character.TYPE.equals(in.nextType()) || in.readChar() != '(')
		return;
	    while (String.class.equals(in.nextType())) {
		String tag = in.readUTF();
		if ("polygonSorting".equals(tag))
		    o.polysorted = in.readBoolean();
		else if ("zBuffer".equals(tag))
		    o.z_buffered = in.readBoolean();
		else
		    throw new ParseException("found illegal tag: " + tag);
	    } 
	    if (in.readChar() != ')')
		throw new ParseException(in, ")");
	} else if (v instanceof v_World) {}
	else
	    throw new IllegalArgumentException("unhandled type " + v);
    } 

    /**
     * Load additional data of the given command.
     */
    private VectorGraphicsAnimationCommand loadCommand(String cmd, DataReader in) throws ParseException, IOException {
	VectorGraphicsAnimationCommand command = new VectorGraphicsAnimationCommand();
	if ("move".equalsIgnoreCase(cmd))
	    command.cmd = 1;
	else if ("scale".equalsIgnoreCase(cmd))
	    command.cmd = 2;
	else if ("rotate".equalsIgnoreCase(cmd))
	    command.cmd = 3;
	else {
	    if ("exec".equalsIgnoreCase(cmd)) {
		command.cmd = 4;
		command.i_data = in.readInt();
	    } else if ("set_delay".equalsIgnoreCase(cmd)) {
		command.cmd = 5;
		command.i_data = in.readInt();
	    } else if ("start".equalsIgnoreCase(cmd)) {
		command.cmd = 0xF;
	    } else
		throw new ParseException(in, "move, scale, rotate, exec, start, set_delay");
	    return command;
	} 
	//assert 1 <= command.cmd && command.cmd <= 3 :: "move, scale, or rotate";
	String s = in.readLine();
	System.err.println("  at " + s);
	orbital.math.Vector v = (orbital.math.Vector) Values.valueOf(s);
	command.x_data = ((Real) v.get(0)).doubleValue();
	command.y_data = ((Real) v.get(1)).doubleValue();
	command.z_data = ((Real) v.get(2)).doubleValue();
	return command;
    } 

    private static Map typeIdentifier;
    private static Map classIdentifier;
    static {
	typeIdentifier = new HashMap();
	typeIdentifier.put(v_View.class, "world");
	typeIdentifier.put(v_World.class, "world");
	typeIdentifier.put(v_Object.class, "object");
	typeIdentifier.put(v_Polygon.class, "polygon");
	typeIdentifier.put(v_Vertex.class, "vertex");

	typeIdentifier.put(VectorAnimationData.class, "animation");

	classIdentifier = new HashMap();
	for (Iterator i = typeIdentifier.entrySet().iterator(); i.hasNext(); ) {
	    Map.Entry e = (Map.Entry) i.next();
	    classIdentifier.put(e.getValue(), e.getKey());
	} 

	// make sure v_View is used for world
	classIdentifier.put("world", v_View.class);
    } 
}
