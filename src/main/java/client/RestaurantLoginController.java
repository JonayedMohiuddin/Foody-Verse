package client;

import dto.ClientLoginRequestDTO;
import dto.RestaurantLoginRequestDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class RestaurantLoginController
{

    public TextField restaurantLoginUsernameField;
    public Button restaurantLoginRegisterButton;
    public PasswordField restaurantLoginPasswordField;
    private RestaurantApplication application;

    @FXML
    public ImageView loginBGImage;

    public void setApplication(RestaurantApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        loginBGImage.fitWidthProperty().bind(application.getStage().widthProperty());
        loginBGImage.fitHeightProperty().bind(application.getStage().heightProperty());
    }

    public void restaurantLoginRegisterButtonPressed(ActionEvent actionEvent)
    {
    }

    public void restaurantLoginLoginButtonPressed(ActionEvent actionEvent)
    {
        System.out.println("Login Button Pressed");
        String name = restaurantLoginUsernameField.getText();
        String password = restaurantLoginPasswordField.getText();
        System.out.println("Name : " + name + " Password : " + password);
        RestaurantLoginRequestDTO loginRequestDTO = new RestaurantLoginRequestDTO(name, password);
        try
        {
            application.getSocketWrapper().write(loginRequestDTO);
        }
        catch (IOException e)
        {
            System.err.println("Class : LoginController | Method : restaurantLoginLoginButtonPressed");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void restaurantLoginResetButtonPressed(ActionEvent actionEvent)
    {
        restaurantLoginUsernameField.setText("");
        restaurantLoginPasswordField.setText("");
    }
}
