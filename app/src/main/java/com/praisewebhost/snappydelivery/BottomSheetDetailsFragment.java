package com.praisewebhost.snappydelivery;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BottomSheetDetailsFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_SELECT_PHONE_NUMBER = 1;
    EditText recieversPhone;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public BottomSheetDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_details, container, false);
        recieversPhone = v.findViewById(R.id.RecieversPhone);
        recieversPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
            }
        });

        return v;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == REQUEST_SELECT_PHONE_NUMBER){
//            if(resultCode == RESULT_OK){
//                Uri contactData = data.getData();
//                Cursor cursor =  managedQuery(contactData, null, null, null, null);
//
//                cursor.moveToFirst();
//
//                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                //contactName.setText(name);
//                contactNumber.setText(number);
//                //contactEmail.setText(email);
//            }
//        }
//    }
//}


}