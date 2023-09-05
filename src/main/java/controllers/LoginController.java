package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class LoginController
{
    private MainApplication application;

    @FXML
    public ImageView loginBGImage;

    public void setApplication(MainApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        loginBGImage.fitWidthProperty().bind(application.getStage().widthProperty());
        loginBGImage.fitHeightProperty().bind(application.getStage().heightProperty());
    }

    public void userLoginRegisterButtonPressed(ActionEvent actionEvent)
    {
    }

    public void userLoginLoginButtonPressed(ActionEvent actionEvent)
    {
    }

    public void userLoginResetButtonPressed(ActionEvent actionEvent)
    {
    }
}
