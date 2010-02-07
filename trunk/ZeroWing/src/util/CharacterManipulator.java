package util;

/**
 * Usage: <br>
 * <dt>
 * 	sender calls <br> 
 *   {@code String toSend = CharacterManipulator.constructHuffmanMessage(toSendMessage);}
 *   </dt><br>
 * <dt>
 *  receiver calls <br>
 *   {@code String parsed = CharacterManipulator.deconstructHuffmanMessage(receivedMessage);}
 *   </dt><br>
 *   
 * 
 * 
 * @author kevinzana
 * 
 */
public class CharacterManipulator {
	private static int codeSize = 8;
//	private static String charSet = "UTF8";
//	private static int globalct = 0;

	public static void main(String args[]) {
		// char c = toChar("11111111111111111111111111111");
		// System.out.println(Long.toBinaryString(c));

		String longString = "Some of these models of concurrency are primarily intended to support reasoning and specification, while others can be used through the entire development cycle, including design, implementation, proof, testing and simulation of concurrent systems. The proliferation of different models of concurrency has motivated some researchers to develop ways to unify these different theoretical models. For example, Lee and Sangiovanni-Vincentelli have demonstrated that a so-called \"tagged-signal\" model can be used to provide a common framework for defining the denotational semantics of a variety of different models of concurrency,[4] while Nielsen, Sassone, and Winskel have demonstrated that category theory can be used to provide a similar unified understanding of different models.[5] The Concurrency Representation Theorem in the Actor model provides a fairly general way to represent concurrent systems that are closed in the sense that they do not receive communications from outside. (Other concurrency systems, e.g., process calculi can be modeled in the Actor model using a two-phase commit protocol.[6]) The mathematical denotation denoted by a closed system S is constructed increasingly better approximations from an initial behavior called S using a behavior approximating function progressionS to construct a denotation (meaning ) for S as follows:[7]";
		String shortString = "\"kevin lorenzana kekkkkkkkkkkkkk";
		String girlString = "2007: Debut and first album In "
				+ "July 2007, Girls' Generation had their unofficial first stage performance on MCountdown. "
				+ "The song the girls performed was called \"Into the New World\" "
				+ "(Korean: \"DaSi ManNan SeGye\"), which became their first single. A show documenting "
				+ "the girls' debut titled M.Net's Girls Go To School was filmed during this time. "
				+ "The song had a physical release, which included two other songs, \"Beginning\" and "
				+ "\"Perfect for You\", which was later renamed to \"Honey\" for their self-titled debut "
				+ "album Girls' Generation. An instrumental of \"Into the New World\" was also included. "
				+ "The group began promoting the single soon after its release. Girls' "
				+ "Generation performed their single on SBS's Inkigayo, MBC's Show! Music Core, and on "
				+ "KBS's Music Bank. They officially debuted on August 5, 2007. After a short break, the "
				+ "group's first full album was released in late autumn 2007, with the lead single \"Girls' "
				+ "Generation\" (Korean: \"SoNyeoShiDae\"), a cover of Lee Seung-Chul's hit. Promotion for the "
				+ "single started in early November. Marked by dance routines and vocals, \"Girls' Generation\" "
				+ "was a hit for the group, and the album sold 121,143 copies to date, "
				+ "placing 8th in the year-end charts.[1] In early 2008, Girls' Generation "
				+ "began promoting their second single, \"Kissing You\". The music video featured Donghae "
				+ "from Super Junior. This song achieved the #1 spot on three major TV music rankings—SBS "
				+ "Inkigayo, M.net M.Countdown! and KBS Music Bank. In March 2008, the album was re-released "
				+ "and re-titled Baby Baby. A third single, \"Baby Baby\", was used to promote the album."
				+ " A digital EP featuring Jessica, Tiffany, and Seohyun was released on April 2008, "
				+ "titled Roommate. The single from the mini-album is \"Oppa Nappa\" "
				+ "(\"Korean: Oppa Nappa\", literally \"Bad Brother\", although contextually it refers to an "
				+ "older male friend).";
		String checkString = "00000000000000010000001000000011000001000000010100000110000001110000100000001001000010100000101100001100000011010000111000001111000100000001000100010010000100110001010000010101000101100001011100011000000110010001101000011011000111000001110100011110000111110010000000100001001000100010001100100100001001010010011000100111001010000010100100101010001010110010110000101101001011100010111100110000001100010011001000110011001101000011010100110110001101110011100000111001001110100011101100111100001111010011111000111111010000000100000101000010010000110100010001000101010001100100011101001000010010010100101001001011010011000100110101001110010011110101000001010001010100100101001101010100010101010101011001010111010110000101100101011010010110110101110001011101010111100101111101100000011000010110001001100011011001000110010101100110011001110110100001101001011010100110101101101100011011010110111001101111011100000111000101110010011100110111010001110101011101100111011101111000011110010111101001111011011111000111110101111110011111111000000010000001100000101000001110000100100001011000011010000111100010001000100110001010100010111000110010001101100011101000111110010000100100011001001010010011100101001001010110010110100101111001100010011001100110101001101110011100100111011001111010011111101000001010000110100010101000111010010010100101101001101010011110101000101010011010101010101011101011001010110110101110101011111011000010110001101100101011001110110100101101011011011010110111101110001011100110111010101110111011110010111101101111101011111111000000110000011100001011000011110001001100010111000110110001111100100011001001110010101100101111001100110011011100111011001111110100001101000111010010110100111101010011010101110101101101011111011000110110011101101011011011110111001101110111011110110111111110000011100001111000101110001111100100111001011110011011100111111010001110100111101010111010111110110011101101111011101110111111110000111100011111001011110011111101001111010111110110111101111111100011111001111110101111101111111100111111011111111011111111";
		String checkString2 = "000011110000ab";

//		CharacterManipulator.runTests(girlString + girlString + girlString);
//		CharacterManipulator.runTests(checkString);
//		CharacterManipulator.runTests(checkString2);
//		CharacterManipulator.runTests(longString);
//		CharacterManipulator.runTests(girlString);
//		CharacterManipulator.runTests(shortString);
		
		System.out.println(testMessages(shortString));
		System.out.println(testMessages(checkString));
		System.out.println(testMessages(longString));
		System.out.println(testMessages(girlString));
		System.out.println(testMessages(girlString + girlString + girlString));
	}
	
