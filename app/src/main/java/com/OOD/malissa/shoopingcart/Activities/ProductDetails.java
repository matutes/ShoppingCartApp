package com.OOD.malissa.shoopingcart.Activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.OOD.malissa.shoopingcart.Activities.HelperClasses.User;
import com.OOD.malissa.shoopingcart.Controllers.BuyerClerk;
import com.OOD.malissa.shoopingcart.Controllers.SellerClerk;
import com.OOD.malissa.shoopingcart.Controllers.StoreClerk;
import com.OOD.malissa.shoopingcart.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductDetails extends Activity {

    //region INSTANCE VARIABLES
    // 0 - name, 1 - id, 2 - description, 3 - type, 4 - quantity, 5 - invoiceP, 6 - sellingP, 7 - sellerID
    private ArrayList<String> _productInfo;
    // might want to spell out which textview is which instead of having it in an array
    private ArrayList<EditText> _productTextFields;
    private User _currentUser;
    private Button _editBtn;
    private Button _addCart;
    private Button _saveBtn;
    private Button _cancelBtn;
    private Button _removeBtn;
    private EditText _productName;
    private EditText _productDes;
    private EditText _productCost;
    private EditText _productPrice;
    private EditText _productQuant;
    private EditText _productType;
    private DecimalFormat df = new DecimalFormat("0.00");

    private BuyerClerk bClerk = BuyerClerk.getInstance();
    private SellerClerk sClerk = SellerClerk.getInstance();
    private static Context context; // used to get the context of this activity. only use when onCreate of Activity has been called!
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProductDetails.context = getApplicationContext();
        _currentUser = (User) getIntent().getSerializableExtra("User");
        _productInfo = (ArrayList<String>) getIntent().getSerializableExtra("Product");
        setupView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if logout was clicked...
        if (id == R.id.logout) {
            // restart storeclerks
            BuyerClerk.getInstance().reset();
            SellerClerk.getInstance().reset();
            StoreClerk.getInstance().reset();

            // redirect user to login screen
            Intent i = new Intent(getApplicationContext(), Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function used to get the application's context. Only use if the application exists!
     * @return The context of this activity
     * @author Malissa Augustin
     */
    public static Context getAppContext() {
        return ProductDetails.context;
    }

    /**
     * Private method used to identify what view to show.
     * @author Malissa Augustin
     */
    private void setupView(){
        // set up which view to show
        if(_currentUser == User.BUYER){
            setContentView(R.layout.product_details_buyer);
        }
        else if(_currentUser == User.SELLER) {
            setContentView(R.layout.product_details_seller);
        }

        // set up button listeners first.
        setUpListeners();
    }

    /**
     * Private method used to setUp UI listeners based on User that logged in
     * @author Malissa Augustin
     */
    private void setUpListeners(){

        if(_currentUser == User.BUYER){
            //LINK UI OBJECTS TO XML HERE
            _productName = (EditText)  findViewById(R.id.bProductName);
            _productDes = (EditText)  findViewById(R.id.bprodDescrp);
            _productPrice = (EditText)  findViewById(R.id.bprice);
            _productQuant = (EditText)  findViewById(R.id.bquantity);
            _productType = (EditText)  findViewById(R.id.productType);
            _addCart = (Button) findViewById(R.id.addToCart);

            _addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add this item to cart and bring user to shopping cart screen
                    //add selected item to cart
                    BuyerClerk.getInstance().addToCart(_productInfo.get(1),_productInfo.get(7));
                    // and bring user to the shopping cart screen
                    BuyerClerk.getInstance().showShoppingCart();
                }
            });
        }
        else if(_currentUser == User.SELLER) {

            //LINK UI OBJECTS TO XML HERE
            _productName = (EditText)  findViewById(R.id.ProductName);
            _productDes = (EditText)  findViewById(R.id.prodDescrp);
            _productPrice = (EditText)  findViewById(R.id.price);
            _productQuant = (EditText)  findViewById(R.id.quantity);
            _productCost = (EditText)  findViewById(R.id.cost);
            _productType = (EditText)  findViewById(R.id.sProductType);
            _saveBtn = (Button) findViewById(R.id.SaveBtn);
            _editBtn = (Button) findViewById(R.id.editProd);
            _removeBtn = (Button) findViewById(R.id.removeProd);
            _cancelBtn = (Button) findViewById(R.id.cancelbtn);

            //set text for cost
            _productCost.setText(_productInfo.get(5));
            _productCost.setText(_productInfo.get(5));

            _productTextFields = new ArrayList<>();
            // setup each EditText item in array here
            _productTextFields.add(_productName);
            _productTextFields.add(_productDes);
            _productTextFields.add(_productPrice);
            _productTextFields.add(_productQuant);
            _productTextFields.add(_productCost);
            _productTextFields.add(_productType);


            //setting visibility of saveBtn to initially hide it
            _saveBtn.setVisibility(View.GONE);
            _cancelBtn.setVisibility(View.GONE);

            _editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the edit button and show the save button
                    _editBtn.setVisibility(View.GONE);
                    _removeBtn.setVisibility(View.GONE);
                    _saveBtn.setVisibility(View.VISIBLE);
                    _cancelBtn.setVisibility(View.VISIBLE);

                    // iterate through textfields and make them editable
                    for(EditText text : _productTextFields)
                    {
                        text.setEnabled(true);
                        text.setFocusableInTouchMode(true);
                        text.setBackgroundColor(Color.LTGRAY);

                    }
                }
            });
            _saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the save button and show the edit button
                    _editBtn.setVisibility(View.VISIBLE);
                    _removeBtn.setVisibility(View.VISIBLE);
                    _saveBtn.setVisibility(View.GONE);
                    _cancelBtn.setVisibility(View.GONE);

                    // iterate through textfields and make them not editable
                    for(EditText text : _productTextFields)
                    {
                        // format the number values of price and cost
                        if(text.equals(_productPrice)||text.equals(_productCost))
                        {
                            String num = df.format(Double.parseDouble(text.getText().toString()));
                            text.setText(num);
                        }
                        text.setEnabled(false);
                        text.setFocusable(false);
                        text.setBackgroundColor(Color.TRANSPARENT);
                    }


                    //grab data from each text field and
                    _productInfo.set(3,_productType.getText().toString());
                    _productInfo.set(5,_productCost.getText().toString());
                    _productInfo.set(4,_productQuant.getText().toString());
                    _productInfo.set(6,_productPrice.getText().toString());
                    _productInfo.set(2,_productDes.getText().toString());
                    _productInfo.set(0, _productName.getText().toString());

                    // update correct product from seller's inventory.
                    sClerk.saveProductChanges(_productInfo);

                    // post toast
                    Toast.makeText(getApplicationContext(), "Product was updated.",
                            Toast.LENGTH_LONG).show();

                }
            });

            _cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the save button and show the edit button
                    _editBtn.setVisibility(View.VISIBLE);
                    _removeBtn.setVisibility(View.VISIBLE);
                    _saveBtn.setVisibility(View.GONE);
                    _cancelBtn.setVisibility(View.GONE);

                    // iterate through textfields and make them editable
                    for (EditText text : _productTextFields) {

                        text.setEnabled(false);
                        text.setFocusable(false);
                        text.setBackgroundColor(Color.TRANSPARENT);
                    }

                    //set product info on screen
                    _productName.setText(_productInfo.get(0));
                    _productDes.setText(_productInfo.get(2));
                    _productType.setText(_productInfo.get(3));
                    _productPrice.setText(_productInfo.get(6));
                    _productQuant.setText(_productInfo.get(4));
                    _productCost.setText(_productInfo.get(5));
                }
            });

            _removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogFragment dialog = sClerk.getRemoveProductDialog(_productInfo);
                    dialog.show(getFragmentManager(), "removeProd");

                }
            });

        }
        //set product info on screen
        //
        _productName.setText(_productInfo.get(0));
        _productDes.setText( _productInfo.get(2) );
        _productType.setText(_productInfo.get(3) );
        _productPrice.setText(_productInfo.get(6));
        _productQuant.setText(_productInfo.get(4));

    }

}
