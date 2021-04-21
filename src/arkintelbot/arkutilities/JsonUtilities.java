package arkintelbot.arkutilities;

import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class JsonUtilities 
{
	public static Object GetAnyConfigValue(JsonObject jsonObj, String keyPath)
	{
		if (jsonObj == null || keyPath == null)
		{
			return null;
		}
		
		String keys[] = keyPath.split(":");
		JsonValue parent = null;
		if (keys.length == 1)
		{
			return convertToPrimitive(jsonObj.get(keys[0]));
		}
		
		parent = jsonObj;
		for (int i = 0; i < keys.length; i++)
		{
			if (parent == null)
			{
				return null;
			}
			
			if (parent.getValueType() != JsonValue.ValueType.OBJECT)
			{
				return null;
			}
			
			parent = ((JsonObject)parent).get(keys[i]);
		}
		
		return convertToPrimitive(parent);
	}
	
	private static Object convertToPrimitive(JsonValue parent)
	{
		if (parent == null)
		{
			return null;
		}
		
		if (parent.getValueType() == JsonValue.ValueType.STRING)
		{
			return ((JsonString)parent).getString();
		}
		else if (parent.getValueType() == JsonValue.ValueType.OBJECT)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			((JsonObject)parent).forEach((k,v) -> map.put(k, convertToPrimitive(v)));
			return map;
		}
		else if (parent.getValueType() == JsonValue.ValueType.NUMBER)
		{
			return ((JsonNumber)parent).longValue();
		}
		else if (parent.getValueType() == JsonValue.ValueType.ARRAY)
		{
			return ((JsonArray)parent).toArray();
		}
		else if (parent.getValueType() == JsonValue.ValueType.FALSE)
		{
			return false;
		}
		else if (parent.getValueType() == JsonValue.ValueType.TRUE)
		{
			return true;
		}
		
		return null;
	}
}
