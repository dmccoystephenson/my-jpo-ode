package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.util.CodecUtils;

public class IntersectionParser extends LogFileParser {
	public static final int INTERSECTION_STATUS_LENGTH = 1;
	public static final int INTERSECTION_ID_LENGTH = 4;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected LogIntersection intersection;

	public IntersectionParser() {
		super();
	}

	@Override
	public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {

		ParserStatus status = ParserStatus.INIT;

		try {
			this.intersection = new LogIntersection();

			// Step 1 - parse intersection.intersectionStatus
			if (getStep() == 0) {
				status = parseStep(bis, INTERSECTION_STATUS_LENGTH);
				if (status != ParserStatus.COMPLETE)
					return status;
				intersection.setIntersectionStatus(readBuffer[0]);
			}

			// Step 2 - parse intersection.intersectionId
			if (getStep() == 1) {
				status = parseStep(bis, INTERSECTION_ID_LENGTH);
				if (status != ParserStatus.COMPLETE)
					return status;
				intersection.setIntersectionId(
						CodecUtils.bytesToInt(readBuffer, 0, INTERSECTION_ID_LENGTH, ByteOrder.LITTLE_ENDIAN));
			}

			resetStep();
			status = ParserStatus.COMPLETE;
		} catch (Exception e) {
			throw new FileParserException(String.format("Error parsing %s on step %d", fileName, getStep()), e);
		}

		return status;
	}

	public LogIntersection getIntersection() {
		return intersection;
	}

	public void setIntersection(LogIntersection intersection) {
		this.intersection = intersection;
	}
	 public void setIntersection(byte code) {
	      try {
	    	  this.intersection.setIntersectionStatus(code);
	      } catch (Exception e) {
	         logger.error("Intersection status value error, Invalid value {}: ", 
	            code);
	      }
	   }
	@Override
	public void writeTo(OutputStream os) throws IOException {
		os.write(CodecUtils.intToBytes(intersection.getIntersectionStatus(), ByteOrder.LITTLE_ENDIAN));
		os.write(CodecUtils.intToBytes(intersection.getIntersectionId(), ByteOrder.LITTLE_ENDIAN));
	}
}