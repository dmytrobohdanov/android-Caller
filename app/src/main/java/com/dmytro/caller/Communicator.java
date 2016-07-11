package com.dmytro.caller;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

/**
 * Responsible for WebRTC communication
 */
public class Communicator {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final String TRACK_ID = "someID"; //todo findout about this IDs
    private static final String LOCAL_MEDIA_STREAM_ID = "someLocalID";

    public static final String FRONT_CAMERA = VideoCapturerAndroid.getNameOfFrontFacingDevice();
    public static final String BACK_CAMERA = VideoCapturerAndroid.getNameOfBackFacingDevice();

    private boolean INITIALIZE_AUDIO = true;
    private boolean INITIALIZE_VIDEO = true;
    private boolean VIDEO_CODEC_HW_ACCELERATION = true;

    private static Communicator instance;
    private GLSurfaceView videoView;

    /**
     * Constructor
     */
    private Communicator(Activity activity) {
        videoView = (GLSurfaceView) activity.findViewById(R.id.calleeView);

        //initializing global settings of peer connection and checking this
        if (!PeerConnectionFactory.initializeAndroidGlobals(
                activity,
                INITIALIZE_AUDIO,
                INITIALIZE_VIDEO,
                VIDEO_CODEC_HW_ACCELERATION,
                VideoRendererGui.getEGLContext())) {

            Log.e(LOG_TAG, "Android globals are not initialized");
        }

        PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();


        MediaStream mediaStream = getLocalMediaStream(peerConnectionFactory, INITIALIZE_AUDIO, INITIALIZE_VIDEO);


    }



    public static Communicator getInstance(Activity activity) {
        if (instance == null) {
            instance = new Communicator(activity);
        }

        return instance;
    }

    /**
     * Creates local media stream
     */
    public MediaStream getLocalMediaStream(PeerConnectionFactory peerConnectionFactory,
                                           boolean initialize_audio, boolean initialize_video) {
        int defaultCameraID = 0;
        return getLocalMediaStream(peerConnectionFactory, initialize_audio,initialize_video, defaultCameraID);
    }

    /**
     *  Creates local media stream
     *  with specified camera name
     *  @param cameraName could be specified by static constants in Communicator class
     *                    can be: Communicator.FRONT_CAMERA and Communicator.BACK_CAMERA
     */
    public MediaStream getLocalMediaStream(PeerConnectionFactory peerConnectionFactory,
                                           boolean initialize_audio, boolean initialize_video, String cameraName) {

        if(!cameraName.equals(Communicator.FRONT_CAMERA) || !cameraName.equals(Communicator.BACK_CAMERA)){
            //todo change to exception sometime
            Log.e(LOG_TAG, "Wrong camera name have passed. Using default camera ID value...");
            return getLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video);
        }

        int numberOfCameras= VideoCapturerAndroid.getDeviceCount();

        //looking for ID of specified camera name
        //setting default ID value
        int defaultIDValue = -1;
        int cameraID = defaultIDValue;
        for(int i = 0; i < numberOfCameras; i++){
            String camera = VideoCapturerAndroid.getDeviceName(i);
            if(camera.equals(cameraName)){
                cameraID = i;
                break;
            }
        }
        //if there is no such camera - using default ID
        if(cameraID == defaultIDValue){
            return getLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video);
        }
        return getLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video, cameraID);
    }

    /**
     * Creates local media stream
     * with specified camera id
     */
    public MediaStream getLocalMediaStream(PeerConnectionFactory peerConnectionFactory,
                                           boolean initialize_audio, boolean initialize_video, int cameraID) {
        MediaStream localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        //forming audio stream
        if(initialize_audio){
            MediaConstraints audioConstraints = new MediaConstraints();
            AudioSource audioSource =
                    peerConnectionFactory.createAudioSource(audioConstraints);
            AudioTrack localAudioTrack =
                    peerConnectionFactory.createAudioTrack(TRACK_ID, audioSource);
            localMediaStream.addTrack(localAudioTrack); //todo check is success
        }

        //forming video stream
        if(initialize_video){
            //checking availability of camera with specified ID
            //if there is no such camera we can not form video stream
            if(cameraID < VideoCapturerAndroid.getDeviceCount()){
                VideoCapturerAndroid capturer = VideoCapturerAndroid.create(
                        VideoCapturerAndroid.getDeviceName(cameraID));
                MediaConstraints videoConstraints = new MediaConstraints();
                VideoSource videoSource =
                        peerConnectionFactory.createVideoSource(capturer, videoConstraints);
                VideoTrack localVideoTrack =
                        peerConnectionFactory.createVideoTrack(TRACK_ID, videoSource);
                localMediaStream.addTrack(localVideoTrack);
            }
        }
        return localMediaStream;
    }

}
