package com.sugarcubes.myglucose.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtilities
{


	public static HashMap<String, String> toMap( JSONObject object ) throws JSONException
	{
		HashMap<String, String> map = new HashMap<>();
		try
		{
			Iterator<String> keysItr = object.keys();
			while( keysItr.hasNext() )
			{
				String key = keysItr.next();
				Object value = object.get( key );

				if( value instanceof JSONObject )
				{
					value = toMap( (JSONObject) value );
				}
				else if( value instanceof JSONArray )
				{
					JSONArray jsonArray = (JSONArray) value;
					//				for( int i = 0; i < ((HashMap<String, String>) value).size(); i++ )
					//					value = toMap( ((HashMap<String, String>) value).get( i ) );
					for( int i = 0; i < jsonArray.length(); i++ )    // Iterate
						value = toMap( jsonArray.getJSONObject( i ) );

				} // if

				map.put( key, value.toString() );

			} // while

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return map;

	} // toMap


	public static JSONObject mapToJson(Map<?, ?> data)
	{
		JSONObject object = new JSONObject();

		for (Map.Entry<?, ?> entry : data.entrySet())
		{
			/*
			 * Deviate from the original by checking that keys are non-null and
			 * of the proper type. (We still defer validating the values).
			 */
			String key = (String) entry.getKey();
			if (key == null)
			{
				throw new NullPointerException("key == null");
			}
			try
			{
				object.put(key, wrap(entry.getValue()));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return object;
	}


	public static JSONArray collectionToJson(Collection data)
	{
		JSONArray jsonArray = new JSONArray();
		if (data != null)
		{
			for (Object aData : data)
			{
				jsonArray.put(wrap(aData));
			}
		}
		return jsonArray;
	}


	public static JSONArray arrayToJson(Object data) throws JSONException
	{
		if (!data.getClass().isArray())
		{
			throw new JSONException("Not a primitive data: " + data.getClass());
		}
		final int length = Array.getLength(data);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < length; ++i)
		{
			jsonArray.put(wrap(Array.get(data, i)));
		}

		return jsonArray;
	}


	private static Object wrap(Object o)
	{
		if (o == null)
		{
			return null;
		}
		if (o instanceof JSONArray || o instanceof JSONObject)
		{
			return o;
		}
		try
		{
			if (o instanceof Collection)
			{
				return collectionToJson((Collection) o);
			}
			else if (o.getClass().isArray())
			{
				return arrayToJson(o);
			}
			if (o instanceof Map)
			{
				return mapToJson((Map) o);
			}
			if (o instanceof Boolean ||
					o instanceof Byte ||
					o instanceof Character ||
					o instanceof Double ||
					o instanceof Float ||
					o instanceof Integer ||
					o instanceof Long ||
					o instanceof Short ||
					o instanceof String)
			{
				return o;
			}
			if (o.getClass().getPackage().getName().startsWith("java."))
			{
				return o.toString();
			}
		}
		catch (Exception ignored)
		{
		}
		return null;
	}

} // class