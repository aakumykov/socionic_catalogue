package ru.aakumykov.me.mvp.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.mvp.R;


public class TagAutocompleteAdapter2 extends ArrayAdapter<String> {

    public static final String TAG = "TagAutocompleteAdapter2";
    private int viewResourceId;
    private List<String> tagsList;
    private List<String> allTags;
    private List<String> suggestedTags;

    public TagAutocompleteAdapter2(Context context, int viewResourceId, List<String> list) {
        super(context, viewResourceId, list);
        this.viewResourceId = viewResourceId;
        this.tagsList = list;
        this.allTags = new ArrayList<>();
        this.allTags.addAll(this.tagsList);
        this.suggestedTags = new ArrayList<>();
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }

        String tagName = tagsList.get(position);
        if (tagName != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.titleView);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView String Name:"+customer.getName());
                customerNameLabel.setText(tagName);
            }
        }

        return v;
    }


    @Override
    public Filter getFilter() {
        return tagFilter;
    }


    Filter tagFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint != null) {
                suggestedTags.clear();

                for (String tagName : allTags) {
                    if(tagName.toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestedTags.add(tagName);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestedTags;
                filterResults.count = suggestedTags.size();
                return filterResults;
            }
            else {
                return new FilterResults();
            }
        }

        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((String)(resultValue));
            return str;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<String> filteredList = (ArrayList<String>) results.values;

            if (results != null && results.count > 0) {
                clear();
                for (String c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }

    };

}