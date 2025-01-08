import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company","root","Ijse@123");
            ResultSet resultSet=connection.prepareStatement("SELECT * FROM customer").executeQuery();
            resp.setContentType("application/json");
            JsonArrayBuilder allcustomer=Json.createArrayBuilder();

            while (resultSet.next()){
                String id=resultSet.getString(1);
                String name=resultSet.getString(2);
                String address=resultSet.getString(3);
                System.out.println(id+" "+name+" "+address);
                JsonObjectBuilder customer= Json.createObjectBuilder();
                customer.add("id",id);
                customer.add("name",name);
                customer.add("address",address);
                allcustomer.add(customer.build());


            }
            resp.getWriter().write(allcustomer.build().toString());
            PrintWriter out=resp.getWriter();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}