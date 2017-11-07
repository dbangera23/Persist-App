package com.dmbangera.deanbangera.peristantmessage;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        final ImageView EImage = view.findViewById(R.id.image);
        EImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        EImage.setAdjustViewBounds(true);
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        FrameLayout imageListener = view.findViewById(R.id.image_listener);
        mCurrentPhotoPath = settings.getString("photoPath", "");
        if (!checkPermissionForReadExtertalStorage()) {
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View camera_gallery_prompt = inflater.inflate(R.layout.image_or_message, container, false);
                    builder.setView(camera_gallery_prompt);
                    builder.setTitle(R.string.sourcePrompt);
                    ImageView gallery = camera_gallery_prompt.findViewById(R.id.gallery);
                    ImageView camera = camera_gallery_prompt.findViewById(R.id.camera);
                    final AlertDialog alert = builder.create();
                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (checkPermissionForReadExtertalStorage()) {
                                alert.cancel();
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
                            } else {
                                try {
                                    requestPermissionForReadExternalStorage();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), R.string.retrieveError, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.cancel();
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                File photoFile = createImageFile();
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    mCurrentCameraPath = photoFile.getAbsolutePath();
                                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                                            "com.dmbangera.deanbangera.peristantmessage.fileprovider",
                                            photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        }
                    });
                    alert.show();
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
                deleteExternalStoragePrivateFiles();
            }
        });
        return view;
    }

    private void deleteExternalStoragePrivateFiles() {
        File fileDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (fileDirectory != null) {
            Boolean allTrue = true;
            for (File file : fileDirectory.listFiles()) {
                if (file != null)
                    allTrue = file.delete();
            }
            if (!allTrue)
                Log.d(TAG, "deleteExternalStoragePrivateFiles: " + fileDirectory);
        }
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
            Bitmap bitmap = null;
            File imgFile = null;
            Uri selectedImageUri = data.getData();
            if (requestCode == ImageFragment.REQUEST_IMAGE_CAPTURE) {
                imgFile = new File(mCurrentCameraPath);
                if (imgFile.exists()) {
                    bitmap = bitmapFromFilePath(mCurrentCameraPath);
                    mCurrentPhotoPath = imgFile.getAbsolutePath();
                }
            } else {
                if (requestCode == ImageFragment.REQUEST_IMAGE_GALLERY) {
                    try {
                        InputStream is = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        bitmap = BitmapFactory.decodeStream(is);
                        imgFile = createImageFile();
                        bitmapToFilePath(bitmap, imgFile);
                        mCurrentPhotoPath = imgFile.getAbsolutePath();
                    } catch (IOException e) {
                        Toast.makeText(getContext(), R.string.retrieveError, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onActivityResult: file:\n" + selectedImageUri);
                        return;
                    }
                }
            }
            if (bitmap != null && imgFile.exists()) {
                mCurrentPhotoPath = imgFile.getAbsolutePath();
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
                Toast.makeText(getContext(), R.string.retrieveError, Toast.LENGTH_SHORT).show();
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

    private static void bitmapToFilePath(Bitmap bitmap, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
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
*/


    private File createImageFile()  {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName + ".jpg");
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
