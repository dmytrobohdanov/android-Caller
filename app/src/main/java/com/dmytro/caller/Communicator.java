package com.dmytro.caller;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.LinkedList;

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
    private MediaStream localMediaStream;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private ConnectionsObserver peerConnectionObserver;


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

        peerConnectionFactory = new PeerConnectionFactory();

        localMediaStream = initializeLocalMediaStream(peerConnectionFactory, INITIALIZE_AUDIO, INITIALIZE_VIDEO);

        peerConnectionObserver = ConnectionsObserver.getInstance();

        this.prepareToMakeCall();

//        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection();
//        peerConnection.createOffer(this, );
    }

    public static Communicator getInstance(Activity activity) {
        if (instance == null) {
            instance = new Communicator(activity);
        }

        return instance;
    }

    public void prepareToMakeCall() {
        //setting media constrains
        MediaConstraints pcConstraints = new MediaConstraints();
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToRecieveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("RtpDataChannels", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        //creating list of ice-servers
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);

        //initializing instance of PeerConnection class
        peerConnection = peerConnectionFactory.createPeerConnection(
                configuration,
                pcConstraints,
                peerConnectionObserver);

        //adding local stream
        peerConnection.addStream(localMediaStream);



    }

    /**
     * @return local media stream
     */
    public MediaStream getLocalMediaStream() {
        //todo handle non-initialized local media stream
        return localMediaStream;
    }

    /**
     * Visualizing media stream in videoView
     *
     * @param videoView   holder of stream
     * @param mediaStream needed to be shown
     */
    public void visualizeMediaStream(GLSurfaceView videoView, MediaStream mediaStream) {
        //todo find out what about audio stream playing
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                //callback
            }
        });

        try {
            VideoRenderer renderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);
            mediaStream.videoTracks.getFirst().addRenderer(renderer);
        } catch (Exception e) {
            String s = e.getMessage();
            Log.e(LOG_TAG, s);
        }
    }

    /**
     * Creates local media stream
     */
    public MediaStream initializeLocalMediaStream(PeerConnectionFactory peerConnectionFactory,
                                                  boolean initialize_audio, boolean initialize_video) {
        int defaultCameraID = 0;
        return initializeLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video, defaultCameraID);
    }

    /**
     * Creates local media stream
     * with specified camera name
     *
     * @param cameraName could be specified by static constants in Communicator class
     *                   can be: Communicator.FRONT_CAMERA and Communicator.BACK_CAMERA
     */
    public MediaStream initializeLocalMediaStream(PeerConnectionFactory peerConnectionFactory,
                                                  boolean initialize_audio, boolean initialize_video, String cameraName) {

        if (!cameraName.equals(Communicator.FRONT_CAMERA) || !cameraName.equals(Communicator.BACK_CAMERA)) {
            //todo change to exception sometime
            Log.e(LOG_TAG, "Wrong camera name have passed. Using default camera ID value...");
            return initializeLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video);
        }

        int numberOfCameras = VideoCapturerAndroid.getDeviceCount();

        //looking for ID of specified camera name
        //setting default ID value
        int defaultIDValue = -1;
        int cameraID = defaultIDValue;
        for (int i = 0; i < numberOfCameras; i++) {
            String camera = VideoCapturerAndroid.getDeviceName(i);
            if (camera.equals(cameraName)) {
                cameraID = i;
                break;
            }
        }
        //if there is no such camera - using default ID
        if (cameraID == defaultIDValue) {
            return initializeLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video);
        }
        return initializeLocalMediaStream(peerConnectionFactory, initialize_audio, initialize_video, cameraID);
    }

    /**
     * Creates local media stream
     * with specified camera id
     */
    public MediaStream initializeLocalMediaStream(PeerConnectionFactory peerConnectionFactory,
                                                  boolean initialize_audio, boolean initialize_video, int cameraID) {
        MediaStream localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        //forming audio stream
        if (initialize_audio) {
            MediaConstraints audioConstraints = new MediaConstraints();
            AudioSource audioSource =
                    peerConnectionFactory.createAudioSource(audioConstraints);
            AudioTrack localAudioTrack =
                    peerConnectionFactory.createAudioTrack(TRACK_ID, audioSource);
            localMediaStream.addTrack(localAudioTrack); //todo check is success
        }

        //forming video stream
        if (initialize_video) {
            //checking availability of camera with specified ID
            //if there is no such camera we can not form video stream
            if (cameraID < VideoCapturerAndroid.getDeviceCount()) {
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
