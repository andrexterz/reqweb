/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.auth;

import br.ufg.reqweb.components.UsuarioBean;
import java.io.IOException;
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
public class LoginFilter implements Filter  {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest =(HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;        
        HttpSession session = httpRequest.getSession();
        UsuarioBean usuarioBean = (UsuarioBean) session.getAttribute("usuarioBean");
        if (usuarioBean == null) {
            usuarioBean = new UsuarioBean();
        }
        String path = httpRequest.getContextPath();
        String url = httpRequest.getRequestURI();
        if (usuarioBean.isAutenticado()) {
            chain.doFilter(request, response);
            System.out.println("url: " + url);
        } else {
            httpResponse.sendRedirect(path + "/index.jsp");
        }
    }

    @Override
    public void destroy() {
    }
    
    

}