package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums;



public enum eSortingOrder {

    DIRECT {
        @Override
        public eSortingOrder reverse() {
            return REVERSE;
        }

        @Override
        public boolean isDirect() {
            return true;
        }
    },

    REVERSE {
        @Override
        public eSortingOrder reverse() {
            return DIRECT;
        }

        @Override
        public boolean isDirect() {
            return false;
        }
    };


    public abstract eSortingOrder reverse();
    public abstract boolean isDirect();
}
