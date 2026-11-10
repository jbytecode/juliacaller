package org.expr.juliacaller;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilitiesTest {

    @Test
    public void findsExecutableOnPath() throws Exception {
        Path directory = Files.createTempDirectory("julia-path-test");
        Path executable = Files.createFile(directory.resolve("julia"));
        assertTrue(executable.toFile().setExecutable(true));
        try {
            assertEquals(executable.toString(), Utilities.findOnPath("julia", directory.toString()));
        } finally {
            Files.delete(executable);
            Files.delete(directory);
        }
    }

    @Test
    public void returnsNullWhenExecutableIsNotOnPath() {
        assertNull(Utilities.findOnPath("julia", null));
    }
}
