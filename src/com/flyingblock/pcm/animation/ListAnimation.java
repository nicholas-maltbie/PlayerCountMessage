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
public class ListAnimation extends Animation<List<String>>
{
    private List<Animation<String>> animations;
    
    public ListAnimation(List<Animation<String>> animations, int interval, int loops)
    {
        super(new List[0], interval, loops);
        this.animations = animations;
    }
    
    @Override
    public List<String> getFrame(int index)
    {
        return null;
    }
    
    public List<String> getFrameTime(int millis)
    {
        List<String> list = new ArrayList<>();
        for(Animation<String> a : animations){
            list.add(a.getFrameTime(millis));
        }
        return list;
    }
    
    @Override
    public boolean isOver(int millis)
    {
        for(Animation a : animations)
            if(!a.isOver(millis))
                return false;
        return true;
    }
    
    @Override
    public List<String> getLastFrame()
    {
        List<String> list = new ArrayList<>();
        for(Animation<String> a : animations){
            list.add(a.getLastFrame());
        }
        return list;
    }
}
