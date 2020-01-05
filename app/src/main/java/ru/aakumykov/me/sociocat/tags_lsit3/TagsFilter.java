package ru.aakumykov.me.sociocat.tags_lsit3;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.models.Tag;

public class TagsFilter extends Filter {

    private iTagsList3.iPresenter presenter;
    private List<Tag> originalTagsList = new ArrayList<>();


    public TagsFilter(List<Tag> tagsList, iTagsList3.iPresenter presenter) {
        this.presenter = presenter;
        this.originalTagsList.addAll(tagsList);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults filterResults = new FilterResults();

        if (null != constraint && constraint.length() > 0)
        {
            List<Tag> filteredTagsList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();

            for (Tag tag : originalTagsList) {
                String tagName = tag.getName().toLowerCase();
                if (tagName.contains(query))
                    filteredTagsList.add(tag);
            }

            filterResults.count = filteredTagsList.size();
            filterResults.values = filteredTagsList;
        }
        else
        {
            filterResults.count = originalTagsList.size();
            filterResults.values = originalTagsList;
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        presenter.onListFiltered(constraint, (ArrayList<Tag>) results.values);
    }
}
