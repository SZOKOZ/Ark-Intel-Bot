package arkintelbot.gui.model;

public class ServerDiscordRelay 
{
	private String alias;
	private String ip;
	private int port;
	private int queryPort;
	private String discordRelay;
	
	public ServerDiscordRelay(String alias, String ip, int port, int queryPort, String discordRelay)
	{
		this.alias = alias;
		this.ip = ip;
		this.port = port;
		this.queryPort = queryPort;
		this.discordRelay = discordRelay;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getQueryPort() {
		return queryPort;
	}

	public void setQueryPort(int queryPort) {
		this.queryPort = queryPort;
	}

	public String getDiscordRelay() {
		return discordRelay;
	}

	public void setDiscordRelay(String discordRelay) {
		this.discordRelay = discordRelay;
	}
}
