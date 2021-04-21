package arkintelbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;

import arkintelbot.arkutilities.JsonUtilities;
import gameme.GameMe_Exception;

public class BattleMetrics 
{
	private static String urlFormat = "https://www.battlemetrics.com/servers/search?"
			+ "sort=score&q=server%d";
	private static String searchFilter = "&game=ark&features%5B2e079b9a-d6f7-11e7-8461-83e84cedb373%5D=true";
	private JsonObject jsonCache = null;
	
	private Object GetSpecificServerInfo(String link)
	{
		Pattern idPatt = Pattern.compile("\\d+");
		Matcher matcher = idPatt.matcher(link);
		String id = null;
		if (matcher.find())
		{
			id = matcher.group();
		}
		else
		{
			return null;
		}
			
		URL url = null;
		HttpURLConnection conn = null;
		try 
		{
			url = new URL("https://www."+link);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			
			int responseCode;
			if ((responseCode = conn.getResponseCode()) != HttpURLConnection.HTTP_OK)
			{
				switch (responseCode)
				{
					case HttpURLConnection.HTTP_FORBIDDEN:
					{
						System.out.println(conn.getResponseMessage());
						throw new GameMe_Exception("Encountered a 403 HTTP response while attempting to connect to API."+
						" Please ensure the account name provided is valid.");
					}
				}
				throw new RuntimeException(conn.getResponseMessage());
			}
			
			BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			String jsonLine = null;
			while ((line = input.readLine()) != null)
			{
				//System.out.println("ALine:"+line);
				if (line.contains("loadManager"))
				{
					jsonLine = line;
					break;
				}
			}
			
			System.out.println(jsonLine);
			ArrayList<String> linkParts = new ArrayList<String>();
			
			String json = jsonLine.split("<script id=\"storeBootstrap\" type=\"application/json\">")[1].split("</script>")[0];
			
			return JsonUtilities.GetAnyConfigValue(Json.createReader(new StringReader(json)).readObject(), "servers:servers:"+id);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<String> GetTop3(int serverNo)
	{
		URL url = null;
		HttpURLConnection conn = null;
		try 
		{
			url = new URL(String.format(urlFormat, serverNo)+searchFilter);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
			
			int responseCode;
			if ((responseCode = conn.getResponseCode()) != HttpURLConnection.HTTP_OK)
			{
				switch (responseCode)
				{
					case HttpURLConnection.HTTP_FORBIDDEN:
					{
						System.out.println(conn.getResponseMessage());
						throw new GameMe_Exception("Encountered a 403 HTTP response while attempting to connect to API."+
						" Please ensure the account name provided is valid.");
					}
				}
				throw new RuntimeException(conn.getResponseMessage());
			}
			
			System.out.println("yeee boi");
			BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			String jsonLine = null;
			String tableLine = null;
			while ((line = input.readLine()) != null)
			{
				//System.out.println("ALine:"+line);
				if (line.contains("loadManager"))
				{
					jsonLine = line;
					continue;
				}
				
				if (line.contains("root"))
				{
					tableLine = line;
					break;
				}
			}
			
			System.out.println(jsonLine);
			System.out.println(tableLine);
			Pattern serverPath = Pattern.compile("href=\"/servers/ark/\\d+\"");
			ArrayList<String> linkParts = new ArrayList<String>();
			Matcher matcher = serverPath.matcher(tableLine);
			while (matcher.find())
			{
				linkParts.add("https://www.battlemetrics.com"+matcher.group().split("href=\"")[1].split("\"")[0]);
			}
			
			String json = jsonLine.split("<script id=\"storeBootstrap\" type=\"application/json\">")[1].split("</script>")[0];
			System.out.println("JSON\n"+json);
			
			System.out.println("Links\n");
			linkParts.forEach((s) -> System.out.println(s));
			
			jsonCache = Json.createReader(new StringReader(json)).readObject();
			return linkParts.subList(0, 3);
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Object GetValueFromJsonCache(String keyPath)
	{
		return JsonUtilities.GetAnyConfigValue(jsonCache, keyPath);
	}
	
	public Object GetServerInfo(String link)
	{
		if (link == null)
		{
			return null;
		}
		
		int id = -1;
		try
		{
			id = Integer.parseInt(link);
		}
		catch (NumberFormatException npe)
		{
			Pattern bmLinkPatt = Pattern.compile("battlemetrics.com/servers/ark/\\d+");
			Matcher matcher = bmLinkPatt.matcher(link);
			if (!matcher.find())
			{
				return null;
			}
			
			matcher = Pattern.compile("\\d+").matcher(link);
			matcher.find();
			id = Integer.parseInt(matcher.group());
		}
		
		Object info = null;
		info = GetValueFromJsonCache("servers:servers:"+id);
		if (info != null)
		{
			return info;
		}
		
		return GetSpecificServerInfo("battlemetrics.com/servers/ark/"+id);
	}
}
