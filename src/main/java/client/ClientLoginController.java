package client;

import dto.ClientLoginRequestDTO;
import dto.LoginResponseDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ClientLoginController
{
    public TextField usernameTextField;
    public PasswordField passwordTextField;
    public Label signInMessageText;
    public Label signUpLabel;

    private ClientApplication application;

    @FXML
    public ImageView loginBGImage;

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        signInMessageText.setText("");
        signUpLabel.setStyle("-fx-font-family: Corbel; -fx-font-weight: 16px; -fx-text-fill: #0d00ff; -fx-underline: false;");

        signUpLabel.setOnMouseClicked(event -> {
            signUpButtonClicked();
        });
    }

    public void signInButtonClicked(ActionEvent event)
    {
        System.out.println("Login Button Pressed");
        String name = usernameTextField.getText();
        String password = passwordTextField.getText();
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
            if (obj instanceof LoginResponseDTO loginResponseDTO)
            {
                if (loginResponseDTO.getStatus())
                {
                    System.out.println("Login Successful");
                    System.out.println(loginResponseDTO.getMessage());
                    signInMessageText.setText(loginResponseDTO.getMessage());
                    signInMessageText.setStyle("-fx-text-fill: green");

                    application.setUserName(name);
                    application.showHomePage();
                }
                else
                {
                    System.out.println("Login Failed");
                    System.out.println(loginResponseDTO.getMessage());
                    signInMessageText.setText(loginResponseDTO.getMessage());
                    signInMessageText.setStyle("-fx-text-fill: red");
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

    public void signUpButtonClicked()
    {
        System.out.println("Sign Up Button Pressed");
        try
        {
            application.showSignUpPage();
        }
        catch (IOException e)
        {
            System.err.println("Class : LoginController | Method : userLoginSignupButtonPressed | While showing signup page");
            System.err.println("Error : " + e.getMessage());
        }
    }

    public void mouseHoveringEntered(MouseEvent event)
    {
        signUpLabel.setStyle("-fx-font-family: Corbel; -fx-font-weight: 18px; -fx-text-fill: #00137a; -fx-underline: true;");
    }

    public void mouseHoveringExited(MouseEvent event)
    {
        signUpLabel.setStyle("-fx-font-family: Corbel; -fx-font-weight: 16px; -fx-text-fill: #0d00ff; -fx-underline: false;");
    }
}
