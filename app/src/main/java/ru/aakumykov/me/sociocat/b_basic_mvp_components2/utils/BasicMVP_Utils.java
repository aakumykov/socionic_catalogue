package ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_model.BasicMVP_ViewModel;

public class    BasicMVP_Utils {

    public static void configureRecyclerview(
            RecyclerView recyclerView,
            BasicMVP_DataAdapter dataAdapter,
            RecyclerView.LayoutManager layoutManager,
            @Nullable RecyclerView.ItemDecoration itemDecoration,
            @Nullable iRecyclerViewConfigurationListener recyclerViewConfigurationListener
    ) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(layoutManager);

        if (null != itemDecoration)
            recyclerView.addItemDecoration(itemDecoration);

        if (null != recyclerViewConfigurationListener)
            recyclerViewConfigurationListener.onRecyclerViewAssembled();
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

    public interface iRecyclerViewConfigurationListener {
        void onRecyclerViewAssembled();
    }
}
