package com.agroworld.backend.service;

import com.agroworld.backend.entity.FarmerCropEntity;
import com.agroworld.backend.repository.FarmerCropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FarmerService {

    @Autowired
    private FarmerCropRepository cropRepository;

    public List<FarmerCropEntity> getFarmerCrops(String farmerId) {
        return cropRepository.findByFarmerId(farmerId);
    }

    public FarmerCropEntity getCropById(String cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found: " + cropId));
    }

    @Transactional
    public FarmerCropEntity addCrop(String farmerId, FarmerCropEntity crop) {
        if (crop.getId() == null || crop.getId().isEmpty()) {
            crop.setId("crop_" + UUID.randomUUID().toString().substring(0, 8));
        }
        crop.setFarmerId(farmerId);
        crop.setCreatedAt(LocalDateTime.now());
        crop.setUpdatedAt(LocalDateTime.now());
        return cropRepository.save(crop);
    }

    @Transactional
    public FarmerCropEntity updateCrop(String farmerId, String cropId, FarmerCropEntity updatedData) {
        FarmerCropEntity existing = getCropById(cropId);
        if (!existing.getFarmerId().equals(farmerId)) {
            throw new SecurityException("Unauthorized crop modification.");
        }
        existing.setCropName(updatedData.getCropName());
        existing.setVariety(updatedData.getVariety());
        existing.setLandArea(updatedData.getLandArea());
        existing.setLandUnit(updatedData.getLandUnit());
        existing.setSowingDate(updatedData.getSowingDate());
        existing.setExpectedHarvestDate(updatedData.getExpectedHarvestDate());
        existing.setLocation(updatedData.getLocation());
        existing.setStatus(updatedData.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return cropRepository.save(existing);
    }

    @Transactional
    public void deleteCrop(String farmerId, String cropId) {
        FarmerCropEntity existing = getCropById(cropId);
        if (!existing.getFarmerId().equals(farmerId)) {
            throw new SecurityException("Unauthorized crop deletion.");
        }
        cropRepository.delete(existing);
    }
}
