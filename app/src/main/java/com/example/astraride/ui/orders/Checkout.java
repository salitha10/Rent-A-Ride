package com.example.astraride.ui.orders;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astraride.R;
import com.example.astraride.models.Order;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Checkout extends AppCompatActivity {

    EditText name, email, phoneNo, address, pickupDate, returnDate;
    TextView totalCost;
    Button checkout;
    String rental, currentUser, itemID, pDate, rDate, totalPrice;
    Order order;
    RadioButton cash, card;
    DatabaseReference dbf;
    DatePickerDialog picker;
    long duration;
    Date pickDate, retDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");
        rental = intent.getStringExtra("rental");
        Log.d("rental", rental);

        //Initialize values
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        phoneNo = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        pickupDate = findViewById(R.id.PickupDate);
        returnDate = findViewById(R.id.ReturnDate);
        checkout = findViewById(R.id.btnPay);
        cash = findViewById(R.id.radioCash);
        card = findViewById(R.id.radioCard);
        totalCost = findViewById(R.id.TotalCost);

        //Setup Date picker
        pickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Checkout.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                pDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                try {
                                    pickDate = new SimpleDateFormat("dd/MM/yyyy").parse(pDate);
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                                pickupDate.setText(pDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Checkout.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                rDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                //Calculate total cost
                                try {
                                    retDate = new SimpleDateFormat("dd/MM/yyyy").parse(rDate);
                                }catch (Exception e){e.printStackTrace();}

                                calculate_cost();
                                returnDate.setText(rDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        //When checkout button pressed
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check radio button click
                if(cash.isChecked()){
                    save();
                }
                else {
                    showPayment();
                }
            }
        });

    }

    //Show payment screen
    private void showPayment() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.activity_payment);

        LinearLayout layout = bottomSheetDialog.findViewById(R.id.bottom_sheet_payment);
        Button pay = bottomSheetDialog.findViewById(R.id.btnPay);
        TextView amount = bottomSheetDialog.findViewById(R.id.payment_amount);
        amount.setText("LKR." + totalPrice);

        //When pay button is clicked
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                bottomSheetDialog.cancel();
                //Intent intent = new Intent(Checkout.this, Payment.class);
//                startActivity(intent);
            }
        });

        bottomSheetDialog.show();
    }

    //Save values
    public void save(){

        ProgressDialog pd = new ProgressDialog(Checkout.this);
        pd.setMessage("Uploading...");
        pd.show();
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String time = Long.toString(System.currentTimeMillis());
        order = new Order();
        String id = time + currentUser;

        order.setName(name.getText().toString().trim());
        order.setEmail(email.getText().toString().trim());
        order.setPhoneNo(phoneNo.getText().toString().trim());
        order.setAddress(address.getText().toString().trim());
        order.setPickupDate(pickupDate.getText().toString().trim());
        order.setReturnDate(returnDate.getText().toString().trim());
        order.setOrderId(id);
        order.setBuyerId(currentUser);
        order.setItemID(itemID);
        order.setOrderDate(date);

        //Save to database
        try {
            dbf = FirebaseDatabase.getInstance().getReference().child("Orders").child(id);
            dbf.setValue(order);
            Toast.makeText(Checkout.this, "Order Placed", Toast.LENGTH_SHORT).show();
        }
        catch(DatabaseException error){
            Log.e("DBError", error.getMessage());
        }
        pd.cancel();
    }

    //Convert string to data
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calculate_cost() {

        LocalDate date1 = pickDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = retDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period diff = Period.between(date1, date2);
        int hrs = diff.getDays() * 24;

        String totalPrice = Long.toString(hrs * Long.parseLong(rental)); //Calculate cost
        totalCost.setText("Total Cost: Rs." + totalPrice);

    }
}