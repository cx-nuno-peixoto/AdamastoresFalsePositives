package com.app.repository;

import com.app.model.Entity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityRepository {
    
    private Connection connection;
    
    public EntityRepository(Connection connection) {
        this.connection = connection;
    }
    
    public Optional<Entity> findById(long id) throws SQLException {
        String sql = "SELECT id, name, email, status, active, balance FROM entities WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Entity e = new Entity();
                e.setId(rs.getLong("id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setStatus(rs.getInt("status"));
                e.setActive(rs.getBoolean("active"));
                e.setBalance(rs.getDouble("balance"));
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
    
    public List<Entity> findAll() throws SQLException {
        List<Entity> entities = new ArrayList<>();
        String sql = "SELECT id, name, email, status, active, balance FROM entities";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Entity e = new Entity();
                e.setId(rs.getLong("id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setStatus(rs.getInt("status"));
                e.setActive(rs.getBoolean("active"));
                e.setBalance(rs.getDouble("balance"));
                entities.add(e);
            }
        }
        return entities;
    }
    
    public List<Entity> findByStatus(int status) throws SQLException {
        List<Entity> entities = new ArrayList<>();
        String sql = "SELECT id, name, email, status, active, balance FROM entities WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Entity e = new Entity();
                e.setId(rs.getLong("id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setStatus(rs.getInt("status"));
                e.setActive(rs.getBoolean("active"));
                e.setBalance(rs.getDouble("balance"));
                entities.add(e);
            }
        }
        return entities;
    }
}

