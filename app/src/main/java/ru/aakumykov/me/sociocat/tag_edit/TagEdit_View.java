package ru.aakumykov.me.sociocat.tag_edit;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.tag_edit.view_model.TagEdit_ViewModel;
import ru.aakumykov.me.sociocat.tag_edit.view_model.TagEdit_ViewModelFactory;

public class TagEdit_View extends BaseView
        implements iTagEdit_View, LifecycleOwner
{
    @BindView(R.id.tagNameInput) TextInputEditText tagNameInput;
    @BindView(R.id.saveButton) Button saveButton;

    private TagEdit_ViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();

        mViewModel = new ViewModelProvider(this, new TagEdit_ViewModelFactory())
                .get(TagEdit_ViewModel.class);

        getLifecycle().addObserver(mViewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.unbindView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

}
