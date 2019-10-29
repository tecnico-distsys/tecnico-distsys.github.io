package example.ws.handler;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the endpoint address of messages, if available.
 */
public class EndpointAddressHandler implements SOAPHandler<SOAPMessageContext> {

	//
	// Handler interface implementation
	//

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of in-bound and
	 * out-bound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		printEndpointAddress(smc);
		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		printEndpointAddress(smc);
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}

	/**
	 * Print the endpoint address (URL), if available.
	 */
	private void printEndpointAddress(SOAPMessageContext smc) {
		String endpointAddress = (String) smc.get(ENDPOINT_ADDRESS_PROPERTY);

		// return if the property does not exist (e.g. server-side)
		if (endpointAddress == null)
			return;

		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound)
			System.out.println("Message destination: " + endpointAddress);
		else
			System.out.println("Message source: " + endpointAddress);
	}

}
