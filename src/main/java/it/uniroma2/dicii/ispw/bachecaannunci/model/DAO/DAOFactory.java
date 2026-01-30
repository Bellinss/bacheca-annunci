package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

// Import delle Interfacce
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.*;

// Import delle implementazioni File System
import it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem.*;

// Import delle implementazioni MySQL
import it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL.*;

// Import della Configurazione
import it.uniroma2.dicii.ispw.bachecaannunci.model.inmemory.*;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

public class DAOFactory {

    // Campi statici per mantenere le istanze (Singleton Pattern per la Factory)
    private static AdDAO adDaoInstance = null;
    private static CategoryDAO categoryDaoInstance = null;
    private static CommentDAO commentDaoInstance = null;
    private static MessageDAO messageDaoInstance = null;
    private static NoteDAO noteDaoInstance = null;
    private static NotificationDAO notificationDaoInstance = null;
    private static ReportDAO reportDaoInstance = null;
    private static UserDAO userDaoInstance = null;

    private DAOFactory() {}

    // ------------------------------------------------------------------------
    // USER DAO (Login & Registrazione)
    // ------------------------------------------------------------------------
    public static UserDAO getUserDAO() {
        if (userDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    userDaoInstance = UserDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    userDaoInstance = new UserDAOFileSystem();
                    break;
                case IN_MEMORY:
                    userDaoInstance = new UserDAOInMemory();
                    break;
            }
        }
        return userDaoInstance;
    }

    // ------------------------------------------------------------------------
    // AD DAO (Annunci)
    // ------------------------------------------------------------------------
    public static AdDAO getAdDAO() {
        if (adDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    adDaoInstance = AdDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    adDaoInstance = new AdDAOFileSystem();
                    break;
                case IN_MEMORY:
                    adDaoInstance = new AdDAOInMemory();
                    break;
            }
        }
        return adDaoInstance;
    }

    // ------------------------------------------------------------------------
    // CATEGORY DAO
    // ------------------------------------------------------------------------
    public static CategoryDAO getCategoryDAO() {
        if (categoryDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    categoryDaoInstance = CategoryDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    categoryDaoInstance = new CategoryDAOFileSystem();
                    break;
                case IN_MEMORY:
                    categoryDaoInstance = new CategoryDAOInMemory();
                    break;
            }
        }
        return categoryDaoInstance;
    }

    // ------------------------------------------------------------------------
    // COMMENT DAO
    // ------------------------------------------------------------------------
    public static CommentDAO getCommentDAO() {
        if (commentDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    commentDaoInstance = CommentDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    commentDaoInstance = new CommentDAOFileSystem();
                    break;
                case IN_MEMORY:
                    commentDaoInstance = new CommentDAOInMemory();
                    break;
            }
        }
        return commentDaoInstance;
    }

    // ------------------------------------------------------------------------
    // MESSAGE DAO
    // ------------------------------------------------------------------------
    public static MessageDAO getMessageDAO() {
        if (messageDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    messageDaoInstance = MessageDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    messageDaoInstance = new MessageDAOFileSystem();
                    break;
                case IN_MEMORY:
                    messageDaoInstance = new MessageDAOInMemory();
                    break;
            }
        }
        return messageDaoInstance;
    }

    // ------------------------------------------------------------------------
    // NOTE DAO
    // ------------------------------------------------------------------------
    public static NoteDAO getNoteDAO() {
        if (noteDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    noteDaoInstance = NoteDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    noteDaoInstance = new NoteDAOFileSystem();
                    break;
                case IN_MEMORY:
                    noteDaoInstance = new NoteDAOInMemory();
                    break;
            }
        }
        return noteDaoInstance;
    }

    // ------------------------------------------------------------------------
    // NOTIFICATION DAO
    // ------------------------------------------------------------------------
    public static NotificationDAO getNotificationDAO() {
        if (notificationDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    notificationDaoInstance = NotificationDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    notificationDaoInstance = new NotificationDAOFileSystem();
                    break;
                case IN_MEMORY:
                    notificationDaoInstance = new NotificationDAOInMemory();
                    break;
            }
        }
        return notificationDaoInstance;
    }

    // ------------------------------------------------------------------------
    // REPORT DAO
    // ------------------------------------------------------------------------
    public static ReportDAO getReportDAO() {
        if (reportDaoInstance == null) {
            switch (Config.mode) {
                case MYSQL:
                    reportDaoInstance = ReportDAOMySQL.getInstance();
                    break;
                case FILE_SYSTEM:
                    reportDaoInstance = new ReportDAOFileSystem();
                    break;
                case IN_MEMORY:
                    reportDaoInstance = new ReportDAOInMemory();
                    break;
            }
        }
        return reportDaoInstance;
    }
}