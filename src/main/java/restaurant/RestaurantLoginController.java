package restaurant;

import dto.LoginResponseDTO;
import dto.RestaurantLoginRequestDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class RestaurantLoginController
{

    public TextField restaurantLoginUsernameField;
    public Button restaurantLoginRegisterButton;
    public PasswordField restaurantLoginPasswordField;
    public Label loginMessageText;
    @FXML
    public ImageView loginBGImage;
    private RestaurantApplication application;

    public void setApplication(RestaurantApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        loginBGImage.fitWidthProperty().bind(application.getStage().widthProperty());
        loginBGImage.fitHeightProperty().bind(application.getStage().heightProperty());
        loginMessageText.setText("");
    }

    public void loginButtonPressed(ActionEvent actionEvent)
    {
        System.out.println("Login Button Pressed");
        String name = restaurantLoginUsernameField.getText();
        String password = restaurantLoginPasswordField.getText();
        System.out.println("Name : " + name + " Password : " + password);
        RestaurantLoginRequestDTO restaurantLoginRequestDTO = new RestaurantLoginRequestDTO(name, password);
        try
        {
            application.getSocketWrapper().write(restaurantLoginRequestDTO);
        }
        catch (IOException e)
        {
            System.err.println("Class : LoginController | Method : restaurantLoginLoginButtonPressed");
            System.err.println("Error : " + e.getMessage());
        }

        try
        {
            Object obj = application.getSocketWrapper().read();
            if (obj instanceof LoginResponseDTO)
            {
                LoginResponseDTO loginResponseDTO = (LoginResponseDTO) obj;
                if (loginResponseDTO.getStatus())
                {
                    System.out.println("Login Successful.");
                    System.out.println(loginResponseDTO.getMessage());
                    loginMessageText.setText(loginResponseDTO.getMessage());
                    loginMessageText.setStyle("-fx-text-fill: green");

                    application.username = name;
                    application.showHomePage();
                }
                else
                {
                    System.out.println("Login failed.");
                    System.out.println(loginResponseDTO.getMessage());
                    loginMessageText.setText(loginResponseDTO.getMessage());
                    loginMessageText.setStyle("-fx-text-fill: red");
                }
            }
            else
            {
                System.out.println("Expected LoginResponseDTO but got something else.");
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.err.println("Class : LoginController | Method : restaurantLoginLoginButtonPressed | While reading login response from server");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void resetButtonPressed(ActionEvent actionEvent)
    {
        restaurantLoginUsernameField.setText("");
        restaurantLoginPasswordField.setText("");
    }

    public void registerButtonPressed(ActionEvent actionEvent)
    {
        System.out.println("Register Button Pressed");
    }
}
