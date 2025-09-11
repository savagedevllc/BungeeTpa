package net.savagedev.tpa.common.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class UpdateChecker {
    private static final String URI_BASE = "https://api.github.com";
    private static final String URI_PATH = "/repos/savagedevllc/BungeeTpa/releases";

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    private final Executor executor;

    public UpdateChecker(Executor executor) {
        this.executor = executor;
    }

    public UpdateChecker() {
        this(null);
    }

    public CompletableFuture<Info> checkForUpdateAsync() {
        if (this.executor == null) {
            return CompletableFuture.supplyAsync(this::checkForUpdate);
        }
        return CompletableFuture.supplyAsync(this::checkForUpdate, this.executor);
    }

    private Info checkForUpdate() {
        final HttpRequest request = HttpRequest.newBuilder(URI.create(URI_BASE + URI_PATH)).GET()
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .build();

        final HttpResponse<InputStream> response;
        try {
            response = this.httpClient.send(request, BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        final JsonElement root = JsonParser.parseReader(new InputStreamReader(response.body()));

        if (root == null || root.isJsonNull()) {
            return null;
        }

        final JsonArray rootArr = root.getAsJsonArray();

        if (rootArr == null || rootArr.isJsonNull()) {
            return null;
        }

        final JsonObject container = rootArr.get(0).getAsJsonObject();

        if (container == null || container.isJsonNull()) {
            return null;
        }

        return new Info(container.get("name").getAsString(), container.get("body").getAsString(),
                container.get("html_url").getAsString(), container.get("prerelease").getAsBoolean(),
                container.get("tag_name").getAsString());
    }

    public static class Info {
        private final String name;
        private final String desc;
        private final String url;
        private final boolean prerelease;
        private final String tag;

        private Info(String name, String desc, String url, boolean prerelease, String tag) {
            this.name = name;
            this.desc = desc;
            this.url = url;
            this.prerelease = prerelease;
            this.tag = tag;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.desc;
        }

        public String getDownloadUrl() {
            return this.url;
        }

        public boolean isPrerelease() {
            return this.prerelease;
        }

        public String getTag() {
            return this.tag;
        }

        public boolean isUpdateAvailable(String currentTag) {
            return !this.tag.equals(currentTag);
        }
    }
}
