package com.flyingblock.pcm.animation;

import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;
import com.flyingblock.pcm.PingData;
import java.util.List;

public class PingAnimation 
{
    private Animation<List<String>> playersAnimation;
    private Animation<CompressedImage> imagesAnimation;
    private Animation<String> playerCountAnimation;
    private Animation<String> motdAnimation;
    private int length;
    
    public PingAnimation(Animation<List<String>> playersAnimation, Animation<CompressedImage> imagesAnimation,
            Animation<String> playerCoutAnimation, Animation<String> motdAnimation, int length)
    {
        this.playerCountAnimation = playerCoutAnimation;
        this.playersAnimation = playersAnimation;
        this.imagesAnimation = imagesAnimation;
        this.motdAnimation = motdAnimation;
        this.length = length;
    }
    
    public PingData getLastPing()
    {
        return new PingData(playerCountAnimation.getLastFrame(), motdAnimation.getLastFrame(), 
                imagesAnimation.getLastFrame(), playersAnimation.getLastFrame());
    }
    
    public PingData getPing(int millis)
    {
        return new PingData(playerCountAnimation.getFrameTime(millis), motdAnimation.getFrameTime(millis),
                imagesAnimation.getFrameTime(millis),playersAnimation.getFrameTime(millis));
    }
    
    public boolean isBounded()
    {
        return playersAnimation.isBounded() || imagesAnimation.isBounded() || playerCountAnimation.isBounded() || motdAnimation.isBounded();
    }
    
    public boolean isOver(int millis)
    {
        return millis > length || (isBounded() && playersAnimation.isOver(millis) && imagesAnimation.isOver(millis) && playerCountAnimation.isOver(millis) && motdAnimation.isOver(millis));
    }
}
