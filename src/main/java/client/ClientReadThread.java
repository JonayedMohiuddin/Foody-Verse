package client;

import dto.*;
import javafx.application.Platform;
import server.SocketWrapper;

import java.io.IOException;

public class ClientReadThread implements Runnable
{
    Thread thread;
    SocketWrapper socketWrapper;
    ClientHomeController clientHomeController;

    ClientReadThread(ClientHomeController clientHomeController, String threadName)
    {
        this.clientHomeController = clientHomeController;
        this.socketWrapper = clientHomeController.application.getSocketWrapper();
        thread = new Thread(this, threadName);
        thread.start();
    }

    public void run()
    {
        try
        {
            while (true)
            {
                Object obj = socketWrapper.read();

                if (obj instanceof FoodAddRequestDTO foodAddRequestDTO)
                {
                    System.out.println("New Food Added request");
                    System.out.println(foodAddRequestDTO);


                    clientHomeController.application.getRestaurantList().get(foodAddRequestDTO.getFood().getRestaurantId()).getFoodList().add(foodAddRequestDTO.getFood());
                    clientHomeController.application.getFoodList().add(foodAddRequestDTO.getFood());

//                    clientHomeController.newFoodAdded(foodAddRequestDTO.getFood());
                    Platform.runLater(() -> clientHomeController.newFoodAdded(foodAddRequestDTO.getFood()));
                }
                else if (obj instanceof RestaurantAddUpdateDTO restaurantAddUpdateDTO)
                {
                    System.out.println("New Restaurant Add request");
                    System.out.println(restaurantAddUpdateDTO);

//                    clientHomeController.newRestaurantAdded(restaurantAddUpdateDTO.getRestaurant());
                    Platform.runLater(() -> clientHomeController.newRestaurantAdded(restaurantAddUpdateDTO.getRestaurant()));
                }
                else if(obj instanceof DeliverDTO deliverDTO)
                {
                    System.out.println("New delivery received,");
                    System.out.println();

                    Platform.runLater(() -> { clientHomeController.newDelivery(deliverDTO); });
                }
                else if (obj instanceof NewReviewRequest newReviewRequest)
                {
                    System.out.println("New review received,");
                    System.out.println();

                    Platform.runLater(() -> { clientHomeController.newReview(newReviewRequest.getReview()); });
                }
                else if (obj instanceof LogoutDTO logoutDTO)
                {
                    break;
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("Class : ClientReadThread | Method : run | While reading from server");
            System.out.println(e.getMessage());
            System.out.println("CLOSING CONNECTION WITH SERVER");
        }
        finally
        {
            if(!socketWrapper.isClosed())
            {
                try
                {
                    socketWrapper.closeConnection();
                }
                catch (IOException e)
                {
                    System.out.println("Class : ClienReadThread | Method : run | Couldn't clost socket connection properly.");
                }
            }
        }
    }
}