package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.real.data.AuctionContent;
import com.example.real.databasemanager.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AuctionBidActivity extends AppCompatActivity {

    TextView AuctionPriceEditText;
    TextView AuctionCurrentPriceTextView;
    TextView AuctionPriceGapTextView;
    ImageView UserPaddleImageView;
    CardView AuctionPriceBidBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_auction_bid);
        setContentView(R.layout.activity_auction_bid_design);

        String contentId = getIntent().getStringExtra("contentId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirestoreManager firestoreManagerForAuctionContent =
                new FirestoreManager(AuctionBidActivity.this, "AuctionContent", user.getUid());

        AuctionPriceEditText = findViewById(R.id.AuctionBidActivityBiddingPriceEditText);
        AuctionPriceBidBtn = findViewById(R.id.AuctionBidActivityBidButton);
        AuctionCurrentPriceTextView = findViewById(R.id.AuctionBidActivityCurrentPriceTextView);
        AuctionPriceGapTextView = findViewById(R.id.AuctionBidActivityPriceGapTextView);
        UserPaddleImageView = findViewById(R.id.AuctionBidActivityUserPaddleImageView);

        firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                AuctionContent auctionContent = (AuctionContent)object;
                Long bidPrice = Long.parseLong(auctionContent.getPrice()) + Long.parseLong(auctionContent.getPriceGap());
                AuctionPriceEditText.setText(String.valueOf(bidPrice));
                AuctionCurrentPriceTextView.setText(auctionContent.getPrice());
                AuctionPriceGapTextView.setText(auctionContent.getPriceGap());

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
                        //userList.add(user.getUid());

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
                            String userListItem = user.getUid() + "#" + bidPrice;
                            userList.add(userListItem);
                            // userList, bidPrice, priceGap
                            // Start transaction for change bidprice, pricegap, userList
                            List<Object> newFieldData = new ArrayList<>(); List<String> fieldPath = new ArrayList<>();
                            newFieldData.add(bidPrice); newFieldData.add(priceGap);
                            fieldPath.add("price"); fieldPath.add("priceGap");
                            firestoreManagerForAuctionContent.transactionUpdate(newFieldData, "Content", contentId, fieldPath, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    firestoreManagerForAuctionContent.update("Content", contentId, "auctionUserList", userList, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            setResult(100);
                                            Intent intent = new Intent(AuctionBidActivity.this, AuctionContentActivity.class);
                                            intent.putExtra("ContentId", contentId);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }, new Callback() {
                                @Override
                                public void OnCallback(Object object) {

                                }
                            });
                            /*
                            firestoreManagerForAuctionContent.update("Content", contentId, "auctionUserList", userList, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    firestoreManagerForAuctionContent.update("Content", contentId, "price", bidPrice, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            firestoreManagerForAuctionContent.update("Content", contentId, "priceGap", priceGap, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {

                                                }
                                            });
                                        }
                                    });
                                }
                            });*/
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}