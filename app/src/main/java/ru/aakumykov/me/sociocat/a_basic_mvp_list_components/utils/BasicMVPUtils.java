package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_model.BasicMVP_ViewModel;

public class BasicMVPUtils {

    public static void configureRecyclerview(
            RecyclerView recyclerView,
            BasicMVP_DataAdapter dataAdapter,
            RecyclerView.LayoutManager layoutManager,
            @Nullable RecyclerView.ItemDecoration itemDecoration
    ) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(layoutManager);

        clearItemDecorations(recyclerView);

        if (null != itemDecoration)
            recyclerView.addItemDecoration(itemDecoration);
    }

    public static BasicMVP_Presenter prepPresenter(BasicMVP_ViewModel viewModel, iPresenterPreparationCallback callback) {
        if (viewModel.hasPresenter()) {
            return (BasicMVP_Presenter) viewModel.getPresenter();
        }
        else {
            BasicMVP_Presenter presenter = callback.onPresenterPrepared();
            viewModel.setPresenter(presenter);
            return presenter;
        }
    }

    public static BasicMVP_DataAdapter prepDataAdapter(BasicMVP_ViewModel viewModel, iDataAdapterPreparationCallback callback) {
        if (viewModel.hasDataAdapter()) {
            return (BasicMVP_DataAdapter) viewModel.getDataAdapter();
        }
        else {
            BasicMVP_DataAdapter dataAdapter = callback.onDataAdapterPrepared();
            viewModel.setDataAdapter(dataAdapter);
            return dataAdapter;
        }
    }

    private static void clearItemDecorations(RecyclerView recyclerView) {
        for (int i=0; i<recyclerView.getItemDecorationCount(); i++)
            recyclerView.removeItemDecorationAt(i);
    }

}
