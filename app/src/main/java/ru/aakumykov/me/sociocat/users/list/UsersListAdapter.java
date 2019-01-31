package ru.aakumykov.me.sociocat.users.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.models.User;

public class UsersListAdapter extends ArrayAdapter<User> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "UsersListAdapter";
    private LayoutInflater inflater;
    private int layout;
    private List<User> users;

    UsersListAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.users = users;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);
        TextView nameView = view.findViewById(R.id.nameView);

        User user = users.get(position);
        nameView.setText(user.getName());

        return view;
    }
}