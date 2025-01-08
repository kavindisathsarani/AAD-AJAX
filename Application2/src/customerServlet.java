import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/customerJson")
public class customerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            Connection connection=dataSource.getConnection();
            ResultSet resultSet=connection.prepareStatement("SELECT*FROM customer").executeQuery();
            JsonArrayBuilder allCustomer=Json.createArrayBuilder();
            while (resultSet.next()){
                String id=resultSet.getString(1);
                String name=resultSet.getString(2);
                String address=resultSet.getString(3);
                System.out.println(id+" "+name+" "+address);
                JsonObjectBuilder customer=Json.createObjectBuilder();
                customer.add("id",id);
                customer.add("name",name);
                customer.add("address",address);
                allCustomer.add(customer.build());
            }
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("customer", allCustomer);
            response.add("status", HttpServletResponse.SC_OK);
            response.add("message", "success");

            JsonObject json = response.build();
            resp.setContentType("application/json");
            resp.getWriter().print(json);

        } catch (SQLException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("customer", "");
            response.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message", e.getMessage());
        }
    }




    /* @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {



        try {

            //this is a json object
            JsonObjectBuilder customer1 = Json.createObjectBuilder();
            customer1.add("id", 1);
            customer1.add("name", "saman");
            customer1.add("address", "galle");

            //this is a json object
            JsonObjectBuilder customer2 = Json.createObjectBuilder();
            customer2.add("id", 2);
            customer2.add("name", "amal");
            customer2.add("address", "matara");

            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            arrayBuilder.add(customer1.build());
            arrayBuilder.add(customer2.build());

            //this is a json object to get a more clear response
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("customers", arrayBuilder);
            response.add("status", HttpServletResponse.SC_OK);
            response.add("message", "Success");


//        builder.add("customers",arrayBuilder);

            JsonObject json = response.build();
            resp.setContentType("application/json");
            resp.getWriter().print(json);

        } catch (Exception e) {
            JsonObjectBuilder response=Json.createObjectBuilder();
            response.add("data", "");
            response.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message", e.getMessage());
        }
    }
*/
   /* @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            JsonReader jsonReader=Json.createReader(req.getReader());
            JsonObject jsonObject=jsonReader.readObject();
            String id=jsonObject.getString("id");
            String name=jsonObject.getString("name");
            String address=jsonObject.getString("address");
            System.out.println(id+" "+name+" "+address);
            //save on database

            //send a proper response
            JsonObjectBuilder response=Json.createObjectBuilder();
            response.add("data",jsonObject);
            response.add("status",HttpServletResponse.SC_CREATED);
            response.add("message","Success");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());

        }catch (Exception e){
            JsonObjectBuilder response=Json.createObjectBuilder();
            response.add("data","");
            response.add("status",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message",e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());
        }


    }*/
}