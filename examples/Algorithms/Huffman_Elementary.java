import orbital.io.*;
import orbital.text.*;
import orbital.util.*;
import orbital.util.graph.*;
import orbital.util.graph.ListTree.TreeNode;
import java.util.*;
import java.io.*;
import orbital.Adjoint;

/**
 * (Elementary version of greedy) Huffman compression.
 * Creates the huffman code for a file.
 * <p>
 * Note that the huffman code is not stored in the compressed file such that
 * it cannot be decoded unless the huffman code is preserved as well.
 * </p>
 * <p>
 * Also note that the size of a data chunk is not stored in the compressed file
 * such that some trailing bits at the end of the file may be "decoded" as well.
 * </p>
 *
 * @version 0.9, 2000/08/07
 * @author  Andr&eacute; Platzer
 * @see Huffman
 */
public class Huffman_Elementary {
    public static void main(String arg[]) throws Exception {
	File		     file = new File(arg.length > 0 ? arg[0] : "t.txt");
	OccurrenceAnalyzer   occurrences = new OccurrenceAnalyzer();
	AnalyzingInputStream is = new AnalyzingInputStream(new FileInputStream(file), occurrences);
	IOUtilities.copy(is, null);	   // read all
	Huffman_Elementary h = new Huffman_Elementary(occurrences.getOccurrences());
	Object			   code = h.createHuffmanCode();
	System.out.println(code);

	// use huffman code to perform compression
	OutputStream os;
	h.encode(new FileInputStream(file), os = new FileOutputStream(IOUtilities.changeExtension(file, "cmp")));
	os.close();

	// use huffman code again to perform decompression
	h.decode(new FileInputStream(IOUtilities.changeExtension(file, "cmp")), os = new FileOutputStream(IOUtilities.changeExtension(file, "decmp")));
	os.close();
    } 

    private final double[] occurrences;
    public Huffman_Elementary(double[] occurrences) {
	this.occurrences = occurrences;
    }

    /**
     * Binary decision trie containing huffman code.
     * @see #huffman_trie
     */
    private ListTree		  trie;

    public ListTree createHuffmanCode() {
	List c = new LinkedList();
	for (int i = 0; i < occurrences.length; i++) {
	    if (occurrences[i] != 0)
		c.add(new TreeNode(new String("" + (char) i), new Double(occurrences[i])));
	}
	//assert c.size() >= 1 : "we do not encode an empty string";

        while (c.size() > 1) {
	    // remove the two worst nodes
	    TreeNode c1 = (TreeNode) Collections.min(c, occurrenceComparator);
	    if (!c.remove(c1))
		throw new AssertionError("removing an existing element changes");
	    TreeNode c2 = (TreeNode) Collections.min(c, occurrenceComparator);
	    if (!c.remove(c2))
		throw new AssertionError("removing an existing element changes");
	    // we have two new choices so let's make a new node containing both children
	    String new_key = "" + c1.getKey() + c2.getKey();
	    double weight_sum = ((Number) c1.getValue()).doubleValue() + ((Number) c2.getValue()).doubleValue();
	    TreeNode new_node = new TreeNode(new_key, new Double(weight_sum));
	    new_node.add(c1);
	    new_node.add(c2);
	    c.add(new_node);
	}
	//assert c.size() == 1 : "successively removing two and adding one elements of a non-empty list finally leads to one element";
	return trie = new ListTree((TreeNode) c.get(0));
    } 

    private static final Comparator occurrenceComparator = new Comparator() {
	    public int compare(Object a, Object b) {
		final double oa = ((Double) ((KeyValuePair) a).getValue()).doubleValue();
		final double ob = ((Double) ((KeyValuePair) b).getValue()).doubleValue();
		return oa > ob ? 1 : oa < ob ? -1 : 0;
	    }
	};


    // encoding and decoding given the corresponding huffman trie

    private void encode(InputStream is, OutputStream os) throws IOException {
	System.out.println(trie);
	BitSet data = new BitSet();
	int	   dlen = 0;
	while (is.available() > 0) {
	    int	   b = is.read();
	    BitSet c;
	    int	   clen = code((char) b, c = new BitSet());
	    dlen = append(data, dlen, c, clen);
	} 
	Adjoint.print(Adjoint.DEBUG, "writing", toString(data, dlen));

	// byte-wise writing
	for (int i = 0; i < dlen; ) {
	    int val = 0;
	    for (int k = 0; k < 8; k++)
		val |= data.get(i++) ? 1 << k : 0;
	    os.write(val);
	} 
    } 

    private void decode(InputStream is, OutputStream os) throws IOException {
	BitSet data = new BitSet();
	int	   dlen = 0;
	while (is.available() > 0) {
	    int b = is.read();
	    for (int k = 0; k < 8; k++)
		if ((b & (1 << k)) == 0)
		    data.clear(dlen++);
		else
		    data.set(dlen++);
	} 
	Adjoint.print(Adjoint.DEBUG, "reading", toString(data, dlen));
	System.out.println(trie);
	TreeNode n = (TreeNode) trie.getRoot();
	for (int i = 0; i < dlen; ) {
	    if (n.isLeaf()) {
		//assert ((String) n.getKey()).length() == 1 : "leaves have a single character";

		// write character
		os.write(((String) n.getKey()).charAt(0));

		// reenter at the trie's root
		n = (TreeNode) trie.getRoot();
	    } else if (data.get(i++)) {

		// right-child
		Iterator it = n.edges();
		it.next();
		n = (TreeNode) it.next();
	    } else {

		// left-child
		n = (TreeNode) n.edges().next();
	    } 
	} 
    } 

    /**
     * @pre c must be a new and empty BitSet
     */
    private int code(char b, BitSet c) {
	int	 clen = 0;
	TreeNode n = (TreeNode) trie.getRoot();
	descending:
	while (!n.isLeaf()) {
	    //assert n.getEdgeCount() <= 2 : "binary tree";
	    int i = 0;
	    for (Iterator it = n.edges(); it.hasNext(); i++) {
		TreeNode o = (TreeNode) it.next();
		//assert i < 2 : "binary tree";
		if (((String) o.getKey()).indexOf(b) >= 0) {
		    if (i == 0)
			c.clear(clen++);
		    else
			c.set(clen++);
		    n = o;
		    continue descending;
		} 
	    } 
	    throw new InternalError("unsupported encoding for character '" + b + "' in code table");
	} 
	//assert n.getKey().equals("" + b) : "descending along the character should finally lead to the character's leaf";
	return clen;
    } 


    /**
     * Append a bit set to a bit set.
     * @param b the bitset to append to.
     * @param blength the length of b, i.e. the number of bits contained in b.
     * @param a the bitset to append to b.
     * @param alength the length of a, i.e. the number of bits contained in a.
     * @return the new number of bits contained in b (blength + alength).
     */
    public static int append(BitSet b, int blength, BitSet a, int alength) {
	for (int i = 0; i < alength; i++)
	    if (a.get(i))
		b.set(blength + i);
	    else
		b.clear(blength + i);
	return blength + alength;
    } 

    private static String toString(BitSet b, int blength) {
	StringBuffer sb = new StringBuffer(blength);
	for (int i = 0; i < blength; i++)
	    sb.append(b.get(i) ? '1' : '0');
	return sb.toString();
    } 

}
