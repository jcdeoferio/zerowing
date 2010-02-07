package util;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

/**
 * STORE AS HEX!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * @author kevinzana
 * 
 */
public class CharacterManipulator {
	static int codeSize = 4;
	static String charSet = "UTF8";
	static int globalct = 0;

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

		CharacterManipulator.runTests(girlString + girlString + girlString);
		CharacterManipulator.runTests(checkString);
		CharacterManipulator.runTests(shortString);
		CharacterManipulator.runTests(longString);
		CharacterManipulator.runTests(girlString);

		// for(int i=0;i<256;i++){
		// System.out.println("====================================");
		// String s = Integer.toBinaryString(i);
		// System.out.println(s);
		// String out = CharacterManipulator.compressBinary(s);
		// System.out.println("compBinResult: "+out);
		// }

		// System.out.println("====================================");
		// String s = Integer.toBinaryString(127);
		// System.out.println(s);
		// String out = CharacterManipulator.compressBinary(s);
		// System.out.println("compBinResult: "+out);

		// int max = Integer.valueOf("11111111", 2) + 1;
		//
		// String supraText = "";
		// for (int i = 0; i < max; i++) {
		// String s = Integer.toBinaryString(i);
		// supraText = supraText + padTo(s, 8);
		// }
		// System.out.println(supraText);
		// String res = compressBinary_beta3(supraText);
		// System.out.println(res + " "+res.length());
		// System.out.println(extractTo8(supraText));
		// String dec = decompressToBinary_beta(res);
		//
		// System.out.println(extractTo8(dec));
		// System.out.println(dec);
		// System.out.println(supraText.compareTo(dec) +
		// " "+supraText.equals(dec));
	}

	public static String extractTo8(String src) {
		String out = "";
		// src = pad(src,checkPad(src));
		for (int i = 0; i < src.length(); i = i + 8) {
			out = out + src.substring(i, i + 8) + " ";
		}
		return out;
	}

	public static void runTests(String s) {
		System.out.println("==================================  ");
		Huffman hm = new Huffman();
		HuffmanResultTuple hrt = hm.getCoding(s);

		String tTreeInfo = hrt.toData;
		String tEncodedText = hrt.encode();
		String binCompressionText = CharacterManipulator
				.compressBinary_beta3(tEncodedText);

		System.out.println("src: (" + s.length() + ") " + s);
		System.out.println("hman: " + hrt);
		System.out.println("tTreeInfo: (" + tTreeInfo.length() + ")"
				+ tTreeInfo);

		// System.out.println("tCode: [" + tEncodedText+"]");
		System.out.print("tCode: ");
		for (int i = 0; i < tEncodedText.length(); i++)
			System.out.print(tEncodedText.charAt(i) + ".");
		System.out.println();

		System.out.println("tTrueCode: (" + binCompressionText.length() + ") "
				+ binCompressionText);

		int total = tTreeInfo.length() + binCompressionText.length();

		System.out.println("Total: " + total);
		System.out.println("Ratio: " + (double) total / (double) s.length());

		Huffman hm2 = new Huffman();
		HuffEnt he2 = hm2.toTree(tTreeInfo);

		// =================== DECOOODE!
		String newBinary = decompressToBinary_beta(binCompressionText);
		System.out.println("newBinary: " + newBinary);
		System.out.println("same?" + (newBinary.equals(tEncodedText)));

		// for (int i = 0; i < newBinary.length(); i = i + 8) {
		// String sub = newBinary.substring(i, i + 8);
		// System.out.println(sub + " " + binCompressionText.charAt(i / 8));
		// }

	}

	public static String compressBinary(String s) {
		int padding = checkPad(s);
		String src = pad(s, padding);
		String out = "";

		for (int i = 0; i < src.length(); i = i + 4) {
			String binary = src.substring(i, i + 4);

			int value = Integer.parseInt(binary, 2);
			String hex = Integer.toHexString(value);
			// System.out.println(hex);
			out = out + hex;
		}
		return out;
	}

	public static String decompressToBinary(String bin) {
		return null;
	}

	// LONG contains double the possible space that INT can contain.
	// we can modify the code to support 16 bit compression,
	// potentially cutting the size in half.
	// (this is possible because chars actually contain more information
	// than we are currently using.)
	// TODO: implement 16 bit compression.

	private static char toChar(String length8) {
		return (char) Integer.parseInt(length8, 2);
	}

	/**
	 * This is used to convert characters to binary code - ideally after
	 * transfer over a network.
	 * 
	 * @param c
	 * @return
	 */
	private static String toCode(char c) {
		String s = Integer.toBinaryString((int) c);
		while (s.length() < codeSize) {
			s = '0' + s;
		}
		return s;
	}

	public static String compressBinary_beta3(String bin) {
		codeSize = 8;
		// int pad = bin.length() % codeSize;
		// int toPad = checkPad(bin);
		String out = "";
		System.out.println("binLen: " + bin.length());
		int padLen = checkPad(bin);
		bin = pad(bin, padLen);
		System.out.println("binLen2: " + bin.length());
		// System.out.println("src: "+bin);
		// System.out.println("compBin: ");
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
		// System.out.println();

		return padLen + out;
	}

	public static String compressBinary_beta2(String bin) {
		codeSize = 8;
		int pad = bin.length() % codeSize;
		int toPad = checkPad(bin);
		String out = "";
		System.out.println("length: " + bin.length() + " pad needed: " + toPad
				+ " = " + (bin.length() + toPad));
		if (toPad != 0) {
			StringBuilder sb = new StringBuilder();
			pad = codeSize - pad;
			for (int i = 0; i < pad; i++) {
				sb.append("0");
			}
			bin = sb.toString() + bin;
		}

		byte[] bits = new byte[bin.length()];
		System.out.println("src: " + bin);
		System.out.println("compBin: ");
		for (int i = 0; i < (bin.length()); i = i + codeSize) {
			String temp = bin.substring(i, i + codeSize);
			System.out.println(temp);
			Integer in = Integer.parseInt(temp, 2);
			bits[i] = new Byte(String.valueOf(in));
			// System.out.print(bits[i]+".");
			String utf;

			try {
				utf = new String(new byte[] { bits[i] }, charSet);
			} catch (UnsupportedEncodingException e) {
				utf = null;
				JOptionPane.showMessageDialog(null, "ERROR: "
						+ e.getLocalizedMessage() + " for " + temp);
				e.printStackTrace();
			}
			System.out.println(temp + " " + bits[i] + " char:[" + utf + "] "
					+ utf.length());

			// CHECK if NEWLINE!!
			String tester = temp;
			if (tester.equals("00001101")) {
				utf = "#n";
			} else if (tester.equals("00001010")) {
				utf = "#o";
			} else if (utf.equals("#")) {
				utf = "##";
			}
			out = out + utf;

			if (utf.length() < 1) {
				System.exit(1);
			}
		}
		System.out.println();
		// String s =

		return out;
	}

	public static String decompressToBinary_beta(String chars) {
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

	/**
	 * this compresses binary code by exploiting the large size of the Java char
	 * data type - by grabbing 8 1's and 0's, we can construct a char
	 * 
	 * @param bin
	 * @return
	 */
	public static String compressBinary_beta(String bin) {

		int pad = bin.length() % codeSize;
		String out = "";
		// System.out.println(bin.length() + " " +pad);
		if (pad != 0) {
			StringBuilder sb = new StringBuilder();
			pad = codeSize - pad;
			for (int i = 0; i < pad; i++) {
				sb.append("0");
			}
			bin = sb.toString() + bin;
		}
		int max = bin.length() / codeSize;
		System.out.println(bin + " " + max);
		for (int i = 0; i < max; i++) {
			String s = bin.substring(i * codeSize, (i * codeSize) + codeSize);
			char c = toChar(s);

			if (!s.equals("00001101")) {
				out = out + c;
				if (c == '#')
					System.out.println(i + ": " + s + ": " + c + c);
				else
					System.out.println(i + ": " + s + ": " + c);
			} else {
				out = out + "#n";
				System.out.println(i + ": " + s + ": #n");
			}

			if (c == '#') {
				out = out + c;
			}

		}

		return out;
	}

	public static void runTests() {
		char c = 'a';
		String code = toCode(c);
		char c2 = toChar(code);
		System.out.println(c2);

		char test = toChar("00000000");
		String testCode = toCode(test);
		char res = toChar(testCode);

		long ct = 0;
		System.out.println(Integer.parseInt("11111111", 2));
		System.out.println("largest int: " + Integer.MAX_VALUE);

		long startTime = System.currentTimeMillis();
		long max = 2 * (long) Integer.MAX_VALUE;
		while (test == res && ct < max) {
			test++;
			testCode = toCode(test);
			res = toChar(testCode);
			// System.out.print(ct++ + " ");
			// System.out.println(test==res);]
			int chunk = 10000000;

			if (ct % chunk == 0) {
				long endTime = System.currentTimeMillis();
				long dur = endTime - startTime;
				if (dur == 0)
					dur = 1;
				startTime = endTime;
				long speed = chunk / dur;

				long rem = (max - ct) / speed;
				rem = rem / 1000;
				System.out.println(ct + " " + (test == res) + " " + speed + " "
						+ rem);

			}
			ct++;
		}

		System.out.println("ended at " + ct);
		// 2 147 483 647

	}
}
