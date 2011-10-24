package org.ned.server.nedadminconsole.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ned.server.nedadminconsole.datasource.PostgresConnection;

public class NedStatisticsFileServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 2030641733778432400L;
    private static final String filename = "statistics.csv";

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ServletOutputStream outputStream = response.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(filename);
        PrintWriter writer = new PrintWriter(outputStream);


            response.setContentType((mimetype != null) ? mimetype
                    : "application/octet-stream");
            //response.setContentLength(statisticsBytes.length);
            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + filename + "\"");

            writeStatistics(writer);

        writer.close();
        outputStream.flush();
        outputStream.close();

    }

    private void writeStatistics(PrintWriter writer) {

        PostgresConnection connection = new PostgresConnection();

        try {
            connection.getFullStatistics(writer);
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            Logger.getLogger(NedStatisticsFileServlet.class.getName()).log(
                    Level.SEVERE, ex.getMessage(), ex);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Statistics upload servlet";
    }// </editor-fold>

}
