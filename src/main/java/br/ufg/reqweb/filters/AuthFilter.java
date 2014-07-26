/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.filters;

import br.ufg.reqweb.components.UsuarioBean;
import br.ufg.reqweb.model.PerfilEnum;
import java.io.IOException;
import javax.faces.application.ViewExpiredException;
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
public class AuthFilter implements Filter {
    
    private UsuarioBean usuarioBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String path = httpRequest.getContextPath();
        try {
            usuarioBean = (UsuarioBean) session.getAttribute("usuarioBean");
            if (usuarioBean.isAutenticado()) {
                String url = httpRequest.getRequestURI();
                System.out.println(String.format("url: %s", url));
                if (isUrlAuthorized(url)) {
                    chain.doFilter(request, response);
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                httpResponse.sendRedirect(String.format("%s/index.jsp", path));
            }
        } catch (NullPointerException | ViewExpiredException e) {
            httpResponse.sendRedirect(String.format("%s/index.jsp", path));
            System.out.println(String.format("error: %s", e.getCause()));            
        }
    }

    @Override
    public void destroy() {
    }
    
    private boolean isUrlAuthorized(String url) {
        boolean granted = false;
        String commonDir = "/views/secure/common/all";
        String staffCommonDir = "/views/secure/common/staff";
        boolean isStaff = !usuarioBean.getPerfil().equals(PerfilEnum.DISCENTE);
        if (url.contains(usuarioBean.homeDir())) {
            granted = true;
        }
        if (url.contains(staffCommonDir) && isStaff) {
            granted = true;
        }
        if (url.contains(commonDir)) {
            granted = true;
        }
        return  granted;
    }

}
