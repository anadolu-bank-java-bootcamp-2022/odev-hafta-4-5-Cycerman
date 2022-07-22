package com.gokhantamkoc.javabootcamp.odevhafta45.repository;
import com.gokhantamkoc.javabootcamp.odevhafta45.model.Product;
import com.gokhantamkoc.javabootcamp.odevhafta45.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductRepository {

    DatabaseConnection databaseConnection;

    @Autowired
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<Product> getAll() {
        final String SQL =
                "SELECT id, name, description FROM public.product";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                products.add(
                        new Product(
                                id,
                                name,
                                description
                        )
                );
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return products;
    }

    public Product get(long id) {
        final String SQL = "SELECT * FROM public.product where id = ? limit 1;";
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void save(Product product) throws RuntimeException {
        final String SQL = "INSERT INTO public.owner(id, name, description) values(?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
            preparedStatement.setLong(1, product.getId());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setString(3, product.getDescription());
            int affectedRows = preparedStatement.executeUpdate(SQL);
            if (affectedRows <= 0) {
                throw new RuntimeException(String.format("Could not save order %s!", product));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void update(Product product) throws RuntimeException {
        Product foundProduct = this.get(product.getId());
        if (foundProduct != null) {
            final String SQL = "UPDATE public.product set name = ?, description = ?";
            try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getDescription());
                int affectedRows = preparedStatement.executeUpdate(SQL);
                if (affectedRows <= 0) {
                    throw new RuntimeException(String.format("Could not update order %s!", product));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    // BU METHODU SILMEYINIZ YOKSA TESTLER CALISMAZ
    public void delete(long id) throws RuntimeException {
        Product foundProduct = this.get(id);
        if (foundProduct != null) {
            final String SQL = "delete from public.product where id = ?";
            try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SQL)) {
                preparedStatement.setLong(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows <= 0) {
                    throw new RuntimeException(String.format("Could not delete product with id %d!", id));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
}
