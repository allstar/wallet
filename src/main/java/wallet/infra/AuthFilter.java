package wallet.infra;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.util.*;

public class AuthFilter implements ContainerRequestFilter {
    private static final Map<String, List<String>> ROLES = new HashMap<>();

    static {
        ROLES.put("web", Collections.singletonList("WEB"));
        ROLES.put("roulette", Collections.singletonList("GAME"));
        ROLES.put("classic-slot", Collections.singletonList("GAME"));
    }

    private static final Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("web", "web");
        USERS.put("roulette", "roulette");
        USERS.put("classic-slot", "classic-slot");
    }

    @Override
    public void filter(ContainerRequestContext context) {
        String username = authenticate(context);
        authorize(username, context.getMethod());
    }

    private static void authorize(String username, String method) {
        List<String> userRoles = ROLES.get(username);

        switch (method) {
            case "GET":
                if (!userRoles.contains("WEB") && !userRoles.contains("GAME")) {
                    forbidden();
                }
                break;
            default:
                if (!userRoles.contains("GAME")) {
                    forbidden();
                }
                break;
        }
    }

    private static String unauthorised() {
        throw new WebApplicationException(Response.status(401).build());
    }

    private static void forbidden() {
        throw new WebApplicationException(Response.status(403).build());
    }

    private static String authenticate(ContainerRequestContext context) {
        String token = new String(Base64.getDecoder().decode(getBearerToken(context)));
        String username = token.replaceFirst(":.*$", "");
        String password = token.replaceFirst("^.*:", "");
        if (!USERS.containsKey(username) || !USERS.get(username).equals(password)) {
            unauthorised();
        }
        return username;
    }

    private static String getBearerToken(ContainerRequestContext context) {
        String authorization = context.getHeaderString("Authorization");

        if (authorization == null) {
            return unauthorised();
        }

        return authorization.replaceFirst("^Basic ", "");
    }

}
