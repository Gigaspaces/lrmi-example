package com.gigaspaces.example;

import java.io.*;
import java.rmi.Remote;

public class IO {
    public static void write (Remote remote, String path) throws IOException {
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))){
            os.writeObject(remote);
        }
    }
    public static <T extends Remote> T read(String path) throws IOException, ClassNotFoundException {
        try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))){
            return (T) is.readObject();
        }

    }
}
