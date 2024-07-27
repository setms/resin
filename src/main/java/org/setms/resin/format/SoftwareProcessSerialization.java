package org.setms.resin.format;

import org.setms.resin.process.SoftwareProcess;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;


public interface SoftwareProcessSerialization extends Function<InputStream, SoftwareProcess> {

    default SoftwareProcess apply(String text) {
        return apply(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    default SoftwareProcess apply(byte[] bytes) {
        return apply(new ByteArrayInputStream(bytes));
    }

}
