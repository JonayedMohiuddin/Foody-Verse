package client;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ClientCartController
{
    public Label usernameLabel;
    public ListView cartItemListView;

    private ClientApplication application;
    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        usernameLabel.setText(application.getUsername());
    }

    public void backButtonClicked(ActionEvent event)
    {
    }
}
