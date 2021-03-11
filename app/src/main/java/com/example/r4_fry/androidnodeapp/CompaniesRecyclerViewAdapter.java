package com.example.r4_fry.androidnodeapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;
import com.example.r4_fry.androidnodeapp.NodeDiagram.NodeDiagramActivity;

import java.util.ArrayList;

/**
 * Class to manage the RecyclerView in the MainActivity
 */
public class CompaniesRecyclerViewAdapter extends RecyclerView.Adapter<CompaniesRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Company> mDataset;
    public static final String COMPANY_ID_KEY = "com.example.r4_fry.androidnodeapp.companyId";
    public static final String COMPANY_NAME_KEY = "com.example.r4_fry.androidnodeapp.companyName";


    /**
     * class used to define ViewHolder for use by RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // elements within this view
        public TextView textView;
        public CardView cardView;


        /**Set up the  card to display the data and specify click listener to start new activity
         * @param v view
         */
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.companyTextView);
            cardView = v.findViewById(R.id.companiesCardView);
            cardView.setOnClickListener((View v2)->{
                Log.d("Click detected: ", mDataset.get(getAdapterPosition()).getTitle());
                Context context = v2.getContext();
                Intent intent = new Intent(context, NodeDiagramActivity.class);
                intent.putExtra(COMPANY_ID_KEY, mDataset.get(getAdapterPosition()).getCompanyId());
                intent.putExtra(COMPANY_NAME_KEY, mDataset.get(getAdapterPosition()).getTitle());
                context.startActivity(intent);

            });

        }
    }

    /**Constructs the LiveData adaptor and sets up the initial data
     * @param myDataset initial dataset for use by recylerview
     */
    public CompaniesRecyclerViewAdapter(ArrayList<Company> myDataset) {
        mDataset = myDataset;
    }

    /**Called by the layout manager to create each list item
     * @param parent parent context for inflating the layout from file
     * @param viewType viewType unused
     * @return
     */
    @NonNull
    @Override
    public CompaniesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.companies_recycler_view_item, parent,  false);
        return new MyViewHolder(v);
    }

    /**Updates the data held in the view to reflect what is passed in to the data list
     * @param holder holds the view that has been inflated
     * @param position position the view will have in the UI
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(mDataset != null) {
            // if Data-set is initialised set the text of it to that of the title in the array
            Log.d("BindViewHolder", mDataset.get(position).getTitle());
            holder.textView.setText(mDataset.get(position).getTitle());
        }else{
            holder.textView.setText(R.string.emptyListWarning);
        }

    }

    /**
     * @return number of elements to be displayed
     */
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset == null)
            return 0;
        else
            return mDataset.size();
    }

    /**
     * @param dataset collection of companies to be displayed in the RecyclerView
     */
    void setDataset(ArrayList<Company> dataset) {
        mDataset = null;
        mDataset = dataset;
        notifyDataSetChanged();
    }
}
