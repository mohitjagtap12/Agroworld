package com.agroworld.backend.controller;

import com.agroworld.backend.dto.ApiResponse;
import com.agroworld.backend.entity.FarmerCropEntity;
import com.agroworld.backend.service.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    @GetMapping("/crops")
    public ResponseEntity<ApiResponse<List<FarmerCropEntity>>> getCrops(Authentication auth) {
        String farmerId = (String) auth.getPrincipal();
        List<FarmerCropEntity> crops = farmerService.getFarmerCrops(farmerId);
        return ResponseEntity.ok(ApiResponse.success(crops));
    }

    @PostMapping("/crops")
    public ResponseEntity<ApiResponse<FarmerCropEntity>> addCrop(Authentication auth, @RequestBody FarmerCropEntity crop) {
        String farmerId = (String) auth.getPrincipal();
        FarmerCropEntity created = farmerService.addCrop(farmerId, crop);
        return ResponseEntity.ok(ApiResponse.success("Crop registered successfully", created));
    }

    @PutMapping("/crops/{cropId}")
    public ResponseEntity<ApiResponse<FarmerCropEntity>> updateCrop(
            Authentication auth,
            @PathVariable String cropId,
            @RequestBody FarmerCropEntity crop) {
        String farmerId = (String) auth.getPrincipal();
        FarmerCropEntity updated = farmerService.updateCrop(farmerId, cropId, crop);
        return ResponseEntity.ok(ApiResponse.success("Crop updated successfully", updated));
    }

    @DeleteMapping("/crops/{cropId}")
    public ResponseEntity<ApiResponse<String>> deleteCrop(Authentication auth, @PathVariable String cropId) {
        String farmerId = (String) auth.getPrincipal();
        farmerService.deleteCrop(farmerId, cropId);
        return ResponseEntity.ok(ApiResponse.success("Crop removed successfully", cropId));
    }
}
