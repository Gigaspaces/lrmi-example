package com.gigaspaces.example;

import com.gigaspaces.annotation.lrmi.AsyncRemoteCall;
import com.gigaspaces.annotation.lrmi.OneWayRemoteCall;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Service extends Remote {
    String echo (String msg) throws RemoteException;
    @OneWayRemoteCall
    void sendMessage(String message) throws RemoteException;
    @AsyncRemoteCall
    String toUpper(String message) throws RemoteException;
}
