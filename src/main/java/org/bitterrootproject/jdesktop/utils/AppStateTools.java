package org.bitterrootproject.jdesktop.utils;

import lombok.extern.log4j.Log4j2;


/**
 * This class helps manage the application state. Currently, it only helps fetch the runtime context (dev/prod).
 */
@Log4j2
public class AppStateTools {
	public enum RuntimeContext {
		DEV,
		PROD;
		
		@Override
		public String toString() {
			return this.name();
		}
	}
	
	public static AppStateTools.RuntimeContext getRuntimeContext() throws RuntimeException {
		if (EnvTools.getBoolean("DEV", false)) {
			log.info("Selected runtime context: DEV");
			return AppStateTools.RuntimeContext.DEV;
		} else {
			log.info("Selected runtime context: PROD");
			return AppStateTools.RuntimeContext.PROD;
		}
	}
}
