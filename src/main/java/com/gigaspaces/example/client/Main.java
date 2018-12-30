package com.gigaspaces.example.client;

import com.gigaspaces.example.IO;
import com.gigaspaces.example.Service;
import com.gigaspaces.lrmi.nio.async.FutureContext;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        BasicConfigurator.configure();
        Service service = IO.read("service.ser");
        String reply = service.echo("- foo -");
        logger.info("reply is: " + reply);
        service.sendMessage("one way message");
        service.toUpper("message");
        Future res = FutureContext.getFutureResult();
        String result = (String) res.get();
        FutureContext.clear();
        logger.info("got async result: " + result);

    }
}
