package com.gigaspaces.example.server;


import com.gigaspaces.example.Service;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;

public class ServiceImpl implements Service {
    private static final Logger logger = Logger.getLogger(ServiceImpl.class);

    @Override
    public String echo(String msg) throws RemoteException {
        return msg;
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        logger.info("got a one way message '" + message + "'");
    }

    @Override
    public String toUpper(String message) throws RemoteException {
        if (message != null){
            return message.toUpperCase();
        }
        return message;
    }
}
