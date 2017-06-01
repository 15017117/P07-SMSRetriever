package com.myapplicationdev.android.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_second extends Fragment {
    Button btnRetrieve2,btnSend;
    TextView tvFrag2;
    EditText etWord;



    public fragment_second() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_fragment_second, container, false);
        btnRetrieve2 = (Button)view.findViewById(R.id.btnRetrive2);
        etWord = (EditText)view.findViewById(R.id.etWord);
        tvFrag2 = (TextView)view.findViewById(R.id.tvFrag2);
        btnSend = (Button)view.findViewById(R.id.btnSend);
        btnRetrieve2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);
                        if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_SMS},0);

                            return;
                        }
                        Uri uri = Uri.parse("content://sms");

                        String[] reqCols = new String[]{"date","address","body","type"};
                        String word = etWord.getText().toString();
                String[]name = word.split("");
                String filter = "body LIKE ?";
                for(int i=0;i<name.length;i++){
                    name[i] = "%"+name[i]+"%";
                    if(i !=0){
                        filter +=(" OR BODY LIKE ?");
                    }


                }



                        ContentResolver cr = getActivity().getContentResolver();



                        Cursor cursor = cr.query(uri,reqCols,filter,name,null);
                        String smsBody = "";
                        if(cursor.moveToFirst()){
                            do{
                                long dateInMillis = cursor.getLong(0);
                                String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa",dateInMillis);
                                String address = cursor.getString(1);
                                String body = cursor.getString(2);
                                String type = cursor.getString(3);
                                if(type.equalsIgnoreCase("1")){
                                    type="Inbox:";
                                } else{
                                    type="Sent:";
                                }
                                smsBody += type + " "+address+ "\n at "+date+"\n\""+body+"\"\n\n";

                            } while (cursor.moveToNext());
                        }
                        tvFrag2.setText(smsBody);


                    }
        });

        return view;
    }

            @Override
            public void onRequestPermissionsResult(int requestCode,
                                                   String permissions[], int[] grantResults) {

                switch (requestCode) {
                    case 0: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                            // permission was granted, yay! Do the read SMS
                            //  as if the btnRetrieve is clicked
                            btnRetrieve2.performClick();

                        } else {
                            // permission denied... notify user
                            Toast.makeText(getActivity(), "Permission not granted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL,new String[]{"jason_lim@rp.edu.sg"});
                        email.putExtra(Intent.EXTRA_SUBJECT,"SMS");
                        email.putExtra(Intent.EXTRA_TEXT,String.valueOf(tvFrag2));
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email,"Choose an Email client:"));
                    }
                });
            }


        }
