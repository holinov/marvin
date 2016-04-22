package org.fruttech.marvin.http;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.fruttech.marvin.AdmBot;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class NotificationHandler extends AbstractHandler {

    private final AdmBot admBot;

    public NotificationHandler(AdmBot admBot) {
        this.admBot = admBot;
    }

    @Override
    public void handle(String target, Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        final Map<String, String[]> parameterMap = request.getParameterMap();

        response.setContentType("text/html; charset=utf-8");

        if (parameterMap.containsKey("msg") && parameterMap.containsKey("grp")) {
            final String msg = parameterMap.get("msg")[0];
            final String grp = parameterMap.get("grp")[0];

            if (admBot.sendMessage(grp, msg)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("ok");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("chat group " + grp + " not found");
            }

        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("bad params");
        }
        baseRequest.setHandled(true);
    }
}