	private static boolean testMessages(String rawMsg){
		String msg = constructHuffmanMessage(rawMsg);
		double ratio = ((double)msg.length()) / ((double) rawMsg.length()); 
		System.out.println(ratio + " " +msg);
		String received = deconstructHuffmanMessage(msg);
		
		return rawMsg.equals(received);
	}

	private static String extractTo8(String src) {
		String out = "";
		src = padMe(src);
		for (int i = 0; i < src.length(); i = i + 8) {
			out = out + src.substring(i, i + 8) + " ";
		}
		return out;
	}
	public static String constructHuffmanMessage(String s){
		Huffman hm = new Huffman();
		HuffmanResultTuple hrt = hm.getCoding(s);
		
		String treeInfo = hrt.toData;
		String binaryCodedText = hrt.encode();
		String charedBin = compressBinary(binaryCodedText);
		
		// System guarantees that the tree cannot contain this string
		String cut = "#Ac";
		return treeInfo+cut+charedBin;
	}
	public static String deconstructHuffmanMessage(String s){
		int targ = s.indexOf("#Ac");
		String tree = s.substring(0,targ);
		String compBin = s.substring(targ+3);
		System.out.println(tree);
		System.out.println(compBin);
		
		Huffman hm = new Huffman();
		HuffEnt he = hm.toTree(tree);
		
		String chars = decompressToBinary(compBin);
		String trueMsg = solveFromHuffman(he, chars);
		return trueMsg;
	}

	public static void runTests(String s) {
		System.out.println("==================================  ");
		Huffman hm = new Huffman();
		HuffmanResultTuple hrt = hm.getCoding(s);

		String tTreeInfo = hrt.toData;
		String tEncodedText = hrt.encode();
		String binCompressionText = CharacterManipulator
				.compressBinary(tEncodedText);

		displayln("src: (" + s.length() + ") " + s);
		System.out.println("hman: " + hrt);
		System.out.println("tTreeInfo: (" + tTreeInfo.length() + ")"
				+ tTreeInfo);

		displayln("tCode: ("+tEncodedText.length()+") "+tEncodedText);

		displayln("tTrueCode: (" + binCompressionText.length() + ") "+binCompressionText);

		int total = tTreeInfo.length() + binCompressionText.length();

		System.out.println("Total Huffed vs chars: " + total + " | "+s.length());
		System.out.println("Ratio: " + (double) total / (double) s.length());

		Huffman hm2 = new Huffman();
		HuffEnt he2 = hm2.toTree(tTreeInfo);

		// =================== DECOOODE!
		String newBinary = decompressToBinary(binCompressionText);
		System.out.print("newBinary: ("+newBinary.length()+")");
		displayln(newBinary);
		System.out.println("same compressed and decompressed?: " + (newBinary.equals(tEncodedText)));

		String trueMsg = solveFromHuffman(he2, newBinary);
		displayln("MSG: "+trueMsg);
		System.out.println("same init text to final?: "+trueMsg.equals(s));
		System.out.println(s+"\n"+trueMsg);
	}
	
