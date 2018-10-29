package ru.aakumykov.me.mvp.tags.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Tag;

public class TagsListAdapter extends ArrayAdapter<Tag> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "TagsListAdapter";
    private LayoutInflater inflater;
    private int layout;
    private List<Tag> tagsList;

    TagsListAdapter(Context context, int resource, List<Tag> tags) {
        super(context, resource, tags);
        this.tagsList = tags;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);
        TextView nameView = view.findViewById(R.id.nameView);
        TextView counterView = view.findViewById(R.id.counterView);

        Tag oneTag = tagsList.get(position);
        Log.d(TAG, "oneTag: "+oneTag);

        nameView.setText(oneTag.getName());
        counterView.setText(oneTag.getCounter());

        return view;
    }
}