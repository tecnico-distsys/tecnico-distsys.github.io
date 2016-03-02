package example.ws.impl;

import javax.jws.*;
import example.ws.*; // classes generated from WSDL

@WebService(
    endpointInterface="example.ws.Hello", 
    wsdlLocation="Hello.wsdl",
    name="Hello",
    portName="HelloImplPort",
    targetNamespace="http://ws.example/",
    serviceName="HelloImplService"
)
public class HelloImpl implements Hello {

    @Override
    public String sayHello(String name) {
        return "Hello " + name + "!";
    }

}
