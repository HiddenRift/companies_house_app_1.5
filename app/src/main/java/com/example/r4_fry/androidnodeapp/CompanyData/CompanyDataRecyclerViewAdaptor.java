package com.example.r4_fry.androidnodeapp.CompanyData;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.r4_fry.androidnodeapp.CompaniesDB.Company;
import com.example.r4_fry.androidnodeapp.CompaniesDB.Officer;
import com.example.r4_fry.androidnodeapp.R;

import java.util.ArrayList;


/**
 * RecyclerViewAdaptor for for the data from the database
 */
public class CompanyDataRecyclerViewAdaptor extends RecyclerView.Adapter<CompanyDataRecyclerViewAdaptor.MyViewHolder> {
    private static final int TITLE = 0;
    private static final int DESCRIPTION = 1;


    public CompanyDataRecyclerViewAdaptor(){
        foo = new ArrayList<>();
    }

    ArrayList<String[]> foo;

    /**
     * class used to define ViewHolder for use by RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public TextView mContent;
        public CardView mCard;

        /**Set up the  card to display the data and specify click listener to start new activity
         * @param itemView view
         */
        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.fieldName);
            mContent = itemView.findViewById(R.id.fieldContent);
            mCard = itemView.findViewById(R.id.dataCardView);

        }
    }

    /**Called by the layout manager to create each list item
     * @param parent parent context for inflating the layout from file
     * @param viewType viewType unused
     * @return
     */
    @NonNull
    @Override
    public CompanyDataRecyclerViewAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_display_cards, parent,  false);
        return new MyViewHolder(v);
    }

    /**Updates the data held in the view to reflect what is passed in to the data list
     * @param holder holds the view that has been inflated
     * @param position position the view will have in the UI
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(foo != null) {
            // set title and content text from array
            String[] field = foo.get(position);
            holder.mTitle.setText(field[TITLE]);
            holder.mContent.setText(field[DESCRIPTION]);
        }else{
            // set warning as no data present
            holder.mTitle.setText(R.string.emptyListWarning);
            holder.mContent.setText(R.string.emptyListWarning);
        }
    }

    /**
     * @return total items to be displayed by the adaptor
     */
    @Override
    public int getItemCount() {
        if(foo != null) {
            return foo.size();
        }else
        {
            return 0;
        }
    }

    /**Sets the company info to display in the view
     * @param company company selected to display
     */
    public void setCompanies(Company company){
        foo.clear();
        foo.add(new String[]{"Company ID", company.getCompanyId()});
        foo.add(new String[]{"Company Name", company.getTitle()});
        foo.add(new String[]{"Company Description", company.getDescription()});
        foo.add(new String[]{"Company Address", company.getAddressSnippet()});
        foo.add(new String[]{"Company Status", company.getCompanyStatus()});
        notifyDataSetChanged();
    }

    /**Sets the officer info for the current view
     * @param officer officer selected to display
     */
    public void setOfficers(Officer officer){
        foo.clear();
        foo.add(new String[]{"Officer ID", officer.getOfficerId()});
        foo.add(new String[]{"Name", officer.getName()});
        foo.add(new String[]{"Appointed on", officer.getAppointedOnDate()});
        foo.add(new String[]{"Nationality", officer.getNationality()});
        foo.add(new String[]{"Occupation", officer.getOccupation()});
        foo.add(new String[]{"Officer role", officer.getOfficerRole()});
        notifyDataSetChanged();
    }


}
