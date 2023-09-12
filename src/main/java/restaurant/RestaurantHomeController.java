package restaurant;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class RestaurantHomeController
{
    public Label restaurantName;
    public Label flowpaneTitleLabel;
    public FlowPane flowpane;

    private RestaurantApplication application;
    public void setApplication(RestaurantApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        restaurantName.setText(application.username);

        new RestaurantReadThread(application);
    }

    public void logoutButtonClicked(ActionEvent event)
    {
    }
}
