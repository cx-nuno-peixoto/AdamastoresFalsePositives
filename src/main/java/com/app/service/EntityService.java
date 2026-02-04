package com.app.service;

import com.app.model.Entity;
import com.app.repository.EntityRepository;
import com.app.util.Sanitizer;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityService {
    
    private EntityRepository repository;
    
    public EntityService(EntityRepository repository) {
        this.repository = repository;
    }
    
    public long getEntityId(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getId).orElse(0L);
    }
    
    public int getEntityStatus(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getStatus).orElse(-1);
    }
    
    public boolean isEntityActive(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::isActive).orElse(false);
    }
    
    public double getEntityBalance(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getBalance).orElse(0.0);
    }
    
    public int getEntityCode(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getCode).orElse(0);
    }
    
    public List<Long> getAllEntityIds() throws SQLException {
        return repository.findAll().stream()
            .map(Entity::getId)
            .collect(Collectors.toList());
    }
    
    public List<Integer> getAllEntityStatuses() throws SQLException {
        return repository.findAll().stream()
            .map(Entity::getStatus)
            .collect(Collectors.toList());
    }
    
    public String getEscapedName(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(e -> Sanitizer.escapeHtml(e.getName())).orElse("");
    }
    
    public int getFirstEntityStatus() throws SQLException {
        List<Entity> entities = repository.findAll();
        return entities.isEmpty() ? -1 : entities.get(0).getStatus();
    }

    public long getFirstEntityId() throws SQLException {
        List<Entity> entities = repository.findAll();
        return entities.isEmpty() ? 0L : entities.get(0).getId();
    }

    // New methods for Stored XSS scenarios
    public String getEntityName(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getName).orElse("");
    }

    public String getEntityDescription(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getDescription).orElse("");
    }

    public String getEntityType(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getType).orElse("OTHER");
    }

    public String getEntityCategory(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getCategory).orElse("other");
    }

    public String getEntityUuid(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getUuid).orElse("00000000-0000-0000-0000-000000000000");
    }

    public String getEntityContent(long id) throws SQLException {
        Optional<Entity> entity = repository.findById(id);
        return entity.map(Entity::getContent).orElse("");
    }
}

