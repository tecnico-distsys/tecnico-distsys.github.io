package example.ws.handler;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of the message context for inbound and
 * outbound messages.
 */
public class MessageContextHandler implements SOAPHandler<SOAPMessageContext> {

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
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		printMessageContext(smc, System.out);
		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		printMessageContext(smc, System.out);
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
	 * Iterate the SOAP message context properties and print each one in
	 * (scope,key,value) format.
	 */
	private void printMessageContext(MessageContext map, PrintStream out) {
		Boolean outbound = (Boolean) map.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		out.print("Context for ");
		out.print(outbound ? "OUTbound" : "INbound");
		out.println(" message: (scope,key,value)");
		try {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				Object value = map.get(key);

				String keyString;
				if (key == null)
					keyString = "null";
				else
					keyString = key.toString();

				String valueString;
				if (value == null)
					valueString = "null";
				else
					valueString = value.toString();

				String scopeString;
				try {
					Object scope = map.getScope(keyString);
					if (scope == null) {
						scopeString = "null";
					} else {
						scopeString = scope.toString();
						scopeString = scopeString.toLowerCase();
					}
				} catch (IllegalArgumentException e) {
					// workaround for bug in java 1.8.0_121
					// some properties throw IAE when trying to get scope
					scopeString = "IllegalArgumentException";
				}

				out.println("(" + scopeString + "," + keyString + "," + valueString + ")");
			}

		} catch (Exception e) {
			out.printf("Exception while printing message context: %s%n", e);
		}
	}

}
