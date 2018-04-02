package com.example.administrator.signin.calendar.Tool;

/**
 * Created by sy on 2016/9/13.
 */
public class ChangeTool {

    public static int changeToInt(String data){

        int i = -1;
        try {
            i = Integer.parseInt(data.trim());
        } catch (NumberFormatException e) {

        }
        return i;
    }


}
