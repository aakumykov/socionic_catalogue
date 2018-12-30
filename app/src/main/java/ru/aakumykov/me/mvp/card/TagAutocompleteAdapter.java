package ru.aakumykov.me.mvp.card;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.R;

public class TagAutocompleteAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;
    private int layout;
    private List<String> list;

    public TagAutocompleteAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);
        this.list = list;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

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
}