/**
 * JsonTokenizer.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.io.*;

/**
 * @author Cameron Behar
 *
 */
public class JsonTokenizer
{
	private int		character;
	private boolean	eof;
	private int		index;
	private int		line;
	private char	previousChar;
	private Reader	reader;
	private boolean	usePrevious;
	
	public JsonTokenizer(String s)
	{
		this.reader = new StringReader(s);
		this.eof = false;
		this.usePrevious = false;
		this.previousChar = 0;
		this.index = 0;
		this.character = 1;
		this.line = 1;
	}
	
	// Back up one character. This provides a sort of lookahead capability,
	// so that you can test for a digit or letter before attempting to parse
	// the next number or identifier.
	public void moveBack()
	{
		if (usePrevious || index <= 0)
			throw new Error("Stepping back two steps is not supported");
		
		this.index -= 1;
		this.character -= 1;
		this.usePrevious = true;
		this.eof = false;
	}
	
	public boolean end()
	{
		return eof && !usePrevious;
	}
	
	// Determine if the source string still contains characters that next()
	// can consume.
	public boolean hasNext()
	{
		nextChar();
		if (end())
			return false;
		
		moveBack();
		return true;
	}
	
	// Get the next character in the source string.
	public char nextChar()
	{
		int c;
		
		if (usePrevious)
		{
			usePrevious = false;
			c = previousChar;
		}
		else
		{
			try { c = reader.read(); }
			catch (IOException exception) { throw new Error(exception); }
			
			if (c <= 0)
			{ // End of stream
				eof = true;
				c = 0;
			}
		}
		
		++index;
		
		if (previousChar == '\r')
		{
			++line;
			character = c == '\n' ? 0 : 1;
		}
		else if (c == '\n')
		{
			++line;
			character = 0;
		}
		else
			++line;
		
		previousChar = (char) c;
		return previousChar;
	}
	
	// Get the next n characters.
	public String nextChars(int n)
	{
		if (n == 0)
			return "";
		
		char[] buffer = new char[n];
		int pos = 0;
		
		while (pos < n)
		{
			buffer[pos] = nextChar();
			if (end()) throw new Error("Substring bounds error at " + index);
			++pos;
		}
		
		return new String(buffer);
	}
	
	// Get the next char in the string, skipping whitespace.
	public char nextNonWhitespaceChar()
	{
		while(true)
		{
			char c = nextChar();
			if (c == 0 || c > ' ') return c;
		}
	}
	
	// Return the characters up to the next close quote character.
	// Backslash processing is done. The formal JSON format does not
	// allow strings in single quotes, but an implementation is allowed to
	// accept them.
	public String nextString(char quote)
	{
		char c;
		StringBuffer sb = new StringBuffer();
		
		while(true)
		{
			c = nextChar();
			switch (c)
			{
				case 0:
				case '\n':
				case '\r':
					throw new Error("Unterminated string at " + index);
				case '\\':
					c = nextChar();
					switch (c)
					{
						case 'b':
							sb.append('\b');
							break;
						case 't':
							sb.append('\t');
							break;
						case 'n':
							sb.append('\n');
							break;
						case 'f':
							sb.append('\f');
							break;
						case 'r':
							sb.append('\r');
							break;
						case 'u':
							sb.append((char) Integer.parseInt(nextChars(4), 16));
							break;
						case '"':
						case '\'':
						case '\\':
						case '/':
							sb.append(c);
							break;
						default:
							throw new Error("Illegal escape at " + index);
					}
					break;
				default:
					if (c == quote)
						return sb.toString();
					sb.append(c);
			}
		}
	}

	// Get the next value. The value can be a Boolean, Double, Integer,
	// JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
	public Object nextValue()
	{
		char c = nextNonWhitespaceChar();
		String s;
		
		switch (c)
		{
			case '"':             return nextString(c);
			case '{': moveBack(); return JsonUtilities.parseObject(this);
			case '[': moveBack(); return JsonUtilities.parseArray(this);
		}
		
		/*
		 * Handle unquoted text. This could be the values true, false, or
		 * null, or it can be a number. An implementation (such as this one)
		 * is allowed to also accept non-standard forms.
		 * 
		 * Accumulate characters until we reach the end of the text or a
		 * formatting character.
		 */

		StringBuffer sb = new StringBuffer();
		while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0)
		{
			sb.append(c);
			c = nextChar();
		}
		moveBack();
		
		s = sb.toString().trim();
		if (s.length() <= 0) throw new Error("Missing value at " + index);
		
		return JsonUtilities.parseValue(s);
	}
}



