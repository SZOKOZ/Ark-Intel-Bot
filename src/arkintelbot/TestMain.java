package arkintelbot;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.servers.SourceServer;

public class TestMain {
	public static String ConvertID64(String id64)
	{		
		String invalidSteamId = String.format("%s is not an individual steam id.", id64);
		long id64num = Long.parseLong(id64);
		long X = (id64num >> 56) & 0xFF;
		if (X != 0 && X != 1)
		{
			System.out.println(invalidSteamId);
			System.out.println("X"+String.valueOf(X));
			//return null;
		}
		
		if (X == 1)
		{
			X = 0;
		}
		
		long Y = id64num & 1;
		
		if (Y != 1)
		{
			System.out.println(invalidSteamId);
			System.out.println("Y"+String.valueOf(Y));
			//return null;
		}
		
		long Z = (id64num>>1)& 0x7FFFFFF;
		
		System.out.println(String.format("ID64 Conversion %s: STEAM_%d:%d:%d", id64, X, Y, Z));
		return String.format("STEAM_%d:%d:%d", X, Y, Z);
	}
	
	public static void main(String[] args) throws UnknownHostException, SteamCondenserException, TimeoutException
	{
		/*
		byte[] response = {20, 0, 0, 0, 0, 0, 0, -3, -113, -77, 72, 0, 0, 0, 0, 0, 0, -12, 23, 
				-87, 72, 0, 0, 0, 0, 0, 0, -61, -106, -91, 72, 0, 0, 0, 0, 0, 0, -90, -93, 116, 
				72, 0, 0, 0, 0, 0, 0, 74, -89, 103, 72, 0, 0, 0, 0, 0, 0, -118, -55, -24, 71, 0, 
				0, 0, 0, 0, 0, -68, 114, -27, 71, 0, 0, 0, 0, 0, 0, -43, -54, -85, 71, 0, 0, 0, 0, 
				0, 0, 93, 24, 85, 71, 0, 49, 50, 51, 0, 0, 0, 0, 0, -61, -24, 76, 71, 0, 112, 117, 
				110, 105, 115, 104, 101, 114, 56, 57, 0, 0, 0, 0, 0, -79, -105, 20, 71, 0, 49, 50, 
				51, 0, 0, 0, 0, 0, -75, 21, 11, 71, 0, 49, 50, 51, 0, 0, 0, 0, 0, 107, 79, 115, 70, 
				0, 49, 50, 51, 0, 0, 0, 0, 0, -77, 107, 16, 70, 0, 49, 50, 51, 0, 0, 0, 0, 0, 38, -9, -
				2, 69, 0, 78, 105, 103, 104, 116, 87, 111, 108, 102, 0, 0, 0, 0, 0, 36, 117, -58, 69, 
				0, 0, 0, 0, 0, 0, 63, -114, -20, 68, 0, 0, 0, 0, 0, 0, 99, 112, -67, 68, 0, 78, 111, 32,
				72, 97, 110, 100, 108, 101, 32, 66, 97, 114, 115, 0, 0, 0, 0, 0, -119, 80, 119, 67, 0, 49,
				50, 51, 0, 0, 0, 0, 0, 49, 124, -28, 66};
		
		System.out.println(new String(response));
		*/

		
		InetAddress arkServerIp = InetAddress.getByAddress(new byte[] {37, 10, 127, 11});//37.10.127.11:27019
		SourceServer arkServer = new SourceServer(arkServerIp, 27019);
		arkServer.getServerInfo().forEach((k,v) ->
		{
			/*if (k.equals("serverId"))
			{
				System.out.println("UnsignedLongString:"+Long.toUnsignedString((long)v));
				long l = 687076500422344705l;
				System.out.println(l);
			}*/
			System.out.println(k+":"+v);
		});
		System.out.println("===============");
		arkServer.getPlayers().forEach((k,v) ->
		{
			System.out.println(k+":"+v);
		});
		
		
		ConvertID64("-5343171536515760129");
		ConvertID64("-885139019");
		ConvertID64("90126704874949641");
		/*
		try 
		{
			SteamAPI.printDebugInfo(System.out);
			System.out.println(SteamAPI.isSteamRunning());
		    SteamAPI.loadLibraries();
		    
		    if (!SteamAPI.init()) 
		    {
		        // Steamworks initialization error, e.g. Steam client not running
		    	System.out.println("Failed to initialise.");
		    	
		    }
		} 
		catch (SteamException e) 
		{
		    // Error extracting or loading native libraries
			e.printStackTrace();
		}
		*/
		
		/*
		// initialization
		try {
		    SteamGameServerAPI.loadLibraries();
		    if (!SteamGameServerAPI.init((127 << 24) + 1, (short) 27015, (short) 27016, (short) 27017,
		        SteamGameServerAPI.ServerMode.NoAuthentication, "0.0.1")) 
		    {
		        // initialization error
		    }
		} catch (SteamException e) {
		    // Error extracting or loading native libraries
		}

		// update ticks
		while (serverIsAlive) {
		  SteamGameServerAPI.runCallbacks();
		}

		// shutdown
		SteamGameServerAPI.shutdown();
		*/
		
	}

}
