package com.example.maizedisease;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maizedisease.ml.MaizeModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final float CONFIDENCE_THRESHOLD = 0.60f;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1 MB

    private ImageView imageView,message;
    private TextView resultTextView;
    private Button predictButton;
    private Bitmap imageBitmap;
    private MaizeModel model;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.outputTextView);
        predictButton = findViewById(R.id.predictButton);
        message = findViewById(R.id.message);

        Button uploadButton = findViewById(R.id.button);
        predictButton.setEnabled(false);

        uploadButton.setOnClickListener(v -> pickImage());
        predictButton.setOnClickListener(v -> predictDisease());
        message.setOnClickListener(v -> messaging());

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            imageBitmap = compressImage(imageBitmap); // Compress the image
                            imageView.setImageBitmap(imageBitmap);
                            predictButton.setEnabled(true); // Enable predict button after image is loaded
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading image", e);
                            showErrorToast("Error loading image");
                        }
                    }
                });

        // Request storage permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    private void pickImage() {
        pickImageLauncher.launch("image/*");
    }

    private void predictDisease() {
        if (imageBitmap != null) {
            resultTextView.setText("Processing...");
            predictButton.setEnabled(false); // Disable predict button during processing

            new Thread(() -> {
                try {
                    model = MaizeModel.newInstance(MainActivity.this);

                    // Resize the input image to match the expected input shape
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 256, 256, true);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(resizedBitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    MaizeModel.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    int[] outputArray = outputFeature0.getIntArray();
                    int outputIndex = 0;
                    float maxConfidence = outputFeature0.getFloatArray()[0];

                    for (int i = 1; i < outputArray.length; i++) {
                        if (outputFeature0.getFloatArray()[i] > maxConfidence) {
                            maxConfidence = outputFeature0.getFloatArray()[i];
                            outputIndex = i;
                        }
                    }

                    String outputLabel = getLabelForIndex(outputIndex);

                    float finalMaxConfidence = maxConfidence;
                    runOnUiThread(() -> {
                        predictButton.setEnabled(true); // Enable predict button after processing
                        if (finalMaxConfidence >= CONFIDENCE_THRESHOLD) {
                            resultTextView.setText( outputLabel );

                            // Create an ArrayList of FungicideModel objects based on the predicted disease
                            ArrayList<FungicideModel> fungicideList = new ArrayList<>();
                            switch (outputLabel) {
                                case "Blight":
                                    fungicideList.add(new FungicideModel(R.drawable.blight, getString(R.string.blight)));
                                    break;
                                case "Common Rust":
                                    fungicideList.add(new FungicideModel(R.drawable.rust, getString(R.string.common_rust)));
                                    break;
                                case "Gray Leaf Spot":
                                    fungicideList.add(new FungicideModel(R.drawable.spot, getString(R.string.gray_leaf_spot)));
                                    break;
                            }

                            // Start ResultsActivity with prediction result and fungicide recommendations
                            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                            intent.putExtra("ResultActivity", outputLabel);
                            intent.putParcelableArrayListExtra("fungicideList", fungicideList);
                            startActivity(intent);
                        } else {
                            resultTextView.setText("Low confidence prediction. Please try again with a different image.");
                        }
                    });
                } catch (Exception e) {
                    handlePredictionError(e);
                } finally {
                    if (model != null) {
                        model.close();
                    }
                }
            }).start();
        } else {
            showErrorToast("Please upload an image first");
        }
    }

    private String getLabelForIndex(int index) {
        switch (index) {
            case 0:
                return "Blight";
            case 1:
                return "Common Rust";
            case 2:
                return "Gray Leaf Spot";
            case 3:
                return "Healthy";
            default:
                return "Unknown";
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0) {
                boolean permissionGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionGranted = false;
                        break;
                    }
                }

                if (permissionGranted) {
                    // Storage permission granted, proceed with image picking
                    pickImage();
                } else {
                    showErrorToast("Storage permission denied. Cannot access images.");
                }
            } else {
                showErrorToast("Storage permission denied. Cannot access images.");
            }
        }
    }

    private Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > MAX_IMAGE_SIZE && options > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        byte[] data = baos.toByteArray();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void handlePredictionError(Exception e) {
        runOnUiThread(() -> {
            predictButton.setEnabled(true);
            resultTextView.setText("Error predicting disease: " + e.getMessage());
            Log.e(TAG, "Error predicting disease", e);
        });
    }
    private void messaging(){
        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
        startActivity(intent);
    }
}