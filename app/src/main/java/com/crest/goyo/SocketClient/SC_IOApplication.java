package com.crest.goyo.SocketClient;


import com.crest.goyo.Utils.Global;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


/**
 * Created by mTech on 18-Apr-2017.IO.
 */
public class SC_IOApplication {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Global.SOCKET_URL);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}