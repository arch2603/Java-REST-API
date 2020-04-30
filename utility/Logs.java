package utility;

import java.util.logging.*;

public class Logs {
    static private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //static private FileHandler fileHandler;
    static private SimpleFormatter simpleFortxt;
    static private  FileHandler fileHandler;

    public void setup(FileHandler fh)
    {
        this.fileHandler = fh;
    }


    public void logSuccess(String msg)
    {
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);
            //fileHandler = new FileHandler(filename, true);
            simpleFortxt = new SimpleFormatter();
            fileHandler.setFormatter(simpleFortxt);
            logger.addHandler(fileHandler);
            logger.info(msg);
    }
    public void logErrors(String msg)
    {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.SEVERE);
        fileHandler.setFormatter(simpleFortxt);
        logger.addHandler(fileHandler);
        logger.info(msg);
    }

    public void logMessages(String cases, String msg)
    {
        switch(cases)
        {
            case LogCases.LOG_MESSAGE:
                logger.setUseParentHandlers(false);
                logger.setLevel(Level.INFO);
                //fileHandler = new FileHandler(filename, true);
                simpleFortxt = new SimpleFormatter();
                fileHandler.setFormatter(simpleFortxt);
                logger.addHandler(fileHandler);
                logger.info(msg);
                break;
            case LogCases.LOG_MESSAGE_SEVERE:
                break;

            case LogCases.LOG_MESSAGE_WARNING:
                break;

            case LogCases.LOG_MESSAGE_EXCEPTION:
                logger.setUseParentHandlers(false);
                logger.setLevel(Level.SEVERE);
                fileHandler.setFormatter(simpleFortxt);
                logger.addHandler(fileHandler);
                logger.info(msg);
        }
    }

}