	public static String solveFromHuffman(HuffEnt root, String binary){
		HuffEnt curNode = root;
		String out = "";
		char[] cset = binary.toCharArray();
		String curString = "";
		
		for(int i=0;i<cset.length;i++){
			if(curNode.left==null){
//				System.out.println("sfm: "+curString+" ["+curNode.ch+"]");
				out = out + curNode.ch;
				curNode = root;
//				curString = "";
			} 
			if(cset[i]=='0'){
				curNode = curNode.left;
//				curString = curString + "0";
			} else if(cset[i]=='1'){
				curNode = curNode.right;
//				curString = curString + "1";
			}
		}
		out = out + curNode.ch;
		
		
		
		return out;
	}
	
	public static void displayln(String s){
		int cutLen = 4000;
		int end = s.length();
		if(end>=cutLen){
			for(int i=0;i<end;i = i + cutLen){
				String out = s.substring(i, Math.min(i+cutLen, end));
				if(i==0)
					System.out.println(out);
				else System.out.println("\t "+out);
			}
			
		} else {
			System.out.println(s);
		}
	}

	public static String compressBinary(String bin) {
		codeSize = 8;
		String out = "";
//		System.out.println("binLen: " + bin.length());
		int padLen = checkPad(bin);
		bin = pad(bin, padLen);
//		System.out.println("binLen2: " + bin.length());
		for (int i = 0; i < (bin.length()); i = i + codeSize) {
			String temp = bin.substring(i, i + codeSize);
			int in = Integer.parseInt(temp, 2);
			Character c = (char) in;

			if (Character.isWhitespace(in) || in == 35) {
				// System.out.println("!!!WHITESPACE " + temp + " " + in);
				// System.out.println(temp + " #" +
				// Integer.toHexString(globalct));
				// System.out.println("else if(temp.equals(\""+temp+"\")){");
				// System.out.println("out = out + \"#"+Integer.toHexString(globalct)+"\";");
				// System.out.println("}");
				// globalct++;
				if (temp.equals("00001001")) {
					out = out + "#0";
				} else if (temp.equals("00001010")) {
					out = out + "#1";
				} else if (temp.equals("00001011")) {
					out = out + "#2";
				} else if (temp.equals("00001100")) {
					out = out + "#3";
				} else if (temp.equals("00001101")) {
					out = out + "#4";
				} else if (temp.equals("00011100")) {
					out = out + "#5";
				} else if (temp.equals("00011101")) {
					out = out + "#6";
				} else if (temp.equals("00011110")) {
					out = out + "#7";
				} else if (temp.equals("00011111")) {
					out = out + "#8";
				} else if (temp.equals("00100000")) {
					out = out + "#9";
				} else if (temp.equals("00100011")) {
					out = out + "#A";
				}

			} else if (c == '#') {
				out = out + "##";
			} else {
				out = out + c;
			}
		}
		return padLen + out;
	}

	/**
	 * Decompresses the char set into chars.
	 * automatically removes padding 
	 */
	public static String decompressToBinary(String chars) {
		String out = "";
		int padLen = Integer.parseInt(chars.substring(0, 1));
		chars = chars.substring(1);
		char[] cset = chars.toCharArray();
		for (int i = 0; i < cset.length; i++) {
			char c = cset[i];
			// out = out+".";
			Integer e = (int) c;
			String set = c + "";
			if (c == '#') {
				set = c + "" + cset[++i];
			}
			// System.out.println(">"+set);
			if (set.length() == 1 || c == ' ') {
				out = out + padTo(Integer.toBinaryString(e), 8);
			} else {
				if (set.equals("#0")) {
					out = out + "00001001";
				} else if (set.equals("#1")) {
					out = out + "00001010";
				} else if (set.equals("#2")) {
					out = out + "00001011";
				} else if (set.equals("#3")) {
					out = out + "00001100";
				} else if (set.equals("#4")) {
					out = out + "00001101";
				} else if (set.equals("#5")) {
					out = out + "00011100";
				} else if (set.equals("#6")) {
					out = out + "00011101";
				} else if (set.equals("#7")) {
					out = out + "00011110";
				} else if (set.equals("#8")) {
					out = out + "00011111";
				} else if (set.equals("#9")) {
					out = out + "00100000";
				} else if (set.equals("#A")) {
					out = out + "00100011";
				}
			}

		}

		return out.substring(padLen);
	}

	public static String pad(String s, int padlen) {
		for (int i = 0; i < padlen; i++) {
			s = '0' + s;
		}
		return s;
	}

	public static String padTo(String s, int mark) {
		return pad(s, mark - s.length());
	}

	public static int checkPad(String s) {
		int pad = s.length() % codeSize;
		if (pad == 0)
			return 0;
		return codeSize - pad;
	}

	public static String padMe(String s) {
		return pad(s, checkPad(s));
	}
}
