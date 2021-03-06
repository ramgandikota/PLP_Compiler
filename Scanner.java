/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
  */

package cop5556fa17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Scanner {

	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {

		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}

		public int getPos() {
			return pos;
		}

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, KW_image/* image */, KW_int/* int */, KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}

	public static enum State {

		START, GOT_EQUAL, GOT_STAR, GOT_LT, GOT_GT, GOT_NEQ, STR_LIT, NUM_LIT, COMMENT, IDENT_START, GOT_MINUS, GOT_BS, EscChar;

	}

	/**
	 * Class to represent Tokens.
	 * 
	 * This is defined as a (non-static) inner class which means that each Token
	 * instance is associated with a specific Scanner instance. We use this when
	 * some token methods access the chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			} else
				return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the enclosing "
		 * characters and convert escaped characters to the represented
		 * character. For example the two characters \ t in the char array
		 * should be converted to a single tab character in the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial
																// and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); // for completeness, line termination
											// chars not allowed in String
											// literals
						break;
					case 'n':
						sb.append('\n'); // for completeness, line termination
											// chars not allowed in String
											// literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition: This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length) + "," + pos + "," + length + "," + line
					+ "," + pos_in_line + "]";
		}

		/**
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object is the same
		 * class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is associated
		 * with.
		 * 
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/**
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.
	 */
	static final char EOFchar = 0;

	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;

	/**
	 * An array of characters representing the input. These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;
	HashMap<String, Kind> keywordMap;
	HashMap<String, String> booleanMap;

	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input
																				// string
																				// terminated
																				// with
																				// null
																				// char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
		keywordMap = new HashMap<>();
		booleanMap = new HashMap<>();
		initDatastructures();

	}

	public Scanner initDatastructures() {

		// HashMap of Keywords in the system
		keywordMap.put("x", Kind.KW_x);
		keywordMap.put("y", Kind.KW_y);
		keywordMap.put("X", Kind.KW_X);
		keywordMap.put("Y", Kind.KW_Y);
		keywordMap.put("r", Kind.KW_r);
		keywordMap.put("R", Kind.KW_R);
		keywordMap.put("a", Kind.KW_a);
		keywordMap.put("A", Kind.KW_A);
		keywordMap.put("Z", Kind.KW_Z);
		keywordMap.put("DEF_X", Kind.KW_DEF_X);
		keywordMap.put("DEF_Y", Kind.KW_DEF_Y);
		keywordMap.put("SCREEN", Kind.KW_SCREEN);
		keywordMap.put("cart_x", Kind.KW_cart_x);
		keywordMap.put("cart_y", Kind.KW_cart_y);
		keywordMap.put("polar_a", Kind.KW_polar_a);
		keywordMap.put("polar_r", Kind.KW_polar_r);
		keywordMap.put("abs", Kind.KW_abs);
		keywordMap.put("sin", Kind.KW_sin);
		keywordMap.put("cos", Kind.KW_cos);
		keywordMap.put("atan", Kind.KW_atan);
		keywordMap.put("log", Kind.KW_log);
		keywordMap.put("image", Kind.KW_image);
		keywordMap.put("int", Kind.KW_int);
		keywordMap.put("boolean", Kind.KW_boolean);
		keywordMap.put("url", Kind.KW_url);
		keywordMap.put("file", Kind.KW_file);

		// boolean words
		booleanMap.put("true", "true");
		booleanMap.put("false", "false");

		return null;

	}

	// Reusable function for creating Numbers and Strings
	public String makeTokens(int startPos, int pos) throws LexicalException {

		StringBuilder sb = new StringBuilder();
		for (int i = startPos; i < pos; i++) {
			sb.append(chars[i]);
		}

		return sb.toString();

	}

	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		int startPos = 0;
		State state = State.START;
		int length = chars.length;

		while (pos < length) {
			char token = chars[pos];
			switch (state) {

			case START:

				// Check for whitespace
				if (Character.isWhitespace(token)) {
					pos++;
					posInLine = posInLine + (pos - startPos);
					startPos = pos;
					if (token == '\n') {
						line++;
						posInLine = 1;
					} else if (token == '\r') {
						if (chars[pos] != '\n') {
							line++;
							posInLine = 1;
						}

					}
					break;
				}
				// Increment the value of posInLine by the length of the token -
				// This is done for all the cases in the Start state only once
				// Save the startPos of the tokens that are being created
				posInLine = posInLine + (pos - startPos);
				startPos = pos;
				
				switch (token) {

					// Separators
					case ',':
						tokens.add(new Token(Kind.COMMA, startPos, 1, line, posInLine));
						break;
					case ';':
						tokens.add(new Token(Kind.SEMI, startPos, 1, line, posInLine));
						break;
					case ')':
						tokens.add(new Token(Kind.RPAREN, startPos, 1, line, posInLine));
						break;
					case '(':
						tokens.add(new Token(Kind.LPAREN, startPos, 1, line, posInLine));
						break;
					case ']':
						tokens.add(new Token(Kind.RSQUARE, startPos, 1, line, posInLine));
						break;
					case '[':
						tokens.add(new Token(Kind.LSQUARE, startPos, 1, line, posInLine));
						break;

					// Single Char Operators might need to handle the && and ||
					// operators
					case '&':
						tokens.add(new Token(Kind.OP_AND, startPos, 1, line, posInLine));
						break;
					case '|':
						tokens.add(new Token(Kind.OP_OR, startPos, 1, line, posInLine));
						break;
					case '+':
						tokens.add(new Token(Kind.OP_PLUS, startPos, 1, line, posInLine));
						break;
					case '?':
						tokens.add(new Token(Kind.OP_Q, startPos, 1, line, posInLine));
						break;
					case ':':
						tokens.add(new Token(Kind.OP_COLON, startPos, 1, line, posInLine));
						break;
					case '@':
						tokens.add(new Token(Kind.OP_AT, startPos, 1, line, posInLine));
						break;
					case '%':
						tokens.add(new Token(Kind.OP_MOD, startPos, 1, line, posInLine));
						break;

					// Comments and Div tag
					case '/': {
						state = State.GOT_BS;
						break;
					}

					// Cases where operators can be followed by other operators
					case '-': {
						state = State.GOT_MINUS;
						break;
					}
					case '*': {
						state = State.GOT_STAR;
						break;

					}
					case '=': {
						state = State.GOT_EQUAL;
						break;
					}
					case '<': {
						state = State.GOT_LT;
						break;
					}
					case '>': {
						state = State.GOT_GT;
						break;
					}
					case '!': {
						state = State.GOT_NEQ;
						break;
					}

					case '"':
						state = State.STR_LIT;
						break;
						
					case EOFchar:
						break;
						
					default:
						
						// Identifier Start
						if (Character.isAlphabetic(token) || token == '_' || token == '$') {
							state = State.IDENT_START;

						} else if (token == '0') {
							tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, 1, line, posInLine));
							
						} else if (Character.isDigit(token)) {

							state = State.NUM_LIT;

					} else{
						throw new LexicalException("Invalid input to the start state", pos);
						}
					}
			
				pos++;
				break;

			case GOT_MINUS:

				if (token == '>') {
					pos++;
					tokens.add(new Token(Kind.OP_RARROW, startPos, pos - startPos, line, posInLine));

				} else {
					tokens.add(new Token(Kind.OP_MINUS, startPos, pos - startPos, line, posInLine));

				}
				state = State.START;
				break;

			case GOT_EQUAL:

				if (token == '=') {
					pos++;
					tokens.add(new Token(Kind.OP_EQ, startPos, pos - startPos, line, posInLine));

				} else {
					tokens.add(new Token(Kind.OP_ASSIGN, startPos, pos - startPos, line, posInLine));

				}
				state = State.START;
				break;

			case GOT_LT:
				if (token == '=') {
					pos++;
					tokens.add(new Token(Kind.OP_LE, startPos, pos - startPos, line, posInLine));

				} else if (token == '-') {
					pos++;
					tokens.add(new Token(Kind.OP_LARROW, startPos, pos - startPos, line, posInLine));

				} else {

					tokens.add(new Token(Kind.OP_LT, startPos, pos - startPos, line, posInLine));
				}
				state = State.START;
				break;

			case GOT_GT:
				if (token == '=') {
					pos++;
					tokens.add(new Token(Kind.OP_GE, startPos, pos - startPos, line, posInLine));

				} else {
					tokens.add(new Token(Kind.OP_GT, startPos, pos - startPos, line, posInLine));

				}
				state = State.START;
				break;

			case GOT_NEQ:
				if (token == '=') {
					pos++;
					tokens.add(new Token(Kind.OP_NEQ, startPos, pos - startPos, line, posInLine));

				} else {
					tokens.add(new Token(Kind.OP_EXCL, startPos, pos - startPos, line, posInLine));

				}
				state = State.START;
				break;

			case GOT_STAR:
				if (token == '*') {
					pos++;
					tokens.add(new Token(Kind.OP_POWER, startPos, pos - startPos, line, posInLine));

				} else {
					tokens.add(new Token(Kind.OP_TIMES, startPos, pos - startPos, line, posInLine));

				}
				state = State.START;
				break;

			case IDENT_START:

				if (Character.isAlphabetic(token) || Character.isDigit(token) || token == '$' || token == '_') {
					pos++;
				} else {
					String str = makeTokens(startPos, pos);

					if (keywordMap.containsKey(str)) {
						Kind kindIs = keywordMap.get(str);
						tokens.add(new Token(kindIs, startPos, pos - startPos, line, posInLine));
					} else if (booleanMap.containsKey(str)) {
						tokens.add(new Token(Kind.BOOLEAN_LITERAL, startPos, pos - startPos, line, posInLine));
					} else {
						tokens.add(new Token(Kind.IDENTIFIER, startPos, pos - startPos, line, posInLine));
					}
					state = State.START;
				}
				break;

			case NUM_LIT:
				if (Character.isDigit(token)) {
					pos++;
				} else {
					try {
						Token newToken = new Token(Kind.INTEGER_LITERAL, startPos, pos - startPos, line, posInLine);
						@SuppressWarnings("unused")
						int intNumber = newToken.intVal();

						tokens.add(newToken);
						state = State.START;
						break;
					} catch (NumberFormatException e) {
						throw new LexicalException("Out of range of Java int", startPos);
					}
				}
				break;

			case GOT_BS:
				if (token == '/') {
					state = State.COMMENT;
					pos++;
				} else {
					tokens.add(new Token(Kind.OP_DIV, startPos, pos - startPos, line, posInLine));
					state = State.START;
				}
				break;

			case COMMENT:
				if (token == '\n' || token == '\r') {
					state = State.START;

				} else {
					pos++;
					state = State.COMMENT;
				}
				break;

			case STR_LIT:

				if (token != '"') {
					if (token == '\n' || token == '\r') {
						throw new LexicalException("Invalid Input of \n \r to the String Literal", pos);
					} else if (token == EOFchar) {
						throw new LexicalException("End of input reached without proper end to String Literal", pos);
					} else if (token == '\\') {
						state = State.EscChar;
					}
					pos++;
				} else {
					pos++;
					tokens.add(new Token(Kind.STRING_LITERAL, startPos, pos - startPos, line, posInLine));
					state = State.START;

				}
				break;

			case EscChar:
				try {
					if (token == 'n' | token == 't' | token == 'f' | token == 'r' | token == 'b' | token == '"'
							| token == '\'' | token == '\\') {

						state = State.STR_LIT;
					} else {
						throw new LexicalException("Unknown input", pos);
					}
					pos++;
				} catch (Exception e) {
					throw new LexicalException(e.getMessage(), pos);
				}
				break;

			default:
				throw new LexicalException("State is invalid", pos);
			}
			if (token == EOFchar)
				break;
		}
		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;
	}

	/**
	 * Returns true if the internal iterator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that the next
	 * call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}

	/**
	 * Returns the next Token, but does not update the internal iterator. This
	 * means that the next call to nextToken or peek will return the same Token
	 * as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}

	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
