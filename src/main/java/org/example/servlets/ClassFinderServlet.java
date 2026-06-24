package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;

@WebServlet("/class-finder")
public class ClassFinderServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String fqcn = request.getParameter("fqcn");

        var out = response.getWriter();

        out.println("""
                <html>
                <head>
                  <title>Class Finder</title>
                </head>
                <body>
                <h2>Recherche de classe dans le classloader</h2>
                
                <form method="get">
                  FQCN :
                  <input type="text" name="fqcn" size="60" autofocus/>
                  <input type="submit" value="Chercher"/>
                </form>
                
                <b>Version de Java Runtime : %s</b><br/>
                <hr/>
                """ .formatted(System.getProperty("java.version")));

        if (fqcn != null && !fqcn.isBlank()) {

            String resource = fqcn.replace('.', '/') + ".class";

            out.println("<b>Recherche :</b> " + resource + "<br/><br/>");

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            Enumeration<URL> urls = cl.getResources(resource);

            boolean found = false;
            int occurences = 0;

            while (urls.hasMoreElements()) {

                found = true;
                occurences++;

                URL url = urls.nextElement();

                out.println("<pre>");
                out.println("URL : " + url);

                if ("jar".equals(url.getProtocol())) {
                    JarURLConnection conn = (JarURLConnection) url.openConnection();
                    out.println("JAR : " + conn.getJarFileURL());
                } else if ("file".equals(url.getProtocol())) {
                    File file = new File(url.getPath());
                    out.println("Classe fichier : " + file.getAbsolutePath());
                }

                out.println("</pre><br/>");
            }

            if (!found) {
                out.println("<b>Aucun résultat</b>");
            }
            if (occurences > 1) {
                out.println("<b>⚠️ Plusieurs occurences trouvées</b>");
            }
        }

        out.println("""
                </body>
                </html>
                """);
    }
}