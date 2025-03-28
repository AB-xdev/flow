/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.flow.plugin.maven.it;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Support class to be uses in verification scripts executed by maven invoker
 * plugin when running integration tests.
 */
public class IntegrationTestHelper {

    private static final String IMPORT_TEMPLATE = "import '%s';";

    /**
     * Checks that javascript modules has been found during production build and
     * correctly imported into the generated bundle.
     * <p>
     * </p>
     * Helper for {@literal src/it/frontend-scanner-tuning-project} test. It
     * expects that the test projects copies frontend generated import file and
     * chunks into {@code importsDir} folder, and checks if the import statement
     * for javascript modules in {@code expectations} are present. The
     * expectations are grouped by the profile used to execute the build. The js
     * module should be a path relative to 'Frontend/generated/jar-resources'.
     * Prepend the js module with an exclamation mark, to invert the check and
     * ensure the module is NOT imported (e.g. {@code !my-module.js}).
     *
     * <pre>
     * generated-imports/
     * ├── all-deps
     * │ ├── chunks
     * │ ├── chunk-92e7c77c450ec2b4897a66279506d76cfbbba9edd8ac2fddb2d50decf23a5089.js
     * │ │ └── chunk-b87575f9f7d0e4bad37bd5e479c4bac0bec6cb0baca59ae6c1dd366799ece23f.js
     * │ └── generated-flow-imports.js
     * ├── exclude-alpha
     * │ ├── chunks
     * │ │ └── chunk-b87575f9f7d0e4bad37bd5e479c4bac0bec6cb0baca59ae6c1dd366799ece23f.js
     * │ └── generated-flow-imports.js
     * ├── not-optimized
     * │ └── all-deps
     * │     └── generated-flow-imports.js
     * └── optimized
     *     └── all-deps
     *         ├── chunks
     *         │ ├── chunk-92e7c77c450ec2b4897a66279506d76cfbbba9edd8ac2fddb2d50decf23a5089.js
     *         │ └── chunk-b87575f9f7d0e4bad37bd5e479c4bac0bec6cb0baca59ae6c1dd366799ece23f.js
     *         └── generated-flow-imports.js
     * </pre>
     * <p>
     * </p>
     *
     * Example invocation:
     *
     * <pre>
     * {@code
     * Map<String, List<String>> verifications = new HashMap<>();
     * verifications.put("all-deps", List.of("alpha.js", "beta.js"));
     * verifications.put("exclude-alpha", List.of("!alpha.js", "beta.js"));
     *
     * Path path = Paths.get("generated-imports");
     * System.out.println("Checking imports in " + path.toAbsolutePath());
     * IntegrationTestHelper.verifyImports(path, verifications);
     * }
     * </pre>
     *
     * @param importsDir
     *            path to folder where generated imports are stored.
     * @param expectations
     *            js modules to check.
     */
    public static void verifyImports(Path importsDir,
            Map<String, List<String>> expectations) {
        try {
            for (Map.Entry<String, List<String>> check : expectations
                    .entrySet()) {
                String profile = check.getKey();
                List<String> jsModules = check.getValue();

                // Concat all import
                // Not optimized bundle
                Path importFile = importsDir.resolve(Path.of("not-optimized",
                        profile, "generated-flow-imports.js"));
                try {
                    verifyImports(readFile(importFile), jsModules);
                } catch (AssertionError e) {
                    throw new AssertionError(e.getMessage()
                            + " in not optimized bundle generated by profile '"
                            + profile + "' (" + importFile + ")");
                }

                // Optimize bundle
                importFile = importsDir.resolve(Path.of("optimized", profile,
                        "generated-flow-imports.js"));
                String allImports = readFile(importFile);
                Path chunksDir = importsDir
                        .resolve(Path.of("optimized", profile, "chunks"));
                if (Files.exists(chunksDir)) {
                    allImports += "\n" + Files.list(chunksDir)
                            .filter(file -> file.getFileName().toString()
                                    .startsWith("chunk-"))
                            .map(IntegrationTestHelper::readFile)
                            .collect(Collectors.joining("\n"));
                }
                try {
                    verifyImports(allImports, jsModules);
                } catch (AssertionError e) {
                    throw new AssertionError(e.getMessage()
                            + " in optimized bundle generated by profile '"
                            + profile + "' (" + importFile + " and " + chunksDir
                            + "/*)");
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void verifyImport(String source, String jsModule) {
        boolean negate = jsModule.startsWith("!");
        if (negate) {
            jsModule = jsModule.substring(1);
        }
        boolean hasImport = source
                .contains(IMPORT_TEMPLATE.formatted(jsModule));
        if (hasImport && negate) {
            throw new AssertionError(
                    "Import of '" + jsModule + "' should not be present");
        } else if (!hasImport && !negate) {
            throw new AssertionError(
                    "Import of '" + jsModule + "' is expected but not present");
        }
    }

    private static void verifyImports(String source, List<String> jsModules)
            throws IOException {
        for (String jsModule : jsModules) {
            verifyImport(source, jsModule);
        }
    }

    private static String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
