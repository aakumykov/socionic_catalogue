package ru.aakumykov.me.sociocat.a_basic_mvp_components;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_model.Basic_ViewModel;


public class MVPUtils {

    public interface iRecyclerViewConfigurationListener {
        void onRecyclerViewAssembled();
    }

    public static void configureRecyclerview(
            RecyclerView recyclerView,
            Basic_DataAdapter dataAdapter,
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

    public static Basic_Presenter prepPresenter(Basic_ViewModel viewModel, iPresenterPreparationCallback callback) {
        if (viewModel.hasPresenter()) {
            return (Basic_Presenter) viewModel.getPresenter();
        }
        else {
            Basic_Presenter presenter = callback.onPresenterPrepared();
            viewModel.setPresenter(presenter);
            return presenter;
        }
    }

    public static Basic_DataAdapter prepDataAdapter(Basic_ViewModel viewModel, iDataAdapterPreparationCallback callback) {
        if (viewModel.hasDataAdapter()) {
            return (Basic_DataAdapter) viewModel.getDataAdapter();
        }
        else {
            Basic_DataAdapter dataAdapter = callback.onDataAdapterPrepared();
            viewModel.setDataAdapter(dataAdapter);
            return dataAdapter;
        }
    }
}
