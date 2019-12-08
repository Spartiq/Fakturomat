package bartlek.fakturomat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddNew extends AppCompatActivity {

    EditText adress, name, nip;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        saveButton = findViewById(R.id.save);
        cancelButton = findViewById(R.id.cancel);
        adress  = findViewById(R.id.companyAdress);
        name = findViewById(R.id.companyName);
        nip = findViewById(R.id.nip);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Company.PROPERTIES.name.toString(), name.getText().toString());
                intent.putExtra(Company.PROPERTIES.nip.toString(), nip.getText().toString());
                intent.putExtra(Company.PROPERTIES.adress.toString(), adress.getText().toString());
                setResult(100, intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setResult(200);
                finish();
            }
        });


    }
}
