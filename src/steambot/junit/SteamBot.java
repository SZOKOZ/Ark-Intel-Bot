package steambot.junit;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.koraktor.steamcondenser.servers.SourceServer;

import killzonebot.Main;
import steambot.SteamBotJNI;
import steambot.SteamBotJNI.SteamPlayer;

class SteamBot {

	private static final Logger logger = LoggerFactory.getLogger(SteamBot.class);
	static SteamBotJNI steambot;
	@BeforeAll
	static void setUpBeforeClass() throws Exception 
	{
		steambot = new SteamBotJNI();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception 
	{
		
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Disabled
	void IsSteamBotInitialised() 
	{
		assertTrue(steambot.IsInitialised(), "Steam bot is not initialised!");
	}
	
	@Disabled
	void GetDatPointer()
	{
		System.out.println(steambot.GetPointer());
	}

	@Disabled
	void GetDatServerInformation() throws Exception
	{
		InetAddress arkServerIp = InetAddress.getByAddress(new byte[] {37, 10, 127, 11});//37.10.127.11:27019
		SourceServer arkServer = new SourceServer(arkServerIp, 27019);
		HashMap<String, Object> info = arkServer.getServerInfo();
		assertTrue(info != null);
		System.out.println(info.get("serverId"));
		logger.info(String.valueOf(info.get("serverId")));
	}
	
	@Disabled
	void GetPlayerCountFromSteam()
	{
		int players = steambot.GetPlayerCountByServerId(90126785324025865l);
		assertFalse(players == 0, "You are not connected to the server of this steam id:"+players);
		System.out.println(players);
	}
	
	@Test
	void GetPlayersFromSteam()
	{
		SteamPlayer[] onlinePlayers = steambot.GetPlayersByServerId(90126785324025865l);
		assertFalse(onlinePlayers == null);
		for (int i = 0; i < onlinePlayers.length; i++)
		{
			System.out.println(onlinePlayers[i].getPlayerName()+":"+onlinePlayers[i].id64());
		}
	}

}
