package com.flyingblock.pcm.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;

public class Animation<E>
{
    public static final String DELIMITER = ";";
    public static enum StringAnimationType {WRAP_SCROLL_FWD, WRAP_SCROLL_BCK, SCROLL_FWD, SCROLL_BCK, FRAME_BY_FRAME, NONE};
    public static enum ImageAnimationType {GIF, PNG};
    
    private E[] frames;
    private int interval;
    private int loops;
    
    public Animation(E[] frames, int interval, int loops)
    {
        this.frames = frames;
        this.interval = interval;
        this.loops = loops;
    }
    
    public boolean isBounded()
    {
        return loops != -1;
    }
    
    public E getFrameTime(int millis)
    {
        if(isBounded())
        {
            if(millis >= interval*getNumFrames()*loops)
                return getFrame(getNumFrames()-1);
            millis %= interval*getNumFrames();
            return getFrame(millis/interval);
        }
        else
        {
            millis %= interval*getNumFrames();
            return getFrame(millis/interval);
            
        }
    }
    
    public E getFrame(int index)
    {
        return frames[index];
    }
    
    public int getNumFrames()
    {
        return frames.length;
    }
    
    public int getInterval()
    {
        return interval;
    }
    
    public int getLoops()
    {
        return loops;
    }
    
    public E getLastFrame()
    {
        return getFrame(getNumFrames() - 1);
    }
    
    public boolean isOver(int millis)
    {
        return isBounded() && millis > getInterval() * getNumFrames() * getLoops();
    }
    
    public static Animation<String> combineAnimations(List<Animation<String>> animations, int interval, int loops, String separator)
    {
        return new MeldAnimation(animations, interval, loops, separator);
    }
    
    public static Animation<List<String>> combineAnimations(List<Animation<String>> animation, int interval, int loops)
    {
        return new ListAnimation(animation, interval, loops);
    }
    
    private static String correctFrameForColors(int startIndex, int endIndex, String base)
    {
        if((startIndex != 0 && base.charAt(startIndex - 1) == ChatColor.COLOR_CHAR) || 
                base.charAt(startIndex) == ChatColor.COLOR_CHAR) {
            return null;
        }
        String prefix = "";
        for(int index = startIndex; index >= 0; index--)
        {
            if(base.charAt(index) == ChatColor.COLOR_CHAR)
                prefix = ChatColor.COLOR_CHAR + "" + base.charAt(index + 1) + prefix;
        }
        for(int index = startIndex; index < endIndex; index++)
        {
            if(base.charAt(index) == ChatColor.COLOR_CHAR)
                endIndex += 2;
        }
        return prefix + base.substring(startIndex, endIndex);
    }
    
    public static Animation<String> developAnimation(StringAnimationType type, String str, int frameSize, int interval, int loops)
    {
        List<String> frames = new ArrayList<>();
        String base = str;
        int length = ChatColor.stripColor(base).length();
        int fullLength = base.length();
        if(base.length() > 0)
        {
            switch(type)
            {
                case SCROLL_FWD:
                    for(int i = 0; i < frameSize; i++)
                        base = " " + base + " ";
                    for(int i = 0; i < fullLength + frameSize; i++)
                    {
                        String frame = correctFrameForColors(fullLength + frameSize - i - 1, fullLength + frameSize*2 - i - 1, base);
                        if(frame != null)
                            frames.add(frame);
                    }
                    for(int i = 0; i < frameSize; i++)  {
                        frames.add(0, frames.remove(frames.size() - 1));
                    }
                    break;
                case SCROLL_BCK:
                    for(int i = 0; i < frameSize || base.length() < frameSize; i++)
                        base = " " + base + " ";
                    for(int i = 0; i < fullLength + frameSize +  1; i++)
                    {
                        String frame = correctFrameForColors(i, frameSize + i, base);
                        if(frame != null)
                            frames.add(frame);
                    }
                    for(int i = 0; i < length; i++) {
                        frames.add(0, frames.remove(frames.size() - 1));
                    }
                    break;
                case WRAP_SCROLL_FWD:
                    while(base.length() < frameSize)
                        base = " " + base + " ";
                    length = base.length();
                    base = base + " " + base + " " + base;
                    while(base.length() < frameSize*2){
                        base += " " + base;
                    }
                    for(int i = 0; i < length + 1; i++)
                    {
                        String frame = correctFrameForColors(length + 2 - i, length + frameSize + 2 - i, base);
                        if(frame != null)
                            frames.add(frame);
                    }
                    break;
                case WRAP_SCROLL_BCK:
                    while(base.length() < frameSize)
                        base = " " + base + " ";
                    length = base.length();
                    base = base + " " + base + " " + base;
                    while(base.length() < frameSize*2){
                        base += " " + base;
                    }
                    for(int i = 0; i < length + 1; i++)
                    {
                        String frame = correctFrameForColors(i, frameSize + i, base);
                        if(frame != null)
                            frames.add(frame);
                    }
                    break;
                case NONE:
                    for(int i = 0; i < frameSize && i < base.length(); i++)
                    {
                        if(base.charAt(i) == ChatColor.COLOR_CHAR)
                            frameSize += 2;
                    }
                    if(base.length() > frameSize)
                        frames.add(base.substring(0, frameSize));
                    else
                        frames.add(base);
                    break;
                case FRAME_BY_FRAME:
                    for(String frame : base.split(DELIMITER))
                    {
                        int tempFrame = frameSize;
                        for(int i = 0; i < tempFrame && i < frame.length(); i++)
                        {
                            if(base.charAt(i) == ChatColor.COLOR_CHAR)
                                tempFrame += 2;
                        }
                        if(frame.length() > tempFrame)
                            frames.add(frame.substring(0, tempFrame));
                        else
                            frames.add(frame);
                    }
                    break;
            }
        }
        else
            frames.add("");
        
        return new Animation<>(frames.toArray(new String[frames.size()]), interval, loops);
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        Animation<String> scroll = developAnimation(StringAnimationType.NONE, "§2hello §4world", 5, 100, 1);
        System.out.println();
        scroll = developAnimation(StringAnimationType.WRAP_SCROLL_BCK, ChatColor.RED + "this line is not short at all :OIJP:IO ", 20, 100, 10);
        for(int i = 0; i < scroll.getNumFrames(); i++)
        {
            System.out.println(ChatColor.stripColor(scroll.getFrame(i)) + " | " + scroll.getFrame(i));
        }
        
        System.out.println();
        scroll = developAnimation(StringAnimationType.WRAP_SCROLL_FWD, ChatColor.RED + "short", 20, 100, 10);
        for(int i = 0; i < scroll.getNumFrames(); i++)
        {
            System.out.println(ChatColor.stripColor(scroll.getFrame(i)) + " | " + scroll.getFrame(i));
        }
    }
}
