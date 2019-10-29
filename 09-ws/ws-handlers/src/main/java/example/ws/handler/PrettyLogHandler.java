package example.ws.handler;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class PrettyLogHandler implements SOAPHandler<SOAPMessageContext> {

	/** Date formatter used for outputting time stamp in ISO 8601 format. */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	/** XML transformer factory. */
	private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	/** XML transformer property name for XML indentation amount. */
	private static final String XML_INDENT_AMOUNT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
	/** XML indentation amount to use (default=0). */
	private static final Integer XML_INDENT_AMOUNT_VALUE = 2;

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
	 * outgoing or incoming message. Write the SOAP message to the print stream with
	 * indentation for easier reading.
	 */
	private void logSOAPMessage(SOAPMessageContext smc, PrintStream out) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// print current time stamp
		Date now = new Date();
		out.print(dateFormat.format(now));
		// print SOAP message direction
		out.println(" " + (outbound ? "OUT" : "IN") + "bound SOAP message:");

		// pretty-print SOAP message contents
		try {
			SOAPMessage message = smc.getMessage();
			Source src = message.getSOAPPart().getContent();

			// transform the (DOM) Source into a StreamResult
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(XML_INDENT_AMOUNT_PROPERTY, XML_INDENT_AMOUNT_VALUE.toString());
			StreamResult result = new StreamResult(out);
			transformer.transform(src, result);

		} catch (SOAPException se) {
			out.print("Ignoring SOAPException in handler: ");
			out.println(se);
		} catch (TransformerConfigurationException tce) {
			out.print("Unable to transform XML into text due to configuration: ");
			out.println(tce);
		} catch (TransformerException te) {
			out.print("Unable to transform XML into text: ");
			out.println(te);
		}
	}

}
