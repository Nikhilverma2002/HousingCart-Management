package com.example.hcart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.furkanakdemir.surroundcardview.SurroundCardView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import www.sanju.motiontoast.MotionToast;


public class Scanner extends Fragment {

    View view;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    String pushkey;
    Context contextNullSafe;
    Dialog dialog1;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView back;
    String entry;
    private BarcodeDetector barcodeDetector;
    DatabaseReference reference,user_ref;
    String currentDate,currentTime;
    LinearLayout layout;
    SurroundCardView in, out;
    int attend = 0;
    int val =0,count;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_scanner, container, false);

        back = view.findViewById(R.id.back_img);
        initViews();
        layout = view.findViewById(R.id.linearLayout2);
        reference = FirebaseDatabase.getInstance().getReference().child("Attendance");
        user_ref = FirebaseDatabase.getInstance().getReference().child("users");

        auth =  FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        back.setOnClickListener(v->{
           back();
        });


        in.setOnClickListener(v -> {
            if (!in.isCardSurrounded()) {
                in.setSurroundStrokeWidth(R.dimen.width_card);
                in.surround();
                out.release();
                entry = "IN";
                val = 1;
            }
        });

        out.setOnClickListener(v -> {
            if (!out.isCardSurrounded()) {
                out.setSurroundStrokeWidth(R.dimen.width_card);
                out.surround();
                in.release();
                entry = "OUT";
                reference.child(currentDate).child(user.getUid()).child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("IN").exists()){
                            val = 2;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;

    }

    private void initViews() {
        in = view.findViewById(R.id.alumni);
        out = view.findViewById(R.id.student);
        txtBarcodeValue = view.findViewById(R.id.txtBarcodeValue);
        surfaceView = view.findViewById(R.id.surfaceView);
    }

  /*   reference.child(currentDate).child(user.getUid()).child("name").setValue(user.getDisplayName());
                                        reference.child(currentDate).child(user.getUid()).child("uid").setValue(user.getUid());
                                        reference.child(currentDate).child(user.getUid()).child("email").setValue(user.getEmail());
                                        reference.child(currentDate).child(user.getUid()).child("time").child(entry).setValue(currentTime);
                                        reference.child(currentDate).child(user.getUid()).child("date").setValue(currentDate);
*/
    private void initialiseDetectorsAndSources() {

        //Toast.makeText(requireActivity().getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(requireActivity().getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).toString() != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).rawValue;
                                txtBarcodeValue.setText(intentData);

                                if (val!=0) {
                                    if (intentData.equals(currentDate)) {
                                        attendance();
                                        count = 1;
                                    }
                                }

                            } else {
                                Toast.makeText(requireActivity(), "Please scan the correct scanner!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public void getAttendance(){

        user_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attend = Integer.parseInt(Objects.requireNonNull(snapshot.child("present").getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void attendance() {

        //getAttendance();

        if (count != 1) {
            currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());


            reference.child(currentDate).child(user.getUid()).child("name").setValue(user.getDisplayName());
            reference.child(currentDate).child(user.getUid()).child("uid").setValue(user.getUid());
            reference.child(currentDate).child(user.getUid()).child("email").setValue(user.getEmail());
            reference.child(currentDate).child(user.getUid()).child("date").setValue(currentDate);
            user_ref.child(user.getUid()).child("present").setValue(attend+1);

            if (entry.equals("IN")) {
                reference.child(currentDate).child(user.getUid()).child("time").child(entry).setValue(currentTime);
                reference.child(currentDate).child(user.getUid()).child("time").child("OUT").setValue("");
                sendAttendance();
                inmotion();
            }
            if (entry.equals("OUT")) {
                reference.child(currentDate).child(user.getUid()).child("time").child("OUT").setValue(currentTime);
                sendAttendance();
                outmotion();
            }
        }
    }

        public void inmotion() {

            if (check_timing(currentTime.substring(0, 5), "10:10")) {
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Great, You are on time. \uD83D\uDE03",
                        "Attendance Marked!!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.quicksand_bold));
                reference.child(currentDate).child(user.getUid()).child("in_str").setValue("OnTime");
            } else {
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "You are late!! \uD83D\uDE03",
                        "Attendance Marked!!",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.quicksand_bold));
                reference.child(currentDate).child(user.getUid()).child("in_str").setValue("Late");
            }
            back();
        }

    public  void outmotion(){
            if (!check_timing(currentTime, "18:00")) {
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Great, You are leaving on time. \uD83D\uDE03",
                        "Attendance Marked!!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.quicksand_bold));
                reference.child(currentDate).child(user.getUid()).child("out_str").setValue("OnTime");
            } else {
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Specify a reason for leaving early to admin.",
                        "Attendance Marked!!",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.quicksand_bold));
                reference.child(currentDate).child(user.getUid()).child("out_str").setValue("Late");
            }
        back();
        }

    private boolean check_timing(String time, String endtime) {

        String pattern = "HH:mm";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            assert date1 != null;
            return date1.before(date2);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public void back(){
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(Scanner.this).commit();
    }

    public void sendAttendance(){
        StringRequest request = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyf01wVCogiqZ9qqHp6HIAwx64bCb60e2u2GeLawwLkIiRcZdVTad59J54g9j_24EV6tQ/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                dialog1 = new Dialog(getContextNullSafety());
                dialog1.setCancelable(false);
                dialog1.setContentView(R.layout.done);
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog1.show();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog1.dismiss();
                    }

                },1500);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("action","addAttendance");

                params.put("vName",user.getDisplayName());
                params.put("vDate",currentDate);
                params.put("vIn",currentTime);
                params.put("vOut",currentTime);


                return params;
            }
        };

        int socket = 5000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socket,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(getContextNullSafety());
        requestQueue.add((request));
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