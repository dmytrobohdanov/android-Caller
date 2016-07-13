package com.dmytro.caller;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

public class ConnectionsObserver implements PeerConnection.Observer{
    private static ConnectionsObserver instance;

    private ConnectionsObserver(){}

    public static ConnectionsObserver getInstance(){
        if (instance == null) {
            instance = new ConnectionsObserver();
        }
        return instance;
    }
    /**
     * Triggered when media is received on a new stream from remote peer.
     */
    @Override
    public void onAddStream(MediaStream mediaStream) {

    }

    /**
     * Triggered when a remote peer opens a DataChannel.
     */
    @Override
    public void onDataChannel(DataChannel dataChannel) {

    }

    /**
     *  Triggered when a new ICE candidate has been found.
     */
    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {

    }

    /**
     * Triggered when the IceConnectionState changes.
     */
    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

    }

    /**
     *  Triggered when the IceGatheringState changes.
     */
    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

    }

    /**
     *  Triggered when a remote peer close a stream.
     */
    @Override
    public void onRemoveStream(MediaStream mediaStream) {

    }

    /**
     * Triggered when renegotiation is necessary.
     */
    @Override
    public void onRenegotiationNeeded() {

    }

    /**
     * Triggered when the SignalingState changes.
     */
    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {

    }
}
