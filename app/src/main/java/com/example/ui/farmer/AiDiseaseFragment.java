package com.example.ui.farmer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adapter.ScansHistoryAdapter;
import com.example.ai.AiDiseaseService;
import com.example.databinding.FragmentAiDiseaseBinding;
import com.example.model.DiseaseAnalysisResult;
import com.example.model.SavedDiseaseScan;
import com.example.viewmodel.FarmerViewModel;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment implementing the AI Crop Disease Detection module.
 */
public class AiDiseaseFragment extends Fragment {

    public static final String ARG_CROP_ID = "arg_crop_id";
    public static final String ARG_CROP_NAME = "arg_crop_name";

    private FragmentAiDiseaseBinding binding;
    private FarmerViewModel viewModel;
    private ScansHistoryAdapter historyAdapter;

    private String selectedCrop = "Tomato";
    private String selectedCropId = null;
    private Bitmap selectedLeafBitmap = null;
    private DiseaseAnalysisResult latestAnalysisResult = null;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    public static AiDiseaseFragment newInstance(String cropId, String cropName) {
        AiDiseaseFragment fragment = new AiDiseaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CROP_ID, cropId);
        args.putString(ARG_CROP_NAME, cropName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCropId = getArguments().getString(ARG_CROP_ID);
            String cropName = getArguments().getString(ARG_CROP_NAME);
            if (cropName != null && !cropName.isEmpty()) {
                selectedCrop = cropName;
            }
        }

