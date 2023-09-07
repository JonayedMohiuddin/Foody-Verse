package client;

import dto.ClientLoginRequestDTO;
import dto.LoginResponseDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class ClientLoginController
{
    public PasswordField userLoginPasswordField;
    public TextField userLoginUsernameField;
    public Label loginMessageText;

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
        loginMessageText.setText("");
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
        ClientLoginRequestDTO loginRequestDTO = new ClientLoginRequestDTO(name, password);
        try
        {
            application.getSocketWrapper().write(loginRequestDTO);
        }
        catch (IOException e)
        {
            System.err.println("Class : LoginController | Method : userLoginLoginButtonPressed | While sending login request to server");
            System.err.println("Error : " + e.getMessage());
        }

        try
        {
            Object obj = application.getSocketWrapper().read();
            if(obj instanceof LoginResponseDTO)
            {
                LoginResponseDTO loginResponseDTO = (LoginResponseDTO) obj;
                if(loginResponseDTO.getStatus())
                {
                    System.out.println("Login Successful");
                    System.out.println(loginResponseDTO.getMessage());
                    loginMessageText.setText(loginResponseDTO.getMessage());
                    loginMessageText.setStyle("-fx-text-fill: green");
                }
                else
                {
                    System.out.println("Login Failed");
                    System.out.println(loginResponseDTO.getMessage());
                    loginMessageText.setText(loginResponseDTO.getMessage());
                    loginMessageText.setStyle("-fx-text-fill: red");
                }
            }
            else
            {
                System.err.println("Expected LoginResponseDTO but got something else");
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.out.println("Class : LoginController | Method : userLoginLoginButtonPressed | While reading login response from server");
            System.out.println("Error : " + e.getMessage());
        }
    }

    public void userLoginResetButtonPressed(ActionEvent actionEvent)
    {
        userLoginUsernameField.setText("");
        userLoginPasswordField.setText("");
    }
}
