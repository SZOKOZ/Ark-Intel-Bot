package arkintelbot;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.faces.io.Base64InputStream;
import com.sun.xml.ws.security.opt.impl.util.Base64OutputStream;

import arkintelbot.arkutilities.JsonUtilities;

public class ConfigLoader 
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
	
	boolean newFile = false;
	private File defaultPath = new File("config.json");
	private JsonObject config;
	
	public ConfigLoader(boolean masterConfig) throws IOException
	{
		if (!masterConfig)
		{
			ReadSlaveConfig();
			return;
		}
		
		if (!defaultPath.exists())
		{
			defaultPath.createNewFile();
			newFile = true;
		}
		
		if (newFile)
		{
			JsonObject tempJsonObject = Json.createObjectBuilder()
				.add("serverName", "SERVERIP:GAMEPORT:(optional)RCONPORT")
				.build();
		
			JsonObject tempJsonObject2 = Json.createObjectBuilder()
					.add("channelName", "CHANNELID")
					.build();
			
			config = Json.createObjectBuilder()
					.add("botToken", "BOTTOKEN")
					.add("license", "LICENSE")
					.add("licensingChannel", "CHANNELID")
					.add("tribeInputChannel", "CHANNELID")
					.add("allyInputChannel", "CHANNELID")
					.add("enemyInputChannel", "CHANNELID")
					.add("liveIntelChannels", tempJsonObject2)
					.add("enemyalertchannel", "CHANNELID")
					.add("battlemetricsChannel", "CHANNELID")
					.add("servers", tempJsonObject)
					.build();
			
			JsonWriter jsonWriter = Json.createWriter(new FileWriter(defaultPath));
			jsonWriter.writeObject(config);
			jsonWriter.close();
		}
		else
		{
			config = Json.createReader(new FileReader(defaultPath)).readObject();
		}
	}
	
	public boolean IsBlankConfig()
	{
		return newFile;
	}
	
	public Object GetAnyConfigValue(String keyPath)
	{
		return JsonUtilities.GetAnyConfigValue(config, keyPath);
	}
	
	public void SpawnSlaveConfig(String license)
	{
		File slave = new File(license+".slave.json");
		try 
		{
			slave.createNewFile();
		} 
		catch (IOException e1) 
		{
			logger.error(e1.getLocalizedMessage());
		}
		
		JsonObject slaveConfig = Json.createObjectBuilder()
				.add("botToken", config.get("botToken"))
				.add("license", license)
				.add("licensingChannel", config.get("licensingChannel"))
				.add("tribeInputChannel", config.get("tribeInputChannel"))
				.add("allyInputChannel", config.get("allyInputChannel"))
				.add("enemyInputChannel", config.get("enemyInputChannel"))
				.add("liveIntelChannels", config.get("liveIntelChannels"))
				.add("enemyalertchannel", config.get("enemyalertchannel"))
				.add("servers", config.get("servers"))
				.build();
		
		try 
		{
			Base64OutputStream b64 = new Base64OutputStream(new FileOutputStream(slave));
			b64.write(slaveConfig.toString().getBytes());
			b64.close();
		} 
		catch (IOException e) 
		{
			logger.error(e.getLocalizedMessage());
		}
	}
	
	private void ReadSlaveConfig() throws IOException, JsonException
	{
		File dir = new File("");
		if (dir.isDirectory())
		{
			File slaveConfigs[] = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File arg0) 
				{
					if (arg0.getName().endsWith(".slave.json"))
					{
						return true;
					}
					
					return false;
				}
				
			});
			
			if (slaveConfigs == null)
			{
				return;
			}
			
			if (slaveConfigs.length == 0)
			{
				return;
			}
			
			byte[] b = new byte[(int) slaveConfigs[0].length()];
			FileInputStream fis = new FileInputStream(slaveConfigs[0]);
			fis.read(b);
			fis.close();
			config = Json.createReader(new Base64InputStream(new String(b).trim())).readObject();
		}
	}
}
