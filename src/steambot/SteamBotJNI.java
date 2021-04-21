package steambot;

import java.io.File;

public class SteamBotJNI 
{
	private long pointer;
	static 
	{
		//System.loadLibrary("MySteamBot-JNI.dll.dll");
		File file = new File(".");
		System.load(file.getAbsolutePath()+"\\steam_api64.dll");
		System.load(file.getAbsolutePath()+"\\MySteamBot-JNI.dll.dll");
	}
	
	public SteamBotJNI()
	{
		pointer = Initialise();
	}
	
	public boolean IsInitialised()
	{
		return IsInitialised(pointer);
	}
	
	public long GetPointer()
	{
		return pointer;
	}
	
	public void SetServerInformation(long id64)
	{
		SetServerInformation(pointer, id64);
	}
	
	public int GetPlayerCount()
	{
		return GetPlayerCount(pointer);
	}
	
	public int GetPlayerCountByServerId(long id64)
	{
		return GetPlayerCount(pointer, id64);
	}
	
	public SteamPlayer[] GetPlayers()
	{
		return GetPlayers(pointer);
	}
	
	public SteamPlayer[] GetPlayersByServerId(long id64)
	{
		return GetPlayers(pointer, id64);
	}
	
	private native long Initialise();
	
	private native boolean IsInitialised(long pointer);
	
	private native void SetServerInformation(long pointer, long id64);
	
	private native int GetPlayerCount(long pointer);
	
	private native int GetPlayerCount(long pointer, long id64);
	
	private native SteamPlayer[] GetPlayers(long pointer);
	
	private native SteamPlayer[] GetPlayers(long pointer, long id64);
	
	public static class SteamPlayer
	{
		private String playerName;
		private long id64;
		
		public SteamPlayer(String playerName, long id64)
		{
			this.playerName = playerName;
			this.id64 = id64;
		}
		
		public String getPlayerName()
		{
			return playerName;
		}
		
		public long id64()
		{
			return id64;
		}
	}
}
