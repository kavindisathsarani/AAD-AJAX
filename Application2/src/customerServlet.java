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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/customerJson")
public class customerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
//            JsonObjectBuilder customer1 = Json.createObjectBuilder();
//            customer1.add("id", 1);
//            customer1.add("name", "Achini");
//            customer1.add("address", "Galle");
//
//            JsonObjectBuilder customer2 = Json.createObjectBuilder();
//            customer2.add("id", 2);
//            customer2.add("name", "Kasuni");
//            customer2.add("address", "Matara");
//
//            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//            arrayBuilder.add(customer1.build());
//            arrayBuilder.add(customer2.build());
//
//            JsonObjectBuilder response = Json.createObjectBuilder();
//            response.add("data", arrayBuilder);
//            response.add("status", HttpServletResponse.SC_OK);
//            response.add("message", "Success");
//
//            JsonObject json = response.build();
//            resp.setContentType("application/json");
//            resp.getWriter().print(json);
//
//        } catch (Exception e) {
//            JsonObjectBuilder response = Json.createObjectBuilder();
//            response.add("data","");
//            response.add("status",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.add("message",e.getMessage());
//        }
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM customer";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                while (resultSet.next()) {
                    JsonObjectBuilder customer = Json.createObjectBuilder();
                    customer.add("id", resultSet.getString("id"));
                    customer.add("name", resultSet.getString("name"));
                    customer.add("address", resultSet.getString("address"));
                    arrayBuilder.add(customer.build());
                }

                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("data", arrayBuilder);
                response.add("status", HttpServletResponse.SC_OK);
                response.add("message", "Success");

                resp.setContentType("application/json");
                resp.getWriter().print(response.build());
            }
        } catch (Exception e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("data", "");
            response.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = dataSource.getConnection()) {

            JsonReader jsonReader = Json.createReader(req.getReader());
            JsonObject jsonObject = jsonReader.readObject();
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String address = jsonObject.getString("address");

            // Save to the database
            String sql = "INSERT INTO customer (id, name, address) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, address);
                preparedStatement.executeUpdate();
            }

            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("data", jsonObject);
            response.add("status", HttpServletResponse.SC_CREATED);
            response.add("message", " Success");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());

        } catch (Exception e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("data", "");
            response.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = dataSource.getConnection()) {
            JsonReader jsonReader = Json.createReader(req.getReader());
            JsonObject jsonObject = jsonReader.readObject();
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String address = jsonObject.getString("address");

            String sql = "UPDATE customer SET name = ?, address = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3,id);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("data", jsonObject);
                    response.add("status", HttpServletResponse.SC_OK);
                    response.add("message", "Success");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().print(response.build());
                } else {
                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("data", "");
                    response.add("status", HttpServletResponse.SC_NOT_FOUND);
                    response.add("message", "Not found");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.setContentType("application/json");
                    resp.getWriter().print(response.build());
                }
            }
        } catch (Exception e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("data", "");
            response.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = dataSource.getConnection()) {
            String id = req.getParameter("id");

            if (id == null || id.isEmpty()) {
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("data", "");
                response.add("status", HttpServletResponse.SC_BAD_REQUEST);
                response.add("message", "Customer ID is required");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().print(response.build());
                return;
            }

            String sql = "DELETE FROM customer WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);

                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("data", "");
                    response.add("status", HttpServletResponse.SC_OK);
                    response.add("message", "Customer deleted successfully");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().print(response.build());
                } else {
                    JsonObjectBuilder response = Json.createObjectBuilder();
                    response.add("data", "");
                    response.add("status", HttpServletResponse.SC_NOT_FOUND);
                    response.add("message", "Customer not found");
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.setContentType("application/json");
                    resp.getWriter().print(response.build());
                }
            }
        } catch (Exception e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("data", "");
            response.add("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("message", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print(response.build());
        }
    }
}