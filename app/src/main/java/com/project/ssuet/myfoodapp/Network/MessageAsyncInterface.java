package com.project.ssuet.myfoodapp.Network;

import java.util.HashMap;

/**
 * Created by Abdul Nafay Waseem on 12/24/2017.
 */

public interface MessageAsyncInterface {
    void didCompleteWithTask(HashMap<String, String> data);
    void didFailToCompleteTask();
}
