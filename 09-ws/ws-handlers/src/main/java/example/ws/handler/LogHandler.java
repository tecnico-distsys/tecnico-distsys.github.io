package example.ws.handler;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class LogHandler implements SOAPHandler<SOAPMessageContext> {

	/** Date formatter used for outputting time stamp in ISO 8601 format. */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
		logSOAPMessage(smc, System.out);
		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		logSOAPMessage(smc, System.out);
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
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
	 * outgoing or incoming message. Write the SOAP message to the print stream. The
	 * writeTo() method can throw SOAPException or IOException.
	 */
	private void logSOAPMessage(SOAPMessageContext smc, PrintStream out) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// print current time stamp
		Date now = new Date();
		out.print(dateFormat.format(now));
		// print SOAP message direction
		out.println(" " + (outbound ? "OUT" : "IN") + "bound SOAP message:");

		// print SOAP message contents
		SOAPMessage message = smc.getMessage();
		try {
			message.writeTo(out);
			// print a newline after message
			out.println();

		} catch (SOAPException se) {
			out.print("Ignoring SOAPException in handler: ");
			out.println(se);
		} catch (IOException ioe) {
			out.print("Ignoring IOException in handler: ");
			out.println(ioe);
		}
	}

}
