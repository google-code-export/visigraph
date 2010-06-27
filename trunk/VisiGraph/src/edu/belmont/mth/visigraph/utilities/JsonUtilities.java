/**
 * JsonUtilities.java
 */
package edu.belmont.mth.visigraph.utilities;

import java.awt.Color;
import java.util.*;
import java.util.Map.*;
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
		return parseObject(new JsonTokenizer(json));
	}
	
	protected static Map<String, Object> parseObject(JsonTokenizer tokenizer)
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
			return new Integer(((Color)o).getRGB()).toString();
		else if(o instanceof Iterable<?>)
			return formatArray((Iterable<?>)o);
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
		{
			Long value = new Long(json);
			return (value.longValue() == value.intValue() ? value : new Integer(value.intValue()));
		}
		else if (json.matches("^[+-]?(\\d+\\.?\\d*|\\d*\\.?\\d+)([eE][+\\-\\x20]?\\d+)?$"))
			return new Double(json);
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
		return parseArray(new JsonTokenizer(json));
	}
	
	protected static Iterable<?> parseArray(JsonTokenizer tokenizer)
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
}






