package it.uniroma2.dicii.ispw.bachecaannunci.model.DAO;

// Import delle Interfacce
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.AdDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CategoryDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.CommentDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.MessageDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.NoteDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.NotificationDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.ReportDAO;
import it.uniroma2.dicii.ispw.bachecaannunci.model.DAO.UserDAO;

// Import delle implementazioni File System (Demo)
import it.uniroma2.dicii.ispw.bachecaannunci.model.filesystem.*;

// Import delle implementazioni MySQL (Full)
import it.uniroma2.dicii.ispw.bachecaannunci.model.MySQL.*;
import it.uniroma2.dicii.ispw.bachecaannunci.utils.Config;

public class DAOFactory {

    // Campi statici per mantenere le istanze (Singleton Pattern)
    private static AdDAO adDaoInstance = null;
    private static CategoryDAO categoryDaoInstance = null;
    private static CommentDAO commentDaoInstance = null;
    private static MessageDAO messageDaoInstance = null;
    private static NoteDAO noteDaoInstance = null;
    private static NotificationDAO notificationDaoInstance = null;
    private static ReportDAO reportDaoInstance = null;
    private static UserDAO userDaoInstance = null;

    private DAOFactory() {}

    // --- AD DAO (Annunci) ---
    public static AdDAO getAdDAO() {
        if (adDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                adDaoInstance = new AdDAOFileSystem();
            } else {
                adDaoInstance = AdDAOMySQL.getInstance();
            }
        }
        return adDaoInstance;
    }

    // --- CATEGORY DAO (Categorie) ---
    public static CategoryDAO getCategoryDAO() {
        if (categoryDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                categoryDaoInstance = new CategoryDAOFileSystem();
            } else {
                categoryDaoInstance = CategoryDAOMySQL.getInstance();
            }
        }
        return categoryDaoInstance;
    }

    // --- COMMENT DAO (Commenti) ---
    public static CommentDAO getCommentDAO() {
        if (commentDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                commentDaoInstance = new CommentDAOFileSystem();
            } else {
                commentDaoInstance = CommentDAOMySQL.getInstance();
            }
        }
        return commentDaoInstance;
    }

    // --- MESSAGE DAO (Messaggi) ---
    public static MessageDAO getMessageDAO() {
        if (messageDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                messageDaoInstance = new MessageDAOFileSystem();
            } else {
                messageDaoInstance = MessageDAOMySQL.getInstance();
            }
        }
        return messageDaoInstance;
    }

    // --- NOTE DAO (Note) ---
    public static NoteDAO getNoteDAO() {
        if (noteDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                noteDaoInstance = new NoteDAOFileSystem();
            } else {
                noteDaoInstance = NoteDAOMySQL.getInstance();
            }
        }
        return noteDaoInstance;
    }

    // --- NOTIFICATION DAO (Notifiche) ---
    public static NotificationDAO getNotificationDAO() {
        if (notificationDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                notificationDaoInstance = new NotificationDAOFileSystem();
            } else {
                notificationDaoInstance = NotificationDAOMySQL.getInstance();
            }
        }
        return notificationDaoInstance;
    }

    // --- REPORT DAO (Report) ---
    public static ReportDAO getReportDAO() {
        if (reportDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                reportDaoInstance = new ReportDAOFileSystem();
            } else {
                reportDaoInstance = ReportDAOMySQL.getInstance();
            }
        }
        return reportDaoInstance;
    }

    // --- USER DAO (Login & Registrazione) ---
    public static UserDAO getUserDAO() {
        if (userDaoInstance == null) {
            if (Config.IS_DEMO_VERSION) {
                userDaoInstance = new UserDAOFileSystem();
            } else {
                userDaoInstance = UserDAOMySQL.getInstance();
            }
        }
        return userDaoInstance;
    }
}