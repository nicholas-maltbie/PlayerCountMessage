/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.animation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nick_Pro
 */
public class MeldAnimation extends Animation<String>
{
    private List<Animation<String>> animations;
    private String separator;
    
    public MeldAnimation(List<Animation<String>> animations, int interval, int loops, String separator)
    {
        super(new String[0], interval, loops);
        this.animations = animations;
        this.separator = separator;
    }
    
    @Override
    public String getFrame(int index)
    {
        return null;
    }
    
    public String getFrameTime(int millis)
    {
        List<String> list = new ArrayList<>();
        for(Animation<String> a : animations){
            list.add(a.getFrameTime(millis));
        }
        String line = list.get(0);
        for(int i = 1; i < list.size(); i ++)
        {
            line += separator + list.get(i);
        }
        return line;
    }
    
    @Override
    public String getLastFrame()
    {
        List<String> list = new ArrayList<>();
        for(Animation<String> a : animations){
            list.add(a.getLastFrame());
        }
        String line = list.get(0);
        for(int i = 1; i < list.size(); i ++)
        {
            line += separator + list.get(i);
        }
        return line;
    }
    
    @Override
    public boolean isOver(int millis)
    {
        for(Animation a : animations)
            if(!a.isOver(millis))
                return false;
        return true;
    }
}
