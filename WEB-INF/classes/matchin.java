import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "matchin", urlPatterns = { "/matchin" })
@MultipartConfig(
  fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
  maxFileSize = 1024 * 1024 * 10,      // 10 MB
  maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class matchin extends HttpServlet {

  private String uploadDirectory;
  private String pythonScriptPath;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    uploadDirectory = config.getServletContext().getInitParameter("uploadDirectory");
    pythonScriptPath = "/opt/tomcat/webapps/ROOT/WEB-INF/classes/face_detect.py"; // Set the path to your Python script
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
    // Create the upload directory if it doesn't exist
    File directory = new File(uploadDirectory);
    if (!directory.exists()) {
        directory.mkdirs(); // Create directories along the path if they don't exist
    }

    // Write the file to the directory
    Part filePart = request.getPart("file");
    String fileName = filePart.getSubmittedFileName();
    String filePath = uploadDirectory + fileName;
    filePart.write(filePath);
    String error="";
    // Execute the Python script with the uploaded file name
    String outputFileName = fileName.split("\\.")[0] + "_output.jpg";
    String command = "python3 " + pythonScriptPath + " " + filePath;

    //ProcessBuilder builder = new ProcessBuilder(command);
    //Process process = builder.start();

    Process process = Runtime.getRuntime().exec("/usr/bin/python3 /opt/tomcat/webapps/ROOT/WEB-INF/classes/face_detect.py /opt/tomcat/webapps/ROOT/matchin/images/istockphoto-805012064-612x612.jpg");
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    String line;
    String erout="";
    while ((line = reader.readLine()) != null) {
        erout += line;
    }

    // Send the user the new file generated by the script
    File outputFile = new File(uploadDirectory + outputFileName);
    if (outputFile.exists()) {
      response.setContentType("image/jpeg");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFileName + "\"");
      OutputStream out = response.getOutputStream();
      FileInputStream in = new FileInputStream(outputFile);
      byte[] buffer = new byte[4096];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
      in.close();
      out.flush();
    } else {
      response.getWriter().println("Error: Output file does not exist.    " + erout);
    }
  }
}
