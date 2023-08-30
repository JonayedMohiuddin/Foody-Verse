package controllers;

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
}
