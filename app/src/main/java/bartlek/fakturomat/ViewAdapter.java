package bartlek.fakturomat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private int STORAGE_PERMISSION_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 1;
    private int SAVE_PERMISSION_CODE = 1;

    public String action;
    private List<Company> mCompanies;
    private Context mContext;
    private String mClicked;

    public ViewAdapter(List<Company> listOfCompanies,  Context context){
        mCompanies = listOfCompanies;
        mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent , false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.companyName.setText(mCompanies.get(position).getName());
        holder.companyAdress.setText(mCompanies.get(position).getAdress());
        holder.recycleViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClicked = (String) Objects.requireNonNull(holder).companyName.getText();

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.inflate(R.menu.gallery_or_camera);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent();
                        switch (item.getItemId()){
                            case R.id.camera:
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra("company",mClicked);
                                File photoFile = createImageFile();
                                if (photoFile != null) {
                                    Uri photoURI = Uri.fromFile(photoFile);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    Log.d(TAG, "onMenuItemClick: "+intent+ mClicked);

                                    intent.setFlags(0);
                                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestSavePermission();
                                    }

                                    ActivityCompat.startActivityForResult((Activity) mContext,intent,300,null);
                                }else {
                                    Log.d(TAG, "onMenuItemClick: Failed to make Activity");
                                }
                                    //ActivityCompat.startActivityForResult((Activity) mContext,intent,300,null);
                                break;

                            case R.id.gallery:
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                intent.putExtra("company",mClicked);
                                ActivityCompat.startActivityForResult((Activity) mContext,intent,400,null);

                                Log.d(TAG, "onMenuItemClick: "+mClicked);
                                break;

                            default:
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCompanies.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView companyName, companyAdress;
        LinearLayout recycleViewItem;

        public ViewHolder (View itemView){
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            companyAdress = itemView.findViewById(R.id.companyAdress);
            recycleViewItem = itemView.findViewById(R.id.recycler_view_item);
        }

    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp+"_";
        File wallpaperDirectory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)));
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    wallpaperDirectory
            );
            Log.d(TAG, "createImageFile: file created "+ imageFileName+".jpg");
        }catch (java.io.IOException e){
            Log.d(TAG, "createImageFile: Failed to create IMAGE: "+ imageFileName+".jpg");
        }
        return image;
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(mContext)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext,
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
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
        }
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(mContext)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext,
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
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void requestSavePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(mContext)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext,
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
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, SAVE_PERMISSION_CODE);
        }
    }
}
