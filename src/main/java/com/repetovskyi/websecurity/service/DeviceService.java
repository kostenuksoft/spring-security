package com.repetovskyi.websecurity.service;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  DeviceService
  @version  1.1.0
  @since 9/12/2026 - 19.07
*/

import com.repetovskyi.websecurity.model.Device;
import com.repetovskyi.websecurity.repository.DeviceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @PostConstruct
    void init() {
        deviceRepository.deleteAll();
        deviceRepository.saveAll(List.of(
                Device.builder().id("1").name("router-01").type("router").owner("it-department").build(),
                Device.builder().id("2").name("printer-02").type("printer").owner("accounting").build(),
                Device.builder().id("3").name("laptop-03").type("laptop").owner("r.vladyslav").build()
        ));
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Optional<Device> findById(String id) {
        return deviceRepository.findById(id);
    }

    public Device create(Device device) {
        device.setId(UUID.randomUUID().toString());
        return deviceRepository.save(device);
    }

    public Optional<Device> update(String id, Device device) {
        return deviceRepository.findById(id).map(existing -> {
            existing.setName(device.getName());
            existing.setType(device.getType());
            existing.setOwner(device.getOwner());
            return deviceRepository.save(existing);
        });
    }

    public boolean delete(String id) {
        if (!deviceRepository.existsById(id)) {
            return false;
        }
        deviceRepository.deleteById(id);
        return true;
    }
}
