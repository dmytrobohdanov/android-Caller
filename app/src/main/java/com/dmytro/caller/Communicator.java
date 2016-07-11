package com.dmytro.caller;

import android.app.Activity;
import android.opengl.GLSurfaceView;

/**
 * Responsible for WebRTC communication
 */
public class Communicator {
    
    private static Communicator instance;
    private GLSurfaceView videoView;

    private Communicator(Activity activity) {
        videoView = (GLSurfaceView) activity.findViewById(R.id.calleeView);
    }

    public static Communicator getInstance(Activity activity) {
        if (instance == null) {
            instance = new Communicator(activity);
        }

        return instance;
    }


}
