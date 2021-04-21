package arkintelbot.arkutilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.BiConsumer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import killzonebot.Main;

public class ClientPref 
{
	private static final File file = new File("client.pref");
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private static JsonObject clientPref = Json.createObjectBuilder().build();
	
	static
	{
		initialise();
		try
		{
			
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(1);
		}
	}
	
	private static void initialise()
	{
		if (!file.exists())
		{
			try 
			{
				file.createNewFile();
			} 
			catch (IOException e) 
			{
				logger.error(e.getLocalizedMessage());
			}
		}
		else
		{
			if (file.length() == 0)
			{
				return;
			}
			
			try 
			{
				clientPref = Json.createReader(new FileReader(file)).readObject();
			} 
			catch (Exception e) 
			{
				logger.error(e.getLocalizedMessage());
			}
		}
	}
	
	private static void put(String id, HashMap<String, String> keyValues)
	{
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		if (clientPref.size() == 0)
		{
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			keyValues.forEach((k,v) -> jsonObjBuilder.add(k, v));
			jsonBuilder.add(id, jsonObjBuilder.build());
			clientPref = jsonBuilder.build();
			return;
		}
		
		clientPref.forEach(new BiConsumer<String, JsonValue>(){

			@Override
			public void accept(String arg0, JsonValue arg1) 
			{
				if (arg0.equals(id))
				{
					JsonObject prefObj = arg1.asJsonObject();
					JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
					prefObj.forEach((k,v) -> jsonObjBuilder.add(k,v));
					keyValues.forEach((k,v) -> jsonObjBuilder.add(k, v));
					jsonBuilder.add(arg0, jsonObjBuilder.build());
					return;
				}
				
				jsonBuilder.add(arg0, arg1);
			}
			
		});
		
		clientPref = jsonBuilder.build();
	}
	
	private static void writePrefs(String id, HashMap<String, String> keyValues)
	{
		put(id, keyValues);
		JsonWriter writer;
		try 
		{
			writer = Json.createWriter(new FileOutputStream(file));
			writer.writeObject(clientPref);
			writer.close();
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	/**
	 * Adds or replaces a preference.
	 * @param id
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public static void AddPref(String id, String key, String value)
	{
		HashMap<String, String> keyValues = new HashMap<String, String>();
		keyValues.put(key, value);
		AddPrefs(id, keyValues);
	}
	
	/**
	 * Adds or replaces multiple preferences.
	 * @param id
	 * @param keyValues
	 * @throws IOException
	 */
	public static void AddPrefs(String id, HashMap<String, String> keyValues)
	{
		writePrefs(id, keyValues);
	}
	
	public static Object GetClientPrefs(String id)
	{
		return JsonUtilities.GetAnyConfigValue(clientPref, id);
	}
	
	public static Object GetClientPref(String id, String key)
	{
		return JsonUtilities.GetAnyConfigValue(clientPref, id+":"+key);
	}
}
