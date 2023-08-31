package com.example.hcart;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfile extends Fragment {


    View view;
    TextView submit, male, female;
    ConstraintLayout lay;
    DatabaseReference reference, get_ref;
    String gender ="";
    Dialog dialog;
    public ActivityResultLauncher<Intent> resultLauncher;
    String selectedImagePath = "";
    FirebaseUser user;
    FirebaseAuth auth;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    Context contextNullSafe;
    EditText number, bio, insta, fb;
    String fcb, inst, num, dp_uri, position, gen;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri imageUri;
    SimpleDraweeView shopImage;
    ImageView back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        submit = view.findViewById(R.id.save);
        number = view.findViewById(R.id.number);
        bio = view.findViewById(R.id.bio);
        male = view.findViewById(R.id.male);
        female = view.findViewById(R.id.female);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        lay = view.findViewById(R.id.lay1);
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        submit = view.findViewById(R.id.save);
        //Editext
        insta = view.findViewById(R.id.instagram);
        fb = view.findViewById(R.id.adhaaar);
        bio = view.findViewById(R.id.bio);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        shopImage = view.findViewById(R.id.image);

        back = view.findViewById(R.id.back);


        valueGetting();


        shopImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(intent);
                //dialog.dismiss();
            }
        });


        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() != null) {
                    imageUri = result.getData().getData();
                    addImageNote(imageUri);
                }
            }
        });

        submit.setOnClickListener(v -> {
            submit_click();
        });


        male.setOnClickListener(v -> {
            male.setBackgroundResource(R.drawable.bg_selector);
            female.setBackgroundResource(R.drawable.bg_male);
            gender = "Male";
        });


        female.setOnClickListener(v -> {
            female.setBackgroundResource(R.drawable.bg_selector);
            male.setBackgroundResource(R.drawable.bg_male);
            gender = "Female";
        });

        back.setOnClickListener(v -> {
            bck();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
    }

    private void addImageNote(Uri imageUri) {

        shopImage.setVisibility(View.VISIBLE);
        selectedImagePath = compressImage(imageUri + "");
        shopImage.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        //findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

    }


    public void submit_click() {

        if (!number.getText().toString().trim().equals("")) {
            if (!bio.getText().toString().trim().equals("")) {

                uploadImage();

            } else {
                bio.setError("Empty");
                Snackbar.make(lay, "Please Add Your Number", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.parseColor("#171746"))
                        .setTextColor(Color.parseColor("#FF7F5C"))
                        .setBackgroundTint(Color.parseColor("#171746"))
                        .show();
            }
        } else {
            number.setError("Empty");
            Snackbar.make(lay, "Please Enter Your position", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.parseColor("#171746"))
                    .setTextColor(Color.parseColor("#FF7F5C"))
                    .setBackgroundTint(Color.parseColor("#171746"))
                    .show();
        }
    }
    public void sendData(){


        reference.child(user.getUid()).child("position").setValue(bio.getText().toString());
        reference.child(user.getUid()).child("number").setValue(number.getText().toString());
        if(!insta.getText().toString().trim().equals(""))
            reference.child(user.getUid()).child("instagram").setValue(insta.getText().toString().trim());
        if(!fb.getText().toString().trim().equals(""))
            reference.child(user.getUid()).child("adhaar").setValue(fb.getText().toString().trim());

        if (gender.equals("Male"))
            reference.child(user.getUid()).child("gender").setValue("Male");
        else if (gender.equals("Female"))
            reference.child(user.getUid()).child("gender").setValue("Female");
    }

    private void uploadImage() {
        if (!selectedImagePath.equals("")) {

            dialog = new Dialog(getContextNullSafety());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.loading);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            //for image storing
            String imagepath = "Profile/";

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);
            Log.e("check_uris",selectedImagePath);
            Log.e("check_uris2",storageReference.toString());


            try {
                InputStream stream = new FileInputStream(new File(selectedImagePath));
                storageReference.putStream(stream)
                        .addOnSuccessListener(taskSnapshot ->
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                        task -> {
                                            String image_link = Objects.requireNonNull(task.getResult()).toString();
                                            reference.child(user.getUid()).child("dp_link").setValue(image_link);
                                        }));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            number.getText().toString().trim() + ".png");
            ref.putFile(imageUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    dialog.dismiss();
                                    bck();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            sendData();
            dialog = new Dialog(requireActivity());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.loading);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    bck();
                }
            }, 1000);

        }
    }


    public void bck(){
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(EditProfile.this).commit();
    }

    private void valueGetting() {

        get_ref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        get_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    fcb = snapshot.child("adhaar").getValue(String.class);
                    inst = snapshot.child("instagram").getValue(String.class);
                    dp_uri = snapshot.child("dp_link").getValue(String.class);
                    num = snapshot.child("number").getValue(String.class);
                    position = snapshot.child("position").getValue(String.class);
                    gen = snapshot.child("gender").getValue(String.class);

                    if (!dp_uri.equals("")) {
                        shopImage.setVisibility(View.VISIBLE);
                        try {
                            Uri uri = Uri.parse(dp_uri);
                            shopImage.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        shopImage.setVisibility(View.VISIBLE);
                        try {
                            Uri uri = Uri.parse(String.valueOf(R.drawable.ic_avtar));
                            shopImage.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    assert gen != null;
                    if (gen.equals("Male")) {
                        male.setBackgroundResource(R.drawable.bg_selector);
                        female.setBackgroundResource(R.drawable.bg_male);
                    }
                    if (gen.equals("Female")) {
                        male.setBackgroundResource(R.drawable.bg_male);
                        female.setBackgroundResource(R.drawable.bg_selector);
                    }

                    //setting values

                    fb.setText(fcb);
                    insta.setText(inst);
                    number.setText(num);
                    bio.setText(position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(Uri.parse(imageUri),requireActivity());
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight+1;
        int actualWidth = options.outWidth+1;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            assert scaledBitmap != null;
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(requireActivity().getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }

    private static String getRealPathFromURI(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = requireActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            Log.e("column",index+"");
            return cursor.getString(index)+"";
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}