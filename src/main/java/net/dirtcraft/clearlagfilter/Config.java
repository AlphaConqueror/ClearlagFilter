package net.dirtcraft.clearlagfilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.dirtcraft.clearlagfilter.utils.Logger;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class Config {

    private static final String ASSET_LIST_PATH = "asset-list.txt";
    private static final String CONFIG_DIRECTORY_PATH = "plugins/ClearlagFilter/";
    private static List<String> filter;

    public static void read() throws IOException {
        try (final InputStream jsonStream =
                     Objects.requireNonNull(Files.newInputStream(Paths.get(
                             CONFIG_DIRECTORY_PATH + "config.json")));
             final InputStream schemaStream
                     = Objects.requireNonNull(Config.class.getResourceAsStream("/schema.json"))) {
            final ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root;

            Logger.info("Validating 'config.json' ...");

            try {
                root = objectMapper.readTree(jsonStream);

                final JsonSchemaFactory schemaFactory =
                        JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
                final JsonSchema schema = schemaFactory.getSchema(schemaStream);
                final Set<ValidationMessage> validationResult = schema.validate(root);

                if (validationResult.isEmpty()) {
                    Logger.info("Validation successful.");
                } else {
                    Logger.warn("Validation failed: %s",
                            validationResult.stream().map(ValidationMessage::getMessage)
                                    .collect(Collectors.joining()));
                    Logger.warn("Using default config instead.");
                    root = objectMapper.readTree(
                            Objects.requireNonNull(
                                    Config.class.getResourceAsStream("/config.json")));
                }
            } catch (final JsonParseException e) {
                Logger.error("Validation failed: %s", e.getMessage());
                Logger.error("Using default config instead.");
                root = objectMapper.readTree(Objects.requireNonNull(
                        Config.class.getResourceAsStream("/config.json")));
            }

            final ObjectReader reader =
                    objectMapper.readerFor(new TypeReference<List<String>>() {});

            filter = reader.readValue(root.get("filter"));
        }
    }

    public static void copyDataIfNotExistent() throws IOException {
        try (final BufferedReader listFile = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Cli.class.getResourceAsStream("/" + Config.ASSET_LIST_PATH)),
                StandardCharsets.UTF_8))) {

            String assetResource;

            while ((assetResource = listFile.readLine()) != null) {
                try (final InputStream asset = Cli.class.getResourceAsStream("/" + assetResource)) {
                    final Path path = Paths.get(Config.CONFIG_DIRECTORY_PATH + assetResource);
                    final Path parent = path.getParent();

                    if (parent != null) {
                        FileUtils.forceMkdir(parent.toFile());
                    }

                    try {
                        assert asset != null;
                        Files.copy(asset, path);
                    } catch (final FileAlreadyExistsException ignored) {
                        Logger.info("File '%s' already exists, ignoring it.", path);
                    }
                }
            }
        }
    }

    public static List<String> getFilter() {
        return filter;
    }

    public static void onDisable() {
        filter.clear();
    }
}
