import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.io.*;
//import orbital.text.OccurrenceAnalyzer;
import orbital.util.*;
import orbital.util.graph.*;
import orbital.util.graph.ListTree.TreeNode;
import java.util.*;
import java.io.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Huffman compression.
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
 * @invariants treeView(trieOpt).equals(trieExpl)
 * @version 0.9, 2000/08/07
 * @author  Andr&eacute; Platzer
 * @see Huffman_Elementary
 */
public class Huffman implements GreedyProblem {
    private static final Logger logger = Logger.global;
    public static void main(String arg[]) throws Exception {
	logger.setLevel(Level.FINEST);
	// analyze occurrences
	File		     file = new File(arg.length > 0 ? arg[0] : "t.txt");
	OccurrenceAnalyzer   occurrences = new OccurrenceAnalyzer();
	AnalyzingInputStream is = new AnalyzingInputStream(new FileInputStream(file), occurrences);
	IOUtilities.copy(is, null);	   // read all

	// construct huffman code
	Huffman h = new Huffman(occurrences.getOccurrences());
	List	code = new Greedy().solve(h);
	System.out.println("code:\t" + code);

	// use huffman code to perform compression
	OutputStream os;
	IOUtilities.copy(new FileInputStream(file), os = h.new HuffmanOutputStream(new FileOutputStream(IOUtilities.changeExtension(file, "cmp"))));
	os.close();

	// use huffman code again to perform decompression
	IOUtilities.copy(h.new HuffmanInputStream(new FileInputStream(IOUtilities.changeExtension(file, "cmp"))), os = new FileOutputStream(IOUtilities.changeExtension(file, "decmp")));
	os.close();
    }

    /**
     * Byte occurrence table.
     */
    private final double[] occurrences;
    public Huffman(double[] occurrences) {
	this.occurrences = occurrences;
    }

    // prefix code as a binary decision trie

    /**
     * List representation of binary decision trie (@xxx sure?) containing huffman codes
     * of the byte b at the corresponding index b.
     * This trie is kept in <b>reverse</b> order during construction
     * and must be reverted and turned into a heap after a solution is found.
     * <p>
     * Could be used for an optimized algorithm.
     * </p>
     * @invariants The tree view of this list equals trieExpl.
     * @see #trieExpl
     */
    private List			   trieOpt;

    /**
     * Explicit binary decision trie containing huffman code.
     * @invariants This tree equals the tree view of trieOpt.
     * @see #trieOpt
     */
    private ListTree		   trieExpl;

    // mere buffers

    /**
     * buffers the last new_choice object until next new_choice occurs
     * (necessary for adding pairs of choices instead of single choices).
     */
    private transient TreeNode last_new_choice = null;

    /**
     * keeps the last new node added by {@link #nextPartialSolution(List,Object)}.
     */
    private transient TreeNode new_node = null;

    public List getInitialCandidates() {
	trieOpt = new ArrayList(2 * (Byte.MAX_VALUE - Byte.MIN_VALUE + 1));

	List c = new LinkedList();
	for (int i = 0; i < occurrences.length; i++) {
	    if (occurrences[i] != 0)
		c.add(new TreeNode(new String("" + (char) i), new Double(occurrences[i])));
	}
	return c;
    }

    public Function getWeightingFor(List choices) {
	return weighting;
    }
    private static final Function weighting = new Function() {
	    public Object apply(Object o) {
		return new Double(-((Double) ((KeyValuePair) o).getValue()).doubleValue());
	    }
	};

    /**
     * Extends the choices with a new_choice if that is feasible, otherwise nothing is changed.
     * @preconditions choices is a valid partial solution, new_choice has maximum local weight.
     * @postconditions RES new solution value that includes new_choice if feasible.
     */
    public List nextPartialSolution(List choices, Object new_choice) {

	// we use trieOpt instead of choices
	if (last_new_choice == null) {
	    // remember single choice until next call and return
	    last_new_choice = (TreeNode) new_choice;
	    return choices;
	}

	// we have two new choices so let's make a new node containing both children
	// this means we build the tree from bottom to top meanwhile merging its subtrees
	TreeNode c1 = last_new_choice;
	TreeNode c2 = (TreeNode) new_choice;
	// forget single choice now, because we add it anyway
	last_new_choice = null;

	// add to optimized trie
	trieOpt.add(c1.getKey() + "");
	trieOpt.add(c2.getKey() + "");

	// add to explicit trie
	// make a new parent for both keys having the sum of their individual weights as occurrence frequency
	final String new_key = "" + c1.getKey() + c2.getKey();
	final double weight_sum = ((Number) c1.getValue()).doubleValue() + ((Number) c2.getValue()).doubleValue();
	new_node = new TreeNode(new_key, new Double(weight_sum));
	new_node.add(c1);
	new_node.add(c2);
	choices.add(new_node);
	return choices;
    }

