package com.nishchay.productservice.service.impl;

import com.nishchay.productservice.entity.Attribute;
import com.nishchay.productservice.repository.AttributeRepository;
import com.nishchay.productservice.service.AttributeService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;

    @Override
    @Transactional
    public Attribute createAttribute(String name, String dataType) {
        if (attributeRepository.findByName(name).isPresent()) {;
            throw new RuntimeException("Attribute with name '" + name + "' already exists");
        }
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setDataType(dataType);
        return attributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public Attribute updateAttribute(Long id, String newName, String newDataType) {
        Attribute attribute= attributeRepository.findById(id).orElseThrow( () -> new RuntimeException("Attribute not found") );
        attribute.setName(newName);
        attribute.setDataType(newDataType);
        return attributeRepository.save(attribute);

    }

    @Override
    public List<Attribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    @Override
    public Attribute getAttributeByName(String name) {
        return attributeRepository.findByName(name).orElseThrow(() -> new RuntimeException("Attribute not found"));
    }

    @Override
    @Transactional
    public void deleteAttribute(Long id) {
        attributeRepository.deleteById(id);
    }
}
