package arkintelbot;

import java.util.HashMap;

public class ArkServer
{
	private HashMap<String, Object> serverInfo;
	private String alias;
	private String ip;
	private int port;
	private int queryPort;
	
	public ArkServer(String ip, int port, int queryPort, HashMap<String, Object> serverInfo)
	{
		this.ip = ip;
		this.port = port;
		this.queryPort = queryPort;
		this.serverInfo = serverInfo;
	}
	
	public ArkServer(String ip, int port, int queryPort, HashMap<String, Object> serverInfo, String alias)
	{
		this.ip = ip;
		this.port = port;
		this.queryPort = queryPort;
		this.serverInfo = serverInfo;
		this.alias = alias;
	}

	public String getIp() 
	{
		return ip;
	}

	public int getPort() 
	{
		return port;
	}

	public int getQueryPort() 
	{
		return queryPort;
	}
	
	public long getSteamId()
	{
		return (long)serverInfo.get("serverId");
	}
	
	public String getAlias()
	{
		return alias;
	}
	
	public void setAlias(String alias)
	{
		this.alias = alias;
	}
}