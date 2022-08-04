package testSuiteJavaMaven;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet
public class SQLi_parameterized_notvalidated extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("text/html"); // #1 Doesn't set character set
        PrintWriter out = response.getWriter();
        
        out.println("<HTML><BODY><p><b>Parameterized query and not validated</b></p>");
        
        // print form
        out.println("<p>Enter your credentials to log in</p>" 
        		+ "<FORM action='" + request.getContextPath() + "/SQLi4' method='POST'>"
        		+ "userid: <input name='userid'>"
        		+ "<br>password: <input name='password'>"
        		+ "<br><input type='submit'>"
        		+ "</BODY></HTML>"); // #2 No autocomplete on form
        
		//doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");
		
		// #3 userid parameter not validated
		// #4 password parameter not validated
		
		out.println("<p>Submitted userid: " + userid + "<BR>");	// #5 Reflected XSS
		out.println("Submitted password: " + password + "</p>"); // #6 Reflected XSS
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");  
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/insecureapp","insecureapp","45EUlZOpL7");  // #7 Hardcoded password
			
			String sql = "SELECT * FROM users WHERE userid=? AND password=?";
			
        	out.println("<p><b>SQL:</b> " + sql + "</p>"); // #8 Reflected XSS 
        	out.println("<p><b>Results:</b></p>");
  
			PreparedStatement pstmt = connection.prepareStatement( sql ); 
			pstmt.setString(1,userid);
			pstmt.setString(2,password);
            ResultSet rs = pstmt.executeQuery(sql);  
            
            while(rs.next()) {
            	out.println("<p>Name: " + rs.getString(3) + " " + rs.getString(2) + ", Address: " + rs.getString(4) + ", Phone no: " + rs.getString(5) + "</p>"); // #10 Stored XSS
            }
            connection.close();
            out.println("</BODY></HTML>");
            
        } 
		catch(Exception e){ 
			out.println(e); // #11 Stack trace
			System.out.println(e);
		}  		
	}
}
