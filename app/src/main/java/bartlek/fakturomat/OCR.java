package bartlek.fakturomat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class OCR extends AppCompatActivity {
    private static final String TAG = "OCR";
    ImageView imageView;
    String uri, source , name, adress ,nip;
    Company company;
    String[] activity = {"pickCompanyName","pickExpenses"};

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        imageView = findViewById(R.id.imageView);
        name = (String) getIntent().getExtras().get("name");
        adress = (String) getIntent().getExtras().get("adress");
        nip = (String) getIntent().getExtras().get("nip");
        source = (String) getIntent().getExtras().get("source");
        uri =  getIntent().getExtras().get("uri").toString();
        Log.d(TAG, "onCreate: " + name +" "+adress +" "+ nip +" "+ uri+ " "+ source);



        imageView.setImageURI(Uri.parse(uri));

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.d("OCR", "Detector dependencies are not yet available");
        } else {
            Bitmap bitMap = null;
            try {
                bitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uri));
                Frame frame = new Frame.Builder().setBitmap(bitMap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder sb = new StringBuilder();

                for(int i =0 ; i < items.size(); i++){
                    sb.append(items.keyAt(i));
                    sb.append("/n");
                }

                textView = findViewById(R.id.textView);
                Log.d("OCR", sb.toString());
                textView.setText(sb.toString());

            } catch (IOException e) {
                Log.d("OCR", "Failure: "+e);
                e.printStackTrace();
            }


        }
    }
}
