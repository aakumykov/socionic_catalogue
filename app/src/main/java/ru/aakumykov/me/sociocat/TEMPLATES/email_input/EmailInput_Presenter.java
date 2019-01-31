package ru.aakumykov.me.sociocat.TEMPLATES.email_input;

public class EmailInput_Presenter implements iEmailInput.Presenter {

    private iEmailInput.View view;
    
    
    // Системные методы
    @Override
    public void linkView(iEmailInput.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    // Интерфейсные методы
    @Override
    public void sendRegistrationEmail(String email) {

    }

    

}
