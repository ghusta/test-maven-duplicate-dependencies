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
                <!doctype html>
                <html lang="fr">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Class Finder</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">                </head>
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
                <body>
                <div class="container" id="main">
                <h2>Recherche de classe dans le classloader</h2>
                
                <form method="get" class="mt-3">
                    <div class="mb-3">
                        <label for="fqcn" class="form-label">FQCN :</label>
                        <input type="text" class="form-control" name="fqcn" size="60" autofocus id="fqcn"/>
                    </div>
                    <div class="mb-3">
                        <input type="submit" value="Chercher" class="btn btn-primary"/>
                    </div>
                </form>
                
                <div class="mt-3 alert alert-info" role="alert">
                    <b><i class="bi bi-info-circle me-2"></i>Version de Java Runtime : %s</b>
                </div>
                <hr/>
                """ .formatted(System.getProperty("java.version")));

        if (fqcn != null && !fqcn.isBlank()) {

            String resource = fqcn.replace('.', '/') + ".class";

            out.println("<b><i class=\"bi bi-search me-2 \"></i>Recherche :</b> " + resource + "<br/><br/>");

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            Enumeration<URL> urls = cl.getResources(resource);

            boolean found = false;
            int occurences = 0;

            while (urls.hasMoreElements()) {

                found = true;
                occurences++;

                URL url = urls.nextElement();

                out.println("<pre style=\"white-space: pre-wrap;\">");
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
                out.println("""
                        <div class="alert alert-warning" role="alert">
                          <b><span class="me-2">⚠️</span>Plusieurs occurences trouvées</b>
                        </div>
                        """);
            }
        }

        out.println("""
                </div>
                <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js" integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.min.js" integrity="sha384-G/EV+4j2dNv+tEPo3++6LCgdCROaejBqfUeNjuKAiuXbjrxilcCdDz6ZAVfHWe1Y" crossorigin="anonymous"></script>
                </body>
                </html>
                """);
    }
}