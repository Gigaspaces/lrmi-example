# lrmi-example

## Running from within Idea.

1. Run Server.Main
2. Run Client.Main

### Remote Objects Interface

1. Should extends [Remote](https://docs.oracle.com/javase/8/docs/api/java/rmi/Remote.html) each remote method should throws [RemoteException](https://docs.oracle.com/javase/8/docs/api/index.html?java/rmi/RemoteException.html), example [Service.java](https://github.com/Gigaspaces/lrmi-example/blob/master/src/main/java/com/gigaspaces/example/Service.java)

2. Control flow  can be changed using annotations on remote method interfaces `@OneWayRemoteCall` mark method as one way (not waiting for an answer), `AsyncRemoteCall` returns the result in a Java Future. 

### The basic Idea of RMI

1. Configuration of LRMI is done using the class `NIOConfiguration` this configuration is passed in when the Exporter is created and from there it passed on to the clients `Exporter exporter = new GenericExporter(NIOConfiguration.create());`.

2. The [GenericExporter](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/GenericExporter.java)  is used to export implementation of remote, it is doing couple of things:

   * [Save](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/GenericExporter.java#L290) the handled object in an LRMI map to be used when invoked from network (or embedded)
   
   * Index all the method for fast access.
   
   * [Create](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/GenericExporter.java#L288) and return an Remote object that when serialized save the address and Id to the original object, this is actually the proxy that can passed to the clients, it can passed serialized as a sequence of bytes throw a global repository in the network or as in our example in a file.

3. The Proxy [Generation](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/LRMIRuntime.java#L317)

   The idea is to use Java [DynamicProxy](https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html) and provide an [InvocationHandler](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationHandler.html) that know how to call back to the server using local LRMI.
   
   Our Invocation handler is the class [DynamicSmartStub](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/DynamicSmartStub.java#L89) it is serailized so it can be sent to remote clients as blob and handle the calls back to the server using its (invoke)[https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/DynamicSmartStub.java#L412] method.
   
   It is wrapped inside a Dynamic proxy it implements all the Interfaces that marked as Remote in the exported Object.
   
   It uses the [writeExternal](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/DynamicSmartStub.java#L517) / (readExternal)[https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/DynamicSmartStub.java#L587] trick to execute code when deserialize, the local LRMI [node](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/DynamicSmartStub.java#L492) is created from this class if needed.
           
### Metworking.

#### Client side
[Initialization](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/PAdapter.java#L99) is triggered by call on proxy method
Connection to the server is done using the class [CPeer](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/CPeer.java)
The main method to send a request to the server is [invoke](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/CPeer.java#L631)
The class uses [Reader and Writer](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/CPeer.java#L123) to send and read bytes to the other side.
Using [handshake](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/CPeer.java#L591) to prevent buffer overflow when receiving broken messages.
And [write bytes](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/Writer.java#L381)  


#### Server side
[Pivot](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/Pivot.java#L237) is the class that manage the socket at the server side
[handleRequest](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/Pivot.java#L554) is the method that get the RequestPacket dispatch it to the remote object and send the result [replyPacket](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/Pivot.java#L587)
[consumeAndHandleRequest] is where the Pivot forward the invoke to the [RMI Runtime](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/Pivot.java#L494)
Each ReadSelectorThread holds an internal [Pivot that being used](https://github.com/xap/xap/blob/master/xap-core/xap-datagrid/src/main/java/com/gigaspaces/lrmi/nio/selector/handler/ReadSelectorThread.java#L62) whenever there are some bytes in the channel (socket)              
    