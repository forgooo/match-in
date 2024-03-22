import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "FileUploadServlet", urlPatterns = { "/matchin" })
@MultipartConfig(
  fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
  maxFileSize = 1024 * 1024 * 10,      // 10 MB
  maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class FileUploadServlet extends HttpServlet {

  private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/matchin";
  private static final String JDBC_USER = "postgres";
  private static final String JDBC_PASSWORD = "postgres";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws IOException{
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html><head><title>matchinSERVLET</title></head>");
    out.println("<body>");
    out.println("<form method='post' enctype='multipart/form-data'><input type='file' name='file' /><input type='submit' value='Upload' /></form>");
    out.println("</body></html>");
    out.close();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uploadDirPath = getServletContext().getRealPath("/images/");
    File uploadDir = new File(uploadDirPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }

    Part filePart = request.getPart("file");
    String fileName = filePart.getSubmittedFileName();
    String filePath = uploadDirPath + File.separator + fileName;
    filePart.write(filePath);

    // Insert the file name into the database
    try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
      String sql = "INSERT INTO images (image_name) VALUES (?)";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, fileName);
        statement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new ServletException("Database error", e);
    }

    response.getWriter().print("The file uploaded successfully.");
  }
}
