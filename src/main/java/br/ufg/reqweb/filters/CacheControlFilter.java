/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.filters;

import br.ufg.reqweb.components.UsuarioBean;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author andre
 */
public class CacheControlFilter implements Filter {
    
    private UsuarioBean usuarioBean;
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String path = httpRequest.getContextPath();
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
        
        usuarioBean = session == null ? null: (UsuarioBean) session.getAttribute("usuarioBean");        
        if (usuarioBean != null && usuarioBean.isAutenticado()) {
            URI uri = URI.create(usuarioBean.home());
            String url = Paths.get(path, String.format("%s%s?%s", uri.getPath(), ".xhtml", uri.getQuery())).toString();
            System.out.println(
                    String.format(
                            "usuario:%s logged in - no cache for login page -> redirecting to %s",
                            usuarioBean.getSessionUsuario().getLogin(),
                            url)
            );
            httpResponse.sendRedirect(url);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

}
