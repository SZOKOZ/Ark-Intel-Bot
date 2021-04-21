package arkintelbot;

public class ArkPlayer
{
	private String name = null;
	private long id64 = 0;
	private ArkServer server = null;
	private ArkAllegiance allegiance = ArkAllegiance.ALLEGIANCE_UNKNOWN;
	
	public ArkPlayer(String name, long id64)
	{
		this.name = name;
		this.id64 = id64;
	}
	
	public ArkPlayer(String name, long id64, ArkAllegiance allegiance)
	{
		this.name = name;
		this.id64 = id64;
		this.allegiance = allegiance;
	}
	
	public ArkPlayer(String name, long id64, ArkServer server)
	{
		this.name = name;
		this.id64 = id64;
		this.server = server;
	}
	
	public ArkPlayer(String name, long id64, ArkAllegiance allegiance, ArkServer server)
	{
		this.name = name;
		this.id64 = id64;
		this.allegiance = allegiance;
		this.server = server;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	public long getId64() 
	{
		return id64;
	}
	
	public void setId64(long id64) 
	{
		this.id64 = id64;
	}
	
	public ArkAllegiance getAllegiance() 
	{
		return allegiance;
	}
	
	public void setAllegiance(ArkAllegiance allegiance) 
	{
		this.allegiance = allegiance;
	}
	
	public ArkServer getServer()
	{
		return server;
	}
	
	public void setServer(ArkServer server)
	{
		this.server = server;
	}
}
