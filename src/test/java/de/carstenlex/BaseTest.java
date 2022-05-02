package de.carstenlex;

import java.io.IOException;
import java.io.InputStream;

public abstract class BaseTest {
    protected String loadResourceAsString(String resourceName) throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(resourceName);
        byte[] bytes = resourceAsStream.readAllBytes();
        String content = new String(bytes);
        return content;
    }
}