        // Camera Contract
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap != null) {
                setLeafImage(bitmap);
            }
        });

        // Gallery Contract
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null && getActivity() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    setLeafImage(bitmap);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Failed to load image from gallery", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiDiseaseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FarmerViewModel.class);

        setupToolbar();
        setupCropChips();
        setupHistoryRecyclerView();
        setupClickListeners();
        observeViewModel();

        // If target crop passed, automatically select it and generate an initial leaf preview
        selectTargetCrop(selectedCrop);
        generateSampleLeafBitmap(selectedCrop);
    }

    private void setupToolbar() {
        binding.toolbarAiDisease.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupCropChips() {
        binding.chipGroupCrops.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int id = checkedIds.get(0);
                Chip chip = group.findViewById(id);
                if (chip != null) {
                    String chipText = chip.getText().toString();
                    selectedCrop = chipText.replaceAll("[^a-zA-Z ]", "").trim();
                    generateSampleLeafBitmap(selectedCrop);
                }
            }
        });
    }

    private void selectTargetCrop(String cropName) {
        if (cropName == null) return;
        String lower = cropName.toLowerCase();
        if (lower.contains("onion")) {
            binding.chipOnion.setChecked(true);
            selectedCrop = "Onion";
        } else if (lower.contains("rice")) {
            binding.chipRice.setChecked(true);
            selectedCrop = "Rice";
        } else if (lower.contains("sugar")) {
            binding.chipSugarcane.setChecked(true);
            selectedCrop = "Sugarcane";
        } else if (lower.contains("soy")) {
            binding.chipSoybean.setChecked(true);
            selectedCrop = "Soybean";
        } else if (lower.contains("cotton")) {
            binding.chipCotton.setChecked(true);
            selectedCrop = "Cotton";
        } else if (lower.contains("chilli")) {
            binding.chipChilli.setChecked(true);
            selectedCrop = "Chilli";
        } else {
            binding.chipTomato.setChecked(true);
            selectedCrop = "Tomato";
        }
    }

    private void setupHistoryRecyclerView() {
        historyAdapter = new ScansHistoryAdapter(scan -> {
            displaySavedScanResult(scan);
        });
        binding.rvScansHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvScansHistory.setAdapter(historyAdapter);
    }

    private void observeViewModel() {
        viewModel.getDiseaseScansLiveData().observe(getViewLifecycleOwner(), scans -> {
            if (scans != null) {
                historyAdapter.submitList(scans);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnCamera.setOnClickListener(v -> {
            try {
                cameraLauncher.launch(null);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Camera not available in emulator, using leaf sample", Toast.LENGTH_SHORT).show();
                generateSampleLeafBitmap(selectedCrop);
            }
        });

        binding.btnGallery.setOnClickListener(v -> {
            try {
                galleryLauncher.launch("image/*");
            } catch (Exception e) {
                Toast.makeText(getContext(), "Gallery access unavailable, using sample", Toast.LENGTH_SHORT).show();
                generateSampleLeafBitmap(selectedCrop);
            }
        });

        binding.btnSampleLeaf.setOnClickListener(v -> {
            generateSampleLeafBitmap(selectedCrop);
            Toast.makeText(getContext(), "Sample leaf loaded for " + selectedCrop, Toast.LENGTH_SHORT).show();
        });

        binding.btnDiagnoseAi.setOnClickListener(v -> runAiDiagnosis());

        binding.btnSaveRecord.setOnClickListener(v -> saveCurrentDiagnosis());

        binding.btnScanAgain.setOnClickListener(v -> {
            binding.cardResult.setVisibility(View.GONE);
            generateSampleLeafBitmap(selectedCrop);
        });
    }

    private void setLeafImage(Bitmap bitmap) {
        selectedLeafBitmap = bitmap;
        binding.ivLeafPreview.setImageBitmap(bitmap);
        binding.ivLeafPreview.setVisibility(View.VISIBLE);
        binding.layoutPlaceholder.setVisibility(View.GONE);
    }

    private void generateSampleLeafBitmap(String crop) {
        int width = 300;
        int height = 200;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#E8F5E9"));
        canvas.drawRect(0, 0, width, height, bgPaint);

        Paint leafPaint = new Paint();
        leafPaint.setAntiAlias(true);
        leafPaint.setColor(Color.parseColor("#43A047"));
        canvas.drawOval(40, 30, 260, 170, leafPaint);

        Paint lesionPaint = new Paint();
        lesionPaint.setAntiAlias(true);
        lesionPaint.setColor(Color.parseColor("#5D4037"));
        canvas.drawCircle(110, 80, 18, lesionPaint);
        canvas.drawCircle(170, 110, 24, lesionPaint);

        Paint yellowHalo = new Paint();
        yellowHalo.setStyle(Paint.Style.STROKE);
        yellowHalo.setStrokeWidth(4);
        yellowHalo.setColor(Color.parseColor("#FBC02D"));
        canvas.drawCircle(170, 110, 28, yellowHalo);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16);
        textPaint.setFakeBoldText(true);
        canvas.drawText("Leaf Specimen: " + crop, 55, 155, textPaint);

        setLeafImage(bitmap);
    }

    private void runAiDiagnosis() {
        if (selectedLeafBitmap == null) {
            generateSampleLeafBitmap(selectedCrop);
        }

        binding.cardLoading.setVisibility(View.VISIBLE);
        binding.cardResult.setVisibility(View.GONE);
        binding.btnDiagnoseAi.setEnabled(false);

        AiDiseaseService service = AiDiseaseService.getInstance();
        service.analyzeCropImage(getContext(), selectedLeafBitmap, selectedCrop, new AiDiseaseService.DiseaseAnalysisCallback() {
            @Override
            public void onSuccess(DiseaseAnalysisResult result) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    latestAnalysisResult = result;
                    binding.cardLoading.setVisibility(View.GONE);
                    binding.btnDiagnoseAi.setEnabled(true);
                    displayDiagnosisResult(result);
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.cardLoading.setVisibility(View.GONE);
                    binding.btnDiagnoseAi.setEnabled(true);
                    Toast.makeText(getContext(), "Analysis Error: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void displayDiagnosisResult(DiseaseAnalysisResult result) {
        binding.cardResult.setVisibility(View.VISIBLE);
        binding.tvResultDiseaseName.setText(result.getDisease());
        binding.tvResultMarathiName.setText("स्थानिक पीक: " + (result.getCrop() != null ? result.getCrop() : selectedCrop));
        binding.tvResultSeverity.setText(result.getSeverity() + " Severity");
        binding.tvResultConfidence.setText("Confidence: " + result.getConfidencePercent() + "% (" + result.getConfidence() + ")");

        // Symptoms
        StringBuilder symSb = new StringBuilder();
        if (result.getSymptoms() != null) {
            for (String s : result.getSymptoms()) {
                symSb.append("• ").append(s).append("\n");
            }
        }
        binding.tvResultSymptoms.setText(symSb.toString().trim());

        // Causes
        StringBuilder causeSb = new StringBuilder();
        if (result.getPossibleCauses() != null) {
            for (String c : result.getPossibleCauses()) {
                causeSb.append("• ").append(c).append("\n");
            }
        }
        binding.tvResultCauses.setText(causeSb.toString().trim());

        // Treatment
        StringBuilder treatSb = new StringBuilder();
        if (result.getRecommendedAction() != null) {
            for (String t : result.getRecommendedAction()) {
                treatSb.append("• ").append(t).append("\n");
            }
        }
        binding.tvResultTreatment.setText(treatSb.toString().trim());

        // Prevention
        StringBuilder prevSb = new StringBuilder();
        if (result.getPrevention() != null) {
            for (String p : result.getPrevention()) {
                prevSb.append("• ").append(p).append("\n");
            }
        }
        binding.tvResultPrevention.setText(prevSb.toString().trim());
    }

    private void displaySavedScanResult(SavedDiseaseScan scan) {
        binding.cardResult.setVisibility(View.VISIBLE);
        binding.tvResultDiseaseName.setText(scan.getDiseaseName());
        binding.tvResultMarathiName.setText("Crop: " + scan.getCropName() + " (" + scan.getFormattedDate() + ")");
        binding.tvResultSeverity.setText(scan.getSeverity() + " Severity");
        binding.tvResultConfidence.setText("Confidence: " + scan.getConfidence());

        if (scan.getSymptoms() != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : scan.getSymptoms()) sb.append("• ").append(s).append("\n");
            binding.tvResultSymptoms.setText(sb.toString().trim());
        }

        if (scan.getRecommendedAction() != null) {
            StringBuilder sb = new StringBuilder();
            for (String t : scan.getRecommendedAction()) sb.append("• ").append(t).append("\n");
            binding.tvResultTreatment.setText(sb.toString().trim());
        }
    }

    private void saveCurrentDiagnosis() {
        if (latestAnalysisResult == null) {
            Toast.makeText(getContext(), "No diagnosis result to save", Toast.LENGTH_SHORT).show();
            return;
        }

        String scanId = "scan_" + System.currentTimeMillis();
        String dateStr = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

        SavedDiseaseScan scan = new SavedDiseaseScan(
                scanId,
                latestAnalysisResult.getCrop(),
                latestAnalysisResult.getDisease(),
                latestAnalysisResult.getConfidencePercent() + "%",
                latestAnalysisResult.getSeverity(),
                latestAnalysisResult.getSymptoms(),
                latestAnalysisResult.getPossibleCauses(),
                latestAnalysisResult.getRecommendedAction(),
                latestAnalysisResult.getPrevention(),
                "good",
                dateStr
        );

        viewModel.saveDiseaseScan(scan);
        Toast.makeText(getContext(), "Diagnosis record saved successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
