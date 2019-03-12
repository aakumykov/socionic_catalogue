package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;

public class TagAutocompleteAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private int layout;
    private List<String> list;
    private List<String> allTags = new ArrayList<>();
    private List<String> suggestedTags = new ArrayList<>();

    public TagAutocompleteAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);
        this.list = list;
        this.allTags.addAll(this.list);
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    // Разметочная машина
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String element = list.get(position);

        viewHolder.titleView.setText(element);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.titleView) TextView titleView;
        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    // Фильтровальная станция
    @NonNull @Override
    public Filter getFilter() {
        return tagFilter;
    }

    private Filter tagFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence pattern) {

            if (pattern != null) {
                suggestedTags.clear();
                pattern = pattern.toString().toLowerCase();

                for (String tag : allTags) {
                    tag = tag.toLowerCase();
                    String[] tagPieces = tag.split("\\s+");

                    for (String word : tagPieces) {
                        if (word.startsWith(pattern.toString())) {
                            suggestedTags.add(tag);
                            break;
                        }
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