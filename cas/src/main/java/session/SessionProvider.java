package session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public interface SessionProvider {

    void setAttribute(HttpServletRequest request, HttpServletResponse response, String key, Serializable value);

    Serializable getAttribute(HttpServletRequest request, HttpServletResponse response, String key);

    void casLogout(String sessionId);

    String getSessionId(HttpServletRequest request, HttpServletResponse response);

}
