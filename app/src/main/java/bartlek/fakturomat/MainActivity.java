package bartlek.fakturomat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 1;
    private int SAVE_PERMISSION_CODE = 1;
    private static final String TAG = "JAVA MAIN";
    public String name;
    public String uri;
    final int RESULT_OK = 100;
    final int startActivity = 100;
    final int photoTaken = 300;
    final int pickedFromGallery = 400;
    Button addButton;
    ViewAdapter adapter;
    List<Company> companies = new ArrayList<>();
    Company companyTemplate;
    RecyclerView recyclerView;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Company cmp = null;
        Uri imageUri;
        Log.d(TAG, "onActivityResult: "+requestCode+" "+resultCode+" "+data);

        if(resultCode == 0)
            return;

        switch (requestCode) {
            case startActivity: {
                if (resultCode == RESULT_OK) {
                    companyTemplate.setName((String) data.getExtras().get(Company.PROPERTIES.name.toString()));
                    companyTemplate.setNip((String) data.getExtras().get(Company.PROPERTIES.nip.toString()));
                    companyTemplate.setAdress((String) data.getExtras().get(Company.PROPERTIES.adress.toString()));
                    companies.add(companyTemplate);
                    // Do something with the contact here (bigger example below)
                }
                break;
            }

            case photoTaken:{
                Intent intent = new Intent(this, OCR.class);

                if (null == data || null == data.getExtras())
                    return;
                Log.d(TAG, "onActivityResult: "+data);
                Log.d(TAG, "onActivityResult: "+data.getExtras());
                cmp = findByName(data.getExtras().getString("company"));
                Log.d(TAG, "onActivityResult: "+cmp);

                intent.putExtra(Company.PROPERTIES.name.toString(), cmp.getName());
                intent.putExtra(Company.PROPERTIES.nip.toString(), cmp.getNip());
                intent.putExtra(Company.PROPERTIES.adress.toString(), cmp.getAdress());
                intent.putExtra("uri", (String) data.getExtras().get( MediaStore.EXTRA_OUTPUT));
                intent.putExtra("source","camera");
                Log.d(TAG, "onActivityResult: start OCR for "+findByName(data.getExtras().getString("company")));
                startActivity(intent);
                break;
            }
            case pickedFromGallery: {
                Log.d(TAG, "onActivityResult: "+data);
               /* if (null == data.getExtras()) {
                    Log.d(TAG, "onActivityResult: no Extras passed");
                    return;
                }
               */
                Intent intent = new Intent(this, OCR.class);
                //cmp = findByName(data.getExtras().getString("company"));
                Log.d(TAG, "onActivityResult: "+cmp);

                imageUri =  (Uri) data.getData();

                intent.putExtra(Company.PROPERTIES.name.toString(), cmp.getName());
                intent.putExtra(Company.PROPERTIES.nip.toString(), cmp.getNip());
                intent.putExtra(Company.PROPERTIES.adress.toString(), cmp.getAdress());
                intent.putExtra("uri", imageUri);
                intent.putExtra("source","gallery");
                //Log.d(TAG, "onActivityResult: start OCR for"+findByName(data.getExtras().getString("company")));
                startActivity(intent);
                break;
            }
             /*       if (requestCode == startActivity) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                companyTemplate.setName((String) data.getExtras().get(Company.PROPERTIES.name.toString()));
                companyTemplate.setNip((String) data.getExtras().get(Company.PROPERTIES.nip.toString()));
                companyTemplate.setAdress((String) data.getExtras().get(Company.PROPERTIES.adress.toString()));
                companies.add(companyTemplate);
                // Do something with the contact here (bigger example below)
*/
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check permissions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestSavePermission();
        }


        //recycle list of companies

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ViewAdapter(companies, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //go to add view
        addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openNewCompanyForm();
            }
        });
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
        }
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void requestSavePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, SAVE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, SAVE_PERMISSION_CODE);
        }
    }


    public void openNewCompanyForm(){
        Intent intent = new Intent(this, AddNew.class);
        startActivityForResult(intent ,startActivity);
    }

    public Company findByName(String name){
        Log.d(TAG, "findByName: "+ companies + "looking for"+ name);
        for(Company com : companies){
            if(com.getName() == name)
                return com;
        }
        return null;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File wallpaperDirectory = new File(
                String.valueOf(Environment.getExternalStorageDirectory()));
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".png");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/png"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getPath());
            return f.getPath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}
