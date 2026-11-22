package tech.trackium.lite;

import java.net.URI;
import java.util.Locale;

final class GatewayOrigin {
    private final String origin;

    private GatewayOrigin(String origin) {
        this.origin = origin;
    }

    static GatewayOrigin parse(String value) {
        URI uri = URI.create(value.trim());
        String path = uri.getRawPath();
        if ((path != null && !path.isEmpty() && !path.equals("/")) ||
                uri.getRawQuery() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("Enter only the HTTPS Gateway origin");
        }
        return new GatewayOrigin(httpsOrigin(uri));
    }

    private static String httpsOrigin(URI uri) {
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ||
                uri.getRawUserInfo() != null || uri.getPort() > 65535 || uri.getPort() == 0) {
            throw new IllegalArgumentException("A valid HTTPS Gateway origin is required");
        }
        int port = uri.getPort();
        return "https://" + uri.getHost().toLowerCase(Locale.ROOT) +
                (port == -1 || port == 443 ? "" : ":" + port);
    }

    boolean permits(String value, boolean mainFrame) {
        try {
            URI target = URI.create(value);
            if (!origin.equals(httpsOrigin(target))) return false;
            String rawPath = target.getRawPath();
            if (!rawPath.equals(target.getPath()) || !rawPath.equals(target.normalize().getRawPath())) return false;
            if (mainFrame && target.getRawQuery() != null) return false;
            // Navigation is limited to the workspace. Its API calls use the same origin.
            return rawPath.startsWith("/app/") || (!mainFrame && rawPath.startsWith("/v1/"));
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    String workspace() { return origin + "/app/"; }

    @Override public String toString() { return origin; }
}
