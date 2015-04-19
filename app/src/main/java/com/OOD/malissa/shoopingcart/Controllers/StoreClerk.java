package com.OOD.malissa.shoopingcart.Controllers;

import android.content.Context;
import android.content.Intent;
import android.view.View;


import com.OOD.malissa.shoopingcart.Activities.BrowseList;
import com.OOD.malissa.shoopingcart.Activities.HelperClasses.Buyer;
import com.OOD.malissa.shoopingcart.Activities.HelperClasses.Seller;
import com.OOD.malissa.shoopingcart.Activities.HelperClasses.User;
import com.OOD.malissa.shoopingcart.Activities.Interfaces.UserType;
import com.OOD.malissa.shoopingcart.Activities.Login;
import com.OOD.malissa.shoopingcart.Activities.ProductDetails;
import com.OOD.malissa.shoopingcart.Models.AccountList;
import com.OOD.malissa.shoopingcart.Models.Interfaces.NewIterator;
import com.OOD.malissa.shoopingcart.Models.SellerAccount;
import com.OOD.malissa.shoopingcart.Models.BuyerAccount;
import com.OOD.malissa.shoopingcart.Models.Inventory;
import com.OOD.malissa.shoopingcart.Models.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Malissa on 3/29/2015.
 *
 */
public class StoreClerk {


    //region INSTANCE VARIABLES
    protected  User _user;
    protected  BuyerAccount _userAccountB; // when doing anything involving specific accounttype data, use casting. static so buyer/seller clerk can have access
    protected  SellerAccount _userAccountS;
    protected AccountList _accList;
    //protected NewIterator _sellerIterator; // might not use??
    protected Inventory _currentInventory; // MIGHT NOT USE?
    //endregion

    //region SINGLETON SETUP
    private static StoreClerk ourInstance = new StoreClerk();

    public static StoreClerk getInstance() {
        return ourInstance;
    }

    // protected so the children of class can use them
    protected StoreClerk() {
        this._accList = AccountList.getInstance();
        this._user = null;
        this._userAccountB = null;
        this._userAccountS = null;
        //this._sellerIterator = null;
        this._currentInventory = null;
    }
    //endregion

    public SellerAccount get_userAccountS(){ return this._userAccountS;}

    public void initializeModel(String key){

    }

    /**
     * Initializes all models in system
     * All model info is located in the Login context
     */
    public void initializeAllModel(Context context){
        // used to store the current storage key
        String currentStorageKey = null;
        Object savedItem = null;

        try {
            //initialize buyerlist
            currentStorageKey = "BuyerList";
           savedItem =  StorageController.readObject(context,currentStorageKey);
            //todo: check to make sure if storagecontroller throws IOException if there is
            //todo: nothing in the Internal Storage. if so, add logic to use pre made stuff
            //_accList.initialized(savedItem, currentStorageKey);

            //initialize sellerlist
            currentStorageKey = "SellerList";
           // savedItem = StorageController.readObject(context,currentStorageKey);
           // _accList.initialized(savedItem, currentStorageKey);

        } catch ( ClassCastException e) {
            System.out.println("Incorrect object cast for : " + currentStorageKey);
            e.printStackTrace();

        }  catch (IOException e) {
            System.out.println("Issue getting data from: " + currentStorageKey);
            e.printStackTrace();
            _accList.initialized(savedItem, currentStorageKey);
            currentStorageKey = "SellerList";
            _accList.initialized(savedItem, currentStorageKey);

        } catch (ClassNotFoundException e) {
            System.out.println("The class is not found. Issue getting data from: " + currentStorageKey);
            e.printStackTrace();
        }
        // initialize account lists



    }

    public void updateStorage(){

    }

    /**
     * This is used to iterate through _accList to check the username
     * and password of the user.
     * @param username a String which contains the user's username
     * @param pass a String which contains the user's password
     * @param isSeller a boolean which determines whether to iterate
     *                 through the seller accounts or buyer accounts
     * @return a boolean to determine if the account was found or not.
     */
    public boolean verifyAccount(String username, String pass, boolean isSeller){
        // set the account list isSeller status so that the list knows which list to look at
        _accList.set_isLookingForSeller(isSeller);

        for(Iterator iter = _accList.iterator(); iter.hasNext();) {
            // todo: reset the iterator once the user account is found. might need to add new/modify first function for iterator
            if(isSeller){
                this._userAccountS = (SellerAccount) iter.next();
                if (this._userAccountS.getPassword().equals(pass) && this._userAccountS.getUsername().equals(username))
                    {return true;}
                //_userAccountS = null;
            }
            else {

                this._userAccountB = (BuyerAccount) iter.next();
                if (this._userAccountB.getPassword().equals(pass) && this._userAccountB.getUsername().equals(username))
                    return true;
                //_userAccountB = null;
            }

        }

        return false;
    }

    /**
     * A function that calls verifyAccount then calls the function that
     * sets up BrowseList if the account is verified.
     * @param username
     * @param pass
     * @param isSeller
     *
     */
    public void login(String username, String pass, boolean isSeller) {

        if(verifyAccount(username, pass, isSeller)) {

            Login.logInFail.setVisibility(View.GONE);
            if(isSeller) {
                _user = User.SELLER;
                //forward info to sellerClerk
                SellerClerk.getInstance().setUpUser(this._user,this._userAccountS);
            }
            else {
                _user = User.BUYER;
                //forward info to sellerClerk
                BuyerClerk.getInstance().setUpUser(this._user,this._userAccountB);
            }

            // set up the correct user for the browselist
            setUser();

        }
        else
        {
            //todo: make this a dialog box
            Login.logInFail.setVisibility(View.VISIBLE);
        }




    }

    /**
     * Used to set which user logged in and call setup function for that user
     * removed use of the usertype
     */
    private void setUser(){
    //private void setUser(UserType user){

       // user.setUp(_user);

        Intent i = new Intent(Login.getAppContext(), BrowseList.class);
        i.putExtra("User", this._user);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Login.getAppContext().startActivity(i);
    }



    public User currentUserType() { return this._user;}

    /**
     * Function used to get the productDetails of something
     * @param item
     */
    public void getProductDets(Product item){

        Intent i = new Intent(BrowseList.getAppContext(), ProductDetails.class);
        // grab the user type
        i.putExtra("User", this._user);
        // grab the product information
        i.putExtra("Product", item.toArrayList());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BrowseList.getAppContext().startActivity(i);


    }

    public void updateAccount(ArrayList<String> infoListArrayList){

    }

    public void showAccountInfo(){

    }


}
