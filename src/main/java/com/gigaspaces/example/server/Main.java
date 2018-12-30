package com.gigaspaces.example.server;


import com.gigaspaces.config.lrmi.nio.NIOConfiguration;
import com.gigaspaces.example.IO;
import com.gigaspaces.example.Service;
import com.gigaspaces.lrmi.GenericExporter;
import net.jini.export.Exporter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.rmi.Remote;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws IOException, InterruptedException {
        BasicConfigurator.configure();
        Exporter exporter = new GenericExporter(NIOConfiguration.create());
        Service service = new ServiceImpl();
        Remote serviceStub = exporter.export( service );
        IO.write(serviceStub, "service.ser");
        logger.info("exported: " + serviceStub);
        //noinspection InfiniteLoopStatement
        while (true){
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}
