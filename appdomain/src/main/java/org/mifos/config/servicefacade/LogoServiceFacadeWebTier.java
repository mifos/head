package org.mifos.config.servicefacade;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.mifos.framework.util.ConfigurationLocator;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class LogoServiceFacadeWebTier implements LogoServiceFacade {
    public static final String LOGO_DIRECTORY = "logo";
    public static final float MAX_WIDTH = 200;
    public static final float MAX_HEIGHT = 70;

    public void uploadNewLogo(CommonsMultipartFile logo) throws IOException { 
        BufferedImage bufferedImage = ImageIO.read(logo.getInputStream());
        BufferedImage finalImage = null;
        if (bufferedImage.getWidth() > MAX_WIDTH || bufferedImage.getHeight() > MAX_HEIGHT) {
            float wRatio, hRatio;
            if (bufferedImage.getWidth() >= bufferedImage.getHeight()) {
                wRatio = MAX_WIDTH / bufferedImage.getWidth();
                hRatio = MAX_HEIGHT / bufferedImage.getHeight();
            } else {
                wRatio = MAX_HEIGHT / bufferedImage.getWidth();
                hRatio = MAX_WIDTH / bufferedImage.getHeight();
            }
            float resizeRatio = Math.min(wRatio, hRatio);
            float newHeight = bufferedImage.getHeight() * resizeRatio;
            float newWidth = bufferedImage.getWidth() * resizeRatio;
            finalImage = new BufferedImage((int) newWidth, (int) newHeight, bufferedImage.getType());  
            Graphics2D g = finalImage.createGraphics();  
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
            g.drawImage(bufferedImage, 0, 0, (int) newWidth, (int) newHeight, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);  
            g.dispose();  
        } else {
            finalImage = bufferedImage;
        }
        ConfigurationLocator configurationLocator = new ConfigurationLocator();
        File dir = new File(configurationLocator.getConfigurationDirectory() + File.separator + configurationLocator.getLogoDirectory() + File.separator);
        dir.mkdirs();
        File file = new File(dir, configurationLocator.getLogoName());
        ImageIO.write(finalImage, "png", file);
    }

}