    public List nextCandidates(List candidates) {
	if (new_node == null)
	    return candidates;

	// add new node to the list of candidates
	candidates.add(new_node);

	// clear the reference to the new candidate that has alread been added
	new_node = null;
	return candidates;
    }

    public boolean isPartialSolution(List choices) {
	return true;
    }

    public boolean isSolution(List choices) {
	// the list version of the bdd trie describing the huffman code solution
	trieOpt.add(last_new_choice.getKey());
	// a bdd trie describing the huffman code solution
	trieExpl = new ListTree(last_new_choice);

	logger.log(Level.FINER, "isSolution()=true, list-version={0}", trieOpt);
	logger.log(Level.FINER, "isSolution()=true, bdd-version={0}", trieExpl);
	return true;
    }


    // encoding and decoding given the corresponding huffman trie

    /**
     * Encode a stream with the current huffman trie.
     */
    public class HuffmanOutputStream extends FilterOutputStream {
    	private BitSet data = new BitSet();
    	private int	   dlen = 0;
	public HuffmanOutputStream(OutputStream out) {
	    super(out);
	    logger.log(Level.FINER, "encode {0}", trieExpl);
	}

	public void flush() throws IOException {
	    writeBuffer(dlen);
	    super.flush();
	}

    	public void write(int b) throws IOException {
	    byte[] cb = {
		(byte) b
	    };
	    this.write(cb, 0, 1);
    	}
    	public void write(byte[] b) throws IOException {
	    this.write(b, 0, b.length);
    	}

    	/**
    	 * Basic-Method for write.
    	 * @throws IOException when an I/O error occurs. Note that we cannot safely continue afterwards in all cases.
    	 */
    	public void write(byte[] b, int off, int len) throws IOException {
	    if (len == 0)
		return;
	    // encode b as a bit sequence
	    for (int i = 0; i < len; i++) {
		BitSet c;
		int	   clen = encode((char) b[off + i], c = new BitSet());
		dlen = append(data, dlen, c, clen);
	    }
	    logger.log(Level.FINER, "writing {0}", Huffman.toString(data, dlen));

	    writeBuffer(8*(dlen/8));	// ==8*(int)Math.floor(dlen/8.0) sic(!)
    	}
    	private void writeBuffer(int bitLength) throws IOException {
	    Utility.pre(bitLength <= dlen, "the number of bits to flush out should not exceed the number of bits available");
	    // byte-wise conversion to a new buffer
	    final byte outBuffer[] = new byte[(int)Math.ceil(bitLength/8.0f)];
	    for (int i = 0, index = 0; i < bitLength; index++) {
		int val = 0;
		for (int k = 0; k < 8; k++)
		    val |= data.get(i++) ? 1 << k : 0;
		outBuffer[index] = (byte) val;
	    }
	    // write the new buffer
	    out.write(outBuffer);
	    // strip the leading part of data that we already have compressed and written (i.e. all but the last (<8) bits)
	    // shift BitSet: data >> bitLength
	    //data = data.get(bitLength, dlen);		//@version 1.4
	    this.dlen -= bitLength;
	    BitSet shiftedData = new BitSet(dlen);
	    for (int j = 0; j < dlen; j++)
		if (data.get(bitLength + j))
		    shiftedData.set(j);
		else
		    shiftedData.clear(j);
	    this.data = shiftedData;
	}

    	/**
    	 * @param c must be a new and empty BitSet. Will contain the return value afterwards.
    	 * @preconditions c must be a new and empty BitSet to get the return value
    	 */
    	private int encode(char b, BitSet c) {
	    if (trieExpl == null)
		throw new IllegalStateException("no huffman code solution constructed yet");
	    int		 clen = 0;
	    TreeNode n = (TreeNode) trieExpl.getRoot();
	    descending:
	    while (!n.isLeaf()) {
		//assert n.getEdgeCount() <= 2 : "binary tree";
		int i = 0;
		for (Iterator it = n.edges(); it.hasNext(); i++) {
		    TreeNode o = (TreeNode) it.next();
		    //assert i < 2 : "binary tree";
		    if (((String) o.getKey()).indexOf(b) >= 0) {
			if (i == 0)
			    // left child
			    c.clear(clen++);
			else
			    // right child
			    c.set(clen++);
			// continue descending from the child found
			n = o;
			continue descending;
		    }
		}
		throw new InternalError("code table has no encoding for character '" + b + "'");
	    }

	    //assert n.getKey().equals("" + b) && n.isLeaf() : "descending along the character should finally lead to the character's leaf";
	    return clen;
    	}

    	/**
    	 * Append a bit set to a bit set.
    	 * @param b the bitset to append to.
    	 * @param blength the length of b, i.e. the number of bits contained in b.
    	 * @param a the bitset to append to b.
    	 * @param alength the length of a, i.e. the number of bits contained in a.
    	 * @return the new number of bits contained in b, i.e. (blength + alength).
    	 */
    	private int append(BitSet b, int blength, BitSet a, int alength) {
	    for (int i = 0; i < alength; i++)
		if (a.get(i))
		    b.set(blength + i);
		else
		    b.clear(blength + i);
	    return blength + alength;
    	}
    }

