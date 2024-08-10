package org.setms.resin.format;

import org.setms.resin.process.SoftwareProcess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;


/**
 * Deserializes a {@link SoftwareProcess} from some external representation.
 */
public interface SoftwareProcessDeserialization extends Function<InputStream, SoftwareProcess> {

    default SoftwareProcess apply(String text) {
        return apply(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    default SoftwareProcess apply(byte[] bytes) {
        return apply(new ByteArrayInputStream(bytes));
    }

    default SoftwareProcess apply(File file) {
        try (var input = new FileInputStream(file)) {
            return apply(input);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read software process from " + file, e);
        }
    }

}
