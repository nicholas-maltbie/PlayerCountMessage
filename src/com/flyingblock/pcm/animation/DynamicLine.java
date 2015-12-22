/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.animation;

import static com.flyingblock.pcm.animation.Animation.DELIMITER;
import com.flyingblock.pcm.tags.Tag;
import java.util.List;
import org.bukkit.ChatColor;

/**
 *
 * @author Nick_Pro
 */
public class DynamicLine extends Animation<String>
{    
    private StringAnimationType animation;
    private int lineLength;
    private int timeLength;

    public DynamicLine(int interval, int loops, int lineLength, StringAnimationType
            animationType, String line, List<Tag> tags) {
        super(new String[]{line}, interval, loops);
        this.animation = animationType;
        this.lineLength = lineLength;
        timeLength = Math.max(lineLength, line.length()) * loops * getInterval();
    }
    
    /*@Override
    public String getFrameTime(int millis)
    {
        String frame = getFrame(0);
        if(animation == StringAnimationType.NONE || (!isBounded() && millis > timeLength))
        {
            StringBuilder b = new StringBuilder(frame);
            b.setLength(lineLength);
            return b.toString();
        }
        int frames = Math.max(lineLength, frame.length());
        int curentFrame = (millis / getInterval()) % (frames * getInterval());
        //WRAP_SCROLL_FWD, WRAP_SCROLL_BCK, SCROLL_FWD, SCROLL_BCK, FRAME_BY_FRAME, NONE;
        switch(animation)
        {
            case FRAME_BY_FRAME:
                
                break;
        }
        StringBuilder b = new StringBuilder(frame);
        b.setLength(lineLength);
    }*/
}
