/**
 * JsonUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import java.util.regex.*;

import edu.belmont.mth.visigraph.models.ObservableBase.*;

/**
 * @author Cameron Behar
 *
 */
public class JsonUtilities
{	
	public static String formatString(String s)
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append('"');
		
		if (s != null && s.length() > 0)
		{
			for (int i = 0; i < s.length(); ++i)
	        {
	        	char c = s.charAt(i);
	        	
				switch (c)
				{
					case '\\': sb.append("\\\\"); break;
					case '"' : sb.append("\\\""); break;
					case '/' : sb.append("\\/");  break;
					case '\b': sb.append("\\b");  break;
					case '\t': sb.append("\\t");  break;
					case '\n': sb.append("\\n");  break;
					case '\f': sb.append("\\f");  break;
					case '\r': sb.append("\\r");  break;
					default:
						if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100'))
						{
							String hex = "000" + Integer.toHexString(c);
							sb.append("\\u" + hex.substring(hex.length() - 4));
						}
						else
							sb.append(c);
				}
	        }
		}
		
		sb.append('"');
		
		return sb.toString();
	}
	
	public static String parseString(String json)
	{
        StringBuffer sb = new StringBuffer();
        
        for (int i = 1; i < json.length() - 1; ++i)
        {
        	char c = json.charAt(i);
        	
            switch (c)
            {
            	case 0:    throw new IllegalArgumentException("Illegal character");
            	case '\n': throw new IllegalArgumentException("Illegal character");
            	case '\r': throw new IllegalArgumentException("Illegal character");
	            case '\\':
	                c = json.charAt(++i);
	                switch (c)
	                {
	                	case 'b':  sb.append('\b'); break;
	                	case 't':  sb.append('\t'); break;
	                	case 'n':  sb.append('\n'); break;
	                	case 'f':  sb.append('\f'); break;
	                	case 'r':  sb.append('\r'); break;
	                	case 'u':  sb.append((char)Integer.parseInt(json.substring(i += 1, i += 4), 16)); break;
	                	case '"':  sb.append('"');  break;
	                	case '\'': sb.append('\''); break;
	                	case '\\': sb.append('\\'); break;
	                	case '/':  sb.append('/');  break;
	                	default:
	                		throw new IllegalArgumentException("Illegal escape");
	                }
	                break;
	            default:
	                sb.append(c);
            }
        }
        
        return sb.toString();
	}
	
	public static String formatObject(Map<String, Object> members)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("{ ");
		
		for(Entry<String, Object> member : members.entrySet())
			sb.append(formatString(member.getKey()) + " : " + formatValue(member.getValue()) + ", ");
		
		sb.replace(sb.length() - 2, sb.length(), " }");
		
		return sb.toString();
	}
	
	public static Map<String, Object> parseObject(String json)
	{
		return parseObject(new JsonScanner(json));
	}
	
	private static Map<String, Object> parseObject(JsonScanner tokenizer)
	{
		Map<String, Object> members = new HashMap<String, Object>();
		
		char c;
		String key;
		
		if (tokenizer.nextNonWhitespaceChar() != '{')
			throw new Error("A JSON object's text must begin with '{'");
		
		while (true)
		{
			c = tokenizer.nextNonWhitespaceChar();
			
			switch (c)
			{
				case 0: throw new Error("A JSON object's text must end with '}'");
				case '}': return members;
				default: tokenizer.moveBack(); key = tokenizer.nextValue().toString();
			}
			
			/*
			 * The key is followed by ':'.
			 */

			c = tokenizer.nextNonWhitespaceChar();
			if (c != ':')
				throw new Error("Expected ':' after key");
			
			
			// Put once
			
			Object value = tokenizer.nextValue();
			if (key != null && value != null)
			{
				if (members.containsKey(key))
					throw new Error("Duplicate key \"" + key + "\"");
				
				members.put(key, value);
			}
			
			/*
			 * Pairs are separated by ','. We will also tolerate ';'.
			 */

			switch (tokenizer.nextNonWhitespaceChar())
			{
				case ';':
				case ',':
					if (tokenizer.nextNonWhitespaceChar() == '}')
						return members;
					
					tokenizer.moveBack();
					break;
				case '}':
					return members;
				default:
					throw new Error("Expected ',' or '}'");
			}
		}
	}
	
	public static String formatValue(Object o)
	{
		if(o instanceof String)
			return formatString((String)o);
		else if(o instanceof Property<?>)
			return formatValue(((Property<?>)o).get());
		else if(o instanceof Number)
			return o.toString();
		else if(o instanceof Boolean)
			return o.toString();
		else if(o instanceof Color)
			return formatColor((Color)o);
		else if(o instanceof Iterable<?>)
			return formatArray((Iterable<?>)o);
		else if(o instanceof UUID)
			return formatString(((UUID)o).toString());
		else if(o == null)
			return "null";
		else
			return o.toString();
	}
	
	public static Object parseValue(String json)
	{
		if (json.matches("^\".*\"$"))
			return json.substring(1, json.length() - 1);
		else if (json.matches("^\\[.*\\]$"))
			return parseArray(json);
		else if (json.equalsIgnoreCase("true"))
			return Boolean.TRUE;
		else if (json.equalsIgnoreCase("false"))
			return Boolean.FALSE;
		else if (json.equalsIgnoreCase("null"))
			return null;
		else if (json.matches("^[+-]?\\d+$"))
			return new Integer(json);
		else if (json.matches("^[+-]?(\\d+\\.?\\d*|\\d*\\.?\\d+)([eE][+\\-\\x20]?\\d+)?$"))
			return new Double(json);
		else if(json.matches("^\\#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$"))
			return parseColor(json);
		else 
			throw new IllegalArgumentException("Illegal value");
	}
	
	public static String formatArray(Iterable<?> i)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[ ");
		
		for(Object o : i)
			sb.append(formatValue(o) + ", ");
		
		if(sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		
		sb.append(" ]");
		
		return sb.toString();
	}
	
	public static Iterable<?> parseArray(String json)
	{
		return parseArray(new JsonScanner(json));
	}
	
	private static Iterable<?> parseArray(JsonScanner tokenizer)
	{
		List<Object> items = new Vector<Object>();
		
		char c = tokenizer.nextNonWhitespaceChar();
		
		if (c != '[')
			throw new Error("A JSON array's text must begin with '['");
		
		if (tokenizer.nextNonWhitespaceChar() == ']')
			return items;
		
		tokenizer.moveBack();
		while (true)
		{
			c = tokenizer.nextNonWhitespaceChar();
			tokenizer.moveBack();
			items.add(c == ',' ? null : tokenizer.nextValue());

			c = tokenizer.nextNonWhitespaceChar();
			
			if(c == ',')
			{
				if (tokenizer.nextNonWhitespaceChar() == ']')
					return items;
				
				tokenizer.moveBack();
			}
			else if (c==']')
				return items;
			else
				throw new Error("Expected ',' or ']'");
		}
	}

	private static String formatColor(Color color)
	{
		String rHex = "0" + Integer.toHexString(color.getRed());
		String gHex = "0" + Integer.toHexString(color.getGreen());
		String bHex = "0" + Integer.toHexString(color.getBlue());
		String aHex = "0" + Integer.toHexString(color.getAlpha());
		return "#" + rHex.substring(rHex.length() - 2) + 
					 gHex.substring(gHex.length() - 2) + 
					 bHex.substring(bHex.length() - 2) + 
					 aHex.substring(aHex.length() - 2);
	}
	
	private static Color parseColor(String json)
	{
		Pattern pattern = Pattern.compile("^\\#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})$");
		Matcher matcher = pattern.matcher(json);
		matcher.find();

		Integer r = Integer.parseInt(matcher.group(1), 16);
		Integer g = Integer.parseInt(matcher.group(2), 16);
		Integer b = Integer.parseInt(matcher.group(3), 16);
		Integer a = Integer.parseInt(matcher.group(4), 16);
		
		return new Color(r.intValue(), g.intValue(), b.intValue(), a.intValue());
	}
	
	private static class JsonScanner
	{
		private int		character;
		private boolean	eof;
		private int		index;
		private int		line;
		private char	previousChar;
		private Reader	reader;
		private boolean	usePrevious;
		
		public JsonScanner(String s)
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
			while (c >= ' ' && ",:]}/\\\"[{;=".indexOf(c) < 0)
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
}






