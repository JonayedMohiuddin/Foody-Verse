package client;

import client.ClientApplication;
import dto.LoginRequestDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class LoginController
{
    public PasswordField userLoginPasswordField;
    public TextField userLoginUsernameField;

    private ClientApplication application;

    @FXML
    public ImageView loginBGImage;

    public void setApplication(ClientApplication application)
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
        System.out.println("Login Button Pressed");
        String name = userLoginUsernameField.getText();
        String password = userLoginPasswordField.getText();
        System.out.println("Name : " + name + " Password : " + password);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(name, password);
        try
        {
            application.getSocketWrapper().write(loginRequestDTO);
        }
        catch (IOException e)
        {
            System.err.println("Class : LoginController | Method : userLoginLoginButtonPressed");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void userLoginResetButtonPressed(ActionEvent actionEvent)
    {
        userLoginUsernameField.setText("");
        userLoginPasswordField.setText("");
    }
}
