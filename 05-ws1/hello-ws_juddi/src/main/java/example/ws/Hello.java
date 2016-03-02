package example.ws;

import javax.jws.*;

@WebService
public interface Hello {

    @WebMethod String sayHello(String name);

}
