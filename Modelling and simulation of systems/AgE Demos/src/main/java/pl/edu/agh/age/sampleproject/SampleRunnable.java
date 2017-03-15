package pl.edu.agh.age.sampleproject;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This configuration prints sample string into the *.log file.
 */
public final class SampleRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SampleRunnable.class);

	@Override
	public void run() {
		logger.info("This is my own runnable!");
	}

	@Override
	public String toString() {
		return toStringHelper(this).toString();
	}

}
