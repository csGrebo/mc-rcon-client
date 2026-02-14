package net.csgrebo.mccontrol.util;

import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;

public abstract class PosixUtils {

    private static final POSIX posix = POSIXFactory.getPOSIX();

    private PosixUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getHostname() {
        return posix.gethostname();
    }
}
