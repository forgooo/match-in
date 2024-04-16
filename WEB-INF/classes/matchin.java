import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "matchin", urlPatterns = { "/matchin" })
@MultipartConfig(
  fileSizeThreshold = 1024 * 1024 * 1,
  maxFileSize = 1024 * 1024 * 10,
  maxRequestSize = 1024 * 1024 * 100
)
public class matchin extends HttpServlet {
  private String uploadDirectory;
  private String pythonScriptPath;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    uploadDirectory = config.getServletContext().getInitParameter("uploadDirectory");
    pythonScriptPath = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/face_detect.py";
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html><head><title>matchin</title></head>");
    out.println("<body>");
    out.println("<form method='post' enctype='multipart/form-data'><input type='file' name='file' /><input type='submit' value='Upload' /></form>");
    out.println("</body></html>");
    out.close();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    File directory = new File(uploadDirectory);
    if (!directory.exists()) {
        directory.mkdirs();
    }

    Part filePart = request.getPart("file");
    String fileName = filePart.getSubmittedFileName();
    String filePath = uploadDirectory + fileName;
    filePart.write(filePath);

    String outputFileName = fileName.split("\\.")[0] + "_output.jpg";

    Process process = Runtime.getRuntime().exec("/usr/bin/python3 /opt/tomcat/webapps/ROOT/WEB-INF/classes/face_detect.py " + filePath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    String line;
    String erout = "";
    while ((line = reader.readLine()) != null) {
        erout += line;
    }

    File outputFile = new File(uploadDirectory + outputFileName);
    if (outputFile.exists()) {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Uploaded Image</title></head>");
        out.println("<body>");
        out.println("<h2>Uploaded Image:</h2>");
        out.println("<img src=\"wafle.ru/matchin/images/" + outputFileName + "\" alt=\"Uploaded Image\">");
        out.println("</body>");
        out.println("</html>");

        out.flush();
    } else {
        response.getWriter().println("cheto slomalos btw");
        //response.getWriter().println("Error: Output file does not exist.    " + erout);
    }
}

}
