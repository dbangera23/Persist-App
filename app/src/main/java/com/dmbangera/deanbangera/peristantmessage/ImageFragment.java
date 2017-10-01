package com.dmbangera.deanbangera.peristantmessage;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by Dean Bangera on 12/23/2016.
 * Fragment to handle the view for image setting
 */

public class ImageFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final int REQUEST_IMAGE_GALLERY = 0;
    private String mCurrentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentCameraPath;

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        final ImageView EImage = view.findViewById(R.id.image);
        EImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        EImage.setAdjustViewBounds(true);
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        FrameLayout imageListener = view.findViewById(R.id.image_listener);
        mCurrentPhotoPath = settings.getString("photoPath", "");
        if(!checkPermissionForReadExtertalStorage()){
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.permissionIssueDisclaimer, Toast.LENGTH_LONG).show();
            }
        }
        File imgFile = new File(mCurrentPhotoPath);
        if (imgFile.exists()) {
            Bitmap bitmap = bitmapFromFilePath(mCurrentPhotoPath);
            EImage.setImageResource(0);
            EImage.setBackground(null);
            EImage.setImageBitmap(bitmap);
        }

        final SeekBar opacity_seek = view.findViewById(R.id.opacity_seek);
        if (opacity_seek != null) {
            opacity_seek.setMax(100);
            float saved_opacity = settings.getFloat("image_trans", 1f);
            opacity_seek.setProgress((int) saved_opacity * 100);
            final TextView opacityText = view.findViewById(R.id.opacity);
            opacityText.setText(String.format(Locale.getDefault(), "%.02f", saved_opacity));
            EImage.setAlpha(saved_opacity);
            opacity_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float opacity = (float) (progress / 100.00);
                    opacityText.setText(String.format(Locale.getDefault(), "%.02f", opacity));
                    EImage.setAlpha(opacity);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        final SeekBar size_seek = view.findViewById(R.id.size_seeker);
        if (size_seek != null) {
            final TextView sizeText = view.findViewById(R.id.size);
            size_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float progressSet = (float) ((progress / 100.0) + .01f);
                    sizeText.setText(String.format(Locale.getDefault(), "%.02f", progressSet));
                    changeImageViewSize(EImage, progressSet);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            size_seek.setMax(199);
            float saved_size = settings.getFloat("image_size", .99f);
            if (saved_size == 2.0f)
                saved_size = 1.99f;
            size_seek.setProgress((int) (saved_size * 100));
            changeImageViewSize(EImage, saved_size);
            sizeText.setText(String.format(Locale.getDefault(), "%.02f", saved_size + .01f));
        }

        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            imageListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("Pick Source")
                            .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                        File photoFile = null;
                                        try {
                                            photoFile = createImageFile();
                                        } catch (IOException ex) {
                                            Toast.makeText(getContext(), R.string.errorTakePic, Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "onPictureClick: ", ex);
                                        }
                                        // Continue only if the File was successfully created
                                        if (photoFile != null) {
                                            Uri photoURI = FileProvider.getUriForFile(getContext(),
                                                    "com.dmbangera.deanbangera.peristantmessage.fileprovider",
                                                    photoFile);
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("Image", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(checkPermissionForReadExtertalStorage()){
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
                                    }else{
                                        try {
                                            requestPermissionForReadExternalStorage();
                                            v.performClick();
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(),  R.string.retrieveError, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                    builder.show();
                }
            });
        }

        SeekBar rot_seek = view.findViewById(R.id.rotation_seek);
        if (rot_seek != null) {
            rot_seek.setMax(359);
            int saved_rot = settings.getInt("image_rot", 0);
            rot_seek.setProgress(saved_rot);
            final TextView rotText = view.findViewById(R.id.rotation);
            rotText.setText(String.format(Locale.getDefault(), "%d", saved_rot));
            EImage.setRotation(saved_rot);
            rot_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    rotText.setText(String.format(Locale.getDefault(), "%d", progress));
                    EImage.setRotation(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        Button image_set_button = view.findViewById(R.id.image_set_button);
        image_set_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EImage.getDrawable() != null && mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
                    SharedPreferences.Editor editor = settings.edit();
                    TextView opacityText = view.findViewById(R.id.opacity);
                    editor.putFloat("opacity", Float.parseFloat(opacityText.getText().toString()));

                    TextView scaleText = view.findViewById(R.id.size);
                    editor.putFloat("image_size", Float.parseFloat(scaleText.getText().toString()));
                    editor.putFloat("image_width", EImage.getWidth());
                    editor.putFloat("image_height", EImage.getHeight());

                    TextView rotation = view.findViewById(R.id.rotation);
                    editor.putInt("RotSeek", Integer.parseInt(rotation.getText().toString()));

                    editor.putString("photoPath", mCurrentPhotoPath);

                    editor.putBoolean("textBased", false);
                    editor.apply();

                    Intent i = new Intent(view.getContext(), setPersistService.class);
                    getActivity().stopService(i);
                    getActivity().startService(i);
                    Snackbar snackbar = Snackbar
                            .make(view, getString(R.string.SetImage), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(view.getContext(), setPersistService.class);
                                    getActivity().stopService(intent);
                                    Snackbar snackbar = Snackbar.make(view, R.string.DeleteImage, Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });

                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(view, R.string.EmptyImage, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
        Button image_loc_button = view.findViewById(R.id.image_loc_button);
        image_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("locTextBased", false);
                editor.apply();
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                startActivity(intent);
            }
        });
        Button image_del_button = view.findViewById(R.id.image_del_button);
        image_del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size_seek != null) {
                    size_seek.setProgress(99);
                }
                EImage.setImageResource(0);
                EImage.setBackgroundResource(R.drawable.ic_insert_photo_black_24dp);
                ViewGroup.LayoutParams params = EImage.getLayoutParams();
                params.width = EImage.getBackground().getIntrinsicWidth();
                params.height = EImage.getBackground().getIntrinsicWidth();
                EImage.setLayoutParams(params);
                Intent intent = new Intent(view.getContext(), setPersistService.class);
                getActivity().stopService(intent);
                mCurrentPhotoPath = "";
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("photoPath", "");
                editor.apply();
            }
        });
        return view;
    }

    private void changeImageViewSize(ImageView EImage, float scale) {
        Drawable d = EImage.getDrawable();
        if (d != null) {
            ViewGroup.LayoutParams params = EImage.getLayoutParams();
            params.width = (int) (d.getMinimumHeight() * scale);
            params.height = (int) (d.getMinimumHeight() * scale);
            EImage.setLayoutParams(params);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String tempPath = "";
            if (requestCode == ImageFragment.REQUEST_IMAGE_CAPTURE) {
                tempPath = mCurrentCameraPath;
            }
            if (requestCode == ImageFragment.REQUEST_IMAGE_GALLERY) {
                tempPath = getFileUri(data.getData());
                if (tempPath == null) {
                    Toast.makeText(getContext(), R.string.retrieveError, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            File imgFile = new File(tempPath);
            if (imgFile.exists()) {
                mCurrentPhotoPath = imgFile.getAbsolutePath();
                Bitmap bitmap = bitmapFromFilePath(mCurrentPhotoPath);
                Activity activity = getActivity();
                ImageView mImageView = activity.findViewById(R.id.image);
                mImageView.setImageResource(0);
                mImageView.setBackground(null);
                mImageView.setImageBitmap(bitmap);
                changeImageViewSize(mImageView, 1);
                SeekBar size_seek = activity.findViewById(R.id.size_seeker);
                if (size_seek != null) {
                    size_seek.setProgress(99);
                }
            } else {
                Toast.makeText(getContext(),  R.string.retrieveError, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: file:\n" + imgFile);
            }
        }
    }

    static Bitmap bitmapFromFilePath(String photoPath) {
        try {
            return BitmapFactory.decodeFile(photoPath);
        } catch (OutOfMemoryError oomE) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                return BitmapFactory.decodeFile(photoPath, options);
            } catch (Exception e) {
                Log.e(TAG, "bitmapFromFilePath: ", e);
            }
        }
        return null;
    }

    private String getFileUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return null;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentCameraPath = image.getAbsolutePath();
        return image;
    }

    private boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermissionForReadExternalStorage() {
        try {
            int READ_STORAGE_PERMISSION_REQUEST_CODE = 2;
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
