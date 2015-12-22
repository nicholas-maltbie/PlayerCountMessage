/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

/**
 *
 * @author Nicholas
 */
public class UnicodeTag extends Tag
{
    public static final String UNICODE_TAG = "%u";
    
    public UnicodeTag()
    {
        super(UNICODE_TAG);
    }
    
    @Override
    public String applyTo(String str)
    {
        while(this.containsTarget(str))
        {
            int index = str.indexOf(UNICODE_TAG);
            if(index + 4 < str.length() && str.substring(index + UNICODE_TAG.length(), index + UNICODE_TAG.length() + 4).matches("^[0-9A-F]+$"))
                str = str.substring(0, index).concat(Character.toString((char) Integer.decode("0x" + str.substring(index+2, index+6)).intValue())).concat(str.substring(index + UNICODE_TAG.length() + 4));
            else
                str = str.replaceFirst(UNICODE_TAG, "");
        }
        return str;
    }
    
    @Override
    public boolean removeLine(String str)
    {
        return false;
    }
    
    @Override
    public boolean reset()
    {
        return false;
    }  
    
}
