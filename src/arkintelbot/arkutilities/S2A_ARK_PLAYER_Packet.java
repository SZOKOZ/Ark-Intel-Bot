package arkintelbot.arkutilities;

import java.util.HashMap;

import com.github.koraktor.steamcondenser.exceptions.PacketFormatException;
import com.github.koraktor.steamcondenser.servers.SteamPlayer;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;

public class S2A_ARK_PLAYER_Packet extends SteamPacket {

	private HashMap<String, SteamPlayer> playerHash;
	
	public S2A_ARK_PLAYER_Packet(byte[] dataBytes) throws PacketFormatException 
	{
		super(SteamPacket.S2A_PLAYER_HEADER, dataBytes);
		
		if(this.contentData.getLength() == 0) {
            throw new PacketFormatException("Wrong formatted S2A_PLAYER response packet.");
        }

        this.playerHash = new HashMap<>(this.contentData.getByte());

        while(this.contentData.hasRemaining()) {
            int playerId = this.contentData.getByte() & 0xff;
            String playerName = this.contentData.getString();
            String key = playerName;
            int i = 0;
            while (playerHash.containsKey(key))
            {
            	key = playerName+String.format("(%d)", ++i);
            }
            
            this.playerHash.put(key, new SteamPlayer(
                playerId,
                playerName,
                Integer.reverseBytes(this.contentData.getInt()),
                Float.intBitsToFloat(Integer.reverseBytes(this.contentData.getInt()))
            ));
        }
	}
	
	public HashMap<String, SteamPlayer> getPlayerHash() 
	{
        return this.playerHash;
    }

}
