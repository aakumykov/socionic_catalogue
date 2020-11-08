package ru.aakumykov.me.sociocat.a_basic_mvp_components.utils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_model.Basic_ViewModel;


public class BasicMVP_Utils {

    public interface iRecyclerViewConfigurationListener {
        void onRecyclerViewAssembled();
    }

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

    public static BasicMVP_Presenter prepPresenter(Basic_ViewModel viewModel, iPresenterPreparationCallback callback) {
        if (viewModel.hasPresenter()) {
            return (BasicMVP_Presenter) viewModel.getPresenter();
        }
        else {
            BasicMVP_Presenter presenter = callback.onPresenterPrepared();
            viewModel.setPresenter(presenter);
            return presenter;
        }
    }

    public static BasicMVP_DataAdapter prepDataAdapter(Basic_ViewModel viewModel, iDataAdapterPreparationCallback callback) {
        if (viewModel.hasDataAdapter()) {
            return (BasicMVP_DataAdapter) viewModel.getDataAdapter();
        }
        else {
            BasicMVP_DataAdapter dataAdapter = callback.onDataAdapterPrepared();
            viewModel.setDataAdapter(dataAdapter);
            return dataAdapter;
        }
    }
}