    /**
     * Decode a stream with the current huffman trie.
     */
    public class HuffmanInputStream extends FilterInputStream {
    	private BitSet	data = new BitSet();
    	private int		dlen = 0;
    	/**
    	 * whether the underlying stream already reached EOF.
    	 */
    	private boolean	reachedEOF = false;
	public HuffmanInputStream(InputStream in) {
	    super(in);
	    logger.log(Level.FINER, "decode {0}", trieExpl);
	}
        
    	/**
    	 * Returns the number of bytes that can be read without blocking.
    	 * @xxx To be honest, this method is wrong since it does not know about decompression.
    	 */
    	public int available() throws IOException {
	    int avail = super.available();
	    return avail > 0 ? avail : dlen > 0 ? 1/*@xxx 1 is a great underestimation */ : 0;
    	} 
        
    	public int read() throws IOException {
	    byte[] cb = new byte[1];
	    if (this.read(cb, 0, 1) == -1)
		return -1;
	    return IOUtilities.byteToUnsigned(cb[0]);
    	} 
    	public int read(byte[] b) throws IOException {
	    return this.read(b, 0, b.length);
    	} 
    
    	/**
    	 * Basic-Method for read.
    	 * @throws IOException when an I/O error occurs. Note that we cannot safely continue afterwards in all cases.
    	 */
    	public int read(byte[] b, final int off, final int len) throws IOException {
	    if (len == 0)
		return 0;
	    if (trieExpl == null)
		throw new IllegalStateException("no huffman code solution constructed yet");
       		
	    if (reachedEOF && dlen == 0)
		return -1;

	    {
		byte inputBuffer[] = new byte[Math.max(0, len - dlen/8)];
		int blen = in.read(inputBuffer, off, inputBuffer.length);			//@note len - dlen/8 is an estimate for the number of bytes to decompress as well in order to get len decompressed bytes. Since it's not necessary to read len if we could, this is ok.
		if (blen == -1) {
		    reachedEOF = true;
		    if (dlen == 0)
			return -1;
		    else
			blen = 0;		// nothing read, then 0 are in
		}
        		
		// convert blen bytes of inputBuffer to a bit sequence (appending to data)
		for (int i = 0; i < blen; i++) {
		    int value = inputBuffer[off + i];
		    for (int k = 0; k < 8; k++)
			if ((value & (1 << k)) == 0)
			    data.clear(dlen++);
			else
			    data.set(dlen++);
		}
		logger.log(Level.FINER, "current bit chunk {0}", Huffman.toString(data, dlen));
	    }

	    // decode bit sequence (only up to len resulting bytes)
	    int		 index = 0;				// number of bytes decompressed
	    int		 bits = 0;				// number of bits of data consumed
	    TreeNode n = (TreeNode) trieExpl.getRoot();
	    {
		int trialBits = bits;
		while (index < len && trialBits < dlen) {
		    if (n.isLeaf()) {
			//assert ((String) n.getKey()).length() == 1 : "leaves have a single character";
        
			// write character
			b[off + (index++)] = (byte) ((String) n.getKey()).charAt(0);
        				
			// consume bits up to trialBits
			bits = trialBits;
        
			// re-enter at the trie's root
			n = (TreeNode) trieExpl.getRoot();
		    } else if (data.get(trialBits++)) {
			// right child
			Iterator it = n.edges();
			it.next();
			n = (TreeNode) it.next();
		    } else {
			// left child
			n = (TreeNode) n.edges().next();
		    }
		}
	    }
    		
	    // strip the leading part of data that we already have consumed and decompressed
	    // shift BitSet: data >> bits - 1
	    //data = data.get(bits, dlen);		//@version 1.4
	    this.dlen -= bits;
	    BitSet shiftedData = new BitSet(dlen);
	    for (int j = 0; j < dlen; j++)
		if (data.get(bits + j))
		    shiftedData.set(j);
		else
		    shiftedData.clear(j);
	    this.data = shiftedData;
	    //assert dlen >= 0 : "subtracting read bits still leads to a nonnegative length";
    		
	    if (reachedEOF && index < len) {
		// if we could not provide as much data as was required, and there is no hope for
		// additional compressed bytes to come along since the underlying stream has
		// reached EOF, kill buffer
		dlen = 0;
		data = new BitSet();
	    }
	    return index;
	}
		
	public void close() throws IOException {
	    super.close();
	    data = null;
	    dlen = 0;
	}
    }


    private static String toString(BitSet b, int blength) {
	StringBuffer sb = new StringBuffer(blength);
	for (int i = 0; i < blength; i++)
	    sb.append(b.get(i) ? '1' : '0');
	return sb.toString();
    }
}
