package util;

import javafx.animation.ScaleTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class ImageTransitions
{
    public static void imageMouseHoverEntered(MouseEvent event)
    {
        ScaleTransition iconButtonsZoomInOutTransition = new ScaleTransition(Duration.millis(200), (ImageView)event.getTarget());
        iconButtonsZoomInOutTransition.setFromX(1);  // Initial scale
        iconButtonsZoomInOutTransition.setFromY(1);
        iconButtonsZoomInOutTransition.setToX(1.2);   // Scale to 120% on hover
        iconButtonsZoomInOutTransition.setToY(1.2);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.15);
        ((ImageView) event.getTarget()).setEffect(colorAdjust);

        iconButtonsZoomInOutTransition.setRate(1.0);
        iconButtonsZoomInOutTransition.play();
    }

    public static void imageMouseHoverExited(MouseEvent event)
    {
        ScaleTransition iconButtonsZoomInOutTransition = new ScaleTransition(Duration.millis(200), (ImageView)event.getTarget());
        iconButtonsZoomInOutTransition.setFromX(1);  // Initial scale
        iconButtonsZoomInOutTransition.setFromY(1);
        iconButtonsZoomInOutTransition.setToX(1.2);   // Scale to 120% on hover
        iconButtonsZoomInOutTransition.setToY(1.2);

        ((ImageView) event.getTarget()).setEffect(null);

        iconButtonsZoomInOutTransition.setRate(-1.0);
        iconButtonsZoomInOutTransition.play();
    }

    public static void imageMouseHoverEntered(MouseEvent event, double zoomFactor, double brightnessFactor)
    {
        ScaleTransition iconButtonsZoomInOutTransition = new ScaleTransition(Duration.millis(200), (ImageView)event.getTarget());
        iconButtonsZoomInOutTransition.setFromX(1);  // Initial scale
        iconButtonsZoomInOutTransition.setFromY(1);
        iconButtonsZoomInOutTransition.setToX(zoomFactor);   // Scale to 120% on hover
        iconButtonsZoomInOutTransition.setToY(zoomFactor);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-brightnessFactor);
        ((ImageView) event.getTarget()).setEffect(colorAdjust);

        iconButtonsZoomInOutTransition.setRate(1.0);
        iconButtonsZoomInOutTransition.play();
    }

    public static void imageMouseHoverExited(MouseEvent event, double zoomFactor, double brightnessFactor)
    {
        ScaleTransition iconButtonsZoomInOutTransition = new ScaleTransition(Duration.millis(200), (ImageView)event.getTarget());
        iconButtonsZoomInOutTransition.setFromX(1);  // Initial scale
        iconButtonsZoomInOutTransition.setFromY(1);
        iconButtonsZoomInOutTransition.setToX(zoomFactor);   // Scale to 120% on hover
        iconButtonsZoomInOutTransition.setToY(zoomFactor);

        ((ImageView) event.getTarget()).setEffect(null);

        iconButtonsZoomInOutTransition.setRate(-1.0);
        iconButtonsZoomInOutTransition.play();
    }
}
