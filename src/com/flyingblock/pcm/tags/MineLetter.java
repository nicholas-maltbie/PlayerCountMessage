package com.flyingblock.pcm.tags;

import java.util.Arrays;
import java.util.List;

public class MineLetter 
{	
    public static List<Character> width1 = Arrays.asList('!', '\'', ',', '.', ':', ';', 'i', '|');
    public static List<Character> width2 = Arrays.asList('`', 'l');
    public static List<Character> width3 = Arrays.asList(' ', '\"', ']', '[', 't');
    public static List<Character> width4 = Arrays.asList('f', '(', ')', '*', '<', '>', 'k', '}', '{');
    //width5 = default Arrays.asList('#', '$', '%', '&', '+', '-', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '=','?', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N','O'..., '\\''^', '_', 
    public static List<Character> width6 = Arrays.asList('@', '~');

    public static int width(char c)
    {
        if(width1.contains(c))
            return 1;
        else if(width2.contains(c))
            return 2;
        else if(width3.contains(c))
            return 3;
        else if(width4.contains(c))
            return 4;
        else if(width6.contains(c))
            return 6;
        else
            return 5;
    }
	
    public static int length(String s)
    {
        int index = 0;
        int length = 0;
        int add = 0;

        while(index < s.length())
        {
            if(s.charAt(index) != '\u00A7')
            {
                length += width(s.charAt(index)) + add;
            }
            else
            {
                index++;
                if(s.charAt(index) == 'l')
                    add = 1;
                else if(s.charAt(index) == 'r')
                    add = 0;
            }
            index++;
        }

        return length;
    }
}
