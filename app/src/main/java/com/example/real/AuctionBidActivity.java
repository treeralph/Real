package com.example.real;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.real.data.AuctionContent;
import com.example.real.databasemanager.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class AuctionBidActivity extends AppCompatActivity {

    TextView AuctionPriceEditText;
    Button AuctionPriceBidBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_bid);

        String contentId = getIntent().getStringExtra("contentId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirestoreManager firestoreManagerForAuctionContent =
                new FirestoreManager(AuctionBidActivity.this, "AuctionContent", user.getUid());

        AuctionPriceEditText = findViewById(R.id.AuctionBidActivityPriceEditText);
        AuctionPriceBidBtn = findViewById(R.id.AuctionBidActivityBidBtn);

        firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                AuctionContent auctionContent = (AuctionContent)object;
                int bidPrice = Integer.parseInt(auctionContent.getPrice()) + Integer.parseInt(auctionContent.getPriceGap());
                AuctionPriceEditText.setText(String.valueOf(bidPrice));
            }
        });

        // Caution : Race Condition ( 후에 mutual exclusive 추가 예정 )
        AuctionPriceBidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        AuctionContent auctionContent = (AuctionContent)object;
                        ArrayList<String> userList = auctionContent.getAuctionUserList();
                        userList.add(user.getUid());

                        AuctionContent tempAuctionContent = null;
                        try {
                            tempAuctionContent = (AuctionContent)auctionContent.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                        String bidPrice = AuctionPriceEditText.getText().toString();
                        tempAuctionContent.setPrice(bidPrice);
                        tempAuctionContent.PriceGapPolicy(bidPrice);
                        String priceGap = tempAuctionContent.getPriceGap();

                        // minimum bidable price
                        int threshold = Integer.parseInt(auctionContent.getPrice()) + Integer.parseInt(auctionContent.getPriceGap());

                        if(Integer.parseInt(bidPrice) >= threshold) { // Bid price test
                            // userList, bidPrice, priceGap
                            firestoreManagerForAuctionContent.update("Content", contentId, "auctionUserList", userList, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    firestoreManagerForAuctionContent.update("Content", contentId, "price", bidPrice, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            firestoreManagerForAuctionContent.update("Content", contentId, "priceGap", priceGap, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {
                                                    Intent intent = new Intent(AuctionBidActivity.this, AuctionContentActivity.class);
                                                    intent.putExtra("ContentId", contentId);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else{ // print error message
                            Toast.makeText(AuctionBidActivity.this, "" +
                                    "Bid Price must be greater than minimum coast" + "(" + String.valueOf(threshold) + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // Race condition



    }
}