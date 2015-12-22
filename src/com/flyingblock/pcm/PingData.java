package com.flyingblock.pcm;

import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;
import com.flyingblock.pcm.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class PingData {


    private String playerCount;
    private String motd;
    private CompressedImage image;
    private List<String> players;

    public PingData(String playerCount, String motd, File imagePath, List<String> players) throws IOException
    {
        this(playerCount, motd, CompressedImage.fromPng(ImageIO.read(imagePath)), players);
    }
    
    public PingData(String playerCount, String motd, CompressedImage image, List<String> players) {
        this.playerCount = playerCount;
        this.motd = motd;
        this.image = image;
        this.players = players;
    }
    
    public String getPlayerCount() {
        return playerCount;
    }
    
    public void setPlayerCount(String playerCount)
    {
        this.playerCount = playerCount;
    }

    public String getMotd() {
        return motd;
    }
    
    public void setMotd(String motd)
    {
        this.motd = motd;
    }

    public CompressedImage getImage() {
        return image;
    }

    public List<String> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<String> players)
    {
        this.players = players;
    }
    
    public void applyTo(Tag tag)
    {
        List<String> players = getPlayers();
        for(int i = 0; i < players.size(); i++)
        {
            players.set(i, tag.applyTo(players.get(i)));
        }
        setPlayers(players);
        setMotd(tag.applyTo(getMotd()));
        setPlayerCount(tag.applyTo(getPlayerCount()));
    }

}
