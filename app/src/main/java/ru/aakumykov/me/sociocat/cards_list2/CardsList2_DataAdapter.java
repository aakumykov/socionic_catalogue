package ru.aakumykov.me.sociocat.cards_list2;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewHolderBinder;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewHolderCreator;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewTypeDetector;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsList2_DataAdapter extends ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter {

    public CardsList2_DataAdapter(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }


    public int updateCardInList(@NonNull Card oldCard, @NonNull Card newCard) {
        return 0;
    }


    @Override
    protected BasicMVP_ViewHolderCreator prepareViewHolderCreator() {
        return new CardsList2_ViewHolderCreator(mItemClickListener);
    }

    @Override
    protected BasicMVP_ViewHolderBinder prepareViewHolderBinder() {
        return new CardsList2_ViewHolderBinder();
    }

    @Override
    protected BasicMVP_ViewTypeDetector prepareViewTypeDetector() {
        return new CardsList2_ViewTypeDetector();
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return null;
    }


}
